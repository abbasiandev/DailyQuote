package dev.abbasian.dailyquote.data.preferences

import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.Settings
import dev.abbasian.dailyquote.data.model.Quote
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

class IOSQuoteDataStore : QuoteDataStore {
    private val settings: Settings = AppleSettings(NSUserDefaults.standardUserDefaults)

    companion object {
        private const val KEY_LAST_VIEWED_QUOTE = "last_viewed_quote"
        private const val KEY_LAST_VIEW_TIME = "last_view_time"
    }

    override suspend fun saveLastViewedQuote(quote: Quote) {
        settings.putString(KEY_LAST_VIEWED_QUOTE, Json.encodeToString(quote))
    }

    override suspend fun getLastViewedQuote(): Quote? {
        val quoteJson = settings.getStringOrNull(KEY_LAST_VIEWED_QUOTE)
        return quoteJson?.let { Json.decodeFromString<Quote>(it) }
    }

    override suspend fun saveLastQuoteViewTime(timestamp: Long) {
        settings.putLong(KEY_LAST_VIEW_TIME, timestamp)
    }

    override suspend fun getLastQuoteViewTime(): Long? {
        return if (settings.hasKey(KEY_LAST_VIEW_TIME)) {
            settings.getLong(KEY_LAST_VIEW_TIME)
        } else {
            null
        }
    }
}