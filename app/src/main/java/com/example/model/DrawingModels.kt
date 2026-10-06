package com.example.model

import androidx.compose.ui.graphics.Color
import org.json.JSONArray
import org.json.JSONObject

enum class BrushMode {
    PENCIL,
    MARKER,
    NEON,
    RAINBOW,
    GLITTER,
    ERASER
}

enum class StampType(val symbol: String, val label: String) {
    STAR("⭐", "Star"),
    HEART("💖", "Heart"),
    FLOWER("🌸", "Flower"),
    PAW("🐾", "Paw"),
    SMILEY("😊", "Smiley"),
    CROWN("👑", "Crown"),
    BUTTERFLY("🦋", "Butterfly"),
    SUN("☀️", "Sun")
}

data class StrokePoint(val x: Float, val y: Float)

data class DrawingPath(
    val points: List<StrokePoint>,
    val color: Long,
    val strokeWidth: Float,
    val brushMode: BrushMode
)

data class PlacedStamp(
    val x: Float,
    val y: Float,
    val stampType: StampType,
    val size: Float,
    val color: Long
)

data class DrawingCanvasData(
    val paths: List<DrawingPath> = emptyList(),
    val stamps: List<PlacedStamp> = emptyList(),
    val templateId: String = "free_draw",
    val photoLineArtBase64: String? = null
) {
    fun toJson(): String {
        val root = JSONObject()
        root.put("templateId", templateId)
        if (!photoLineArtBase64.isNullOrBlank()) {
            root.put("photoLineArtBase64", photoLineArtBase64)
        }

        val pathsArray = JSONArray()
        for (path in paths) {
            val pObj = JSONObject()
            pObj.put("color", path.color)
            pObj.put("width", path.strokeWidth.toDouble())
            pObj.put("mode", path.brushMode.name)
            val ptsArray = JSONArray()
            for (pt in path.points) {
                val ptObj = JSONObject()
                ptObj.put("x", pt.x.toDouble())
                ptObj.put("y", pt.y.toDouble())
                ptsArray.put(ptObj)
            }
            pObj.put("pts", ptsArray)
            pathsArray.put(pObj)
        }
        root.put("paths", pathsArray)

        val stampsArray = JSONArray()
        for (st in stamps) {
            val sObj = JSONObject()
            sObj.put("x", st.x.toDouble())
            sObj.put("y", st.y.toDouble())
            sObj.put("type", st.stampType.name)
            sObj.put("size", st.size.toDouble())
            sObj.put("color", st.color)
            stampsArray.put(sObj)
        }
        root.put("stamps", stampsArray)

        return root.toString()
    }

    companion object {
        fun fromJson(jsonStr: String): DrawingCanvasData {
            if (jsonStr.isBlank()) return DrawingCanvasData()
            return try {
                val root = JSONObject(jsonStr)
                val templateId = root.optString("templateId", "free_draw")

                val pathsList = mutableListOf<DrawingPath>()
                val pathsArray = root.optJSONArray("paths")
                if (pathsArray != null) {
                    for (i in 0 until pathsArray.length()) {
                        val pObj = pathsArray.getJSONObject(i)
                        val color = pObj.optLong("color", 0xFFFF3838)
                        val width = pObj.optDouble("width", 14.0).toFloat()
                        val modeStr = pObj.optString("mode", BrushMode.MARKER.name)
                        val mode = try { BrushMode.valueOf(modeStr) } catch (_: Exception) { BrushMode.MARKER }

                        val ptsList = mutableListOf<StrokePoint>()
                        val ptsArray = pObj.optJSONArray("pts")
                        if (ptsArray != null) {
                            for (j in 0 until ptsArray.length()) {
                                val ptObj = ptsArray.getJSONObject(j)
                                ptsList.add(StrokePoint(ptObj.getDouble("x").toFloat(), ptObj.getDouble("y").toFloat()))
                            }
                        }
                        if (ptsList.isNotEmpty()) {
                            pathsList.add(DrawingPath(ptsList, color, width, mode))
                        }
                    }
                }

                val stampsList = mutableListOf<PlacedStamp>()
                val stampsArray = root.optJSONArray("stamps")
                if (stampsArray != null) {
                    for (i in 0 until stampsArray.length()) {
                        val sObj = stampsArray.getJSONObject(i)
                        val x = sObj.getDouble("x").toFloat()
                        val y = sObj.getDouble("y").toFloat()
                        val typeStr = sObj.getString("type")
                        val type = try { StampType.valueOf(typeStr) } catch (_: Exception) { StampType.STAR }
                        val size = sObj.optDouble("size", 42.0).toFloat()
                        val color = sObj.optLong("color", 0xFFFFD32A)
                        stampsList.add(PlacedStamp(x, y, type, size, color))
                    }
                }

                val photoLineArtBase64 = root.optString("photoLineArtBase64", "").ifBlank { null }

                DrawingCanvasData(pathsList, stampsList, templateId, photoLineArtBase64)
            } catch (_: Exception) {
                DrawingCanvasData()
            }
        }
    }
}
