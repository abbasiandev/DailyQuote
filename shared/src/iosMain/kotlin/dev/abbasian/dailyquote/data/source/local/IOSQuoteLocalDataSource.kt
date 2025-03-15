package dev.abbasian.dailyquote.data.source.local

import com.russhwolf.settings.Settings
import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.data.source.QuoteLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

class IOSQuoteLocalDataSource : QuoteLocalDataSource {
    private val settings = Settings(NSUserDefaults.standardUserDefaults)

    // StateFlow to simulate reactive data
    private val favoritesFlow = MutableStateFlow<List<Quote>>(emptyList())

    companion object {
        private const val KEY_DAILY_QUOTE = "daily_quote"
        private const val KEY_QUOTE_DATE = "quote_date"
        private const val KEY_FAVORITES = "favorites"
        private const val KEY_QUOTES = "quotes"
    }

    override suspend fun getDailyQuote(): Quote? {
        val quoteJson = settings.getStringOrNull(KEY_DAILY_QUOTE)
        return quoteJson?.let { Json.decodeFromString(it) }
    }

    override suspend fun saveDailyQuote(quote: Quote, date: LocalDate) {
        settings.putString(KEY_DAILY_QUOTE, Json.encodeToString(quote))
        settings.putString(KEY_QUOTE_DATE, date.toString())
    }

    override suspend fun getFavorites(): List<Quote> {
        val favoritesJson = settings.getStringOrNull(KEY_FAVORITES)
        val favorites = favoritesJson?.let { Json.decodeFromString<List<Quote>>(it) } ?: emptyList()
        favoritesFlow.value = favorites
        return favorites
    }

    override fun observeFavorites(): Flow<List<Quote>> {
        // Initialize flow with current favorites
        getFavorites()
        return favoritesFlow
    }

    override suspend fun toggleFavorite(quoteId: String): Boolean {
        val favorites = getFavorites().toMutableList()
        val index = favorites.indexOfFirst { it.id == quoteId }

        if (index != -1) {
            favorites.removeAt(index)
        } else {
            // Find the quote in all quotes
            val allQuotes = getAllQuotes()
            val quote = allQuotes.find { it.id == quoteId }

            if (quote != null) {
                favorites.add(quote.copy(isFavorite = true))
            } else {
                // If quote not found, try to get from daily quote
                val dailyQuote = getDailyQuote()
                if (dailyQuote?.id == quoteId) {
                    favorites.add(dailyQuote.copy(isFavorite = true))
                } else {
                    return false
                }
            }
        }

        settings.putString(KEY_FAVORITES, Json.encodeToString(favorites))
        favoritesFlow.update { favorites }

        return true
    }

    override suspend fun getRandomQuote(): Quote? {
        val quotes = getAllQuotes()
        return if (quotes.isNotEmpty()) {
            quotes.random()
        } else {
            null
        }
    }

    override suspend fun saveQuotes(quotes: List<Quote>) {
        settings.putString(KEY_QUOTES, Json.encodeToString(quotes))
    }

    override suspend fun getLastQuoteDate(): LocalDate? {
        val dateString = settings.getStringOrNull(KEY_QUOTE_DATE)
        return dateString?.toLocalDate()
    }

    private fun getAllQuotes(): List<Quote> {
        val quotesJson = settings.getStringOrNull(KEY_QUOTES)
        return quotesJson?.let { Json.decodeFromString(it) } ?: getDefaultQuotes()
    }

    private fun getDefaultQuotes(): List<Quote> {
        return listOf(
            Quote(
                id = "1",
                text = "The only way to do great work is to love what you do.",
                author = "Steve Jobs",
                category = "Inspiration"
            ),
            Quote(
                id = "2",
                text = "Life is what happens when you're busy making other plans.",
                author = "John Lennon",
                category = "Life"
            ),
            Quote(
                id = "3",
                text = "The future belongs to those who believe in the beauty of their dreams.",
                author = "Eleanor Roosevelt",
                category = "Dreams"
            ),
            Quote(
                id = "4",
                text = "In the end, it's not the years in your life that count. It's the life in your years.",
                author = "Abraham Lincoln",
                category = "Life"
            ),
            Quote(
                id = "5",
                text = "The greatest glory in living lies not in never falling, but in rising every time we fall.",
                author = "Nelson Mandela",
                category = "Perseverance"
            )
        )
    }
}