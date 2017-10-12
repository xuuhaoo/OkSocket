# OkSocket 简介
一个Android轻量级Socket通讯框架

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/xuuhaoo/maven/OkSocket/images/download.svg)](https://dl.bintray.com/xuuhaoo/maven/OkSocket/_latestVersion)

### <font id="1">OkSocket简介</font>
<font size=2>Android OkSocket是一款基于JavaNIO和传统Socket的一款Socket客户端整体解决方案.您可以使用它进行简单的基于Tcp协议的Socket通讯,当然,也可以进行大数据量复杂的Socket通讯,支持单工,双工通讯.</font>


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


##### <font id="2.2">手动集成</font>
* <font size=2>请向管理员索要相关的aar包,进行手动依赖.该种方式极不推荐,但考虑开发库有可能提供给第三方使用,故提供此方法.</font>

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

### <font id="5">简单的初始化</font>
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
ConnectionInfo info = new ConnectionInfo("111.206.162.232", 8088);
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
===

### <font id="7">高级调用使用说明</font>

* OkSocketOptions
	* Socket通讯模式`mIOThreadMode`
	* 连接是否管理保存`isConnectionHolden`
	* 写入字节序`mWriteOrder`
	* 读取字节序`mReadByteOrder`
	* 头字节协议`mHeaderProtocol`
	* 发送单个数据包的总长度`mSendSinglePackageBytes`
	* 单次读取的缓存字节长度`mReadSingleTimeBufferBytes`
	* 脉搏频率,每分钟多少次`mPulseFrequency`
	* 脉搏最大丢失次数`mPulseFeedLoseTimes`
	* 后台存活时间(分钟)`mBackgroundLiveMinute`
	* 连接超时时间(秒)`mConnectTimeoutSecond`
	* 最大读取数据的兆数(MB)`mMaxReadDataMB`
	* 重新连接管理器`mReconnectionManager`

> <font size=3>Tips:可以进行如上配置修改,其中有小部分值不支持热修改,热修改会导致崩溃,例如在阻塞模式下的IoThreadMode切换会导致崩溃.</font>

* ISocketActionListener
	* Socket读写线程启动后回调`onSocketIOThreadStart`
	* Socket读写线程关闭后回调`onSocketIOThreadShutdown`
	* Socket连接状态由连接->断开回调`onSocketDisconnection`
	* Socket连接成功回调`onSocketConnectionSuccess`
	* Socket连接失败回调`onSocketConnectionFailed`
	* Socket从服务器读取到字节回调`onSocketReadResponse`
	* Socket写给服务器字节后回调`onSocketWriteResponse`
	* 发送心跳后的回调`onPulseSend`










































