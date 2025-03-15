package dev.abbasian.dailyquote.domain

import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.data.repository.QuoteRepository

class GetDailyQuoteUseCase(private val repository: QuoteRepository) {
    suspend operator fun invoke(): Quote {
        return repository.getDailyQuote()
    }
}