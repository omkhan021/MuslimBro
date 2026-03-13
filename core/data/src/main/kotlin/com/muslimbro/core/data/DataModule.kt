package com.muslimbro.core.data

import com.muslimbro.core.common.DefaultDispatcherProvider
import com.muslimbro.core.common.DispatcherProvider
import com.muslimbro.core.data.repository.LocationRepositoryImpl
import com.muslimbro.core.data.repository.PrayerTimesRepositoryImpl
import com.muslimbro.core.data.repository.QuranRepositoryImpl
import com.muslimbro.core.data.repository.SettingsRepositoryImpl
import com.muslimbro.core.domain.repository.LocationRepository
import com.muslimbro.core.domain.repository.PrayerTimesRepository
import com.muslimbro.core.domain.repository.QuranRepository
import com.muslimbro.core.domain.repository.SettingsRepository
import com.muslimbro.core.domain.usecase.GetPrayerTimesUseCase
import com.muslimbro.core.domain.usecase.GetQiblaDirectionUseCase
import com.muslimbro.core.domain.usecase.SearchQuranUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindPrayerTimesRepository(impl: PrayerTimesRepositoryImpl): PrayerTimesRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindQuranRepository(impl: QuranRepositoryImpl): QuranRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    @Provides
    fun provideGetPrayerTimesUseCase(
        prayerTimesRepository: PrayerTimesRepository,
        locationRepository: LocationRepository,
        settingsRepository: SettingsRepository
    ) = GetPrayerTimesUseCase(prayerTimesRepository, locationRepository, settingsRepository)

    @Provides
    fun provideSearchQuranUseCase(
        quranRepository: QuranRepository
    ) = SearchQuranUseCase(quranRepository)

    @Provides
    fun provideGetQiblaDirectionUseCase(
        locationRepository: LocationRepository
    ) = GetQiblaDirectionUseCase(locationRepository)
}
