package com.apps.jhl.wifiremote.communication;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.apps.jhl.wifiremote.app.App;
import com.apps.jhl.wifiremote.utils.Utilities;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Handles Connection via Wifi */
public class WifiComms extends Comms {
    private WifiManager wifiManager;
    private DatagramSocket socket;
    private DatagramPacket sender;

    public WifiComms(Context context) {
        super(context);
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void connect() throws SocketException, UnknownHostException {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        setCurrentState(CommState.STATE_CONNECTING);
        socket = new DatagramSocket();
        socket.setReuseAddress(true);

        sender = null;

        setCurrentState(CommState.STATE_CONNECTED);
    }

    @Override
    public byte read(){
        // TODO : read method
        return (byte)0xff;
    }

    @Override
    public void write(byte[] data) throws IOException{
        if (sender == null) {
            sender = new DatagramPacket(data, data.length, InetAddress.getByAddress(App.IP_ADDR), App.PORT);
        } else {
            sender.setData(data);
        }

        socket.send(sender);
        // DEBUG
        Log.d(Comms.TAG, "" + Utilities.bytesToHex(data));
    }

    @Override
    public void close(){
        setCurrentState(CommState.STATE_NONE);
        if (socket != null && socket.isConnected()) {
            socket.close();
        }
    }

    @Override
    protected void readSignalStrength() {
        try {
            int rssi = wifiManager.getConnectionInfo().getRssi();
            int level = WifiManager.calculateSignalLevel(rssi, 10);
            int retVal = (int)((level / 10.0) * 100);
            sendMessageSignalStrength(retVal);
        } catch (Exception e) {
            Log.d(Comms.TAG, "Exception raised from getSignalStrength");
            sendMessageSignalStrength(0);
        }
    }
}
