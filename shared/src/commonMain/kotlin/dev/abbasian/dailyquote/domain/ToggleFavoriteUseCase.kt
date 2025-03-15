package dev.abbasian.dailyquote.domain

import dev.abbasian.dailyquote.data.repository.QuoteRepository

class ToggleFavoriteUseCase(private val repository: QuoteRepository) {
    suspend operator fun invoke(quoteId: String): Boolean {
        return repository.toggleFavorite(quoteId)
    }
}