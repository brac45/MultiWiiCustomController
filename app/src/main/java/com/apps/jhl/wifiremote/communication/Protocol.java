package com.apps.jhl.wifiremote.communication;

/**
 * Contains protocol constants */
public class Protocol {
    // -------------- MSP header ---------------- //
    public static final byte PREAMBLE_DOLLAR = 0x24;
    public static final byte PREAMBLE_M = 0x4d;
    public static final byte DIR_TO_MWC = 0x3c;

    // -------------- MSP commands for my custom quad  -------------- //
    public static final int  MSP_ACC_CALIBRATION    = 205;
    public static final int  MSP_SET_RAW_RC_SERIAL 	= 150;
    public static final int  MSP_ARM 	        	= 151;
    public static final int  MSP_DISARM 		    = 152;
    public static final int  MSP_TRIM_UP		    = 153;
    public static final int  MSP_TRIM_DOWN		    = 154;
    public static final int  MSP_TRIM_LEFT		    = 155;
    public static final int  MSP_TRIM_RIGHT		    = 156;
}
