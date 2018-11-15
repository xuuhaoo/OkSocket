package com.xuhao.didi.oksocket.data;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminHandShakeBean extends DefaultSendBean {


    public AdminHandShakeBean(String pass) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", 54);
            jsonObject.put("handshake", pass);
            content = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
