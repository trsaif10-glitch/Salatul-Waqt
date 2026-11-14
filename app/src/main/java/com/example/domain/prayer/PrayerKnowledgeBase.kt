package com.example.domain.prayer

import com.example.domain.localization.arabicDetails
import com.example.domain.localization.arabicGuideTopics
import com.example.domain.localization.bengaliDetails
import com.example.domain.localization.bengaliGuideTopics
import com.example.domain.localization.frenchDetails
import com.example.domain.localization.frenchGuideTopics
import com.example.domain.localization.hindiDetails
import com.example.domain.localization.hindiGuideTopics
import com.example.domain.localization.indonesianDetails
import com.example.domain.localization.indonesianGuideTopics
import com.example.domain.localization.malayDetails
import com.example.domain.localization.malayGuideTopics
import com.example.domain.localization.spanishDetails
import com.example.domain.localization.spanishGuideTopics
import com.example.domain.localization.turkishDetails
import com.example.domain.localization.turkishGuideTopics
import com.example.domain.localization.urduDetails
import com.example.domain.localization.urduGuideTopics
import com.example.domain.model.AppLanguage
import com.example.domain.model.GuideSection
import com.example.domain.model.GuideTopic
import com.example.domain.model.PrayerDetailInfo
import com.example.domain.model.PrayerType
import com.example.domain.model.RakaatPart

object PrayerKnowledgeBase {

  fun getPrayerDetail(
    type: PrayerType,
    language: AppLanguage = AppLanguage.ENGLISH,
  ): PrayerDetailInfo {
    val map = when (language) {
      AppLanguage.ENGLISH -> detailsMap
      AppLanguage.BENGALI -> bengaliDetails
      AppLanguage.ARABIC -> arabicDetails
      AppLanguage.URDU -> urduDetails
      AppLanguage.INDONESIAN -> indonesianDetails
      AppLanguage.MALAY -> malayDetails
      AppLanguage.TURKISH -> turkishDetails
      AppLanguage.FRENCH -> frenchDetails
      AppLanguage.SPANISH -> spanishDetails
      AppLanguage.HINDI -> hindiDetails
    }
    return map[type] ?: detailsMap[type] ?: detailsMap[PrayerType.FAJR]!!
  }

  fun getGuideTopics(language: AppLanguage = AppLanguage.ENGLISH): List<GuideTopic> = when (language) {
    AppLanguage.ENGLISH -> guideTopics
    AppLanguage.BENGALI -> bengaliGuideTopics
    AppLanguage.ARABIC -> arabicGuideTopics
    AppLanguage.URDU -> urduGuideTopics
    AppLanguage.INDONESIAN -> indonesianGuideTopics
    AppLanguage.MALAY -> malayGuideTopics
    AppLanguage.TURKISH -> turkishGuideTopics
    AppLanguage.FRENCH -> frenchGuideTopics
    AppLanguage.SPANISH -> spanishGuideTopics
    AppLanguage.HINDI -> hindiGuideTopics
  }

