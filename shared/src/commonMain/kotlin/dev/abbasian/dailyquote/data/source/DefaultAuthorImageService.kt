package dev.abbasian.dailyquote.data.source

import kotlin.math.absoluteValue

class DefaultAuthorImageService : AuthorImageService {
    private val authorImageConfig = mapOf(
        "Steve Jobs" to ImageConfig(ServiceType.ROBOHASH, seed = "stevejobs"),
        "Albert Einstein" to ImageConfig(ServiceType.DICEBEAR, seed = "einstein"),
        "Abraham Lincoln" to ImageConfig(ServiceType.UI_AVATARS, name = "Abraham+Lincoln"),
        "Mark Twain" to ImageConfig(ServiceType.ROBOHASH, seed = "marktwain"),
        "Eleanor Roosevelt" to ImageConfig(ServiceType.DICEBEAR, seed = "eleanor"),
        "Nelson Mandela" to ImageConfig(ServiceType.UI_AVATARS, name = "Nelson+Mandela"),
        "John Lennon" to ImageConfig(ServiceType.ROBOHASH, seed = "johnlennon"),
        "Maya Angelou" to ImageConfig(ServiceType.DICEBEAR, seed = "maya"),
        "Oscar Wilde" to ImageConfig(ServiceType.UI_AVATARS, name = "Oscar+Wilde"),
        "Marie Curie" to ImageConfig(ServiceType.ROBOHASH, seed = "mariecurie"),
        "Mahatma Gandhi" to ImageConfig(ServiceType.DICEBEAR, seed = "gandhi"),
        "Friedrich Nietzsche" to ImageConfig(ServiceType.UI_AVATARS, name = "Friedrich+Nietzsche")
    )

    private enum class ServiceType {
        ROBOHASH, DICEBEAR, UI_AVATARS
    }

    private data class ImageConfig(
        val service: ServiceType,
        val seed: String = "",
        val name: String = ""
    )

    override fun getAuthorImageUrl(author: String, imageSize: Int): String {
        val normalizedAuthor = author.trim()

        // Try direct match first
        authorImageConfig[normalizedAuthor]?.let { config ->
            return generateUrlFromConfig(config, normalizedAuthor, imageSize)
        }

        // Try case-insensitive match
        authorImageConfig.entries.firstOrNull {
            it.key.equals(normalizedAuthor, ignoreCase = true)
        }?.let { entry ->
            return generateUrlFromConfig(entry.value, normalizedAuthor, imageSize)
        }

        // Fallback using a rotation of the services
        val serviceIndex = author.hashCode().absoluteValue % 3

        return when (serviceIndex) {
            0 -> "https://robohash.org/${encode(author)}?size=${imageSize}x$imageSize"
            1 -> "https://avatars.dicebear.com/api/avataaars/${encode(author)}.svg?size=$imageSize"
            else -> "https://ui-avatars.com/api/?name=${encode(author)}&size=$imageSize"
        }
    }

    private fun generateUrlFromConfig(config: ImageConfig, author: String, imageSize: Int): String {
        return when (config.service) {
            ServiceType.ROBOHASH ->
                "https://robohash.org/${config.seed.ifEmpty { encode(author) }}?size=${imageSize}x$imageSize"

            ServiceType.DICEBEAR ->
                "https://avatars.dicebear.com/api/avataaars/${config.seed.ifEmpty { encode(author) }}.svg?size=$imageSize"

            ServiceType.UI_AVATARS ->
                "https://ui-avatars.com/api/?name=${config.name.ifEmpty { encode(author) }}&size=$imageSize"
        }
    }

    private fun encode(input: String): String =
        input.trim().replace(" ", "+").percentEncode()

    fun String.percentEncode(): String = buildString {
        for (char in this@percentEncode) {
            when {
                char.isLetterOrDigit() || "-._~".contains(char) -> append(char)
                else -> append("%${char.code.toString(16).uppercase()}")
            }
        }
    }
}