# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# --- Retrofit & OkHttp ---
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

# --- Gson ---
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# --- Room ---
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-dontwarn androidx.room.**

# --- Lottie ---
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# --- WorkManager ---
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# --- DataStore ---
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# --- Coroutines ---
-dontwarn kotlinx.coroutines.**

# --- General AndroidX ---
-keep class androidx.** { *; }
-dontwarn androidx.**

# --- Compose ---
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# --- MainActivity entry point ---
-keep class com.example.livekick.MainActivity { *; }

# --- Keep Application class ---
-keep class com.example.livekick.LiveKickApplication { *; }

# --- Keep all ViewModels ---
-keep class *ViewModel { *; }

# --- Keep all Activities and Fragments ---
-keep class *Activity { *; }
-keep class *Fragment { *; }

# --- Keep all data classes (optional, для сериализации) ---
-keep class com.example.livekick.domain.model.** { *; }