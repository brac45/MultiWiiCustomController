package com.apps.jhl.wifiremote.communication;

/**
 * Class for representing the communication state */
public class CommState {
    // ------------------- Constants -------------------- //
    // Indicates the current connection state
    public static final int STATE_NONE = 0;             // Idle state
    public static final int STATE_CONNECTING = 2;       // Initiate outgoing comms
    public static final int STATE_CONNECTED = 3;        // Connection successful and established

    public static String toString(int state) {
        switch (state) {
            case STATE_NONE:
                return "No Connection";
            case STATE_CONNECTING:
                return "Connecting";
            case STATE_CONNECTED:
                return "Connected";
            default:
                return "Unrecognized state";
        }
    }
}
