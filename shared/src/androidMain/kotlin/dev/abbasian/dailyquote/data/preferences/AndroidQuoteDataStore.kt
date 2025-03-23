package dev.abbasian.dailyquote.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.abbasian.dailyquote.data.model.Quote
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.viewedQuoteDataStore by preferencesDataStore("viewed_quote_prefs")

class AndroidQuoteDataStore(private val context: Context) : QuoteDataStore {
    private val dataStore: DataStore<Preferences> = context.viewedQuoteDataStore

    companion object {
        private val KEY_LAST_VIEWED_QUOTE = stringPreferencesKey("last_viewed_quote")
        private val KEY_LAST_VIEW_TIME = longPreferencesKey("last_view_time")
    }

    override suspend fun saveLastViewedQuote(quote: Quote) {
        dataStore.edit { preferences ->
            preferences[KEY_LAST_VIEWED_QUOTE] = Json.encodeToString(quote)
        }
    }

    override suspend fun getLastViewedQuote(): Quote? {
        val quoteJson = dataStore.data.map { preferences ->
            preferences[KEY_LAST_VIEWED_QUOTE]
        }.firstOrNull()

        return quoteJson?.let { Json.decodeFromString<Quote>(it) }
    }

    override suspend fun saveLastQuoteViewTime(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[KEY_LAST_VIEW_TIME] = timestamp
        }
    }

    override suspend fun getLastQuoteViewTime(): Long? {
        return dataStore.data.map { preferences ->
            preferences[KEY_LAST_VIEW_TIME]
        }.firstOrNull()
    }
}