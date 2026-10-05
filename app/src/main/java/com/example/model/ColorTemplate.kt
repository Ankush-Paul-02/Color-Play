package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

data class ColorTemplate(
    val id: String,
    val name: String,
    val category: String,
    val emoji: String,
    val difficulty: String, // "Easy", "Medium", "Fun"
    val defaultTitle: String
)

object TemplateRegistry {
    val templates = listOf(
        ColorTemplate("rooster", "Barnyard Rooster", "Farm Animals", "🐓", "Fun", "My Farm Rooster"),
        ColorTemplate("butterfly", "Rainbow Butterfly", "Nature", "🦋", "Easy", "Magical Butterfly"),
        ColorTemplate("car", "Speedy Race Car", "Vehicles", "🚗", "Easy", "My Red Racecar"),
        ColorTemplate("puppy", "Playful Puppy", "Pets", "🐶", "Easy", "Cute Little Puppy"),
        ColorTemplate("dino", "Friendly Dinosaur", "Prehistoric", "🦖", "Fun", "Gentle Dino"),
        ColorTemplate("cake", "Birthday Cupcake", "Treats", "🧁", "Easy", "Party Cupcake"),
        ColorTemplate("rocket", "Cosmic Rocket", "Space", "🚀", "Medium", "Moon Rocket"),
        ColorTemplate("free_draw", "Blank Magic Canvas", "Creative", "🎨", "All Ages", "My Masterpiece")
    )

    fun getById(id: String): ColorTemplate {
        return templates.find { it.id == id } ?: templates.last()
    }

    /**
     * Draws the template outline on canvas scaled proportionally to width and height
     */
    fun drawTemplateOutline(scope: DrawScope, templateId: String, outlineColor: Color) {
        val w = scope.size.width
        val h = scope.size.height
        val stroke = Stroke(width = (w * 0.008f).coerceIn(4f, 10f))

        when (templateId) {
            "rooster" -> drawRooster(scope, w, h, outlineColor, stroke)
            "butterfly" -> drawButterfly(scope, w, h, outlineColor, stroke)
            "car" -> drawCar(scope, w, h, outlineColor, stroke)
            "puppy" -> drawPuppy(scope, w, h, outlineColor, stroke)
            "dino" -> drawDino(scope, w, h, outlineColor, stroke)
            "cake" -> drawCake(scope, w, h, outlineColor, stroke)
            "rocket" -> drawRocket(scope, w, h, outlineColor, stroke)
            else -> { /* Blank Canvas */ }
        }
    }

