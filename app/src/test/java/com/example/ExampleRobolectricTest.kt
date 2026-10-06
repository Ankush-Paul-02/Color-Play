package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.BrushMode
import com.example.model.DrawingCanvasData
import com.example.model.DrawingPath
import com.example.model.StrokePoint
import com.example.util.LineArtStyle
import com.example.util.PhotoLineArtConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Color & Play", appName)
  }

  @Test
  fun `photo line art conversion produces outline bitmap and transparent overlay`() {
    val samplePhoto = PhotoLineArtConverter.createSamplePhoto("puppy")
    assertNotNull(samplePhoto)
    assertEquals(500, samplePhoto.width)
    assertEquals(500, samplePhoto.height)

    val lineArt = PhotoLineArtConverter.convertToLineArt(
      source = samplePhoto,
      detailLevel = 0.5f,
      style = LineArtStyle.BALANCED,
      thickness = 2
    )
    assertNotNull(lineArt)
    assertTrue(lineArt.width > 0)
    assertTrue(lineArt.height > 0)

    val overlay = PhotoLineArtConverter.createTransparentLineOverlay(lineArt)
    assertNotNull(overlay)
    assertEquals(lineArt.width, overlay.width)
    assertEquals(lineArt.height, overlay.height)

    val base64 = PhotoLineArtConverter.bitmapToBase64(lineArt)
    assertTrue(base64.isNotBlank())

    val decoded = PhotoLineArtConverter.base64ToBitmap(base64)
    assertNotNull(decoded)
    assertEquals(lineArt.width, decoded?.width)
    assertEquals(lineArt.height, decoded?.height)
  }

  @Test
  fun `drawing canvas data preserves photoLineArtBase64 across JSON serialization`() {
    val samplePhoto = PhotoLineArtConverter.createSamplePhoto("teddy")
    val lineArt = PhotoLineArtConverter.convertToLineArt(samplePhoto)
    val base64 = PhotoLineArtConverter.bitmapToBase64(lineArt)

    val testPath = DrawingPath(
      points = listOf(StrokePoint(10f, 10f), StrokePoint(20f, 20f)),
      color = 0xFFFF0000,
      strokeWidth = 14f,
      brushMode = BrushMode.MARKER
    )

    val canvasData = DrawingCanvasData(
      paths = listOf(testPath),
      stamps = emptyList(),
      templateId = "photo_art",
      photoLineArtBase64 = base64
    )

    val json = canvasData.toJson()
    val restored = DrawingCanvasData.fromJson(json)

    assertEquals("photo_art", restored.templateId)
    assertEquals(base64, restored.photoLineArtBase64)
    assertEquals(1, restored.paths.size)
    assertEquals(2, restored.paths[0].points.size)
  }
}
