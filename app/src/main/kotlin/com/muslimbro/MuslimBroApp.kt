package com.muslimbro.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.muslimbro.feature.widget.WidgetUpdateWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MuslimBroApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Pre-load Quran font in background
        scheduleBackgroundTasks()
    }

    private fun scheduleBackgroundTasks() {
        WidgetUpdateWorker.schedule(this)
    }
}
