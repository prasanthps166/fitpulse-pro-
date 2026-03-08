package com.fitpulse.pro.data.seed

import com.fitpulse.pro.data.model.Difficulty
import com.fitpulse.pro.data.model.TemplateExercise
import com.fitpulse.pro.data.model.WorkoutTemplate

internal fun seedWorkoutTemplates(): List<WorkoutTemplate> = listOf(

        // ====== BEGINNER ROUTINES ======
        WorkoutTemplate(
            name = "StrongLifts 5x5 A",
            description = "Classic strength foundation workout A",
            category = "Beginner",
            exercises = listOf(
                TemplateExercise(exerciseId = 41, exerciseName = "Barbell Squat", targetSets = 5, targetReps = 5, restSeconds = 180),
                TemplateExercise(exerciseId = 1, exerciseName = "Barbell Bench Press", targetSets = 5, targetReps = 5, restSeconds = 180),
                TemplateExercise(exerciseId = 13, exerciseName = "Barbell Row", targetSets = 5, targetReps = 5, restSeconds = 180)
            ),
            estimatedDurationMinutes = 45,
            difficulty = Difficulty.BEGINNER,
            isPreset = true
        ),
        WorkoutTemplate(
            name = "StrongLifts 5x5 B",
            description = "Classic strength foundation workout B",
            category = "Beginner",
            exercises = listOf(
                TemplateExercise(exerciseId = 41, exerciseName = "Barbell Squat", targetSets = 5, targetReps = 5, restSeconds = 180),
                TemplateExercise(exerciseId = 21, exerciseName = "Overhead Press", targetSets = 5, targetReps = 5, restSeconds = 180),
                TemplateExercise(exerciseId = 11, exerciseName = "Deadlift", targetSets = 1, targetReps = 5, restSeconds = 180)
            ),
            estimatedDurationMinutes = 45,
            difficulty = Difficulty.BEGINNER,
            isPreset = true
        ),
        WorkoutTemplate(
            name = "Full Body Starter",
            description = "Perfect for those new to the gym",
            category = "Beginner",
            exercises = listOf(
                TemplateExercise(exerciseId = 3, exerciseName = "Push-Ups", targetSets = 3, targetReps = 10, restSeconds = 60),
                TemplateExercise(exerciseId = 41, exerciseName = "Barbell Squat", targetSets = 3, targetReps = 10, restSeconds = 90),
                TemplateExercise(exerciseId = 14, exerciseName = "Lat Pulldown", targetSets = 3, targetReps = 10, restSeconds = 60),
                TemplateExercise(exerciseId = 56, exerciseName = "Plank", targetSets = 3, targetReps = 30, restSeconds = 45)
            ),
            estimatedDurationMinutes = 35,
            difficulty = Difficulty.BEGINNER,
            isPreset = true
        ),
        WorkoutTemplate(
            name = "Gym Foundation A",
            description = "Simple full-body gym session with beginner-friendly machine and dumbbell options",
            category = "Beginner",
            exercises = listOf(
                TemplateExercise(exerciseId = 51, exerciseName = "Goblet Squats", targetSets = 3, targetReps = 10, restSeconds = 75),
                TemplateExercise(exerciseId = 6, exerciseName = "Dumbbell Bench Press", targetSets = 3, targetReps = 10, restSeconds = 75),
                TemplateExercise(exerciseId = 15, exerciseName = "Seated Cable Row", targetSets = 3, targetReps = 10, restSeconds = 75),
                TemplateExercise(exerciseId = 42, exerciseName = "Romanian Deadlift", targetSets = 3, targetReps = 8, restSeconds = 90),
                TemplateExercise(exerciseId = 56, exerciseName = "Plank", targetSets = 3, targetReps = 30, restSeconds = 45)
            ),
            estimatedDurationMinutes = 40,
            difficulty = Difficulty.BEGINNER,
            isPreset = true
        ),
        WorkoutTemplate(
            name = "Dumbbell Full Body",
            description = "A low-friction full-body session when you only have dumbbells and bodyweight",
            category = "General Fitness",
            exercises = listOf(
                TemplateExercise(exerciseId = 51, exerciseName = "Goblet Squats", targetSets = 3, targetReps = 12, restSeconds = 60),
                TemplateExercise(exerciseId = 6, exerciseName = "Dumbbell Bench Press", targetSets = 3, targetReps = 10, restSeconds = 75),
                TemplateExercise(exerciseId = 16, exerciseName = "Dumbbell Row", targetSets = 3, targetReps = 10, restSeconds = 60),
                TemplateExercise(exerciseId = 44, exerciseName = "Bulgarian Split Squat", targetSets = 2, targetReps = 10, restSeconds = 75),
                TemplateExercise(exerciseId = 56, exerciseName = "Plank", targetSets = 3, targetReps = 30, restSeconds = 45)
            ),
            estimatedDurationMinutes = 35,
            difficulty = Difficulty.BEGINNER,
            isPreset = true
        ),
        WorkoutTemplate(
            name = "General Fitness Express",
            description = "Fast full-body training for busy days when consistency matters more than complexity",
            category = "General Fitness",
            exercises = listOf(
                TemplateExercise(exerciseId = 3, exerciseName = "Push-Ups", targetSets = 3, targetReps = 12, restSeconds = 45),
                TemplateExercise(exerciseId = 51, exerciseName = "Goblet Squats", targetSets = 3, targetReps = 12, restSeconds = 60),
                TemplateExercise(exerciseId = 14, exerciseName = "Lat Pulldown", targetSets = 3, targetReps = 10, restSeconds = 60),
                TemplateExercise(exerciseId = 44, exerciseName = "Bulgarian Split Squat", targetSets = 2, targetReps = 10, restSeconds = 60),
                TemplateExercise(exerciseId = 56, exerciseName = "Plank", targetSets = 3, targetReps = 30, restSeconds = 45)
            ),
            estimatedDurationMinutes = 30,
            difficulty = Difficulty.BEGINNER,
            isPreset = true
        ),

        // ====== INTERMEDIATE SPLITS (Upper/Lower) ======
        WorkoutTemplate(
            name = "Upper Power",
            description = "Heavy upper body compound lifting",
            category = "Upper/Lower",
            exercises = listOf(
                TemplateExercise(exerciseId = 1, exerciseName = "Barbell Bench Press", targetSets = 4, targetReps = 6, restSeconds = 120),
                TemplateExercise(exerciseId = 13, exerciseName = "Barbell Row", targetSets = 4, targetReps = 6, restSeconds = 120),
                TemplateExercise(exerciseId = 21, exerciseName = "Overhead Press", targetSets = 3, targetReps = 8, restSeconds = 90),
                TemplateExercise(exerciseId = 12, exerciseName = "Pull-Ups", targetSets = 3, targetReps = 8, restSeconds = 90),
                TemplateExercise(exerciseId = 31, exerciseName = "Barbell Curl", targetSets = 3, targetReps = 10, restSeconds = 60),
                TemplateExercise(exerciseId = 34, exerciseName = "Skull Crushers", targetSets = 3, targetReps = 10, restSeconds = 60)
            ),
            estimatedDurationMinutes = 60,
            difficulty = Difficulty.INTERMEDIATE,
            isPreset = true
        ),
        WorkoutTemplate(
            name = "Lower Power",
            description = "Heavy lower body compound lifting",
            category = "Upper/Lower",
            exercises = listOf(
                TemplateExercise(exerciseId = 41, exerciseName = "Barbell Squat", targetSets = 4, targetReps = 6, restSeconds = 180),
                TemplateExercise(exerciseId = 42, exerciseName = "Romanian Deadlift", targetSets = 3, targetReps = 8, restSeconds = 120),
                TemplateExercise(exerciseId = 43, exerciseName = "Leg Press", targetSets = 3, targetReps = 10, restSeconds = 90),
                TemplateExercise(exerciseId = 44, exerciseName = "Bulgarian Split Squat", targetSets = 3, targetReps = 10, restSeconds = 90),
                TemplateExercise(exerciseId = 45, exerciseName = "Calf Raises", targetSets = 4, targetReps = 15, restSeconds = 60),
                TemplateExercise(exerciseId = 57, exerciseName = "Hanging Leg Raises", targetSets = 3, targetReps = 12, restSeconds = 60)
            ),
            estimatedDurationMinutes = 65,
            difficulty = Difficulty.INTERMEDIATE,
            isPreset = true
        ),
        WorkoutTemplate(
            name = "Full Body Progression",
            description = "Intermediate full-body structure for people who want strength and hypertrophy in one week",
            category = "Full Body",
            exercises = listOf(
                TemplateExercise(exerciseId = 41, exerciseName = "Barbell Squat", targetSets = 4, targetReps = 6, restSeconds = 150),
                TemplateExercise(exerciseId = 1, exerciseName = "Barbell Bench Press", targetSets = 4, targetReps = 6, restSeconds = 120),
                TemplateExercise(exerciseId = 13, exerciseName = "Barbell Row", targetSets = 3, targetReps = 8, restSeconds = 90),
                TemplateExercise(exerciseId = 42, exerciseName = "Romanian Deadlift", targetSets = 3, targetReps = 8, restSeconds = 120),
                TemplateExercise(exerciseId = 57, exerciseName = "Hanging Leg Raises", targetSets = 3, targetReps = 12, restSeconds = 60)
            ),
            estimatedDurationMinutes = 55,
            difficulty = Difficulty.INTERMEDIATE,
            isPreset = true
        ),

        // ====== PUSH PULL LEGS (Classic) ======
        WorkoutTemplate(
            name = "Push Day",
            description = "Chest, shoulders, and triceps hypertrophy",
            category = "Push/Pull/Legs",
            exercises = listOf(
                TemplateExercise(exerciseId = 1, exerciseName = "Barbell Bench Press", targetSets = 4, targetReps = 8, restSeconds = 90),
                TemplateExercise(exerciseId = 2, exerciseName = "Incline Dumbbell Press", targetSets = 3, targetReps = 10, restSeconds = 75),
                TemplateExercise(exerciseId = 21, exerciseName = "Overhead Press", targetSets = 3, targetReps = 8, restSeconds = 90),
                TemplateExercise(exerciseId = 22, exerciseName = "Lateral Raises", targetSets = 3, targetReps = 15, restSeconds = 60),
                TemplateExercise(exerciseId = 33, exerciseName = "Tricep Pushdowns", targetSets = 3, targetReps = 12, restSeconds = 60),
                TemplateExercise(exerciseId = 4, exerciseName = "Cable Flyes", targetSets = 3, targetReps = 12, restSeconds = 60)
            ),
            estimatedDurationMinutes = 60,
            difficulty = Difficulty.INTERMEDIATE,
            isPreset = true
        ),
        WorkoutTemplate(
            name = "Pull Day",
            description = "Back and biceps hypertrophy",
            category = "Push/Pull/Legs",
            exercises = listOf(
                TemplateExercise(exerciseId = 11, exerciseName = "Deadlift", targetSets = 3, targetReps = 5, restSeconds = 180),
                TemplateExercise(exerciseId = 12, exerciseName = "Pull-Ups", targetSets = 4, targetReps = 8, restSeconds = 90),
                TemplateExercise(exerciseId = 13, exerciseName = "Barbell Row", targetSets = 3, targetReps = 8, restSeconds = 90),
                TemplateExercise(exerciseId = 23, exerciseName = "Face Pulls", targetSets = 3, targetReps = 15, restSeconds = 60),
                TemplateExercise(exerciseId = 31, exerciseName = "Barbell Curl", targetSets = 3, targetReps = 10, restSeconds = 60),
                TemplateExercise(exerciseId = 32, exerciseName = "Hammer Curls", targetSets = 3, targetReps = 12, restSeconds = 60)
            ),
            estimatedDurationMinutes = 65,
            difficulty = Difficulty.INTERMEDIATE,
            isPreset = true
        ),
        WorkoutTemplate(
            name = "Leg Day",
            description = "Complete lower body hypertrophy",
            category = "Push/Pull/Legs",
            exercises = listOf(
                TemplateExercise(exerciseId = 41, exerciseName = "Barbell Squat", targetSets = 4, targetReps = 6, restSeconds = 180),
                TemplateExercise(exerciseId = 42, exerciseName = "Romanian Deadlift", targetSets = 3, targetReps = 10, restSeconds = 90),
                TemplateExercise(exerciseId = 43, exerciseName = "Leg Press", targetSets = 3, targetReps = 12, restSeconds = 90),
                TemplateExercise(exerciseId = 44, exerciseName = "Bulgarian Split Squat", targetSets = 3, targetReps = 10, restSeconds = 75),
                TemplateExercise(exerciseId = 46, exerciseName = "Hip Thrusts", targetSets = 3, targetReps = 12, restSeconds = 75),
                TemplateExercise(exerciseId = 45, exerciseName = "Calf Raises", targetSets = 4, targetReps = 15, restSeconds = 60)
            ),
            estimatedDurationMinutes = 70,
            difficulty = Difficulty.INTERMEDIATE,
            isPreset = true
        )
)
