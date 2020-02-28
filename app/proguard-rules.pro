# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\tools\adt-bundle-windows-x86_64-20131030\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
#class:
-dontshrink
-dontoptimize
-ignorewarnings
-dontusemixedcaseclassnames
-keepattributes *Annotation*
-keep class * implements android.os.Parcelable {
  public static android.os.Parcelable$Creator *;
  public static final android.os.Parcelable$Creator *;
  public final static android.os.Parcelable$Creator *;
  
}
#-keep class * extends android.os.Parcelable$Creator { *;}
-keepclassmembers class * { @android.webkit.JavascriptInterface *;}
-keepclassmembers class * { @moe.app.WorkerThread$Worker *;}
-keepclasseswithmembers class android.**
-keep class android.**

