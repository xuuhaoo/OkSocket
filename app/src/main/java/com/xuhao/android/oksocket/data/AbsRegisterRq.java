package com.xuhao.android.oksocket.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.content.Context;

/**
 * Created by xuhao on 2017/6/7.
 */
public abstract class AbsRegisterRq extends AbsRequest {
    /**
     * 是否是进行注册,true注册,false反注册
     */
    protected final boolean isRegister;

    public AbsRegisterRq(Context context, boolean isRegister) {
        super(context);
        this.isRegister = isRegister;
    }

    @Override
    protected int getCmd() {
        if (isRegister) {
            return 17;
        } else {
            return 18;
        }
    }

    @Override
    protected JsonElement getDataBody() {
        JsonObject jsonObject = getParams();
        if (jsonObject == null) {
            jsonObject = new JsonObject();
        }
        jsonObject.addProperty("module", getModule());
        return jsonObject;
    }

    public abstract int getModule();

    public abstract JsonObject getParams();

}
