package com.muslimbro.feature.qibla

import android.content.Context
import android.hardware.SensorManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object QiblaModule {
    @Provides
    fun provideSensorManager(@ApplicationContext context: Context): SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
}
