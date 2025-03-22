package dev.abbasian.dailyquote.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen

class IOSScreenHeightProvider : ScreenHeightProvider {
    @OptIn(ExperimentalForeignApi::class)
    override fun getScreenHeight(): Int {
        return UIScreen.mainScreen.bounds.useContents { this.size.height.toInt() }
    }
}