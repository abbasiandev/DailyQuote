package dev.abbasian.dailyquote.domain

import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.data.repository.QuoteRepository

class GetRandomQuoteUseCase(private val repository: QuoteRepository) {
    suspend operator fun invoke(): Quote {
        return repository.getRandomQuote()
    }
}