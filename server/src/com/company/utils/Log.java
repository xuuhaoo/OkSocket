package com.company.utils;

public class Log {
    public static void i(String msg) {
        System.out.println(msg);
    }

    public static void e(String msg) {
        System.err.println(msg);
    }

    public static void bytes(String prefix, byte[] data) {
        String debugSendBytes = "";
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                String tempHexStr = Integer.toHexString(data[i] & 0xff) + " ";
                tempHexStr = tempHexStr.length() == 2 ? "0" + tempHexStr : tempHexStr;
                debugSendBytes += tempHexStr;
            }
        }
        i(prefix + debugSendBytes);
    }

}
