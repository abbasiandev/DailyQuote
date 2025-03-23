package dev.abbasian.dailyquote.presentation

import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.domain.CheckQuoteAvailabilityUseCase
import dev.abbasian.dailyquote.domain.GetDailyQuoteUseCase
import dev.abbasian.dailyquote.domain.GetFavoritesUseCase
import dev.abbasian.dailyquote.domain.GetNextQuoteTimeUseCase
import dev.abbasian.dailyquote.domain.GetRandomQuoteUseCase
import dev.abbasian.dailyquote.domain.RefreshQuotesUseCase
import dev.abbasian.dailyquote.domain.ToggleFavoriteUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class QuoteViewModel(
    private val getDailyQuoteUseCase: GetDailyQuoteUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getRandomQuoteUseCase: GetRandomQuoteUseCase,
    private val refreshQuotesUseCase: RefreshQuotesUseCase,
    private val checkQuoteAvailabilityUseCase: CheckQuoteAvailabilityUseCase,
    private val getNextQuoteTimeUseCase: GetNextQuoteTimeUseCase,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _uiState = MutableStateFlow(QuoteUiState())
    val uiState: StateFlow<QuoteUiState> = _uiState.asStateFlow()

    init {
        loadDailyQuote()
        loadFavorites()
        observeFavorites()
        checkQuoteAvailability()
    }

    private fun loadDailyQuote() {
        coroutineScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val quote = getDailyQuoteUseCase()
                val nextQuoteTime = getNextQuoteTimeUseCase()

                _uiState.update {
                    it.copy(
                        currentQuote = quote,
                        isLoading = false,
                        error = null,
                        nextQuoteAvailableAt = nextQuoteTime,
                        canRequestNewQuote = false
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
                _uiState.update {
                    it.copy(
                        currentQuote = quote,
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
                _uiState.update { it.copy(favorites = favorites) }
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
                _uiState.update { it.copy(favorites = favorites) }

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

    private fun checkQuoteAvailability() {
        coroutineScope.launch {
            val canRequestNewQuote = checkQuoteAvailabilityUseCase()
            val nextQuoteTime = getNextQuoteTimeUseCase()

            _uiState.update {
                it.copy(
                    nextQuoteAvailableAt = nextQuoteTime,
                    canRequestNewQuote = canRequestNewQuote
                )
            }

            if (!canRequestNewQuote && nextQuoteTime != null) {
                monitorQuoteAvailability(nextQuoteTime)
            }
        }
    }

    private suspend fun monitorQuoteAvailability(nextQuoteTime: Long) {
        while (true) {
            val currentTime = Clock.System.now().toEpochMilliseconds()
            if (currentTime >= nextQuoteTime) {
                _uiState.update { it.copy(canRequestNewQuote = true) }
                break
            }
            delay(1000)
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
}

data class QuoteUiState(
    val currentQuote: Quote? = null,
    val favorites: List<Quote> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val nextQuoteAvailableAt: Long? = null,
    val canRequestNewQuote: Boolean = true
)