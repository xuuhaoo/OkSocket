# OkSocket Document
An blocking socket client for Java application or Andorid.

| [![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0) | [![Download](https://api.bintray.com/packages/xuuhaoo/maven/OkSocket/images/download.svg)](https://bintray.com/xuuhaoo/maven/OkSocket/_latestVersion) | [![Download](https://api.bintray.com/packages/xuuhaoo/maven/ServerImpl/images/download.svg)](https://bintray.com/xuuhaoo/maven/ServerImpl/_latestVersion) |
| :------: | :------: | :------: |
| `Open source licenses` | `Basic Socket Library` | `Socket Server Plugin Library` |


Read this in other languages: [简体中文](https://github.com/xuuhaoo/OkSocket/blob/master/README-CN.md)

### OkSocket Introduce
<font size=2>
Oksocket Library is socket client solution base on java blocking socket. You can use it to develop line chat  or data transmission etc.
</font>


### Gradle Configuration
* <font size=2>Add the following configuration to the build.gradle file under the project project.</font>
    
```groovy
allprojects {
    repositories {
        jcenter()
    }
}
```
* <font size=2>Make sure you have already done with put JCenter into repositories blocking in project Gradle files than you need add the following configuration to the module's build.gradle file.</font>

```groovy
dependencies {
	//Basic Socket client functionality
	compile 'com.tonystark.android:socket:4.0.1'
	//If you want to use server functionality, you need to compile the following libraries
	compile 'com.tonystark.android:socket-server:4.0.1'
}
```

### Maven Configuration
```xml
<!--Basic Socket client functionality-->
<dependency>
  <groupId>com.tonystark.android</groupId>
  <artifactId>socket</artifactId>
  <version>4.0.1</version>
  <type>pom</type>
</dependency>

<!--If you want to use server functionality, you need to compile the following libraries-->
<dependency>
  <groupId>com.tonystark.android</groupId>
  <artifactId>socket-server</artifactId>
  <version>4.0.1</version>
  <type>pom</type>
</dependency>
```


### If it is an Android app, you need to declare permissions
* <font size=2>Put the Permissions into AndroidManifest.xml file：</font>

```java
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```


### Proguard Configuration
* <font size=2>Put this code into your Proguard file：</font>

```
-dontwarn com.xuhao.didi.socket.client.**
-dontwarn com.xuhao.didi.socket.common.**
-dontwarn com.xuhao.didi.socket.server.**
-dontwarn com.xuhao.didi.core.**

-keep class com.xuhao.didi.socket.client.** { *; }
-keep class com.xuhao.didi.socket.common.** { *; }
-keep class com.xuhao.didi.socket.server.** { *; }
-keep class com.xuhao.didi.core.** { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class com.xuhao.didi.socket.client.sdk.client.OkSocketOptions$* {
    *;
}
-keep class com.xuhao.didi.socket.server.impl.OkServerOptions$* {
    *;
}
```

### Echo Server
<font size=2>The Echo Server is a demonstration server specially designed for beginners to debug the OkSocket library. Beginners can install the app in the project to the mobile phone, click the `Connect` button, or use the Java program to connect to the Echo server. The Echo server should be used as a familiar communication method and parsing method, can not be used as a commercial server.</font>

* IP:`104.238.184.237`
* Port:`8080`

### The instructions



```
   Copyright [2017] [徐昊]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```



















