package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

object SheikahAudio {
    private val scope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 22050

    fun playBeep(freq: Float = 880f, durationMs: Int = 90) {
        scope.launch {
            val numSamples = (SAMPLE_RATE * (durationMs / 1000f)).toInt()
            val buffer = ShortArray(numSamples)
            val angleStep = (2.0 * PI * freq) / SAMPLE_RATE

            for (i in 0 until numSamples) {
                val envelope = when {
                    i < numSamples * 0.15f -> i / (numSamples * 0.15f)
                    i > numSamples * 0.7f -> (numSamples - i) / (numSamples * 0.3f)
                    else -> 1.0f
                }
                val sample = sin(i * angleStep) * envelope
                buffer[i] = (sample * Short.MAX_VALUE * 0.45).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playRuneSelect() {
        scope.launch {
            // Ascending Sheikah tech chirp
            val notes = listOf(587f, 740f, 880f, 1175f) // D5, F#5, A5, D6
            val noteDurationMs = 50
            val noteSamples = (SAMPLE_RATE * (noteDurationMs / 1000f)).toInt()
            val totalSamples = noteSamples * notes.size
            val buffer = ShortArray(totalSamples)

            notes.forEachIndexed { nIdx, freq ->
                val start = nIdx * noteSamples
                val angleStep = (2.0 * PI * freq) / SAMPLE_RATE
                for (i in 0 until noteSamples) {
                    val env = (1.0f - (i.toFloat() / noteSamples) * 0.3f)
                    val s = (sin(i * angleStep) * 0.7 + sin(i * angleStep * 2) * 0.3) * env
                    buffer[start + i] = (s * Short.MAX_VALUE * 0.35).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
            }
            playPcm(buffer)
        }
    }

    fun playDetonation() {
        scope.launch {
            val durationMs = 380
            val numSamples = (SAMPLE_RATE * (durationMs / 1000f)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val progress = i.toFloat() / numSamples
                val decay = (1.0f - progress) * (1.0f - progress)
                val noise = (Random.nextFloat() * 2.0f - 1.0f) * 0.6f
                val lowBoom = sin(i * (2.0 * PI * (120.0 - progress * 80.0)) / SAMPLE_RATE) * 0.8f
                val sample = (noise * 0.4f + lowBoom * 0.6f) * decay
                buffer[i] = (sample * Short.MAX_VALUE * 0.6).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playCookingFanfare() {
        scope.launch {
            // Zelda recipe fanfare: G4 -> A4 -> B4 -> D5 -> G5 chord
            val freqs = listOf(392f, 440f, 493.88f, 587.33f, 783.99f)
            val durations = listOf(70, 70, 70, 90, 420)
            var totalSamples = 0
            durations.forEach { totalSamples += (SAMPLE_RATE * (it / 1000f)).toInt() }
            val buffer = ShortArray(totalSamples)

            var offset = 0
            freqs.forEachIndexed { idx, freq ->
                val durSamples = (SAMPLE_RATE * (durations[idx] / 1000f)).toInt()
                val step = (2.0 * PI * freq) / SAMPLE_RATE
                val isLast = idx == freqs.lastIndex

                for (i in 0 until durSamples) {
                    val env = if (isLast) {
                        (1.0f - (i.toFloat() / durSamples)).coerceAtLeast(0.0f)
                    } else {
                        1.0f - (i.toFloat() / durSamples) * 0.2f
                    }
                    val harmonics = sin(i * step) * 0.6 + sin(i * step * 2) * 0.25 + sin(i * step * 3) * 0.15
                    val s = harmonics * env
                    buffer[offset + i] = (s * Short.MAX_VALUE * 0.4).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                offset += durSamples
            }
            playPcm(buffer)
        }
    }

    fun playShrineChime() {
        scope.launch {
            val durMs = 500
            val numSamples = (SAMPLE_RATE * (durMs / 1000f)).toInt()
            val buffer = ShortArray(numSamples)
            val f1 = 587.33f // D5
            val f2 = 880f    // A5
            val f3 = 1174.66f // D6

            for (i in 0 until numSamples) {
                val decay = (1.0f - (i.toFloat() / numSamples))
                val wave = (sin(i * 2.0 * PI * f1 / SAMPLE_RATE) * 0.4 +
                        sin(i * 2.0 * PI * f2 / SAMPLE_RATE) * 0.35 +
                        sin(i * 2.0 * PI * f3 / SAMPLE_RATE) * 0.25) * decay
                buffer[i] = (wave * Short.MAX_VALUE * 0.4).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playCollectChime() {
        playBeep(1046.5f, 70)
    }

    fun playRadarPing(proximity: Float) {
        // Frequency increases from 700Hz to 1400Hz as proximity increases (0.0 to 1.0)
        val freq = 700f + (proximity.coerceIn(0f, 1f) * 700f)
        playBeep(freq = freq, durationMs = 60)
    }

    private fun playPcm(buffer: ShortArray) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            track.setNotificationMarkerPosition(buffer.size)
            track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(track: AudioTrack?) {
                    track?.release()
                }
                override fun onPeriodicNotification(track: AudioTrack?) {}
            })
        } catch (_: Exception) {
            // AudioTrack failure fallback silently
        }
    }
}
