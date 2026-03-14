package com.muslimbro.core.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class PrayerTimes(
    val date: LocalDate,
    val fajr: LocalTime,
    val sunrise: LocalTime,
    val dhuhr: LocalTime,
    val asr: LocalTime,
    val maghrib: LocalTime,
    val isha: LocalTime,
    val latitude: Double,
    val longitude: Double,
    val calculationMethod: CalculationMethod,
    val madhab: Madhab
)

data class NextPrayer(
    val prayer: Prayer,
    val time: LocalTime,
    val remainingSeconds: Long
)

enum class Prayer {
    FAJR, SUNRISE, DHUHR, ASR, MAGHRIB, ISHA;

    fun displayName(): String = when (this) {
        FAJR -> "Fajr"
        SUNRISE -> "Sunrise"
        DHUHR -> "Dhuhr"
        ASR -> "Asr"
        MAGHRIB -> "Maghrib"
        ISHA -> "Isha"
    }

    fun arabicName(): String = when (this) {
        FAJR -> "الفجر"
        SUNRISE -> "الشروق"
        DHUHR -> "الظهر"
        ASR -> "العصر"
        MAGHRIB -> "المغرب"
        ISHA -> "العشاء"
    }

    val isAlarmable: Boolean get() = this != SUNRISE
}

enum class CalculationMethod {
    MUSLIM_WORLD_LEAGUE,
    EGYPTIAN,
    KARACHI,
    UMM_AL_QURA,
    DUBAI,
    MOON_SIGHTING_COMMITTEE,
    NORTH_AMERICA,
    KUWAIT,
    QATAR,
    SINGAPORE,
    TEHRAN,
    TURKEY;

    fun displayName(): String = when (this) {
        MUSLIM_WORLD_LEAGUE -> "Muslim World League"
        EGYPTIAN -> "Egyptian General Authority"
        KARACHI -> "University of Islamic Sciences, Karachi"
        UMM_AL_QURA -> "Umm al-Qura University, Makkah"
        DUBAI -> "Dubai"
        MOON_SIGHTING_COMMITTEE -> "Moonsighting Committee"
        NORTH_AMERICA -> "Islamic Society of North America (ISNA)"
        KUWAIT -> "Kuwait"
        QATAR -> "Qatar"
        SINGAPORE -> "Majlis Ugama Islam Singapura"
        TEHRAN -> "University of Tehran"
        TURKEY -> "Diyanet İşleri Başkanlığı"
    }
}

enum class Madhab {
    SHAFI, HANAFI;

    fun displayName(): String = when (this) {
        SHAFI -> "Shafi'i / Maliki / Hanbali"
        HANAFI -> "Hanafi"
    }
}
