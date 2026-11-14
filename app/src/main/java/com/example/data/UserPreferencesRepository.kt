package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.AppLanguage
import com.example.domain.model.AsrJuristic
import com.example.domain.model.CalculationMethod
import com.example.domain.model.PrayerLocation
import com.example.domain.model.PrayerOffsets
import com.example.domain.model.PrayerType
import com.example.domain.model.ThemeMode
import com.example.domain.model.UserPreferences
import com.example.domain.prayer.CityCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.TimeZone

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "salatul_waqt_prefs")

class UserPreferencesRepository(private val context: Context) {

  private object Keys {
    val LOC_NAME = stringPreferencesKey("loc_name")
    val LOC_REGION = stringPreferencesKey("loc_region")
    val LOC_LAT = doublePreferencesKey("loc_lat")
    val LOC_LON = doublePreferencesKey("loc_lon")
    val LOC_TZ = stringPreferencesKey("loc_tz")
    val AUTO_DETECT_LOCATION = booleanPreferencesKey("auto_detect_location")
    val APP_LANGUAGE = stringPreferencesKey("app_language")
    val CALC_METHOD = stringPreferencesKey("calc_method")
    val ASR_JURISTIC = stringPreferencesKey("asr_juristic")
    val HIJRI_OFFSET = intPreferencesKey("hijri_offset")
    val USE_24H_FORMAT = booleanPreferencesKey("use_24h_format")
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val OFFSET_FAJR = intPreferencesKey("offset_fajr")
    val OFFSET_SUNRISE = intPreferencesKey("offset_sunrise")
    val OFFSET_DHUHR = intPreferencesKey("offset_dhuhr")
    val OFFSET_ASR = intPreferencesKey("offset_asr")
    val OFFSET_MAGHRIB = intPreferencesKey("offset_maghrib")
    val OFFSET_ISHA = intPreferencesKey("offset_isha")
    val ENABLED_REMINDERS = stringSetPreferencesKey("enabled_reminders")
    val TASBIH_COUNT = intPreferencesKey("tasbih_count")
    val TASBIH_PRESET = intPreferencesKey("tasbih_preset")
  }

  private val defaultReminderIds = setOf(
    PrayerType.FAJR.id,
    PrayerType.DHUHR.id,
    PrayerType.ASR.id,
    PrayerType.MAGHRIB.id,
    PrayerType.ISHA.id,
  )

  val preferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
    val defLoc = CityCatalog.defaultForTimeZone(TimeZone.getDefault().id)
    val savedName = prefs[Keys.LOC_NAME]
    val location = if (savedName != null) {
      val englishMatch = CityCatalog.allLocations.firstOrNull {
        it.latitude == (prefs[Keys.LOC_LAT] ?: 0.0) && it.longitude == (prefs[Keys.LOC_LON] ?: 0.0)
      }
      val hasNonAscii = savedName.any { it.code > 127 }
      if (hasNonAscii && englishMatch != null) {
        englishMatch
      } else {
        PrayerLocation(
          name = if (hasNonAscii) defLoc.name else savedName,
          region = prefs[Keys.LOC_REGION]?.let { reg -> if (reg.any { it.code > 127 }) defLoc.region else reg } ?: defLoc.region,
          latitude = prefs[Keys.LOC_LAT] ?: defLoc.latitude,
          longitude = prefs[Keys.LOC_LON] ?: defLoc.longitude,
          timeZoneId = prefs[Keys.LOC_TZ] ?: defLoc.timeZoneId,
        )
      }
    } else {
      defLoc
    }

    val method = prefs[Keys.CALC_METHOD]?.let { name ->
      CalculationMethod.entries.firstOrNull { it.name == name }
    } ?: CalculationMethod.MWL

    val juristic = prefs[Keys.ASR_JURISTIC]?.let { name ->
      AsrJuristic.entries.firstOrNull { it.name == name }
    } ?: AsrJuristic.STANDARD

    val themeMode = prefs[Keys.THEME_MODE]?.let { name ->
      ThemeMode.entries.firstOrNull { it.name == name }
    } ?: ThemeMode.LIGHT

    val reminderIds = prefs[Keys.ENABLED_REMINDERS] ?: defaultReminderIds
    val reminders = reminderIds.mapNotNull { id ->
      PrayerType.entries.firstOrNull { it.id == id }
    }.toSet()

    val language = prefs[Keys.APP_LANGUAGE]?.let { code ->
      AppLanguage.entries.firstOrNull { it.code == code || it.name == code }
    } ?: AppLanguage.ENGLISH

