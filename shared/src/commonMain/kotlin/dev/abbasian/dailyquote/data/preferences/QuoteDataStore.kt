package dev.abbasian.dailyquote.data.preferences

import dev.abbasian.dailyquote.data.model.Quote

interface QuoteDataStore {
    suspend fun saveLastViewedQuote(quote: Quote)
    suspend fun getLastViewedQuote(): Quote?
    suspend fun saveLastQuoteViewTime(timestamp: Long)
    suspend fun getLastQuoteViewTime(): Long?
}