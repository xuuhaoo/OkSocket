package com.company.bean;

import java.io.Serializable;

public class MsgBean implements Serializable {
    private byte[] bytes = new byte[0];
    /**
     * To whose IP ,if to all it will be Empty
     */
    private String toWho;
    /**
     * From who ,must not null;
     */
    private String fromWho;


    public MsgBean(String fromWho, String toWho, byte[] what) {
        this.bytes = what;
        this.toWho = toWho;
        this.fromWho = fromWho;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getToWho() {
        return toWho;
    }

    public String getFromWho() {
        return fromWho;
    }
}
