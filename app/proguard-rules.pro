# FitPulse Pro ProGuard Rules

# Keep Room entities
-keep class com.fitpulse.pro.data.model.** { *; }

# Keep Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

# Keep Compose
-dontwarn androidx.compose.**
