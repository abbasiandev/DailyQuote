package dev.abbasian.dailyquote.data.source

import dev.abbasian.dailyquote.data.model.Quote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

abstract class AbstractQuoteLocalDataSource : QuoteLocalDataSource {
    protected abstract suspend fun getString(key: String): String?
    protected abstract suspend fun putString(key: String, value: String)
    protected abstract fun getFavoritesFlow(): MutableStateFlow<List<Quote>>

    companion object {
        private const val KEY_DAILY_QUOTE = "daily_quote"
        private const val KEY_QUOTE_DATE = "quote_date"
        private const val KEY_FAVORITES = "favorites"
        private const val KEY_QUOTES = "quotes"
    }

    override suspend fun getDailyQuote(): Quote? {
        val quoteJson = getString(KEY_DAILY_QUOTE)
        return quoteJson?.let { Json.decodeFromString(it) }
    }

    override suspend fun saveDailyQuote(quote: Quote, date: LocalDate) {
        putString(KEY_DAILY_QUOTE, Json.encodeToString(quote))
        putString(KEY_QUOTE_DATE, date.toString())
    }

    override suspend fun getFavorites(): List<Quote> {
        val favoritesJson = getString(KEY_FAVORITES)
        val favorites = favoritesJson?.let { Json.decodeFromString<List<Quote>>(it) } ?: emptyList()
        getFavoritesFlow().value = favorites
        return favorites
    }

    override fun observeFavorites(): Flow<List<Quote>> {
        return getFavoritesFlow()
    }

    override suspend fun toggleFavorite(quoteId: String): Boolean {
        val favorites = getFavorites().toMutableList()
        val index = favorites.indexOfFirst { it.id == quoteId }

        if (index != -1) {
            favorites.removeAt(index)
        } else {
            val allQuotes = getAllQuotes()
            val quote = allQuotes.find { it.id == quoteId }

            if (quote != null) {
                favorites.add(quote.copy(isFavorite = true))
            } else {
                val dailyQuote = getDailyQuote()
                if (dailyQuote?.id == quoteId) {
                    favorites.add(dailyQuote.copy(isFavorite = true))
                } else {
                    return false
                }
            }
        }

        putString(KEY_FAVORITES, Json.encodeToString(favorites))
        getFavoritesFlow().update { favorites }

        return true
    }

    override suspend fun isFavorite(quoteId: String): Boolean {
        val favorites = getFavorites()
        return favorites.any { it.id == quoteId }
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
        putString(KEY_QUOTES, Json.encodeToString(quotes))
    }

    override suspend fun getLastQuoteDate(): LocalDate? {
        val dateString = getString(KEY_QUOTE_DATE)
        return dateString?.toLocalDate()
    }

    protected suspend fun getAllQuotes(): List<Quote> {
        val quotesJson = getString(KEY_QUOTES)
        return quotesJson?.let { Json.decodeFromString(it) } ?: getDefaultQuotes()
    }

    private fun getDefaultQuotes(): List<Quote> {
        return listOf(
            Quote(
                id = "1",
                text = "The only way to do great work is to love what you do.",
                author = "Steve Jobs",
                category = "Inspiration",
                authorImageUrl = ""
            ),
            Quote(
                id = "2",
                text = "Life is what happens when you're busy making other plans.",
                author = "John Lennon",
                category = "Life",
                authorImageUrl = ""
            ),
            Quote(
                id = "3",
                text = "The future belongs to those who believe in the beauty of their dreams.",
                author = "Eleanor Roosevelt",
                category = "Dreams",
                authorImageUrl = ""
            ),
            Quote(
                id = "4",
                text = "In the end, it's not the years in your life that count. It's the life in your years.",
                author = "Abraham Lincoln",
                category = "Life",
                authorImageUrl = ""
            ),
            Quote(
                id = "5",
                text = "The greatest glory in living lies not in never falling, but in rising every time we fall.",
                author = "Nelson Mandela",
                category = "Perseverance",
                authorImageUrl = ""
            )
        )
    }
}