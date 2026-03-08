package com.fitpulse.pro.domain.progress

import com.fitpulse.pro.data.model.BodyMeasurement
import com.fitpulse.pro.data.model.DailyStats
import com.fitpulse.pro.data.model.PersonalRecord
import com.fitpulse.pro.data.model.ProgressPhoto
import com.fitpulse.pro.data.repository.FitPulseRepository
import com.fitpulse.pro.data.time.CurrentDayMonitor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressManager(
    private val repository: FitPulseRepository,
    private val calendarWorkoutDataBuilder: CalendarWorkoutDataBuilder,
    private val currentDayMonitor: CurrentDayMonitor
) {

    val latestMeasurement: Flow<BodyMeasurement?> = repository.getLatestMeasurement()
    val recentMeasurements: Flow<List<BodyMeasurement>> = repository.getRecentMeasurements(30)
    val allMeasurements: Flow<List<BodyMeasurement>> = repository.getAllMeasurements()
    val progressPhotos: Flow<List<ProgressPhoto>> = repository.getAllPhotos()
    val personalRecords: Flow<List<PersonalRecord>> = repository.getRecentRecords(20)
    val todayStats: Flow<DailyStats?> = currentDayMonitor.currentDay.flatMapLatest { day ->
        repository.getStatsForDate(day.key)
    }
    val weeklyStats: Flow<List<DailyStats>> = repository.getRecentStats(7)
    val maxStreak: Flow<Int?> = repository.getMaxStreak()

    suspend fun logMeasurement(measurement: BodyMeasurement) {
        repository.insertMeasurement(measurement)
    }

    suspend fun deleteMeasurement(measurement: BodyMeasurement) {
        repository.deleteMeasurement(measurement)
    }

    suspend fun addProgressPhoto(photo: ProgressPhoto) {
        repository.insertPhoto(photo)
    }

    suspend fun addPersonalRecord(record: PersonalRecord) {
        repository.insertRecord(record)
    }

    suspend fun loadCalendarData(monthOffset: Int): Map<String, Int> {
        val range = calendarWorkoutDataBuilder.monthRange(monthOffset)
        val workouts = repository.getWorkoutsBetweenDatesSync(range.startMillis, range.endMillis)
        return calendarWorkoutDataBuilder.buildCalendarData(workouts)
    }

    suspend fun updateSteps(steps: Int) {
        currentDayMonitor.refresh()
        val today = currentDayMonitor.currentDay.value.key
        val currentValue = repository.getStatsForDate(today).first()
        repository.insertOrUpdateStats(
            (currentValue ?: DailyStats(date = today)).copy(steps = steps)
        )
    }
}


