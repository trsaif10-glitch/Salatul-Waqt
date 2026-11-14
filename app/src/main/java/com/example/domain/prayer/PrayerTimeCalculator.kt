package com.example.domain.prayer

import com.example.domain.localization.AppStrings
import com.example.domain.localization.englishStrings
import com.example.domain.model.AsrJuristic
import com.example.domain.model.CalculationMethod
import com.example.domain.model.DailySchedule
import com.example.domain.model.PrayerCategory
import com.example.domain.model.PrayerLocation
import com.example.domain.model.PrayerOffsets
import com.example.domain.model.PrayerType
import com.example.domain.model.PrayerWindow
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

object PrayerTimeCalculator {

  private const val KAABA_LAT = 21.422487
  private const val KAABA_LON = 39.826206

  private data class RawSolarTimes(
    val fajrHours: Double,
    val sunriseHours: Double,
    val transitHours: Double,
    val asrHours: Double,
    val sunsetHours: Double,
    val ishaHours: Double,
  )

  fun calculateMonthSchedules(
    year: Int,
    month: Int,
    location: PrayerLocation,
    method: CalculationMethod,
    asrJuristic: AsrJuristic,
    offsets: PrayerOffsets = PrayerOffsets(),
    strings: AppStrings = englishStrings,
  ): List<DailySchedule> {
    val tz = TimeZone.getTimeZone(location.timeZoneId)
    val firstCal = Calendar.getInstance(tz).apply {
      set(Calendar.YEAR, year)
      set(Calendar.MONTH, month - 1)
      set(Calendar.DAY_OF_MONTH, 1)
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    val maxDays = firstCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val tzOffsetHours = tz.getOffset(firstCal.timeInMillis + 12 * 3_600_000L) / 3_600_000.0

    val prevCal = (firstCal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, -1) }
    var prevRaw = computeRawSolarTimes(
      prevCal.get(Calendar.YEAR), prevCal.get(Calendar.MONTH) + 1, prevCal.get(Calendar.DAY_OF_MONTH),
      location.latitude, location.longitude, tzOffsetHours, method, asrJuristic,
    )
    var prevStartMillis = prevCal.timeInMillis

    var currRaw = computeRawSolarTimes(
      year, month, 1,
      location.latitude, location.longitude, tzOffsetHours, method, asrJuristic,
    )
    var currStartMillis = firstCal.timeInMillis
    var currCal = firstCal

    val result = ArrayList<DailySchedule>(maxDays)
    for (day in 1..maxDays) {
      val nextCal = (currCal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }
      val nextRaw = computeRawSolarTimes(
        nextCal.get(Calendar.YEAR), nextCal.get(Calendar.MONTH) + 1, nextCal.get(Calendar.DAY_OF_MONTH),
        location.latitude, location.longitude, tzOffsetHours, method, asrJuristic,
      )
      val nextStartMillis = nextCal.timeInMillis
      val isFriday = currCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY

      result.add(
        assembleSchedule(
          year = year,
          month = month,
          day = day,
          isFriday = isFriday,
          timeZoneId = location.timeZoneId,
          dayStartMillis = currStartMillis,
          prevDayStartMillis = prevStartMillis,
          nextDayStartMillis = nextStartMillis,
          todayRaw = currRaw,
          prevRaw = prevRaw,
          nextRaw = nextRaw,
          asrJuristic = asrJuristic,
          offsets = offsets,
          strings = strings,
        ),
      )

      prevRaw = currRaw
      prevStartMillis = currStartMillis
      currRaw = nextRaw
      currStartMillis = nextStartMillis
      currCal = nextCal
    }
    return result
  }

