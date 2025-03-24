package dev.abbasian.dailyquote.util

import dev.abbasian.dailyquote.data.preferences.QuoteTimePreferences
import platform.Foundation.NSUserDefaults
import platform.Foundation.setValue

class IOSQuoteTimePreferences : QuoteTimePreferences {
    companion object {
        private const val KEY_NEXT_QUOTE_TIME = "next_quote_time"
    }

    override suspend fun saveNextQuoteTime(timestamp: Long) {
        val userDefaults = NSUserDefaults.standardUserDefaults
        userDefaults.setValue(timestamp, forKey = KEY_NEXT_QUOTE_TIME)
    }

    override suspend fun getNextQuoteTime(): Long? {
        val userDefaults = NSUserDefaults.standardUserDefaults
        val nextQuoteTime = userDefaults.objectForKey(KEY_NEXT_QUOTE_TIME) as? Long
        return nextQuoteTime
    }
}