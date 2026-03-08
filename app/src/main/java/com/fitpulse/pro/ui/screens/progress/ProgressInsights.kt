package com.fitpulse.pro.ui.screens.progress

import com.fitpulse.pro.data.model.BodyMeasurement
import com.fitpulse.pro.data.model.DailyStats
import com.fitpulse.pro.data.model.PersonalRecord
import com.fitpulse.pro.data.model.RecordType
import kotlin.math.abs

internal enum class ProgressInsightTone {
    POSITIVE,
    CAUTION,
    NEUTRAL
}

internal enum class ProgressTrajectory {
    IMPROVING,
    ON_TRACK,
    STALLED
}

internal data class ProgressStatusSummary(
    val title: String,
    val message: String,
    val trajectory: ProgressTrajectory
)

internal data class ProgressSummaryMetric(
    val label: String,
    val value: String,
    val message: String,
    val tone: ProgressInsightTone
)

internal data class ProgressInsight(
    val title: String,
    val message: String,
    val tone: ProgressInsightTone,
    val relatedArticleId: String? = null,
    val relatedArticleLabel: String? = null
)

internal data class MeasurementTrendContext(
    val title: String,
    val message: String,
    val tone: ProgressInsightTone
)

internal fun buildProgressInsights(
    weeklyStats: List<DailyStats>,
    totalWorkouts: Int,
    maxStreak: Int?,
    measurements: List<BodyMeasurement>,
    personalRecords: List<PersonalRecord>
): List<ProgressInsight> {
    val snapshot = weeklySnapshot(weeklyStats)
    val measurementContext = buildMeasurementTrendContext(measurements)

    val trainingInsight = when {
        snapshot.weeklyWorkoutCount >= 4 -> ProgressInsight(
            title = "Consistency is strong",
            message = "You trained ${snapshot.weeklyWorkoutCount} times in the last 7 days. That is enough consistency for real progress without needing fancy programming.",
            tone = ProgressInsightTone.POSITIVE,
            relatedArticleId = "progressive_overload_full_guide",
            relatedArticleLabel = "Read progression guide"
        )
        snapshot.weeklyWorkoutCount >= 2 -> ProgressInsight(
            title = "Base is forming",
            message = "You trained ${snapshot.weeklyWorkoutCount} times this week. Keep repeating this before adding more complexity or volume.",
            tone = ProgressInsightTone.NEUTRAL,
            relatedArticleId = "strength_starter_plan",
            relatedArticleLabel = "Read starter plan"
        )
        else -> ProgressInsight(
            title = "Show-up rate needs work",
            message = "You only logged ${snapshot.weeklyWorkoutCount} workout${if (snapshot.weeklyWorkoutCount == 1) "" else "s"} this week. The biggest unlock is getting back to 2 to 3 repeatable sessions.",
            tone = ProgressInsightTone.CAUTION,
            relatedArticleId = "fitness_fundamentals_full_guide",
            relatedArticleLabel = "Read fitness fundamentals"
        )
    }

    val workloadInsight = when {
        snapshot.weeklyActiveMinutes >= 150 || snapshot.weeklyVolume >= 4000f -> ProgressInsight(
            title = "Weekly workload is meaningful",
            message = "You stacked ${snapshot.weeklyActiveMinutes} active minutes this week. Keep recovery, protein, and sleep steady so this workload can actually adapt.",
            tone = ProgressInsightTone.POSITIVE,
            relatedArticleId = "sleep_recovery_deloads",
            relatedArticleLabel = "Read recovery guide"
        )
        snapshot.weeklyActiveMinutes >= 60 || snapshot.weeklyVolume >= 1500f -> ProgressInsight(
            title = "Workload is building",
            message = "You are moving in the right direction with ${snapshot.weeklyActiveMinutes} active minutes this week. Keep the plan boring and repeatable.",
            tone = ProgressInsightTone.NEUTRAL,
            relatedArticleId = "warm_up_and_injury_prevention",
            relatedArticleLabel = "Read warm-up guide"
        )
        else -> ProgressInsight(
            title = "Workload is still low",
            message = "Your weekly activity is still light. Focus on daily walks and a couple of solid training sessions before worrying about optimization.",
            tone = ProgressInsightTone.CAUTION,
            relatedArticleId = "fitness_dos_and_donts_full_guide",
            relatedArticleLabel = "Read do's and don'ts"
        )
    }

    val bodyTrendInsight = ProgressInsight(
        title = measurementContext.title,
        message = measurementContext.message,
        tone = measurementContext.tone,
        relatedArticleId = "protein_macros_muscle_fat_loss",
        relatedArticleLabel = "Read protein and macros"
    )

    val strengthInsight = buildStrengthProgressInsight(
        personalRecords = personalRecords,
        totalWorkouts = totalWorkouts
    )

    val streakInsight = buildStreakInsight(
        weeklyStats = weeklyStats,
        totalWorkouts = totalWorkouts,
        maxStreak = maxStreak
    )

    return buildList {
        add(trainingInsight)
        add(workloadInsight)
        add(bodyTrendInsight)
        strengthInsight?.let(::add)
        streakInsight?.let(::add)
    }
}

