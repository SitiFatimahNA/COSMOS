package com.siti.cosmos

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.*

/**
 * Simulates a 3D-looking planet sphere using:
 *  - Layered radial gradients for depth / curvature
 *  - Specular highlight (fake light source)
 *  - Ambient occlusion shadow on the dark edge
 *  - Gyroscope tilt + drag gesture to shift the light source position
 *  - Auto slow-spin of the highlight when idle
 *
 * @param baseColor   The main mid-tone colour of the planet surface
 * @param darkColor   The shadow / terminator colour
 * @param lightColor  The highlight / sunlit colour
 * @param ringColor   Optional atmosphere ring tint (use Color.Transparent to skip)
 * @param size        Diameter of the sphere in dp
 */
@Composable
fun Planet3D(
    baseColor: Color,
    darkColor: Color,
    lightColor: Color,
    ringColor: Color = Color.Transparent,
    size: Dp = 220.dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // ── Light-source position (normalised –1..1 on each axis) ────────────────
    var lightX by remember { mutableFloatStateOf(-0.4f) }
    var lightY by remember { mutableFloatStateOf(-0.35f) }

    // ── Gyroscope ─────────────────────────────────────────────────────────────
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // event.values: [roll, pitch, yaw] rad/s
                lightX = (lightX + event.values[1] * 0.03f).coerceIn(-0.9f, 0.9f)
                lightY = (lightY + event.values[0] * 0.03f).coerceIn(-0.9f, 0.9f)
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        if (gyro != null) {
            sensorManager.registerListener(listener, gyro, SensorManager.SENSOR_DELAY_GAME)
        }
        onDispose { sensorManager.unregisterListener(listener) }
    }

    // ── Auto-idle spin (slow drift when user is not touching) ─────────────────
    var dragging by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            if (!dragging) {
                lightX = (lightX - 0.0008f).let { if (it < -0.9f) 0.9f else it }
            }
        }
    }

    // ── Drag gesture to tilt the light source ─────────────────────────────────
    Canvas(
        modifier = modifier
            .size(size)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragging = true },
                    onDragEnd   = { dragging = false },
                    onDragCancel = { dragging = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        lightX = (lightX + dragAmount.x / (this.size.width  * 0.5f)).coerceIn(-0.9f, 0.9f)
                        lightY = (lightY + dragAmount.y / (this.size.height * 0.5f)).coerceIn(-0.9f, 0.9f)
                    }
                )
            }
    ) {
        val cx = size.toPx() / 2f
        val cy = size.toPx() / 2f
        val radius = cx * 0.96f   // slight inset so shadow ring is visible

        drawPlanet3D(
            cx = cx, cy = cy, radius = radius,
            lightX = lightX, lightY = lightY,
            baseColor = baseColor,
            darkColor = darkColor,
            lightColor = lightColor,
            ringColor = ringColor
        )
    }
}