    private fun drawRooster(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        // Barn in background
        val barn = Path().apply {
            moveTo(w * 0.08f, h * 0.45f)
            lineTo(w * 0.25f, h * 0.28f)
            lineTo(w * 0.42f, h * 0.45f)
            lineTo(w * 0.42f, h * 0.85f)
            lineTo(w * 0.08f, h * 0.85f)
            close()
        }
        scope.drawPath(barn, color.copy(alpha = 0.6f), style = stroke)

        // Barn roof lines
        scope.drawLine(color.copy(alpha = 0.6f), Offset(w * 0.05f, h * 0.46f), Offset(w * 0.25f, h * 0.25f), strokeWidth = stroke.width)
        scope.drawLine(color.copy(alpha = 0.6f), Offset(w * 0.25f, h * 0.25f), Offset(w * 0.45f, h * 0.46f), strokeWidth = stroke.width)

        // Rooster Body
        val body = Path().apply {
            moveTo(w * 0.60f, h * 0.42f)
            // Head and beak
            cubicTo(w * 0.58f, h * 0.30f, w * 0.72f, h * 0.26f, w * 0.78f, h * 0.35f)
            // Beak
            lineTo(w * 0.88f, h * 0.38f)
            lineTo(w * 0.78f, h * 0.44f)
            // Chest & belly
            cubicTo(w * 0.85f, h * 0.60f, w * 0.75f, h * 0.75f, w * 0.55f, h * 0.75f)
            // Tail feathers
            cubicTo(w * 0.35f, h * 0.70f, w * 0.30f, h * 0.40f, w * 0.45f, h * 0.35f)
            cubicTo(w * 0.40f, h * 0.50f, w * 0.50f, h * 0.58f, w * 0.60f, h * 0.42f)
            close()
        }
        scope.drawPath(body, color, style = stroke)

        // Comb on head
        val comb = Path().apply {
            moveTo(w * 0.65f, h * 0.28f)
            cubicTo(w * 0.65f, h * 0.20f, w * 0.70f, h * 0.20f, w * 0.71f, h * 0.26f)
            cubicTo(w * 0.73f, h * 0.18f, w * 0.78f, h * 0.20f, w * 0.77f, h * 0.27f)
            cubicTo(w * 0.80f, h * 0.23f, w * 0.84f, h * 0.26f, w * 0.80f, h * 0.32f)
        }
        scope.drawPath(comb, color, style = stroke)

        // Eye
        scope.drawCircle(color, radius = w * 0.022f, center = Offset(w * 0.73f, h * 0.36f))

        // Wing
        val wing = Path().apply {
            moveTo(w * 0.58f, h * 0.50f)
            cubicTo(w * 0.68f, h * 0.50f, w * 0.72f, h * 0.62f, w * 0.60f, h * 0.68f)
            cubicTo(w * 0.50f, h * 0.65f, w * 0.48f, h * 0.55f, w * 0.58f, h * 0.50f)
        }
        scope.drawPath(wing, color, style = stroke)

        // Legs
        scope.drawLine(color, Offset(w * 0.58f, h * 0.75f), Offset(w * 0.56f, h * 0.88f), strokeWidth = stroke.width)
        scope.drawLine(color, Offset(w * 0.56f, h * 0.88f), Offset(w * 0.50f, h * 0.89f), strokeWidth = stroke.width)
        scope.drawLine(color, Offset(w * 0.66f, h * 0.74f), Offset(w * 0.66f, h * 0.88f), strokeWidth = stroke.width)
        scope.drawLine(color, Offset(w * 0.66f, h * 0.88f), Offset(w * 0.60f, h * 0.89f), strokeWidth = stroke.width)

        // Cute Little Baby Chick next to it
        val chick = Path().apply {
            addOval(Rect(w * 0.30f, h * 0.68f, w * 0.44f, h * 0.84f))
        }
        scope.drawPath(chick, color, style = stroke)
        scope.drawCircle(color, radius = w * 0.012f, center = Offset(w * 0.38f, h * 0.73f))
        // Chick beak
        scope.drawLine(color, Offset(w * 0.42f, h * 0.74f), Offset(w * 0.46f, h * 0.75f), strokeWidth = stroke.width)
        scope.drawLine(color, Offset(w * 0.46f, h * 0.75f), Offset(w * 0.42f, h * 0.77f), strokeWidth = stroke.width)
    }

