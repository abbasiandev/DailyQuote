package dev.abbasian.dailyquote.data.source

import dev.abbasian.dailyquote.data.model.Quote
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface QuoteLocalDataSource {
    suspend fun getDailyQuote(): Quote?
    suspend fun saveDailyQuote(quote: Quote, date: LocalDate)
    suspend fun getFavorites(): List<Quote>
    fun observeFavorites(): Flow<List<Quote>>
    suspend fun toggleFavorite(quoteId: String): Boolean
    suspend fun getRandomQuote(): Quote?
    suspend fun saveQuotes(quotes: List<Quote>)
    suspend fun getLastQuoteDate(): LocalDate?
}