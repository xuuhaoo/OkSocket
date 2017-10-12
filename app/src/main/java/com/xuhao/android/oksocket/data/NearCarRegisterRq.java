package com.xuhao.android.oksocket.data;

import com.google.gson.JsonObject;

import android.content.Context;

/**
 * Created by xuhao on 2017/6/7.
 */

public class NearCarRegisterRq extends AbsRegisterRq {

    public NearCarRegisterRq(Context context, boolean isRegister) {
        super(context, isRegister);
    }

    @Override
    public int getModule() {
        return 5001;
    }

    @Override
    public JsonObject getParams() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("lon", "116.368816");
        jsonObject.addProperty("lat", "39.918834");
        jsonObject.addProperty("groupId", "34");
        return jsonObject;
    }
}