    UserPreferences(
      location = location,
      autoDetectLocation = prefs[Keys.AUTO_DETECT_LOCATION] ?: true,
      language = language,
      calculationMethod = method,
      asrJuristic = juristic,
      hijriOffsetDays = (prefs[Keys.HIJRI_OFFSET] ?: 0).coerceIn(-2, 2),
      use24HourFormat = prefs[Keys.USE_24H_FORMAT] ?: false,
      themeMode = themeMode,
      offsets = PrayerOffsets(
        fajrMin = prefs[Keys.OFFSET_FAJR] ?: 0,
        sunriseMin = prefs[Keys.OFFSET_SUNRISE] ?: 0,
        dhuhrMin = prefs[Keys.OFFSET_DHUHR] ?: 0,
        asrMin = prefs[Keys.OFFSET_ASR] ?: 0,
        maghribMin = prefs[Keys.OFFSET_MAGHRIB] ?: 0,
        ishaMin = prefs[Keys.OFFSET_ISHA] ?: 0,
      ),
      enabledReminders = reminders,
      tasbihCount = prefs[Keys.TASBIH_COUNT] ?: 0,
      tasbihPresetIndex = prefs[Keys.TASBIH_PRESET] ?: 0,
    )
  }

  suspend fun updateLocation(location: PrayerLocation, autoDetectLocation: Boolean? = null) {
    context.dataStore.edit { prefs ->
      prefs[Keys.LOC_NAME] = location.name
      prefs[Keys.LOC_REGION] = location.region
      prefs[Keys.LOC_LAT] = location.latitude
      prefs[Keys.LOC_LON] = location.longitude
      prefs[Keys.LOC_TZ] = location.timeZoneId
      if (autoDetectLocation != null) {
        prefs[Keys.AUTO_DETECT_LOCATION] = autoDetectLocation
      }
    }
  }

  suspend fun updateAutoDetectLocation(enabled: Boolean) {
    context.dataStore.edit { it[Keys.AUTO_DETECT_LOCATION] = enabled }
  }

  suspend fun updateLanguage(language: AppLanguage) {
    context.dataStore.edit { it[Keys.APP_LANGUAGE] = language.code }
  }

  suspend fun updateCalculationMethod(method: CalculationMethod) {
    context.dataStore.edit { it[Keys.CALC_METHOD] = method.name }
  }

  suspend fun updateAsrJuristic(juristic: AsrJuristic) {
    context.dataStore.edit { it[Keys.ASR_JURISTIC] = juristic.name }
  }

  suspend fun updateHijriOffset(days: Int) {
    context.dataStore.edit { it[Keys.HIJRI_OFFSET] = days.coerceIn(-2, 2) }
  }

  suspend fun update24HourFormat(enabled: Boolean) {
    context.dataStore.edit { it[Keys.USE_24H_FORMAT] = enabled }
  }

  suspend fun updateThemeMode(mode: ThemeMode) {
    context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
  }

  suspend fun updateOffsets(offsets: PrayerOffsets) {
    context.dataStore.edit { prefs ->
      prefs[Keys.OFFSET_FAJR] = offsets.fajrMin.coerceIn(-20, 20)
      prefs[Keys.OFFSET_SUNRISE] = offsets.sunriseMin.coerceIn(-20, 20)
      prefs[Keys.OFFSET_DHUHR] = offsets.dhuhrMin.coerceIn(-20, 20)
      prefs[Keys.OFFSET_ASR] = offsets.asrMin.coerceIn(-20, 20)
      prefs[Keys.OFFSET_MAGHRIB] = offsets.maghribMin.coerceIn(-20, 20)
      prefs[Keys.OFFSET_ISHA] = offsets.ishaMin.coerceIn(-20, 20)
    }
  }

  suspend fun toggleReminder(type: PrayerType) {
    context.dataStore.edit { prefs ->
      val current = (prefs[Keys.ENABLED_REMINDERS] ?: defaultReminderIds).toMutableSet()
      if (current.contains(type.id)) {
        current.remove(type.id)
      } else {
        current.add(type.id)
      }
      prefs[Keys.ENABLED_REMINDERS] = current
    }
  }

  suspend fun updateTasbih(count: Int, presetIndex: Int) {
    context.dataStore.edit { prefs ->
      prefs[Keys.TASBIH_COUNT] = count
      prefs[Keys.TASBIH_PRESET] = presetIndex
    }
  }
}
