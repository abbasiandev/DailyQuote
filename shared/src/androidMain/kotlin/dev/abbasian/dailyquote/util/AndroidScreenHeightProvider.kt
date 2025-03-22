package dev.abbasian.dailyquote.util

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

class AndroidScreenHeightProvider(private val context: Context) : ScreenHeightProvider {
    override fun getScreenHeight(): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return metrics.heightPixels
    }
}