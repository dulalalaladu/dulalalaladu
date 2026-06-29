# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# Keep Gson classes
-keep class com.google.gson.** { *; }
-keep class com.vehiclesearch.data.** { *; }

# Keep Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }

# Keep OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Keep Jsoup
-keep class org.jsoup.** { *; }
