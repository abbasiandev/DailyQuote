package dev.abbasian.dailyquote.data.preferences

interface QuoteTimePreferences {
    suspend fun saveNextQuoteTime(timestamp: Long)
    suspend fun getNextQuoteTime(): Long?
}