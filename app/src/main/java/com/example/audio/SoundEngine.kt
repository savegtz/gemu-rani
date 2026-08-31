package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

object SoundEngine {
    private const val SAMPLE_RATE = 22050
    var isSoundEnabled: Boolean = true
    var isMusicEnabled: Boolean = true
        set(value) {
            field = value
            if (!value) {
                stopMusic()
            }
        }

    private val scope = CoroutineScope(Dispatchers.Default)
    private var musicJob: Job? = null

    fun init(context: Any?) {
        // Procedural Audio Engine initialized
    }

    fun release() {
        stopMusic()
    }

    private fun playPcm(buffer: ShortArray) {
        if (!isSoundEnabled) return
        scope.launch {
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
                val durationMs = (buffer.size * 1000L) / SAMPLE_RATE
                delay(durationMs + 50)
                track.release()
            } catch (_: Exception) {}
        }
    }

    fun playMenuClick() {
        val duration = (SAMPLE_RATE * 0.05f).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val freq = 650f - (t / 0.05f) * 200f
            val env = 1f - (i.toFloat() / duration)
            val sample = (sin(2.0 * PI * freq * t) * 0.3 * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playJump() {
        val duration = (SAMPLE_RATE * 0.18f).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val freq = 220f + (t / 0.18f) * 440f
            val env = 1f - (i.toFloat() / duration)
            val sample = (sin(2.0 * PI * freq * t) * 0.4 * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playSlide() {
        val duration = (SAMPLE_RATE * 0.22f).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val freq = 380f - (t / 0.22f) * 200f
            val env = 1f - (i.toFloat() / duration)
            val noise = (Random.nextFloat() * 2f - 1f) * 0.25f
            val tone = (sin(2.0 * PI * freq * t) * 0.3f).toFloat()
            val sample = ((tone + noise) * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playLaneSwitch() {
        val duration = (SAMPLE_RATE * 0.08f).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val freq = 450f + sin(t * 80.0).toFloat() * 50f
            val env = (1f - (i.toFloat() / duration)) * 0.3f
            val sample = (sin(2.0 * PI * freq * t) * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playCoin() {
        val duration = (SAMPLE_RATE * 0.12f).toInt()
        val buffer = ShortArray(duration)
        val f1 = 987.77f // B5
        val f2 = 1318.51f // E6
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val currentFreq = if (i < duration / 2) f1 else f2
            val env = 1f - (i.toFloat() / duration)
            val sample = (sin(2.0 * PI * currentFreq * t) * 0.35 * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playGem() {
        val duration = (SAMPLE_RATE * 0.25f).toInt()
        val buffer = ShortArray(duration)
        val freqs = floatArrayOf(880f, 1174.66f, 1479.98f, 1760f)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val step = (i.toFloat() / duration * freqs.size).toInt().coerceIn(0, freqs.size - 1)
            val freq = freqs[step]
            val env = 1f - (i.toFloat() / duration)
            val sample = (sin(2.0 * PI * freq * t) * 0.4 * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playPowerUp() {
        val duration = (SAMPLE_RATE * 0.35f).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val freq = 300f + (t / 0.35f) * 900f
            val env = 1f - (i.toFloat() / duration) * 0.5f
            val sample = ((sin(2.0 * PI * freq * t) + sin(4.0 * PI * freq * t) * 0.3) * 0.35 * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playShieldAbsorb() {
        val duration = (SAMPLE_RATE * 0.28f).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val freq = 600f + sin(t * 120.0).toFloat() * 180f
            val env = 1f - (i.toFloat() / duration)
            val sample = (sin(2.0 * PI * freq * t) * 0.45 * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playCrash() {
        val duration = (SAMPLE_RATE * 0.35f).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = 1f - (i.toFloat() / duration)
            val lowBoom = sin(2.0 * PI * (90f - t * 120f).coerceAtLeast(30f) * t) * 0.5f
            val noise = (Random.nextFloat() * 2f - 1f) * 0.5f
            val sample = ((lowBoom + noise) * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playCountdown(isFinal: Boolean) {
        val duration = (SAMPLE_RATE * if (isFinal) 0.35f else 0.15f).toInt()
        val freq = if (isFinal) 880f else 440f
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = 1f - (i.toFloat() / duration)
            val sample = (sin(2.0 * PI * freq * t) * 0.45 * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playVictory() {
        val notes = floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f)
        val noteDur = (SAMPLE_RATE * 0.12f).toInt()
        val totalSamples = noteDur * notes.size
        val buffer = ShortArray(totalSamples)
        for (n in notes.indices) {
            val freq = notes[n]
            for (i in 0 until noteDur) {
                val idx = n * noteDur + i
                val t = i.toFloat() / SAMPLE_RATE
                val env = 1f - (i.toFloat() / noteDur) * 0.4f
                val sample = (sin(2.0 * PI * freq * t) * 0.4 * env * Short.MAX_VALUE).toInt()
                buffer[idx] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
        }
        playPcm(buffer)
    }

    fun startAfricanRhythmBeat() {
        if (!isMusicEnabled || musicJob != null) return
        musicJob = scope.launch {
            val scale = floatArrayOf(220f, 261.63f, 293.66f, 329.63f, 392f, 440f, 523.25f, 659.25f)
            val bassScale = floatArrayOf(55f, 65.41f, 73.42f, 82.41f, 98.0f)
            var step = 0
            while (isActive && isMusicEnabled) {
                // Amapiano Log Drum + African Bongo syncopation
                if (step % 2 == 0) {
                    playBongoHit(low = (step % 4 == 0))
                }
                // Amapiano warm sub bass pulse
                if (step % 4 == 0) {
                    val bassNote = bassScale[(step / 4) % bassScale.size]
                    playAmapianoLogDrum(bassNote)
                }
                // Kalimba / Marimba melodic lead
                if (step % 3 == 0) {
                    val note = scale[(step / 3) % scale.size]
                    playKalimbaNote(note)
                }
                step++
                delay(150)
            }
        }
    }

    private fun playAmapianoLogDrum(freq: Float) {
        if (!isSoundEnabled) return
        val duration = (SAMPLE_RATE * 0.16f).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = (1f - (i.toFloat() / duration)) * (1f - (i.toFloat() / duration))
            val pitchDrop = freq * (1f - (t / 0.16f) * 0.35f)
            val sub = sin(2.0 * PI * pitchDrop * t) * 0.45
            val punch = sin(2.0 * PI * (pitchDrop * 2.5) * t) * 0.18
            val sample = ((sub + punch) * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun startMusic() {
        startAfricanRhythmBeat()
    }

    fun playBodaBodaEngine() {
        val duration = (SAMPLE_RATE * 0.32f).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val freq = 80f + sin(t * 120.0).toFloat() * 25f + (t / 0.32f) * 70f
            val env = 1f - (i.toFloat() / duration) * 0.2f
            val enginePuff = sin(2.0 * PI * freq * t) * 0.35f + (Random.nextFloat() * 2f - 1f) * 0.15f
            val sample = (enginePuff * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playSwahiliShout(type: String = "twenzetu") {
        val duration = (SAMPLE_RATE * 0.28f).toInt()
        val buffer = ShortArray(duration)
        val basePitch = when (type.lowercase()) {
            "twenzetu" -> 440f
            "chacha" -> 523.25f
            "kariakoo" -> 392f
            "moto" -> 659.25f
            else -> 493.88f
        }
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val freq = basePitch + sin(t * 70.0).toFloat() * 60f
            val env = if (i < duration * 0.2f) i / (duration * 0.2f) else 1f - (i - duration * 0.2f) / (duration * 0.8f)
            val form1 = sin(2.0 * PI * freq * t) * 0.3
            val form2 = sin(2.0 * PI * (freq * 2f) * t) * 0.2
            val form3 = sin(2.0 * PI * (freq * 3f) * t) * 0.1
            val sample = ((form1 + form2 + form3) * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    private fun playBongoHit(low: Boolean) {
        if (!isSoundEnabled) return
        val duration = (SAMPLE_RATE * 0.09f).toInt()
        val buffer = ShortArray(duration)
        val baseFreq = if (low) 130f else 220f
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val freq = baseFreq - (t / 0.09f) * 50f
            val env = 1f - (i.toFloat() / duration)
            val sample = (sin(2.0 * PI * freq * t) * 0.3 * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    private fun playKalimbaNote(freq: Float) {
        if (!isSoundEnabled) return
        val duration = (SAMPLE_RATE * 0.15f).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = 1f - (i.toFloat() / duration)
            val s1 = sin(2.0 * PI * freq * t) * 0.25
            val s2 = sin(4.0 * PI * freq * t) * 0.1
            val sample = ((s1 + s2) * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playDalaDalaHorn() {
        val duration = (SAMPLE_RATE * 0.35f).toInt()
        val buffer = ShortArray(duration)
        val f1 = 349.23f // F4
        val f2 = 440.00f // A4
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = if (i < duration * 0.8f) 1f else (1f - (i - duration * 0.8f) / (duration * 0.2f))
            val s1 = sin(2.0 * PI * f1 * t) * 0.25
            val s2 = sin(2.0 * PI * f2 * t) * 0.25
            val sample = ((s1 + s2) * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playBodaBodaBeep() {
        val duration = (SAMPLE_RATE * 0.12f).toInt()
        val buffer = ShortArray(duration)
        val freq = 587.33f // D5
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = 1f - (i.toFloat() / duration) * 0.3f
            val sample = (sin(2.0 * PI * freq * t) * 0.35 * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playConductorWhistle() {
        val duration = (SAMPLE_RATE * 0.22f).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val freq = 1800f + sin(t * 150.0).toFloat() * 120f
            val env = 1f - (i.toFloat() / duration) * 0.2f
            val sample = (sin(2.0 * PI * freq * t) * 0.28 * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playHoverboardOn() {
        val duration剩下 = (SAMPLE_RATE * 0.30f).toInt()
        val buffer = ShortArray(duration剩下)
        for (i in 0 until duration剩下) {
            val t = i.toFloat() / SAMPLE_RATE
            val freq = 200f + (t / 0.30f) * 800f
            val env = 1f - (i.toFloat() / duration剩下) * 0.15f
            val sample = ((sin(2.0 * PI * freq * t) * 0.3 + sin(4.0 * PI * freq * t) * 0.15) * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playHoverboardShatter() {
        val duration = (SAMPLE_RATE * 0.40f).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = 1f - (i.toFloat() / duration)
            val zap = sin(2.0 * PI * (600f - t * 800f).coerceAtLeast(100f) * t) * 0.4f
            val glass = (Random.nextFloat() * 2f - 1f) * 0.45f
            val sample = ((zap + glass) * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playSgrTrainWhistle() {
        val duration = (SAMPLE_RATE * 0.45f).toInt()
        val buffer = ShortArray(duration)
        val f1 = 440f // A4
        val f2 = 554.37f // C#5
        val f3 = 659.25f // E5
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = if (i < duration * 0.7f) 1f else (1f - (i - duration * 0.7f) / (duration * 0.3f))
            val s1 = sin(2.0 * PI * f1 * t) * 0.2
            val s2充满 = sin(2.0 * PI * f2 * t) * 0.2
            val s3 = sin(2.0 * PI * f3 * t) * 0.15
            val sample = ((s1 + s2充满 + s3) * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playTournamentWin() {
        val notes = floatArrayOf(440f, 554.37f, 659.25f, 880f, 1108.73f)
        val noteDur = (SAMPLE_RATE * 0.14f).toInt()
        val totalSamples = noteDur * notes.size
        val buffer = ShortArray(totalSamples)
        for (n in notes.indices) {
            val freq = notes[n]
            for (i in 0 until noteDur) {
                val idx = n * noteDur + i
                val t = i.toFloat() / SAMPLE_RATE
                val env不易 = 1f - (i.toFloat() / noteDur) * 0.3f
                val sample = (sin(2.0 * PI * freq * t) * 0.45 * env不易 * Short.MAX_VALUE).toInt()
                buffer[idx] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
        }
        playPcm(buffer)
    }

    fun playJetpackThrust() {
        val duration = (SAMPLE_RATE * 0.35f).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val env = 1f - (i.toFloat() / duration) * 0.2f
            val hiss = (Random.nextFloat() * 2f - 1f) * 0.35f
            val roar = sin(2.0 * PI * (120f + sin(t * 40.0) * 30f) * t) * 0.25f
            val sample = ((hiss + roar) * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun playStuntFlip() {
        val duration = (SAMPLE_RATE * 0.25f).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val t = i.toFloat() / SAMPLE_RATE
            val freq = 300f + sin(t * 30.0) * 200f + (t / 0.25f) * 400f
            val env = 1f - (i.toFloat() / duration) * 0.1f
            val sample = (sin(2.0 * PI * freq * t) * 0.4 * env * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcm(buffer)
    }

    fun stopMusic() {
        musicJob?.cancel()
        musicJob = null
    }
}
