package com.app2.manager;

import java.util.Calendar;

public class LogExt {
    public static void e(String tag, String msg) {
        System.out.println(tag + " " + format2TimeString(System.currentTimeMillis()) + " " + getSubTag() + " " + msg);
    }

    public static void d(String tag, String msg) {
        System.out.println(tag + " " + format2TimeString(System.currentTimeMillis()) + " " + getSubTag() + " " + msg);
    }

    public static void e(String tag, String msg, Exception e) {
        System.out.println(tag + " " + format2TimeString(System.currentTimeMillis()) + " " + getSubTag() + " " + msg + " " + e.getMessage());
    }

    public static String getSubTag() {
        return getSubTag("");
    }

    public static String getSubTag(String tag) {
        return "[" + Thread.currentThread().getId() + "-" + Thread.currentThread().getName() + "]";
    }

    public static String bytesToHexString(byte src) {
        byte[] bt = new byte[1];
        bt[0] = src;
        return bytesToHexString(bt);
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            if (i == (src.length - 1)) {
                stringBuilder.append(hv);
            } else {
                stringBuilder.append(hv).append("-");
            }
        }
        return stringBuilder.toString();
    }

    public static String format2TimeString(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY)
                + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "." + calendar.get(Calendar.MILLISECOND);
    }


    // a integer to xx:xx:xx
    public static String secToTime(long time) {
        String timeStr = null;
        long hour = 0;
        long minute = 0;
        long second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60l;
            if (minute < 60l) {
                second = time % 60l;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60l;
                if (hour > 99l)
                    return "99:59:59";
                minute = minute % 60l;
                second = time - hour * 3600l - minute * 60l;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(long i) {
        String retStr = null;
        if (i >= 0l && i < 10l)
            retStr = "0" + Long.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

}
