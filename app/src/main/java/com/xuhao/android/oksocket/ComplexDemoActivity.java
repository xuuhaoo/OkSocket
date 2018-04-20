package com.xuhao.android.oksocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.SocketActionAdapter;
import com.xuhao.android.libsocket.sdk.bean.IPulseSendable;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.bean.OriginalData;
import com.xuhao.android.libsocket.sdk.connection.IConnectionManager;
import com.xuhao.android.libsocket.sdk.connection.NoneReconnect;
import com.xuhao.android.oksocket.adapter.LogAdapter;
import com.xuhao.android.oksocket.data.HandShake;
import com.xuhao.android.oksocket.data.LogBean;
import com.xuhao.android.oksocket.data.NearCarRegisterRq;
import com.xuhao.android.oksocket.data.PulseBean;

import java.nio.charset.Charset;

import static android.widget.Toast.LENGTH_SHORT;
import static com.xuhao.android.libsocket.sdk.OkSocket.open;

public class ComplexDemoActivity extends AppCompatActivity {

    private ConnectionInfo mInfo;

    private Button mConnect;
    private IConnectionManager mManager;
    private EditText mSendSizeET;
    private Button mSetSize;
    private EditText mFrequency;
    private Button mSetFrequency;
    private Button mMenualPulse;
    private Button mClearLog;
    private SwitchCompat mReconnectSwitch;
    private SwitchCompat mLiveBGSwitch;

    private RecyclerView mSendList;
    private RecyclerView mReceList;

    private LogAdapter mSendLogAdapter = new LogAdapter();
    private LogAdapter mReceLogAdapter = new LogAdapter();

    private SocketActionAdapter adapter = new SocketActionAdapter() {

        @Override
        public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
            mManager.send(new HandShake());
            mConnect.setText("DisConnect");
            initSwitch();
        }

        private void initSwitch() {
            OkSocketOptions okSocketOptions = mManager.getOption();
            int minute = okSocketOptions.getBackgroundLiveMinute();
            mLiveBGSwitch.setChecked(minute != -1);
            mReconnectSwitch.setChecked(!(okSocketOptions.getReconnectionManager() instanceof NoneReconnect));
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
                mManager.disconnect(new RedirectException(redirectInfo));
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
        setContentView(R.layout.activity_complex);
        findViews();
        initData();
        setListener();
    }

    private void findViews() {
        mSendList = findViewById(R.id.send_list);
        mReceList = findViewById(R.id.rece_list);
        mClearLog = findViewById(R.id.clear_log);
        mSetFrequency = findViewById(R.id.set_pulse_frequency);
        mFrequency = findViewById(R.id.pulse_frequency);
        mConnect = findViewById(R.id.connect);
        mSendSizeET = findViewById(R.id.send_size);
        mSetSize = findViewById(R.id.set_size);
        mMenualPulse = findViewById(R.id.manual_pulse);
        mLiveBGSwitch = findViewById(R.id.is_live_in_bg);
        mReconnectSwitch = findViewById(R.id.switch_reconnect);
    }

    private void initData() {
        LinearLayoutManager manager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSendList.setLayoutManager(manager1);
        mSendList.setAdapter(mSendLogAdapter);

        LinearLayoutManager manager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReceList.setLayoutManager(manager2);
        mReceList.setAdapter(mReceLogAdapter);

        mInfo = new ConnectionInfo("104.238.184.237", 8080);
        mManager = open(mInfo).option(OkSocketOptions.getDefault());
    }

    private void setListener() {
        mManager.registerReceiver(adapter);

        mLiveBGSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mManager != null && !mManager.isConnect()) {
                    buttonView.setChecked(!isChecked);
                    return;
                }
                int value = -1;
                if (isChecked) {
                    value = OkSocketOptions.getDefault().getBackgroundLiveMinute();
                } else {
                    value = -1;
                }
                OkSocketOptions okSocketOptions = new OkSocketOptions.Builder(mManager.getOption())
                        .setBackgroundLiveMinute(value)
                        .build();
                mManager.option(okSocketOptions);
            }
        });

        mReconnectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mManager != null && !mManager.isConnect()) {
                    buttonView.setChecked(!isChecked);
                    return;
                }
                if (!isChecked) {
                    mManager.option(new OkSocketOptions.Builder(mManager.getOption()).setReconnectionManager(new NoneReconnect()).build());
                } else {
                    mManager.option(new OkSocketOptions.Builder(mManager.getOption()).setReconnectionManager(OkSocketOptions.getDefault().getReconnectionManager()).build());
                }
            }
        });
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
                    mManager.disconnect();
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
                    OkSocketOptions okOptions = new OkSocketOptions.Builder(mManager.getOption())
                            .setWritePackageBytes(size)
                            .build();
                    mManager.option(okOptions);
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
                String frequencyStr = mFrequency.getText().toString();
                long frequency = 0;
                try {
                    frequency = Long.parseLong(frequencyStr);
                    OkSocketOptions okOptions = new OkSocketOptions.Builder(mManager.getOption())
                            .setPulseFrequency(frequency)
                            .build();
                    mManager.option(okOptions);
                } catch (NumberFormatException e) {
                }
            }
        });
        mMenualPulse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManager.getPulseManager().trigger();
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
            mManager.disconnect();
            mManager.unRegisterReceiver(adapter);
        }
    }

}

