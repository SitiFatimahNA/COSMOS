package com.siti.cosmos.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siti.cosmos.ui.theme.SpaceAccent
import com.siti.cosmos.ui.theme.SpaceBlack
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val alphaAnim  = remember { Animatable(0f) }
    val scaleAnim  = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        // Fade + scale in
        alphaAnim.animateTo(1f, animationSpec = tween(900, easing = EaseOutCubic))
        scaleAnim.animateTo(1f, animationSpec = tween(700, easing = EaseOutBack))
        delay(1200)
        // Fade out
        alphaAnim.animateTo(0f, animationSpec = tween(500))
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors  = listOf(SpaceAccent.copy(alpha = 0.18f), SpaceBlack),
                    radius  = 1200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            Text("🌌", fontSize = 90.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                "COSMOS",
                fontSize   = 42.sp,
                fontWeight = FontWeight.Black,
                color      = MaterialTheme.colorScheme.onBackground,
                letterSpacing = 8.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Discover the Universe",
                fontSize = 14.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 2.sp
            )
        }

        // Version tag at bottom
        Text(
            "Powered by NASA Open APIs",
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
                .alpha(alphaAnim.value),
            fontSize = 11.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}
