package com.muslimbro.app

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimbro.core.ui.theme.Gold300
import com.muslimbro.core.ui.theme.Gold500
import kotlinx.coroutines.delay

private val NightSky    = Color(0xFF060E08)
private val DeepGreen   = Color(0xFF0D1F10)
private val MidGreen    = Color(0xFF1B5E20)
private val BrightGreen = Color(0xFF2E7D32)
private val DomeGreen   = Color(0xFF388E3C)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val fadeAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        fadeAlpha.animateTo(1f, animationSpec = tween(900))
        delay(1800)
        onFinished()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val starPulse by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(NightSky, DeepGreen, MidGreen))
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawStars(starPulse)
            drawCrescent()
            drawMosque()
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 200.dp)
        ) {
            Text(
                text = "Muslim Bro",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Gold300.copy(alpha = fadeAlpha.value),
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
                fontSize = 20.sp,
                color = Color.White.copy(alpha = fadeAlpha.value * 0.85f)
            )
        }
    }
}

private val starData = listOf(
    0.08f to 0.04f,  0.22f to 0.10f,  0.55f to 0.06f,
    0.78f to 0.02f,  0.91f to 0.13f,  0.04f to 0.22f,
    0.40f to 0.15f,  0.68f to 0.19f,  0.13f to 0.32f,
    0.84f to 0.28f,  0.33f to 0.07f,  0.52f to 0.25f,
    0.62f to 0.35f,  0.17f to 0.18f,  0.95f to 0.38f
)
private val starSizes = listOf(3f, 2f, 4f, 2.5f, 3f, 2f, 3.5f, 2f, 4f, 3f, 2f, 3f, 2.5f, 2f, 3f)

private fun DrawScope.drawStars(pulse: Float) {
    starData.forEachIndexed { i, (xFrac, yFrac) ->
        val alpha = pulse * if (i % 3 == 0) 1f else 0.55f
        drawCircle(
            color = Color.White.copy(alpha = alpha),
            radius = starSizes[i],
            center = Offset(xFrac * size.width, yFrac * size.height)
        )
    }
}

private fun DrawScope.drawCrescent() {
    val cx = size.width * 0.73f
    val cy = size.height * 0.16f
    val r  = size.width * 0.085f

    // Outer gold disc
    drawCircle(color = Gold500, radius = r, center = Offset(cx, cy))
    // Offset cutout to carve the crescent shape
    drawCircle(color = NightSky, radius = r * 0.82f, center = Offset(cx + r * 0.28f, cy - r * 0.08f))

    // Small star beside the crescent
    val sx = cx + r * 1.35f
    val sy = cy - r * 0.55f
    drawCircle(color = Gold300, radius = 5f, center = Offset(sx, sy))
    // tiny four-point sparkle lines
    for (angle in listOf(0f, 90f)) {
        val rad = Math.toRadians(angle.toDouble()).toFloat()
        val dx = kotlin.math.cos(rad) * 9f
        val dy = kotlin.math.sin(rad) * 9f
        drawLine(Gold300, Offset(sx - dx, sy - dy), Offset(sx + dx, sy + dy), strokeWidth = 1.5f)
    }
}

private fun DrawScope.drawMosque() {
    val w = size.width
    val h = size.height
    val groundY  = h * 0.915f

    // Ground glow
    drawLine(
        color = Gold500.copy(alpha = 0.4f),
        start = Offset(0f, groundY),
        end   = Offset(w, groundY),
        strokeWidth = 2f
    )

    // ── Main building body ──────────────────────────────────────────────────
    val bodyL   = w * 0.22f
    val bodyR   = w * 0.78f
    val bodyTop = h * 0.70f
    drawRect(
        color   = BrightGreen,
        topLeft = Offset(bodyL, bodyTop),
        size    = Size(bodyR - bodyL, groundY - bodyTop)
    )
    // gold trim at top of body
    drawLine(Gold500.copy(0.7f), Offset(bodyL, bodyTop), Offset(bodyR, bodyTop), strokeWidth = 3f)

    // ── Main dome ───────────────────────────────────────────────────────────
    val domeCx = w * 0.5f
    val domeR  = w * 0.145f
    drawDome(domeCx, bodyTop, domeR, DomeGreen)
    // finial
    drawLine(Gold500, Offset(domeCx, bodyTop - domeR * 1.55f), Offset(domeCx, bodyTop - domeR * 1.55f - 16f), strokeWidth = 3f)
    drawCircle(Gold500, 5f, Offset(domeCx, bodyTop - domeR * 1.55f - 21f))

    // ── Side half-domes ─────────────────────────────────────────────────────
    drawDome(w * 0.36f, bodyTop, domeR * 0.55f, DomeGreen)
    drawDome(w * 0.64f, bodyTop, domeR * 0.55f, DomeGreen)

    // ── Left minaret ────────────────────────────────────────────────────────
    drawMinaret(w * 0.30f, groundY, h * 0.19f, w * 0.055f)
    // ── Right minaret ───────────────────────────────────────────────────────
    drawMinaret(w * 0.70f, groundY, h * 0.19f, w * 0.055f)

    // ── Arched door ─────────────────────────────────────────────────────────
    val doorW = w * 0.10f
    val doorH = h * 0.11f
    val doorL = domeCx - doorW / 2
    val doorT = groundY - doorH
    drawRect(MidGreen, Offset(doorL, doorT), Size(doorW, doorH))
    drawArch(doorL, doorT, doorW, MidGreen)

    // ── Side windows ────────────────────────────────────────────────────────
    listOf(w * 0.36f, w * 0.64f).forEach { wx ->
        val winW = w * 0.08f
        val winH = h * 0.065f
        val winL = wx - winW / 2
        val winT = bodyTop + (groundY - bodyTop) * 0.22f
        drawRect(MidGreen, Offset(winL, winT), Size(winW, winH))
        drawArch(winL, winT, winW, MidGreen)
    }
}

private fun DrawScope.drawDome(cx: Float, baseY: Float, r: Float, color: Color) {
    val path = Path().apply {
        moveTo(cx - r, baseY)
        cubicTo(cx - r, baseY - r * 1.55f, cx + r, baseY - r * 1.55f, cx + r, baseY)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawMinaret(cx: Float, groundY: Float, height: Float, width: Float) {
    val top = groundY - height
    drawRect(BrightGreen, Offset(cx - width / 2, top), Size(width, height))
    // minaret dome
    val path = Path().apply {
        moveTo(cx - width / 2, top)
        cubicTo(cx - width / 2, top - width * 1.3f, cx + width / 2, top - width * 1.3f, cx + width / 2, top)
        close()
    }
    drawPath(path, DomeGreen)
    // finial
    drawLine(Gold500, Offset(cx, top - width * 1.3f), Offset(cx, top - width * 1.3f - 10f), strokeWidth = 2f)
    drawCircle(Gold500, 3.5f, Offset(cx, top - width * 1.3f - 13f))
    // balcony ledge
    drawLine(Gold500.copy(0.6f), Offset(cx - width * 0.7f, top + height * 0.15f), Offset(cx + width * 0.7f, top + height * 0.15f), strokeWidth = 2f)
}

private fun DrawScope.drawArch(left: Float, top: Float, width: Float, color: Color) {
    val path = Path().apply {
        moveTo(left, top)
        cubicTo(left, top - width * 0.65f, left + width, top - width * 0.65f, left + width, top)
        close()
    }
    drawPath(path, color)
}
