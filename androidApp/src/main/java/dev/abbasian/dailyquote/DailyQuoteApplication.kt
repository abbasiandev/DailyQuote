package dev.abbasian.dailyquote

import android.app.Application
import dev.abbasian.dailyquote.di.androidModule
import dev.abbasian.dailyquote.di.initKoin

class DailyQuoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(androidModule(applicationContext))
    }
}