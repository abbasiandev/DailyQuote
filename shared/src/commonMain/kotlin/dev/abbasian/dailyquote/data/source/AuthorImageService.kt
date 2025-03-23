package dev.abbasian.dailyquote.data.source

interface AuthorImageService {
    fun getAuthorImageUrl(author: String, imageSize: Int): String
}