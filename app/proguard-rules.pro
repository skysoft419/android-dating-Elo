# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Android\adt-bundle-windows-x86_64-20140702\sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public class * extends com.bumptech.glide.AppGlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#   **[] $VALUES;
#    public *;
#}


-ignorewarnings

#-keep class * {
#    public private *;
#}

#-keep public class



# Butterknife
#-dontwarn butterknife.internal.**
#-keep class butterknife.** { *; }
#-keep class **$$ViewInjector { *; }
#
#-keepclasseswithmembernames class * {
#    @butterknife.InjectView <fields>;
#}
#
#-keepclasseswithmembernames class * {
#    @butterknife.OnClick <methods>;
#    @butterknife.OnEditorAction <methods>;
#    @butterknife.OnItemClick <methods>;
#    @butterknife.OnItemLongClick <methods>;
#    @butterknife.OnLongClick <methods>;
#}


# Glide

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}


-keep public class com.brains.ello.datamodels.** {*;}
-keep class com.brains.ello.views.main.LoginActivity
-keep class ello.utils.** {*;}
-keepclasseswithmembernames class ello.utils.** {*;}

-keep class com.brains.ello.utils.** {*;}
-keepclasseswithmembernames class com.brains.ello.utils.** {*;}


# Retrofit 2.X
## https://square.github.io/retrofit/ ##

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}