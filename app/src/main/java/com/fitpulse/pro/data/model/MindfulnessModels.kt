package com.fitpulse.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meditation_sessions")
data class MeditationSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: MeditationType,
    val durationMinutes: Int,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)

enum class MeditationType { GUIDED, BREATHING, BODY_SCAN, SLEEP, FOCUS, CUSTOM }