  private val detailsMap: Map<PrayerType, PrayerDetailInfo> = mapOf(
    PrayerType.TAHAJJUD to PrayerDetailInfo(
      type = PrayerType.TAHAJJUD,
      title = "Salat al-Tahajjud",
      arabicTitle = "صلاة التهجد",
      categoryLabel = "Best Voluntary (Nafl) Prayer",
      totalRakaat = "2 to 12 Rakaat (in pairs of 2)",
      rakaatBreakdown = listOf(
        RakaatPart("2 x 4 = 8", "Rakaat Nafl (Recommended)"),
        RakaatPart("3", "Rakaat Witr (if not prayed after Isha)"),
      ),
      timeRule = "May be prayed any time after Isha and sleeping; the final third of the night before true dawn (Fajr) is the most virtuous window.",
      optimalAndMakruh = "Optimal: Final third of the night (20–40 min before Fajr). Once Fajr enters, Tahajjud time ends.",
      niyyahPronunciation = "Intention in the heart: Nawaytu an usalliya lillahi ta'ala rak'atay salatit-tahajjudi sunnatan — Allahu Akbar.",
      niyyahMeaning = "I intend to pray two rakaat of Tahajjud voluntarily for the sake of Allah, facing the Qibla.",
      virtues = "The Prophet (peace be upon him) said: 'The best prayer after the obligatory prayers is the night prayer.' Allah descends to the lowest heaven in the last third of the night to answer supplications.",
      specialAmal = "Follow Tahajjud with Istighfar (seeking forgiveness), Durood/Salawat, and sincere personal dua.",
    ),
    PrayerType.FAJR to PrayerDetailInfo(
      type = PrayerType.FAJR,
      title = "Fajr Prayer",
      arabicTitle = "صلاة الفجر",
      categoryLabel = "1st Obligatory (Fard) Prayer",
      totalRakaat = "4 Rakaat Total",
      rakaatBreakdown = listOf(
        RakaatPart("2", "Sunnah Mu'akkadah"),
        RakaatPart("2", "Fard (Audible Recitation)"),
      ),
      timeRule = "From true dawn (Subh Sadiq — horizontal white light along the eastern horizon) until just before sunrise.",
      optimalAndMakruh = "Best prayed promptly in congregation. Fajr time expires the moment the upper edge of the sun rises.",
      niyyahPronunciation = "Intention in the heart: Nawaytu an usalliya lillahi ta'ala rak'atay salatil-fajri fardullahi ta'ala — Allahu Akbar.",
      niyyahMeaning = "I intend to pray two fard rakaat of Fajr for Allah, facing the Qibla.",
      virtues = "The two Sunnah rakaat of Fajr are better than the world and everything in it. Whoever prays Fajr in congregation is under Allah's protection all day.",
      specialAmal = "Recite Ayat al-Kursi, the 3 Quls, Sayyid al-Istighfar, and morning Adhkar after Fajr.",
    ),
    PrayerType.SUNRISE_FORBIDDEN to PrayerDetailInfo(
      type = PrayerType.SUNRISE_FORBIDDEN,
      title = "Sunrise Forbidden Time",
      arabicTitle = "وقت طلوع الشمس",
      categoryLabel = "Prohibited Prayer Window",
      totalRakaat = "No Prayers Allowed",
      rakaatBreakdown = listOf(
        RakaatPart("0", "Fard, Qaza & Nafl all prohibited"),
      ),
      timeRule = "From sunrise until the sun rises approximately one spear-height above the horizon (~15 minutes).",
      optimalAndMakruh = "Avoid obligatory, makeup (Qaza), voluntary (Nafl), funeral prayers, and prostrations of recitation during this brief window.",
      niyyahPronunciation = "Wait ~15 minutes before praying; silent remembrance (Dhikr) and Durood are permissible.",
      niyyahMeaning = "The Prophet (peace be upon him) prohibited prayer at sunrise to avoid resembling sun-worship.",
      virtues = "Waiting peacefully in remembrance until Ishraq begins brings immense reward.",
      specialAmal = "Recite SubhanAllahi wa bihamdihi, Salawat, and morning Adhkar.",
    ),
    PrayerType.ISHRAQ to PrayerDetailInfo(
      type = PrayerType.ISHRAQ,
      title = "Salat al-Ishraq",
      arabicTitle = "صلاة الإشراق",
      categoryLabel = "Voluntary (Nafl) Prayer",
      totalRakaat = "2 or 4 Rakaat",
      rakaatBreakdown = listOf(
        RakaatPart("2 / 4", "Rakaat Nafl"),
      ),
      timeRule = "Begins ~15–20 minutes after sunrise and continues until Duha/Chasht begins.",
      optimalAndMakruh = "Praying Fajr in congregation, remaining seated in remembrance until after sunrise, and then praying 2 rakaat of Ishraq is most rewarding.",
      niyyahPronunciation = "Intention in the heart: Nawaytu an usalliya lillahi ta'ala rak'atay salatil-ishraqi naflan — Allahu Akbar.",
      niyyahMeaning = "I intend to pray two voluntary rakaat of Ishraq for the sake of Allah.",
      virtues = "Narrated in Tirmidhi: Whoever prays Fajr in congregation, remembers Allah until the sun rises, and prays two rakaat receives the reward of a complete Hajj and Umrah.",
      specialAmal = "Perform at least 2 rakaat in gratitude at the start of your day.",
    ),
    PrayerType.CHASHT to PrayerDetailInfo(
      type = PrayerType.CHASHT,
      title = "Salat al-Duha (Chasht)",
      arabicTitle = "صلاة الضحى",
      categoryLabel = "Voluntary (Nafl) Prayer",
      totalRakaat = "2 to 12 Rakaat",
      rakaatBreakdown = listOf(
        RakaatPart("2 - 12", "Rakaat Nafl (in sets of 2)"),
      ),
      timeRule = "From mid-morning (when the sun has risen well and heat increases) until just before Solar Noon (Zawal).",
      optimalAndMakruh = "Optimal around mid-to-late morning (roughly 9:00–11:30 AM local solar time). Ends before Zawal begins.",
      niyyahPronunciation = "Intention in the heart: Nawaytu an usalliya lillahi ta'ala rak'atay salatad-duha naflan — Allahu Akbar.",
      niyyahMeaning = "I intend to pray two voluntary rakaat of Duha for the sake of Allah.",
      virtues = "In Sahih Muslim: Charity is due on every one of the 360 joints of the human body each day, and two rakaat of Duha fulfill all of them.",
      specialAmal = "Praying 2 or 4 rakaat consistently brings barakah in daily work and sustenance.",
    ),
    PrayerType.ZAWAL_FORBIDDEN to PrayerDetailInfo(
      type = PrayerType.ZAWAL_FORBIDDEN,
      title = "Solar Noon / Zawal Forbidden Time",
      arabicTitle = "وقت الزوال",
      categoryLabel = "Prohibited Prayer Window",
      totalRakaat = "Prayer Prohibited",
      rakaatBreakdown = listOf(
        RakaatPart("0", "All prayers & prostrations prohibited"),
      ),
      timeRule = "When the sun reaches its exact zenith (meridian) — roughly 6 to 8 minutes immediately before Dhuhr.",
      optimalAndMakruh = "Wait for the sun to decline past the meridian; Dhuhr begins right after Zawal ends.",
      niyyahPronunciation = "Pause prayer for these few minutes until the Dhuhr window opens.",
      niyyahMeaning = "Prayer is avoided at exact solar noon per Prophetic guidance.",
      virtues = "Using this brief pause to make Wudu prepares the heart for Dhuhr.",
      specialAmal = "Renew Wudu, reply to the Adhan, and prepare for Dhuhr Sunnah.",
    ),
    PrayerType.DHUHR to PrayerDetailInfo(
      type = PrayerType.DHUHR,
      title = "Dhuhr & Jumu'ah Prayer",
      arabicTitle = "صلاة الظهر / الجمعة",
      categoryLabel = "2nd Obligatory (Fard) Prayer",
      totalRakaat = "Dhuhr 12 Rakaat • Jumu'ah 14 Rakaat",
      rakaatBreakdown = listOf(
        RakaatPart("4", "Sunnah Mu'akkadah (before Fard)"),
        RakaatPart("4 / 2", "Fard (4 for Dhuhr, 2 for Jumu'ah)"),
        RakaatPart("2 (+2)", "Sunnah Mu'akkadah after Fard"),
        RakaatPart("2", "Nafl"),
      ),
      timeRule = "Starts once the sun declines past the zenith and ends when Asr begins (1x or 2x shadow length depending on juristic school).",
      optimalAndMakruh = "Pray early in cool weather and slightly delayed during intense midday heat. On Fridays, Jumu'ah replaces Dhuhr in congregation.",
      niyyahPronunciation = "Intention in the heart: Nawaytu an usalliya lillahi ta'ala arba'a rak'ati salatiz-zuhri fardullahi ta'ala — Allahu Akbar.",
      niyyahMeaning = "I intend to pray four fard rakaat of Dhuhr for Allah, facing the Qibla.",
      virtues = "The Prophet (peace be upon him) said: Whoever regularly prays 4 rakaat before and 4 rakaat after Dhuhr, Allah forbids the Fire for them.",
      specialAmal = "On Fridays, recite Surah al-Kahf, send abundant Salawat on the Prophet, and seek the blessed hour of accepted dua before Maghrib.",
    ),
    PrayerType.ASR to PrayerDetailInfo(
      type = PrayerType.ASR,
      title = "Asr Prayer",
      arabicTitle = "صلاة العصر",
      categoryLabel = "3rd Obligatory (Al-Salat al-Wusta)",
      totalRakaat = "8 Rakaat Total",
      rakaatBreakdown = listOf(
        RakaatPart("4", "Sunnah Ghayr Mu'akkadah"),
        RakaatPart("4", "Fard (Silent Recitation)"),
      ),
      timeRule = "Begins when an object's shadow reaches its length (Standard) or twice its length (Hanafi) plus its noon shadow, and ends at sunset.",
      optimalAndMakruh = "Should be prayed before the sun turns pale/yellow (~15 minutes before sunset). Delaying until the sun turns red is disliked (Makruh).",
      niyyahPronunciation = "Intention in the heart: Nawaytu an usalliya lillahi ta'ala arba'a rak'ati salatil-asri fardullahi ta'ala — Allahu Akbar.",
      niyyahMeaning = "I intend to pray four fard rakaat of Asr for Allah, facing the Qibla.",
      virtues = "In Bukhari: 'Whoever misses the Asr prayer, it is as if they have lost their family and wealth.' Guarding Asr is guarding the Middle Prayer.",
      specialAmal = "No voluntary Nafl prayers are prayed after Asr; engage in evening Adhkar and Tasbih.",
    ),
    PrayerType.SUNSET_FORBIDDEN to PrayerDetailInfo(
      type = PrayerType.SUNSET_FORBIDDEN,
      title = "Pre-Sunset Forbidden Time",
      arabicTitle = "وقت اصفرار الشمس",
      categoryLabel = "Prohibited / Makruh Window",
      totalRakaat = "Nafl & Qaza Prohibited",
      rakaatBreakdown = listOf(
        RakaatPart("1 Exception", "Only today's unprayed Asr may be prayed"),
      ),
      timeRule = "Roughly the final 15 minutes before sunset as the sun turns orange/pale and sinks below the horizon.",
      optimalAndMakruh = "Avoid all voluntary (Nafl) and prior Qaza prayers. Only today's current Asr prayer may be performed if not yet prayed.",
      niyyahPronunciation = "Pause voluntary prayers and engage in silent Dua and Istighfar while awaiting Maghrib.",
      niyyahMeaning = "If fasting, this is a prized moment for accepted supplications before Iftar.",
      virtues = "The final moments before sunset are among the finest times for evening remembrance and repentance.",
      specialAmal = "Recite evening Adhkar and prepare to break fast / pray Maghrib promptly.",
    ),
    PrayerType.MAGHRIB to PrayerDetailInfo(
      type = PrayerType.MAGHRIB,
      title = "Maghrib Prayer",
      arabicTitle = "صلاة المغرب",
      categoryLabel = "4th Obligatory (Fard) Prayer",
      totalRakaat = "7 Rakaat Total",
      rakaatBreakdown = listOf(
        RakaatPart("3", "Fard (Audible in first 2)"),
        RakaatPart("2", "Sunnah Mu'akkadah"),
        RakaatPart("2", "Nafl"),
      ),
      timeRule = "Starts immediately after complete sunset and continues until twilight (red/white glow on the western horizon) disappears.",
      optimalAndMakruh = "Maghrib has the shortest time window; it is Sunnah to pray promptly right after the Adhan.",
      niyyahPronunciation = "Intention in the heart: Nawaytu an usalliya lillahi ta'ala thalatha rak'ati salatil-maghribi fardullahi ta'ala — Allahu Akbar.",
      niyyahMeaning = "I intend to pray three fard rakaat of Maghrib for Allah, facing the Qibla.",
      virtues = "Promptness in Maghrib and praying Awwabin afterward carries immense spiritual reward.",
      specialAmal = "Recite 'Allahumma ajirni minan-nar' seven times after Maghrib and evening Adhkar.",
    ),
    PrayerType.AWWABIN to PrayerDetailInfo(
      type = PrayerType.AWWABIN,
      title = "Salat al-Awwabin",
      arabicTitle = "صلاة الأوابين",
      categoryLabel = "Voluntary (Nafl) Prayer",
      totalRakaat = "6 Rakaat (3 sets of 2)",
      rakaatBreakdown = listOf(
        RakaatPart("2 + 2 + 2", "6 Rakaat Nafl after Maghrib Fard & Sunnah"),
      ),
      timeRule = "Between completing Maghrib Fard/Sunnah and the entry of Isha.",
      optimalAndMakruh = "Best prayed calmly right after Maghrib before engaging in worldly conversation.",
      niyyahPronunciation = "Intention in the heart: Nawaytu an usalliya lillahi ta'ala rak'atay salatil-awwabina naflan — Allahu Akbar.",
      niyyahMeaning = "I intend to pray two voluntary rakaat of Awwabin for the sake of Allah.",
      virtues = "Reported in Tirmidhi: Whoever prays six rakaat after Maghrib without speaking ill in between receives reward equivalent to twelve years of worship.",
      specialAmal = "Recite short Surahs peacefully with reflection in each rakaat.",
    ),
    PrayerType.ISHA to PrayerDetailInfo(
      type = PrayerType.ISHA,
      title = "Isha & Witr Prayer",
      arabicTitle = "صلاة العشاء والوتر",
      categoryLabel = "5th Obligatory (Fard) Prayer",
      totalRakaat = "15 Rakaat Total (incl. Sunnah & Witr)",
      rakaatBreakdown = listOf(
        RakaatPart("4", "Sunnah Ghayr Mu'akkadah"),
        RakaatPart("4", "Fard (Audible in first 2)"),
        RakaatPart("2", "Sunnah Mu'akkadah"),
        RakaatPart("2", "Nafl"),
        RakaatPart("3", "Witr"),
      ),
      timeRule = "From the disappearance of evening twilight until just before true dawn (Fajr).",
      optimalAndMakruh = "Recommended to pray within the first third or before Islamic midnight; delaying past midnight without excuse is discouraged.",
      niyyahPronunciation = "Intention in the heart: Nawaytu an usalliya lillahi ta'ala arba'a rak'ati salatil-isha'i fardullahi ta'ala — Allahu Akbar.",
      niyyahMeaning = "I intend to pray four fard rakaat of Isha for Allah, facing the Qibla.",
      virtues = "In Sahih Muslim: Praying Isha in congregation is like praying half the night; praying both Isha and Fajr in congregation is like praying the entire night.",
      specialAmal = "Recite Surah al-Mulk, Ayat al-Kursi, and the last two verses of Surah al-Baqarah before sleeping.",
    ),
    PrayerType.WITR to PrayerDetailInfo(
      type = PrayerType.WITR,
      title = "Salat al-Witr",
      arabicTitle = "صلاة الوتر",
      categoryLabel = "Wajib / Emphasized Sunnah Prayer",
      totalRakaat = "1 or 3 Rakaat (Odd Number)",
      rakaatBreakdown = listOf(
        RakaatPart("3", "Rakaat with Dua Qunoot in the 3rd rakaat"),
      ),
      timeRule = "Any time after praying Isha until true dawn (Fajr).",
      optimalAndMakruh = "If confident of waking for Tahajjud, delay Witr to be the very last prayer of the night; otherwise pray Witr before going to sleep.",
      niyyahPronunciation = "Intention in the heart: Nawaytu an usalliya lillahi ta'ala thalatha rak'ati salatil-witri — Allahu Akbar.",
      niyyahMeaning = "I intend to pray Witr prayer for the sake of Allah, facing the Qibla.",
      virtues = "Allah is Witr (One/Odd) and loves the odd-numbered Witr prayer. The Prophet (peace be upon him) never left Witr even while traveling.",
      specialAmal = "Recite Dua Qunoot in the final rakaat; if still memorizing it, recite 'Rabbana atina fid-dunya hasanah...' or 'Allahummaghfir li'.",
    ),
  )

