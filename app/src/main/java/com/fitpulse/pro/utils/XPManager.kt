package com.fitpulse.pro.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * XP & Leveling System for FitPulse Pro
 * 
 * XP Sources:
 * - Complete a workout: +50 XP
 * - Log a meal: +10 XP
 * - Log water: +5 XP
 * - Hit daily calorie goal: +25 XP
 * - Maintain streak (per day): +15 XP
 * - Complete a challenge: +100 XP
 * - Unlock an achievement: +75 XP
 * - Log meditation: +20 XP
 * - Log body measurement: +15 XP
 * - Daily login: +10 XP
 */
class XPManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("fitpulse_xp", Context.MODE_PRIVATE)
    
    companion object {
        const val KEY_TOTAL_XP = "total_xp"
        const val KEY_LAST_LOGIN_DATE = "last_login_date"
        
        // XP rewards
        const val XP_WORKOUT_COMPLETE = 50
        const val XP_MEAL_LOG = 10
        const val XP_WATER_LOG = 5
        const val XP_CALORIE_GOAL = 25
        const val XP_STREAK_DAY = 15
        const val XP_CHALLENGE_COMPLETE = 100
        const val XP_ACHIEVEMENT_UNLOCK = 75
        const val XP_MEDITATION = 20
        const val XP_MEASUREMENT = 15
        const val XP_DAILY_LOGIN = 10
        const val XP_PERSONAL_RECORD = 30
        
        // Level thresholds - each level requires more XP
        fun xpForLevel(level: Int): Int {
            return when {
                level <= 1 -> 0
                level <= 5 -> (level - 1) * 100
                level <= 10 -> 400 + (level - 5) * 200
                level <= 20 -> 1400 + (level - 10) * 350
                level <= 30 -> 4900 + (level - 20) * 500
                level <= 50 -> 9900 + (level - 30) * 750
                else -> 24900 + (level - 50) * 1000
            }
        }
        
        fun levelForXP(xp: Int): Int {
            var level = 1
            while (xpForLevel(level + 1) <= xp) {
                level++
            }
            return level
        }
        
        fun rankTitle(level: Int): String {
            return when {
                level < 5 -> "Beginner"
                level < 10 -> "Rookie"
                level < 15 -> "Regular"
                level < 20 -> "Dedicated"
                level < 25 -> "Committed"
                level < 30 -> "Advanced"
                level < 35 -> "Warrior"
                level < 40 -> "Elite"
                level < 45 -> "Champion"
                level < 50 -> "Legend"
                else -> "Immortal"
            }
        }
        
        fun rankEmoji(level: Int): String {
            return when {
                level < 5 -> "🌱"
                level < 10 -> "🥉"
                level < 15 -> "🥈"
                level < 20 -> "🥇"
                level < 25 -> "💪"
                level < 30 -> "⚡"
                level < 35 -> "🔥"
                level < 40 -> "💎"
                level < 45 -> "👑"
                level < 50 -> "🏆"
                else -> "⭐"
            }
        }
    }
    
    fun getTotalXP(): Int = prefs.getInt(KEY_TOTAL_XP, 0)
    
    fun getLevel(): Int = levelForXP(getTotalXP())
    
    fun getRank(): String = rankTitle(getLevel())
    
    fun getRankEmoji(): String = rankEmoji(getLevel())
    
    fun getXPProgress(): Float {
        val currentLevel = getLevel()
        val currentLevelXP = xpForLevel(currentLevel)
        val nextLevelXP = xpForLevel(currentLevel + 1)
        val totalXP = getTotalXP()
        return if (nextLevelXP > currentLevelXP) {
            (totalXP - currentLevelXP).toFloat() / (nextLevelXP - currentLevelXP)
        } else 0f
    }
    
    fun getXPToNextLevel(): Int {
        val currentLevel = getLevel()
        val nextLevelXP = xpForLevel(currentLevel + 1)
        return nextLevelXP - getTotalXP()
    }
    
    fun addXP(amount: Int): XPGainResult {
        val previousXP = getTotalXP()
        val previousLevel = levelForXP(previousXP)
        val newXP = previousXP + amount
        val newLevel = levelForXP(newXP)
        
        prefs.edit().putInt(KEY_TOTAL_XP, newXP).apply()
        
        return XPGainResult(
            xpGained = amount,
            totalXP = newXP,
            previousLevel = previousLevel,
            newLevel = newLevel,
            didLevelUp = newLevel > previousLevel
        )
    }
    
    fun checkDailyLogin(): XPGainResult? {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            .format(java.util.Date())
        val lastLogin = prefs.getString(KEY_LAST_LOGIN_DATE, "") ?: ""
        
        return if (lastLogin != today) {
            prefs.edit().putString(KEY_LAST_LOGIN_DATE, today).apply()
            addXP(XP_DAILY_LOGIN)
        } else null
    }
}

data class XPGainResult(
    val xpGained: Int,
    val totalXP: Int,
    val previousLevel: Int,
    val newLevel: Int,
    val didLevelUp: Boolean
)
