package com.fitpulse.pro.domain.progress

import com.fitpulse.pro.data.model.Workout
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

class CalendarWorkoutDataBuilderTest {

    private val builder = CalendarWorkoutDataBuilder(
        locale = Locale.US,
        timeZone = TimeZone.getTimeZone("UTC")
    )

    @Test
    fun monthRange_returnsExpectedBoundsForOffsetMonth() {
        val range = builder.monthRange(
            monthOffset = -1,
            referenceTimeMillis = 1773556800000L
        )

        assertEquals(1769904000000L, range.startMillis)
        assertEquals(1772323199999L, range.endMillis)
    }

    @Test
    fun buildCalendarData_groupsWorkoutsByDate() {
        val workouts = listOf(
            Workout(name = "Push", createdAt = 1740823200000L),
            Workout(name = "Pull", createdAt = 1740830400000L),
            Workout(name = "Legs", createdAt = 1740913200000L)
        )

        val data = builder.buildCalendarData(workouts)

        assertEquals(2, data["2025-03-01"])
        assertEquals(1, data["2025-03-02"])
    }
}
