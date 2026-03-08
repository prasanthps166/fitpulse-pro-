package com.fitpulse.pro.data.time

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class DayWindow(
    val key: String,
    val startMillis: Long,
    val endMillis: Long
)

class AppDateProvider(
    private val locale: Locale = Locale.getDefault(),
    private val timeZone: TimeZone = TimeZone.getDefault()
) {
    fun dayWindow(date: Date = Date()): DayWindow = DayWindow(
        key = dateKey(date),
        startMillis = startOfDay(date),
        endMillis = endOfDay(date)
    )

    fun dateKey(date: Date = Date()): String =
        SimpleDateFormat(DATE_PATTERN, locale).apply {
            this.timeZone = timeZone
        }.format(date)

    fun startOfDay(date: Date = Date()): Long =
        calendar(date).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    fun endOfDay(date: Date = Date()): Long =
        calendar(date).apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

    private fun calendar(date: Date): Calendar =
        Calendar.getInstance(timeZone, locale).apply {
            time = date
        }

    private companion object {
        const val DATE_PATTERN = "yyyy-MM-dd"
    }
}
