package com.app2.banana;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.app2.manager.ChatConnection;
import com.app2.manager.Configuration;
import com.app2.manager.KryoNetServer;
import com.esotericsoftware.kryonet.Connection;

public class ServerThread extends BThread {

	public static final String TAG = "ServerThread";

	private ServerSocket mServerSocket = null;

	private List<AdbProxy> hostList = new ArrayList<AdbProxy>();
	private List<AdbProxy> slaveList = new ArrayList<AdbProxy>();
	private List<Session> sessionList = new ArrayList<Session>();

	KryoNetServer mKryoNetServer = null;

	private void startManagerServer() {
		if (null == mKryoNetServer) {
			mKryoNetServer = new KryoNetServer();
			mKryoNetServer.start();
			Log.d(TAG, "startManagerServer");
		}
	}

	private void startAdbServer() {
		try {
			mServerSocket = new ServerSocket(Configuration.ADB_SERVER_PORT);
			Log.d(TAG, "startAdbServer");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ServerThread() {
		super();
	}

	public ServerThread(String name) {
		super(name);
	}

	@Override
	public void run() {
		super.run();
		startManagerServer();

		startAdbServer();

		while (true) {
			Socket adbSocket = null;
			try {
				adbSocket = mServerSocket.accept();
				Log.d(TAG, "adbSocket is " + adbSocket.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}

			BlockingQueue<Data> responseStreamQueue = new LinkedBlockingQueue<Data>();
			BlockingQueue<Data> requestStreamQueue = new LinkedBlockingQueue<Data>();

			AdbProxy adbProxy = new AdbProxy("adbProxy", adbSocket,
					responseStreamQueue, requestStreamQueue);

			// List<Connection> connections = mKryoNetServer
			// .getDeviceByType(Configuration.TYPE_INT_PC_TERMINAL);

			List<Connection> connections = mKryoNetServer.getConnections();

			Log.d(TAG, "connections size is " + connections.size());
			int size = connections.size();
			if (size > 0) {
				// to do choice connection
				ChatConnection c = null;
				String address = null;
				boolean find = false;

				for (int i = 0; i < size; i++) {
					c = (ChatConnection) connections.get(i);
					Log.d(TAG, "connected phone adb client address is "
							+ address + " name is " + c);
					//Log.d(TAG, "connect pc adb client address is "
					//		+ adbSocket.getInetAddress().toString());
					if (null != c.mIp
							&& c.mIp.trim().contains(
									adbSocket.getInetAddress().toString())) {
						find = true;
						break;
					}
				}

				Log.d(TAG, "11 find = " + find + " connection " + c);

				if (find && null != c) {

					if (null != c.mDestDevice) {
						ChatConnection tempC = null;
						connections = mKryoNetServer
								.getConnectedDevicesByType(Configuration.TYPE_INT_PHONE_CLIENT);

						size = connections.size();
						Log.d(TAG, "22 connections size is " + size);
						find = false;
						for (int i = 0; i < size; i++) {
							tempC = (ChatConnection) connections.get(i);
							Log.d(TAG, "22 connection is " + tempC);
							Log.d(TAG, "22 mDestDevice is " + c.mDestDevice);
							if (tempC.name.equals(c.mDestDevice)) {
								find = true;
								break;
							}
						}
						Log.d(TAG, "22 find = " + find + " connection " + tempC);
						if (find && null != tempC) {
							Session session = new Session(adbProxy, tempC,
									requestStreamQueue, responseStreamQueue);
							session.start();
							
							hostList.add(adbProxy);
							adbProxy.run();
							
							sessionList.add(session);
							Log.d(TAG,
									"sessionList size is " + sessionList.size());
						} else {
							System.out.println("dst device is not online!");
						}
					} else {
						System.out.println("not set dest device error!!!");
					}
				} else {
					System.out.println("please config first!");
				}
			}

		}

		// long timeout = System.currentTimeMillis() + 1000000;
		// while (timeout > System.currentTimeMillis()) {
		// try {
		// sleep(1000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		// Log.d(TAG, "ServerThread run out");

	}
}
