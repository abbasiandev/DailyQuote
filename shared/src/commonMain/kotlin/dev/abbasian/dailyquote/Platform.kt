package dev.abbasian.dailyquote

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform