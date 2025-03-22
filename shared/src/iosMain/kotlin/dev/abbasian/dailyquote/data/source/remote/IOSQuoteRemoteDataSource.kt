package dev.abbasian.dailyquote.data.source.remote

import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.data.model.QuoteDto
import dev.abbasian.dailyquote.data.remote.QuoteRemoteDataSource
import dev.abbasian.dailyquote.util.QuoteApiException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import platform.Foundation.NSLog

class IOSQuoteRemoteDataSource : QuoteRemoteDataSource {
    private val client = HttpClient(Darwin) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    override suspend fun fetchDailyQuote(): Quote {
        return try {
            val response = client.get("https://api.quotable.io/random")
            if (!response.status.isSuccess()) {
                throw QuoteApiException("Failed to fetch quote: ${response.status}")
            }

            val quoteDto = response.body<QuoteDto>()
            quoteDto.toQuote()
        } catch (e: Exception) {
            NSLog("Error fetching daily quote: ${e.message}")
            when (e) {
                is QuoteApiException -> throw e
                else -> throw QuoteApiException("Network error: ${e.message}", e)
            }
        }
    }

    override suspend fun fetchQuotes(): List<Quote> {
        val quotes = mutableListOf<Quote>()
        val exceptions = mutableListOf<Exception>()

        repeat(10) {
            try {
                val response = client.get("https://api.quotable.io/random")
                if (!response.status.isSuccess()) {
                    throw QuoteApiException("Failed to fetch quote: ${response.status}")
                }

                val quoteDto = response.body<QuoteDto>()
                quotes.add(quoteDto.toQuote())
            } catch (e: Exception) {
                NSLog("Skipping failed quote fetch: ${e.message}")
                exceptions.add(e)
            }
        }

        if (quotes.isEmpty()) {
            if (exceptions.isNotEmpty()) {
                throw QuoteApiException("Failed to fetch any quotes", exceptions.first())
            }
            return listOf(getDefaultQuote())
        }

        return quotes
    }

    private fun QuoteDto.toQuote(): Quote {
        return Quote(
            id = this._id,
            text = this.content,
            author = this.author,
            category = this.tags.firstOrNull() ?: "General",
            authorImageUrl = ""
        )
    }

    private fun getDefaultQuote(): Quote {
        return Quote(
            id = "default",
            text = "The best way to predict the future is to create it.",
            author = "Abraham Lincoln",
            category = "Inspiration",
            authorImageUrl = ""
        )
    }
}