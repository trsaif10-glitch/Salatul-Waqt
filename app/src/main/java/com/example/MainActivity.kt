package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.localization.AppLocalizations
import com.example.domain.localization.LocalAppStrings
import com.example.domain.model.PrayerWindow
import com.example.notification.PrayerNotificationHelper
import com.example.ui.components.LanguagePickerSheet
import com.example.ui.components.LocationPickerSheet
import com.example.ui.components.PrayerDetailSheet
import com.example.ui.screens.AllPrayersScreen
import com.example.ui.screens.MonthlyScheduleScreen
import com.example.ui.screens.QiblaScreen
import com.example.ui.screens.SehriIftarScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TasbihScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.SalatulWaqtTheme
import com.example.ui.viewmodel.SalatulWaqtViewModel

enum class AppDestination {
  HOME,
  PRAYER_SCHEDULE,
  QIBLA,
  SEHRI_IFTAR,
  TASBIH,
  HIJRI_CALENDAR,
  SETTINGS,
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.light(
        Color.White.toArgb(),
        Color.White.toArgb(),
      ),
      navigationBarStyle = SystemBarStyle.light(
        Color.White.toArgb(),
        Color.White.toArgb(),
      ),
    )
    PrayerNotificationHelper.ensureChannel(this)

    setContent {
      val viewModel: SalatulWaqtViewModel = viewModel()
      val state by viewModel.uiState.collectAsStateWithLifecycle()

      SalatulWaqtTheme(themeMode = state.preferences.themeMode) {
        SalatulWaqtApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun SalatulWaqtApp(viewModel: SalatulWaqtViewModel) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  val strings = remember(state.preferences.language) {
    AppLocalizations.forLanguage(state.preferences.language)
  }
  val layoutDirection = if (state.preferences.language.isRtl) {
    LayoutDirection.Rtl
  } else {
    LayoutDirection.Ltr
  }

  var destination by remember { mutableStateOf(AppDestination.HOME) }
  var selectedPrayerDetail by remember { mutableStateOf<PrayerWindow?>(null) }
  var showLocationPicker by remember { mutableStateOf(false) }
  var showLanguagePicker by remember { mutableStateOf(false) }
  var hasPromptedAutoLocationThisSession by remember { mutableStateOf(false) }

  var hasNotifPermission by remember {
    mutableStateOf(PrayerNotificationHelper.hasNotificationPermission(context))
  }

  val locationPermLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
  ) { granted ->
    if (granted) {
      viewModel.detectLocationFromGps(enableAutoMode = true)
    } else {
      viewModel.updateAutoDetectLocation(false)
      viewModel.showStatusMessage(strings.locationPermDeniedMsg)
    }
  }

  val notifPermLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
  ) { granted ->
    hasNotifPermission = granted
    if (granted) {
      viewModel.showStatusMessage(strings.notifEnabledMsg)
    } else {
      viewModel.showStatusMessage(strings.notifDeniedMsg)
    }
  }

  fun hasLocationPermission(): Boolean = ContextCompat.checkSelfPermission(
    context,
    Manifest.permission.ACCESS_COARSE_LOCATION,
  ) == PackageManager.PERMISSION_GRANTED

  fun requestGpsDetection() {
    if (hasLocationPermission()) {
      viewModel.detectLocationFromGps(enableAutoMode = true)
    } else {
      locationPermLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }
  }

  fun handleToggleAutoDetectLocation(enabled: Boolean) {
    if (enabled) {
      if (hasLocationPermission()) {
        viewModel.updateAutoDetectLocation(true)
        viewModel.detectLocationFromGps(enableAutoMode = true)
      } else {
        locationPermLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
      }
    } else {
      viewModel.updateAutoDetectLocation(false)
    }
  }

  val lifecycleOwner = LocalLifecycleOwner.current
  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        viewModel.maybeAutoRefreshOnResume(hasLocationPermission())
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
  }

  LaunchedEffect(state.isPreferencesLoaded) {
    if (state.isPreferencesLoaded && state.preferences.autoDetectLocation && !hasPromptedAutoLocationThisSession) {
      hasPromptedAutoLocationThisSession = true
      if (hasLocationPermission()) {
        viewModel.maybeAutoRefreshOnResume(true)
      } else {
        locationPermLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
      }
    }
  }

  fun requestNotifPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    } else {
      hasNotifPermission = true
    }
  }

  LaunchedEffect(state.statusMessage) {
    val msg = state.statusMessage
    if (msg != null) {
      snackbarHostState.showSnackbar(msg)
      viewModel.clearStatusMessage()
    }
  }

  CompositionLocalProvider(
    LocalAppStrings provides strings,
    LocalLayoutDirection provides layoutDirection,
  ) {
    BackHandler(
      enabled = selectedPrayerDetail != null || showLocationPicker || showLanguagePicker,
    ) {
      when {
        selectedPrayerDetail != null -> selectedPrayerDetail = null
        showLocationPicker -> showLocationPicker = false
        showLanguagePicker -> showLanguagePicker = false
      }
    }

    when (destination) {
      AppDestination.HOME -> {
        Scaffold(
          containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
          contentWindowInsets = WindowInsets.safeDrawing,
          snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding),
          ) {
            TodayScreen(
              state = state,
              activeWaqtFlow = viewModel.activeWaqtFlow,
              nowMillisFlow = viewModel.nowMillisFlow,
              onOpenLocationPicker = { showLocationPicker = true },
              onOpenLanguagePicker = { showLanguagePicker = true },
              onQuickDetectGps = ::requestGpsDetection,
              onOpenSettings = { destination = AppDestination.SETTINGS },
              onOpenPrayerSchedule = { destination = AppDestination.PRAYER_SCHEDULE },
              onOpenQibla = { destination = AppDestination.QIBLA },
              onOpenSehriIftar = { destination = AppDestination.SEHRI_IFTAR },
              onOpenTasbih = { destination = AppDestination.TASBIH },
              onOpenHijriCalendar = { destination = AppDestination.HIJRI_CALENDAR },
            )
          }
        }
      }

      AppDestination.PRAYER_SCHEDULE -> AllPrayersScreen(
        state = state,
        onBack = { destination = AppDestination.HOME },
        onOpenPrayerDetail = { selectedPrayerDetail = it },
        onToggleReminder = { type ->
          if (!hasNotifPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotifPermission()
          }
          viewModel.toggleReminder(type)
        },
      )

      AppDestination.QIBLA -> QiblaScreen(
        state = state,
        onStartCompass = viewModel::startCompass,
        onStopCompass = viewModel::stopCompass,
        onBack = { destination = AppDestination.HOME },
      )

      AppDestination.SEHRI_IFTAR -> {
        val monthlyState by viewModel.monthlyStateFlow.collectAsStateWithLifecycle()
        SehriIftarScreen(
          state = state,
          monthlyState = monthlyState,
          onBack = { destination = AppDestination.HOME },
        )
      }

      AppDestination.TASBIH -> TasbihScreen(
        state = state,
        onIncrement = viewModel::incrementTasbih,
        onReset = viewModel::resetTasbih,
        onSelectPreset = viewModel::selectTasbihPreset,
        onBack = { destination = AppDestination.HOME },
      )

      AppDestination.HIJRI_CALENDAR -> {
        val monthlyState by viewModel.monthlyStateFlow.collectAsStateWithLifecycle()
        MonthlyScheduleScreen(
          state = state,
          monthlyState = monthlyState,
          onPrevMonth = { viewModel.shiftMonth(-1) },
          onNextMonth = { viewModel.shiftMonth(1) },
          onResetToToday = viewModel::resetMonthToToday,
          onBack = { destination = AppDestination.HOME },
        )
      }

      AppDestination.SETTINGS -> SettingsScreen(
        state = state,
        hasNotificationPermission = hasNotifPermission,
        onOpenLanguagePicker = { showLanguagePicker = true },
        onOpenLocationPicker = { showLocationPicker = true },
        onDetectGpsLocation = ::requestGpsDetection,
        onToggleAutoDetectLocation = ::handleToggleAutoDetectLocation,
        onSelectAsrJuristic = viewModel::updateAsrJuristic,
        onSelectCalculationMethod = viewModel::updateCalculationMethod,
        onUpdateOffsets = viewModel::updateOffsets,
        onUpdateHijriOffset = viewModel::updateHijriOffset,
        onToggle24HourFormat = viewModel::update24HourFormat,
        onSelectThemeMode = viewModel::updateThemeMode,
        onRequestNotificationPermission = ::requestNotifPermission,
        onSendTestNotification = {
          PrayerNotificationHelper.showNotification(
            context = context,
            id = 999,
            title = strings.testNotifTitle,
            body = "${state.preferences.location.name}: ${strings.testNotifBodySuffix}",
          )
          viewModel.showStatusMessage(strings.testNotifSentMsg)
        },
        onBack = { destination = AppDestination.HOME },
      )
    }

    selectedPrayerDetail?.let { window ->
      PrayerDetailSheet(
        window = window,
        isReminderEnabled = state.preferences.enabledReminders.contains(window.type),
        use24HourFormat = state.preferences.use24HourFormat,
        timeZoneId = state.preferences.location.timeZoneId,
        onToggleReminder = {
          if (!hasNotifPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotifPermission()
          }
          viewModel.toggleReminder(window.type)
        },
        onDismiss = { selectedPrayerDetail = null },
      )
    }

    if (showLocationPicker) {
      LocationPickerSheet(
        currentLocation = state.preferences.location,
        autoDetectLocation = state.preferences.autoDetectLocation,
        isDetectingGps = state.isDetectingLocation,
        onToggleAutoDetect = ::handleToggleAutoDetectLocation,
        onRequestGps = ::requestGpsDetection,
        onSelectLocation = viewModel::selectLocation,
        onDismiss = { showLocationPicker = false },
      )
    }

    if (showLanguagePicker) {
      LanguagePickerSheet(
        currentLanguage = state.preferences.language,
        onSelectLanguage = viewModel::updateLanguage,
        onDismiss = { showLanguagePicker = false },
      )
    }
  }
}
