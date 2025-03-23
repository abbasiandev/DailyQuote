package dev.abbasian.dailyquote.domain

import dev.abbasian.dailyquote.data.repository.QuoteRepository

class GetNextQuoteTimeUseCase(private val repository: QuoteRepository) {
    suspend operator fun invoke(): Long? {
        return repository.getNextQuoteAvailableAt()
    }
}