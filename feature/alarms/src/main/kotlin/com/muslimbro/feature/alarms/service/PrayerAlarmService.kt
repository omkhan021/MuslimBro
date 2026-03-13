package com.muslimbro.feature.alarms.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.exoplayer.ExoPlayer
import com.muslimbro.core.domain.model.Prayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PrayerAlarmService : Service() {

    @Inject
    lateinit var adhanAudioPlayer: AdhanAudioPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prayerName = intent?.getStringExtra(EXTRA_PRAYER_NAME) ?: return START_NOT_STICKY
        val prayer = runCatching { Prayer.valueOf(prayerName) }.getOrNull()
            ?: return START_NOT_STICKY

        val notification = buildNotification(prayer)
        startForeground(NOTIFICATION_ID, notification)

        adhanAudioPlayer.playAdhan(
            prayer = prayer,
            onComplete = { stopSelf() }
        )

        return START_NOT_STICKY
    }

    private fun buildNotification(prayer: Prayer): Notification {
        createNotificationChannel()

        val dismissIntent = Intent(this, PrayerAlarmService::class.java).apply {
            action = ACTION_DISMISS
        }
        val dismissPendingIntent = PendingIntent.getService(
            this, 0, dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(this, PrayerAlarmService::class.java).apply {
            action = ACTION_SNOOZE
        }
        val snoozePendingIntent = PendingIntent.getService(
            this, 1, snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("${prayer.displayName()} • ${prayer.arabicName()}")
            .setContentText("Time for ${prayer.displayName()} prayer")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dismiss", dismissPendingIntent)
            .addAction(android.R.drawable.ic_menu_recent_history, "Snooze 5min", snoozePendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Prayer Alarms",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Adhan notifications for prayer times"
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        adhanAudioPlayer.release()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.muslimbro.ACTION_START_ALARM"
        const val ACTION_DISMISS = "com.muslimbro.ACTION_DISMISS_ALARM"
        const val ACTION_SNOOZE = "com.muslimbro.ACTION_SNOOZE_ALARM"
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
        const val EXTRA_DATE = "extra_date"
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "prayer_alarm_channel"
    }
}
