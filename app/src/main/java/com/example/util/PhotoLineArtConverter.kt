package com.example.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlin.math.sqrt

enum class LineArtStyle(val displayName: String, val emoji: String, val thresholdFactor: Float) {
    BALANCED("Coloring Book", "🖍️", 1.0f),
    BOLD("Bold & Simple", "✏️", 1.35f),
    DETAILED("Detailed Sketch", "✨", 0.75f)
}

object PhotoLineArtConverter {

    /**
     * Converts a camera or gallery photo into a clean, kid-friendly black-and-white
     * coloring page line art drawing.
     *
     * @param source The input photo Bitmap
     * @param detailLevel 0.0f (minimal lines) to 1.0f (very detailed lines)
     * @param style Coloring book style preset
     * @param thickness Line thickness (1 = fine, 2 = standard, 3 = thick outlines)
     */
    fun convertToLineArt(
        source: Bitmap,
        detailLevel: Float = 0.5f,
        style: LineArtStyle = LineArtStyle.BALANCED,
        thickness: Int = 2
    ): Bitmap {
        // 1. Scale down safely maintaining aspect ratio (e.g., max 640x640 for rapid response and clean strokes)
        val maxDim = 640
        val scale = minOf(maxDim.toFloat() / source.width, maxDim.toFloat() / source.height, 1.0f)
        val targetWidth = (source.width * scale).toInt().coerceAtLeast(80)
        val targetHeight = (source.height * scale).toInt().coerceAtLeast(80)

        val scaled = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
        val w = scaled.width
        val h = scaled.height

        val pixels = IntArray(w * h)
        scaled.getPixels(pixels, 0, w, 0, 0, w, h)

        // 2. Grayscale conversion using human perception luminance weights
        val gray = IntArray(w * h)
        for (i in pixels.indices) {
            val c = pixels[i]
            val r = (c shr 16) and 0xFF
            val g = (c shr 8) and 0xFF
            val b = c and 0xFF
            gray[i] = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
        }

        // 3. Smooth with a 3x3 box blur to filter camera noise
        val smoothed = IntArray(w * h)
        for (y in 1 until h - 1) {
            val rowOffset = y * w
            for (x in 1 until w - 1) {
                var sum = 0
                for (dy in -1..1) {
                    val rOff = (y + dy) * w
                    for (dx in -1..1) {
                        sum += gray[rOff + (x + dx)]
                    }
                }
                smoothed[rowOffset + x] = sum / 9
            }
        }

        // Copy borders
        for (x in 0 until w) {
            smoothed[x] = gray[x]
            smoothed[(h - 1) * w + x] = gray[(h - 1) * w + x]
        }
        for (y in 0 until h) {
            smoothed[y * w] = gray[y * w]
            smoothed[y * w + (w - 1)] = gray[y * w + (w - 1)]
        }

        // 4. Sobel Gradient Magnitude Edge Detection
        // Base threshold dynamically scaled by detailLevel (0..1) and style preset
        val baseThreshold = (58.0f - (detailLevel * 32.0f)) * style.thresholdFactor
        val threshold = baseThreshold.toInt().coerceIn(16, 75)

        val outputPixels = IntArray(w * h) { Color.WHITE }

        for (y in 1 until h - 1) {
            val rowOffset = y * w
            for (x in 1 until w - 1) {
                val p00 = smoothed[(y - 1) * w + (x - 1)]
                val p01 = smoothed[(y - 1) * w + x]
                val p02 = smoothed[(y - 1) * w + (x + 1)]
                val p10 = smoothed[rowOffset + (x - 1)]
                val p12 = smoothed[rowOffset + (x + 1)]
                val p20 = smoothed[(y + 1) * w + (x - 1)]
                val p21 = smoothed[(y + 1) * w + x]
                val p22 = smoothed[(y + 1) * w + (x + 1)]

                val gx = (p02 + 2 * p12 + p22) - (p00 + 2 * p10 + p20)
                val gy = (p20 + 2 * p21 + p22) - (p00 + 2 * p01 + p02)
                val magnitude = sqrt((gx * gx + gy * gy).toDouble()).toInt()

                // Outline line color: dark charcoal/black (0xFF202124)
                if (magnitude > threshold) {
                    outputPixels[rowOffset + x] = 0xFF202124.toInt()
                } else {
                    outputPixels[rowOffset + x] = Color.WHITE
                }
            }
        }

        // 5. Morphological Dilation for crisp, kid-friendly coloring line thickness
        val finalPixels = if (thickness > 1) {
            val dilated = outputPixels.clone()
            val rad = thickness - 1
            for (y in rad until h - rad) {
                val rowOffset = y * w
                for (x in rad until w - rad) {
                    if (outputPixels[rowOffset + x] == 0xFF202124.toInt()) {
                        for (dy in -rad..rad) {
                            val targetRow = (y + dy) * w
                            for (dx in -rad..rad) {
                                dilated[targetRow + (x + dx)] = 0xFF202124.toInt()
                            }
                        }
                    }
                }
            }
            dilated
        } else {
            outputPixels
        }

        val outBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        outBitmap.setPixels(finalPixels, 0, w, 0, 0, w, h)
        if (scaled != source) scaled.recycle()
        return outBitmap
    }

