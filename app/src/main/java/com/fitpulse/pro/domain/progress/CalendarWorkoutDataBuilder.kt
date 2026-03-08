package com.fitpulse.pro.domain.progress

import com.fitpulse.pro.data.model.Workout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class MonthRange(
    val startMillis: Long,
    val endMillis: Long
)

class CalendarWorkoutDataBuilder(
    private val locale: Locale = Locale.US,
    private val timeZone: TimeZone = TimeZone.getDefault()
) {

    fun monthRange(monthOffset: Int, referenceTimeMillis: Long = System.currentTimeMillis()): MonthRange {
        val cal = Calendar.getInstance(timeZone).apply {
            timeInMillis = referenceTimeMillis
            add(Calendar.MONTH, monthOffset)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfMonth = cal.timeInMillis

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)

        return MonthRange(startMillis = startOfMonth, endMillis = cal.timeInMillis)
    }

    fun buildCalendarData(workouts: List<Workout>): Map<String, Int> {
        val formatter = SimpleDateFormat(DATE_PATTERN, locale).apply {
            timeZone = timeZone
        }

        return workouts.groupingBy { formatter.format(Date(it.createdAt)) }.eachCount()
    }

    private companion object {
        const val DATE_PATTERN = "yyyy-MM-dd"
    }
}
