package com.example.notification

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.domain.localization.AppStrings
import com.example.domain.localization.englishStrings
import com.example.domain.model.DailySchedule
import com.example.domain.model.PrayerType
import com.example.domain.prayer.BanglaDateFormatter

class PrayerAlarmReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val title = intent.getStringExtra(EXTRA_TITLE) ?: "Salatul Waqt"
    val body = intent.getStringExtra(EXTRA_BODY) ?: "Prayer time has begun"
    val notifId = intent.getIntExtra(EXTRA_NOTIF_ID, 1001)
    PrayerNotificationHelper.showNotification(context, notifId, title, body)
  }

  companion object {
    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_BODY = "extra_body"
    const val EXTRA_NOTIF_ID = "extra_notif_id"
  }
}

object PrayerNotificationHelper {
  private const val CHANNEL_ID = "salatul_waqt_reminders"

  fun ensureChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      val existing = manager.getNotificationChannel(CHANNEL_ID)
      if (existing == null) {
        val channel = NotificationChannel(
          CHANNEL_ID,
          context.getString(R.string.notification_channel_name),
          NotificationManager.IMPORTANCE_HIGH,
        ).apply {
          description = context.getString(R.string.notification_channel_desc)
          enableVibration(true)
        }
        manager.createNotificationChannel(channel)
      }
    }
  }

  fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS,
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }

  fun showNotification(context: Context, id: Int, title: String, body: String) {
    if (!hasNotificationPermission(context)) return
    ensureChannel(context)

    val tapIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val contentPendingIntent = PendingIntent.getActivity(
      context,
      0,
      tapIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle(title)
      .setContentText(body)
      .setStyle(NotificationCompat.BigTextStyle().bigText(body))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setContentIntent(contentPendingIntent)
      .build()

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(id, notification)
  }

  fun scheduleTodayReminders(
    context: Context,
    schedule: DailySchedule,
    enabledTypes: Set<PrayerType>,
    locationName: String,
    use24HourFormat: Boolean,
    timeZoneId: String,
    strings: AppStrings = englishStrings,
  ) {
    ensureChannel(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val now = System.currentTimeMillis()

    for (window in schedule.windows) {
      if (!window.isReminderSupported) continue
      val requestCode = window.type.ordinal + 500
      val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
        putExtra(PrayerAlarmReceiver.EXTRA_NOTIF_ID, requestCode)
        putExtra(PrayerAlarmReceiver.EXTRA_TITLE, "${window.title} ${strings.notifTimeSuffix}")
        val timeText = BanglaDateFormatter.formatClockWithPeriod(
          window.startMillis,
          use24HourFormat,
          timeZoneId,
          strings,
        )
        putExtra(
          PrayerAlarmReceiver.EXTRA_BODY,
          "$locationName: ${window.title} ${strings.notifStartedAtWord} $timeText (${window.rakaatSummary}).",
        )
      }
      val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )

      alarmManager.cancel(pendingIntent)
      if (enabledTypes.contains(window.type) && window.startMillis > now) {
        alarmManager.setAndAllowWhileIdle(
          AlarmManager.RTC_WAKEUP,
          window.startMillis,
          pendingIntent,
        )
      }
    }
  }
}
