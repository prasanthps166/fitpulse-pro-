package com.fitpulse.pro.domain.progress

import com.fitpulse.pro.data.model.PersonalRecord
import com.fitpulse.pro.data.model.RecordType

data class PRDetectionResult(
    val exerciseName: String,
    val newOneRM: Float,
    val previousOneRM: Float?,
    val weightKg: Float,
    val reps: Int
)

data class PersonalRecordCandidate(
    val record: PersonalRecord,
    val detectionResult: PRDetectionResult
)

class PersonalRecordDetector {

    fun detect(
        exerciseId: Long,
        exerciseName: String,
        weightKg: Float,
        reps: Int,
        existingRecords: List<PersonalRecord>
    ): PersonalRecordCandidate? {
        val estimatedOneRM = estimateOneRM(weightKg, reps) ?: return null
        val existingMaxWeight = existingRecords
            .filter { it.exerciseId == exerciseId && it.recordType == RecordType.MAX_WEIGHT }
            .maxOfOrNull { it.value } ?: 0f

        if (estimatedOneRM <= existingMaxWeight) {
            return null
        }

        val previousOneRM = existingMaxWeight.takeIf { it > 0f }
        return PersonalRecordCandidate(
            record = PersonalRecord(
                exerciseId = exerciseId,
                exerciseName = exerciseName,
                recordType = RecordType.MAX_WEIGHT,
                value = estimatedOneRM,
                previousValue = previousOneRM
            ),
            detectionResult = PRDetectionResult(
                exerciseName = exerciseName,
                newOneRM = estimatedOneRM,
                previousOneRM = previousOneRM,
                weightKg = weightKg,
                reps = reps
            )
        )
    }

    fun estimateOneRM(weightKg: Float, reps: Int): Float? {
        if (weightKg <= 0f || reps <= 0) {
            return null
        }
        return if (reps == 1) {
            weightKg
        } else {
            weightKg * (1f + reps / 30f)
        }
    }
}
