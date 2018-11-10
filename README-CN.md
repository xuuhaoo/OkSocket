# OkSocket 简介
一个Java或者Android的轻量级Socket通讯框架

| [![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0) | [![Download](https://api.bintray.com/packages/xuuhaoo/maven/OkSocket/images/download.svg)](https://bintray.com/xuuhaoo/maven/OkSocket/_latestVersion) | [![Download](https://api.bintray.com/packages/xuuhaoo/maven/ServerImpl/images/download.svg)](https://bintray.com/xuuhaoo/maven/ServerImpl/_latestVersion) |
| :------: | :------: | :------: |
| `开源协议` | `基础Socket库` | `Socket服务器插件库` |


其他语言支持: [English](https://github.com/xuuhaoo/OkSocket/blob/master/README.md)

### OkSocket简介
<font size=2>OkSocket是一款基于阻塞式传统Socket的一款Socket客户端整体解决方案.您可以使用它进行简单的基于Tcp协议的Socket通讯,当然,也可以进行大数据量复杂的Socket通讯,支持单工,双工通讯.</font>

### Gradle依赖配置
* <font size=2>在项目工程下的build.gradle文件中添加如下配置</font>
    
```groovy
allprojects {
    repositories {
        jcenter()
    }
}
```
* <font size=2>在Module的build.gradle文件中添加如下配置</font>

```groovy
dependencies {
	//基本Socket客户端功能
	compile 'com.tonystark.android:socket:4.0.1'
	//如果您要使用server功能,需要依赖以下库.如果您只需要基本Socket客户端功能,不需要依赖以下server库
	compile 'com.tonystark.android:socket-server:4.0.1'
}
```
### Maven依赖配置

```xml
<!--基本Socket客户端功能-->
<dependency>
  <groupId>com.tonystark.android</groupId>
  <artifactId>socket</artifactId>
  <version>4.0.1</version>
  <type>pom</type>
</dependency>

<!--如果您要使用server功能,需要依赖以下库.如果您只需要基本Socket客户端功能,不需要依赖以下server库-->
<dependency>
  <groupId>com.tonystark.android</groupId>
  <artifactId>socket-server</artifactId>
  <version>4.0.1</version>
  <type>pom</type>
</dependency>
```
### 如果是Android应用,需要声明权限
* <font size=2>在AndroidManifest.xml中添加权限：</font>

```java
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```


### ProGuard 配置
* <font size=2>请避免混淆OkSocket，在Proguard混淆文件中增加以下配置：</font>

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

### 回音服务器
<font size=2> 回音服务器是专门为初学者调试 OkSocket 库部属的一台演示服务器,初学者可以将项目中的 app 安装到手机上,点击 `Connect` 按钮,也可以使用Java程序连接至回音服务器.回音服务器仅为熟悉通讯方式和解析方式使用,不能作为商用服务器. </font>

* 公网IP:`104.238.184.237`
* 公网Port:`8080`

### 使用指南


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


































