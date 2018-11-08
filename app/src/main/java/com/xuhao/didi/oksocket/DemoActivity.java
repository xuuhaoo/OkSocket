package com.xuhao.didi.oksocket;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xuhao.didi.common.basic.bean.OriginalData;
import com.xuhao.didi.common.interfacies.client.msg.ISendable;
import com.xuhao.didi.common.interfacies.server.IClient;
import com.xuhao.didi.common.interfacies.server.IClientIOCallback;
import com.xuhao.didi.common.interfacies.server.IClientPool;
import com.xuhao.didi.common.interfacies.server.IServerManager;
import com.xuhao.didi.common.interfacies.server.IServerShutdown;
import com.xuhao.didi.libsocket.sdk.OkSocket;
import com.xuhao.didi.oksocket.data.MsgDataBean;
import com.xuhao.didi.server.action.ServerActionAdapter;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * Created by didi on 2018/4/20.
 */

public class DemoActivity extends AppCompatActivity implements IClientIOCallback {

    private Button mSimpleBtn;

    private Button mComplexBtn;

    private Button mServerBtn;

    private IServerManager mServerManager;

    private TextView mIPTv;

    private int mPort = 8080;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        mSimpleBtn = findViewById(R.id.btn1);
        mComplexBtn = findViewById(R.id.btn2);
        mServerBtn = findViewById(R.id.btn3);
        mIPTv = findViewById(R.id.ip);

        mIPTv.setText("当前IP:" + getIPAddress());
        mSimpleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoActivity.this, SimpleDemoActivity.class);
                startActivity(intent);
            }
        });
        mComplexBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoActivity.this, ComplexDemoActivity.class);
                startActivity(intent);
            }
        });

        mServerManager = OkSocket.server(mPort).registerReceiver(new ServerActionAdapter() {
            @Override
            public void onServerListening(Context context, int serverPort) {
                super.onServerListening(context, serverPort);
                Log.i("ServerCallback", "onServerListening,serverPort:" + serverPort);
                flushServerText();
            }

            @Override
            public void onClientConnected(Context context, IClient client, int serverPort, IClientPool clientPool) {
                super.onClientConnected(context, client, serverPort, clientPool);
                Log.i("ServerCallback", "onClientConnected,serverPort:" + serverPort + "--ClientNums:" + clientPool.size() + "--ClientTag:" + client.getUniqueTag());
                client.addIOCallback(DemoActivity.this);
            }

            @Override
            public void onClientDisconnected(Context context, IClient client, int serverPort, IClientPool clientPool) {
                super.onClientDisconnected(context, client, serverPort, clientPool);
                Log.i("ServerCallback", "onClientDisconnected,serverPort:" + serverPort + "--ClientNums:" + clientPool.size() + "--ClientTag:" + client.getUniqueTag());
                client.removeIOCallback(DemoActivity.this);
            }

            @Override
            public void onServerWillBeShutdown(Context context, int serverPort, IServerShutdown shutdown, IClientPool clientPool, Throwable throwable) {
                super.onServerWillBeShutdown(context, serverPort, shutdown, clientPool, throwable);
                Log.i("ServerCallback", "onServerWillBeShutdown,serverPort:" + serverPort + "--ClientNums:" + clientPool
                        .size());
                shutdown.shutdown();
            }

            @Override
            public void onServerAlreadyShutdown(Context context, int serverPort) {
                super.onServerAlreadyShutdown(context, serverPort);
                Log.i("ServerCallback", "onServerAlreadyShutdown,serverPort:" + serverPort);
                mServerBtn.setText(mPort + "服务器启动");
            }
        });

        mIPTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData myClip = ClipData.newPlainText("ip", mIPTv.getText().toString().substring(5));
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(DemoActivity.this, "复制到剪切板", Toast.LENGTH_LONG).show();
            }
        });

        mServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mServerManager.isLive()) {
                    mServerManager.listen();
                } else {
                    mServerManager.shutdown();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        flushServerText();
        mIPTv.setText("当前IP:" + getIPAddress());
    }

    private void flushServerText() {
        if (mServerManager.isLive()) {
            mServerBtn.setText(mPort + "服务器关闭");
        } else {
            mServerBtn.setText(mPort + "服务器启动");
        }
    }

    @Override
    public void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool) {
        String str = new String(originalData.getBodyBytes(), Charset.forName("utf-8"));
        JsonObject jsonObject = null;
        try {
            jsonObject = new JsonParser().parse(str).getAsJsonObject();
            int cmd = jsonObject.get("cmd").getAsInt();
            if (cmd == 54) {//登陆成功
                String handshake = jsonObject.get("handshake").getAsString();
                Log.i("onClientIOServer", "接收到:" + client.getHostIp() + " 握手信息:" + handshake);
            } else if (cmd == 14) {//心跳
                Log.i("onClientIOServer", "接收到:" + client.getHostIp() + " 收到心跳");
            } else {
                Log.i("onClientIOServer", "接收到:" + client.getHostIp() + " " + str);
            }
        } catch (Exception e) {
            Log.i("onClientIOServer", "接收到:" + client.getHostIp() + " " + str);
        }
        MsgDataBean msgDataBean = new MsgDataBean(str);
        clientPool.sendToAll(msgDataBean);
    }

    @Override
    public void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool) {
        byte[] bytes = sendable.parse();
        bytes = Arrays.copyOfRange(bytes, 4, bytes.length);
        String str = new String(bytes, Charset.forName("utf-8"));
        JsonObject jsonObject = null;
        try {
            jsonObject = new JsonParser().parse(str).getAsJsonObject();
            int cmd = jsonObject.get("cmd").getAsInt();
            switch (cmd) {
                case 54: {
                    String handshake = jsonObject.get("handshake").getAsString();
                    Log.i("onClientIOServer", "发送给:" + client.getHostIp() + " 握手数据:" + handshake);
                    break;
                }
                default:
                    Log.i("onClientIOServer", "发送给:" + client.getHostIp() + " " + str);
            }
        } catch (Exception e) {
            Log.i("onClientIOServer", "发送给:" + client.getHostIp() + " " + str);
        }
    }

    public String getIPAddress() {
        NetworkInfo info = ((ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                if (ipAddress == 0) return "未连接wifi";
                return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                        + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
            }
        } else {
            //当前无网络连接,请在设置中打开网络
            return "当前无网络连接,请在设置中打开网络";
        }
        return "IP获取失败";
    }

}
