package dev.abbasian.dailyquote.data.source.remote

import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.data.remote.QuoteRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AndroidQuoteRemoteDataSource : QuoteRemoteDataSource {
    private val client = HttpClient(Android) {
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
            val quoteDto = response.body<QuoteDto>()
            quoteDto.toQuote()
        } catch (e: Exception) {
            Quote(
                id = "default",
                text = "The best way to predict the future is to create it.",
                author = "Abraham Lincoln",
                category = "Inspiration"
            )
        }
    }

    override suspend fun fetchQuotes(): List<Quote> {
        val quotes = mutableListOf<Quote>()
        repeat(10) {
            try {
                val response = client.get("https://api.quotable.io/random")
                val quoteDto = response.body<QuoteDto>()
                quotes.add(quoteDto.toQuote())
            } catch (e: Exception) {
                // Skip failed quotes
            }
        }
        return quotes
    }

    private fun QuoteDto.toQuote(): Quote {
        return Quote(
            id = this._id,
            text = this.content,
            author = this.author,
            category = this.tags.firstOrNull() ?: "General"
        )
    }
}

@kotlinx.serialization.Serializable
data class QuoteDto(
    val _id: String,
    val content: String,
    val author: String,
    val tags: List<String>
)