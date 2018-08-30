package com.xuhao.android.oksocket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.xuhao.android.common.interfacies.server.IClient;
import com.xuhao.android.common.interfacies.server.IClientPool;
import com.xuhao.android.common.interfacies.server.IServerManager;
import com.xuhao.android.common.interfacies.server.IServerShutdown;
import com.xuhao.android.libsocket.sdk.OkSocket;
import com.xuhao.android.server.action.ServerActionAdapter;

/**
 * Created by didi on 2018/4/20.
 */

public class DemoActivity extends AppCompatActivity {

    private Button mSimpleBtn;

    private Button mComplexBtn;

    private Button mServerBtn;

    private IServerManager mServerManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        mSimpleBtn = findViewById(R.id.btn1);
        mComplexBtn = findViewById(R.id.btn2);
        mServerBtn = findViewById(R.id.btn3);

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

        mServerManager = OkSocket.server(8080).registerReceiver(new ServerActionAdapter() {
            @Override
            public void onClientConnected(Context context, IClient client, int serverPort, IClientPool clientPool) {
                super.onClientConnected(context, client, serverPort, clientPool);
                Log.i("ServerCallback", "onClientConnected,serverPort:" + serverPort + "--ClientNums:" + clientPool
                        .size() + "--ClientTag:" + client.getUniqueTag());
            }

            @Override
            public void onServerListening(Context context, int serverPort) {
                super.onServerListening(context, serverPort);
                Log.i("ServerCallback", "onServerListening,serverPort:" + serverPort);
                flushServerText();
            }

            @Override
            public void onClientDisconnected(Context context, IClient client, int serverPort, IClientPool clientPool) {
                super.onClientDisconnected(context, client, serverPort, clientPool);
                Log.i("ServerCallback", "onClientDisconnected,serverPort:" + serverPort + "--ClientNums:" + clientPool.size() + "--ClientTag:" + client.getUniqueTag());
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
                mServerBtn.setText("127.0.0.1/8080服务器启动");
            }
        });

        mServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServerManager.listen();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        flushServerText();
    }

    private void flushServerText() {
        if (mServerManager.isLive()) {
            mServerBtn.setText("127.0.0.1/8080服务器关闭");
        } else {
            mServerBtn.setText("127.0.0.1/8080服务器启动");
        }
    }
}
