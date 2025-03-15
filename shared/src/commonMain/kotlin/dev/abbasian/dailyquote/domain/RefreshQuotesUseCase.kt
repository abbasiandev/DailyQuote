package dev.abbasian.dailyquote.domain

import dev.abbasian.dailyquote.data.repository.QuoteRepository

class RefreshQuotesUseCase(private val repository: QuoteRepository) {
    suspend operator fun invoke(): Boolean {
        return repository.refreshQuotes()
    }
}