package dev.abbasian.dailyquote.data.remote

import dev.abbasian.dailyquote.data.model.Quote

interface QuoteRemoteDataSource {
    suspend fun fetchDailyQuote(): Quote
    suspend fun fetchQuotes(): List<Quote>
}