    private fun drawButterfly(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        val cx = w * 0.5f
        val cy = h * 0.5f

        // Antennae
        scope.drawLine(color, Offset(cx - w * 0.02f, cy - h * 0.22f), Offset(cx - w * 0.12f, cy - h * 0.32f), strokeWidth = stroke.width)
        scope.drawCircle(color, radius = w * 0.02f, center = Offset(cx - w * 0.12f, cy - h * 0.32f))
        scope.drawLine(color, Offset(cx + w * 0.02f, cy - h * 0.22f), Offset(cx + w * 0.12f, cy - h * 0.32f), strokeWidth = stroke.width)
        scope.drawCircle(color, radius = w * 0.02f, center = Offset(cx + w * 0.12f, cy - h * 0.32f))

        // Center body
        val body = Path().apply {
            addOval(Rect(cx - w * 0.04f, cy - h * 0.22f, cx + w * 0.04f, cy + h * 0.25f))
        }
        scope.drawPath(body, color, style = stroke)

        // Left upper wing
        val leftTopWing = Path().apply {
            moveTo(cx - w * 0.04f, cy - h * 0.10f)
            cubicTo(cx - w * 0.35f, cy - h * 0.42f, cx - w * 0.50f, cy - h * 0.15f, cx - w * 0.32f, cy)
            cubicTo(cx - w * 0.20f, cy + h * 0.05f, cx - w * 0.05f, cy, cx - w * 0.04f, cy - h * 0.10f)
            close()
        }
        scope.drawPath(leftTopWing, color, style = stroke)

        // Right upper wing
        val rightTopWing = Path().apply {
            moveTo(cx + w * 0.04f, cy - h * 0.10f)
            cubicTo(cx + w * 0.35f, cy - h * 0.42f, cx + w * 0.50f, cy - h * 0.15f, cx + w * 0.32f, cy)
            cubicTo(cx + w * 0.20f, cy + h * 0.05f, cx + w * 0.05f, cy, cx + w * 0.04f, cy - h * 0.10f)
            close()
        }
        scope.drawPath(rightTopWing, color, style = stroke)

        // Left lower wing
        val leftBottomWing = Path().apply {
            moveTo(cx - w * 0.03f, cy + h * 0.02f)
            cubicTo(cx - w * 0.38f, cy + h * 0.08f, cx - w * 0.32f, cy + h * 0.36f, cx - w * 0.10f, cy + h * 0.28f)
            cubicTo(cx - w * 0.05f, cy + h * 0.22f, cx - w * 0.03f, cy + h * 0.15f, cx - w * 0.03f, cy + h * 0.02f)
            close()
        }
        scope.drawPath(leftBottomWing, color, style = stroke)

        // Right lower wing
        val rightBottomWing = Path().apply {
            moveTo(cx + w * 0.03f, cy + h * 0.02f)
            cubicTo(cx + w * 0.38f, cy + h * 0.08f, cx + w * 0.32f, cy + h * 0.36f, cx + w * 0.10f, cy + h * 0.28f)
            cubicTo(cx + w * 0.05f, cy + h * 0.22f, cx + w * 0.03f, cy + h * 0.15f, cx + w * 0.03f, cy + h * 0.02f)
            close()
        }
        scope.drawPath(rightBottomWing, color, style = stroke)

        // Decorative inner wing spots
        scope.drawCircle(color, radius = w * 0.045f, center = Offset(cx - w * 0.22f, cy - h * 0.12f), style = stroke)
        scope.drawCircle(color, radius = w * 0.045f, center = Offset(cx + w * 0.22f, cy - h * 0.12f), style = stroke)
        scope.drawCircle(color, radius = w * 0.030f, center = Offset(cx - w * 0.16f, cy + h * 0.16f), style = stroke)
        scope.drawCircle(color, radius = w * 0.030f, center = Offset(cx + w * 0.16f, cy + h * 0.16f), style = stroke)
    }

