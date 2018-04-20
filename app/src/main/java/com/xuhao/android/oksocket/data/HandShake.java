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

public class HandShake implements ISendable {
    private String content = "";

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

    @Override
    public byte[] parse() {
        byte[] body = content.getBytes(Charset.defaultCharset());
        ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(body.length);
        bb.put(body);
        return bb.array();
    }
}
