package dev.abbasian.dailyquote.data.repository

import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.data.remote.QuoteRemoteDataSource
import dev.abbasian.dailyquote.data.source.QuoteLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class QuoteRepositoryImpl(
    private val localDataSource: QuoteLocalDataSource,
    private val remoteDataSource: QuoteRemoteDataSource
) : QuoteRepository {
    override suspend fun getDailyQuote(): Quote {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val storedDate = localDataSource.getLastQuoteDate()
        val storedQuote = localDataSource.getDailyQuote()

        return if (storedDate == today && storedQuote != null) {
            storedQuote
        } else {
            try {
                val newQuote = remoteDataSource.fetchDailyQuote()
                localDataSource.saveDailyQuote(newQuote, today)
                newQuote
            } catch (e: Exception) {
                getRandomQuote()
            }
        }
    }

    override suspend fun getFavorites(): List<Quote> {
        return localDataSource.getFavorites()
    }

    override fun observeFavorites(): Flow<List<Quote>> {
        return localDataSource.observeFavorites()
    }

    override suspend fun toggleFavorite(quoteId: String): Boolean {
        return localDataSource.toggleFavorite(quoteId)
    }

    override suspend fun getRandomQuote(): Quote {
        return localDataSource.getRandomQuote() ?: Quote(
            id = "fallback",
            text = "The best preparation for tomorrow is doing your best today.",
            author = "H. Jackson Brown Jr.",
            category = "Motivation",
            authorImageUrl = ""
        )
    }

    override suspend fun refreshQuotes(): Boolean {
        return try {
            val quotes = remoteDataSource.fetchQuotes()
            localDataSource.saveQuotes(quotes)
            true
        } catch (e: Exception) {
            false
        }
    }
}