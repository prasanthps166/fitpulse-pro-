package com.fitpulse.pro.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.fitpulse.pro.MainActivity
import com.fitpulse.pro.R
import com.fitpulse.pro.data.preferences.ReminderSettingsManager
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "fitpulse_reminders"
        const val CHANNEL_NAME = "Workout Reminders"
        const val NOTIFICATION_ID_WORKOUT = 1001
        const val NOTIFICATION_ID_WATER = 1002
        const val NOTIFICATION_ID_REST = 1003
        const val ACTION_WORKOUT_REMINDER = "com.fitpulse.pro.WORKOUT_REMINDER"
        const val ACTION_WATER_REMINDER = "com.fitpulse.pro.WATER_REMINDER"
        const val ACTION_REST_TIMER = "com.fitpulse.pro.REST_TIMER"

        private val workoutMotivations = listOf(
            "Time to crush it! Your workout is waiting.",
            "Rise and grind! Let's get those gains today.",
            "No excuses! Your future self will thank you.",
            "Every rep counts! Let's make today legendary.",
            "Champions train consistently! Your turn.",
            "The gym misses you! Let's clock in.",
            "Let's go, Athlete! Time to level up.",
            "Sweat now, shine later! Workout time!"
        )

        private val waterReminders = listOf(
            "Stay hydrated! Time for a glass of water.",
            "Your body needs fuel! Grab some water.",
            "Water break! Keep the hydration up.",
            "Don't forget to drink up! Stay sharp."
        )

        fun scheduleWorkoutReminder(context: Context, hour: Int, minute: Int) {
            createNotificationChannel(context)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                action = ACTION_WORKOUT_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_ID_WORKOUT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) add(Calendar.DAY_OF_MONTH, 1)
            }

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }

        fun cancelWorkoutReminder(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                action = ACTION_WORKOUT_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_ID_WORKOUT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }

        fun scheduleWaterReminders(context: Context) {
            createNotificationChannel(context)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                action = ACTION_WATER_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_ID_WATER,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR * 2,
                AlarmManager.INTERVAL_HOUR * 2,
                pendingIntent
            )
        }

        fun cancelWaterReminders(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                action = ACTION_WATER_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_ID_WATER,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Fitness reminders and workout notifications"
                    enableVibration(true)
                }
                val manager = context.getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(channel)
            }
        }

        fun triggerRestTimerVibration(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 200, 100, 200, 100, 400),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(0, 200, 100, 200, 100, 400),
                            -1
                        )
                    )
                }
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context)

        when (intent.action) {
            ACTION_WORKOUT_REMINDER -> showWorkoutReminder(context)
            ACTION_WATER_REMINDER -> showWaterReminder(context)
            Intent.ACTION_BOOT_COMPLETED -> rescheduleSavedReminders(context)
        }
    }

    private fun rescheduleSavedReminders(context: Context) {
        val reminderState = ReminderSettingsManager(context).loadState()
        if (reminderState.isEnabled) {
            scheduleWorkoutReminder(context, reminderState.hour, reminderState.minute)
        }
        if (reminderState.waterRemindersEnabled) {
            scheduleWaterReminders(context)
        }
    }

    private fun showWorkoutReminder(context: Context) {
        val motivation = workoutMotivations.random()
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_splash)
            .setContentTitle("FitPulse Pro")
            .setContentText(motivation)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(motivation))
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID_WORKOUT, notification)
    }

    private fun showWaterReminder(context: Context) {
        val reminder = waterReminders.random()
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_splash)
            .setContentTitle("Hydration Reminder")
            .setContentText(reminder)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID_WATER, notification)
    }
}
