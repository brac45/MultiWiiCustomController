package com.apps.jhl.wifiremote.app;

import android.app.Application;

import com.apps.jhl.wifiremote.communication.Transmitter;
import com.apps.jhl.wifiremote.communication.RCData;
import com.apps.jhl.wifiremote.communication.WifiComms;

/**
 * Application class for the android controller.
 * Should contain constants shared by the application */
public class App extends Application {
    // ----------------- Application Constants -------------------//
    //public static final String IP_ADDR = "192.168.4.1";
    public static final byte[] IP_ADDR = {
            (byte)(192 & 0xFF),
            (byte)(168 & 0xFF),
            (byte)(4 & 0xFF),
            (byte)(1 & 0xFF)
    };
    public static final int PORT = 5000;

    // ----------------- Handler message constants ---------------//
    public static final int MSG_NO_VAL = -1;
    public static final int MSG_STATE_CHANGE = 11;
    public static final int MSG_REFRESH_UI_RAWRC_NO_AUX = 12;
    public static final int MSG_SIGSTREN_FROM_COMMS = 13;

    // ----------------- Application Objects ---------------------//
    private WifiComms wifiComms;
    public MessageHandler handler;
    public RCData rc;
    private Transmitter connection;       // Connection

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new MessageHandler();
        wifiComms = new WifiComms(getApplicationContext());
        wifiComms.setHandler(handler);
        rc = RCData.getInstance();
        connection = new Transmitter(this, wifiComms);
    }

    // ----------------- Main Activity interface methods -------------- //
    public void armCopter() {
        if (connection.isAlive()) {
            connection.armCopter();
        }
    }

    public void accCalCopter() {
        if (connection.isAlive()) {
            connection.calibrateAcc();
        }
    }

    public void disarmCopter() {
        if (connection.isAlive()) {
            connection.disarmCopter();
        }
    }

    // ----------------- Settings Activity interface methods -------------- //
    public void killConnection() {
        if (connection.isAlive()) {
            connection.destroyConnection();
        }
    }

    public void tryConnection() {
        if (!connection.isAlive()) {
            connection.start();
        }
    }

    public void trimUpCopter() {
        if (connection.isAlive()) {
            connection.trimUp();
        }
    }
    public void trimDownCopter() {
        if (connection.isAlive()) {
            connection.trimDown();
        }
    }
    public void trimLeftCopter() {
        if (connection.isAlive()) {
            connection.trimLeft();
        }
    }
    public void trimRightCopter() {
        if (connection.isAlive()) {
            connection.trimRight();
        }
    }

    // ----------------- Common interface methods -------------- //
    public boolean isTransmitting() {
        return connection.isAlive();
    }
}
