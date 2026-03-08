package com.fitpulse.pro.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Generates a premium-looking workout summary card as a Bitmap
 * and shares it via Android share sheet.
 */
object WorkoutShareHelper {
    
    fun shareWorkoutCard(
        context: Context,
        workoutName: String,
        duration: String,
        exercises: Int,
        totalVolume: String,
        calories: Int,
        rating: Int,
        userName: String,
        level: Int,
        rank: String
    ) {
        val bitmap = generateWorkoutCard(
            workoutName, duration, exercises, totalVolume, calories, rating, userName, level, rank
        )
        
        // Save to cache
        val cachePath = File(context.cacheDir, "shared_images")
        cachePath.mkdirs()
        val file = File(cachePath, "fitpulse_workout.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()
        
        val contentUri = FileProvider.getUriForFile(
            context, "${context.packageName}.fileprovider", file
        )
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_TEXT, "Just crushed my workout! 💪🔥 #FitPulsePro")
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Share Workout"))
    }
    
    fun generateWorkoutCard(
        workoutName: String,
        duration: String,
        exercises: Int,
        totalVolume: String,
        calories: Int,
        rating: Int,
        userName: String,
        level: Int,
        rank: String
    ): Bitmap {
        val w = 1080
        val h = 1350
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Background gradient
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, w.toFloat(), h.toFloat(),
                intArrayOf(
                    Color.parseColor("#0A0A1A"),
                    Color.parseColor("#0F0B2E"),
                    Color.parseColor("#1A0A2E"),
                    Color.parseColor("#0A0A1A")
                ),
                floatArrayOf(0f, 0.3f, 0.7f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)
        
        // Accent glow circles
        val glowPaint = Paint().apply {
            shader = RadialGradient(
                200f, 300f, 400f,
                Color.parseColor("#6C63FF"), Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
            alpha = 40
        }
        canvas.drawCircle(200f, 300f, 400f, glowPaint)
        
        val glow2 = Paint().apply {
            shader = RadialGradient(
                900f, 1000f, 350f,
                Color.parseColor("#FF6B6B"), Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
            alpha = 30
        }
        canvas.drawCircle(900f, 1000f, 350f, glow2)
        
        // Card background
        val cardPaint = Paint().apply {
            color = Color.parseColor("#1A1A2E")
            alpha = 180
            isAntiAlias = true
        }
        val cardRect = RectF(60f, 180f, w - 60f, h - 180f)
        canvas.drawRoundRect(cardRect, 40f, 40f, cardPaint)
        
        // Card border
        val borderPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f
            shader = LinearGradient(
                60f, 180f, w - 60f, h - 180f,
                Color.parseColor("#6C63FF"), Color.parseColor("#FF6B6B"),
                Shader.TileMode.CLAMP
            )
            alpha = 100
            isAntiAlias = true
        }
        canvas.drawRoundRect(cardRect, 40f, 40f, borderPaint)
        
        // FITPULSE PRO branding
        val brandPaint = Paint().apply {
            color = Color.parseColor("#6C63FF")
            textSize = 36f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            letterSpacing = 0.15f
        }
        canvas.drawText("FITPULSE PRO", 110f, 260f, brandPaint)
        
        // User info
        val userPaint = Paint().apply {
            color = Color.parseColor("#8888AA")
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }
        canvas.drawText("$userName • Lvl $level $rank", 110f, 310f, userPaint)
        
        // Divider line
        val divPaint = Paint().apply {
            shader = LinearGradient(
                110f, 340f, w - 110f, 340f,
                Color.parseColor("#6C63FF"), Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
            strokeWidth = 2f
        }
        canvas.drawLine(110f, 340f, w - 110f, 340f, divPaint)
        
        // Workout Title
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 56f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(workoutName, 110f, 430f, titlePaint)
        
        // "WORKOUT COMPLETE" subtitle
        val subtitlePaint = Paint().apply {
            color = Color.parseColor("#4CAF50")
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            letterSpacing = 0.2f
        }
        canvas.drawText("✅ WORKOUT COMPLETE", 110f, 480f, subtitlePaint)
        
        // Stats section
        val statY = 560f
        val statSpacing = 180f

        drawStatBox(canvas, 110f, statY, "DURATION", duration, "#00E5FF")
        drawStatBox(canvas, 110f + statSpacing * 1.6f, statY, "EXERCISES", "$exercises", "#6C63FF")
        drawStatBox(canvas, 110f + statSpacing * 3.2f, statY, "VOLUME", totalVolume, "#FF6B6B")
        
        drawStatBox(canvas, 110f, statY + 200f, "CALORIES", "$calories kcal", "#FF9800")
        drawStatBox(canvas, 110f + statSpacing * 1.6f, statY + 200f, "RATING", "⭐".repeat(rating.coerceIn(1, 5)), "#FFD600")
        
        // Bottom motivational quote
        val quotePaint = Paint().apply {
            color = Color.parseColor("#AAAACC")
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("\"Consistency is the key to greatness.\"", w / 2f, h - 320f, quotePaint)
        
        // Bottom bar
        val bottomPaint = Paint().apply {
            color = Color.parseColor("#666688")
            textSize = 24f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Track your fitness journey with FitPulse Pro", w / 2f, h - 240f, bottomPaint)
        
        return bitmap
    }
    
    private fun drawStatBox(
        canvas: Canvas, x: Float, y: Float,
        label: String, value: String, color: String
    ) {
        // Background box
        val boxPaint = Paint().apply {
            this.color = Color.parseColor(color)
            alpha = 25
            isAntiAlias = true
        }
        val boxRect = RectF(x, y, x + 250f, y + 150f)
        canvas.drawRoundRect(boxRect, 20f, 20f, boxPaint)
        
        // Border
        val borderPaint = Paint().apply {
            style = Paint.Style.STROKE
            this.color = Color.parseColor(color)
            alpha = 50
            strokeWidth = 1.5f
            isAntiAlias = true
        }
        canvas.drawRoundRect(boxRect, 20f, 20f, borderPaint)
        
        // Label
        val labelPaint = Paint().apply {
            this.color = Color.parseColor(color)
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            letterSpacing = 0.1f
        }
        canvas.drawText(label, x + 20f, y + 38f, labelPaint)
        
        // Value
        val valuePaint = Paint().apply {
            this.color = Color.WHITE
            textSize = if (value.length > 8) 30f else 38f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(value, x + 20f, y + 100f, valuePaint)
    }
}
