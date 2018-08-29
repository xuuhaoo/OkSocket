# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/xuhao/Library/Android/sdk/tools/proguard/proguard-android.txt
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

-keep class com.xuhao.android.libsocket.impl.abilities.** { *; }
-keep class com.xuhao.android.libsocket.impl.exceptions.** { *; }
-keep class com.xuhao.android.libsocket.impl.client.EnvironmentalManager { *; }
-keep class com.xuhao.android.libsocket.impl.client.ConnectionManagerImpl { *; }
-keep class com.xuhao.android.libsocket.impl.UnBlockConnectionManager { *; }
-keep class com.xuhao.android.libsocket.impl.client.action.SocketActionHandler { *; }
-keep class com.xuhao.android.libsocket.impl.client.PulseManager { *; }
-keep class com.xuhao.android.libsocket.impl.client.ManagerHolder { *; }
-keep class com.xuhao.android.libsocket.interfaces.** { *; }
-keep class com.xuhao.android.libsocket.sdk.** { *; }

# 枚举类不能被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class com.xuhao.android.libsocket.sdk.client.OkSocketOptions$* {
    *;
}
-keep class com.xuhao.android.libsocket.sdk.client.OkSocketSSLConfig$* {
    *;
}
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod