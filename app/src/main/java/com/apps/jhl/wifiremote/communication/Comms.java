package com.apps.jhl.wifiremote.communication;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.apps.jhl.wifiremote.app.App;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Abstract class for communications
 * Classes inherited should follow the interfaces defined here. */
public abstract class Comms {
    // TODO : DEBUG Tag
    public static final String TAG = "Comms";

    // ------------------- Members ---------------------- //
    protected Context context = null;
    protected int currentState = CommState.STATE_NONE;
    protected Handler uiHandler = null;

    // ------------------- Methods ---------------------- //
    public Comms(Context context) {
        this.context = context;
    }

    /**
     * Set handler to send messages to main ui thread
     * @param uiHandler integer constant from CommState */
    public void setHandler(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }

    /**
     * Set current state. Also sends a message with the set state
     * @param state integer constant from CommState */
    protected synchronized void setCurrentState(int state) {
        // DEBUG
        Log.d(TAG, "State changed from " + CommState.toString(currentState) + " to " + CommState.toString(state));

        currentState = state;

        if (uiHandler != null) {
            uiHandler.obtainMessage(App.MSG_STATE_CHANGE, state, App.MSG_NO_VAL).sendToTarget();
        } else {
            Log.d(Comms.TAG, "Reference to ui handler is null!!");
        }
    }

    /**
     * Send string message via handler
     * @param msg String to be sent */
    protected void sendMessageSignalStrength(int msg) {
        if (uiHandler != null) {
            uiHandler.obtainMessage(App.MSG_SIGSTREN_FROM_COMMS, msg, App.MSG_NO_VAL).sendToTarget();
        } else {
            Log.d(Comms.TAG, "Reference to ui handler is null!!");
        }
    }

    // ---------------------------------- Class interface ------------------------------- //
    /**
     * Connect to the specified address.*/
    public abstract void connect() throws SocketException, UnknownHostException;

    /**
     * Read a single byte from MWC */
    public abstract byte read();

    /**
     * Write a byte array to the connected stream
     * @param data Data packet to be sent */
    public abstract void write(byte[] data) throws IOException;

    /**
     * Close the connection */
    public abstract void close();

    /**
     * Get signal strength */
    protected abstract void readSignalStrength();
}
