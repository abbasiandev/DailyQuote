package dev.abbasian.dailyquote.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.data.source.QuoteLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("quotes_prefs")

class AndroidQuoteLocalDataSource(private val context: Context) : QuoteLocalDataSource {
    private val dataStore: DataStore<Preferences> = context.dataStore

    companion object {
        private val KEY_DAILY_QUOTE = stringPreferencesKey("daily_quote")
        private val KEY_QUOTE_DATE = stringPreferencesKey("quote_date")
        private val KEY_FAVORITES = stringPreferencesKey("favorites")
        private val KEY_QUOTES = stringPreferencesKey("quotes")
    }

    override suspend fun getDailyQuote(): Quote? {
        val quoteJson = dataStore.data.map { preferences ->
            preferences[KEY_DAILY_QUOTE]
        }.firstOrNull()

        return quoteJson?.let { Json.decodeFromString(it) }
    }

    override suspend fun saveDailyQuote(quote: Quote, date: LocalDate) {
        dataStore.edit { preferences ->
            preferences[KEY_DAILY_QUOTE] = Json.encodeToString(quote)
            preferences[KEY_QUOTE_DATE] = date.toString()
        }
    }

    override suspend fun getFavorites(): List<Quote> {
        val favoritesJson = dataStore.data.map { preferences ->
            preferences[KEY_FAVORITES]
        }.firstOrNull()

        return favoritesJson?.let { Json.decodeFromString<List<Quote>>(it) } ?: emptyList()
    }

    override fun observeFavorites(): Flow<List<Quote>> {
        return dataStore.data.map { preferences ->
            val favoritesJson = preferences[KEY_FAVORITES]
            favoritesJson?.let { Json.decodeFromString<List<Quote>>(it) } ?: emptyList()
        }
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

        dataStore.edit { preferences ->
            preferences[KEY_FAVORITES] = Json.encodeToString(favorites)
        }

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
        dataStore.edit { preferences ->
            preferences[KEY_QUOTES] = Json.encodeToString(quotes)
        }
    }

    override suspend fun getLastQuoteDate(): LocalDate? {
        val dateString = dataStore.data.map { preferences ->
            preferences[KEY_QUOTE_DATE]
        }.firstOrNull()

        return dateString?.toLocalDate()
    }

    private suspend fun getAllQuotes(): List<Quote> {
        val quotesJson = dataStore.data.map { preferences ->
            preferences[KEY_QUOTES]
        }.firstOrNull()

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