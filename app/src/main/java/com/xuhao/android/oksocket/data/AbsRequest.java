package com.xuhao.android.oksocket.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.content.Context;

import com.xuhao.android.libsocket.sdk.bean.ISendable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * Created by xuhao on 2017/6/2.
 */

public abstract class AbsRequest implements ISendable {

    protected Context mContext;

    protected abstract int getCmd();

    protected abstract JsonElement getDataBody();

    public AbsRequest(Context context) {
        mContext = context;
    }

    @Override
    public byte[] parse() {
        int cmd = getCmd();
        JsonObject result = new JsonObject();
        JsonElement data = getDataBody();
        try {
            if (data != null) {
                result.addProperty("cmd", cmd);
                result.add("data", data);
                byte[] body = result.toString().getBytes(Charset.defaultCharset());
                ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
                bb.order(ByteOrder.BIG_ENDIAN);
                bb.putInt(body.length);
                bb.put(body);
                return bb.array();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
