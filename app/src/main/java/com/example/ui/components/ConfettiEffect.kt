package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val speedX: Float,
    val speedY: Float,
    val color: Color,
    val size: Float,
    val rotation: Float
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    particleCount: Int = 45,
    onFinished: () -> Unit = {}
) {
    val progress = remember { Animatable(0f) }
    val colors = listOf(
        Color(0xFFFF3838),
        Color(0xFFFF9F1A),
        Color(0xFFFFD32A),
        Color(0xFF2ED573),
        Color(0xFF1E90FF),
        Color(0xFF9B59B6),
        Color(0xFFFF6B8B)
    )

    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * 0.3f,
                speedX = (Random.nextFloat() - 0.5f) * 200f,
                speedY = Random.nextFloat() * 600f + 300f,
                color = colors[Random.nextInt(colors.size)],
                size = Random.nextFloat() * 12f + 8f,
                rotation = Random.nextFloat() * 360f
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
        )
        onFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val p = progress.value

        for (pt in particles) {
            val curX = (pt.x * w + pt.speedX * p).coerceIn(0f, w)
            val curY = pt.y * h + pt.speedY * p
            val alpha = (1f - p).coerceIn(0f, 1f)

            if (curY < h) {
                drawRect(
                    color = pt.color.copy(alpha = alpha),
                    topLeft = Offset(curX, curY),
                    size = Size(pt.size, pt.size * 0.7f)
                )
            }
        }
    }
}
