package com.fitpulse.pro.domain.progress

import com.fitpulse.pro.data.model.PersonalRecord
import com.fitpulse.pro.data.model.RecordType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PersonalRecordDetectorTest {

    private val detector = PersonalRecordDetector()

    @Test
    fun detect_createsCandidateWhenEstimatedOneRepMaxImproves() {
        val candidate = detector.detect(
            exerciseId = 1L,
            exerciseName = "Bench Press",
            weightKg = 100f,
            reps = 5,
            existingRecords = listOf(
                PersonalRecord(
                    exerciseId = 1L,
                    exerciseName = "Bench Press",
                    recordType = RecordType.MAX_WEIGHT,
                    value = 110f
                )
            )
        )

        assertNotNull(candidate)
        assertEquals(116.66667f, candidate?.record?.value ?: 0f, 0.0001f)
        assertEquals(110f, candidate?.record?.previousValue ?: 0f, 0f)
        assertEquals("Bench Press", candidate?.detectionResult?.exerciseName)
    }

    @Test
    fun detect_returnsNullWhenInputIsInvalidOrNotBetterThanExistingRecord() {
        val invalid = detector.detect(
            exerciseId = 1L,
            exerciseName = "Bench Press",
            weightKg = 0f,
            reps = 5,
            existingRecords = emptyList()
        )
        val notBetter = detector.detect(
            exerciseId = 1L,
            exerciseName = "Bench Press",
            weightKg = 90f,
            reps = 3,
            existingRecords = listOf(
                PersonalRecord(
                    exerciseId = 1L,
                    exerciseName = "Bench Press",
                    recordType = RecordType.MAX_WEIGHT,
                    value = 110f
                )
            )
        )

        assertNull(invalid)
        assertNull(notBetter)
    }
}
