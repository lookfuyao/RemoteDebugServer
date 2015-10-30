package com.app2.banana;

import java.util.concurrent.BlockingQueue;

import com.app2.manager.ChatConnection;
import com.app2.manager.Configuration;
import com.app2.manager.LogExt;
import com.app2.manager.Network.ChatMessage;
import com.app2.manager.ReceiveBufferExListener;
import com.esotericsoftware.kryonet.Connection;

public class Session implements ReceiveBufferExListener {

    private static final String TAG = "Session";
	private AdbProxy host = null;
    private Connection slave = null;
    private BlockingQueue<Data> requestStreamQueue = null;
    private BlockingQueue<Data> responseStreamQueue = null;

	private boolean mStop = false;
	private Thread mRespnseThread = null;

	public Session(AdbProxy host, Connection connection,
			BlockingQueue<Data> requestStreamQueue,
			BlockingQueue<Data> responseStreamQueue) {
		this.host = host;
		this.slave = connection;
		this.requestStreamQueue = requestStreamQueue;
		this.responseStreamQueue = responseStreamQueue;
	}

	public void start() {
		Log.d(TAG, "session start");
		((ChatConnection) slave).setBufferExListener(this);
		ChatMessage cmd = new ChatMessage();
		String pc_Ip = host.getSocket().getInetAddress().toString();
		cmd.text = Configuration.CMD_CONNECT_ADB + pc_Ip;
		((ChatConnection) slave).sendTCP(cmd);
	}

	public void setAdbConnectedStatus(boolean connect) {
		Log.d(TAG, "setAdbConnectedStatus connect = " + connect);
		if (connect) {
			mRespnseThread = new Thread(mSendRunnable, "response thread");
			mRespnseThread.start();
		}
	}

    public void destory() {
        mStop = true;
    }

    @Override
    public void onReceive(Data data) {
        try {
            responseStreamQueue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    Runnable mSendRunnable = new Runnable() {
        @Override
        public void run() {
            Data tempData;
            Log.d(TAG,"session mSendRunnable start queue size is " + requestStreamQueue.size());
            try {
                while (!mStop) {
                    tempData = requestStreamQueue.take();
                    slave.sendTCP(tempData);
                    Log.d("send " + tempData.getString() + " to phone porxy client");
                }
            } catch (InterruptedException e) {
                LogExt.e(TAG,"mSendRunnable exception " + e );
            }
        }
    };
}
