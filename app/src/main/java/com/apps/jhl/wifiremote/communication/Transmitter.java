package com.apps.jhl.wifiremote.communication;

import android.util.Log;

import com.apps.jhl.wifiremote.app.App;

import java.io.IOException;

/**
 * Background thread for connecting via MSP protocol
 * Responsibility : runs a transmitter thread to send MSP packets */
public class Transmitter {
    // DEBUG
    public static final String TAG = "Transmitter";

    // ------------- Constants -------------------//
    private final int DELAY = 30;

    // ------------- Members ---------------------//
    private App app;
    private WifiComms transmitter;

    private volatile boolean start_flag;
    private volatile boolean accCalSigSent;
    private volatile boolean armSigSent;
    private volatile boolean disarmSigSent;

    private volatile boolean trimUpSigSent;
    private volatile boolean trimDwnSigSent;
    private volatile boolean trimRgtSigSent;
    private volatile boolean trimLftSigSent;

    public Transmitter(App app, WifiComms wifiComms) {
        this.app = app;
        transmitter = wifiComms;

        start_flag = false;
        accCalSigSent = true;
        armSigSent = true;
        disarmSigSent = true;

        trimUpSigSent   = true;
        trimDwnSigSent  = true;
        trimRgtSigSent  = true;
        trimLftSigSent  = true;
    }

    public void start() {
        start_flag = true;
        Thread workerThread = new Thread(new transmitterTask());
        workerThread.start();
    }

    public boolean isAlive() {
        return start_flag;
    }

    /**
     * Runs the thread to completion */
    public void destroyConnection() {
        Log.d(Transmitter.TAG, "Destroying connection..");
        start_flag = false;
    }

    // ------------------------- Send message ------------------------------ //
    /**
     * Send calibrate acc message to MWC */
    public void calibrateAcc() {
        if (start_flag) {
            accCalSigSent = false;
        }
    }

    /**
     * Send disarm message to MWC */
    public void disarmCopter() {
        if (start_flag) {
            disarmSigSent = false;
        }
    }

    /**
     * Send arm message to MWC */
    public void armCopter() {
        if (start_flag) {
            armSigSent = false;
        }
    }

    public void trimUp() {
        if (start_flag) {
            trimUpSigSent = false;
        }
    }

    public void trimDown() {
        if (start_flag) {
            trimDwnSigSent = false;
        }
    }

    public void trimLeft() {
        if (start_flag) {
            trimLftSigSent = false;
        }
    }

    public void trimRight() {
        if (start_flag) {
            trimRgtSigSent = false;
        }
    }

    private class transmitterTask implements Runnable {
        @Override
        public void run() {
            // Attempt to connect to MWC
            try {
                transmitter.connect();
            } catch (Exception e) {
                e.printStackTrace();
                start_flag = false;
            }

            // Start transmission
            while (start_flag) {
                try {
                    transmitter.write(app.rc.getMspPacket(Protocol.MSP_SET_RAW_RC_SERIAL, true));

                    if (!accCalSigSent) {           // Send acc_cal
                        transmitter.write(app.rc.getMspPacket(Protocol.MSP_ACC_CALIBRATION, false));
                        accCalSigSent = true;
                    }
                    if (!armSigSent) {              // Send arm
                        transmitter.write(app.rc.getMspPacket(Protocol.MSP_ARM, false));
                        armSigSent = true;
                    }
                    if (!disarmSigSent) {           // Send disarm
                        transmitter.write(app.rc.getMspPacket(Protocol.MSP_DISARM, false));
                        disarmSigSent = true;
                    }
                    if (!trimUpSigSent) {
                        transmitter.write(app.rc.getMspPacket(Protocol.MSP_TRIM_UP, false));
                        trimUpSigSent = true;
                    }
                    if (!trimDwnSigSent) {
                        transmitter.write(app.rc.getMspPacket(Protocol.MSP_TRIM_DOWN, false));
                        trimDwnSigSent = true;
                    }
                    if (!trimRgtSigSent) {
                        transmitter.write(app.rc.getMspPacket(Protocol.MSP_TRIM_RIGHT, false));
                        trimRgtSigSent = true;
                    }
                    if (!trimLftSigSent) {
                        transmitter.write(app.rc.getMspPacket(Protocol.MSP_TRIM_LEFT, false));
                        trimLftSigSent = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    start_flag = false;
                }

                // Sleep for predetermined amount of time
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {      // if interrupted, run thread to completion
                    e.printStackTrace();
                    start_flag = false;
                } finally {
                    transmitter.readSignalStrength();
                }
            }

            transmitter.close();
        }
    }
}
