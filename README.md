# OkSocket Document
An blocking socket client for Java application or Andorid.

| [![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0) | [![Download](https://api.bintray.com/packages/xuuhaoo/maven/OkSocket/images/download.svg)](https://bintray.com/xuuhaoo/maven/OkSocket/_latestVersion) | [![Download](https://api.bintray.com/packages/xuuhaoo/maven/ServerImpl/images/download.svg)](https://bintray.com/xuuhaoo/maven/ServerImpl/_latestVersion) |
| :------: | :------: | :------: |
| `Open source licenses` | `Basic Socket Library` | `Socket Server Plugin Library` |


### 中文在线文档
* 为了照顾中国同学的使用,提供了中文的说明文档:
https://www.jianshu.com/p/8ee3ee766265

### OkEchoServer Open Source
* Because the echo server is not being renewed and is not working properly, we decided to open it up, hoping let more people who need point-to-point, mobile-to-mobile communication to get more help.

[OkEchoServer Source](https://github.com/xuuhaoo/OkEchoServer)

### OkSocket Introduce
<font size=2>
OkSocket is a Java library project designed to solve lightweight Socket communication, in order to enable developers to focus more on business logic, rather than TCP communication principles and some protocols. Make Socket communication more beautiful, suitable for large, medium and small Project, the rapid development of stable, maintainable, reliable Socket connection.
</font>

### Feature
- SocketClient Tcp IPV4 Connect
- Socket Standard Protocol 
- SocketClient Reconnect
- SocketClient Heartbeat 
- Socket Sticky Unpacking
- Socket SSL Socket Support
- Client Callback In Thread / Main Thread
- Socket Client Redirect
- SocketServer Support


### Instructions

* OkSocket instruction manual [WIKI](https://github.com/xuuhaoo/OkSocket/wiki/What-Is-OkSocket-Is)

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
	api 'com.tonystark.android:socket:latest.release'
	//If you want to use server functionality, you need to compile the following libraries
	api 'com.tonystark.android:socket-server:latest.release'
}
```

### Policy

```
   Copyright [2018] [徐昊]

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



















