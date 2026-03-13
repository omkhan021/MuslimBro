package com.muslimbro.feature.qibla

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.muslimbro.core.ui.components.ErrorScreen
import com.muslimbro.core.ui.components.LoadingScreen
import com.muslimbro.core.ui.theme.Gold500
import com.muslimbro.core.ui.theme.Green800
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    modifier: Modifier = Modifier,
    viewModel: QiblaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    // Haptic feedback when pointing at Qibla
    if (uiState.isPointingAtQibla) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Qibla Direction") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingScreen(Modifier.padding(paddingValues))
            uiState.error != null -> ErrorScreen(
                message = uiState.error!!,
                modifier = Modifier.padding(paddingValues)
            )
            else -> QiblaContent(
                uiState = uiState,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun QiblaContent(
    uiState: QiblaUiState,
    modifier: Modifier = Modifier
) {
    // Needle rotation = Qibla bearing - current device azimuth
    val needleAngle = ((uiState.qiblaDirection - uiState.deviceAzimuth + 360) % 360).toFloat()
    val animatedNeedleAngle by animateFloatAsState(
        targetValue = needleAngle,
        animationSpec = tween(durationMillis = 200),
        label = "needle_rotation"
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        if (uiState.needsCalibration) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Calibrate compass: move device in figure-8 pattern",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Box(contentAlignment = Alignment.Center) {
            CompassView(
                needleAngle = animatedNeedleAngle,
                isAligned = uiState.isPointingAtQibla,
                modifier = Modifier.size(280.dp)
            )
        }

        Text(
            text = if (uiState.isPointingAtQibla) "Facing Qibla ✓" else "Facing Qibla",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (uiState.isPointingAtQibla) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Qibla: %.1f°".format(uiState.qiblaDirection),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(0.6f)
        )
    }
}

@Composable
private fun CompassView(
    needleAngle: Float,
    isAligned: Boolean,
    modifier: Modifier = Modifier
) {
    val primaryColor = Green800
    val goldColor = Gold500
    val alignedColor = Color(0xFF4CAF50)

    Canvas(modifier = modifier) {
        val cx = size.width / 2
        val cy = size.height / 2
        val radius = size.minDimension / 2 * 0.9f

        // Compass circle
        drawCircle(
            color = if (isAligned) alignedColor.copy(alpha = 0.15f) else Color.LightGray.copy(0.2f),
            radius = radius,
            center = Offset(cx, cy)
        )
        drawCircle(
            color = if (isAligned) alignedColor else primaryColor,
            radius = radius,
            center = Offset(cx, cy),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
        )

        // N/S/E/W tick marks
        val cardinals = listOf("N" to 0f, "E" to 90f, "S" to 180f, "W" to 270f)
        // Draw tick marks at 30-degree intervals
        for (angle in 0 until 360 step 30) {
            val rad = Math.toRadians(angle.toDouble())
            val innerR = radius * 0.88f
            val outerR = radius * 0.98f
            val startX = cx + innerR * sin(rad).toFloat()
            val startY = cy - innerR * cos(rad).toFloat()
            val endX = cx + outerR * sin(rad).toFloat()
            val endY = cy - outerR * cos(rad).toFloat()
            drawLine(
                color = primaryColor.copy(alpha = 0.5f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = if (angle % 90 == 0) 3f else 1.5f
            )
        }

        // Qibla needle (Gold, pointing to Mecca)
        rotate(degrees = needleAngle, pivot = Offset(cx, cy)) {
            val needleLength = radius * 0.75f
            val needleWidth = 12f

            // Gold arrowhead pointing up (toward Qibla)
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(cx, cy - needleLength)
                lineTo(cx - needleWidth / 2, cy)
                lineTo(cx + needleWidth / 2, cy)
                close()
            }
            drawPath(path, goldColor)

            // Dark tail
            val tail = androidx.compose.ui.graphics.Path().apply {
                moveTo(cx, cy + needleLength * 0.4f)
                lineTo(cx - needleWidth / 3, cy)
                lineTo(cx + needleWidth / 3, cy)
                close()
            }
            drawPath(tail, primaryColor.copy(alpha = 0.6f))
        }

        // Center dot
        drawCircle(
            color = if (isAligned) alignedColor else primaryColor,
            radius = 8f,
            center = Offset(cx, cy)
        )
    }
}
