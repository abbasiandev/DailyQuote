package dev.abbasian.dailyquote.domain

import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.data.repository.QuoteRepository
import kotlinx.coroutines.flow.Flow

class GetFavoritesUseCase(private val repository: QuoteRepository) {
    suspend operator fun invoke(): List<Quote> {
        return repository.getFavorites()
    }

    fun observe(): Flow<List<Quote>> {
        return repository.observeFavorites()
    }
}