  val guideTopics: List<GuideTopic> = listOf(
    GuideTopic(
      id = "wudu",
      title = "Wudu (Ablution) A-to-Z Rules",
      subtitle = "4 Fard steps, Sunnah practices & 7 nullifiers of Wudu",
      badge = "Purification",
      sections = listOf(
        GuideSection(
          heading = "4 Obligatory (Fard) Pillars of Wudu",
          points = listOf(
            "1. Washing the entire face (from hairline to under the chin, and earlobe to earlobe).",
            "2. Washing both arms thoroughly including the elbows.",
            "3. Wiping (Masah) at least one-fourth of the head with wet hands.",
            "4. Washing both feet including the ankles.",
          ),
        ),
        GuideSection(
          heading = "Key Sunnah Practices of Wudu",
          points = listOf(
            "Making the intention in the heart and saying 'Bismillah'.",
            "Washing both hands up to the wrists 3 times and using a Miswak.",
            "Rinsing the mouth and sniffing water into the nose gently 3 times.",
            "Washing each limb 3 times, starting with the right side, and interlacing fingers and toes.",
          ),
        ),
        GuideSection(
          heading = "7 Common Nullifiers of Wudu",
          points = listOf(
            "1. Any discharge from the front or back private passages (urine, stool, or passing wind).",
            "2. Flowing blood or pus that spreads beyond the wound site (in Hanafi fiqh).",
            "3. Mouthful of vomiting.",
            "4. Deep sleep while lying down or leaning heavily so consciousness is lost.",
            "5. Fainting, loss of consciousness, or intoxication.",
            "6. Loud laughter during a prayer containing Ruku and Sujood.",
          ),
        ),
      ),
    ),
    GuideTopic(
      id = "fard_13",
      title = "13 Pillars & Conditions of Salah",
      subtitle = "7 external preconditions (Shurut) & 6 internal pillars (Arkan)",
      badge = "Essentials",
      sections = listOf(
        GuideSection(
          heading = "7 External Preconditions (Before Starting Salah)",
          points = listOf(
            "1. Purity of the body (free from major & minor impurities).",
            "2. Purity of clothing.",
            "3. Purity of the place of prayer.",
            "4. Covering the Awrah (navel to below knees for men; entire body except face, hands, and feet for women).",
            "5. Facing the Qibla (direction of the Kaaba in Makkah).",
            "6. Entering of the valid prayer time.",
            "7. Sincere intention (Niyyah) in the heart.",
          ),
        ),
        GuideSection(
          heading = "6 Internal Pillars (Inside Salah)",
          points = listOf(
            "1. Opening Takbir (Takbir al-Tahrimah: saying 'Allahu Akbar').",
            "2. Standing (Qiyam) for those physically able.",
            "3. Recitation (Qira'ah) from the Holy Quran (Surah al-Fatihah).",
            "4. Bowing (Ruku) with calmness.",
            "5. Two prostrations (Sujood) in every rakaat.",
            "6. Final sitting (Qa'dah Akhirah) long enough to recite Tashahhud.",
          ),
        ),
      ),
    ),
    GuideTopic(
      id = "wajib_14",
      title = "Wajib Acts & Sujood as-Sahw",
      subtitle = "Required elements corrected by Forgetfulness Prostration",
      badge = "Precision",
      sections = listOf(
        GuideSection(
          heading = "Key Wajib Elements in Salah",
          points = listOf(
            "1. Reciting Surah al-Fatihah completely in every rakaat.",
            "2. Adding another Surah (or 3 short verses) after Fatihah in the first 2 fard rakaat and all Sunnah/Witr rakaat.",
            "3. Maintaining proper order (Tartib) and calmness/stillness (Ta'dil al-Arkan) in Ruku and Sujood.",
            "4. First sitting after 2 rakaat in 3- or 4-rakaat prayers and reciting Tashahhud.",
            "5. Audible recitation in Fajr, Maghrib, and Isha (first 2 rakaat) when leading; silent in Dhuhr & Asr.",
            "6. Reciting Dua Qunoot in Witr and ending prayer with 'As-salamu alaykum wa rahmatullah'.",
          ),
        ),
        GuideSection(
          heading = "How to Perform Sujood as-Sahw (Prostration of Forgetfulness)",
          points = listOf(
            "If a Wajib act is omitted or delayed unintentionally, recite Tashahhud in the final sitting, perform two extra prostrations (either before Salam or after one Salam to the right), then finish with Tashahhud, Durood, Dua, and Salam.",
          ),
        ),
      ),
    ),
    GuideTopic(
      id = "breakers_19",
      title = "Actions That Invalidate Salah",
      subtitle = "Common mistakes to avoid while standing in prayer",
      badge = "Avoid",
      sections = listOf(
        GuideSection(
          heading = "Nullifiers of Prayer",
          points = listOf(
            "1. Speaking intentionally or unintentionally during prayer.",
            "2. Greeting someone or replying to a greeting ('Wa alaykumus-salam').",
            "3. Eating or drinking anything while praying.",
            "4. Turning the chest completely away from the Qibla direction.",
            "5. Excessive unrelated physical movement (Amal Kathir).",
            "6. Laughing aloud or losing Wudu during prayer.",
            "7. Major recitation errors that invert Quranic meaning.",
            "8. A follower intentionally completing a pillar before the Imam.",
          ),
        ),
      ),
    ),
    GuideTopic(
      id = "forbidden_3",
      title = "3 Forbidden & Makruh Prayer Times",
      subtitle = "Exact periods when prayer is prohibited worldwide",
      badge = "Timing Rules",
      sections = listOf(
        GuideSection(
          heading = "3 Strictly Forbidden Windows",
          points = listOf(
            "1. Sunrise: From the moment the sun begins rising until ~15 minutes later.",
            "2. Exact Solar Noon (Zawal): ~6–8 minutes right before Dhuhr when the sun is directly overhead.",
            "3. Pre-Sunset: ~15 minutes right before sunset as the sun dims. Only today's unprayed Asr may be offered before sunset.",
          ),
        ),
        GuideSection(
          heading = "When Only Voluntary (Nafl) Prayer is Disliked",
          points = listOf(
            "After praying Fajr until sunrise.",
            "After praying Asr until sunset.",
            "While the Friday Khutbah is in progress or after the Iqamah for Fard has begun.",
          ),
        ),
      ),
    ),
    GuideTopic(
      id = "qaza_qasr",
      title = "Qaza (Makeup) & Traveler's Qasr",
      subtitle = "Making up missed prayers & shortening prayers while traveling",
      badge = "Travel & Qaza",
      sections = listOf(
        GuideSection(
          heading = "Rules for Qaza (Missed Prayers)",
          points = listOf(
            "Only the 5 daily Fard prayers (2+4+4+3+4 = 17 rakaat) and 3 Witr are made up as Qaza.",
            "Specify the exact missed prayer in your heart (e.g., 'the earliest missed Dhuhr Fard') before beginning.",
          ),
        ),
        GuideSection(
          heading = "Traveler's Qasr (Shortened Prayer)",
          points = listOf(
            "When traveling ~48 miles (~77.25 km) or more with the intention to stay less than 15 days at the destination, a traveler shortens 4-rakaat Fard prayers (Dhuhr, Asr, Isha) to 2 rakaat.",
            "Fajr (2), Maghrib (3), and Witr (3) remain unchanged.",
          ),
        ),
      ),
    ),
  )
}
