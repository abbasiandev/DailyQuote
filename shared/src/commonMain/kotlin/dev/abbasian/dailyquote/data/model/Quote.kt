package dev.abbasian.dailyquote.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val id: String,
    val text: String,
    val author: String,
    val category: String,
    val authorImageUrl: String,
    var isFavorite: Boolean = false
)

@Serializable
data class QuoteDto(
    val _id: String,
    val content: String,
    val author: String,
    val tags: List<String>
)

@Serializable
data class UnsplashImageDto(
    val urls: UnsplashUrls
)

@Serializable
data class UnsplashUrls(
    val small: String
)
