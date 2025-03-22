package dev.abbasian.dailyquote.util

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.datetime.Clock

val Int.hours: Duration get() = toDuration(DurationUnit.HOURS)

fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()

fun Long.toTimeString(): String {
    val hours = (this / 3_600_000) % 24
    val minutes = (this / 60_000) % 60
    return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
}