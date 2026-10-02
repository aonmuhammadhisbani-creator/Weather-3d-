package com.example.ui.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.data.model.WeatherCondition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random

class WeatherAudioSynthesizer {
    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun startAmbient(condition: WeatherCondition) {
        stopAmbient()

        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(sampleRate / 4)

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
        } catch (e: Exception) {
            return
        }

        synthJob = scope.launch {
            val random = Random()
            val chunk = ShortArray(1024)
            var filterState = 0f
            var phase = 0.0

            while (isActive) {
                when (condition) {
                    WeatherCondition.LIGHT_RAIN, WeatherCondition.HEAVY_RAIN -> {
                        // Soft filtered pink/brown noise mimicking rain droplets
                        val gain = if (condition == WeatherCondition.HEAVY_RAIN) 0.18f else 0.08f
                        for (i in chunk.indices) {
                            val white = (random.nextFloat() * 2f - 1f)
                            filterState = filterState * 0.92f + white * 0.08f
                            val sample = (filterState * gain * Short.MAX_VALUE).toInt()
                            chunk[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                    WeatherCondition.WINDY -> {
                        // Low-frequency modulated howling wind
                        for (i in chunk.indices) {
                            phase += 0.015 + (random.nextFloat() * 0.005)
                            val lowMod = kotlin.math.sin(phase * 0.02)
                            val noise = (random.nextFloat() * 2f - 1f)
                            filterState = filterState * 0.96f + noise * 0.04f
                            val sample = (filterState * (0.08f + lowMod.toFloat() * 0.05f) * Short.MAX_VALUE).toInt()
                            chunk[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                    WeatherCondition.THUNDERSTORM -> {
                        // Rain noise + occasional deep rumble
                        for (i in chunk.indices) {
                            val white = (random.nextFloat() * 2f - 1f)
                            filterState = filterState * 0.90f + white * 0.10f
                            val sample = (filterState * 0.16f * Short.MAX_VALUE).toInt()
                            chunk[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                    else -> {
                        // Very gentle tranquil breeze
                        for (i in chunk.indices) {
                            val white = (random.nextFloat() * 2f - 1f)
                            filterState = filterState * 0.98f + white * 0.02f
                            val sample = (filterState * 0.04f * Short.MAX_VALUE).toInt()
                            chunk[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                }

                audioTrack?.write(chunk, 0, chunk.size)
            }
        }
    }

    fun stopAmbient() {
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }
}
