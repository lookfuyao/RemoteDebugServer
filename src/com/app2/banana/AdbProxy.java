package com.app2.banana;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class AdbProxy extends BThread {

    public static final String TAG = "SocketThread";
    public static final String EOS = "OVEROVEROVER"; //end of socket stream

    private Socket mSocket = null;
    private InputStream mInputStream = null;
    private OutputStream mOutputStream = null;

    private BlockingQueue<Data> readQueue = null;
    private BlockingQueue<Data> writeQueue = null;

    private Thread outputStreamThread = null;
    private Thread inputStreamThread = null;

    private Runnable outputStreamRunnable = new Runnable() {
        public void run() {

            String parentName = AdbProxy.this.getName();
            Log.d(TAG + "::" + parentName, "outputStreamRunnable start");

            OutputStream out = getSocketOutputStream();

            while (true) {
                try {
                    Data data = readQueue.take();

                    if (EOS.equalsIgnoreCase(data.getString())) {
                        Log.d(TAG + "::" + parentName, "Take queue data : EOS");
                        break;
                    }

                    Log.d(TAG + "::" + parentName, "Take queue data : " + data.getString());

                    out.write(data.getBytes());
                    Log.d(TAG + "::" + parentName, "send bytes length : " + data.getBytes().length);
                    
                } catch (InterruptedException e) {
                    Log.d(TAG + "::" + parentName, "outputStreamRunnable InterruptedException");
                    e.printStackTrace();
                    break;
                } catch (IOException e) {
                    Log.d(TAG + "::" + parentName, "outputStreamRunnable error");
                    e.printStackTrace();
                }
            }

            try {
                getSocket().shutdownOutput();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG + "::" + parentName, "outputStreamRunnable over");
        }
    };

    private Runnable inputStreamRunnable = new Runnable() {
        public void run() {

            String parentName = AdbProxy.this.getName();
            Log.d(TAG + "::" + parentName, "inputStreamRunnable start");

            InputStream in = getSocketInputStream();
            int bytesRead = 0;
            byte[] buffer = new byte[1024*3];

            try {
                while ((bytesRead = in.read(buffer, 0, buffer.length)) != -1) {
                    Log.d(TAG + "::" + parentName, "receive bytes length : " + bytesRead);

                    String readString = new String(buffer, 0, bytesRead);
                    Log.d(TAG + "::" + parentName, "Put data into queue : " + readString +
                            ", byte[] to string, string length is " + readString.length());

                    Data data = new Data(buffer, 0 , bytesRead);
                    writeQueue.put(data);
                }
            } catch (IOException e) {
                Log.d(TAG + "::" + parentName, "inputStreamRunnable error");
                e.printStackTrace();
            } catch (InterruptedException e) {
                Log.d(TAG + "::" + parentName, "inputStreamRunnable InterruptedException");
                e.printStackTrace();
            }

            try {
                getSocket().shutdownInput();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Log.d(TAG + "::" + parentName, "bytesRead is " + bytesRead);
                Data data = new Data(EOS);
                writeQueue.put(data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d(TAG + "::" + parentName, "inputStreamRunnable over");
        }
    };

    public AdbProxy(String name, Socket socket, BlockingQueue<Data> readQueue, BlockingQueue<Data> writeQueue) {
        super(name);
        mSocket = socket;
        this.readQueue = readQueue;
        this.writeQueue = writeQueue;
    }

    @Override
    public void run() {
        super.run();

        if (mSocket == null) {
            return;
        }

        //mInputStream = getSocketInputStream();
        //mOutputStream = getSocketOutputStream();

        outputStreamThread = new Thread(outputStreamRunnable);
        outputStreamThread.start();
        inputStreamThread = new Thread(inputStreamRunnable);
        inputStreamThread.start();
    }

    public Socket getSocket() {
        return mSocket;
    }

    public InputStream getSocketInputStream() {
        if (mSocket == null) {
            return null;
        } else {
            if (mInputStream == null) {
                try {
                    mInputStream = mSocket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return mInputStream;
        }
    }

    public OutputStream getSocketOutputStream() {
        if (mSocket == null) {
            return null;
        } else {
            if (mOutputStream == null) {
                try {
                    mOutputStream = mSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return mOutputStream;
        }
    }

    private void finalDestroy() {
        Log.d(TAG, "finalDestroy run");

        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
            mInputStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (mOutputStream != null) {
                mOutputStream.close();
            }
            mOutputStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (mSocket != null) {
                mSocket.close();
            }
            mSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
