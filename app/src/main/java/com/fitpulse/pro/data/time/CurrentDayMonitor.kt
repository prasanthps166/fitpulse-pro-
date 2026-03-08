package com.fitpulse.pro.data.time

import java.util.Date
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CurrentDayMonitor(
    scope: CoroutineScope,
    private val dateProvider: AppDateProvider,
    private val pollIntervalMs: Long = DEFAULT_POLL_INTERVAL_MS
) {
    private val _currentDay = MutableStateFlow(dateProvider.dayWindow())
    val currentDay: StateFlow<DayWindow> = _currentDay.asStateFlow()

    init {
        scope.launch {
            while (isActive) {
                delay(pollIntervalMs)
                refresh()
            }
        }
    }

    fun refresh(now: Date = Date()) {
        val next = dateProvider.dayWindow(now)
        if (next.key != _currentDay.value.key) {
            _currentDay.value = next
        }
    }

    private companion object {
        const val DEFAULT_POLL_INTERVAL_MS = 60_000L
    }
}
