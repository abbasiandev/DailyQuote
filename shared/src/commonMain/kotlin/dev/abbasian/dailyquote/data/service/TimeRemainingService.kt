package dev.abbasian.dailyquote.data.service

interface TimeRemainingService {
    suspend fun startCountdown()
    fun stopCountdown()
    fun getCurrentRemainingTime(): String
    fun getCurrentProgress(): Float
    suspend fun isQuoteAvailable(): Boolean
    fun getCurrentTimeState(): Pair<String, Float>

    interface TimeRemainingListener {
        fun onTimeUpdated(remainingTime: String, progress: Float)
        fun onCountdownFinished()
    }

    fun addListener(listener: TimeRemainingListener)
    fun removeListener(listener: TimeRemainingListener)
}