    private fun drawCar(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        // Car body
        val carBody = Path().apply {
            moveTo(w * 0.10f, h * 0.65f)
            lineTo(w * 0.10f, h * 0.55f)
            // Hood
            lineTo(w * 0.28f, h * 0.52f)
            // Windshield
            lineTo(w * 0.40f, h * 0.34f)
            // Roof
            lineTo(w * 0.70f, h * 0.34f)
            // Rear window
            lineTo(w * 0.82f, h * 0.52f)
            // Trunk
            lineTo(w * 0.90f, h * 0.55f)
            lineTo(w * 0.90f, h * 0.65f)
            // Bottom chassis with wheel arches
            lineTo(w * 0.78f, h * 0.65f)
            cubicTo(w * 0.78f, h * 0.52f, w * 0.62f, h * 0.52f, w * 0.62f, h * 0.65f)
            lineTo(w * 0.38f, h * 0.65f)
            cubicTo(w * 0.38f, h * 0.52f, w * 0.22f, h * 0.52f, w * 0.22f, h * 0.65f)
            close()
        }
        scope.drawPath(carBody, color, style = stroke)

        // Windows
        val frontWindow = Path().apply {
            moveTo(w * 0.42f, h * 0.37f)
            lineTo(w * 0.53f, h * 0.37f)
            lineTo(w * 0.53f, h * 0.50f)
            lineTo(w * 0.34f, h * 0.50f)
            close()
        }
        val backWindow = Path().apply {
            moveTo(w * 0.56f, h * 0.37f)
            lineTo(w * 0.68f, h * 0.37f)
            lineTo(w * 0.78f, h * 0.50f)
            lineTo(w * 0.56f, h * 0.50f)
            close()
        }
        scope.drawPath(frontWindow, color, style = stroke)
        scope.drawPath(backWindow, color, style = stroke)

        // Wheels
        scope.drawCircle(color, radius = w * 0.08f, center = Offset(w * 0.30f, h * 0.65f), style = stroke)
        scope.drawCircle(color, radius = w * 0.04f, center = Offset(w * 0.30f, h * 0.65f), style = stroke)
        scope.drawCircle(color, radius = w * 0.08f, center = Offset(w * 0.70f, h * 0.65f), style = stroke)
        scope.drawCircle(color, radius = w * 0.04f, center = Offset(w * 0.70f, h * 0.65f), style = stroke)

        // Headlight & Door handle
        scope.drawCircle(color, radius = w * 0.02f, center = Offset(w * 0.12f, h * 0.58f))
        scope.drawLine(color, Offset(w * 0.48f, h * 0.54f), Offset(w * 0.52f, h * 0.54f), strokeWidth = stroke.width)

        // Road line
        scope.drawLine(color.copy(alpha = 0.5f), Offset(w * 0.04f, h * 0.74f), Offset(w * 0.96f, h * 0.74f), strokeWidth = stroke.width)
    }

    private fun drawPuppy(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        val cx = w * 0.5f
        val cy = h * 0.45f

        // Head
        scope.drawCircle(color, radius = w * 0.22f, center = Offset(cx, cy), style = stroke)

        // Ears
        val leftEar = Path().apply {
            moveTo(cx - w * 0.15f, cy - h * 0.12f)
            cubicTo(cx - w * 0.32f, cy - h * 0.10f, cx - w * 0.36f, cy + h * 0.12f, cx - w * 0.20f, cy + h * 0.10f)
        }
        val rightEar = Path().apply {
            moveTo(cx + w * 0.15f, cy - h * 0.12f)
            cubicTo(cx + w * 0.32f, cy - h * 0.10f, cx + w * 0.36f, cy + h * 0.12f, cx + w * 0.20f, cy + h * 0.10f)
        }
        scope.drawPath(leftEar, color, style = stroke)
        scope.drawPath(rightEar, color, style = stroke)

        // Eyes
        scope.drawCircle(color, radius = w * 0.028f, center = Offset(cx - w * 0.08f, cy - h * 0.02f))
        scope.drawCircle(color, radius = w * 0.028f, center = Offset(cx + w * 0.08f, cy - h * 0.02f))

        // Snout
        val snout = Path().apply {
            addOval(Rect(cx - w * 0.09f, cy + h * 0.02f, cx + w * 0.09f, cy + h * 0.14f))
        }
        scope.drawPath(snout, color, style = stroke)
        // Nose
        scope.drawCircle(color, radius = w * 0.032f, center = Offset(cx, cy + h * 0.05f))

        // Cute smile and tongue
        val smile = Path().apply {
            moveTo(cx - w * 0.05f, cy + h * 0.09f)
            cubicTo(cx - w * 0.02f, cy + h * 0.12f, cx + w * 0.02f, cy + h * 0.12f, cx + w * 0.05f, cy + h * 0.09f)
        }
        scope.drawPath(smile, color, style = stroke)

        // Body & Paws
        val body = Path().apply {
            moveTo(cx - w * 0.16f, cy + h * 0.15f)
            lineTo(cx - w * 0.22f, cy + h * 0.38f)
            lineTo(cx + w * 0.22f, cy + h * 0.38f)
            lineTo(cx + w * 0.16f, cy + h * 0.15f)
        }
        scope.drawPath(body, color, style = stroke)

        // Front paws
        scope.drawCircle(color, radius = w * 0.06f, center = Offset(cx - w * 0.10f, cy + h * 0.38f), style = stroke)
        scope.drawCircle(color, radius = w * 0.06f, center = Offset(cx + w * 0.10f, cy + h * 0.38f), style = stroke)
    }