  fun calculateSchedule(
    year: Int,
    month: Int, // 1..12
    day: Int,
    location: PrayerLocation,
    method: CalculationMethod,
    asrJuristic: AsrJuristic,
    offsets: PrayerOffsets = PrayerOffsets(),
    strings: AppStrings = englishStrings,
  ): DailySchedule {
    val tz = TimeZone.getTimeZone(location.timeZoneId)
    val dayStartCal = Calendar.getInstance(tz).apply {
      set(Calendar.YEAR, year)
      set(Calendar.MONTH, month - 1)
      set(Calendar.DAY_OF_MONTH, day)
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    val dayStartMillis = dayStartCal.timeInMillis
    val tzOffsetHours = tz.getOffset(dayStartMillis + 12 * 3_600_000L) / 3_600_000.0
    val isFriday = dayStartCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY

    val todayRaw = computeRawSolarTimes(
      year = year,
      month = month,
      day = day,
      lat = location.latitude,
      lon = location.longitude,
      tzHours = tzOffsetHours,
      method = method,
      asrJuristic = asrJuristic,
    )

    val prevCal = (dayStartCal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, -1) }
    val prevRaw = computeRawSolarTimes(
      year = prevCal.get(Calendar.YEAR),
      month = prevCal.get(Calendar.MONTH) + 1,
      day = prevCal.get(Calendar.DAY_OF_MONTH),
      lat = location.latitude,
      lon = location.longitude,
      tzHours = tzOffsetHours,
      method = method,
      asrJuristic = asrJuristic,
    )

    val nextCal = (dayStartCal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }
    val nextRaw = computeRawSolarTimes(
      year = nextCal.get(Calendar.YEAR),
      month = nextCal.get(Calendar.MONTH) + 1,
      day = nextCal.get(Calendar.DAY_OF_MONTH),
      lat = location.latitude,
      lon = location.longitude,
      tzHours = tzOffsetHours,
      method = method,
      asrJuristic = asrJuristic,
    )

    return assembleSchedule(
      year = year,
      month = month,
      day = day,
      isFriday = isFriday,
      timeZoneId = location.timeZoneId,
      dayStartMillis = dayStartMillis,
      prevDayStartMillis = prevCal.timeInMillis,
      nextDayStartMillis = nextCal.timeInMillis,
      todayRaw = todayRaw,
      prevRaw = prevRaw,
      nextRaw = nextRaw,
      asrJuristic = asrJuristic,
      offsets = offsets,
      strings = strings,
    )
  }

