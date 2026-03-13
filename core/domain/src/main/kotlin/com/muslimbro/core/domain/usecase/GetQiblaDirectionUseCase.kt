package com.muslimbro.core.domain.usecase

import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.Qibla
import com.muslimbro.core.common.AppResult
import com.muslimbro.core.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetQiblaDirectionUseCase(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Flow<AppResult<Double>> {
        return locationRepository.getCurrentLocation().map { result ->
            when (result) {
                is AppResult.Success -> {
                    val qibla = Qibla(Coordinates(result.data.latitude, result.data.longitude))
                    AppResult.Success(qibla.direction)
                }
                is AppResult.Error -> result
                is AppResult.Loading -> AppResult.Loading
            }
        }
    }
}
