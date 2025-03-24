package dev.abbasian.dailyquote.data.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import dev.abbasian.dailyquote.data.preferences.AndroidQuoteTimePreferences
import dev.abbasian.dailyquote.data.preferences.QuoteTimePreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

actual class PlatformTimeService private constructor(private val context: Context) {
    private val handler = Handler(Looper.getMainLooper())

    actual fun registerBackgroundWork(intervalMs: Long, action: () -> Unit): Any {
        val runnable = object : Runnable {
            override fun run() {
                action()
                handler.postDelayed(this, intervalMs)
            }
        }

        // Start the service for background execution
        val serviceIntent = Intent(context, QuoteTimerService::class.java)
        context.startService(serviceIntent)

        handler.post(runnable)
        return runnable
    }

    actual fun unregisterBackgroundWork(handle: Any) {
        if (handle is Runnable) {
            handler.removeCallbacks(handle)

            // Stop the service
            val serviceIntent = Intent(context, QuoteTimerService::class.java)
            context.stopService(serviceIntent)
        }
    }

    actual fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    actual companion object {
        private var instance: PlatformTimeService? = null

        actual fun create(): PlatformTimeService {
            if (instance == null) {
                throw IllegalStateException("PlatformTimeService must be initialized with initializeWithContext first")
            }
            return instance!!
        }

        fun initializeWithContext(context: Context): PlatformTimeService {
            if (instance == null) {
                instance = PlatformTimeService(context.applicationContext)
            }
            return instance!!
        }
    }

}

class QuoteTimerService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var quoteTimePreferences: QuoteTimePreferences
    private lateinit var timeRemainingService: CommonTimeRemainingService
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()

        quoteTimePreferences = AndroidQuoteTimePreferences(applicationContext)
        timeRemainingService = CommonTimeRemainingService(quoteTimePreferences, serviceScope)

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "DailyQuote::QuoteTimerWakeLock"
        ).apply {
            acquire(24 * 60 * 60 * 1000L)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        timeRemainingService.stopCountdown()
        wakeLock?.release()
        super.onDestroy()
    }
}