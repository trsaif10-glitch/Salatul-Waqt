package com.example.domain.model

import androidx.compose.runtime.Immutable

enum class PrayerType(val id: String) {
  TAHAJJUD("tahajjud"),
  FAJR("fajr"),
  SUNRISE_FORBIDDEN("sunrise_forbidden"),
  ISHRAQ("ishraq"),
  CHASHT("chasht"),
  ZAWAL_FORBIDDEN("zawal_forbidden"),
  DHUHR("dhuhr"),
  ASR("asr"),
  SUNSET_FORBIDDEN("sunset_forbidden"),
  MAGHRIB("maghrib"),
  AWWABIN("awwabin"),
  ISHA("isha"),
  WITR("witr"),
}

enum class PrayerCategory {
  FARD,
  NAFL,
  WAJIB,
  FORBIDDEN,
}

enum class CalculationMethod(
  val titleEn: String,
  val subtitleEn: String,
  val fajrAngle: Double,
  val ishaAngle: Double,
  val ishaFixedMinutesAfterMaghrib: Int? = null,
) {
  MWL(
    titleEn = "Muslim World League (MWL)",
    subtitleEn = "Fajr 18°, Isha 17° (Global Standard)",
    fajrAngle = 18.0,
    ishaAngle = 17.0,
  ),
  ISNA(
    titleEn = "ISNA (North America)",
    subtitleEn = "Fajr 15°, Isha 15°",
    fajrAngle = 15.0,
    ishaAngle = 15.0,
  ),
  UMM_AL_QURA(
    titleEn = "Umm al-Qura (Makkah)",
    subtitleEn = "Fajr 18.5°, Isha 90 min after Maghrib",
    fajrAngle = 18.5,
    ishaAngle = 18.5,
    ishaFixedMinutesAfterMaghrib = 90,
  ),
  EGYPTIAN(
    titleEn = "Egyptian General Authority",
    subtitleEn = "Fajr 19.5°, Isha 17.5°",
    fajrAngle = 19.5,
    ishaAngle = 17.5,
  ),
  ISLAMIC_FOUNDATION_BD(
    titleEn = "Karachi / South Asia",
    subtitleEn = "Fajr 18°, Isha 18°",
    fajrAngle = 18.0,
    ishaAngle = 18.0,
  ),
}

enum class AsrJuristic(val titleEn: String, val shadowFactor: Double, val descEn: String) {
  STANDARD(
    titleEn = "Standard (Shafi'i, Maliki, Hanbali)",
    shadowFactor = 1.0,
    descEn = "Asr begins when an object's shadow equals its height",
  ),
  HANAFI(
    titleEn = "Hanafi",
    shadowFactor = 2.0,
    descEn = "Asr begins when an object's shadow reaches twice its height",
  ),
}

enum class AppLanguage(
  val code: String,
  val nativeName: String,
  val englishName: String,
  val isRtl: Boolean = false,
) {
  ENGLISH("en", "English", "English"),
  BENGALI("bn", "বাংলা", "Bengali"),
  ARABIC("ar", "العربية", "Arabic", isRtl = true),
  URDU("ur", "اردو", "Urdu", isRtl = true),
  INDONESIAN("id", "Bahasa Indonesia", "Indonesian"),
  MALAY("ms", "Bahasa Melayu", "Malay"),
  TURKISH("tr", "Türkçe", "Turkish"),
  FRENCH("fr", "Français", "French"),
  SPANISH("es", "Español", "Spanish"),
  HINDI("hi", "हिन्दी", "Hindi"),
}

enum class ThemeMode(val titleEn: String) {
  LIGHT("Classic White"),
  SYSTEM("System"),
  DARK("Dark"),
}

@Immutable
data class PrayerLocation(
  val name: String,
  val region: String,
  val latitude: Double,
  val longitude: Double,
  val timeZoneId: String,
)

@Immutable
data class PrayerWindow(
  val type: PrayerType,
  val category: PrayerCategory,
  val title: String,
  val arabicTitle: String,
  val startMillis: Long,
  val endMillis: Long,
  val optimalHint: String,
  val rakaatSummary: String,
  val shortNote: String,
  val isReminderSupported: Boolean,
)

@Immutable
data class DailySchedule(
  val year: Int,
  val month: Int,
  val day: Int,
  val sehriEndMillis: Long,
  val iftarMillis: Long,
  val fajrStartMillis: Long,
  val sunriseMillis: Long,
  val dhuhrStartMillis: Long,
  val asrStartMillis: Long,
  val maghribStartMillis: Long,
  val ishaStartMillis: Long,
  val midnightMillis: Long,
  val lastThirdNightMillis: Long,
  val windows: List<PrayerWindow>,
)

@Immutable
data class UiPrayerItem(
  val window: PrayerWindow,
  val startTimeText: String,
  val endTimeText: String,
  val startPeriodText: String,
  val endPeriodText: String,
)

@Immutable
data class UiMonthlyDayItem(
  val schedule: DailySchedule,
  val dateShortText: String,
  val hijriShortText: String,
  val fajrText: String,
  val dhuhrText: String,
  val asrText: String,
  val maghribText: String,
  val ishaText: String,
  val sehriText: String,
  val iftarText: String,
  val isToday: Boolean,
)

data class RakaatPart(
  val count: String,
  val type: String,
)

data class PrayerDetailInfo(
  val type: PrayerType,
  val title: String,
  val arabicTitle: String,
  val categoryLabel: String,
  val totalRakaat: String,
  val rakaatBreakdown: List<RakaatPart>,
  val timeRule: String,
  val optimalAndMakruh: String,
  val niyyahPronunciation: String,
  val niyyahMeaning: String,
  val virtues: String,
  val specialAmal: String,
)

data class GuideTopic(
  val id: String,
  val title: String,
  val subtitle: String,
  val badge: String,
  val sections: List<GuideSection>,
)

data class GuideSection(
  val heading: String,
  val points: List<String>,
)

@Immutable
data class PrayerOffsets(
  val fajrMin: Int = 0,
  val sunriseMin: Int = 0,
  val dhuhrMin: Int = 0,
  val asrMin: Int = 0,
  val maghribMin: Int = 0,
  val ishaMin: Int = 0,
)

@Immutable
data class UserPreferences(
  val location: PrayerLocation,
  val autoDetectLocation: Boolean = true,
  val language: AppLanguage = AppLanguage.ENGLISH,
  val calculationMethod: CalculationMethod = CalculationMethod.MWL,
  val asrJuristic: AsrJuristic = AsrJuristic.STANDARD,
  val hijriOffsetDays: Int = 0,
  val use24HourFormat: Boolean = false,
  val themeMode: ThemeMode = ThemeMode.LIGHT,
  val offsets: PrayerOffsets = PrayerOffsets(),
  val enabledReminders: Set<PrayerType> = setOf(
    PrayerType.FAJR,
    PrayerType.DHUHR,
    PrayerType.ASR,
    PrayerType.MAGHRIB,
    PrayerType.ISHA,
  ),
  val tasbihCount: Int = 0,
  val tasbihPresetIndex: Int = 0,
)