internal fun buildProgressStatusSummary(
    weeklyStats: List<DailyStats>,
    totalWorkouts: Int,
    maxStreak: Int?,
    measurements: List<BodyMeasurement>
): ProgressStatusSummary {
    val snapshot = weeklySnapshot(weeklyStats)
    val weightEntries = measurements.mapNotNull { it.weightKg }.size

    return when {
        snapshot.workoutDays >= 3 && (snapshot.activeDays >= 4 || (maxStreak ?: 0) >= 7) -> ProgressStatusSummary(
            title = "You are improving",
            message = "Your recent training frequency and adherence are high enough to drive progress. Keep repeating the same boring basics before changing the plan.",
            trajectory = ProgressTrajectory.IMPROVING
        )
        snapshot.workoutDays >= 2 || snapshot.trackedDays >= 4 || totalWorkouts >= 5 || weightEntries >= 2 -> ProgressStatusSummary(
            title = "You are on track",
            message = "The base is forming, but the biggest win is still more consistency. Keep logging and repeat the same routine for another week.",
            trajectory = ProgressTrajectory.ON_TRACK
        )
        else -> ProgressStatusSummary(
            title = "You are stalled",
            message = "You do not need a smarter plan yet. You need more repeatable training and tracking data so progress can actually become visible.",
            trajectory = ProgressTrajectory.STALLED
        )
    }
}

internal fun buildProgressSummaryMetrics(
    weeklyStats: List<DailyStats>
): List<ProgressSummaryMetric> {
    val snapshot = weeklySnapshot(weeklyStats)

    return listOf(
        ProgressSummaryMetric(
            label = "Training Days",
            value = "${snapshot.workoutDays}/7",
            message = when {
                snapshot.workoutDays >= 4 -> "Consistency is strong."
                snapshot.workoutDays >= 2 -> "A repeatable base is forming."
                else -> "Get back to 2-3 sessions."
            },
            tone = when {
                snapshot.workoutDays >= 4 -> ProgressInsightTone.POSITIVE
                snapshot.workoutDays >= 2 -> ProgressInsightTone.NEUTRAL
                else -> ProgressInsightTone.CAUTION
            }
        ),
        ProgressSummaryMetric(
            label = "Weekly Volume",
            value = formatVolume(snapshot.weeklyVolume),
            message = when {
                snapshot.weeklyVolume >= 4000f -> "Workload is meaningful."
                snapshot.weeklyVolume >= 1500f -> "Volume is building."
                else -> "More quality work is needed."
            },
            tone = when {
                snapshot.weeklyVolume >= 4000f -> ProgressInsightTone.POSITIVE
                snapshot.weeklyVolume >= 1500f -> ProgressInsightTone.NEUTRAL
                else -> ProgressInsightTone.CAUTION
            }
        ),
        ProgressSummaryMetric(
            label = "Tracked Days",
            value = "${snapshot.trackedDays}/7",
            message = when {
                snapshot.trackedDays >= 5 -> "Adherence is giving you usable data."
                snapshot.trackedDays >= 3 -> "Keep the logging loop tighter."
                else -> "Track more days so trends are real."
            },
            tone = when {
                snapshot.trackedDays >= 5 -> ProgressInsightTone.POSITIVE
                snapshot.trackedDays >= 3 -> ProgressInsightTone.NEUTRAL
                else -> ProgressInsightTone.CAUTION
            }
        )
    )
}

