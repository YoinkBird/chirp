# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/Android Studio.app/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

## Chirp SDK Interface
-keep !abstract class io.chirp.sdk** { *; }

## Chirp SDK Engine
-keep class io.chirp.chirpengine** { *; }

## For Dagger
-keep !abstract class io.chirp.injection.modules** { *; }

-keepattributes *Annotation*

-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
    <init>();
}

-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter

-keep class **$$ModuleAdapter
-keep class **$$InjectAdapter
-keep class **$$StaticInjection

-keep class dagger.* { *; }

-keep class javax.inject.* { *; }
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter
-keep class * extends dagger.internal.StaticInjection

-keepattributes Signature

-dontwarn dagger.internal.codegen.**
-dontwarn com.squareup.javawriter.**
-dontwarn dagger.internal.codegen.**

## For Retrofit
-keepattributes Signature
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-dontwarn rx.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

## For Okio
-dontwarn okio.**

## For Joda Time 2.3
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

## For OrmLite
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }

## For Jackson
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.**

## Other
-dontwarn com.google.common.**