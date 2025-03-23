package dev.abbasian.dailyquote.domain

import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.data.repository.QuoteRepository

class GetSavedQuoteUseCase(private val quoteRepository: QuoteRepository) {
    suspend operator fun invoke(): Quote? {
        return quoteRepository.getLastViewedQuote()
    }
}