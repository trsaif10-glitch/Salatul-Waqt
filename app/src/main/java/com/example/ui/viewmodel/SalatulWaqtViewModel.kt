package com.example.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.UserPreferencesRepository
import com.example.domain.localization.AppLocalizations
import com.example.domain.localization.AppStrings
import com.example.domain.localization.englishStrings
import com.example.domain.model.AppLanguage
import com.example.domain.model.AsrJuristic
import com.example.domain.model.CalculationMethod
import com.example.domain.model.DailySchedule
import com.example.domain.model.PrayerCategory
import com.example.domain.model.PrayerLocation
import com.example.domain.model.PrayerOffsets
import com.example.domain.model.PrayerType
import com.example.domain.model.PrayerWindow
import com.example.domain.model.ThemeMode
import com.example.domain.model.UiMonthlyDayItem
import com.example.domain.model.UiPrayerItem
import com.example.domain.model.UserPreferences
import com.example.domain.prayer.BanglaDateFormatter
import com.example.domain.prayer.CityCatalog
import com.example.domain.prayer.PrayerTimeCalculator
import com.example.notification.PrayerNotificationHelper
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume

data class TasbihPreset(
  val title: String,
  val arabicText: String,
  val target: Int,
)

val tasbihPresets = listOf(
  TasbihPreset("SubhanAllah", "سُبْحَانَ اللَّهِ", 33),
  TasbihPreset("Alhamdulillah", "الْحَمْدُ لِلَّهِ", 33),
  TasbihPreset("Allahu Akbar", "اللَّهُ أَكْبَرُ", 34),
  TasbihPreset("Astaghfirullah", "أَسْتَغْفِرُ اللَّهَ", 100),
  TasbihPreset("La ilaha illallah", "لَا إِلَٰهَ إِلَّا اللَّهُ", 100),
  TasbihPreset("Durood / Salawat", "صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ", 100),
)

@Immutable
data class ActiveWaqtStatus(
  val currentWindow: PrayerWindow?,
  val nextWindow: PrayerWindow?,
  val startTimeText: String,
  val endTimeText: String,
  val nextStartText: String,
  val targetMillis: Long,
  val progressFraction: Float,
  val isRunningWindow: Boolean,
  val isForbiddenNow: Boolean,
)

@Immutable
data class MonthlyUiState(
  val year: Int = 2026,
  val month: Int = 1,
  val title: String = "",
  val items: List<UiMonthlyDayItem> = emptyList(),
)

@Immutable
data class SalatulWaqtUiState(
  val isPreferencesLoaded: Boolean = false,
  val preferences: UserPreferences = UserPreferences(CityCatalog.defaultLocation),
  val todayYear: Int = 2026,
  val todayMonth: Int = 1,
  val todayDay: Int = 1,
  val todaySchedule: DailySchedule? = null,
  val tomorrowSchedule: DailySchedule? = null,
  val todayPrayerItems: List<UiPrayerItem> = emptyList(),
  val todaySehriText: String = "",
  val todayIftarText: String = "",
  val todayMidnightText: String = "",
  val todayLastThirdText: String = "",
  val gregorianDateText: String = "",
  val hijriDateText: String = "",
  val qiblaBearingDeg: Double = 277.6,
  val distanceToKaabaKm: Double = 0.0,
  val deviceAzimuthDeg: Float = 0f,
  val hasCompassSensor: Boolean = false,
  val isDetectingLocation: Boolean = false,
  val statusMessage: String? = null,
)

class SalatulWaqtViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = UserPreferencesRepository(application)

  private val _nowMillisFlow = MutableStateFlow(System.currentTimeMillis())
  val nowMillisFlow: StateFlow<Long> = _nowMillisFlow.asStateFlow()

  private val quarterMinuteBucketFlow = MutableStateFlow(System.currentTimeMillis() / 15_000L)
  private val currentDayKeyFlow = MutableStateFlow(computeDayKey(System.currentTimeMillis(), TimeZone.getDefault().id))

  private val initialCal = Calendar.getInstance()
  private val monthlyYearMonthFlow = MutableStateFlow(
    initialCal.get(Calendar.YEAR) to (initialCal.get(Calendar.MONTH) + 1),
  )

  private val deviceAzimuthFlow = MutableStateFlow(0f)
  private val hasSensorFlow = MutableStateFlow(false)
  private val isDetectingLocationFlow = MutableStateFlow(false)
  private val statusMessageFlow = MutableStateFlow<String?>(null)
  private var lastAutoDetectTimestampMs = 0L

  private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
  private val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
  private val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
  private val magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
  private val lastAccel = FloatArray(3)
  private var hasAccel = false
  private val lastMag = FloatArray(3)
  private var hasMag = false

  private val sensorListener = object : SensorEventListener {
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    override fun onSensorChanged(event: SensorEvent) {
      val azimuthDeg: Float? = when (event.sensor.type) {
        Sensor.TYPE_ROTATION_VECTOR -> {
          SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
          SensorManager.getOrientation(rotationMatrix, orientation)
          ((Math.toDegrees(orientation[0].toDouble()) + 360.0) % 360.0).toFloat()
        }
        Sensor.TYPE_ACCELEROMETER -> {
          System.arraycopy(event.values, 0, lastAccel, 0, 3)
          hasAccel = true
          computeFromAccelMag()
        }
        Sensor.TYPE_MAGNETIC_FIELD -> {
          System.arraycopy(event.values, 0, lastMag, 0, 3)
          hasMag = true
          computeFromAccelMag()
        }
        else -> null
      }
      if (azimuthDeg != null) {
        val prev = deviceAzimuthFlow.value
        val diff = ((azimuthDeg - prev + 540f) % 360f) - 180f
        deviceAzimuthFlow.value = (prev + diff * 0.2f + 360f) % 360f
      }
    }

    private fun computeFromAccelMag(): Float? {
      if (!hasAccel || !hasMag) return null
      val ok = SensorManager.getRotationMatrix(rotationMatrix, null, lastAccel, lastMag)
      if (!ok) return null
      SensorManager.getOrientation(rotationMatrix, orientation)
      return ((Math.toDegrees(orientation[0].toDouble()) + 360.0) % 360.0).toFloat()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
  }

  private val dailyComputationFlow = combine(
    repository.preferencesFlow,
    currentDayKeyFlow,
  ) { prefs, _ ->
    val now = System.currentTimeMillis()
    val tzId = prefs.location.timeZoneId
    val use24h = prefs.use24HourFormat
    val strings = AppLocalizations.forLanguage(prefs.language)
    val tz = TimeZone.getTimeZone(tzId)
    val cal = Calendar.getInstance(tz).apply { timeInMillis = now }
    val tYear = cal.get(Calendar.YEAR)
    val tMonth = cal.get(Calendar.MONTH) + 1
    val tDay = cal.get(Calendar.DAY_OF_MONTH)

    val todaySchedule = PrayerTimeCalculator.calculateSchedule(
      year = tYear,
      month = tMonth,
      day = tDay,
      location = prefs.location,
      method = prefs.calculationMethod,
      asrJuristic = prefs.asrJuristic,
      offsets = prefs.offsets,
      strings = strings,
    )

    val tomorrowCal = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }
    val tomorrowSchedule = PrayerTimeCalculator.calculateSchedule(
      year = tomorrowCal.get(Calendar.YEAR),
      month = tomorrowCal.get(Calendar.MONTH) + 1,
      day = tomorrowCal.get(Calendar.DAY_OF_MONTH),
      location = prefs.location,
      method = prefs.calculationMethod,
      asrJuristic = prefs.asrJuristic,
      offsets = prefs.offsets,
      strings = strings,
    )

    val uiItems = todaySchedule.windows.map { w ->
      UiPrayerItem(
        window = w,
        startTimeText = BanglaDateFormatter.formatClockCompact(w.startMillis, use24h, tzId),
        endTimeText = BanglaDateFormatter.formatClockCompact(w.endMillis, use24h, tzId),
        startPeriodText = BanglaDateFormatter.formatClockWithPeriod(w.startMillis, use24h, tzId, strings),
        endPeriodText = BanglaDateFormatter.formatClockWithPeriod(w.endMillis, use24h, tzId, strings),
      )
    }

    DailyComputation(
      prefs = prefs,
      year = tYear,
      month = tMonth,
      day = tDay,
      todaySchedule = todaySchedule,
      tomorrowSchedule = tomorrowSchedule,
      prayerItems = uiItems,
      sehriText = BanglaDateFormatter.formatClockWithPeriod(todaySchedule.sehriEndMillis, use24h, tzId, strings),
      iftarText = BanglaDateFormatter.formatClockWithPeriod(todaySchedule.iftarMillis, use24h, tzId, strings),
      midnightText = BanglaDateFormatter.formatClockWithPeriod(todaySchedule.midnightMillis, use24h, tzId, strings),
      lastThirdText = BanglaDateFormatter.formatClockWithPeriod(todaySchedule.lastThirdNightMillis, use24h, tzId, strings),
      gregorianDateText = BanglaDateFormatter.formatGregorianDate(tYear, tMonth, tDay, tzId, strings),
      hijriDateText = BanglaDateFormatter.formatHijriDate(now, prefs.hijriOffsetDays, tzId, strings),
      qiblaBearing = PrayerTimeCalculator.calculateQiblaBearing(prefs.location.latitude, prefs.location.longitude),
      distanceKm = PrayerTimeCalculator.calculateDistanceToKaabaKm(prefs.location.latitude, prefs.location.longitude),
    )
  }.flowOn(Dispatchers.Default)

  val activeWaqtFlow: StateFlow<ActiveWaqtStatus?> = combine(
    dailyComputationFlow,
    quarterMinuteBucketFlow,
  ) { daily, _ ->
    val now = System.currentTimeMillis()
    val strings = AppLocalizations.forLanguage(daily.prefs.language)
    computeActiveWaqt(
      today = daily.todaySchedule,
      tomorrow = daily.tomorrowSchedule,
      nowMillis = now,
      use24HourFormat = daily.prefs.use24HourFormat,
      timeZoneId = daily.prefs.location.timeZoneId,
      strings = strings,
    )
  }.flowOn(Dispatchers.Default).stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = null,
  )

  val monthlyStateFlow: StateFlow<MonthlyUiState> = combine(
    repository.preferencesFlow,
    monthlyYearMonthFlow,
    currentDayKeyFlow,
  ) { prefs, (mYear, mMonth), dayKey ->
    val todayY = dayKey / 10000
    val todayM = (dayKey % 10000) / 100
    val todayD = dayKey % 100
    val use24h = prefs.use24HourFormat
    val tzId = prefs.location.timeZoneId
    val strings = AppLocalizations.forLanguage(prefs.language)

    val monthSchedules = PrayerTimeCalculator.calculateMonthSchedules(
      year = mYear,
      month = mMonth,
      location = prefs.location,
      method = prefs.calculationMethod,
      asrJuristic = prefs.asrJuristic,
      offsets = prefs.offsets,
      strings = strings,
    )

    val items = monthSchedules.map { sched ->
      UiMonthlyDayItem(
        schedule = sched,
        dateShortText = BanglaDateFormatter.formatShortDate(sched.day, mMonth, strings),
        hijriShortText = BanglaDateFormatter.formatHijriShort(
          sched.dhuhrStartMillis,
          prefs.hijriOffsetDays,
          tzId,
          strings,
        ),
        fajrText = BanglaDateFormatter.formatClockCompact(sched.fajrStartMillis, use24h, tzId),
        dhuhrText = BanglaDateFormatter.formatClockCompact(sched.dhuhrStartMillis, use24h, tzId),
        asrText = BanglaDateFormatter.formatClockCompact(sched.asrStartMillis, use24h, tzId),
        maghribText = BanglaDateFormatter.formatClockCompact(sched.maghribStartMillis, use24h, tzId),
        ishaText = BanglaDateFormatter.formatClockCompact(sched.ishaStartMillis, use24h, tzId),
        sehriText = BanglaDateFormatter.formatClockWithPeriod(sched.sehriEndMillis, use24h, tzId, strings),
        iftarText = BanglaDateFormatter.formatClockWithPeriod(sched.iftarMillis, use24h, tzId, strings),
        isToday = (mYear == todayY && mMonth == todayM && sched.day == todayD),
      )
    }

    MonthlyUiState(
      year = mYear,
      month = mMonth,
      title = BanglaDateFormatter.formatMonthYear(mYear, mMonth, strings),
      items = items,
    )
  }.flowOn(Dispatchers.Default).stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = MonthlyUiState(),
  )

  private val sensorAndStatusFlow = combine(
    deviceAzimuthFlow,
    hasSensorFlow,
    isDetectingLocationFlow,
    statusMessageFlow,
  ) { az, hasSensor, detecting, msg ->
    SensorAndStatus(az, hasSensor, detecting, msg)
  }

  val uiState: StateFlow<SalatulWaqtUiState> = combine(
    dailyComputationFlow,
    sensorAndStatusFlow,
  ) { daily, extra ->
    SalatulWaqtUiState(
      isPreferencesLoaded = true,
      preferences = daily.prefs,
      todayYear = daily.year,
      todayMonth = daily.month,
      todayDay = daily.day,
      todaySchedule = daily.todaySchedule,
      tomorrowSchedule = daily.tomorrowSchedule,
      todayPrayerItems = daily.prayerItems,
      todaySehriText = daily.sehriText,
      todayIftarText = daily.iftarText,
      todayMidnightText = daily.midnightText,
      todayLastThirdText = daily.lastThirdText,
      gregorianDateText = daily.gregorianDateText,
      hijriDateText = daily.hijriDateText,
      qiblaBearingDeg = daily.qiblaBearing,
      distanceToKaabaKm = daily.distanceKm,
      deviceAzimuthDeg = extra.azimuth,
      hasCompassSensor = extra.hasSensor,
      isDetectingLocation = extra.isDetectingLocation,
      statusMessage = extra.statusMessage,
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = SalatulWaqtUiState(),
  )

  init {
    viewModelScope.launch(Dispatchers.Default) {
      var tickCount = 0
      while (true) {
        val now = System.currentTimeMillis()
        _nowMillisFlow.value = now
        val bucket = now / 15_000L
        if (bucket != quarterMinuteBucketFlow.value) {
          quarterMinuteBucketFlow.value = bucket
        }
        if (tickCount % 60 == 0) {
          val tzId = uiState.value.preferences.location.timeZoneId
          val dayKey = computeDayKey(now, tzId)
          if (dayKey != currentDayKeyFlow.value) {
            currentDayKeyFlow.value = dayKey
          }
        }
        tickCount++
        delay(1_000L)
      }
    }

    viewModelScope.launch(Dispatchers.Default) {
      repository.preferencesFlow
        .distinctUntilChanged()
        .map { prefs ->
          val tz = TimeZone.getTimeZone(prefs.location.timeZoneId)
          val cal = Calendar.getInstance(tz)
          val strings = AppLocalizations.forLanguage(prefs.language)
          val today = PrayerTimeCalculator.calculateSchedule(
            year = cal.get(Calendar.YEAR),
            month = cal.get(Calendar.MONTH) + 1,
            day = cal.get(Calendar.DAY_OF_MONTH),
            location = prefs.location,
            method = prefs.calculationMethod,
            asrJuristic = prefs.asrJuristic,
            offsets = prefs.offsets,
            strings = strings,
          )
          Triple(prefs, today, strings)
        }
        .collect { (prefs, today, strings) ->
          PrayerNotificationHelper.scheduleTodayReminders(
            context = getApplication(),
            schedule = today,
            enabledTypes = prefs.enabledReminders,
            locationName = prefs.location.name,
            use24HourFormat = prefs.use24HourFormat,
            timeZoneId = prefs.location.timeZoneId,
            strings = strings,
          )
        }
    }
  }

  fun startCompass() {
    if (rotationSensor != null) {
      hasSensorFlow.value = true
      sensorManager.registerListener(sensorListener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
    } else if (accelSensor != null && magSensor != null) {
      hasSensorFlow.value = true
      sensorManager.registerListener(sensorListener, accelSensor, SensorManager.SENSOR_DELAY_UI)
      sensorManager.registerListener(sensorListener, magSensor, SensorManager.SENSOR_DELAY_UI)
    } else {
      hasSensorFlow.value = false
    }
  }

  fun stopCompass() {
    sensorManager.unregisterListener(sensorListener)
  }

  fun shiftMonth(delta: Int) {
    val (y, m) = monthlyYearMonthFlow.value
    val cal = Calendar.getInstance().apply {
      set(Calendar.YEAR, y)
      set(Calendar.MONTH, m - 1)
      set(Calendar.DAY_OF_MONTH, 1)
      add(Calendar.MONTH, delta)
    }
    monthlyYearMonthFlow.value = cal.get(Calendar.YEAR) to (cal.get(Calendar.MONTH) + 1)
  }

  fun resetMonthToToday() {
    val cal = Calendar.getInstance()
    monthlyYearMonthFlow.value = cal.get(Calendar.YEAR) to (cal.get(Calendar.MONTH) + 1)
  }

  fun selectLocation(location: PrayerLocation) {
    viewModelScope.launch {
      val strings = AppLocalizations.forLanguage(uiState.value.preferences.language)
      repository.updateLocation(location, autoDetectLocation = false)
      currentDayKeyFlow.value = computeDayKey(System.currentTimeMillis(), location.timeZoneId)
      statusMessageFlow.value = "${strings.locationSetPrefix} ${location.name}"
    }
  }

  fun updateAutoDetectLocation(enabled: Boolean) {
    viewModelScope.launch {
      repository.updateAutoDetectLocation(enabled)
      if (enabled) {
        lastAutoDetectTimestampMs = System.currentTimeMillis()
      }
    }
  }

  fun updateLanguage(language: AppLanguage) {
    viewModelScope.launch { repository.updateLanguage(language) }
  }

  fun updateCalculationMethod(method: CalculationMethod) {
    viewModelScope.launch { repository.updateCalculationMethod(method) }
  }

  fun updateAsrJuristic(juristic: AsrJuristic) {
    viewModelScope.launch { repository.updateAsrJuristic(juristic) }
  }

  fun updateHijriOffset(days: Int) {
    viewModelScope.launch { repository.updateHijriOffset(days) }
  }

  fun update24HourFormat(enabled: Boolean) {
    viewModelScope.launch { repository.update24HourFormat(enabled) }
  }

  fun updateThemeMode(mode: ThemeMode) {
    viewModelScope.launch { repository.updateThemeMode(mode) }
  }

  fun updateOffsets(offsets: PrayerOffsets) {
    viewModelScope.launch { repository.updateOffsets(offsets) }
  }

  fun toggleReminder(type: PrayerType) {
    viewModelScope.launch { repository.toggleReminder(type) }
  }

  fun incrementTasbih() {
    val prefs = uiState.value.preferences
    val preset = tasbihPresets[prefs.tasbihPresetIndex.coerceIn(tasbihPresets.indices)]
    val next = if (prefs.tasbihCount + 1 > preset.target) 1 else prefs.tasbihCount + 1
    viewModelScope.launch { repository.updateTasbih(next, prefs.tasbihPresetIndex) }
  }

  fun resetTasbih() {
    val prefs = uiState.value.preferences
    viewModelScope.launch { repository.updateTasbih(0, prefs.tasbihPresetIndex) }
  }

  fun selectTasbihPreset(index: Int) {
    viewModelScope.launch { repository.updateTasbih(0, index.coerceIn(tasbihPresets.indices)) }
  }

  fun clearStatusMessage() {
    statusMessageFlow.value = null
  }

  fun showStatusMessage(msg: String) {
    statusMessageFlow.value = msg
  }

  fun maybeAutoRefreshOnResume(hasPermission: Boolean) {
    if (!hasPermission || !uiState.value.preferences.autoDetectLocation || isDetectingLocationFlow.value) {
      return
    }
    val now = System.currentTimeMillis()
    if (now - lastAutoDetectTimestampMs < 180_000L) {
      return
    }
    lastAutoDetectTimestampMs = now
    detectLocationFromGps(silent = true)
  }

  @SuppressLint("MissingPermission")
  fun detectLocationFromGps(
    enableAutoMode: Boolean = false,
    silent: Boolean = false,
  ) {
    val context = getApplication<Application>()
    lastAutoDetectTimestampMs = System.currentTimeMillis()
    isDetectingLocationFlow.value = true
    if (enableAutoMode) {
      viewModelScope.launch { repository.updateAutoDetectLocation(true) }
    }
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    val cts = CancellationTokenSource()
    fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
      .addOnSuccessListener { loc ->
        if (loc != null) {
          resolveCoordinatesAndSave(context, loc.latitude, loc.longitude, silent)
        } else {
          fusedClient.lastLocation
            .addOnSuccessListener { lastLoc ->
              if (lastLoc != null) {
                resolveCoordinatesAndSave(context, lastLoc.latitude, lastLoc.longitude, silent)
              } else {
                fallbackLastKnownLocation(context, silent)
              }
            }
            .addOnFailureListener {
              fallbackLastKnownLocation(context, silent)
            }
        }
      }
      .addOnFailureListener {
        fusedClient.lastLocation
          .addOnSuccessListener { lastLoc ->
            if (lastLoc != null) {
              resolveCoordinatesAndSave(context, lastLoc.latitude, lastLoc.longitude, silent)
            } else {
              fallbackLastKnownLocation(context, silent)
            }
          }
          .addOnFailureListener {
            fallbackLastKnownLocation(context, silent)
          }
      }
  }

  @SuppressLint("MissingPermission")
  private fun fallbackLastKnownLocation(context: Context, silent: Boolean) {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val netLoc = try {
      lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    } catch (_: SecurityException) {
      null
    }
    val passiveLoc = try {
      lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
    } catch (_: SecurityException) {
      null
    }
    val best = listOfNotNull(netLoc, passiveLoc).maxByOrNull { it.time }
    if (best != null) {
      resolveCoordinatesAndSave(context, best.latitude, best.longitude, silent)
    } else {
      isDetectingLocationFlow.value = false
      if (!silent) {
        val strings = AppLocalizations.forLanguage(uiState.value.preferences.language)
        statusMessageFlow.value = strings.gpsUnavailableMsg
      }
    }
  }

  private fun resolveCoordinatesAndSave(
    context: Context,
    lat: Double,
    lon: Double,
    silent: Boolean,
  ) {
    viewModelScope.launch {
      val prevName = uiState.value.preferences.location.name
      val catalogMatch = CityCatalog.findNearestCity(lat, lon)
      val geocoded = reverseGeocodeCity(context, lat, lon)
      val finalLocation = if (geocoded != null) {
        catalogMatch.copy(
          name = geocoded.first,
          region = geocoded.second ?: catalogMatch.region,
          latitude = lat,
          longitude = lon,
          timeZoneId = TimeZone.getDefault().id,
        )
      } else {
        catalogMatch.copy(
          latitude = lat,
          longitude = lon,
          timeZoneId = TimeZone.getDefault().id,
        )
      }
      val strings = AppLocalizations.forLanguage(uiState.value.preferences.language)
      repository.updateLocation(finalLocation)
      currentDayKeyFlow.value = computeDayKey(System.currentTimeMillis(), finalLocation.timeZoneId)
      if (!silent || finalLocation.name != prevName) {
        statusMessageFlow.value = "${strings.locationUpdatedPrefix} ${finalLocation.name}"
      }
      isDetectingLocationFlow.value = false
    }
  }

  private suspend fun reverseGeocodeCity(
    context: Context,
    lat: Double,
    lon: Double,
  ): Pair<String, String?>? = withContext(Dispatchers.IO) {
    try {
      if (!Geocoder.isPresent()) return@withContext null
      val geocoder = Geocoder(context, Locale.ENGLISH)
      val addresses: List<Address> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        suspendCancellableCoroutine { cont ->
          geocoder.getFromLocation(lat, lon, 1) { list ->
            if (cont.isActive) cont.resume(list)
          }
        }
      } else {
        @Suppress("DEPRECATION")
        geocoder.getFromLocation(lat, lon, 1) ?: emptyList()
      }
      val addr = addresses.firstOrNull() ?: return@withContext null
      val locality = listOfNotNull(
        addr.locality ?: addr.subAdminArea,
        addr.subLocality,
      ).distinct().take(1).joinToString("").ifBlank { null } ?: return@withContext null
      val region = listOfNotNull(addr.adminArea, addr.countryName)
        .distinct().joinToString(", ").ifBlank { null }
      locality to region
    } catch (_: Throwable) {
      null
    }
  }

  companion object {
    fun computeDayKey(millis: Long, tzId: String): Int {
      val cal = Calendar.getInstance(TimeZone.getTimeZone(tzId)).apply {
        timeInMillis = millis
      }
      return cal.get(Calendar.YEAR) * 10000 +
        (cal.get(Calendar.MONTH) + 1) * 100 +
        cal.get(Calendar.DAY_OF_MONTH)
    }

    fun computeActiveWaqt(
      today: DailySchedule,
      tomorrow: DailySchedule?,
      nowMillis: Long,
      use24HourFormat: Boolean,
      timeZoneId: String,
      strings: AppStrings = englishStrings,
    ): ActiveWaqtStatus {
      val forbiddenNow = today.windows.firstOrNull {
        it.category == PrayerCategory.FORBIDDEN && nowMillis in it.startMillis..it.endMillis
      }
      val fardRunning = today.windows.firstOrNull {
        it.category == PrayerCategory.FARD && nowMillis in it.startMillis..it.endMillis
      }
      val anyRunning = forbiddenNow ?: fardRunning ?: today.windows.lastOrNull {
        it.type != PrayerType.WITR && nowMillis in it.startMillis..it.endMillis
      }

      val nextFardOrPrimary = today.windows.firstOrNull {
        it.category == PrayerCategory.FARD && it.startMillis > nowMillis
      } ?: tomorrow?.windows?.firstOrNull { it.type == PrayerType.FAJR }

      val displayed = anyRunning ?: nextFardOrPrimary
      val targetMillis = when {
        anyRunning != null -> anyRunning.endMillis
        nextFardOrPrimary != null -> nextFardOrPrimary.startMillis
        tomorrow != null -> tomorrow.fajrStartMillis
        else -> today.fajrStartMillis
      }

      val progress = if (anyRunning != null && anyRunning.endMillis > anyRunning.startMillis) {
        val total = (anyRunning.endMillis - anyRunning.startMillis).toFloat()
        val elapsed = (nowMillis - anyRunning.startMillis).toFloat()
        (elapsed / total).coerceIn(0f, 1f)
      } else {
        0f
      }

      val startText = displayed?.let {
        BanglaDateFormatter.formatClockWithPeriod(it.startMillis, use24HourFormat, timeZoneId, strings)
      } ?: "—"
      val endText = displayed?.let {
        BanglaDateFormatter.formatClockWithPeriod(it.endMillis, use24HourFormat, timeZoneId, strings)
      } ?: "—"
      val nextStartText = nextFardOrPrimary?.let {
        BanglaDateFormatter.formatClockWithPeriod(it.startMillis, use24HourFormat, timeZoneId, strings)
      } ?: ""

      return ActiveWaqtStatus(
        currentWindow = anyRunning,
        nextWindow = nextFardOrPrimary,
        startTimeText = startText,
        endTimeText = endText,
        nextStartText = nextStartText,
        targetMillis = targetMillis,
        progressFraction = progress,
        isRunningWindow = anyRunning != null,
        isForbiddenNow = forbiddenNow != null,
      )
    }
  }
}

private data class DailyComputation(
  val prefs: UserPreferences,
  val year: Int,
  val month: Int,
  val day: Int,
  val todaySchedule: DailySchedule,
  val tomorrowSchedule: DailySchedule,
  val prayerItems: List<UiPrayerItem>,
  val sehriText: String,
  val iftarText: String,
  val midnightText: String,
  val lastThirdText: String,
  val gregorianDateText: String,
  val hijriDateText: String,
  val qiblaBearing: Double,
  val distanceKm: Double,
)

private data class SensorAndStatus(
  val azimuth: Float,
  val hasSensor: Boolean,
  val isDetectingLocation: Boolean,
  val statusMessage: String?,
)
