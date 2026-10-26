# Keep Room entities
-keep class com.antirot.app.data.db.** { *; }
# Keep Hilt generated components
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
