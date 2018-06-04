package com.xuhao.android.oksocket.data;

import com.xuhao.android.libsocket.sdk.bean.ISendable;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * Created by xuhao on 2017/5/22.
 */

public class HandShake extends DefaultSendBean {

    public HandShake() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", 54);
            jsonObject.put("handshake", "Hello I'm a OkSocket demo");
            content = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
