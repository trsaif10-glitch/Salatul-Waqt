package com.example.domain.prayer

import android.icu.util.IslamicCalendar
import com.example.domain.localization.AppStrings
import com.example.domain.localization.englishStrings
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object BanglaDateFormatter {

  fun formatInt(value: Int): String = value.toString()

  fun formatShortDate(
    day: Int,
    month: Int,
    strings: AppStrings = englishStrings,
  ): String {
    val m = strings.gregorianShortMonths[(month - 1).coerceIn(0, 11)]
    return "$day $m"
  }

  fun formatClockCompact(
    epochMillis: Long,
    use24Hour: Boolean = false,
    timeZoneId: String = "UTC",
  ): String {
    val cal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId)).apply {
      timeInMillis = epochMillis
    }
    val hour24 = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)
    if (use24Hour) {
      return String.format(Locale.US, "%02d:%02d", hour24, minute)
    }
    val hour12 = when {
      hour24 == 0 -> 12
      hour24 > 12 -> hour24 - 12
      else -> hour24
    }
    return String.format(Locale.US, "%d:%02d", hour12, minute)
  }

  fun formatClockWithPeriod(
    epochMillis: Long,
    use24Hour: Boolean = false,
    timeZoneId: String = "UTC",
    strings: AppStrings = englishStrings,
  ): String {
    if (use24Hour) {
      return formatClockCompact(epochMillis, use24Hour = true, timeZoneId = timeZoneId)
    }
    val cal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId)).apply {
      timeInMillis = epochMillis
    }
    val hour24 = cal.get(Calendar.HOUR_OF_DAY)
    val amPm = if (hour24 < 12) strings.amLabel else strings.pmLabel
    return "${formatClockCompact(epochMillis, use24Hour = false, timeZoneId = timeZoneId)} $amPm"
  }

  fun formatCountdown(millisRemaining: Long): String {
    val safeSec = millisRemaining.coerceAtLeast(0L) / 1000L
    val hours = safeSec / 3600L
    val mins = (safeSec % 3600L) / 60L
    val secs = safeSec % 60L
    return String.format(Locale.US, "%02d:%02d:%02d", hours, mins, secs)
  }

  fun formatDuration(
    startMillis: Long,
    endMillis: Long,
    strings: AppStrings = englishStrings,
  ): String {
    val diffMin = ((endMillis - startMillis).coerceAtLeast(0L) / 60_000L).toInt()
    val h = diffMin / 60
    val m = diffMin % 60
    return when {
      h > 0 && m > 0 -> "$h${strings.hourShort} $m${strings.minuteShort}"
      h > 0 -> "$h${strings.hourShort}"
      else -> "$m${strings.minuteShort}"
    }
  }

  fun formatGregorianDate(
    year: Int,
    month: Int,
    day: Int,
    timeZoneId: String,
    strings: AppStrings = englishStrings,
  ): String {
    val cal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId)).apply {
      set(year, month - 1, day, 12, 0, 0)
    }
    val weekday = strings.weekdays[(cal.get(Calendar.DAY_OF_WEEK) - 1).coerceIn(0, 6)]
    val monthName = strings.gregorianMonths[(month - 1).coerceIn(0, 11)]
    return "$weekday, $day $monthName $year"
  }

  fun formatMonthYear(
    year: Int,
    month: Int,
    strings: AppStrings = englishStrings,
  ): String {
    val monthName = strings.gregorianMonths[(month - 1).coerceIn(0, 11)]
    return "$monthName $year"
  }

  fun formatHijriDate(
    epochMillis: Long,
    offsetDays: Int,
    timeZoneId: String,
    strings: AppStrings = englishStrings,
  ): String {
    return try {
      val adjustedMillis = epochMillis + offsetDays * 86_400_000L
      val ic = IslamicCalendar(android.icu.util.TimeZone.getTimeZone(timeZoneId)).apply {
        calculationType = IslamicCalendar.CalculationType.ISLAMIC_UMALQURA
        timeInMillis = adjustedMillis
      }
      val hYear = ic.get(IslamicCalendar.YEAR)
      val hMonth = ic.get(IslamicCalendar.MONTH)
      val hDay = ic.get(IslamicCalendar.DAY_OF_MONTH)
      val mName = strings.hijriMonths[hMonth.coerceIn(0, 11)]
      "$hDay $mName $hYear ${strings.hijriAhSuffix}"
    } catch (_: Throwable) {
      strings.hijriDateFallback
    }
  }

  fun formatHijriShort(
    epochMillis: Long,
    offsetDays: Int,
    timeZoneId: String,
    strings: AppStrings = englishStrings,
  ): String {
    return try {
      val adjustedMillis = epochMillis + offsetDays * 86_400_000L
      val ic = IslamicCalendar(android.icu.util.TimeZone.getTimeZone(timeZoneId)).apply {
        calculationType = IslamicCalendar.CalculationType.ISLAMIC_UMALQURA
        timeInMillis = adjustedMillis
      }
      val hMonth = ic.get(IslamicCalendar.MONTH)
      val hDay = ic.get(IslamicCalendar.DAY_OF_MONTH)
      val mName = strings.hijriMonths[hMonth.coerceIn(0, 11)]
      "$hDay $mName"
    } catch (_: Throwable) {
      strings.hijriDateFallback
    }
  }
}
