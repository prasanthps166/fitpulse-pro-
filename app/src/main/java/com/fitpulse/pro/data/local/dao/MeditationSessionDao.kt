package com.fitpulse.pro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitpulse.pro.data.model.MeditationSession
import kotlinx.coroutines.flow.Flow

@Dao
interface MeditationSessionDao {
    @Query("SELECT * FROM meditation_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<MeditationSession>>

    @Query("SELECT * FROM meditation_sessions ORDER BY date DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<MeditationSession>>

    @Query("SELECT SUM(durationMinutes) FROM meditation_sessions")
    fun getTotalMinutes(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: MeditationSession): Long
}
