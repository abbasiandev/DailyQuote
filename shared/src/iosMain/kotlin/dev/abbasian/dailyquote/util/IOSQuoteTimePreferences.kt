package dev.abbasian.dailyquote.util

import dev.abbasian.dailyquote.data.preferences.QuoteTimePreferences

class IOSQuoteTimePreferences : QuoteTimePreferences {
    override suspend fun saveNextQuoteTime(timestamp: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getNextQuoteTime(): Long? {
        TODO("Not yet implemented")
    }

}