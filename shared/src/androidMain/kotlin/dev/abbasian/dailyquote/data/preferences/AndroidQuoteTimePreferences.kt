package dev.abbasian.dailyquote.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.quoteTimeDataStore by preferencesDataStore("quote_time_prefs")

class AndroidQuoteTimePreferences(private val context: Context) : QuoteTimePreferences {
    private val dataStore: DataStore<Preferences> = context.quoteTimeDataStore

    companion object {
        private val KEY_NEXT_QUOTE_TIME = longPreferencesKey("next_quote_time")
    }

    override suspend fun saveNextQuoteTime(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[KEY_NEXT_QUOTE_TIME] = timestamp
        }
    }

    override suspend fun getNextQuoteTime(): Long? {
        return dataStore.data.map { preferences ->
            preferences[KEY_NEXT_QUOTE_TIME]
        }.firstOrNull()
    }
}