package com.muslimbro.core.data.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMuslimBroDatabase(@ApplicationContext context: Context): MuslimBroDatabase =
        Room.databaseBuilder(context, MuslimBroDatabase::class.java, MuslimBroDatabase.DATABASE_NAME)
            .createFromAsset("quran_data.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePrayerTimesDao(db: MuslimBroDatabase) = db.prayerTimesDao()

    @Provides
    fun provideQuranDao(db: MuslimBroDatabase) = db.quranDao()

    @Provides
    fun provideSearchDao(db: MuslimBroDatabase) = db.searchDao()
}
