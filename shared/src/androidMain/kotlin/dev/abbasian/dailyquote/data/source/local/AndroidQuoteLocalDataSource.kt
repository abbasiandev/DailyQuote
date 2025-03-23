package dev.abbasian.dailyquote.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.data.source.AbstractQuoteLocalDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("quotes_prefs")

class AndroidQuoteLocalDataSource(private val context: Context) : AbstractQuoteLocalDataSource() {
    private val dataStore: DataStore<Preferences> = context.dataStore
    private val favoritesFlow = MutableStateFlow<List<Quote>>(emptyList())

    override suspend fun getString(key: String): String? {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)]
        }.firstOrNull()
    }

    override suspend fun putString(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    override fun getFavoritesFlow(): MutableStateFlow<List<Quote>> {
        return favoritesFlow
    }
}