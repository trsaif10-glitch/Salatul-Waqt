package com.example

import com.example.domain.localization.AppLocalizations
import com.example.domain.model.AppLanguage
import com.example.domain.model.AsrJuristic
import com.example.domain.model.CalculationMethod
import com.example.domain.model.PrayerType
import com.example.domain.prayer.BanglaDateFormatter
import com.example.domain.prayer.CityCatalog
import com.example.domain.prayer.PrayerKnowledgeBase
import com.example.domain.prayer.PrayerTimeCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class PrayerTimeCalculatorTest {

  @Test
  fun dhakaScheduleMaintainsChronologicalOrderAndAllWindows() {
    val dhaka = CityCatalog.allLocations.first { it.name == "Dhaka" }
    val schedule = PrayerTimeCalculator.calculateSchedule(
      year = 2026,
      month = 11,
      day = 14,
      location = dhaka,
      method = CalculationMethod.ISLAMIC_FOUNDATION_BD,
      asrJuristic = AsrJuristic.HANAFI,
    )

    assertTrue(schedule.fajrStartMillis < schedule.sunriseMillis)
    assertTrue(schedule.sunriseMillis < schedule.dhuhrStartMillis)
    assertTrue(schedule.dhuhrStartMillis < schedule.asrStartMillis)
    assertTrue(schedule.asrStartMillis < schedule.maghribStartMillis)
    assertTrue(schedule.maghribStartMillis < schedule.ishaStartMillis)
    assertTrue(schedule.midnightMillis > schedule.maghribStartMillis)
    assertTrue(schedule.lastThirdNightMillis > schedule.midnightMillis)

    // Verify all A-to-Z prayer slots are present
    assertEquals(13, schedule.windows.size)
    assertTrue(schedule.windows.any { it.type == PrayerType.TAHAJJUD })
    assertTrue(schedule.windows.any { it.type == PrayerType.ISHRAQ })
    assertTrue(schedule.windows.any { it.type == PrayerType.CHASHT })
    assertTrue(schedule.windows.any { it.type == PrayerType.AWWABIN })
    assertTrue(schedule.windows.any { it.type == PrayerType.WITR })
    assertTrue(schedule.windows.any { it.type == PrayerType.ZAWAL_FORBIDDEN })

    // Verify Dhaka Fajr on Nov 14 is around 4:50 - 5:10 AM local time
    val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka")).apply {
      timeInMillis = schedule.fajrStartMillis
    }
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    assertEquals(4, hour)
  }

  @Test
  fun hanafiAsrIsLaterThanStandardAsr() {
    val makkah = CityCatalog.defaultLocation
    val hanafi = PrayerTimeCalculator.calculateSchedule(
      2026, 6, 21, makkah, CalculationMethod.MWL, AsrJuristic.HANAFI,
    )
    val standard = PrayerTimeCalculator.calculateSchedule(
      2026, 6, 21, makkah, CalculationMethod.MWL, AsrJuristic.STANDARD,
    )
    assertTrue(hanafi.asrStartMillis > standard.asrStartMillis)
  }

  @Test
  fun dhakaQiblaBearingIsAround277Degrees() {
    val dhaka = CityCatalog.allLocations.first { it.name == "Dhaka" }
    val bearing = PrayerTimeCalculator.calculateQiblaBearing(dhaka.latitude, dhaka.longitude)
    assertTrue("Expected ~277.6 deg, got $bearing", bearing in 275.0..280.0)
  }

  @Test
  fun englishClockAndHijriDatesFormatCorrectly() {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/London")).apply {
      set(2026, Calendar.NOVEMBER, 14, 15, 45, 0)
    }
    assertEquals("3:45", BanglaDateFormatter.formatClockCompact(cal.timeInMillis, use24Hour = false, timeZoneId = "Europe/London"))
    assertEquals("15:45", BanglaDateFormatter.formatClockCompact(cal.timeInMillis, use24Hour = true, timeZoneId = "Europe/London"))
    assertEquals("3:45 PM", BanglaDateFormatter.formatClockWithPeriod(cal.timeInMillis, use24Hour = false, timeZoneId = "Europe/London"))
    assertEquals("01:05:09", BanglaDateFormatter.formatCountdown(3_909_000L))
    val gregorian = BanglaDateFormatter.formatGregorianDate(2026, 11, 14, "Europe/London")
    assertTrue(gregorian.contains("14 November 2026"))
    val london = CityCatalog.defaultForTimeZone("Europe/London")
    assertEquals("London", london.name)
  }

  @Test
  fun calculateMonthSchedulesMatchesDailySchedule() {
    val london = CityCatalog.allLocations.first { it.name == "London" }
    val monthSchedules = PrayerTimeCalculator.calculateMonthSchedules(
      year = 2026,
      month = 11,
      location = london,
      method = CalculationMethod.MWL,
      asrJuristic = AsrJuristic.STANDARD,
    )
    assertEquals(30, monthSchedules.size)
    val day14Fast = monthSchedules[13]
    val day14Direct = PrayerTimeCalculator.calculateSchedule(
      year = 2026,
      month = 11,
      day = 14,
      location = london,
      method = CalculationMethod.MWL,
      asrJuristic = AsrJuristic.STANDARD,
    )
    assertEquals(day14Direct.fajrStartMillis, day14Fast.fajrStartMillis)
    assertEquals(day14Direct.dhuhrStartMillis, day14Fast.dhuhrStartMillis)
    assertEquals(day14Direct.asrStartMillis, day14Fast.asrStartMillis)
    assertEquals(day14Direct.maghribStartMillis, day14Fast.maghribStartMillis)
    assertEquals(day14Direct.ishaStartMillis, day14Fast.ishaStartMillis)
    assertEquals(day14Direct.lastThirdNightMillis, day14Fast.lastThirdNightMillis)
    assertEquals(day14Direct.windows.size, day14Fast.windows.size)
  }

  @Test
  fun allTenLanguagesProvideCompleteTranslationsAndKnowledgeBase() {
    val makkah = CityCatalog.defaultLocation
    val englishSched = PrayerTimeCalculator.calculateSchedule(
      2026, 11, 14, makkah, CalculationMethod.UMM_AL_QURA, AsrJuristic.STANDARD,
      strings = AppLocalizations.forLanguage(AppLanguage.ENGLISH),
    )

    for (lang in AppLanguage.entries) {
      val s = AppLocalizations.forLanguage(lang)
      assertEquals(lang, s.language)
      assertEquals(7, s.weekdays.size)
      assertEquals(12, s.gregorianMonths.size)
      assertEquals(12, s.gregorianShortMonths.size)
      assertEquals(12, s.hijriMonths.size)
      assertEquals(8, s.directions8.size)
      assertEquals(6, s.tasbihPresetTitles.size)
      assertEquals(PrayerType.entries.size, s.prayerWindows.size)
      assertEquals(CalculationMethod.entries.size, s.calcMethodTitles.size)
      assertEquals(CalculationMethod.entries.size, s.calcMethodSubtitles.size)
      assertFalse(s.prayerScheduleTitle.isBlank())
      assertFalse(s.settingsTitle.isBlank())

      val sched = PrayerTimeCalculator.calculateSchedule(
        2026, 11, 14, makkah, CalculationMethod.UMM_AL_QURA, AsrJuristic.STANDARD, strings = s,
      )
      assertEquals(13, sched.windows.size)

      for (type in PrayerType.entries) {
        val detail = PrayerKnowledgeBase.getPrayerDetail(type, lang)
        assertFalse(detail.title.isBlank())
        assertFalse(detail.timeRule.isBlank())
      }
      val guide = PrayerKnowledgeBase.getGuideTopics(lang)
      assertEquals(6, guide.size)
    }

    val bnSched = PrayerTimeCalculator.calculateSchedule(
      2026, 11, 14, makkah, CalculationMethod.UMM_AL_QURA, AsrJuristic.STANDARD,
      strings = AppLocalizations.forLanguage(AppLanguage.BENGALI),
    )
    assertNotEquals(englishSched.windows.first().title, bnSched.windows.first().title)
    assertTrue(AppLanguage.ARABIC.isRtl)
    assertTrue(AppLanguage.URDU.isRtl)
    assertFalse(AppLanguage.ENGLISH.isRtl)
    assertFalse(AppLanguage.BENGALI.isRtl)
  }
}
