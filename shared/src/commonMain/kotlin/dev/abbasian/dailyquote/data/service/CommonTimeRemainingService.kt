package dev.abbasian.dailyquote.data.service

import dev.abbasian.dailyquote.data.preferences.QuoteTimePreferences
import kotlinx.coroutines.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.max

expect class PlatformTimeService {
    fun registerBackgroundWork(intervalMs: Long, action: () -> Unit): Any
    fun unregisterBackgroundWork(handle: Any)
    fun getCurrentTimeMillis(): Long

    companion object {
        fun create(): PlatformTimeService
    }
}

class CommonTimeRemainingService(
    private val quoteTimePreferences: QuoteTimePreferences,
    private val coroutineScope: CoroutineScope
) : TimeRemainingService {
    private val listeners = mutableListOf<TimeRemainingService.TimeRemainingListener>()
    private val platformTimeService = PlatformTimeService.create()

    private var targetTimeMs: Long = 0
    private var startTimeMs: Long = 0
    private var backgroundWorkHandle: Any? = null

    private var currentRemainingTime: String = ""
    private var currentProgress: Float = 0f

    override suspend fun startCountdown() {
        var nextQuoteTime = quoteTimePreferences.getNextQuoteTime()
        val currentTime = platformTimeService.getCurrentTimeMillis()

        val countdownDuration = 24 * 60 * 60 * 1000L

        if (nextQuoteTime == null || nextQuoteTime <= currentTime) {
            nextQuoteTime = currentTime + countdownDuration
            quoteTimePreferences.saveNextQuoteTime(nextQuoteTime)
        }

        this.targetTimeMs = nextQuoteTime
        this.startTimeMs = currentTime

        val timeRemaining = max(0, nextQuoteTime - currentTime)
        if (timeRemaining <= 0) {
            handleCountdownFinished()
            return
        }

        updateTimeDisplay(timeRemaining)
        listeners.forEach { it.onTimeUpdated(currentRemainingTime, currentProgress) }

        stopCountdown()
        backgroundWorkHandle = platformTimeService.registerBackgroundWork(1000) {
            updateCountdown()
        }
    }

    override fun stopCountdown() {
        backgroundWorkHandle?.let {
            platformTimeService.unregisterBackgroundWork(it)
            backgroundWorkHandle = null
        }
    }

    override fun getCurrentRemainingTime(): String = currentRemainingTime

    override fun getCurrentProgress(): Float = currentProgress

    override suspend fun isQuoteAvailable(): Boolean {
        val nextQuoteTime = quoteTimePreferences.getNextQuoteTime() ?: return true
        return platformTimeService.getCurrentTimeMillis() >= nextQuoteTime
    }

    override fun getCurrentTimeState(): Pair<String, Float> {
        return Pair(currentRemainingTime, currentProgress)
    }

    override fun addListener(listener: TimeRemainingService.TimeRemainingListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
            listener.onTimeUpdated(currentRemainingTime, currentProgress)
        }
    }

    override fun removeListener(listener: TimeRemainingService.TimeRemainingListener) {
        listeners.remove(listener)
    }

    private fun updateCountdown() {
        coroutineScope.launch {
            val nextQuoteTime = quoteTimePreferences.getNextQuoteTime() ?: return@launch
            val currentTime = platformTimeService.getCurrentTimeMillis()
            val timeRemaining = max(0, nextQuoteTime - currentTime)

            if (timeRemaining <= 0) {
                handleCountdownFinished()
                return@launch
            }

            updateProgress(currentTime)
            updateTimeDisplay(timeRemaining)
            listeners.forEach { it.onTimeUpdated(currentRemainingTime, currentProgress) }
        }
    }

    private fun handleCountdownFinished() {
        currentRemainingTime = ""
        currentProgress = 1f
        listeners.forEach { it.onCountdownFinished() }
        stopCountdown()
    }

    private fun updateTimeDisplay(timeRemainingMs: Long) {
        val totalSeconds = (timeRemainingMs / 1000).toInt()
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        val hoursStr = hours.toString().padStart(2, '0')
        val minutesStr = minutes.toString().padStart(2, '0')
        val secondsStr = seconds.toString().padStart(2, '0')
        currentRemainingTime = "$hoursStr:$minutesStr:$secondsStr"
    }

    private fun updateProgress(currentTime: Long) {
        val totalDuration = targetTimeMs - startTimeMs
        val elapsedTime = currentTime - startTimeMs
        currentProgress = (elapsedTime.toFloat() / totalDuration).coerceIn(0f, 1f)
    }
}