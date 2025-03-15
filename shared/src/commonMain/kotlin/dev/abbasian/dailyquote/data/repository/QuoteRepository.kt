package dev.abbasian.dailyquote.data.repository

import dev.abbasian.dailyquote.data.model.Quote
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {
    suspend fun getDailyQuote(): Quote
    suspend fun getFavorites(): List<Quote>
    suspend fun toggleFavorite(quoteId: String): Boolean
    suspend fun getRandomQuote(): Quote
    suspend fun refreshQuotes(): Boolean
    fun observeFavorites(): Flow<List<Quote>>
}