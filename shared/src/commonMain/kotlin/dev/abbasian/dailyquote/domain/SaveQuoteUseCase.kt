package dev.abbasian.dailyquote.domain

import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.data.repository.QuoteRepository

class SaveQuoteUseCase(private val quoteRepository: QuoteRepository) {
    suspend operator fun invoke(quote: Quote) {
        quoteRepository.saveLastViewedQuote(quote)
    }
}