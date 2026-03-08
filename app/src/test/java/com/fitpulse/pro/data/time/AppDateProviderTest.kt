package com.fitpulse.pro.data.time

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class AppDateProviderTest {

    private val provider = AppDateProvider(
        locale = Locale.US,
        timeZone = TimeZone.getTimeZone("UTC")
    )

    @Test
    fun dayWindow_returnsExpectedKeyAndBounds() {
        val referenceDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US).apply {
            set(2025, Calendar.MARCH, 7, 14, 35, 42)
            set(Calendar.MILLISECOND, 123)
        }.time

        val dayWindow = provider.dayWindow(referenceDate)

        assertEquals("2025-03-07", dayWindow.key)
        assertEquals(1741305600000L, dayWindow.startMillis)
        assertEquals(1741391999999L, dayWindow.endMillis)
    }
}
