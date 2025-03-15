package dev.abbasian.dailyquote

import android.app.Application
import dev.abbasian.dailyquote.data.remote.QuoteRemoteDataSource
import dev.abbasian.dailyquote.data.source.QuoteLocalDataSource
import dev.abbasian.dailyquote.data.source.local.AndroidQuoteLocalDataSource
import dev.abbasian.dailyquote.data.source.remote.AndroidQuoteRemoteDataSource
import dev.abbasian.dailyquote.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class DailyQuoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(module {
            single<QuoteLocalDataSource> { AndroidQuoteLocalDataSource(androidContext()) }
            single<QuoteRemoteDataSource> { AndroidQuoteRemoteDataSource() }
        })
    }
}