/** All drawing logic – pure DrawScope, no state. */
private fun DrawScope.drawPlanet3D(
    cx: Float, cy: Float, radius: Float,
    lightX: Float, lightY: Float,
    baseColor: Color,
    darkColor: Color,
    lightColor: Color,
    ringColor: Color
) {
    // ── 1. Outer glow / atmosphere halo ──────────────────────────────────────
    if (ringColor != Color.Transparent) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Transparent, ringColor.copy(alpha = 0.18f), Color.Transparent),
                center = Offset(cx, cy),
                radius = radius * 1.22f
            ),
            radius = radius * 1.22f,
            center = Offset(cx, cy)
        )
    }

    // ── 2. Base sphere (ambient / diffuse) ────────────────────────────────────
    // The light offset is mapped to a gradient centre so the lit side appears brighter
    val litCx = cx + lightX * radius * 0.55f
    val litCy = cy + lightY * radius * 0.55f

    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0.0f  to lightColor.copy(alpha = 0.95f),
                0.45f to baseColor,
                0.78f to baseColor.darken(0.25f),
                1.0f  to darkColor
            ),
            center = Offset(litCx, litCy),
            radius = radius * 1.3f
        ),
        radius = radius,
        center = Offset(cx, cy)
    )

    // ── 3. Terminator shadow (dark crescent on opposite side of light) ─────────
    val shadowCx = cx - lightX * radius * 0.6f
    val shadowCy = cy - lightY * radius * 0.6f
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0.0f  to Color.Transparent,
                0.55f to Color.Transparent,
                0.80f to darkColor.copy(alpha = 0.55f),
                1.0f  to darkColor.copy(alpha = 0.90f)
            ),
            center = Offset(shadowCx, shadowCy),
            radius = radius * 1.4f
        ),
        radius = radius,
        center = Offset(cx, cy)
    )

    // ── 4. Specular highlight (small bright glint at light source position) ───
    val specX = cx + lightX * radius * 0.42f
    val specY = cy + lightY * radius * 0.42f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.55f),
                Color.White.copy(alpha = 0.18f),
                Color.Transparent
            ),
            center = Offset(specX, specY),
            radius = radius * 0.38f
        ),
        radius = radius,
        center = Offset(cx, cy)
    )

    // ── 5. Limb darkening (edge darkens like a real sphere) ───────────────────
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0.0f  to Color.Transparent,
                0.70f to Color.Transparent,
                0.88f to Color.Black.copy(alpha = 0.18f),
                1.0f  to Color.Black.copy(alpha = 0.52f)
            ),
            center = Offset(cx, cy),
            radius = radius
        ),
        radius = radius,
        center = Offset(cx, cy)
    )

    // ── 6. Surface texture bands (subtle horizontal stripes for gas giants) ───
    // We draw very faint ellipses to mimic cloud/band features
    val bandAlpha = 0.07f
    for (i in -3..3) {
        val bandY = cy + i * radius * 0.22f
        val bandH = radius * 0.07f
        val halfW = sqrt(max(0f, radius * radius - (bandY - cy) * (bandY - cy))) * 0.98f
        if (halfW > 0) {
            drawOval(
                color = darkColor.copy(alpha = bandAlpha),
                topLeft = Offset(cx - halfW, bandY - bandH),
                size = androidx.compose.ui.geometry.Size(halfW * 2f, bandH * 2f)
            )
        }
    }

    // ── 7. Drop shadow below sphere ───────────────────────────────────────────
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent),
            center = Offset(cx + radius * 0.08f, cy + radius * 0.72f),
            radius = radius * 0.65f
        ),
        radius = radius * 0.65f,
        center = Offset(cx + radius * 0.08f, cy + radius * 0.72f)
    )
}

/** Darken a colour by [factor] (0 = unchanged, 1 = black). */
private fun Color.darken(factor: Float): Color {
    val f = 1f - factor.coerceIn(0f, 1f)
    return Color(red * f, green * f, blue * f, alpha)
}

// ──────────────────────────────────────────────────────────────────────────────
// Per-planet colour presets
// ──────────────────────────────────────────────────────────────────────────────

data class PlanetColors(
    val base: Color,
    val dark: Color,
    val light: Color,
    val ring: Color = Color.Transparent
)

fun planetColors(imageRes: Int): PlanetColors = when (imageRes) {
    R.drawable.planet_mercury -> PlanetColors(
        base  = Color(0xFF8A8FA8),
        dark  = Color(0xFF2E3040),
        light = Color(0xFFCDD0E0)
    )
    R.drawable.planet_venus   -> PlanetColors(
        base  = Color(0xFFD4906A),
        dark  = Color(0xFF6B3820),
        light = Color(0xFFF5D5B0),
        ring  = Color(0xFFD4906A)
    )
    R.drawable.planet_earth   -> PlanetColors(
        base  = Color(0xFF2E6FA3),
        dark  = Color(0xFF0D2B40),
        light = Color(0xFF7BBFE0),
        ring  = Color(0xFF4A9FD0)
    )
    R.drawable.planet_mars    -> PlanetColors(
        base  = Color(0xFFC0392B),
        dark  = Color(0xFF4A1008),
        light = Color(0xFFE8846A),
        ring  = Color(0xFFC0392B)
    )
    R.drawable.planet_jupiter -> PlanetColors(
        base  = Color(0xFFC8956A),
        dark  = Color(0xFF5A3A1A),
        light = Color(0xFFEED5A0),
        ring  = Color(0xFFC8956A)
    )
    R.drawable.planet_saturn  -> PlanetColors(
        base  = Color(0xFFD4B060),
        dark  = Color(0xFF604820),
        light = Color(0xFFF5E0A0),
        ring  = Color(0xFFD4B060)
    )
    R.drawable.planet_uranus  -> PlanetColors(
        base  = Color(0xFF5AC8D8),
        dark  = Color(0xFF1A4858),
        light = Color(0xFFA0E8F0),
        ring  = Color(0xFF5AC8D8)
    )
    R.drawable.planet_neptune -> PlanetColors(
        base  = Color(0xFF3A5FD8),
        dark  = Color(0xFF0D1A58),
        light = Color(0xFF7090F0),
        ring  = Color(0xFF3A5FD8)
    )
    else -> PlanetColors(
        base  = Color(0xFF607080),
        dark  = Color(0xFF202830),
        light = Color(0xFF90A8C0)
    )
}