  private fun assembleSchedule(
    year: Int,
    month: Int,
    day: Int,
    isFriday: Boolean,
    timeZoneId: String,
    dayStartMillis: Long,
    prevDayStartMillis: Long,
    nextDayStartMillis: Long,
    todayRaw: RawSolarTimes,
    prevRaw: RawSolarTimes,
    nextRaw: RawSolarTimes,
    asrJuristic: AsrJuristic,
    offsets: PrayerOffsets,
    strings: AppStrings,
  ): DailySchedule {

    fun toEpoch(baseDayStart: Long, hours: Double, offsetMin: Int): Long {
      val totalMin = (hours * 60.0).roundToLong() + offsetMin
      return baseDayStart + totalMin * 60_000L
    }

    val fajrMillis = toEpoch(dayStartMillis, todayRaw.fajrHours, offsets.fajrMin)
    val sunriseMillis = toEpoch(dayStartMillis, todayRaw.sunriseHours, offsets.sunriseMin)
    val transitMillis = toEpoch(dayStartMillis, todayRaw.transitHours, 0)
    val dhuhrStartMillis = transitMillis + 2 * 60_000L + offsets.dhuhrMin * 60_000L
    val asrStartMillis = toEpoch(dayStartMillis, todayRaw.asrHours, offsets.asrMin)
    val maghribMillis = toEpoch(dayStartMillis, todayRaw.sunsetHours, offsets.maghribMin)
    val ishaStartMillis = toEpoch(dayStartMillis, todayRaw.ishaHours, offsets.ishaMin)

    val prevMaghribMillis = toEpoch(prevDayStartMillis, prevRaw.sunsetHours, offsets.maghribMin)
    val nextFajrMillis = toEpoch(nextDayStartMillis, nextRaw.fajrHours, offsets.fajrMin)

    // Night duration for early morning Tahajjud (between yesterday Maghrib and today Fajr)
    val prevNightDuration = (fajrMillis - prevMaghribMillis).coerceAtLeast(6 * 3_600_000L)
    val earlyMorningTahajjudStart = prevMaghribMillis + (prevNightDuration * 2L) / 3L

    // Upcoming night midpoint & last third (between today Maghrib and tomorrow Fajr)
    val tonightDuration = (nextFajrMillis - maghribMillis).coerceAtLeast(6 * 3_600_000L)
    val midnightMillis = maghribMillis + tonightDuration / 2L
    val lastThirdNightMillis = maghribMillis + (tonightDuration * 2L) / 3L

    // Daytime derived windows
    val sunriseForbiddenEnd = sunriseMillis + 15 * 60_000L
    val ishraqStart = sunriseMillis + 16 * 60_000L
    val dayDuration = (maghribMillis - sunriseMillis).coerceAtLeast(8 * 3_600_000L)
    val chashtStart = sunriseMillis + (dayDuration / 4L).coerceAtLeast(45 * 60_000L)
    val zawalStart = transitMillis - 6 * 60_000L
    val zawalEnd = dhuhrStartMillis - 60_000L
    val sunsetForbiddenStart = maghribMillis - 15 * 60_000L
    val awwabinStart = maghribMillis + 15 * 60_000L

    val asrWarningTime = BanglaDateFormatter.formatClockWithPeriod(
      epochMillis = sunsetForbiddenStart,
      use24Hour = false,
      timeZoneId = timeZoneId,
      strings = strings,
    )

    fun slot(
      type: PrayerType,
      category: PrayerCategory,
      arabicTitle: String,
      startMillis: Long,
      endMillis: Long,
      isReminderSupported: Boolean,
      titleOverride: String? = null,
      hintOverride: String? = null,
      rakaatOverride: String? = null,
      noteOverride: String? = null,
    ): PrayerWindow {
      val copy = strings.prayerWindows[type] ?: englishStrings.prayerWindows[type]!!
      return PrayerWindow(
        type = type,
        category = category,
        title = titleOverride ?: copy.title,
        arabicTitle = arabicTitle,
        startMillis = startMillis,
        endMillis = endMillis,
        optimalHint = hintOverride ?: copy.optimalHint,
        rakaatSummary = rakaatOverride ?: copy.rakaatSummary,
        shortNote = noteOverride ?: copy.shortNote,
        isReminderSupported = isReminderSupported,
      )
    }

    val windows = listOf(
      slot(
        type = PrayerType.TAHAJJUD,
        category = PrayerCategory.NAFL,
        arabicTitle = "صلاة التهجد",
        startMillis = earlyMorningTahajjudStart,
        endMillis = fajrMillis - 60_000L,
        isReminderSupported = true,
      ),
      slot(
        type = PrayerType.FAJR,
        category = PrayerCategory.FARD,
        arabicTitle = "صلاة الفجر",
        startMillis = fajrMillis,
        endMillis = sunriseMillis - 60_000L,
        isReminderSupported = true,
      ),
      slot(
        type = PrayerType.SUNRISE_FORBIDDEN,
        category = PrayerCategory.FORBIDDEN,
        arabicTitle = "وقت الشروق",
        startMillis = sunriseMillis,
        endMillis = sunriseForbiddenEnd,
        isReminderSupported = false,
      ),
      slot(
        type = PrayerType.ISHRAQ,
        category = PrayerCategory.NAFL,
        arabicTitle = "صلاة الإشراق",
        startMillis = ishraqStart,
        endMillis = chashtStart - 60_000L,
        isReminderSupported = true,
      ),
      slot(
        type = PrayerType.CHASHT,
        category = PrayerCategory.NAFL,
        arabicTitle = "صلاة الضحى",
        startMillis = chashtStart,
        endMillis = zawalStart - 60_000L,
        isReminderSupported = true,
      ),
      slot(
        type = PrayerType.ZAWAL_FORBIDDEN,
        category = PrayerCategory.FORBIDDEN,
        arabicTitle = "وقت الزوال",
        startMillis = zawalStart,
        endMillis = zawalEnd,
        isReminderSupported = false,
      ),
      slot(
        type = PrayerType.DHUHR,
        category = PrayerCategory.FARD,
        arabicTitle = if (isFriday) "صلاة الجمعة" else "صلاة الظهر",
        startMillis = dhuhrStartMillis,
        endMillis = asrStartMillis - 60_000L,
        isReminderSupported = true,
        titleOverride = if (isFriday) strings.jumuahTitle else null,
        hintOverride = if (isFriday) strings.jumuahHint else null,
        rakaatOverride = if (isFriday) strings.jumuahRakaat else null,
      ),
      slot(
        type = PrayerType.ASR,
        category = PrayerCategory.FARD,
        arabicTitle = "صلاة العصر",
        startMillis = asrStartMillis,
        endMillis = maghribMillis - 60_000L,
        isReminderSupported = true,
        hintOverride = String.format(strings.asrHintBeforeFormat, asrWarningTime),
        noteOverride = String.format(strings.asrNoteFormat, strings.asrTitle(asrJuristic)),
      ),
      slot(
        type = PrayerType.SUNSET_FORBIDDEN,
        category = PrayerCategory.FORBIDDEN,
        arabicTitle = "وقت الغروب",
        startMillis = sunsetForbiddenStart,
        endMillis = maghribMillis - 60_000L,
        isReminderSupported = false,
      ),
      slot(
        type = PrayerType.MAGHRIB,
        category = PrayerCategory.FARD,
        arabicTitle = "صلاة المغرب",
        startMillis = maghribMillis,
        endMillis = ishaStartMillis - 60_000L,
        isReminderSupported = true,
      ),
      slot(
        type = PrayerType.AWWABIN,
        category = PrayerCategory.NAFL,
        arabicTitle = "صلاة الأوابين",
        startMillis = awwabinStart,
        endMillis = ishaStartMillis - 60_000L,
        isReminderSupported = false,
      ),
      slot(
        type = PrayerType.ISHA,
        category = PrayerCategory.FARD,
        arabicTitle = "صلاة العشاء والوتر",
        startMillis = ishaStartMillis,
        endMillis = nextFajrMillis - 60_000L,
        isReminderSupported = true,
      ),
      slot(
        type = PrayerType.WITR,
        category = PrayerCategory.WAJIB,
        arabicTitle = "صلاة الوتر",
        startMillis = ishaStartMillis,
        endMillis = nextFajrMillis - 60_000L,
        isReminderSupported = false,
      ),
    )

    return DailySchedule(
      year = year,
      month = month,
      day = day,
      sehriEndMillis = fajrMillis,
      iftarMillis = maghribMillis,
      fajrStartMillis = fajrMillis,
      sunriseMillis = sunriseMillis,
      dhuhrStartMillis = dhuhrStartMillis,
      asrStartMillis = asrStartMillis,
      maghribStartMillis = maghribMillis,
      ishaStartMillis = ishaStartMillis,
      midnightMillis = midnightMillis,
      lastThirdNightMillis = lastThirdNightMillis,
      windows = windows,
    )
  }

