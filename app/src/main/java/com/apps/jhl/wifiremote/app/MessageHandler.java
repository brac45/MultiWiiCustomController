package com.apps.jhl.wifiremote.app;

import android.os.Handler;
import android.os.Message;

import com.apps.jhl.wifiremote.communication.CommState;

/**
 * Class for handling inter-thread communication */
public class MessageHandler extends Handler {
    private MainActivity mActivity;

    public void setmActivity(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case App.MSG_STATE_CHANGE:
                mActivity.setStatusTextView(CommState.toString(msg.arg1));
                if (msg.arg1 == CommState.STATE_NONE) {
                    mActivity.setSignalTextView("0");
                }
                break;
            case App.MSG_REFRESH_UI_RAWRC_NO_AUX:
                int[] payload = (int[])msg.obj;
                mActivity.setRcValues(payload);
                break;
            case App.MSG_SIGSTREN_FROM_COMMS:
                String temp = " " + msg.arg1;
                mActivity.setSignalTextView(temp);
                break;
            default:
                break;
        }
    }
}
