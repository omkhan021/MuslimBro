package com.muslimbro.core.domain.model

import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.PrayerAdjustments
import com.muslimbro.core.domain.model.CalculationMethod.*
import com.batoulapps.adhan2.Madhab as AdhanMadhab

fun CalculationMethod.toAdhanParameters(): CalculationParameters = when (this) {
    MUSLIM_WORLD_LEAGUE ->
        com.batoulapps.adhan2.CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
    EGYPTIAN ->
        com.batoulapps.adhan2.CalculationMethod.EGYPTIAN.parameters
    KARACHI ->
        com.batoulapps.adhan2.CalculationMethod.KARACHI.parameters
    UMM_AL_QURA ->
        com.batoulapps.adhan2.CalculationMethod.UMM_AL_QURA.parameters
    DUBAI ->
        com.batoulapps.adhan2.CalculationMethod.DUBAI.parameters
    MOON_SIGHTING_COMMITTEE ->
        com.batoulapps.adhan2.CalculationMethod.MOON_SIGHTING_COMMITTEE.parameters
    NORTH_AMERICA ->
        com.batoulapps.adhan2.CalculationMethod.NORTH_AMERICA.parameters
    KUWAIT ->
        com.batoulapps.adhan2.CalculationMethod.KUWAIT.parameters
    QATAR ->
        com.batoulapps.adhan2.CalculationMethod.QATAR.parameters
    SINGAPORE ->
        com.batoulapps.adhan2.CalculationMethod.SINGAPORE.parameters
    TEHRAN ->
        CalculationParameters(
            fajrAngle = 17.7,
            ishaAngle = 14.0,
            method = com.batoulapps.adhan2.CalculationMethod.OTHER,
            methodAdjustments = PrayerAdjustments(dhuhr = 5, asr = 5, maghrib = 5)
        )
    TURKEY ->
        com.batoulapps.adhan2.CalculationMethod.TURKEY.parameters
}

fun Madhab.toAdhanMadhab(): AdhanMadhab = when (this) {
    Madhab.SHAFI -> AdhanMadhab.SHAFI
    Madhab.HANAFI -> AdhanMadhab.HANAFI
}
