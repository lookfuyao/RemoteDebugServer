package com.app2.manager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.app2.banana.Data;
import com.app2.manager.Network.ChatMessage;
import com.app2.manager.Network.RegisterName;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class KryoNetServer extends Thread {
	private static final String TAG = "fuyao-ProxyServerThread";

	private boolean mStop = false;
	private Server mServer;

	public List<Connection> getConnections() {
		return mConnections;
	}

	@Override
	public void run() {

		mServer = new Server() {
			protected Connection newConnection() {
				// By providing our own connection implementation, we can store
				// per
				// connection state without a connection ID to state look up.
				return new ChatConnection();
			}
		};
		Network.register(mServer);
		mServer.addListener(new Listener() {
			public void received(Connection c, Object object) {

				// LogExt.d(TAG, "received " + c + " object " + object);
				// We know all connections for this mTermServer are actually
				// ChatConnections.
				ChatConnection connection = (ChatConnection) c;

				if (object instanceof RegisterName) {
					// Ignore the object if a client has already registered a
					// name. This is
					// impossible with our client, but a hacker could send
					// messages at any time.
					if (connection.name != null)
						return;
					// Ignore the object if the name is invalid.
					String name = ((RegisterName) object).name;
					if (name == null)
						return;
					name = name.trim();
					LogExt.d(TAG, "received device name is " + name);
					if (name.length() == 0)
						return;
					// Store the name on the connection.
					connection.name = name;

					if (name.startsWith(Configuration.TYPE_PC_TERMINAL)) {
						connection.mType = Configuration.TYPE_INT_PC_TERMINAL;
					} else if (name.startsWith(Configuration.TYPE_PHONE_CLIENT)) {
						connection.mType = Configuration.TYPE_INT_PHONE_CLIENT;
					}

					InetSocketAddress remoteSocketAddress = (InetSocketAddress) connection
							.getRemoteAddressTCP();
					if (remoteSocketAddress != null) {
						connection.mIp = remoteSocketAddress.getAddress()
								.toString();
					}

					int size = mConnections.size();
					ChatConnection temp = null;
					boolean has = false;
					for (int i = 0; i < size; i++) {
						temp = (ChatConnection) mConnections.get(i);
						LogExt.d(TAG, "temp.name " + temp.name);
						LogExt.d(TAG, "connection.name " + connection.name);
						if (temp.name.equals(connection.name)) {
							has = true;
							break;
						}
					}
					if (!has) {
						LogExt.d(TAG, "add device " + connection);
						mConnections.add(connection);
					}
					return;
				}

				if (object instanceof ChatMessage) {
					// Ignore the object if a client tries to chat before
					// registering a name.
					if (connection.name == null)
						return;
					ChatMessage chatMessage = (ChatMessage) object;
					// Ignore the object if the chat message is invalid.
					String message = chatMessage.text;
					if (message == null)
						return;
					message = message.trim();
					if (message.length() == 0)
						return;

					LogExt.d(TAG, "received msg name is " + message);
					if (message.startsWith(Configuration.CMD_LIST_DEVICES)) {
						String[] devices = getAllPhoneDevices();

						String devs = "";
						for (int i = 0; i < devices.length - 1; i++) {
							devs += devices[i] + Configuration.SEG;
						}
						if (devices.length - 1 >= 0) {
							devs += devices[devices.length - 1];
						}
						ChatMessage msg = new ChatMessage();
						msg.text = Configuration.CMD_RETRUN_LIST_DEVICES + devs;
						mServer.sendToTCP(c.getID(), msg);
					} else if (message.startsWith(Configuration.CMD_SET_DEVICE)) {
						String dstDev = message
								.substring(Configuration.CMD_SET_DEVICE
										.length());
						connection.mDestDevice = dstDev;
						
						int size = mConnections.size();
						ChatConnection temp = null;
						boolean has = false;
						for (int i = 0; i < size; i++) {
							temp = (ChatConnection) mConnections.get(i);
							LogExt.d(TAG, "temp.name " + temp.name);
							LogExt.d(TAG, "connection.name " + connection.name);
							if (temp.name.equals(connection.name)) {
								has = true;
								temp.mDestDevice = dstDev;
								break;
							}
						}
						if (!has) {
							//mConnections.add(connection);
							LogExt.e(TAG, "Error connection not register " + connection);
						}
						
						LogExt.d(TAG, "connection.mDestDevice "
								+ connection.mDestDevice);
						ChatMessage msg = new ChatMessage();
						msg.text = Configuration.CMD_RETRUN_SET_DEVICE;
						mServer.sendToTCP(c.getID(), msg);
					} else if (message.startsWith(Configuration.CMD_GET_DEVICE)) {
						LogExt.d(TAG, "connection get device " + connection);
						ChatMessage msg = new ChatMessage();
						msg.text = Configuration.CMD_RETRUN_GET_DEVICE
								+ getDestDevice(connection);
						mServer.sendToTCP(c.getID(), msg);
					} else if (message
							.startsWith(Configuration.CMD_RETRUN_CONNECT_ADB)) {
						String result = message
								.substring(Configuration.CMD_RETRUN_CONNECT_ADB
										.length());
						int ret = -1;
						try {
							ret = Integer.valueOf(result);
						} catch (NumberFormatException e) {
							ret = -1;
						}
						
						if (null == result || -1 == ret) {
							return;
						}
						if (ret == Configuration.FLAG_START_ADB_SUCCESS) {
							connection.mSession.setAdbConnectedStatus(true);
						} else if (ret == Configuration.FLAG_START_ADB_FAIL) {
							connection.mSession.setAdbConnectedStatus(false);
						} else {
							LogExt.e(TAG, "msg proctol error " + message);
						}
					}
					return;
				}

				if (object instanceof Data) {
					LogExt.d(TAG, "received Data Object " + object);
					((ChatConnection) c)
							.handlerReceiveBufferEx(((Data) object));
				}
			}

			public void disconnected(Connection c) {
				ChatConnection connection = (ChatConnection) c;
				if (connection.name != null) {
					// Announce to everyone that someone (with a registered
					// name) has left.
					ChatMessage chatMessage = new ChatMessage();
					chatMessage.text = connection.name + " disconnected.";
					// mServer.sendToAllTCP(chatMessage);
				}
			}
		});
		try {
			mServer.bind(Configuration.MANAGER_SERVER_PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mServer.start();
	}

	private List<Connection> mConnections = new ArrayList<Connection>();

	public String[] getAllPhoneDevices() {
		// Collect the names for each connection.
		List<Connection> connections = getConnections();// mServer.getConnections();
		int size = connections.size();
		ArrayList names = new ArrayList(size);
		for (int i = size - 1; i >= 0; i--) {
			ChatConnection connection = (ChatConnection) connections.get(i);
			if (connection.mType == Configuration.TYPE_INT_PHONE_CLIENT) {
				names.add(connection.name);
			}
		}
		return (String[]) names.toArray(new String[names.size()]);
	}

	public List<Connection> getConnectedDevicesByType(int type) {
		Connection[] connections = mServer.getConnections();
		List<Connection> devices = new ArrayList<Connection>(connections.length);
		for (int i = connections.length - 1; i >= 0; i--) {
			ChatConnection connection = (ChatConnection) connections[i];
			if ((connection.mType & type) > 0) {
				devices.add(connection);
			}
		}
		return devices;
	}

	public String getDestDevice(ChatConnection c) {

		if (null == c) {
			return null;
		}

		InetSocketAddress remoteSocketAddress = (InetSocketAddress) c
				.getRemoteAddressTCP();
		if (remoteSocketAddress != null) {
			c.mIp = remoteSocketAddress.getAddress().toString();
		}

		if (null == c.mIp || c.mIp.equals("")) {
			return null;
		}

		List<Connection> connections = getConnections();
		int size = connections.size();

		for (int i = size - 1; i >= 0; i--) {
			ChatConnection connection = (ChatConnection) connections.get(i);
			if (null != connection.mIp && connection.mIp.equals(c.mIp)) {
				return connection.mDestDevice;
			}
		}
		return null;
	}
}
