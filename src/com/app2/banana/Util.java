package com.app2.banana;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public static final String TAG = "Util";
    
    /**
     * The default buffer size ({@value}) used by
     * {@link #copyStream  copyStream } and {@link #copyReader  copyReader}
     * and by the copyReader/copyStream methods if a zero or negative buffer size is supplied.
     */
    public static final int DEFAULT_COPY_BUFFER_SIZE = 1024;
    public static final int LARGE_COPY_BUFFER_SIZE = 8024;
    public static final int SEEK_TIME_OUT = 10 * 1000;
    
    public static String getCurrentTimeMillis() {
        Date mDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateTime = dateTimeFormat.format(mDate);
//        Log.d(TAG, "current date and time is " + dateTime);
        return dateTime;
    }
    
    public static String getCurrentDate() {
        Date mDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(mDate);
//        Log.d(TAG, "current date is " + date);
        return date;
    }
    
    public static String getCurrentTime() {
        Date mDate = new Date(System.currentTimeMillis());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String time = timeFormat.format(mDate);
//        Log.d(TAG, "current time is " + time);
        return time;
    }
    
    public static String getCurrentLogTime() {
        Date mDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        String dateTime = dateTimeFormat.format(mDate);
//        Log.d(TAG, "current date and time is " + dateTime);
        return dateTime;
    }
}


