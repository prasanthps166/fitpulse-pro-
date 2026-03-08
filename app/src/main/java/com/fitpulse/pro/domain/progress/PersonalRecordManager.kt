package com.fitpulse.pro.domain.progress

import com.fitpulse.pro.data.repository.FitPulseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

class PersonalRecordManager(
    private val repository: FitPulseRepository,
    private val personalRecordDetector: PersonalRecordDetector
) {

    private val _newPRDetected = MutableStateFlow<PRDetectionResult?>(null)
    val newPRDetected: StateFlow<PRDetectionResult?> = _newPRDetected

    fun clearNewPR() {
        _newPRDetected.value = null
    }

    suspend fun detectAndStorePR(
        exerciseId: Long,
        exerciseName: String,
        weightKg: Float,
        reps: Int
    ): PRDetectionResult? {
        val existingRecords = repository.getAllRecords().first()
        val candidate = personalRecordDetector.detect(
            exerciseId = exerciseId,
            exerciseName = exerciseName,
            weightKg = weightKg,
            reps = reps,
            existingRecords = existingRecords
        ) ?: return null

        repository.insertRecord(candidate.record)
        _newPRDetected.value = candidate.detectionResult
        return candidate.detectionResult
    }
}
