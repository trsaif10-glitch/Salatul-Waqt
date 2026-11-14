package com.example.domain.localization

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.domain.model.AppLanguage
import com.example.domain.model.AsrJuristic
import com.example.domain.model.CalculationMethod
import com.example.domain.model.PrayerType
import com.example.domain.model.ThemeMode

@Immutable
data class LocalizedPrayerWindow(
  val title: String,
  val optimalHint: String,
  val rakaatSummary: String,
  val shortNote: String,
)

@Immutable
data class AppStrings(
  val language: AppLanguage,
  val back: String,
  val close: String,
  val cancel: String,
  val save: String,
  val selected: String,
  val minShort: String,

  // Date & Clock
  val weekdays: List<String>,
  val gregorianMonths: List<String>,
  val gregorianShortMonths: List<String>,
  val hijriMonths: List<String>,
  val hijriAhSuffix: String,
  val hijriDateFallback: String,
  val amLabel: String,
  val pmLabel: String,
  val hourShort: String,
  val minuteShort: String,

  // Home screen
  val forbiddenTimeBadge: String,
  val currentPrayerBadge: String,
  val nextPrayerBadge: String,
  val startsLabel: String,
  val endsLabel: String,
  val endsPrefix: String,
  val timeRemaining: String,
  val startsIn: String,
  val upNextPrefix: String,
  val prayerScheduleTitle: String,
  val prayerScheduleSubtitle: String,
  val qiblaCompassTitle: String,
  val qiblaCompassSubtitle: String,
  val sehriIftarTitle: String,
  val dailyTimesFallback: String,
  val tasbihTitle: String,
  val tasbihSubtitle: String,
  val hijriCalendarTitle: String,
  val hijriCalendarSubtitle: String,
  val cdChangeCity: String,
  val cdChangeLanguage: String,
  val cdDetectGps: String,
  val cdSettings: String,

  // All Prayers & Guide
  val tabAllPrayers: String,
  val tabSalahGuide: String,
  val guideSearchPlaceholder: String,
  val reminderEnabledCd: String,
  val reminderDisabledCd: String,

  // Prayer Detail Sheet
  val startEndTimeLabel: String,
  val rakaatBreakdownPrefix: String,
  val timeWindowRulesTitle: String,
  val optimalMakruhTitle: String,
  val intentionNiyyahTitle: String,
  val meaningPrefix: String,
  val virtuesHadithTitle: String,
  val recommendedSunnahTitle: String,
  val enableReminderBtn: String,
  val disableReminderBtn: String,

  // Qibla screen
  val qiblaFromPrefix: String,
  val facingQiblaMsg: String,
  val qiblaDirectionFromPrefix: String,
  val fromNorthLabel: String,
  val distanceLabel: String,
  val kmUnit: String,
  val qiblaNeedleLabel: String,
  val compassUnavailableFormat: String,
  val compassInstruction: String,
  val directions8: List<String>,

  // Sehri & Iftar screen
  val sehriEndsToday: String,
  val todaysIftar: String,
  val fastingDuasTitle: String,
  val suhoorIntentionLabel: String,
  val suhoorIntentionDesc: String,
  val iftarDuaLabel: String,
  val iftarDuaDesc: String,
  val sehriIftarTimetableSuffix: String,
  val colDate: String,
  val colSehriEnds: String,
  val colIftar: String,

  // Tasbih screen
  val digitalTasbihTitle: String,
  val targetPrefix: String,
  val resetZeroBtn: String,
  val tapToCountCd: String,
  val tasbihPresetTitles: List<String>,

  // Hijri Calendar screen
  val hijriCalendarHeader: String,
  val tapDayDetailsSuffix: String,
  val prevMonthCd: String,
  val currentMonthCd: String,
  val nextMonthCd: String,
  val colDateHijri: String,
  val colFajr: String,
  val colDhuhr: String,
  val colAsr: String,
  val colMaghrib: String,
  val colIsha: String,

  // Pickers
  val selectLocationTitle: String,
  val currentCoordsPrefix: String,
  val detectingGps: String,
  val detectGpsBtn: String,
  val searchCitiesPlaceholder: String,
  val selectLanguageTitle: String,
  val selectLanguageSubtitle: String,
  val searchLanguagePlaceholder: String,

  // Settings
  val settingsTitle: String,
  val languageSectionTitle: String,
  val languageSectionSubtitle: String,
  val changeLanguageBtn: String,
  val locationSectionTitle: String,
  val autoDetectLocationTitle: String,
  val autoDetectLocationDesc: String,
  val autoLocationModeBadge: String,
  val manualLocationModeBadge: String,
  val changeCityBtn: String,
  val locatingBtn: String,
  val useGpsBtn: String,
  val asrJuristicSectionTitle: String,
  val asrStandardTitle: String,
  val asrStandardDesc: String,
  val asrHanafiTitle: String,
  val asrHanafiDesc: String,
  val calcMethodSectionTitle: String,
  val manualOffsetsBtn: String,
  val calcMethodTitles: Map<CalculationMethod, String>,
  val calcMethodSubtitles: Map<CalculationMethod, String>,
  val dateClockThemeTitle: String,
  val hijriAdjustmentTitle: String,
  val hijriAdjustmentDesc: String,
  val decreaseDayCd: String,
  val increaseDayCd: String,
  val clock24hTitle: String,
  val clock24hDesc: String,
  val appThemeTitle: String,
  val themeLightTitle: String,
  val themeSystemTitle: String,
  val themeDarkTitle: String,
  val notificationsSectionTitle: String,
  val notifEnabledDesc: String,
  val notifDisabledDesc: String,
  val allowNotifBtn: String,
  val sendTestNotifBtn: String,
  val offsetsDialogTitle: String,
  val offsetFajrSehri: String,
  val offsetSunrise: String,
  val offsetDhuhr: String,
  val offsetAsr: String,
  val offsetMaghribIftar: String,
  val offsetIsha: String,
  val decreaseCd: String,
  val increaseCd: String,

  // Messages & Notifications
  val locationSetPrefix: String,
  val locationUpdatedPrefix: String,
  val gpsUnavailableMsg: String,
  val locationPermDeniedMsg: String,
  val notifEnabledMsg: String,
  val notifDeniedMsg: String,
  val testNotifTitle: String,
  val testNotifBodySuffix: String,
  val testNotifSentMsg: String,
  val notifTimeSuffix: String,
  val notifStartedAtWord: String,

  // Prayer windows
  val prayerWindows: Map<PrayerType, LocalizedPrayerWindow>,
  val jumuahTitle: String,
  val jumuahHint: String,
  val jumuahRakaat: String,
  val asrHintBeforeFormat: String,
  val asrNoteFormat: String,
) {
  fun asrTitle(juristic: AsrJuristic): String = when (juristic) {
    AsrJuristic.STANDARD -> asrStandardTitle
    AsrJuristic.HANAFI -> asrHanafiTitle
  }

  fun asrDesc(juristic: AsrJuristic): String = when (juristic) {
    AsrJuristic.STANDARD -> asrStandardDesc
    AsrJuristic.HANAFI -> asrHanafiDesc
  }

  fun themeTitle(mode: ThemeMode): String = when (mode) {
    ThemeMode.LIGHT -> themeLightTitle
    ThemeMode.SYSTEM -> themeSystemTitle
    ThemeMode.DARK -> themeDarkTitle
  }

  fun methodTitle(method: CalculationMethod): String = calcMethodTitles[method] ?: method.titleEn
  fun methodSubtitle(method: CalculationMethod): String = calcMethodSubtitles[method] ?: method.subtitleEn
}

val LocalAppStrings = staticCompositionLocalOf { englishStrings }

object AppLocalizations {
  fun forLanguage(language: AppLanguage): AppStrings = when (language) {
    AppLanguage.ENGLISH -> englishStrings
    AppLanguage.BENGALI -> bengaliStrings
    AppLanguage.ARABIC -> arabicStrings
    AppLanguage.URDU -> urduStrings
    AppLanguage.INDONESIAN -> indonesianStrings
    AppLanguage.MALAY -> malayStrings
    AppLanguage.TURKISH -> turkishStrings
    AppLanguage.FRENCH -> frenchStrings
    AppLanguage.SPANISH -> spanishStrings
    AppLanguage.HINDI -> hindiStrings
  }
}
