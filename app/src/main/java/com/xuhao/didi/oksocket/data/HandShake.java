package com.xuhao.didi.oksocket.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xuhao on 2017/5/22.
 */

public class HandShake extends DefaultSendBean {

    public HandShake() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", 54);
            jsonObject.put("handshake", "admin");
            content = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
