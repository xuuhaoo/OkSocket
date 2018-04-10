package com.xuhao.android.oksocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.SocketActionAdapter;
import com.xuhao.android.libsocket.sdk.bean.IPulseSendable;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.bean.OriginalData;
import com.xuhao.android.libsocket.sdk.connection.IConnectionManager;
import com.xuhao.android.oksocket.adapter.LogAdapter;
import com.xuhao.android.oksocket.data.HandShake;
import com.xuhao.android.oksocket.data.LogBean;
import com.xuhao.android.oksocket.data.NearCarRegisterRq;
import com.xuhao.android.oksocket.data.PulseBean;

import java.nio.charset.Charset;

import static android.widget.Toast.LENGTH_SHORT;
import static com.xuhao.android.libsocket.sdk.OkSocket.open;

public class MainActivity extends AppCompatActivity {

    private ConnectionInfo mInfo;

    private Button mConnect;
    private IConnectionManager mManager;
    private Button mSub;
    private Button mUnSub;
    private EditText mSendSizeET;
    private Button mSetSize;
    private OkSocketOptions mOkOptions;
    private EditText mFrequency;
    private Button mSetFrequency;
    private Button mMenualPulse;
    private Button mClearLog;

    private RecyclerView mSendList;
    private RecyclerView mReceList;

    private LogAdapter mSendLogAdapter = new LogAdapter();
    private LogAdapter mReceLogAdapter = new LogAdapter();

    private SocketActionAdapter adapter = new SocketActionAdapter() {

        @Override
        public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
            mManager.send(new HandShake());
            mConnect.setText("DisConnect");
        }

        @Override
        public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {
            if (e != null) {
                if (e instanceof RedirectException) {
                    logSend("正在重定向连接...");
                    mManager.switchConnectionInfo(((RedirectException) e).redirectInfo);
                    mManager.connect();
                } else {
                    logSend("异常断开:" + e.getMessage());
                }
            } else {
                Toast.makeText(context, "正常断开", LENGTH_SHORT).show();
                logSend("正常断开");
            }
            mConnect.setText("Connect");
        }

