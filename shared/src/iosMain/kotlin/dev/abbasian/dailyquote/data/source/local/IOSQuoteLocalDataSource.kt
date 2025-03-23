package dev.abbasian.dailyquote.data.source.local

import com.russhwolf.settings.Settings
import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.data.source.AbstractQuoteLocalDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import platform.Foundation.NSUserDefaults

class IOSQuoteLocalDataSource : AbstractQuoteLocalDataSource() {
    private val settings: Settings = com.russhwolf.settings.AppleSettings(NSUserDefaults.standardUserDefaults)
    private val favoritesFlow = MutableStateFlow<List<Quote>>(emptyList())

    override suspend fun getString(key: String): String? {
        return settings.getStringOrNull(key)
    }

    override suspend fun putString(key: String, value: String) {
        settings.putString(key, value)
    }

    override fun getFavoritesFlow(): MutableStateFlow<List<Quote>> {
        return favoritesFlow
    }
}