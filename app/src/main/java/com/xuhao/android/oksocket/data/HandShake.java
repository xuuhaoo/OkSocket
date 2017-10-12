package com.xuhao.android.oksocket.data;

import com.xuhao.android.libsocket.sdk.bean.ISendable;

import java.nio.charset.Charset;

/**
 * Created by xuhao on 2017/5/22.
 */

public class HandShake implements ISendable {
    private String content = "";

    public HandShake() {
        StringBuilder sb = new StringBuilder();
        sb.append("get /mobile?user=" + 13666098);// 用户ID
        sb.append("&hash=UDE4NTEwMjUyNzk5fDE1MDU4Nzg4NzcyMzY.");//token
        sb.append("&mid=android");// 系统型号
        sb.append("&dt=4");// 系统型号
        sb.append("&cid=Anzhi_1");// 渠道号
        sb.append("&ver=1 HTTP/1.0\n\n");// 版本号

        content = sb.toString();
    }

    @Override
    public byte[] parse() {
        return content.getBytes(Charset.defaultCharset());
    }
}
