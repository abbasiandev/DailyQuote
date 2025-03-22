package dev.abbasian.dailyquote.util

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.NetworkFetcher
import coil3.network.okhttp.asNetworkClient
import coil3.request.crossfade
import coil3.util.DebugLogger
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@OptIn(ExperimentalCoilApi::class)
actual fun getPlatformImageLoader(context: PlatformContext): ImageLoader {
    val sslContext = SSLContext.getInstance("SSL").apply {
        init(null, arrayOf(trustAllManager), SecureRandom())
    }

    val okHttpClient = OkHttpClient.Builder()
        .sslSocketFactory(sslContext.socketFactory, trustAllManager)
        .hostnameVerifier { _, _ -> true }
        .build()

    return ImageLoader.Builder(context)
        .components {
            add(NetworkFetcher.Factory(
                networkClient = { okHttpClient.asNetworkClient() }
            ))
        }
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, 0.3)
                .strongReferencesEnabled(true)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizeBytes(1024L * 1024 * 1024) // 1GB
                .build()
        }
        .crossfade(true)
        .logger(DebugLogger())
        .build()
}

private val trustAllManager = object : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
    override fun getAcceptedIssuers() = emptyArray<X509Certificate>()
}