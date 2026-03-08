package com.fitpulse.pro.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class UtilsTest {

    @Test
    fun formatNumber_compactsLargeValues() {
        assertEquals("950", Utils.formatNumber(950))
        assertEquals("1.5K", Utils.formatNumber(1_500))
        assertEquals("2.3M", Utils.formatNumber(2_300_000))
    }

    @Test
    fun formatHydrationAmount_usesMlAndLiters() {
        assertEquals("750ml", Utils.formatHydrationAmount(750))
        assertEquals("2L", Utils.formatHydrationAmount(2_000))
        assertEquals("2.5L", Utils.formatHydrationAmount(2_500))
    }

    @Test
    fun formatDuration_handlesMinutesAndHours() {
        assertEquals("45m", Utils.formatDuration(45))
        assertEquals("1h", Utils.formatDuration(60))
        assertEquals("1h 30m", Utils.formatDuration(90))
    }
}
