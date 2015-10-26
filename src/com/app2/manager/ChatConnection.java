package com.app2.manager;

import com.app2.banana.Data;
import com.esotericsoftware.kryonet.Connection;

// This holds per connection state.
public class ChatConnection extends Connection {
    public String name;

    public int mType = -1;
    
    public String mIp = null;
    
    //for pc adb set mark dest device
    public String mDestDevice = null;
    
    private ReceiveBufferExListener mReceiveBufferExListener = null;

    public void setBufferExListener(ReceiveBufferExListener l) {
        mReceiveBufferExListener = l;
    }

    public void handlerReceiveBufferEx(Data bufferEx) {
        if (null != mReceiveBufferExListener) {
            mReceiveBufferExListener.onReceive(bufferEx);
        }
    }

	@Override
	public String toString() {
		return "id " + getID() + " mIp " + mIp + " name " + name + " type " + mType + " mDestDevice " + mDestDevice;
	}
    
    
}