        @Override
        public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
            Toast.makeText(context, "连接失败" + e.getMessage(), LENGTH_SHORT).show();
            logSend("连接失败");
            mConnect.setText("Connect");
        }

        @Override
        public void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data) {
            super.onSocketReadResponse(context, info, action, data);
            String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            logRece(str);
            //打印日志
            byte[] body = data.getBodyBytes();
            String bodyStr = new String(body);
            JsonObject jsonObject = new JsonParser().parse(bodyStr).getAsJsonObject();
            int cmd = jsonObject.get("cmd").getAsInt();
            if (cmd == 54) {//登陆成功
                mManager.getPulseManager().setPulseSendable(new PulseBean()).pulse();
            } else if (cmd == 57) {//切换
                String ip = jsonObject.get("data").getAsString().split(":")[0];
                int port = Integer.parseInt(jsonObject.get("data").getAsString().split(":")[1]);
                ConnectionInfo redirectInfo = new ConnectionInfo(ip, port);
                redirectInfo.setBackupInfo(mInfo.getBackupInfo());
                mManager.getReconnectionManager().addIgnoreException(RedirectException.class);
                mManager.disConnect(new RedirectException(redirectInfo));
            } else if (cmd == 14) {//心跳
                mManager.getPulseManager().feed();
            }
        }

        @Override
        public void onSocketWriteResponse(Context context, ConnectionInfo info, String action, ISendable data) {
            super.onSocketWriteResponse(context, info, action, data);
            String str = new String(data.parse(), Charset.forName("utf-8"));
            logSend(str);
        }

        @Override
        public void onPulseSend(Context context, ConnectionInfo info, IPulseSendable data) {
            super.onPulseSend(context, info, data);
            String str = new String(data.parse(), Charset.forName("utf-8"));
            logSend(str);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initData();
        setListener();
    }

    private void findViews() {
        mSendList = (RecyclerView) findViewById(R.id.send_list);
        mReceList = (RecyclerView) findViewById(R.id.rece_list);
        mClearLog = (Button) findViewById(R.id.clear_log);
        mSetFrequency = (Button) findViewById(R.id.set_pulse_frequency);
        mFrequency = (EditText) findViewById(R.id.pulse_frequency);
        mConnect = (Button) findViewById(R.id.connect);
        mSub = (Button) findViewById(R.id.subscript);
        mUnSub = (Button) findViewById(R.id.unsubscript);
        mSendSizeET = (EditText) findViewById(R.id.send_size);
        mSetSize = (Button) findViewById(R.id.set_size);
        mMenualPulse = (Button) findViewById(R.id.manual_pulse);
    }

    private void initData() {
        LinearLayoutManager manager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSendList.setLayoutManager(manager1);
        mSendList.setAdapter(mSendLogAdapter);

        LinearLayoutManager manager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReceList.setLayoutManager(manager2);
        mReceList.setAdapter(mReceLogAdapter);

//        mInfo = new ConnectionInfo("111.206.162.233", 8088);
        mInfo = new ConnectionInfo("117.136.38.163", 8080);
        mOkOptions = OkSocketOptions.getDefault();
        mManager = open(mInfo, mOkOptions);
    }

    private void setListener() {
        mManager.registerReceiver(adapter);
        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mManager == null) {
                    return;
                }
                if (!mManager.isConnect()) {
                    mManager.connect();
                } else {
                    mConnect.setText("DisConnecting");
                    mManager.disConnect();
                }
            }
        });
        mSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mManager == null) {
                    return;
                }
                if (!mManager.isConnect()) {
                    Toast.makeText(getApplicationContext(), "未连接,请先连接", LENGTH_SHORT).show();
                } else {
                    NearCarRegisterRq nearCarRegisterRq = new NearCarRegisterRq(getApplicationContext(), true);
                    mManager.send(nearCarRegisterRq);
                }
            }
        });
        mClearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReceLogAdapter.getDataList().clear();
                mSendLogAdapter.getDataList().clear();
                mReceLogAdapter.notifyDataSetChanged();
                mSendLogAdapter.notifyDataSetChanged();
            }
        });
        mUnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mManager == null) {
                    return;
                }
                if (!mManager.isConnect()) {
                    Toast.makeText(getApplicationContext(), "未连接,请先连接", LENGTH_SHORT).show();
                } else {
                    NearCarRegisterRq nearCarRegisterRq = new NearCarRegisterRq(getApplicationContext(), false);
                    mManager.send(nearCarRegisterRq);
                }
            }
        });
        mSetSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mManager == null) {
                    return;
                }
                String sizestr = mSendSizeET.getText().toString();
                int size = 0;
                try {
                    size = Integer.parseInt(sizestr);
                    mOkOptions = new OkSocketOptions.Builder(mOkOptions)
                            .setSinglePackageBytes(size).build();
                    mManager.option(mOkOptions);
                } catch (NumberFormatException e) {
                }
            }
        });
        mSetFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mManager == null) {
                    return;
                }
                String timeoutstr = mFrequency.getText().toString();
                long frequency = 0;
                try {
                    frequency = Long.parseLong(timeoutstr);
                    mOkOptions = new OkSocketOptions.Builder(mOkOptions)
                            .setPulseFrequency(frequency).build();
                    mManager.option(mOkOptions);
                } catch (NumberFormatException e) {
                }
            }
        });
        mMenualPulse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManager.send(new PulseBean());
            }
        });
    }

    private void logSend(String log) {
        LogBean logBean = new LogBean(System.currentTimeMillis(), log);
        mSendLogAdapter.getDataList().add(0, logBean);
        mSendLogAdapter.notifyDataSetChanged();
    }

    private void logRece(String log) {
        LogBean logBean = new LogBean(System.currentTimeMillis(), log);
        mReceLogAdapter.getDataList().add(0, logBean);
        mReceLogAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mManager != null) {
            mManager.disConnect();
        }
    }

}

