package com.xuhao.didi.oksocket;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.oksocket.adapter.LogAdapter;
import com.xuhao.didi.oksocket.data.AdminHandShakeBean;
import com.xuhao.didi.oksocket.data.AdminKickOfflineBean;
import com.xuhao.didi.oksocket.data.LogBean;
import com.xuhao.didi.oksocket.data.RestartBean;
import com.xuhao.didi.socket.client.impl.client.action.ActionDispatcher;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.nio.charset.Charset;

/**
 * Created by Tony on 2017/10/24.
 */

public class ServerAdminActivity extends AppCompatActivity {
    private ConnectionInfo mInfo;


    private EditText mIPEt;
    private EditText mPortEt;
    private IConnectionManager mManager;
    private OkSocketOptions mOkOptions;
    private Button mConnect;
    private Button mClearLog;
    private Button mRestart;
    private Button mKickOffLine;

    private RecyclerView mOpsList;
    private String mPass;

    private LogAdapter mReceLogAdapter = new LogAdapter();

    private SocketActionAdapter adapter = new SocketActionAdapter() {

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            AdminHandShakeBean adminHandShakeBean = new AdminHandShakeBean(mPass);
            mManager.send(adminHandShakeBean);
            mConnect.setText("DisConnect");
            log("连接成功");
            mPortEt.setEnabled(false);
            mIPEt.setEnabled(false);
        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            if (e != null) {
                log("异常断开:" + e.getMessage());
            } else {
                log("正常断开");
            }
            mPortEt.setEnabled(true);
            mIPEt.setEnabled(true);
            mConnect.setText("Connect");
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            log("连接失败");
            mConnect.setText("Connect");
            mPortEt.setEnabled(true);
            mIPEt.setEnabled(true);
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            log(str);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        findViews();
        initData();
        setListener();
    }

    private void findViews() {
        mOpsList = findViewById(R.id.ops_list);
        mIPEt = findViewById(R.id.ip);
        mPortEt = findViewById(R.id.port);
        mClearLog = findViewById(R.id.clear_log);
        mConnect = findViewById(R.id.connect);
        mRestart = findViewById(R.id.restart);
        mKickOffLine = findViewById(R.id.kick_people_offline);
    }

    private void initData() {
        LinearLayoutManager manager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mOpsList.setLayoutManager(manager2);
        mOpsList.setAdapter(mReceLogAdapter);

        initManager();
    }

    private void initManager() {
        final Handler handler = new Handler();
        mInfo = new ConnectionInfo(mIPEt.getText().toString(), Integer.parseInt(mPortEt.getText().toString()));
        mOkOptions = new OkSocketOptions.Builder()
                .setConnectTimeoutSecond(10)
                .setCallbackThreadModeToken(new OkSocketOptions.ThreadModeToken() {
                    @Override
                    public void handleCallbackEvent(ActionDispatcher.ActionRunnable runnable) {
                        handler.post(runnable);
                    }
                })
                .build();
        mManager = OkSocket.open(mInfo).option(mOkOptions);
        mManager.registerReceiver(adapter);
    }


    private void setListener() {
        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mManager == null) {
                    return;
                }
                if (!mManager.isConnect()) {
                    initManager();
                    final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.alert_admin_login_layout, null);
                    new AlertDialog.Builder(ServerAdminActivity.this)
                            .setTitle("Admin Login")
                            .setView(view)
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mPass = ((EditText) view.findViewById(R.id.pass)).getText().toString();
                                    mPortEt.setEnabled(false);
                                    mIPEt.setEnabled(false);
                                    mManager.connect();
                                }
                            }).show();
                } else {
                    mConnect.setText("DisConnecting");
                    mManager.disconnect();
                }
            }
        });
        mClearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReceLogAdapter.getDataList().clear();
                mReceLogAdapter.notifyDataSetChanged();
            }
        });

        mRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mManager == null) {
                    return;
                }
                if (!mManager.isConnect()) {
                    Toast.makeText(getBaseContext(), "请先连接!", Toast.LENGTH_SHORT).show();
                } else {
                    mManager.send(new RestartBean());
                }
            }
        });
        mKickOffLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mManager == null) {
                    return;
                }
                if (!mManager.isConnect()) {
                    Toast.makeText(getBaseContext(), "请先连接!", Toast.LENGTH_SHORT).show();
                } else {
                    final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.alert_kickoffline_layout, null);
                    new AlertDialog.Builder(ServerAdminActivity.this)
                            .setTitle("KickOffline")
                            .setView(view)
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Do it", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String who = ((EditText) view.findViewById(R.id.who)).getText().toString();
                                    mManager.send(new AdminKickOfflineBean(who));
                                }
                            }).show();
                }
            }
        });
    }

    private void log(final String log) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            LogBean logBean = new LogBean(System.currentTimeMillis(), log);
            try {
                logBean.mWho = log.substring(0, log.indexOf("@"));
            } catch (Exception e) {
            }
            mReceLogAdapter.getDataList().add(0, logBean);
            mReceLogAdapter.notifyDataSetChanged();
        } else {
            final String threadName = Thread.currentThread().getName();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    log(threadName + " 线程打印:" + log);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mManager != null) {
            mManager.disconnect();
            mManager.unRegisterReceiver(adapter);
        }
    }
}
