package dev.abbasian.dailyquote.data.source

import kotlin.math.absoluteValue

class DefaultAuthorImageService : AuthorImageService {
    private val authorDefaultImageBaseUrls = mapOf(
        "Steve Jobs" to "https://i.pravatar.cc/ID?img=13",
        "Albert Einstein" to "https://i.pravatar.cc/ID?img=11",
        "Abraham Lincoln" to "https://i.pravatar.cc/ID?img=53",
        "Mark Twain" to "https://i.pravatar.cc/ID?img=67",
        "Eleanor Roosevelt" to "https://i.pravatar.cc/ID?img=29",
        "Nelson Mandela" to "https://i.pravatar.cc/ID?img=12",
        "John Lennon" to "https://i.pravatar.cc/ID?img=15",
        "Maya Angelou" to "https://i.pravatar.cc/ID?img=32",
        "Oscar Wilde" to "https://i.pravatar.cc/ID?img=68",
        "Marie Curie" to "https://i.pravatar.cc/ID?img=5",
        "Mahatma Gandhi" to "https://i.pravatar.cc/ID?img=18",
        "Friedrich Nietzsche" to "https://i.pravatar.cc/ID?img=16"
    )

    override fun getAuthorImageUrl(author: String, imageSize: Int): String {
        authorDefaultImageBaseUrls[author]?.let {
            return it.replace("ID", imageSize.toString())
        }

        val seed = author.hashCode().rem(70).coerceIn(1, 70)
        val serviceIndex = (author.hashCode() % 4).absoluteValue

        return when (serviceIndex) {
            0 -> "https://i.pravatar.cc/${imageSize}?img=$seed"
            1 -> "https://robohash.org/${urlEncode(author)}?size=${imageSize}x${imageSize}"
            2 -> "https://avatars.dicebear.com/api/avataaars/${urlEncode(author)}.svg?width=${imageSize}&height=${imageSize}"
            else -> "https://ui-avatars.com/api/?name=${urlEncode(author)}&size=${imageSize}&background=random"
        }
    }

    private fun urlEncode(string: String): String {
        val allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"
        return string.map { char ->
            if (allowedCharacters.contains(char)) char.toString()
            else "%${char.code.toString(16).padStart(2, '0').uppercase()}"
        }.joinToString("")
    }
}