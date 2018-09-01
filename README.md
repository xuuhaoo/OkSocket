# OkSocket Document
An blocking socket client for Android applications.

| [![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0) | [![Download](https://api.bintray.com/packages/xuuhaoo/maven/OkSocket/images/download.svg)](https://bintray.com/xuuhaoo/maven/OkSocket/_latestVersion) | [![Download](https://api.bintray.com/packages/xuuhaoo/maven/ServerImpl/images/download.svg)](https://bintray.com/xuuhaoo/maven/ServerImpl/_latestVersion) |
| :------: | :------: | :------: |
| `Open source licenses` | `Basic Socket Library` | `Socket Server Plugin Library` |


Read this in other languages: [简体中文](https://github.com/xuuhaoo/OkSocket/blob/master/README-CN.md)

### <font id="1">OkSocket Introduce</font>
<font size=2>
Android Oksocket Library is socket client solution base on java blocking socket.You can use it to develop line chat 
rooms or data transmission etc.
</font>


### <font id="2">Maven Configuration</font>
##### <font id="2.1">Automatic Import(Recommend)</font>
* <font size=2>OkSocket Library is uploaded in JCenter,please add the code into your project gradle file</font>
    
```groovy
allprojects {
    repositories {
        jcenter()
    }
}
```
* <font size=2>Make sure you already done with put JCenter into repositories blocking in project gradle 
files than you need put this into Module build.gradle file</font>

```groovy
dependencies {
        //Basic Socket client functionality
        compile 'com.tonystark.android:socket:3.0.0'
	//If you want to use server functionality, you need compile the following libraries
	compile 'com.tonystark.android:socket-server:3.0.1'
}
```

### <font id="3">Manifest Configuration</font>
* <font size=2>Put the Permissions into AndroidManifest.xml file：</font>

```java
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```


### <font id="4">Proguard Configuration</font>
* <font size=2>Put this code into your Proguard file：</font>

```
-dontwarn com.xuhao.android.libsocket.**
-dontwarn com.xuhao.android.common.**
-dontwarn com.xuhao.android.server.**

-keep class com.xuhao.android.common.** { *; }
-keep class com.xuhao.android.server.** { *; }
-keep class com.xuhao.android.libsocket.** { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class com.xuhao.android.socket.sdk.OkSocketOptions$* {
    *;
}
-keep class com.xuhao.android.server.impl.OkServerOptions$* {
    *;
}
```

### <font id="5">OkSocket Initialization</font>
* <font size=2>To copy the following code into the project Application class onCreate (), OkSocket will automatically detect the environment and complete the configuration</font>

```java
public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		//The main process needs to be distinguished at the beginning of the primary process.
		OkSocket.initialize(this);
		//If you need to open the Socket debug log, configure the following
		//OkSocket.initialize(this,true);
	}
}
```
### <font id="6">Call The Demonstration</font>

##### Demo Connection Server
<font size=2>The server is designed for beginners affiliated OkSocket library, beginners can install the project of the app to mobile phones, click the `Connect` button, the server is only familiar with communication methods and analytical way. The server does not support the heart back, not a commercial server. The server code in ` SocketServerDemo ` folder, please note that the reference reading.</font>

* IP:`104.238.184.237`
* Port:`8080`

<font size=2>You can also choose to download the JAR file to local and run it locally for debugging [Download JAR](https://github.com/xuuhaoo/OkSocket/blob/release-2.0.0-beta/server/out/artifacts/socketserver_jar/socketserver.jar?raw=true "download jar file")</font>

* <font size=2>You can use the following code to run it`java -jar SocketServerDemo.jar`</font>

##### <font id="6.1">Simple connections</font>
* <font size=2> OkSocket will default to each Open new channels for cache management, only in the first call to the Open method is created when the ConnectionManager manager, after the caller can pass retrieves a reference to the ConnectionManager, continue to call the related method</font>
* <font size=2> ConnectionManager is mainly responsible for the Socket connection, disconnect, send message, heartbeat management, etc.</font>

```java
//Connection parameter Settings (IP, port number), which is also a unique identifier for a connection, with different connections, at least one of the two values in this parameter
ConnectionInfo info = new ConnectionInfo("104.238.184.237", 8080);
//Call OkSocket, open the channel for this connection, and call the channel's connection method for physical connections.
OkSocket.open(info).connect();
```
##### <font id="6.2">A connection with callback</font>
* <font size=2> Registered Socket channel listener, each Socket channel listener in isolation from each other, so if in a project to connect multiple Socket channel, need to be registered in each Socket channel listeners own connections, connection listener OkSocket library is the the only way to interact with the caller</font>

```java
//After obtaining the connection manager from the above method...
manager.registerReceiver(new SocketActionAdapter(){
	@Override
	public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
	 Toast.makeText(context, "The connection is successful", LENGTH_SHORT).show();
	}
});
//call the channel's connection method for physical connections.
manager.connect();
```
##### <font id="6.3">Configurable connections</font>
* <font size=2> Obtain OkSocketOptions behavior belongs to the more advanced behavior, each Socket connection will correspond to a OkSocketOptions, if call Open for the first time is not specified OkSocketOptions, OkSocket library will use a default configuration object, the default configuration, please see the class documentation</font>

```java
ConnectionInfo info = new ConnectionInfo("104.238.184.237", 8080);
IConnectionManager manager = OkSocket.open(info);
//Gets the reference object for the current connection channel
OkSocketOptions options= manager.getOption();
//Build a builder class based on the current reference object
OkSocketOptions.Builder builder = new OkSocketOptions.Builder(options);
//Modify the parameter Settings (refer to the class documentation for other references)
builder.setSinglePackageBytes(size);
//Create a new reference object and set it to the channel
manager.option(builder.build());
manager.connect();
```

##### <font id="6.4">How do I send data</font>
```java
//Class A:
//...Define the data structure to be sent...
public class TestSendData implements ISendable {
	private String str = "";

    public TestSendData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", 14);
            jsonObject.put("data", "{x:2,y:1}");
            str = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] parse() {
    	//Build the byte array according to the server's parsing rules
        byte[] body = str.getBytes(Charset.defaultCharset());
        ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(body.length);
        bb.put(body);
        return bb.array();
    }
}

//Class B:
private IConnectionManager mManager;
//...Omit the connection and set the associated code for the callback...
@Override
public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
     //Chain programming call
     OkSocket.open(info)
     	.send(new TestSendData());
     ============OR==============
     //The IConnectManager can also be saved as a member variable.
     mManager = OkSocket.open(info);
     if(mManager != null){
        mManager.send(new TestSendData());
     }
}
```
##### <font id="6.4">How to receive data</font>
* The OkSocket client receives server data in a certain format, and the client's OkSocketOptions provides the interface to modify the default header parsing rules. See below for the default package body parsing rules
![img](https://github.com/xuuhaoo/OkSocket/blob/master/package.png?raw=true)
* As above, the contents of the header for 4 bytes of type int, the int value identifies the inclusions the length of the data area(body area), this is the default resolution, if you need custom header please according to the following method.

```java
//Set the custom parsing protocol
OkSocketOptions.Builder okOptionsBuilder = new OkSocketOptions.Builder(mOkOptions);
okOptionsBuilder.setReaderProtocol(new IReaderProtocol() {
    @Override
    public int getHeaderLength() {
    	//Returns a custom header length that automatically parses the length of the head
        return 0;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
    	//The length of the body is parsed from the header, byteOrder is the sequence of bytes that you configured in the parameter, which can be easily parsed using ByteBuffer
        return 0;
    }
});
//Set the modified parameter to the connection manager
mManager.option(okOptionsBuilder.build());


//...After the parsing header is properly set...
@Override
public void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data) {
    //Follow the above rules, this callback can be normal received the data returned from the server, the data in the OriginalData, for byte [] array, the array data already processed byte sequence problem, can be used in the ByteBuffer directly
}
```

##### <font id="6.4">How to keep a heartbeat</font>
```java
//Class A:
//...Define the heartbeat data type required by the heartbeat manager...
public class PulseData implements IPulseSendable {
	private String str = "pulse";

    @Override
    public byte[] parse() {
    //Build the byte array according to the server's parsing rules
        byte[] body = str.getBytes(Charset.defaultCharset());
        ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(body.length);
        bb.put(body);
        return bb.array();
    }
}

//Class B:
private IConnectionManager mManager;
private PulseData mPulseData = new PulseData;
@Override
public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
     //Chain programming call. Set heartbeat data for the heartbeat manager. A connection only has a heartbeat manager, so the data is set only once. If disconnected, please set it again.
     OkSocket.open(info)
     	.getPulseManager()
     	.setPulseSendable(mPulseData)
     	.pulse();//Start the heartbeat. 
     	//After the heartbeat, the heartbeat manager will automatically carry out the heartbeat trigger
  		======OR======
     mManager = OkSocket.open(info);
     if(mManager != null){
        PulseManager pulseManager = mManager.getPulseManager();
        //Set the heartbeat data to the heartbeat manager, and a connection has only one heartbeat manager, so the data is only set once, and if disconnected, set it again.
        pulseManager.setPulseSendable(mPulseData);
        //Start the heartbeat. After the heartbeat, the heartbeat manager will automatically carry out the heartbeat trigger
        pulseManager.pulse();
     }
}
```
##### <font id="6.4">The heartbeat feed dog</font>
* Feed the dog because our client needs to know the Socket server received the heart data, so the server after received my heartbeat data need to reply the client after receiving the response data,called it ACK. We need to be local dog operations, or when more than a certain number of times the heart data, but did not get feed the dog, the dog will connect the disconnect reconnection.

```java
private IConnectionManager mManager;
//When the client receives the message
@Override
public void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data) {
	if(mManager != null && It's the heartbeat return package){
		//Whether it is a heartbeat return package, you need to resolve the data returned by the server to know
		//Feed dog
	    mManager.getPulseManager().feed();
	}
}
```


##### <font id="6.4">Manual trigger heartbeat</font>
```java
private IConnectionManager mManager;
//...in anywhere...
mManager = OkSocket.open(info);
if(mManager != null){
	PulseManager pulseManager = mManager.getPulseManager();
	//Manually trigger a heartbeat (mainly used for situations requiring manual control of trigger timing)
	pulseManager.trigger();
}
```
### <font id="7">Explanation Of OkSocketOptions And Callbacks</font>
* OkSocketOptions
	* Socket communication mode`mIOThreadMode`
	* Connection is managed save`isConnectionHolden`
	* Write byte order`mWriteOrder`
	* Read byte order`mReadByteOrder`
	* Header protocol`mHeaderProtocol`
	* The total length of a single packet is sent`mSendSinglePackageBytes`
	* Cache byte length for single reads`mReadSingleTimeBufferBytes`
	* Pulse frequency interval`mPulseFrequency`
	* Maximum number of pulse loss (loss of feed dog)`mPulseFeedLoseTimes`
	* Backstage survival time (minutes)`mBackgroundLiveMinute`
	* Connection timeout (seconds)`mConnectTimeoutSecond`
	* Maximum number of megabytes (MB) reading data`mMaxReadDataMB`
	* Reconnection manager`mReconnectionManager`

* ISocketActionListener
	* A callback for the Socket reader/writer thread start`onSocketIOThreadStart`
	* A callback for the Socket read/write thread shutdown`onSocketIOThreadShutdown`
	* Socket connection status changes`onSocketDisconnection`
	* Socket connection successful callback`onSocketConnectionSuccess`
	* Socket connection failed callback`onSocketConnectionFailed`
	* The Socket reads the callback from the server to the byte`onSocketReadResponse`
	* Socket write to server byte back callback`onSocketWriteResponse`
	* Send a heartbeat back`onPulseSend`


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



















