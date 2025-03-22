package dev.abbasian.dailyquote.presentation

import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.domain.GetDailyQuoteUseCase
import dev.abbasian.dailyquote.domain.GetFavoritesUseCase
import dev.abbasian.dailyquote.domain.GetRandomQuoteUseCase
import dev.abbasian.dailyquote.domain.RefreshQuotesUseCase
import dev.abbasian.dailyquote.domain.ToggleFavoriteUseCase
import dev.abbasian.dailyquote.util.ScreenHeightProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class QuoteViewModel(
    private val getDailyQuoteUseCase: GetDailyQuoteUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getRandomQuoteUseCase: GetRandomQuoteUseCase,
    private val refreshQuotesUseCase: RefreshQuotesUseCase,
    private val screenHeightProvider: ScreenHeightProvider,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _uiState = MutableStateFlow(QuoteUiState())
    val uiState: StateFlow<QuoteUiState> = _uiState.asStateFlow()

    private val authorDefaultImageBaseUrls = mapOf(
        "Steve Jobs" to "https://i.pravatar.cc/ID?img=13",
        "Albert Einstein" to "https://i.pravatar.cc/ID?img=11",
        "Abraham Lincoln" to "https://i.pravatar.cc/ID?img=53",
        "Mark Twain" to "https://i.pravatar.cc/ID?img=67",
        "Eleanor Roosevelt" to "https://i.pravatar.cc/ID?img=29",
        "Nelson Mandela" to "https://i.pravatar.cc/ID?img=12",
        "John Lennon" to "https://i.pravatar.cc/ID?img=15",
        "Maya Angelou" to "https://i.pravatar.cc/ID?img=32",
        "Oscar Wilde" to "https://i.pravatar.cc/ID?img=68",
        "Marie Curie" to "https://i.pravatar.cc/ID?img=5",
        "Mahatma Gandhi" to "https://i.pravatar.cc/ID?img=18",
        "Friedrich Nietzsche" to "https://i.pravatar.cc/ID?img=16"
    )

    private val optimalImageSize: Int by lazy {
        (screenHeightProvider.getScreenHeight() / 4).coerceIn(150, 500)
    }

    init {
        loadDailyQuote()
        loadFavorites()
        observeFavorites()
    }

    fun loadDailyQuote() {
        coroutineScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val quote = getDailyQuoteUseCase()
                val enhancedQuote = enhanceQuoteWithImage(quote)
                _uiState.update {
                    it.copy(
                        currentQuote = enhancedQuote,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load quote: ${e.message}"
                    )
                }
            }
        }
    }

    fun loadRandomQuote() {
        coroutineScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val quote = getRandomQuoteUseCase()
                val enhancedQuote = enhanceQuoteWithImage(quote)
                _uiState.update {
                    it.copy(
                        currentQuote = enhancedQuote,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load random quote: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadFavorites() {
        coroutineScope.launch {
            try {
                val favorites = getFavoritesUseCase()
                val enhancedFavorites = favorites.map { enhanceQuoteWithImage(it) }
                _uiState.update { it.copy(favorites = enhancedFavorites) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to load favorites: ${e.message}")
                }
            }
        }
    }

    private fun observeFavorites() {
        coroutineScope.launch {
            getFavoritesUseCase.observe().collect { favorites ->
                val enhancedFavorites = favorites.map { enhanceQuoteWithImage(it) }
                _uiState.update { it.copy(favorites = enhancedFavorites) }

                _uiState.value.currentQuote?.let { currentQuote ->
                    val updatedQuote = currentQuote.copy(
                        isFavorite = favorites.any { it.id == currentQuote.id }
                    )
                    _uiState.update { it.copy(currentQuote = updatedQuote) }
                }
            }
        }
    }

    fun toggleFavorite() {
        val quote = _uiState.value.currentQuote ?: return
        toggleFavorite(quote.id)
    }

    fun toggleFavorite(quoteId: String) {
        coroutineScope.launch {
            try {
                toggleFavoriteUseCase(quoteId)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to update favorite: ${e.message}")
                }
            }
        }
    }

    fun refreshQuotes() {
        coroutineScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                val success = refreshQuotesUseCase()
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        error = if (success) null else "Failed to refresh quotes"
                    )
                }
                if (success) {
                    loadDailyQuote()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        error = "Failed to refresh quotes: ${e.message}"
                    )
                }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun enhanceQuoteWithImage(quote: Quote): Quote {
        if (quote.authorImageUrl.isNotEmpty()) {
            return quote
        }

        val authorImage = getAuthorImage(quote.author)
        return quote.copy(authorImageUrl = authorImage)
    }

    private fun getAuthorImage(author: String): String {
        authorDefaultImageBaseUrls[author]?.let {
            return it.replace("ID", optimalImageSize.toString())
        }

        val seed = author.hashCode().rem(70).coerceIn(1, 70)

        val serviceIndex = (author.hashCode() % 4).absoluteValue

        return when (serviceIndex) {
            0 -> "https://i.pravatar.cc/${optimalImageSize}?img=$seed"
            1 -> "https://robohash.org/${urlEncode(author)}?size=${optimalImageSize}x${optimalImageSize}"
            2 -> "https://avatars.dicebear.com/api/avataaars/${urlEncode(author)}.svg?width=${optimalImageSize}&height=${optimalImageSize}"
            else -> "https://ui-avatars.com/api/?name=${urlEncode(author)}&size=${optimalImageSize}&background=random"
        }
    }

    private fun urlEncode(string: String): String {
        val allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"
        return string.map { char ->
            if (allowedCharacters.contains(char)) char.toString()
            else "%${char.code.toString(16).padStart(2, '0').uppercase()}"
        }.joinToString("")
    }
}

data class QuoteUiState(
    val currentQuote: Quote? = null,
    val favorites: List<Quote> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)