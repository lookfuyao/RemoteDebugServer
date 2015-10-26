package com.app2.banana;

public class BThread extends Thread {

    public static final String TAG = "BThread";
    
    public BThread() {
        Log.d(TAG, this.getClass().getSimpleName() + " class construct");
    }

    public BThread(Runnable target) {
        super(target);
        // TODO Auto-generated constructor stub
    }

    public BThread(String name) {
        super(name);
        Log.d(TAG, this.getClass().getSimpleName() + " class construct" + ", name is " + name);
    }

    public BThread(ThreadGroup group, Runnable target) {
        super(group, target);
        // TODO Auto-generated constructor stub
    }

    public BThread(ThreadGroup group, String name) {
        super(group, name);
        // TODO Auto-generated constructor stub
    }

    public BThread(Runnable target, String name) {
        super(target, name);
        // TODO Auto-generated constructor stub
    }

    public BThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
        // TODO Auto-generated constructor stub
    }

    public BThread(ThreadGroup group, Runnable target, String name,
            long stackSize) {
        super(group, target, name, stackSize);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void run() {
        Log.d(TAG, this.getName() + " run");
    }
}