  private fun computeRawSolarTimes(
    year: Int,
    month: Int,
    day: Int,
    lat: Double,
    lon: Double,
    tzHours: Double,
    method: CalculationMethod,
    asrJuristic: AsrJuristic,
  ): RawSolarTimes {
    val jd0 = julianDay(year, month, day)

    fun sunDeclinationAndEqT(localHour: Double): Pair<Double, Double> {
      val jd = jd0 + (localHour - tzHours) / 24.0
      val d = jd - 2451545.0
      val g = fixAngle(357.529 + 0.98560028 * d)
      val q = fixAngle(280.459 + 0.98564736 * d)
      val l = fixAngle(q + 1.915 * sinDeg(g) + 0.020 * sinDeg(2.0 * g))
      val e = 23.439 - 0.00000036 * d
      val ra = fixHour(atan2Deg(cosDeg(e) * sinDeg(l), cosDeg(l)) / 15.0)
      val decl = asinDeg(sinDeg(e) * sinDeg(l))
      var eqt = q / 15.0 - ra
      if (eqt > 12.0) eqt -= 24.0
      if (eqt < -12.0) eqt += 24.0
      return decl to eqt
    }

    fun computeTransit(approxHour: Double): Double {
      val (_, eqt) = sunDeclinationAndEqT(approxHour)
      return fixHour(12.0 + tzHours - lon / 15.0 - eqt)
    }

    fun computeSunAngleHour(angleBelowHorizon: Double, isMorning: Boolean, approxHour: Double): Double {
      val (decl, eqt) = sunDeclinationAndEqT(approxHour)
      val transit = fixHour(12.0 + tzHours - lon / 15.0 - eqt)
      val cosHa = (-sinDeg(angleBelowHorizon) - sinDeg(lat) * sinDeg(decl)) /
        (cosDeg(lat) * cosDeg(decl))
      val clamped = cosHa.coerceIn(-0.9999, 0.9999)
      val haHours = acosDeg(clamped) / 15.0
      return if (isMorning) transit - haHours else transit + haHours
    }

    fun computeAsrHour(shadowFactor: Double, approxHour: Double): Double {
      val (decl, eqt) = sunDeclinationAndEqT(approxHour)
      val transit = fixHour(12.0 + tzHours - lon / 15.0 - eqt)
      val targetAlt = atanDeg(1.0 / (shadowFactor + tanDeg(abs(lat - decl))))
      val cosHa = (sinDeg(targetAlt) - sinDeg(lat) * sinDeg(decl)) /
        (cosDeg(lat) * cosDeg(decl))
      val clamped = cosHa.coerceIn(-0.9999, 0.9999)
      val haHours = acosDeg(clamped) / 15.0
      return transit + haHours
    }

    // Two-pass refinement for sub-minute precision
    val transit1 = computeTransit(12.0)
    val transit = computeTransit(transit1)

    val sunrise1 = computeSunAngleHour(0.833, isMorning = true, approxHour = 6.0)
    val sunrise = computeSunAngleHour(0.833, isMorning = true, approxHour = sunrise1)

    val sunset1 = computeSunAngleHour(0.833, isMorning = false, approxHour = 18.0)
    val sunset = computeSunAngleHour(0.833, isMorning = false, approxHour = sunset1)

    val fajr1 = computeSunAngleHour(method.fajrAngle, isMorning = true, approxHour = 5.0)
    val fajr = computeSunAngleHour(method.fajrAngle, isMorning = true, approxHour = fajr1)

    val asr1 = computeAsrHour(asrJuristic.shadowFactor, approxHour = 15.5)
    val asr = computeAsrHour(asrJuristic.shadowFactor, approxHour = asr1)

    val isha = if (method.ishaFixedMinutesAfterMaghrib != null) {
      sunset + method.ishaFixedMinutesAfterMaghrib / 60.0
    } else {
      val isha1 = computeSunAngleHour(method.ishaAngle, isMorning = false, approxHour = 19.5)
      computeSunAngleHour(method.ishaAngle, isMorning = false, approxHour = isha1)
    }

    return RawSolarTimes(
      fajrHours = fajr,
      sunriseHours = sunrise,
      transitHours = transit,
      asrHours = asr,
      sunsetHours = sunset,
      ishaHours = isha,
    )
  }