    private fun drawDino(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        val dino = Path().apply {
            moveTo(w * 0.35f, h * 0.28f)
            // Head & snout
            cubicTo(w * 0.35f, h * 0.18f, w * 0.58f, h * 0.18f, w * 0.62f, h * 0.25f)
            lineTo(w * 0.70f, h * 0.28f)
            cubicTo(w * 0.72f, h * 0.36f, w * 0.60f, h * 0.38f, w * 0.52f, h * 0.35f)
            // Neck & back with cute rounded spikes
            cubicTo(w * 0.48f, h * 0.44f, w * 0.48f, h * 0.50f, w * 0.42f, h * 0.56f)
            // Tail
            cubicTo(w * 0.25f, h * 0.60f, w * 0.10f, h * 0.62f, w * 0.08f, h * 0.55f)
            cubicTo(w * 0.12f, h * 0.68f, w * 0.28f, h * 0.72f, w * 0.40f, h * 0.74f)
            // Belly & legs
            lineTo(w * 0.42f, h * 0.86f)
            lineTo(w * 0.48f, h * 0.86f)
            lineTo(w * 0.50f, h * 0.75f)
            lineTo(w * 0.60f, h * 0.75f)
            lineTo(w * 0.62f, h * 0.86f)
            lineTo(w * 0.68f, h * 0.86f)
            lineTo(w * 0.68f, h * 0.68f)
            // Chest
            cubicTo(w * 0.72f, h * 0.52f, w * 0.60f, h * 0.40f, w * 0.48f, h * 0.35f)
            close()
        }
        scope.drawPath(dino, color, style = stroke)

        // Dino eye
        scope.drawCircle(color, radius = w * 0.022f, center = Offset(w * 0.54f, h * 0.25f))

        // Cute back plates/triangles
        val p1 = Path().apply { moveTo(w * 0.38f, h * 0.26f); lineTo(w * 0.34f, h * 0.21f); lineTo(w * 0.41f, h * 0.24f) }
        val p2 = Path().apply { moveTo(w * 0.34f, h * 0.36f); lineTo(w * 0.28f, h * 0.32f); lineTo(w * 0.36f, h * 0.38f) }
        val p3 = Path().apply { moveTo(w * 0.28f, h * 0.48f); lineTo(w * 0.20f, h * 0.46f); lineTo(w * 0.29f, h * 0.54f) }
        scope.drawPath(p1, color, style = stroke)
        scope.drawPath(p2, color, style = stroke)
        scope.drawPath(p3, color, style = stroke)

        // Cute little hands
        scope.drawLine(color, Offset(w * 0.62f, h * 0.52f), Offset(w * 0.69f, h * 0.50f), strokeWidth = stroke.width)
        scope.drawLine(color, Offset(w * 0.69f, h * 0.50f), Offset(w * 0.67f, h * 0.54f), strokeWidth = stroke.width)
    }

