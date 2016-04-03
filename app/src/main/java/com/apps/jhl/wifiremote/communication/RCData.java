package com.apps.jhl.wifiremote.communication;

import android.util.Log;

import java.util.Arrays;

/**
 * Class for representing the payload of the packet
 * Synchronized data class since more than 2 threads may access it */
public class RCData {
    // Constants
    private static RCData instance = null;
    // Raw data indices (No AUX)
    private static final int ROLL = 0;
    private static final int PITCH = 1;
    private static final int YAW = 2;
    private static final int THROTTLE = 3;
    // Default packet size (constants!)
    private final int DEFAULT_SET_RC_PAYLOAD_SIZE = 5;
    private final int DEFAUL_REQ_PAYLOAD_SIZE = 0;
    private final int DEFAULT_REQUEST_PACKET_SIZE = 6;          // Header 5 bytes, checksum 1 byte
    private final int DEFAULT_SET_RC_PACKET_SIZE = DEFAULT_REQUEST_PACKET_SIZE + DEFAULT_SET_RC_PAYLOAD_SIZE;

    // Default RC min and max values (constants!)
    public static final int RC_MAX = 250;
    public static final int RC_MIN = 0;
    public static final int RC_MID = (RC_MAX - RC_MIN) / 2;
    public static final int RC_MID_THROTTLE = 80;

    // Packet indices
    private final int PACKET_PREAMBLE_DOLLAR = 0;
    private final int PACKET_PREAMBLE_M = 1;
    private final int PACKET_DIR = 2;
    private final int PACKET_DATA_SIZE = 3;
    private final int PACKET_CMD = 4;

    // Private members
    private static volatile int[] rawRcSignals = new int[5];        // Critical data
    private static byte[] msp_request_packet;               // Packet for requesting data from MWC
    private static byte[] msp_set_raw_rc_packet;       // Packet for controlling MWC

    public static RCData getInstance() {
        if (instance == null) {
            instance = new RCData();
        }
        return instance;
    }

    // Private Construtor : invoked only once
    private RCData() {
        // Reset raw data
        rawRcSignals[ROLL] = RC_MID;
        rawRcSignals[PITCH] = RC_MID;
        rawRcSignals[YAW] = RC_MID;
        rawRcSignals[THROTTLE] = RC_MID_THROTTLE;
        rawRcSignals[4] = 5;           // TODO : review code

        // Create byte data
        msp_request_packet = new byte[DEFAULT_REQUEST_PACKET_SIZE];
        msp_set_raw_rc_packet = new byte[DEFAULT_SET_RC_PACKET_SIZE];

        // Set MSP headers
        msp_set_raw_rc_packet[PACKET_PREAMBLE_DOLLAR] = Protocol.PREAMBLE_DOLLAR;
        msp_set_raw_rc_packet[PACKET_PREAMBLE_M] = Protocol.PREAMBLE_M;
        msp_set_raw_rc_packet[PACKET_DIR] = Protocol.DIR_TO_MWC;
        msp_request_packet[PACKET_PREAMBLE_DOLLAR] = Protocol.PREAMBLE_DOLLAR;
        msp_request_packet[PACKET_PREAMBLE_M] = Protocol.PREAMBLE_M;
        msp_request_packet[PACKET_DIR] = Protocol.DIR_TO_MWC;
    }

    public void setRoll(int roll) {
        rawRcSignals[ROLL] = roll;
    }
    public void setPitch(int pitch) {
        rawRcSignals[PITCH] = pitch;
    }
    public void setYaw(int yaw) {
        rawRcSignals[YAW] = yaw;
    }
    public void setThrottle(int throttle) {
        rawRcSignals[THROTTLE] = throttle;
    }

    /**
     * Sets a msp packet with the specified command
     * @param command MSP command for the packet
     * @return MSP packet to be sent. If unrecognized command is received, returns a null value */
    public byte[] getMspPacket(int command, boolean hasPayload) {
        // TODO : DEBUG
        Log.d(Transmitter.TAG, "Raw data : " + Arrays.toString(rawRcSignals));
        if (hasPayload) {
            return getMspPacketWithPayload(command);
        } else {
            return getMspPacketNoPayload(command);
        }
    }

    // ---------------------------- Private methods ------------------------------------ //
    private byte[] getMspPacketNoPayload(int command) {
        // Local variables
        byte checksum = 0;
        int index = PACKET_CMD + 1;

        // Set data size and command
        msp_request_packet[PACKET_DATA_SIZE] = DEFAUL_REQ_PAYLOAD_SIZE;     // 0 bytes
        checksum ^= msp_request_packet[PACKET_DATA_SIZE];
        msp_request_packet[PACKET_CMD] = (byte)(command & 0xff);
        checksum ^= msp_request_packet[PACKET_CMD];
        msp_request_packet[index] = checksum;

        return msp_request_packet;
    }
    private byte[] getMspPacketWithPayload(int command) {
        // Local variables
        byte checksum = 0;
        int index = PACKET_CMD + 1;

        // Set data size and command
        msp_set_raw_rc_packet[PACKET_DATA_SIZE] = DEFAULT_SET_RC_PAYLOAD_SIZE & 0xff;     // 5 bytes
        checksum ^= msp_set_raw_rc_packet[PACKET_DATA_SIZE];
        msp_set_raw_rc_packet[PACKET_CMD] = (byte)(command & 0xff);
        checksum ^= msp_set_raw_rc_packet[PACKET_CMD];

        // Set payload
        for (int i = 0; i < DEFAULT_SET_RC_PAYLOAD_SIZE; i++) {
            msp_set_raw_rc_packet[index++] = (byte)(rawRcSignals[i] & 0xff);        // Critical code
            checksum ^= (byte)(rawRcSignals[i] & 0xff);
        }
        msp_set_raw_rc_packet[index] = checksum;
        return msp_set_raw_rc_packet;
    }
}
