package dev.abbasian.dailyquote

import android.app.Application
import dev.abbasian.dailyquote.data.service.PlatformTimeService
import dev.abbasian.dailyquote.di.androidModule
import dev.abbasian.dailyquote.di.initKoin

class DailyQuoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        PlatformTimeService.initializeWithContext(applicationContext)

        initKoin(androidModule(applicationContext))
    }
}