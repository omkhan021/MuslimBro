package com.muslimbro.feature.alarms.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.muslimbro.feature.alarms.worker.RescheduleAlarmsWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED -> {
                // Enqueue reschedule via WorkManager — safe to call from BroadcastReceiver
                val work = OneTimeWorkRequestBuilder<RescheduleAlarmsWorker>().build()
                WorkManager.getInstance(context).enqueue(work)
            }
        }
    }
}
