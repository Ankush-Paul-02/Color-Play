package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundPlayer(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var soundEnabled: Boolean = true

    fun playPop() {
        if (!soundEnabled) return
        scope.launch {
            // Rapid pitch drop 800Hz -> 200Hz in 60ms
            playToneSweep(startFreq = 800.0, endFreq = 250.0, durationMs = 60)
        }
        vibrate(30)
    }

    fun playChimeSuccess() {
        if (!soundEnabled) return
        scope.launch {
            // Pleasant arpeggio: C5 (523Hz), E5 (659Hz), G5 (784Hz), C6 (1046Hz)
            playTone(523.25, 90)
            playTone(659.25, 90)
            playTone(783.99, 90)
            playTone(1046.50, 160)
        }
        vibrate(80)
    }

    fun playClick() {
        if (!soundEnabled) return
        scope.launch {
            playTone(950.0, 30)
        }
        vibrate(15)
    }

    fun playSparkle() {
        if (!soundEnabled) return
        scope.launch {
            val freqs = listOf(1200.0, 1500.0, 1800.0, 2100.0)
            for (f in freqs) {
                playTone(f, 40)
            }
        }
        vibrate(40)
    }

    fun playBrushStroke() {
        if (!soundEnabled) return
        scope.launch {
            playToneSweep(300.0, 450.0, 40)
        }
    }

    fun playErrorBuzz() {
        if (!soundEnabled) return
        scope.launch {
            playTone(160.0, 120)
        }
        vibrate(100)
    }

    private fun playTone(freq: Double, durationMs: Int) {
        val sampleRate = 22050
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            // Sine wave with soft attack and decay envelope to avoid clicks
            val envelope = when {
                i < numSamples * 0.1 -> i / (numSamples * 0.1)
                i > numSamples * 0.8 -> (numSamples - i) / (numSamples * 0.2)
                else -> 1.0
            }
            val sample = (sin(2.0 * PI * freq * t) * envelope * 16000.0).toInt().coerceIn(-32767, 32767)
            buffer[i] = sample.toShort()
        }
        playAudioBuffer(buffer, sampleRate)
    }

    private fun playToneSweep(startFreq: Double, endFreq: Double, durationMs: Int) {
        val sampleRate = 22050
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            phase += 2.0 * PI * currentFreq / sampleRate
            val envelope = when {
                i < numSamples * 0.1 -> i / (numSamples * 0.1)
                i > numSamples * 0.7 -> (numSamples - i) / (numSamples * 0.3)
                else -> 1.0
            }
            val sample = (sin(phase) * envelope * 16000.0).toInt().coerceIn(-32767, 32767)
            buffer[i] = sample.toShort()
        }
        playAudioBuffer(buffer, sampleRate)
    }

    private fun playAudioBuffer(buffer: ShortArray, sampleRate: Int) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep((buffer.size.toDouble() / sampleRate * 1000).toLong() + 20)
            audioTrack.release()
        } catch (_: Exception) {
            // Audio fallback gracefully ignores
        }
    }

    private fun vibrate(durationMs: Long) {
        try {
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {}
    }
}
