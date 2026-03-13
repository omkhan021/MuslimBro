package com.muslimbro.core.common

import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class HijriDate(
    val day: Int,
    val month: Int,
    val year: Int,
    val monthName: String
)

private val hijriMonthNames = listOf(
    "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
    "Jumada al-Ula", "Jumada al-Akhirah", "Rajab", "Sha'ban",
    "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
)

fun LocalDate.toHijriDate(): HijriDate {
    val hijrah = HijrahDate.from(this)
    val month = hijrah.get(java.time.temporal.ChronoField.MONTH_OF_YEAR)
    val day = hijrah.get(java.time.temporal.ChronoField.DAY_OF_MONTH)
    val year = hijrah.get(java.time.temporal.ChronoField.YEAR)
    return HijriDate(
        day = day,
        month = month,
        year = year,
        monthName = hijriMonthNames[month - 1]
    )
}

fun HijriDate.format(): String = "$day ${monthName} $year AH"

fun LocalDate.formatDisplay(): String =
    format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault()))

fun java.util.Date.toLocalTime(zoneId: java.time.ZoneId = java.time.ZoneId.systemDefault()): java.time.LocalTime =
    toInstant().atZone(zoneId).toLocalTime()

fun java.util.Date.toLocalDateTime(zoneId: java.time.ZoneId = java.time.ZoneId.systemDefault()): java.time.LocalDateTime =
    toInstant().atZone(zoneId).toLocalDateTime()
