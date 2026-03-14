package com.muslimbro.core.domain.repository

import com.muslimbro.core.common.AppResult
import com.muslimbro.core.domain.model.UserLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getCurrentLocation(): Flow<AppResult<UserLocation>>
    fun getSavedLocation(): Flow<UserLocation?>
    suspend fun saveManualLocation(location: UserLocation)
    suspend fun clearManualLocation()
    suspend fun geocodeLocation(query: String): List<UserLocation>
}
