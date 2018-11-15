package com.xuhao.didi.oksocket.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xuhao on 2017/5/22.
 */

public class RestartBean extends DefaultSendBean {

    public RestartBean() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", 911);
            jsonObject.put("port", 8080);
            content = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
