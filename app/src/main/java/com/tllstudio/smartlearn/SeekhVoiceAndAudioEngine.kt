package com.tllstudio.smartlearn

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SeekhVoiceAndAudioEngine(
    private val context: Context,
    private val onMoodChanged: (BuddyMood) -> Unit,
    private val onWordSpokenByTts: (Int, Int) -> Unit = { _, _ -> },
    private val onSpeechRecognizedWords: (List<String>) -> Unit = {}
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val audioExecutor = Executors.newSingleThreadExecutor()
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var mediaPlayer: MediaPlayer? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    var isSoundEnabled: Boolean = true

    init {
        initTts()
    }

    private fun initTts() {
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isTtsReady = true
                    tts?.language = Locale.US
                    tts?.setPitch(1.10f) // Warm, friendly AI tutor voice
                    tts?.setSpeechRate(0.93f) // Clear educational pacing
                    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            mainHandler.post { onMoodChanged(BuddyMood.SPEAKING) }
                        }

                        override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                            mainHandler.post { onWordSpokenByTts(start, end) }
                        }

                        override fun onDone(utteranceId: String?) {
                            mainHandler.post { onMoodChanged(BuddyMood.IDLE) }
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            mainHandler.post { onMoodChanged(BuddyMood.IDLE) }
                        }
                    })
                }
            }
        } catch (_: Exception) {
        }
    }

    fun speak(
        text: String,
        flushQueue: Boolean = true,
        utteranceId: String = "seekh_utt_${System.currentTimeMillis()}"
    ) {
        if (!isSoundEnabled) return
        val cleanText = text.replace("_", " ").trim()
        if (cleanText.isEmpty()) return
        onMoodChanged(BuddyMood.SPEAKING)
        if (isTtsReady && tts != null) {
            val params = Bundle()
            val queueMode = if (flushQueue) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            tts?.speak(cleanText, queueMode, params, utteranceId)
            val wordCount = cleanText.split(Regex("\\s+")).size.coerceAtLeast(1)
            val estMs = (wordCount * 380L).coerceIn(900L, 6800L)
            mainHandler.postDelayed({
                if (tts?.isSpeaking != true) {
                    onMoodChanged(BuddyMood.IDLE)
                }
            }, estMs)
        } else {
            playTapSound()
            val wordCount = cleanText.split(Regex("\\s+")).size.coerceAtLeast(1)
            val estMs = (wordCount * 350L).coerceIn(850L, 4500L)
            mainHandler.postDelayed({
                onMoodChanged(BuddyMood.IDLE)
            }, estMs)
        }
    }

    // Pronounce a single letter, phonics blend, or syllable with a gentle crystal tap chime
    fun speakPhoneticToken(token: String) {
        if (!isSoundEnabled) return
        playTapSound()
        val spoken = when (token.lowercase().trim()) {
            "s" -> "sss"
            "b" -> "buh"
            "m" -> "mmm"
            "p" -> "puh"
            "c" -> "kuh"
            "t" -> "tuh"
            "r" -> "rrr"
            "f" -> "fff"
            "d" -> "duh"
            "l" -> "lll"
            "n" -> "nnn"
            "g" -> "guh"
            "h" -> "huh"
            "k" -> "kuh"
            "w" -> "wuh"
            "sh" -> "shh"
            "ch" -> "chuh"
            "th" -> "thh"
            "fl" -> "fluh"
            "st" -> "stuh"
            "tr" -> "truh"
            "br" -> "bruh"
            "cl" -> "cluh"
            "gr" -> "gruh"
            else -> token.lowercase().trim()
        }
        speak(spoken, flushQueue = true, utteranceId = "phoneme_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
        } catch (_: Exception) {
        }
        onMoodChanged(BuddyMood.IDLE)
    }

    // 1. Soft Crystal Marimba Tap Sound (Synthesized clean sine wave with exponential envelope)
    fun playTapSound() {
        if (!isSoundEnabled) return
        playSynthesizedNotes(listOf(NoteSpec(659.25, 65, 0.28f)))
    }

    // 2. Cheerful Ascending Major Arpeggio Chime (C5 -> E5 -> G5 -> C6)
    fun playSuccessChime() {
        if (!isSoundEnabled) return
        playSynthesizedNotes(
            listOf(
                NoteSpec(523.25, 70, 0.30f),
                NoteSpec(659.25, 70, 0.32f),
                NoteSpec(783.99, 75, 0.34f),
                NoteSpec(1046.50, 140, 0.36f)
            )
        )
    }

    // 3. Acoustic Balloon Pop + Sparkle Overtone
    fun playBalloonPopSound() {
        if (!isSoundEnabled) return
        audioExecutor.execute {
            try {
                val sampleRate = 24000
                val durationMs = 110
                val numSamples = (sampleRate * durationMs) / 1000
                val pcm = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    // Pitch drops rapidly from 820 Hz to 170 Hz for realistic balloon pop, followed by high sparkle
                    val freq = if (progress < 0.45) {
                        820.0 - (650.0 * (progress / 0.45))
                    } else {
                        1174.66 // D6 sparkle
                    }
                    val env = exp(-4.5 * progress)
                    val sample = sin(2.0 * PI * freq * t) * env * 0.45
                    pcm[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                playPcmBuffer(pcm, sampleRate)
            } catch (_: Exception) {
            }
        }
    }

    // 4. Kid-Friendly Soft Double Woodblock Error Tone (Never harsh)
    fun playErrorTone() {
        if (!isSoundEnabled) return
        playSynthesizedNotes(
            listOf(
                NoteSpec(293.66, 85, 0.30f),
                NoteSpec(246.94, 125, 0.30f)
            )
        )
    }

    // 5. Celebration Clapping + Fanfare Chime
    fun playClappingSound() {
        if (!isSoundEnabled) return
        playSuccessChime()
        playAssetAudioSafe("sounds/clapping.mp3")
    }

    // 6. Safe Phoneme & SFX Playback (Maps character/index to musical chime + phoneme audio)
    fun playPhonemeSound(index: Int = 0) {
        if (!isSoundEnabled) return
        // If index is an ASCII character code (e.g. 'a'..'z' or 'A'..'Z'), pronounce the letter phonetically + play marimba note!
        if (index in 65..90 || index in 97..122) {
            val ch = index.toChar().lowercaseChar()
            val scaleFreqs = listOf(523.25, 587.33, 659.25, 698.46, 783.99, 880.00, 987.77, 1046.50)
            val freq = scaleFreqs[(ch - 'a').mod(scaleFreqs.size)]
            playSynthesizedNotes(listOf(NoteSpec(freq, 75, 0.30f)))
            return
        }
        val safeIdx = index.mod(36).coerceAtLeast(0)
        playTapSound()
        playAssetAudioSafe("phonemes/$safeIdx.ogg")
    }

    private data class NoteSpec(val freqHz: Double, val durationMs: Int, val amplitude: Float)

    private fun playSynthesizedNotes(notes: List<NoteSpec>) {
        audioExecutor.execute {
            try {
                val sampleRate = 24000
                val totalMs = notes.sumOf { it.durationMs }
                val totalSamples = (sampleRate * totalMs) / 1000
                val pcm = ShortArray(totalSamples)
                var cursor = 0
                for (note in notes) {
                    val count = (sampleRate * note.durationMs) / 1000
                    for (i in 0 until count) {
                        if (cursor + i >= totalSamples) break
                        val t = i.toDouble() / sampleRate
                        val attack = (i.toDouble() / (sampleRate * 0.006)).coerceAtMost(1.0)
                        val decay = exp(-4.2 * (i.toDouble() / count))
                        val wave = sin(2.0 * PI * note.freqHz * t) + 0.25 * sin(4.0 * PI * note.freqHz * t)
                        val sample = wave * attack * decay * note.amplitude
                        pcm[cursor + i] = (sample * Short.MAX_VALUE).toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }
                    cursor += count
                }
                playPcmBuffer(pcm, sampleRate)
            } catch (_: Exception) {
            }
        }
    }

    private fun playPcmBuffer(pcm: ShortArray, sampleRate: Int) {
        try {
            val byteCount = pcm.size * 2
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
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
                .setBufferSizeInBytes(byteCount.coerceAtLeast(1024))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            track.write(pcm, 0, pcm.size)
            track.play()
            val sleepMs = ((pcm.size * 1000L) / sampleRate) + 40L
            Thread.sleep(sleepMs)
            track.stop()
            track.release()
        } catch (_: Exception) {
        }
    }

    private fun playAssetAudioSafe(assetPath: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = null
            val mp = MediaPlayer()
            try {
                // First try direct uncompressed file descriptor
                val afd = context.assets.openFd(assetPath)
                mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
            } catch (_: Exception) {
                // Fallback for compressed assets: stream to cache file and play from file path
                val safeName = assetPath.replace("/", "_")
                val cachedFile = File(context.cacheDir, safeName)
                if (!cachedFile.exists() || cachedFile.length() == 0L) {
                    context.assets.open(assetPath).use { input ->
                        FileOutputStream(cachedFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                mp.setDataSource(cachedFile.absolutePath)
            }
            mp.setOnCompletionListener { player ->
                try {
                    player.release()
                } catch (_: Exception) {
                }
            }
            mp.prepare()
            mp.start()
            mediaPlayer = mp
        } catch (_: Exception) {
        }
    }

    fun startVoiceListening(onFallbackTick: () -> Unit) {
        stopSpeaking()
        playTapSound()
        isListening = true
        onMoodChanged(BuddyMood.LISTENING)
        try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                if (speechRecognizer == null) {
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                }
                speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        onMoodChanged(BuddyMood.LISTENING)
                    }
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onError(error: Int) {
                        if (isListening) {
                            playSuccessChime()
                            onFallbackTick()
                        }
                    }
                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            playSuccessChime()
                            val words = matches.first().lowercase().split(Regex("\\s+"))
                            onSpeechRecognizedWords(words)
                        } else if (isListening) {
                            playSuccessChime()
                            onFallbackTick()
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val words = matches.first().lowercase().split(Regex("\\s+"))
                            onSpeechRecognizedWords(words)
                        }
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                }
                speechRecognizer?.startListening(intent)
            } else {
                playSuccessChime()
                onFallbackTick()
            }
        } catch (_: Exception) {
            playSuccessChime()
            onFallbackTick()
        }
    }

    fun stopVoiceListening() {
        isListening = false
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {
        }
        onMoodChanged(BuddyMood.IDLE)
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
            mediaPlayer?.release()
            speechRecognizer?.destroy()
            audioExecutor.shutdownNow()
        } catch (_: Exception) {
        }
    }
}
