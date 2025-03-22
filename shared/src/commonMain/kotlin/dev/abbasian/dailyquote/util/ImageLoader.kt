package dev.abbasian.dailyquote.util

import coil3.ImageLoader
import coil3.PlatformContext

expect fun getPlatformImageLoader(context: PlatformContext): ImageLoader

fun getAsyncImageLoader(context: PlatformContext): ImageLoader {
    return getPlatformImageLoader(context)
}