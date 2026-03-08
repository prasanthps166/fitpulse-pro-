package com.fitpulse.pro.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ExerciseSetTest {

    @Test
    fun estimatedOneRepMax_returnsZeroWithoutWeightOrReps() {
        val noWeight = ExerciseSet(setNumber = 1, reps = 5, weightKg = 0f)
        val noReps = ExerciseSet(setNumber = 1, reps = 0, weightKg = 100f)

        assertEquals(0f, noWeight.estimatedOneRepMax(), 0.001f)
        assertEquals(0f, noReps.estimatedOneRepMax(), 0.001f)
    }

    @Test
    fun estimatedOneRepMax_usesEpleyFormulaForMultipleReps() {
        val set = ExerciseSet(setNumber = 1, reps = 5, weightKg = 100f)

        assertEquals(116.66667f, set.estimatedOneRepMax(), 0.001f)
    }
}
