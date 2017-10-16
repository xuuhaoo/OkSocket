# OkSocket 简介
一个Android轻量级Socket通讯框架

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/xuuhaoo/maven/OkSocket/images/download.svg)](https://dl.bintray.com/xuuhaoo/maven/OkSocket/_latestVersion)

### <font id="1">OkSocket简介</font>
<font size=2>Android OkSocket是一款基于阻塞式传统Socket的一款Socket客户端整体解决方案.您可以使用它进行简单的基于Tcp协议的Socket通讯,当然,也可以进行大数据量复杂的Socket通讯,
支持单工,双工通讯.</font>


### <font id="2">Maven配置</font>
##### <font id="2.1">自动导入(推荐)</font>
* <font size=2>OkSocket 支持 JCenter 直接导入</font>
    
```groovy
allprojects {
    repositories {
        jcenter()
    }
}
```
* <font size=2>在Module的build.gradle文件中添加依赖配置</font>

```groovy
dependencies {
        compile 'com.tonystark.android:socket:1.0.0'
}
```

### <font id="3">参数配置</font>
* <font size=2>在AndroidManifest.xml中添加权限：</font>

```java
<uses-permission android:name="android.permission.GET_ACCOUNTS"/>
<uses-permission android:name="android.permission.READ_PROFILE"/>
<uses-permission android:name="android.permission.READ_CONTACTS"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```


### <font id="4">混淆配置</font>
* <font size=2>请避免混淆OkSocket，在Proguard混淆文件中增加以下配置：</font>

```
-dontwarn com.xuhao.android.libsocket.**
-keep class com.xuhao.android.socket.impl.abilities.** { *; }
-keep class com.xuhao.android.socket.impl.exceptions.** { *; }
-keep class com.xuhao.android.socket.impl.EnvironmentalManager { *; }
-keep class com.xuhao.android.socket.impl.BlockConnectionManager { *; }
-keep class com.xuhao.android.socket.impl.UnBlockConnectionManager { *; }
-keep class com.xuhao.android.socket.impl.SocketActionHandler { *; }
-keep class com.xuhao.android.socket.impl.PulseManager { *; }
-keep class com.xuhao.android.socket.impl.ManagerHolder { *; }
-keep class com.xuhao.android.socket.interfaces.** { *; }
-keep class com.xuhao.android.socket.sdk.** { *; }
 # 枚举类不能被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class com.xuhao.android.socket.sdk.OkSocketOptions$* {
    *;
}

```

### <font id="5">OkSocket初始化</font>
* <font size=2>将以下代码复制到项目Application类onCreate()中，OkSocket会为自动检测环境并完成配置：</font>

```java
public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		//在主进程初始化一次,多进程时需要区分主进程.
		OkSocket.initialize(this);
		//如果需要开启Socket调试日志,请配置
		//OkSocket.initialize(this,true);
	}
}
```
### <font id="6">调用演示</font>
##### <font id="6.1">简单的长连接</font>
* <font size=2> OkSocket 会默认对每一个 Open 的新通道做缓存管理,仅在第一次调用 Open 方法时创建 ConnectionManager 管理器,之后调用者可以通过获取到该ConnectionManager的引用,继续调用相关方法 </font>
* <font size=2> ConnectionManager 主要负责该地址的套接字连接断开发送消息等操作.</font>

```java
//连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
ConnectionInfo info = new ConnectionInfo("127.0.0.1", 8088);
//调用OkSocket,开启这次连接的通道,调用通道的连接方法进行连接.
OkSocket.open(info).connect();
```
##### <font id="6.2">有回调的长连接</font>
* <font size=2> 注册该通道的监听器,每个 Connection 通道中的监听器互相隔离,因此如果一个项目连接了多个 Socket 连接需要在每个 Connection 注册自己的连接监听器,连接监听器是该 OkSocket 与用户交互的唯一途径</font>

```java
//连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
ConnectionInfo info = new ConnectionInfo("127.0.0.1", 8088);
//调用OkSocket,开启这次连接的通道,拿到通道Manager
IConnectionManager manager = OkSocket.open(info);
//注册Socket行为监听器,SocketActionAdapter是回调的Simple类,其他回调方法请参阅类文档
manager.registerReceiver(new SocketActionAdapter(){
	@Override
	public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
	 Toast.makeText(context, "连接成功", LENGTH_SHORT).show();
	}
});
//调用通道进行连接
manager.connect();
```
##### <font id="6.3">可配置的长连接</font>
* <font size=2> 获得 OkSocketOptions 的行为属于比较高级的 OkSocket 调用方法,每个 Connection 将会对应一个 OkSocketOptions,如果第一次调用 Open 时未指定 OkSocketOptions,OkSocket将会使用默认的配置对象,默认配置请见文档下方的高级调用说明</font>

```java
//连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
ConnectionInfo info = new ConnectionInfo("127.0.0.1", 8088);
//调用OkSocket,开启这次连接的通道,拿到通道Manager
IConnectionManager manager = OkSocket.open(info);
//获得当前连接通道的参配对象
OkSocketOptions options= manager.getOption();
//基于当前参配对象构建一个参配建造者类
OkSocketOptions.Builder builder = new OkSocketOptions.Builder(options);
//修改参配设置(其他参配请参阅类文档)
builder.setSinglePackageBytes(size);
//建造一个新的参配对象并且付给通道
manager.option(builder.build());
//调用通道进行连接
manager.connect();
```

##### <font id="6.4">如何进行数据发送</font>
```java
//类A:
//...定义将要发送的数据结构体...
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
        //根据服务器的解析规则,构建byte数组
        byte[] body = str.getBytes(Charset.defaultCharset());
        ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(body.length);
        bb.put(body);
        return bb.array();
    }
}

//类B:
private IConnectionManager mManager;
//...省略连接及设置回调的代码...
@Override
public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
     //连接成功其他操作...
     //链式编程调用
     OkSocket.open(info)
     	.send(new TestSendData());
     
     //此处也可将ConnectManager保存成成员变量使用.
     mManager = OkSocket.open(info);
     if(mManager != null){
        mManager.send(new TestSendData());
     }
     //以上两种方法选择其一,成员变量的方式请注意判空
}
```
##### <font id="6.4">如何接收数据</font>
* OkSocket客户端接收服务器数据是要求一定格式的,客户端的OkSocketOptions提供了接口来修改默认的服务器返回的包头解析规则.请看下图为默认的包头包体解析规则
![img](https://github.com/xuuhaoo/OkSocket/blob/master/package.png?raw=true)
* 如上图包头中的内容为4个字节长度的int型,该int值标识了包体数据区的长度,这就是默认的头解析,如果需要自定义头请按照如下方法.

```java
//设置自定义解析头
OkSocketOptions.Builder okOptionsBuilder = new OkSocketOptions.Builder(mOkOptions);
okOptionsBuilder.setHeaderProtocol(new IHeaderProtocol() {
    @Override
    public int getHeaderLength() {
    	//返回自定义的包头长度,框架会解析该长度的包头
        return 0;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
    	//从header(包头数据)中解析出包体的长度,byteOrder是你在参配中配置的字节序,可以使用ByteBuffer比较方便解析
        return 0;
    }
});
//将新的修改后的参配设置给连接管理器
mManager.option(okOptionsBuilder.build());


//...正确设置解析头之后...
@Override
public void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data) {
    //遵循以上规则,这个回调才可以正常收到服务器返回的数据,数据在OriginalData中,为byte[]数组,该数组数据已经处理过字节序问题,直接放入ByteBuffer中即可使用
}
```

##### <font id="6.4">如何保持心跳</font>
```java
//类A:
//...定义心跳管理器需要的心跳数据类型...
public class PulseData implements IPulseSendable {
	private String str = "pulse";

    @Override
    public byte[] parse() {
        byte[] body = str.getBytes(Charset.defaultCharset());
        ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(body.length);
        bb.put(body);
        return bb.array();
    }
}

//类B:
private IConnectionManager mManager;
private PulseData mPulseData = new PulseData;
//...省略连接及设置回调的代码...
@Override
public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
     //连接成功其他操作...
     //链式编程调用,给心跳管理器设置心跳数据,一个连接只有一个心跳管理器,因此数据只用设置一次,如果断开请再次设置.
     OkSocket.open(info)
     	.getPulseManager()
     	.setPulseSendable(mPulseData)
     	.pulse();//开始心跳,开始心跳后,心跳管理器会自动进行心跳触发
     			
     //此处也可将ConnectManager保存成成员变量使用.
     mManager = OkSocket.open(info);
     if(mManager != null){
        PulseManager pulseManager = mManager.getPulseManager();
        //给心跳管理器设置心跳数据,一个连接只有一个心跳管理器,因此数据只用设置一次,如果断开请再次设置.
        pulseManager.setPulseSendable(mPulseData);
        //开始心跳,开始心跳后,心跳管理器会自动进行心跳触发
        pulseManager.pulse();
     }
     //以上两种方法选择其一,成员变量的方式请注意判空
}
```
##### <font id="6.4">心跳接收到了之后需要进行喂狗</font>
* 因为我们的客户端需要知道服务器收到了此次心跳,因此服务器在收到心跳后需要进行应答,我们收到此次心跳应答后,需要进行本地的喂狗操作,否则当超过一定次数的心跳发送,未得到喂狗操作后,狗将会将此次连接断开重连.

```java
//定义成员变量
private IConnectionManager mManager;
//当客户端收到消息后
@Override
public void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data) {
	if(mManager != null && 是心跳返回包){//是否是心跳返回包,需要解析服务器返回的数据才可知道
		//喂狗操作
	    mManager.getPulseManager().feed();
	}
}
```


##### <font id="6.4">如何手动触发一次心跳,在任何时间</font>
```java
//定义成员变量
private IConnectionManager mManager;
//...在任意地方...
mManager = OkSocket.open(info);
if(mManager != null){
	PulseManager pulseManager = mManager.getPulseManager();
	//手动触发一次心跳(主要用于一些需要手动控制触发时机的场景)
	pulseManager.trigger();
}
```
### <font id="7">OkSocket参配选项及回调说明</font>
* OkSocketOptions
	* Socket通讯模式`mIOThreadMode`
	* 连接是否管理保存`isConnectionHolden`
	* 写入字节序`mWriteOrder`
	* 读取字节序`mReadByteOrder`
	* 头字节协议`mHeaderProtocol`
	* 发送单个数据包的总长度`mSendSinglePackageBytes`
	* 单次读取的缓存字节长度`mReadSingleTimeBufferBytes`
	* 脉搏频率间隔毫秒数`mPulseFrequency`
	* 脉搏最大丢失次数(狗的失喂次数)`mPulseFeedLoseTimes`
	* 后台存活时间(分钟)`mBackgroundLiveMinute`
	* 连接超时时间(秒)`mConnectTimeoutSecond`
	* 最大读取数据的兆数(MB)`mMaxReadDataMB`
	* 重新连接管理器`mReconnectionManager`

* ISocketActionListener
	* Socket读写线程启动后回调`onSocketIOThreadStart`
	* Socket读写线程关闭后回调`onSocketIOThreadShutdown`
	* Socket连接状态由连接->断开回调`onSocketDisconnection`
	* Socket连接成功回调`onSocketConnectionSuccess`
	* Socket连接失败回调`onSocketConnectionFailed`
	* Socket从服务器读取到字节回调`onSocketReadResponse`
	* Socket写给服务器字节后回调`onSocketWriteResponse`
	* 发送心跳后的回调`onPulseSend`










































