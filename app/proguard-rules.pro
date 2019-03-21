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


###################################
#          KEEP IT ALL!           #
# (I HAVE NO CLUE WHAT I'M DOING) #
###################################

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

#################
# KOTLIN
#################

-keep class kotlin.** { *; }
-keep class kotlin.jvm.** { *; }
-keep class kotlin.internal.** { *; }
-keep class kotlin.reflect.** { *; }
-dontnote kotlin.internal.**
-dontwarn kotlin.internal.**
-dontwarn kotlin.reflect.jvm.internal.**


#################
# COROUTINES
#################

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

#################
# ANDROID
#################

-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

#-dontwarn androidx.databinding.**
#-keep class androidx.databinding.** { *; }
#-keep class android.databinding.DataBinderMapper
#-keep class androidx.databinding.DataBinderMapper
#-dontwarn android.databinding.**
#-keep class android.databinding.** { *; }
-dontwarn androidx.databinding.**
#-keep class androidx.databinding.** { *; }

#################
# MATERIAL
#################

-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

#################
# SWIPE
#################

-keep class com.daimajia.swipe.** { *; }

#################
# TRANSITIONS
#################

-keep class com.transitionseverywhere.** { *; }
-dontwarn com.transitionseverywhere.**
-dontnote com.transitionseverywhere.**

#################
# ROOM
#################

-dontwarn android.arch.util.paging.CountedDataSource
-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource

#################
# GSON
#################

# Gson specific classes
-dontwarn sun.misc.**
-dontwarn com.google.gson.**
-dontnote com.google.gson.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class eu.slicky.pomesljajcek.data.pref.model.SectionOrder
-keep class eu.slicky.pomesljajcek.data.pref.model.EntityOrder
-keep class eu.slicky.pomesljajcek.data.pref.model.ChallengeCheckedIds

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

#################
# PICASSO
#################

-keep class okhttp3.Request { *; }
-keep class okhttp3.Response { *; }
-keep class okhttp3.Connection { *; }
-keep class okhttp3.MediaType { *; }
-dontwarn okio.**
-dontnote okio.**
-dontwarn okhttp3.**
-dontnote okhttp3.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

#################
# TIMBER
#################

-dontwarn org.jetbrains.annotations.NonNls

#################
# STETHO
#################

-keep class com.facebook.stetho.** { *; }
-dontwarn com.facebook.stetho.**
-dontnote com.facebook.stetho.**

#################
# MODELS
#################

-keep class eu.slicky.pomesljajcek.model.** { *; }
-keep class eu.slicky.pomesljajcek.ui.** { *; }
-keep class eu.slicky.pomesljajcek.ui.activity.** { *; }
-keep class eu.slicky.pomesljajcek.ui.view.** { *; }
-keep class eu.slicky.pomesljajcek.data.db.** { *; }
-keep class eu.slicky.pomesljajcek.data.pref.** { *; }
-keep class eu.slicky.pomesljajcek.databinding.** { *; }
-dontwarn eu.slicky.pomesljajcek.data.**
-dontnote eu.slicky.pomesljajcek.data.**
