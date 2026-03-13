package com.muslimbro.feature.qibla

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muslimbro.core.common.AppResult
import com.muslimbro.core.domain.usecase.GetQiblaDirectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class QiblaUiState(
    val isLoading: Boolean = false,
    val qiblaDirection: Double = 0.0, // degrees from true north to Mecca
    val deviceAzimuth: Float = 0f,    // current device compass heading
    val isPointingAtQibla: Boolean = false,
    val needsCalibration: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class QiblaViewModel @Inject constructor(
    private val sensorManager: SensorManager,
    private val getQiblaDirectionUseCase: GetQiblaDirectionUseCase
) : ViewModel(), SensorEventListener {

    private val _uiState = MutableStateFlow(QiblaUiState(isLoading = true))
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    // Low-pass filter state
    private val lpfAccel = FloatArray(3)
    private val lpfMagnet = FloatArray(3)
    private val ALPHA = 0.25f // Low-pass filter alpha for jitter reduction

    private var hasAccel = false
    private var hasMagnet = false

    init {
        loadQiblaDirection()
        registerSensors()
    }

    private fun loadQiblaDirection() {
        getQiblaDirectionUseCase()
            .onEach { result ->
                when (result) {
                    is AppResult.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is AppResult.Success -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        qiblaDirection = result.data,
                        error = null
                    )
                    is AppResult.Error -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message ?: "Could not determine Qibla direction"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun registerSensors() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                lowPassFilter(event.values, lpfAccel)
                System.arraycopy(lpfAccel, 0, accelerometerReading, 0, 3)
                hasAccel = true
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                lowPassFilter(event.values, lpfMagnet)
                System.arraycopy(lpfMagnet, 0, magnetometerReading, 0, 3)
                hasMagnet = true
            }
        }

        if (hasAccel && hasMagnet) {
            updateAzimuth()
        }
    }

    private fun lowPassFilter(input: FloatArray, output: FloatArray) {
        for (i in input.indices) {
            output[i] = output[i] + ALPHA * (input[i] - output[i])
        }
    }

    private fun updateAzimuth() {
        val success = SensorManager.getRotationMatrix(
            rotationMatrix, null, accelerometerReading, magnetometerReading
        )
        if (!success) return

        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        val azimuthRad = orientationAngles[0]
        val azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
        val normalizedAzimuth = (azimuthDeg + 360) % 360

        val qiblaDir = _uiState.value.qiblaDirection.toFloat()
        val diff = Math.abs(normalizedAzimuth - qiblaDir)
        val angleDiff = if (diff > 180) 360 - diff else diff
        val isPointing = angleDiff <= 3f

        _uiState.value = _uiState.value.copy(
            deviceAzimuth = normalizedAzimuth,
            isPointingAtQibla = isPointing
        )
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        if (sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            val needsCalibration = accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE
            _uiState.value = _uiState.value.copy(needsCalibration = needsCalibration)
        }
    }

    override fun onCleared() {
        sensorManager.unregisterListener(this)
        super.onCleared()
    }
}
