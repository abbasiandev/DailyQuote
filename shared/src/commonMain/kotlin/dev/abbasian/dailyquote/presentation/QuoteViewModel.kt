package dev.abbasian.dailyquote.presentation

import androidx.datastore.core.Closeable
import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.data.preferences.QuoteTimePreferences
import dev.abbasian.dailyquote.data.service.CommonTimeRemainingService
import dev.abbasian.dailyquote.data.service.TimeRemainingService
import dev.abbasian.dailyquote.domain.CheckQuoteAvailabilityUseCase
import dev.abbasian.dailyquote.domain.GetDailyQuoteUseCase
import dev.abbasian.dailyquote.domain.GetFavoritesUseCase
import dev.abbasian.dailyquote.domain.GetNextQuoteTimeUseCase
import dev.abbasian.dailyquote.domain.GetRandomQuoteUseCase
import dev.abbasian.dailyquote.domain.GetSavedQuoteUseCase
import dev.abbasian.dailyquote.domain.RefreshQuotesUseCase
import dev.abbasian.dailyquote.domain.SaveQuoteUseCase
import dev.abbasian.dailyquote.domain.ToggleFavoriteUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

class QuoteViewModel(
    private val getDailyQuoteUseCase: GetDailyQuoteUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getRandomQuoteUseCase: GetRandomQuoteUseCase,
    private val refreshQuotesUseCase: RefreshQuotesUseCase,
    private val checkQuoteAvailabilityUseCase: CheckQuoteAvailabilityUseCase,
    private val getNextQuoteTimeUseCase: GetNextQuoteTimeUseCase,
    private val getSavedQuoteUseCase: GetSavedQuoteUseCase,
    private val saveQuoteUseCase: SaveQuoteUseCase,
    private val quoteTimePreferences: QuoteTimePreferences,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _uiState = MutableStateFlow(QuoteUiState())
    val uiState: StateFlow<QuoteUiState> = _uiState.asStateFlow()

    val _quoteTimePreferences: QuoteTimePreferences
        get() = quoteTimePreferences

    private val timeRemainingService = CommonTimeRemainingService(_quoteTimePreferences, coroutineScope)

    init {
        _uiState.update { it.copy(timeRemainingService = timeRemainingService) }

        coroutineScope.launch {
            timeRemainingService.startCountdown()
        }

        loadInitialQuote()
        loadFavorites()
        observeFavorites()
    }

    private fun loadInitialQuote() {
        coroutineScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val savedQuote = getSavedQuoteUseCase()
                val canRequestNew = checkQuoteAvailabilityUseCase()
                val nextQuoteTime = getNextQuoteTimeUseCase() ?: run {
                    Clock.System.now().plus(24, DateTimeUnit.HOUR).toEpochMilliseconds()
                }

                if (!canRequestNew) {
                    if (nextQuoteTime == null) {
                        println("Invalid state: can't request but no next time")
                    }
                }

                val finalQuote = savedQuote ?: getDailyQuoteUseCase().also {
                    saveQuoteUseCase(it)
                }

                _uiState.update {
                    it.copy(
                        currentQuote = finalQuote,
                        isLoading = false,
                        error = null,
                        nextQuoteAvailableAt = nextQuoteTime,
                        canRequestNewQuote = canRequestNew
                    )
                }

                if (!canRequestNew) {
                    monitorQuoteAvailability(nextQuoteTime)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load quote: ${
                            e.message?.replace(
                                "java.lang.IllegalStateException: ",
                                ""
                            )
                        }"
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

    fun loadNextDailyQuote() {
        coroutineScope.launch {
            if (!uiState.value.canRequestNewQuote) return@launch

            _uiState.update { it.copy(isLoading = true) }

            try {
                val quote = getDailyQuoteUseCase()
                val nextQuoteTime = getNextQuoteTimeUseCase() ?: run {
                    Clock.System.now().plus(24, DateTimeUnit.HOUR).toEpochMilliseconds()
                }

                saveQuoteUseCase(quote)

                _uiState.update {
                    it.copy(
                        currentQuote = quote,
                        isLoading = false,
                        error = null,
                        nextQuoteAvailableAt = nextQuoteTime,
                        canRequestNewQuote = false
                    )
                }

                monitorQuoteAvailability(nextQuoteTime)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load quote: ${e.message?.replace("java.lang.IllegalStateException: ", "")}"
                    )
                }
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
                    // Don't load a new quote on refresh, just reload the existing saved quote
                    loadInitialQuote()
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

    fun observeState(callback: (QuoteUiState) -> Unit): () -> Unit {
        val job = Job()
        CoroutineScope(job).launch {
            uiState.collect { callback(it) }
        }
        return { job.cancel() }
    }
}

data class QuoteUiState(
    val currentQuote: Quote? = null,
    val favorites: List<Quote> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val nextQuoteAvailableAt: Long? = null,
    val canRequestNewQuote: Boolean = true,
    val timeRemainingService: TimeRemainingService? = null
)