internal fun buildMeasurementTrendContext(
    measurements: List<BodyMeasurement>
): MeasurementTrendContext {
    val orderedMeasurements = measurements.sortedBy { it.date }
    val weightDelta = measurementDelta(orderedMeasurements) { it.weightKg }
    val waistDelta = measurementDelta(orderedMeasurements) { it.waistCm }
    val bodyFatDelta = measurementDelta(orderedMeasurements) { it.bodyFatPercent }

    return when {
        waistDelta != null && waistDelta <= -1f && weightDelta != null && weightDelta > 0.25f -> MeasurementTrendContext(
            title = "Measurements beat the scale",
            message = "Body weight is up ${formatSignedDelta(weightDelta, "kg")} but waist is down ${formatAbsDelta(waistDelta, "cm")}. That often means the scale is hiding better body composition.",
            tone = ProgressInsightTone.POSITIVE
        )
        waistDelta != null && waistDelta <= -1f && (weightDelta == null || abs(weightDelta) <= 0.75f) -> MeasurementTrendContext(
            title = "Waist is moving down",
            message = "Waist is down ${formatAbsDelta(waistDelta, "cm")} while body weight is roughly stable. That is usually a better sign than scale weight alone.",
            tone = ProgressInsightTone.POSITIVE
        )
        bodyFatDelta != null && bodyFatDelta <= -0.5f -> MeasurementTrendContext(
            title = "Body-fat trend is improving",
            message = "Your logged body-fat readings are down ${formatAbsDelta(bodyFatDelta, "%")}. Keep using the same measurement conditions so the trend stays trustworthy.",
            tone = ProgressInsightTone.POSITIVE
        )
        weightDelta != null && weightDelta < -0.75f && (waistDelta == null || waistDelta <= -0.5f) -> MeasurementTrendContext(
            title = "Fat-loss trend looks supported",
            message = "Scale weight is down ${formatAbsDelta(weightDelta, "kg")} and your measurements support it. Hold protein and training quality steady while the cut is working.",
            tone = ProgressInsightTone.POSITIVE
        )
        weightDelta != null && weightDelta > 0.75f && waistDelta != null && waistDelta > 0.75f -> MeasurementTrendContext(
            title = "Scale and waist are both climbing",
            message = "Body weight is up ${formatSignedDelta(weightDelta, "kg")} and waist is up ${formatSignedDelta(waistDelta, "cm")}. Check whether that matches the goal before adding more calories.",
            tone = ProgressInsightTone.CAUTION
        )
        weightDelta != null && abs(weightDelta) < 0.3f -> MeasurementTrendContext(
            title = "Body weight is stable",
            message = "Your scale trend is essentially flat right now. Use waist, body-fat, photos, and gym performance to judge whether the plan is actually working.",
            tone = ProgressInsightTone.NEUTRAL
        )
        weightDelta != null -> MeasurementTrendContext(
            title = "Scale trend needs context",
            message = "Body weight changed ${formatSignedDelta(weightDelta, "kg")} across your logged entries. Pair that with waist, body-fat, and performance so you do not overreact to scale noise.",
            tone = ProgressInsightTone.NEUTRAL
        )
        waistDelta != null || bodyFatDelta != null -> MeasurementTrendContext(
            title = "Measurements are starting to tell a story",
            message = "You have enough circumference or body-fat data to start spotting a trend. Keep measurement conditions consistent for cleaner comparisons.",
            tone = ProgressInsightTone.NEUTRAL
        )
        else -> MeasurementTrendContext(
            title = "Measurement trend needs more data",
            message = "Log at least two check-ins over time so weight and body measurements can be interpreted instead of guessed.",
            tone = ProgressInsightTone.CAUTION
        )
    }
}

private data class WeeklyProgressSnapshot(
    val weeklyWorkoutCount: Int,
    val weeklyActiveMinutes: Int,
    val weeklyVolume: Float,
    val workoutDays: Int,
    val activeDays: Int,
    val trackedDays: Int
)

private fun buildStrengthProgressInsight(
    personalRecords: List<PersonalRecord>,
    totalWorkouts: Int
): ProgressInsight? {
    if (personalRecords.isEmpty()) {
        return if (totalWorkouts >= 6) {
            ProgressInsight(
                title = "Strength signal needs more repeats",
                message = "You have workouts logged, but not enough confirmed PR movement yet. Keep the same core lifts in rotation so real strength change becomes obvious.",
                tone = ProgressInsightTone.NEUTRAL,
                relatedArticleId = "progressive_overload_full_guide",
                relatedArticleLabel = "Read progression guide"
            )
        } else {
            null
        }
    }

    val recentRecords = personalRecords.sortedByDescending { it.date }.take(3)
    val latestRecord = recentRecords.first()
    val recordMessage = latestRecord.previousValue?.let { previousValue ->
        "${latestRecord.exerciseName} moved from ${formatRecordValue(latestRecord.recordType, previousValue)} to ${formatRecordValue(latestRecord.recordType, latestRecord.value)}."
    } ?: "${latestRecord.exerciseName} hit ${formatRecordValue(latestRecord.recordType, latestRecord.value)}."

    return ProgressInsight(
        title = if (recentRecords.size >= 2) "Strength is moving up" else "A new strength marker was logged",
        message = if (recentRecords.size >= 2) {
            "You logged ${recentRecords.size} recent PRs. Latest: $recordMessage Keep the lift selection stable so these jumps stay measurable."
        } else {
            "$recordMessage Small, repeatable increases beat random exercise hopping."
        },
        tone = ProgressInsightTone.POSITIVE,
        relatedArticleId = "progressive_overload_full_guide",
        relatedArticleLabel = "Read progression guide"
    )
}

