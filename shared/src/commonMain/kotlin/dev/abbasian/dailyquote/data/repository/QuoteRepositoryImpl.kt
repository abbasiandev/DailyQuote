package dev.abbasian.dailyquote.data.repository

import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.data.preferences.QuoteDataStore
import dev.abbasian.dailyquote.data.preferences.QuoteTimePreferences
import dev.abbasian.dailyquote.data.remote.QuoteRemoteDataSource
import dev.abbasian.dailyquote.data.source.AuthorImageService
import dev.abbasian.dailyquote.data.source.QuoteLocalDataSource
import dev.abbasian.dailyquote.util.ScreenHeightProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class QuoteRepositoryImpl(
    private val localDataSource: QuoteLocalDataSource,
    private val remoteDataSource: QuoteRemoteDataSource,
    private val authorImageService: AuthorImageService,
    private val screenHeightProvider: ScreenHeightProvider,
    private val quoteTimePreferences: QuoteTimePreferences,
    private val quoteDataStore: QuoteDataStore
) : QuoteRepository {

    private val optimalImageSize: Int by lazy {
        (screenHeightProvider.getScreenHeight() / 4).coerceIn(150, 500)
    }

    override suspend fun getDailyQuote(): Quote {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val storedDate = localDataSource.getLastQuoteDate()
        val storedQuote = localDataSource.getDailyQuote()

        val quote = if (storedDate == today && storedQuote != null) {
            storedQuote
        } else {
            try {
                val newQuote = remoteDataSource.fetchDailyQuote()
                val enhancedQuote = enhanceQuoteWithImage(newQuote)
                localDataSource.saveDailyQuote(enhancedQuote, today)

                // Update the next quote time
                val nextQuoteTime = Clock.System.now().toEpochMilliseconds() + 24 * 60 * 60 * 1000
                quoteTimePreferences.saveNextQuoteTime(nextQuoteTime)

                enhancedQuote
            } catch (e: Exception) {
                enhanceQuoteWithImage(getRandomQuote())
            }
        }

        return enhanceQuoteWithImage(quote)
    }

    override suspend fun getFavorites(): List<Quote> {
        val favorites = localDataSource.getFavorites()
        return favorites.map { enhanceQuoteWithImage(it) }
    }

    override fun observeFavorites(): Flow<List<Quote>> {
        return localDataSource.observeFavorites().map { favorites ->
            favorites.map { enhanceQuoteWithImage(it) }
        }
    }

    override suspend fun toggleFavorite(quoteId: String): Boolean {
        return localDataSource.toggleFavorite(quoteId)
    }

    override suspend fun getRandomQuote(): Quote {
        val randomQuote = localDataSource.getRandomQuote() ?: Quote(
            id = "fallback",
            text = "The best preparation for tomorrow is doing your best today.",
            author = "H. Jackson Brown Jr.",
            category = "Motivation",
            authorImageUrl = ""
        )

        return enhanceQuoteWithImage(randomQuote)
    }

    override suspend fun refreshQuotes(): Boolean {
        return try {
            val quotes = remoteDataSource.fetchQuotes()
            val enhancedQuotes = quotes.map { enhanceQuoteWithImage(it) }
            localDataSource.saveQuotes(enhancedQuotes)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun checkQuoteAvailability(): Boolean {
        val nextQuoteTime = quoteTimePreferences.getNextQuoteTime()
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return nextQuoteTime == null || currentTime >= nextQuoteTime
    }

    override suspend fun getNextQuoteAvailableAt(): Long? {
        return quoteTimePreferences.getNextQuoteTime()
    }

    private fun enhanceQuoteWithImage(quote: Quote): Quote {
        if (quote.authorImageUrl.isNotEmpty()) {
            return quote
        }

        val authorImage = authorImageService.getAuthorImageUrl(quote.author, optimalImageSize)
        return quote.copy(authorImageUrl = authorImage)
    }

    override suspend fun saveLastViewedQuote(quote: Quote) {
        quoteDataStore.saveLastViewedQuote(quote)

        val currentTime = Clock.System.now().toEpochMilliseconds()
        quoteDataStore.saveLastQuoteViewTime(currentTime)
    }

    override suspend fun getLastViewedQuote(): Quote? {
        val lastViewedQuote = quoteDataStore.getLastViewedQuote()
        val lastViewTime = quoteDataStore.getLastQuoteViewTime() ?: return null

        val currentTime = Clock.System.now().toEpochMilliseconds()
        val isFromToday = isSameDay(lastViewTime, currentTime)

        return if (isFromToday && lastViewedQuote != null) {
            val isFavorite = localDataSource.isFavorite(lastViewedQuote.id)
            lastViewedQuote.copy(isFavorite = isFavorite)
        } else {
            null
        }
    }

    private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val dayInMillis = 24 * 60 * 60 * 1000

        val day1 = timestamp1 / dayInMillis
        val day2 = timestamp2 / dayInMillis

        return day1 == day2
    }

}