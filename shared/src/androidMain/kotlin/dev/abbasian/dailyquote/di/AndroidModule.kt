package dev.abbasian.dailyquote.di

import android.content.Context
import dev.abbasian.dailyquote.data.remote.QuoteRemoteDataSource
import dev.abbasian.dailyquote.data.source.QuoteLocalDataSource
import dev.abbasian.dailyquote.data.source.local.AndroidQuoteLocalDataSource
import dev.abbasian.dailyquote.data.source.remote.AndroidQuoteRemoteDataSource
import dev.abbasian.dailyquote.util.AndroidScreenHeightProvider
import dev.abbasian.dailyquote.util.ScreenHeightProvider
import org.koin.dsl.module

fun androidModule(context: Context) = module {
    single<QuoteLocalDataSource> { AndroidQuoteLocalDataSource(context) }
    single<QuoteRemoteDataSource> { AndroidQuoteRemoteDataSource() }
    single<ScreenHeightProvider> { AndroidScreenHeightProvider(context) }
}