package com.muslimbro.core.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.muslimbro.core.common.AppResult
import com.muslimbro.core.data.datastore.SettingsDataStore
import com.muslimbro.core.domain.model.UserLocation
import com.muslimbro.core.domain.repository.LocationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsDataStore: SettingsDataStore
) : LocationRepository {

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(): Flow<AppResult<UserLocation>> = flow {
        emit(AppResult.Loading)
        // Check for saved manual location first
        settingsDataStore.savedLocation.collect { saved ->
            if (saved != null) {
                emit(AppResult.Success(UserLocation(
                    latitude = saved.first,
                    longitude = saved.second,
                    cityName = saved.third,
                    isManual = true
                )))
                return@collect
            }
            // Fall back to GPS
            try {
                val location = suspendCancellableCoroutine { continuation ->
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { loc ->
                            if (loc != null) {
                                continuation.resume(loc)
                            } else {
                                // Try last known
                                fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                                    continuation.resume(lastLoc)
                                }.addOnFailureListener {
                                    continuation.resume(null)
                                }
                            }
                        }
                        .addOnFailureListener { continuation.resume(null) }
                }
                if (location != null) {
                    emit(AppResult.Success(UserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )))
                } else {
                    emit(AppResult.Error(Exception("Location unavailable"), "Could not get current location"))
                }
            } catch (e: Exception) {
                emit(AppResult.Error(e, "Location error: ${e.message}"))
            }
        }
    }

    override fun getSavedLocation(): Flow<UserLocation?> =
        settingsDataStore.savedLocation.map { saved ->
            saved?.let {
                UserLocation(
                    latitude = it.first,
                    longitude = it.second,
                    cityName = it.third,
                    isManual = true
                )
            }
        }

    override suspend fun saveManualLocation(location: UserLocation) {
        settingsDataStore.saveLocation(
            location.latitude,
            location.longitude,
            location.cityName ?: ""
        )
    }

    override suspend fun clearManualLocation() {
        settingsDataStore.clearManualLocation()
    }
}