  /**
   * Computes true Qibla bearing in degrees clockwise from True North (0..360).
   */
  fun calculateQiblaBearing(latitude: Double, longitude: Double): Double {
    val lat1 = Math.toRadians(latitude)
    val lon1 = Math.toRadians(longitude)
    val lat2 = Math.toRadians(KAABA_LAT)
    val lon2 = Math.toRadians(KAABA_LON)
    val dLon = lon2 - lon1
    val y = sin(dLon) * cos(lat2)
    val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
    val deg = Math.toDegrees(atan2(y, x))
    return (deg + 360.0) % 360.0
  }

  /**
   * Great-circle distance in kilometers to the Holy Kaaba in Makkah.
   */
  fun calculateDistanceToKaabaKm(latitude: Double, longitude: Double): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(KAABA_LAT - latitude)
    val dLon = Math.toRadians(KAABA_LON - longitude)
    val a = sin(dLat / 2) * sin(dLat / 2) +
      cos(Math.toRadians(latitude)) * cos(Math.toRadians(KAABA_LAT)) *
      sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadiusKm * c
  }

  private fun julianDay(year: Int, month: Int, day: Int): Double {
    var y = year
    var m = month
    if (m <= 2) {
      y -= 1
      m += 12
    }
    val a = floor(y / 100.0)
    val b = 2.0 - a + floor(a / 4.0)
    return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
  }

  private fun fixAngle(deg: Double): Double {
    var a = deg % 360.0
    if (a < 0) a += 360.0
    return a
  }

  private fun fixHour(hr: Double): Double {
    var h = hr % 24.0
    if (h < 0) h += 24.0
    return h
  }

  private fun sinDeg(d: Double): Double = sin(d * PI / 180.0)
  private fun cosDeg(d: Double): Double = cos(d * PI / 180.0)
  private fun tanDeg(d: Double): Double = tan(d * PI / 180.0)
  private fun asinDeg(x: Double): Double = asin(x) * 180.0 / PI
  private fun acosDeg(x: Double): Double = acos(x) * 180.0 / PI
  private fun atanDeg(x: Double): Double = atan(x) * 180.0 / PI
  private fun atan2Deg(y: Double, x: Double): Double = atan2(y, x) * 180.0 / PI
}
