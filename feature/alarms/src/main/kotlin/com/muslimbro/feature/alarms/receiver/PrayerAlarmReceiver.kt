package com.muslimbro.feature.alarms.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.muslimbro.feature.alarms.service.PrayerAlarmService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrayerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_PRAYER_ALARM -> {
                val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: return
                val date = intent.getStringExtra(EXTRA_DATE) ?: return

                val serviceIntent = Intent(context, PrayerAlarmService::class.java).apply {
                    action = PrayerAlarmService.ACTION_START
                    putExtra(PrayerAlarmService.EXTRA_PRAYER_NAME, prayerName)
                    putExtra(PrayerAlarmService.EXTRA_DATE, date)
                }
                // Must use startForegroundService — system requires foreground within 5 seconds
                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }

    companion object {
        const val ACTION_PRAYER_ALARM = "com.muslimbro.ACTION_PRAYER_ALARM"
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
        const val EXTRA_DATE = "extra_date"
    }
}
