package com.xuhao.didi.socket.common.interfaces.utils;

public class ThreadUtils {

    public static void sleep(long mills) {
        long weakTime = 0;
        long startTime = 0;
        while (true) {
            try {
                if (weakTime - startTime < mills) {
                    mills = mills - (weakTime - startTime);
                } else {
                    break;
                }
                startTime = System.currentTimeMillis();
                Thread.sleep(mills);
                weakTime = System.currentTimeMillis();
            } catch (InterruptedException e) {
                weakTime = System.currentTimeMillis();
            }
        }
    }
}
