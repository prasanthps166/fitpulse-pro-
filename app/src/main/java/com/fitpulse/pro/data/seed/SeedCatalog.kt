package com.fitpulse.pro.data.seed

import com.fitpulse.pro.data.model.Achievement
import com.fitpulse.pro.data.model.Exercise
import com.fitpulse.pro.data.model.WorkoutTemplate

class SeedCatalog {
    fun exercises(): List<Exercise> = seedExercises()

    fun workoutTemplates(): List<WorkoutTemplate> = seedWorkoutTemplates()

    fun achievements(): List<Achievement> = seedAchievements()
}
