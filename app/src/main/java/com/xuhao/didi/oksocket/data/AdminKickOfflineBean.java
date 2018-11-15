package com.xuhao.didi.oksocket.data;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminKickOfflineBean extends DefaultSendBean {


    public AdminKickOfflineBean(String who) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", 912);
            jsonObject.put("who", who);
            content = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
