package com.xuhao.android.oksocket;

import com.google.gson.Gson;
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
import com.xuhao.android.libsocket.sdk.OkSocket;
import com.xuhao.android.libsocket.sdk.client.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.client.SocketActionAdapter;
import com.xuhao.android.libsocket.sdk.client.bean.IPulseSendable;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.bean.OriginalData;
import com.xuhao.android.libsocket.sdk.client.connection.IConnectionManager;
import com.xuhao.android.libsocket.sdk.client.connection.NoneReconnect;
import com.xuhao.android.oksocket.adapter.LogAdapter;
import com.xuhao.android.oksocket.data.DefaultSendBean;
import com.xuhao.android.oksocket.data.HandShake;
import com.xuhao.android.oksocket.data.LogBean;
import com.xuhao.android.oksocket.data.PulseBean;

import java.nio.charset.Charset;
import java.util.Arrays;

import static android.widget.Toast.LENGTH_SHORT;
import static com.xuhao.android.libsocket.sdk.OkSocket.open;

public class ComplexDemoActivity extends AppCompatActivity {

    private ConnectionInfo mInfo;

    private Button mConnect;
    private IConnectionManager mManager;
    private EditText mIPET;
    private EditText mPortET;
    private Button mRedirect;
    private EditText mFrequencyET;
    private Button mLiveBgBtn;
    private EditText mLiveBgET;
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
            logRece("连接成功");
            mManager.send(new HandShake());
            mConnect.setText("DisConnect");
            initSwitch();
        }

        private void initSwitch() {
            OkSocketOptions okSocketOptions = mManager.getOption();
            long minute = OkSocket.getBackgroundSurvivalTime();
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
            JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
            int cmd = jsonObject.get("cmd").getAsInt();
            if (cmd == 54) {//登陆成功
                String handshake = jsonObject.get("handshake").getAsString();
                logRece("握手成功! 握手信息:" + handshake + ". 开始心跳..");
                mManager.getPulseManager().setPulseSendable(new PulseBean()).pulse();
            } else if (cmd == 57) {//切换,重定向.(暂时无法演示,如有疑问请咨询github)
                String ip = jsonObject.get("data").getAsString().split(":")[0];
                int port = Integer.parseInt(jsonObject.get("data").getAsString().split(":")[1]);
                ConnectionInfo redirectInfo = new ConnectionInfo(ip, port);
                redirectInfo.setBackupInfo(mInfo.getBackupInfo());
                mManager.getReconnectionManager().addIgnoreException(RedirectException.class);
                mManager.disconnect(new RedirectException(redirectInfo));
            } else if (cmd == 14) {//心跳
                logRece("收到心跳,喂狗成功");
                mManager.getPulseManager().feed();
            } else {
                logRece(str);
            }
        }

        @Override
        public void onSocketWriteResponse(Context context, ConnectionInfo info, String action, ISendable data) {
            super.onSocketWriteResponse(context, info, action, data);
            byte[] bytes = data.parse();
            bytes = Arrays.copyOfRange(bytes, 4, bytes.length);
            String str = new String(bytes, Charset.forName("utf-8"));
            JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
            int cmd = jsonObject.get("cmd").getAsInt();
            switch (cmd) {
                case 54: {
                    String handshake = jsonObject.get("handshake").getAsString();
                    logSend("发送握手数据:" + handshake);
                    break;
                }
                default:
                    logSend(str);
            }
        }

        @Override
        public void onPulseSend(Context context, ConnectionInfo info, IPulseSendable data) {
            super.onPulseSend(context, info, data);
            byte[] bytes = data.parse();
            bytes = Arrays.copyOfRange(bytes, 4, bytes.length);
            String str = new String(bytes, Charset.forName("utf-8"));
            JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
            int cmd = jsonObject.get("cmd").getAsInt();
            if (cmd == 14) {
                logSend("发送心跳包");
            }
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
        mFrequencyET = findViewById(R.id.pulse_frequency);
        mConnect = findViewById(R.id.connect);
        mIPET = findViewById(R.id.ip);
        mPortET = findViewById(R.id.port);
        mRedirect = findViewById(R.id.redirect);
        mLiveBgET = findViewById(R.id.bg_live_minute);
        mLiveBgBtn = findViewById(R.id.bg_live_minute_btn);
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
        OkSocketOptions.Builder builder = new OkSocketOptions.Builder();
        mManager = open(mInfo).option(builder.build());
    }

    private void setListener() {
        mManager.registerReceiver(adapter);

        mLiveBGSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked() == isChecked) {
                    return;
                }
                long value = -1;
                if (isChecked) {
                    value = OkSocket.getBackgroundSurvivalTime();
                } else {
                    value = -1;
                }
                OkSocket.setBackgroundSurvivalTime(value);
                mLiveBgET.setText("");
                mLiveBgET.setHint(value + "");
            }
        });

        mReconnectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mManager != null && !mManager.isConnect()) {
                    buttonView.setChecked(!isChecked);
                    return;
                }
                if (buttonView.isChecked() == isChecked) {
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

        mLiveBgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStr = mLiveBgET.getText().toString();
                long time = 0;
                try {
                    time = Long.parseLong(timeStr);
                    OkSocket.setBackgroundSurvivalTime(time);
                } catch (Exception e) {
                }
            }
        });

        mRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mManager == null) {
                    return;
                }
                String ip = mIPET.getText().toString();
                String portStr = mPortET.getText().toString();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("cmd", 57);
                jsonObject.addProperty("data", ip + ":" + portStr);
                DefaultSendBean bean = new DefaultSendBean();
                bean.setContent(new Gson().toJson(jsonObject));
                mManager.send(bean);
            }
        });

        mSetFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mManager == null) {
                    return;
                }
                String frequencyStr = mFrequencyET.getText().toString();
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
                if (mManager == null) {
                    return;
                }
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