private fun buildStreakInsight(
    weeklyStats: List<DailyStats>,
    totalWorkouts: Int,
    maxStreak: Int?
): ProgressInsight? {
    if (totalWorkouts <= 0) return null

    val currentStreak = weeklyStats.lastOrNull()?.streakDays ?: 0
    val hadRecentBreak = weeklyStats.dropLast(1).any { it.streakDays == 0 }

    return when {
        currentStreak >= 3 && hadRecentBreak -> ProgressInsight(
            title = "Your streak is back",
            message = "You rebuilt to $currentStreak day${if (currentStreak == 1) "" else "s"} after a recent break. Recovery after a miss matters more than trying to be perfect.",
            tone = ProgressInsightTone.POSITIVE,
            relatedArticleId = "fitness_dos_and_donts_full_guide",
            relatedArticleLabel = "Read do's and don'ts"
        )
        currentStreak >= 7 && currentStreak >= (maxStreak ?: 0) -> ProgressInsight(
            title = "Momentum is at a new high",
            message = "Your current streak is $currentStreak days, which means your adherence is doing the heavy lifting right now. Protect the routine before adding complexity.",
            tone = ProgressInsightTone.POSITIVE,
            relatedArticleId = "fitness_fundamentals_full_guide",
            relatedArticleLabel = "Read fundamentals"
        )
        (maxStreak ?: 0) >= 14 -> ProgressInsight(
            title = "Your streak shows momentum",
            message = "A best streak of ${maxStreak ?: 0} days means your biggest advantage is adherence. Protect that before chasing advanced tactics.",
            tone = ProgressInsightTone.POSITIVE,
            relatedArticleId = "fitness_dos_and_donts_full_guide",
            relatedArticleLabel = "Read do's and don'ts"
        )
        (maxStreak ?: 0) >= 5 -> ProgressInsight(
            title = "Momentum is building",
            message = "Your best streak is ${maxStreak ?: 0} days. Keep stacking simple wins instead of trying to overhaul everything at once.",
            tone = ProgressInsightTone.NEUTRAL,
            relatedArticleId = "fitness_fundamentals_full_guide",
            relatedArticleLabel = "Read fundamentals"
        )
        else -> ProgressInsight(
            title = "Habits need a tighter loop",
            message = "Your current streak history is still short. Use easier daily targets so consistency stops breaking after a few days.",
            tone = ProgressInsightTone.CAUTION,
            relatedArticleId = "fitness_dos_and_donts_full_guide",
            relatedArticleLabel = "Read do's and don'ts"
        )
    }
}

private fun weeklySnapshot(weeklyStats: List<DailyStats>): WeeklyProgressSnapshot {
    val lastSeven = weeklyStats.takeLast(7)
    return WeeklyProgressSnapshot(
        weeklyWorkoutCount = lastSeven.sumOf { it.workoutCount },
        weeklyActiveMinutes = lastSeven.sumOf { it.activeMinutes },
        weeklyVolume = lastSeven.sumOf { it.totalVolume.toDouble() }.toFloat(),
        workoutDays = lastSeven.count { it.workoutCount > 0 },
        activeDays = lastSeven.count { it.activeMinutes >= 20 || it.steps >= 6000 },
        trackedDays = lastSeven.count {
            it.workoutCount > 0 ||
                it.activeMinutes > 0 ||
                it.steps > 0 ||
                it.caloriesConsumed > 0 ||
                it.waterMl > 0
        }
    )
}

private fun measurementDelta(
    measurements: List<BodyMeasurement>,
    selector: (BodyMeasurement) -> Float?
): Float? {
    val values = measurements.mapNotNull { measurement ->
        selector(measurement)?.let { measurement.date to it }
    }.sortedBy { it.first }

    if (values.size < 2) return null
    return values.last().second - values.first().second
}

private fun formatVolume(volume: Float): String {
    val rounded = volume.toInt()
    return when {
        rounded >= 1_000 -> String.format("%.1fK kg", rounded / 1_000f)
        else -> "$rounded kg"
    }
}

private fun formatSignedDelta(value: Float, unit: String): String {
    val prefix = if (value > 0f) "+" else ""
    return "$prefix${String.format("%.1f", value)} $unit"
}

private fun formatAbsDelta(value: Float, unit: String): String {
    return "${String.format("%.1f", abs(value))} $unit"
}

private fun formatRecordValue(recordType: RecordType, value: Float): String {
    return when (recordType) {
        RecordType.MAX_WEIGHT -> "${String.format("%.1f", value)} kg"
        RecordType.MAX_REPS -> "${value.toInt()} reps"
        RecordType.MAX_DURATION -> "${value.toInt()} min"
        RecordType.MAX_DISTANCE -> "${String.format("%.1f", value)} km"
        RecordType.MAX_VOLUME -> "${String.format("%.1f", value)} kg"
    }
}
