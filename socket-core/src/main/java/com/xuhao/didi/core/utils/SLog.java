package com.xuhao.didi.core.utils;

/**
 * Created by xuhao on 2017/6/9.
 */

public class SLog {

    private static boolean isDebug;

    public static void setIsDebug(boolean isDebug) {
        SLog.isDebug = isDebug;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static void e(String msg) {
        if (isDebug) {
            System.err.println("OkSocket, " + msg);
        }
    }

    public static void i(String msg) {
        if (isDebug) {
            System.out.println("OkSocket, " + msg);
        }
    }

    public static void w(String msg) {
        i(msg);
    }
}
