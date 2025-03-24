package dev.abbasian.dailyquote.data.service

import platform.Foundation.NSDate
import platform.Foundation.NSTimer
import platform.Foundation.timeIntervalSince1970

actual class PlatformTimeService {
    private var timer: NSTimer? = null

    actual fun registerBackgroundWork(intervalMs: Long, action: () -> Unit): Any {
        val timerInterval = (intervalMs / 1000.0)
        val timer = NSTimer.scheduledTimerWithTimeInterval(
            timerInterval,
            true,
            { action() }
        )
        this.timer = timer
        return timer
    }

    actual fun unregisterBackgroundWork(handle: Any) {
        if (handle is NSTimer) {
            handle.invalidate()
        }
        timer = null
    }

    actual fun getCurrentTimeMillis(): Long {
        return (NSDate().timeIntervalSince1970 * 1000).toLong()
    }

    actual companion object {
        actual fun create(): PlatformTimeService {
            return PlatformTimeService()
        }
    }
}
