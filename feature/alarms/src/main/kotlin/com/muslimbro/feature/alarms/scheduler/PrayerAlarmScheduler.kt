package com.muslimbro.feature.alarms.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.muslimbro.core.domain.model.Prayer
import com.muslimbro.core.domain.model.PrayerTimes
import com.muslimbro.feature.alarms.receiver.PrayerAlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedule alarms for all prayers using AlarmManager.setAlarmClock().
     * This method is NEVER deferred by Doze mode — critical for prayer times.
     * USE_EXACT_ALARM permission is auto-granted on API 33+; no user action needed.
     * On API 26-32, setAlarmClock() requires no special permission.
     */
    fun schedulePrayerAlarms(prayerTimes: PrayerTimes, notificationsEnabled: Map<Prayer, Boolean>) {
        val zoneId = ZoneId.systemDefault()
        val prayers = listOf(
            Prayer.FAJR to prayerTimes.fajr,
            Prayer.DHUHR to prayerTimes.dhuhr,
            Prayer.ASR to prayerTimes.asr,
            Prayer.MAGHRIB to prayerTimes.maghrib,
            Prayer.ISHA to prayerTimes.isha
        )

        prayers.forEach { (prayer, time) ->
            if (notificationsEnabled[prayer] != false) {
                val triggerMillis = ZonedDateTime.of(
                    prayerTimes.date, time, zoneId
                ).toInstant().toEpochMilli()

                if (triggerMillis > System.currentTimeMillis()) {
                    scheduleAlarm(prayer, prayerTimes.date.toString(), triggerMillis)
                }
            }
        }
    }

    private fun scheduleAlarm(prayer: Prayer, date: String, triggerMillis: Long) {
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_PRAYER_ALARM
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, prayer.name)
            putExtra(PrayerAlarmReceiver.EXTRA_DATE, date)
        }

        val requestCode = prayer.ordinal + (date.hashCode() * 10)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // setAlarmClock() is the correct API — guaranteed to fire even in Doze mode
        // Never use setExact() or setExactAndAllowWhileIdle() which can be deferred
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerMillis, pendingIntent),
            pendingIntent
        )
    }

    fun cancelAlarm(prayer: Prayer, date: String) {
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_PRAYER_ALARM
        }
        val requestCode = prayer.ordinal + (date.hashCode() * 10)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }

    fun cancelAllAlarms() {
        Prayer.values().filter { it.isAlarmable }.forEach { prayer ->
            // Cancel for today and next 7 days
            val today = java.time.LocalDate.now()
            (0..6).forEach { offset ->
                cancelAlarm(prayer, today.plusDays(offset.toLong()).toString())
            }
        }
    }
}