    /**
     * Creates a transparent overlay where white paper is 100% transparent and
     * black lines remain solid, allowing children's coloring to show underneath
     * while keeping the drawing outlines crisp on top!
     */
    fun createTransparentLineOverlay(lineArtBitmap: Bitmap): Bitmap {
        val w = lineArtBitmap.width
        val h = lineArtBitmap.height
        val pixels = IntArray(w * h)
        lineArtBitmap.getPixels(pixels, 0, w, 0, 0, w, h)

        for (i in pixels.indices) {
            val c = pixels[i]
            val r = (c shr 16) and 0xFF
            val g = (c shr 8) and 0xFF
            val b = c and 0xFF

            // If it's a dark outline
            if (r < 110 && g < 110 && b < 110) {
                pixels[i] = (0xFF shl 24) or (0x20 shl 16) or (0x21 shl 8) or 0x24
            } else {
                pixels[i] = Color.TRANSPARENT
            }
        }

        val transparentBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        transparentBitmap.setPixels(pixels, 0, w, 0, 0, w, h)
        return transparentBitmap
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos)
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
    }

    fun base64ToBitmap(base64: String): Bitmap? {
        return try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Built-in colorful sample photos kids can test immediately
     */
    fun createSamplePhoto(type: String): Bitmap {
        val w = 500
        val h = 500
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        when (type) {
            "puppy" -> {
                // Background
                paint.color = Color.rgb(176, 224, 230) // Powder blue
                canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

                // Puppy Head
                paint.color = Color.rgb(205, 133, 63) // Peru brown
                canvas.drawCircle(250f, 250f, 130f, paint)

                // Ears
                paint.color = Color.rgb(139, 69, 19) // Saddle brown
                canvas.drawOval(RectF(110f, 130f, 190f, 280f), paint)
                canvas.drawOval(RectF(310f, 130f, 390f, 280f), paint)

                // Muzzle
                paint.color = Color.rgb(245, 222, 179)
                canvas.drawOval(RectF(200f, 240f, 300f, 320f), paint)

                // Nose & Eyes
                paint.color = Color.rgb(30, 30, 30)
                canvas.drawOval(RectF(230f, 255f, 270f, 285f), paint)
                canvas.drawCircle(200f, 220f, 16f, paint)
                canvas.drawCircle(300f, 220f, 16f, paint)

                // Eye highlights
                paint.color = Color.WHITE
                canvas.drawCircle(204f, 216f, 6f, paint)
                canvas.drawCircle(304f, 216f, 6f, paint)

                // Tongue
                paint.color = Color.rgb(255, 105, 180)
                canvas.drawOval(RectF(235f, 305f, 265f, 340f), paint)
            }
            "teddy" -> {
                // Background
                paint.color = Color.rgb(255, 239, 213) // Papaya whip
                canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

                // Teddy Body
                paint.color = Color.rgb(180, 115, 60)
                canvas.drawCircle(250f, 330f, 110f, paint)

                // Teddy Head
                canvas.drawCircle(250f, 200f, 95f, paint)

                // Ears
                paint.color = Color.rgb(150, 90, 40)
                canvas.drawCircle(170f, 125f, 38f, paint)
                canvas.drawCircle(330f, 125f, 38f, paint)
                paint.color = Color.rgb(220, 170, 130)
                canvas.drawCircle(170f, 125f, 20f, paint)
                canvas.drawCircle(330f, 125f, 20f, paint)

                // Snout
                paint.color = Color.rgb(230, 190, 150)
                canvas.drawOval(RectF(205f, 190f, 295f, 255f), paint)

                // Nose & Eyes
                paint.color = Color.rgb(40, 25, 20)
                canvas.drawOval(RectF(232f, 200f, 268f, 224f), paint)
                canvas.drawCircle(210f, 175f, 12f, paint)
                canvas.drawCircle(290f, 175f, 12f, paint)

                // Bowtie
                paint.color = Color.rgb(255, 75, 75)
                val bowPath = Path().apply {
                    moveTo(250f, 280f)
                    lineTo(200f, 260f)
                    lineTo(200f, 300f)
                    close()
                    moveTo(250f, 280f)
                    lineTo(300f, 260f)
                    lineTo(300f, 300f)
                    close()
                }
                canvas.drawPath(bowPath, paint)
                paint.color = Color.rgb(220, 50, 50)
                canvas.drawCircle(250f, 280f, 14f, paint)
            }
            else -> {
                // Flower & Butterfly
                paint.color = Color.rgb(230, 245, 255)
                canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

                // Flower Stem & Leaves
                paint.color = Color.rgb(46, 204, 113)
                paint.strokeWidth = 14f
                canvas.drawLine(250f, 250f, 250f, 430f, paint)

                paint.style = Paint.Style.FILL
                canvas.drawOval(RectF(250f, 320f, 330f, 360f), paint)
                canvas.drawOval(RectF(170f, 350f, 250f, 390f), paint)

                // Flower Petals
                paint.color = Color.rgb(255, 75, 120)
                for (angle in 0 until 360 step 60) {
                    val rad = Math.toRadians(angle.toDouble())
                    val px = 250f + (kotlin.math.cos(rad) * 65f).toFloat()
                    val py = 200f + (kotlin.math.sin(rad) * 65f).toFloat()
                    canvas.drawCircle(px, py, 42f, paint)
                }

                // Center
                paint.color = Color.rgb(255, 215, 0)
                canvas.drawCircle(250f, 200f, 45f, paint)
            }
        }

        return bitmap
    }
}
