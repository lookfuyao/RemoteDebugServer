package com.app2.banana;

public class Log {

    public static final String TAG = "Banana";
    
    public static void d(String msg) {
        d(TAG, msg);
    }
    
    public static void d(String tag, String msg) {
        System.out.println(Util.getCurrentLogTime() + "  " + tag + " : " + msg);
    }
    
    public static void logd() {
        String fileName = Thread.currentThread().getStackTrace()[3].getFileName();
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
        String log = "[" + fileName + "]" + ", " + "[" + className + "]" + ", " + 
                "[" + methodName + "]" + ", " + "[" + lineNumber + "]";
        Log.d(TAG, log);
    }

    public static void logd(Object paramObject) {
        String fileName = Thread.currentThread().getStackTrace()[3].getFileName();
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
        String log = "[" + fileName + "]" + ", " + "[" + className + "]" + ", " + 
                "[" + methodName + "]" + ", " + "[" + lineNumber + "]";
        Log.d(TAG, log + ", " + paramObject);
    }
}
