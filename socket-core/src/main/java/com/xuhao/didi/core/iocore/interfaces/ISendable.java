package com.xuhao.didi.core.iocore.interfaces;

import java.io.Serializable;

/**
 * 可发送类,继承该类,并实现parse方法即可获得发送能力
 * Created by xuhao on 2017/5/16.
 */
public interface ISendable extends Serializable {
    /**
     * 数据转化
     *
     * @return 将要发送的数据的字节数组
     */
    byte[] parse();
}
