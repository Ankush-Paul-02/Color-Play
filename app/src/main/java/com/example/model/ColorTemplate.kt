package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

data class ColorTemplate(
    val id: String,
    val name: String,
    val category: String,
    val emoji: String,
    val difficulty: String, // "Starter", "Explorer", "Master", "Creative"
    val level: Int, // 1 = Starter, 2 = Explorer, 3 = Master, 0 = Free
    val levelName: String,
    val starsRequired: Int,
    val defaultTitle: String
)

object TemplateRegistry {
    val templates = listOf(
        // LEVEL 1: STARTER (0 stars required - Toddler & Preschool friendly)
        ColorTemplate(
            id = "star",
            name = "Happy Star",
            category = "Sky",
            emoji = "⭐",
            difficulty = "Starter",
            level = 1,
            levelName = "Level 1: Starter",
            starsRequired = 0,
            defaultTitle = "My Shining Star"
        ),
        ColorTemplate(
            id = "sun",
            name = "Morning Sun",
            category = "Nature",
            emoji = "☀️",
            difficulty = "Starter",
            level = 1,
            levelName = "Level 1: Starter",
            starsRequired = 0,
            defaultTitle = "Warm Sunny Day"
        ),
        ColorTemplate(
            id = "apple",
            name = "Juicy Apple",
            category = "Food",
            emoji = "🍎",
            difficulty = "Starter",
            level = 1,
            levelName = "Level 1: Starter",
            starsRequired = 0,
            defaultTitle = "Crisp Red Apple"
        ),
        ColorTemplate(
            id = "butterfly",
            name = "Rainbow Butterfly",
            category = "Nature",
            emoji = "🦋",
            difficulty = "Starter",
            level = 1,
            levelName = "Level 1: Starter",
            starsRequired = 0,
            defaultTitle = "Magical Butterfly"
        ),

        // LEVEL 2: EXPLORER (2 stars required - Fun Animals & Treats)
        ColorTemplate(
            id = "fish",
            name = "Little Goldfish",
            category = "Ocean",
            emoji = "🐠",
            difficulty = "Explorer",
            level = 2,
            levelName = "Level 2: Explorer",
            starsRequired = 2,
            defaultTitle = "Underwater Fish"
        ),
        ColorTemplate(
            id = "puppy",
            name = "Playful Puppy",
            category = "Pets",
            emoji = "🐶",
            difficulty = "Explorer",
            level = 2,
            levelName = "Level 2: Explorer",
            starsRequired = 2,
            defaultTitle = "Cute Little Puppy"
        ),
        ColorTemplate(
            id = "cake",
            name = "Party Cupcake",
            category = "Treats",
            emoji = "🧁",
            difficulty = "Explorer",
            level = 2,
            levelName = "Level 2: Explorer",
            starsRequired = 2,
            defaultTitle = "Birthday Cupcake"
        ),
        ColorTemplate(
            id = "car",
            name = "Speedy Race Car",
            category = "Vehicles",
            emoji = "🚗",
            difficulty = "Explorer",
            level = 2,
            levelName = "Level 2: Explorer",
            starsRequired = 2,
            defaultTitle = "My Red Racecar"
        ),

        // LEVEL 3: MASTER (5 stars required - Detailed Adventures)
        ColorTemplate(
            id = "rooster",
            name = "Barnyard Rooster",
            category = "Farm Animals",
            emoji = "🐓",
            difficulty = "Master",
            level = 3,
            levelName = "Level 3: Master",
            starsRequired = 5,
            defaultTitle = "My Farm Rooster"
        ),
        ColorTemplate(
            id = "dino",
            name = "Friendly Dino",
            category = "Prehistoric",
            emoji = "🦖",
            difficulty = "Master",
            level = 3,
            levelName = "Level 3: Master",
            starsRequired = 5,
            defaultTitle = "Gentle Dinosaur"
        ),
        ColorTemplate(
            id = "rocket",
            name = "Cosmic Rocket",
            category = "Space",
            emoji = "🚀",
            difficulty = "Master",
            level = 3,
            levelName = "Level 3: Master",
            starsRequired = 5,
            defaultTitle = "Moon Rocket"
        ),
        ColorTemplate(
            id = "castle",
            name = "Magic Castle",
            category = "Fantasy",
            emoji = "🏰",
            difficulty = "Master",
            level = 3,
            levelName = "Level 3: Master",
            starsRequired = 5,
            defaultTitle = "Fairytale Castle"
        ),

        // FREE DRAWING CANVAS (All Levels)
        ColorTemplate(
            id = "free_draw",
            name = "Magic Blank Canvas",
            category = "Creative",
            emoji = "🎨",
            difficulty = "All Ages",
            level = 0,
            levelName = "Free Draw",
            starsRequired = 0,
            defaultTitle = "My Masterpiece"
        ),
        // PHOTO TO LINE ART COLORING
        ColorTemplate(
            id = "photo_art",
            name = "Magic Photo Art",
            category = "Camera",
            emoji = "📸",
            difficulty = "All Ages",
            level = 0,
            levelName = "Photo Art",
            starsRequired = 0,
            defaultTitle = "My Photo Coloring"
        )
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
            "star" -> drawStar(scope, w, h, outlineColor, stroke)
            "sun" -> drawSun(scope, w, h, outlineColor, stroke)
            "apple" -> drawApple(scope, w, h, outlineColor, stroke)
            "butterfly" -> drawButterfly(scope, w, h, outlineColor, stroke)
            "fish" -> drawFish(scope, w, h, outlineColor, stroke)
            "puppy" -> drawPuppy(scope, w, h, outlineColor, stroke)
            "cake" -> drawCake(scope, w, h, outlineColor, stroke)
            "car" -> drawCar(scope, w, h, outlineColor, stroke)
            "rooster" -> drawRooster(scope, w, h, outlineColor, stroke)
            "dino" -> drawDino(scope, w, h, outlineColor, stroke)
            "rocket" -> drawRocket(scope, w, h, outlineColor, stroke)
            "castle" -> drawCastle(scope, w, h, outlineColor, stroke)
            else -> { /* Blank Canvas */ }
        }
    }

    // -------------------------------------------------------------
    // LEVEL 1 DRAWINGS
    // -------------------------------------------------------------

    private fun drawStar(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        val cx = w * 0.5f
        val cy = h * 0.48f
        val outerR = w * 0.36f
        val innerR = w * 0.17f

        val starPath = Path()
        val totalPoints = 10
        for (i in 0 until totalPoints) {
            val angle = -Math.PI / 2 + i * (2 * Math.PI / totalPoints)
            val r = if (i % 2 == 0) outerR else innerR
            val px = (cx + r * cos(angle)).toFloat()
            val py = (cy + r * sin(angle)).toFloat()
            if (i == 0) starPath.moveTo(px, py) else starPath.lineTo(px, py)
        }
        starPath.close()
        scope.drawPath(starPath, color, style = stroke)

        // Cute smiling face on star
        scope.drawCircle(color, radius = w * 0.024f, center = Offset(cx - w * 0.08f, cy - h * 0.04f))
        scope.drawCircle(color, radius = w * 0.024f, center = Offset(cx + w * 0.08f, cy - h * 0.04f))

        // Rosy cheeks
        scope.drawCircle(Color(0xFFFF7675).copy(alpha = 0.5f), radius = w * 0.03f, center = Offset(cx - w * 0.11f, cy + h * 0.02f))
        scope.drawCircle(Color(0xFFFF7675).copy(alpha = 0.5f), radius = w * 0.03f, center = Offset(cx + w * 0.11f, cy + h * 0.02f))

        // Cheerful smile
        val smile = Path().apply {
            moveTo(cx - w * 0.06f, cy + h * 0.04f)
            quadraticTo(cx, cy + h * 0.11f, cx + w * 0.06f, cy + h * 0.04f)
        }
        scope.drawPath(smile, color, style = stroke)
    }

    private fun drawSun(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        val cx = w * 0.45f
        val cy = h * 0.42f
        val r = w * 0.20f

        // Sun disc
        scope.drawCircle(color, radius = r, center = Offset(cx, cy), style = stroke)

        // 8 radiating rays
        val rayCount = 8
        for (i in 0 until rayCount) {
            val angle = i * (2 * Math.PI / rayCount)
            val startX = (cx + (r + w * 0.04f) * cos(angle)).toFloat()
            val startY = (cy + (r + w * 0.04f) * sin(angle)).toFloat()
            val endX = (cx + (r + w * 0.14f) * cos(angle)).toFloat()
            val endY = (cy + (r + w * 0.14f) * sin(angle)).toFloat()
            scope.drawLine(color, Offset(startX, startY), Offset(endX, endY), strokeWidth = stroke.width * 1.2f)
        }

        // Happy sun eyes & smile
        scope.drawCircle(color, radius = w * 0.022f, center = Offset(cx - w * 0.07f, cy - h * 0.03f))
        scope.drawCircle(color, radius = w * 0.022f, center = Offset(cx + w * 0.07f, cy - h * 0.03f))
        val smile = Path().apply {
            moveTo(cx - w * 0.06f, cy + h * 0.03f)
            quadraticTo(cx, cy + h * 0.09f, cx + w * 0.06f, cy + h * 0.03f)
        }
        scope.drawPath(smile, color, style = stroke)

        // Fluffy cloud floating nearby
        val cloud = Path().apply {
            moveTo(w * 0.40f, h * 0.72f)
            cubicTo(w * 0.35f, h * 0.62f, w * 0.50f, h * 0.58f, w * 0.58f, h * 0.65f)
            cubicTo(w * 0.65f, h * 0.55f, w * 0.82f, h * 0.58f, w * 0.84f, h * 0.68f)
            cubicTo(w * 0.92f, h * 0.68f, w * 0.95f, h * 0.78f, w * 0.88f, h * 0.82f)
            lineTo(w * 0.42f, h * 0.82f)
            close()
        }
        scope.drawPath(cloud, color.copy(alpha = 0.85f), style = stroke)
    }

    private fun drawApple(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        val cx = w * 0.5f
        val cy = h * 0.52f

        // Apple body with top and bottom indentations
        val apple = Path().apply {
            moveTo(cx, cy - h * 0.20f)
            // Left lobe
            cubicTo(cx - w * 0.15f, cy - h * 0.26f, cx - w * 0.38f, cy - h * 0.10f, cx - w * 0.36f, cy + h * 0.10f)
            cubicTo(cx - w * 0.34f, cy + h * 0.28f, cx - w * 0.15f, cy + h * 0.34f, cx, cy + h * 0.28f)
            // Right lobe
            cubicTo(cx + w * 0.15f, cy + h * 0.34f, cx + w * 0.34f, cy + h * 0.28f, cx + w * 0.36f, cy + h * 0.10f)
            cubicTo(cx + w * 0.38f, cy - h * 0.10f, cx + w * 0.15f, cy - h * 0.26f, cx, cy - h * 0.20f)
            close()
        }
        scope.drawPath(apple, color, style = stroke)

        // Apple stem
        val stem = Path().apply {
            moveTo(cx, cy - h * 0.20f)
            cubicTo(cx - w * 0.02f, cy - h * 0.28f, cx + w * 0.05f, cy - h * 0.36f, cx + w * 0.04f, cy - h * 0.38f)
        }
        scope.drawPath(stem, color, style = stroke)

        // Cute Leaf
        val leaf = Path().apply {
            moveTo(cx + w * 0.02f, cy - h * 0.26f)
            cubicTo(cx + w * 0.16f, cy - h * 0.35f, cx + w * 0.26f, cy - h * 0.30f, cx + w * 0.22f, cy - h * 0.20f)
            cubicTo(cx + w * 0.14f, cy - h * 0.18f, cx + w * 0.06f, cy - h * 0.22f, cx + w * 0.02f, cy - h * 0.26f)
            close()
        }
        scope.drawPath(leaf, color, style = stroke)
        scope.drawLine(color, Offset(cx + w * 0.02f, cy - h * 0.26f), Offset(cx + w * 0.22f, cy - h * 0.20f), strokeWidth = stroke.width * 0.6f)

        // Friendly face
        scope.drawCircle(color, radius = w * 0.02f, center = Offset(cx - w * 0.10f, cy))
        scope.drawCircle(color, radius = w * 0.02f, center = Offset(cx + w * 0.10f, cy))
        val smile = Path().apply {
            moveTo(cx - w * 0.06f, cy + h * 0.06f)
            quadraticTo(cx, cy + h * 0.12f, cx + w * 0.06f, cy + h * 0.06f)
        }
        scope.drawPath(smile, color, style = stroke)
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
            moveTo(cx - w * 0.04f, cy + h * 0.05f)
            cubicTo(cx - w * 0.32f, cy + h * 0.08f, cx - w * 0.38f, cy + h * 0.36f, cx - w * 0.20f, cy + h * 0.36f)
            cubicTo(cx - w * 0.08f, cy + h * 0.32f, cx - w * 0.04f, cy + h * 0.20f, cx - w * 0.04f, cy + h * 0.05f)
            close()
        }
        scope.drawPath(leftBottomWing, color, style = stroke)

        // Right lower wing
        val rightBottomWing = Path().apply {
            moveTo(cx + w * 0.04f, cy + h * 0.05f)
            cubicTo(cx + w * 0.32f, cy + h * 0.08f, cx + w * 0.38f, cy + h * 0.36f, cx + w * 0.20f, cy + h * 0.36f)
            cubicTo(cx + w * 0.08f, cy + h * 0.32f, cx + w * 0.04f, cy + h * 0.20f, cx + w * 0.04f, cy + h * 0.05f)
            close()
        }
        scope.drawPath(rightBottomWing, color, style = stroke)

        // Wing details
        scope.drawCircle(color, radius = w * 0.04f, center = Offset(cx - w * 0.25f, cy - h * 0.16f), style = stroke)
        scope.drawCircle(color, radius = w * 0.04f, center = Offset(cx + w * 0.25f, cy - h * 0.16f), style = stroke)
    }

    // -------------------------------------------------------------
    // LEVEL 2 DRAWINGS
    // -------------------------------------------------------------

    private fun drawFish(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        val cx = w * 0.45f
        val cy = h * 0.50f

        // Fish Body
        val body = Path().apply {
            moveTo(cx - w * 0.30f, cy)
            cubicTo(cx - w * 0.25f, cy - h * 0.25f, cx + w * 0.15f, cy - h * 0.25f, cx + w * 0.25f, cy)
            cubicTo(cx + w * 0.15f, cy + h * 0.25f, cx - w * 0.25f, cy + h * 0.25f, cx - w * 0.30f, cy)
            close()
        }
        scope.drawPath(body, color, style = stroke)

        // Tail Fin
        val tail = Path().apply {
            moveTo(cx - w * 0.28f, cy)
            lineTo(cx - w * 0.44f, cy - h * 0.18f)
            cubicTo(cx - w * 0.38f, cy, cx - w * 0.38f, cy, cx - w * 0.44f, cy + h * 0.18f)
            close()
        }
        scope.drawPath(tail, color, style = stroke)

        // Dorsal Top Fin
        val dorsal = Path().apply {
            moveTo(cx - w * 0.08f, cy - h * 0.18f)
            cubicTo(cx, cy - h * 0.32f, cx + w * 0.10f, cy - h * 0.30f, cx + w * 0.14f, cy - h * 0.14f)
        }
        scope.drawPath(dorsal, color, style = stroke)

        // Ventral Bottom Fin
        val ventral = Path().apply {
            moveTo(cx, cy + h * 0.17f)
            cubicTo(cx + w * 0.04f, cy + h * 0.28f, cx + w * 0.10f, cy + h * 0.26f, cx + w * 0.12f, cy + h * 0.14f)
        }
        scope.drawPath(ventral, color, style = stroke)

        // Big friendly eye & smile
        scope.drawCircle(color, radius = w * 0.035f, center = Offset(cx + w * 0.15f, cy - h * 0.06f), style = stroke)
        scope.drawCircle(color, radius = w * 0.016f, center = Offset(cx + w * 0.16f, cy - h * 0.06f))

        // Cute gills arc
        val gill = Path().apply {
            moveTo(cx + w * 0.04f, cy - h * 0.12f)
            cubicTo(cx + w * 0.08f, cy, cx + w * 0.08f, cy, cx + w * 0.04f, cy + h * 0.12f)
        }
        scope.drawPath(gill, color, style = stroke)

        // Rising water bubbles
        scope.drawCircle(color, radius = w * 0.020f, center = Offset(cx + w * 0.32f, cy - h * 0.18f), style = stroke)
        scope.drawCircle(color, radius = w * 0.014f, center = Offset(cx + w * 0.36f, cy - h * 0.28f), style = stroke)
        scope.drawCircle(color, radius = w * 0.009f, center = Offset(cx + w * 0.40f, cy - h * 0.36f), style = stroke)
    }

    private fun drawPuppy(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        val cx = w * 0.5f
        val cy = h * 0.44f

        // Head
        val head = Path().apply {
            addOval(Rect(cx - w * 0.22f, cy - h * 0.22f, cx + w * 0.22f, cy + h * 0.22f))
        }
        scope.drawPath(head, color, style = stroke)

        // Left ear
        val leftEar = Path().apply {
            moveTo(cx - w * 0.18f, cy - h * 0.14f)
            cubicTo(cx - w * 0.40f, cy - h * 0.08f, cx - w * 0.40f, cy + h * 0.22f, cx - w * 0.22f, cy + h * 0.18f)
            close()
        }
        scope.drawPath(leftEar, color, style = stroke)

        // Right ear
        val rightEar = Path().apply {
            moveTo(cx + w * 0.18f, cy - h * 0.14f)
            cubicTo(cx + w * 0.40f, cy - h * 0.08f, cx + w * 0.40f, cy + h * 0.22f, cx + w * 0.22f, cy + h * 0.18f)
            close()
        }
        scope.drawPath(rightEar, color, style = stroke)

        // Eyes
        scope.drawCircle(color, radius = w * 0.035f, center = Offset(cx - w * 0.09f, cy - h * 0.04f))
        scope.drawCircle(color, radius = w * 0.035f, center = Offset(cx + w * 0.09f, cy - h * 0.04f))
        scope.drawCircle(Color.White, radius = w * 0.012f, center = Offset(cx - w * 0.08f, cy - h * 0.05f))
        scope.drawCircle(Color.White, radius = w * 0.012f, center = Offset(cx + w * 0.10f, cy - h * 0.05f))

        // Cute Nose
        val nose = Path().apply {
            moveTo(cx - w * 0.045f, cy + h * 0.06f)
            lineTo(cx + w * 0.045f, cy + h * 0.06f)
            lineTo(cx, cy + h * 0.10f)
            close()
        }
        scope.drawPath(nose, color)

        // Mouth & tongue
        val mouth = Path().apply {
            moveTo(cx, cy + h * 0.10f)
            lineTo(cx, cy + h * 0.13f)
            moveTo(cx - w * 0.06f, cy + h * 0.13f)
            cubicTo(cx - w * 0.03f, cy + h * 0.16f, cx, cy + h * 0.13f, cx, cy + h * 0.13f)
            cubicTo(cx, cy + h * 0.13f, cx + w * 0.03f, cy + h * 0.16f, cx + w * 0.06f, cy + h * 0.13f)
        }
        scope.drawPath(mouth, color, style = stroke)

        // Puppy body & collar
        val body = Path().apply {
            moveTo(cx - w * 0.18f, cy + h * 0.22f)
            lineTo(cx - w * 0.22f, h * 0.82f)
            lineTo(cx + w * 0.22f, h * 0.82f)
            lineTo(cx + w * 0.18f, cy + h * 0.22f)
        }
        scope.drawPath(body, color, style = stroke)

        // Collar with star tag
        scope.drawLine(color, Offset(cx - w * 0.15f, cy + h * 0.25f), Offset(cx + w * 0.15f, cy + h * 0.25f), strokeWidth = stroke.width * 1.5f)
        scope.drawCircle(color, radius = w * 0.025f, center = Offset(cx, cy + h * 0.28f))
    }

    private fun drawCake(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        val cx = w * 0.5f

        // Cherry on top
        scope.drawCircle(color, radius = w * 0.05f, center = Offset(cx, h * 0.24f))
        val stem = Path().apply {
            moveTo(cx, h * 0.20f)
            cubicTo(cx + w * 0.08f, h * 0.15f, cx + w * 0.05f, h * 0.12f, cx + w * 0.10f, h * 0.10f)
        }
        scope.drawPath(stem, color, style = stroke)

        // Cupcake Frosting
        val frosting = Path().apply {
            moveTo(cx - w * 0.32f, h * 0.44f)
            cubicTo(cx - w * 0.32f, h * 0.30f, cx + w * 0.32f, h * 0.30f, cx + w * 0.32f, h * 0.44f)
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

        // Sprinkles
        scope.drawCircle(color, radius = w * 0.015f, center = Offset(cx - w * 0.14f, h * 0.38f))
        scope.drawCircle(color, radius = w * 0.015f, center = Offset(cx + w * 0.12f, h * 0.36f))
        scope.drawCircle(color, radius = w * 0.015f, center = Offset(cx, h * 0.41f))
    }

    private fun drawCar(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        // Car chassis & body
        val carBody = Path().apply {
            moveTo(w * 0.10f, h * 0.62f)
            lineTo(w * 0.20f, h * 0.48f)
            lineTo(w * 0.38f, h * 0.45f)
            lineTo(w * 0.48f, h * 0.32f)
            lineTo(w * 0.72f, h * 0.32f)
            lineTo(w * 0.82f, h * 0.45f)
            lineTo(w * 0.92f, h * 0.52f)
            lineTo(w * 0.94f, h * 0.65f)
            lineTo(w * 0.85f, h * 0.65f)
            cubicTo(w * 0.85f, h * 0.55f, w * 0.71f, h * 0.55f, w * 0.71f, h * 0.65f)
            lineTo(w * 0.35f, h * 0.65f)
            cubicTo(w * 0.35f, h * 0.55f, w * 0.21f, h * 0.55f, w * 0.21f, h * 0.65f)
            lineTo(w * 0.10f, h * 0.65f)
            close()
        }
        scope.drawPath(carBody, color, style = stroke)

        // Windows
        val window = Path().apply {
            moveTo(w * 0.49f, h * 0.35f)
            lineTo(w * 0.70f, h * 0.35f)
            lineTo(w * 0.78f, h * 0.45f)
            lineTo(w * 0.42f, h * 0.45f)
            close()
        }
        scope.drawPath(window, color, style = stroke)

        // Wheels
        scope.drawCircle(color, radius = w * 0.08f, center = Offset(w * 0.28f, h * 0.65f), style = stroke)
        scope.drawCircle(color, radius = w * 0.035f, center = Offset(w * 0.28f, h * 0.65f))
        scope.drawCircle(color, radius = w * 0.08f, center = Offset(w * 0.78f, h * 0.65f), style = stroke)
        scope.drawCircle(color, radius = w * 0.035f, center = Offset(w * 0.78f, h * 0.65f))

        // Headlight & spoiler
        scope.drawCircle(color, radius = w * 0.025f, center = Offset(w * 0.90f, h * 0.56f))
        scope.drawLine(color, Offset(w * 0.14f, h * 0.48f), Offset(w * 0.10f, h * 0.42f), strokeWidth = stroke.width)
        scope.drawLine(color, Offset(w * 0.08f, h * 0.42f), Offset(w * 0.18f, h * 0.42f), strokeWidth = stroke.width * 1.5f)
    }

    // -------------------------------------------------------------
    // LEVEL 3 DRAWINGS
    // -------------------------------------------------------------

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
            cubicTo(w * 0.58f, h * 0.30f, w * 0.72f, h * 0.26f, w * 0.78f, h * 0.35f)
            lineTo(w * 0.88f, h * 0.38f)
            lineTo(w * 0.78f, h * 0.44f)
            cubicTo(w * 0.85f, h * 0.60f, w * 0.75f, h * 0.75f, w * 0.55f, h * 0.75f)
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
        scope.drawLine(color, Offset(w * 0.42f, h * 0.74f), Offset(w * 0.46f, h * 0.75f), strokeWidth = stroke.width)
        scope.drawLine(color, Offset(w * 0.46f, h * 0.75f), Offset(w * 0.42f, h * 0.77f), strokeWidth = stroke.width)
    }

    private fun drawDino(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        val dino = Path().apply {
            moveTo(w * 0.38f, h * 0.28f)
            // Head
            cubicTo(w * 0.42f, h * 0.16f, w * 0.60f, h * 0.18f, w * 0.65f, h * 0.28f)
            lineTo(w * 0.70f, h * 0.30f)
            lineTo(w * 0.62f, h * 0.36f)
            // Neck down to back
            cubicTo(w * 0.52f, h * 0.42f, w * 0.55f, h * 0.48f, w * 0.68f, h * 0.52f)
            // Back & tail
            cubicTo(w * 0.82f, h * 0.55f, w * 0.90f, h * 0.50f, w * 0.95f, h * 0.42f)
            cubicTo(w * 0.90f, h * 0.68f, w * 0.78f, h * 0.76f, w * 0.62f, h * 0.76f)
            // Back leg
            lineTo(w * 0.62f, h * 0.88f)
            lineTo(w * 0.54f, h * 0.88f)
            lineTo(w * 0.54f, h * 0.76f)
            // Belly
            lineTo(w * 0.40f, h * 0.76f)
            // Front leg
            lineTo(w * 0.40f, h * 0.88f)
            lineTo(w * 0.32f, h * 0.88f)
            lineTo(w * 0.32f, h * 0.72f)
            // Chest & front neck
            cubicTo(w * 0.28f, h * 0.60f, w * 0.30f, h * 0.42f, w * 0.38f, h * 0.28f)
            close()
        }
        scope.drawPath(dino, color, style = stroke)

        // Dino eye
        scope.drawCircle(color, radius = w * 0.022f, center = Offset(w * 0.55f, h * 0.24f))
        scope.drawCircle(Color.White, radius = w * 0.008f, center = Offset(w * 0.56f, h * 0.23f))

        // Dino friendly back plates / scales
        for (i in 0..4) {
            val plateX = w * 0.55f + i * (w * 0.06f)
            val plateY = h * 0.42f + i * (h * 0.025f)
            val plate = Path().apply {
                moveTo(plateX, plateY)
                lineTo(plateX + w * 0.02f, plateY - h * 0.05f)
                lineTo(plateX + w * 0.04f, plateY)
            }
            scope.drawPath(plate, color, style = stroke)
        }
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

    private fun drawCastle(scope: DrawScope, w: Float, h: Float, color: Color, stroke: Stroke) {
        val cx = w * 0.5f

        // Center Main Keep Tower
        val mainTower = Path().apply {
            moveTo(cx - w * 0.16f, h * 0.36f)
            lineTo(cx + w * 0.16f, h * 0.36f)
            lineTo(cx + w * 0.16f, h * 0.84f)
            lineTo(cx - w * 0.16f, h * 0.84f)
            close()
        }
        scope.drawPath(mainTower, color, style = stroke)

        // Main Center Roof Cone
        val mainRoof = Path().apply {
            moveTo(cx - w * 0.18f, h * 0.36f)
            lineTo(cx, h * 0.16f)
            lineTo(cx + w * 0.18f, h * 0.36f)
            close()
        }
        scope.drawPath(mainRoof, color, style = stroke)

        // Flag on main roof
        scope.drawLine(color, Offset(cx, h * 0.16f), Offset(cx, h * 0.08f), strokeWidth = stroke.width)
        val flag = Path().apply {
            moveTo(cx, h * 0.08f)
            lineTo(cx + w * 0.10f, h * 0.12f)
            lineTo(cx, h * 0.16f)
            close()
        }
        scope.drawPath(flag, color, style = stroke)

        // Left Tower
        val leftTower = Path().apply {
            moveTo(cx - w * 0.38f, h * 0.46f)
            lineTo(cx - w * 0.20f, h * 0.46f)
            lineTo(cx - w * 0.20f, h * 0.84f)
            lineTo(cx - w * 0.38f, h * 0.84f)
            close()
        }
        scope.drawPath(leftTower, color, style = stroke)

        // Left Tower Roof
        val leftRoof = Path().apply {
            moveTo(cx - w * 0.40f, h * 0.46f)
            lineTo(cx - w * 0.29f, h * 0.28f)
            lineTo(cx - w * 0.18f, h * 0.46f)
            close()
        }
        scope.drawPath(leftRoof, color, style = stroke)

        // Right Tower
        val rightTower = Path().apply {
            moveTo(cx + w * 0.20f, h * 0.46f)
            lineTo(cx + w * 0.38f, h * 0.46f)
            lineTo(cx + w * 0.38f, h * 0.84f)
            lineTo(cx + w * 0.20f, h * 0.84f)
            close()
        }
        scope.drawPath(rightTower, color, style = stroke)

        // Right Tower Roof
        val rightRoof = Path().apply {
            moveTo(cx + w * 0.18f, h * 0.46f)
            lineTo(cx + w * 0.29f, h * 0.28f)
            lineTo(cx + w * 0.40f, h * 0.46f)
            close()
        }
        scope.drawPath(rightRoof, color, style = stroke)

        // Castle Gate Archway Door
        val gate = Path().apply {
            moveTo(cx - w * 0.08f, h * 0.84f)
            lineTo(cx - w * 0.08f, h * 0.65f)
            cubicTo(cx - w * 0.08f, h * 0.58f, cx + w * 0.08f, h * 0.58f, cx + w * 0.08f, h * 0.65f)
            lineTo(cx + w * 0.08f, h * 0.84f)
        }
        scope.drawPath(gate, color, style = stroke)

        // Arched Keep Window
        val window = Path().apply {
            moveTo(cx - w * 0.04f, h * 0.48f)
            lineTo(cx - w * 0.04f, h * 0.44f)
            cubicTo(cx - w * 0.04f, h * 0.41f, cx + w * 0.04f, h * 0.41f, cx + w * 0.04f, h * 0.44f)
            lineTo(cx + w * 0.04f, h * 0.48f)
            close()
        }
        scope.drawPath(window, color, style = stroke)
    }
}
