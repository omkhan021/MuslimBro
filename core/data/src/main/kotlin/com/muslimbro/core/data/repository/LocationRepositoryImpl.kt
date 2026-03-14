package com.muslimbro.core.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.muslimbro.core.common.AppResult
import com.muslimbro.core.data.datastore.SettingsDataStore
import com.muslimbro.core.domain.model.UserLocation
import com.muslimbro.core.domain.repository.LocationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
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
        settingsDataStore.savedLocation.collect { saved ->
            if (saved != null) {
                emit(AppResult.Success(UserLocation(
                    latitude = saved.first,
                    longitude = saved.second,
                    cityName = saved.third.ifEmpty { null },
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
                    val displayName = reverseGeocode(location.latitude, location.longitude)
                    emit(AppResult.Success(UserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        cityName = displayName
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
                    cityName = it.third.ifEmpty { null },
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

    override suspend fun geocodeLocation(query: String): List<UserLocation> {
        if (!Geocoder.isPresent()) return emptyList()
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(query.trim(), 5) ?: return@withContext emptyList()
                addresses.mapNotNull { address ->
                    if (!address.hasLatitude() || !address.hasLongitude()) return@mapNotNull null
                    val parts = listOfNotNull(
                        address.locality,
                        address.adminArea,
                        address.countryName
                    ).distinct()
                    val displayName = parts.joinToString(", ").ifEmpty { address.featureName ?: query }
                    UserLocation(
                        latitude = address.latitude,
                        longitude = address.longitude,
                        cityName = displayName,
                        countryName = address.countryName
                    )
                }
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    private suspend fun reverseGeocode(lat: Double, lng: Double): String? {
        if (!Geocoder.isPresent()) return null
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1) ?: return@withContext null
                val address = addresses.firstOrNull() ?: return@withContext null
                val parts = listOfNotNull(
                    address.locality,
                    address.adminArea,
                    address.countryName
                ).distinct()
                parts.joinToString(", ").ifEmpty { null }
            } catch (_: Exception) {
                null
            }
        }
    }
}