    private fun drawCake(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        val cx = w * 0.5f

        // Candle
        scope.drawRect(color, topLeft = Offset(cx - w * 0.02f, h * 0.22f), size = androidx.compose.ui.geometry.Size(w * 0.04f, h * 0.12f), style = stroke)
        // Flame
        val flame = Path().apply {
            moveTo(cx, h * 0.14f)
            cubicTo(cx - w * 0.035f, h * 0.18f, cx - w * 0.035f, h * 0.22f, cx, h * 0.22f)
            cubicTo(cx + w * 0.035f, h * 0.22f, cx + w * 0.035f, h * 0.18f, cx, h * 0.14f)
        }
        scope.drawPath(flame, color, style = stroke)

        // Frosting top
        val frosting = Path().apply {
            moveTo(cx - w * 0.32f, h * 0.44f)
            cubicTo(cx - w * 0.32f, h * 0.30f, cx + w * 0.32f, h * 0.30f, cx + w * 0.32f, h * 0.44f)
            // scalloped bottom
            for (i in 5 downTo 0) {
                val segX = cx - w * 0.32f + (w * 0.64f) * (i.toFloat() / 6f)
                val prevX = cx - w * 0.32f + (w * 0.64f) * ((i + 1).toFloat() / 6f)
                cubicTo(prevX - w * 0.02f, h * 0.48f, segX + w * 0.02f, h * 0.48f, segX, h * 0.44f)
            }
            close()
        }
        scope.drawPath(frosting, color, style = stroke)

        // Cupcake base
        val base = Path().apply {
            moveTo(cx - w * 0.28f, h * 0.48f)
            lineTo(cx - w * 0.20f, h * 0.78f)
            lineTo(cx + w * 0.20f, h * 0.78f)
            lineTo(cx + w * 0.28f, h * 0.48f)
            close()
        }
        scope.drawPath(base, color, style = stroke)

        // Base flutes
        scope.drawLine(color, Offset(cx - w * 0.12f, h * 0.48f), Offset(cx - w * 0.08f, h * 0.78f), strokeWidth = stroke.width)
        scope.drawLine(color, Offset(cx, h * 0.48f), Offset(cx, h * 0.78f), strokeWidth = stroke.width)
        scope.drawLine(color, Offset(cx + w * 0.12f, h * 0.48f), Offset(cx + w * 0.08f, h * 0.78f), strokeWidth = stroke.width)

        // Sprinkles on frosting
        scope.drawCircle(color, radius = w * 0.015f, center = Offset(cx - w * 0.14f, h * 0.38f))
        scope.drawCircle(color, radius = w * 0.015f, center = Offset(cx + w * 0.12f, h * 0.36f))
        scope.drawCircle(color, radius = w * 0.015f, center = Offset(cx, h * 0.41f))
    }

    private fun drawRocket(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        val cx = w * 0.5f

        // Rocket cone & body
        val rocket = Path().apply {
            moveTo(cx, h * 0.15f)
            cubicTo(cx - w * 0.20f, h * 0.35f, cx - w * 0.18f, h * 0.60f, cx - w * 0.18f, h * 0.68f)
            lineTo(cx + w * 0.18f, h * 0.68f)
            cubicTo(cx + w * 0.18f, h * 0.60f, cx + w * 0.20f, h * 0.35f, cx, h * 0.15f)
            close()
        }
        scope.drawPath(rocket, color, style = stroke)

        // Porthole window
        scope.drawCircle(color, radius = w * 0.08f, center = Offset(cx, h * 0.42f), style = stroke)
        scope.drawCircle(color, radius = w * 0.05f, center = Offset(cx, h * 0.42f), style = stroke)

        // Left fin
        val leftFin = Path().apply {
            moveTo(cx - w * 0.18f, h * 0.52f)
            lineTo(cx - w * 0.34f, h * 0.68f)
            lineTo(cx - w * 0.18f, h * 0.66f)
            close()
        }
        scope.drawPath(leftFin, color, style = stroke)

        // Right fin
        val rightFin = Path().apply {
            moveTo(cx + w * 0.18f, h * 0.52f)
            lineTo(cx + w * 0.34f, h * 0.68f)
            lineTo(cx + w * 0.18f, h * 0.66f)
            close()
        }
        scope.drawPath(rightFin, color, style = stroke)

        // Exhaust flame
        val flame = Path().apply {
            moveTo(cx - w * 0.10f, h * 0.68f)
            lineTo(cx - w * 0.14f, h * 0.82f)
            lineTo(cx - w * 0.04f, h * 0.76f)
            lineTo(cx, h * 0.88f)
            lineTo(cx + w * 0.04f, h * 0.76f)
            lineTo(cx + w * 0.14f, h * 0.82f)
            lineTo(cx + w * 0.10f, h * 0.68f)
            close()
        }
        scope.drawPath(flame, color, style = stroke)
    }
}
