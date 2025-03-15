package dev.abbasian.dailyquote.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val id: String,
    val text: String,
    val author: String,
    val category: String,
    var isFavorite: Boolean = false
)
