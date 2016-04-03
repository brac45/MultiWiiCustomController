package com.apps.jhl.wifiremote.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.apps.jhl.wifiremote.R;
import com.apps.jhl.wifiremote.communication.RCData;
import com.apps.jhl.wifiremote.view.DualJoystickView;
import com.apps.jhl.wifiremote.view.JoystickMovedListener;

/**
 * Main UI activity for the multiwii remote */
public class MainActivity extends Activity {
    // ----------------- View objects ----------------------//
    private TextView rollTextView, pitchTextView;
    private TextView yawTextView, throttleTextView;
    private TextView statusTextView, signalTextView;
    private DualJoystickView dualJoystickView;
    private Button settingsButton, accCalBtn;
    private ToggleButton armToggleBtn;

    // ------------------ Constants -------------------------//
    // TODO : Settings activity constants
    public static final int DEFAULT_SETTINGS_CODE = 0;

    // ------------------ Members ---------------------------//
    private int roll = RCData.RC_MID;
    private int pitch = RCData.RC_MID;
    private int yaw = RCData.RC_MID;
    private int throttle = RCData.RC_MID_THROTTLE;
    private static final float YAW_SENSITIVITY = 0.3f;
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dualjoystick);
        app = (App)this.getApplication();

        // Get view ids
        rollTextView = (TextView)findViewById(R.id.rollTextView);
        pitchTextView = (TextView)findViewById(R.id.pitchTextView);
        yawTextView = (TextView)findViewById(R.id.yawTextView);
        throttleTextView = (TextView)findViewById(R.id.throttleTextView);
        dualJoystickView = (DualJoystickView)findViewById(R.id.dualjoystickView);
        armToggleBtn = (ToggleButton)findViewById(R.id.armTglBtn);
        settingsButton = (Button)findViewById(R.id.settingsBtn);
        accCalBtn = (Button)findViewById(R.id.calibrateAccBtn);
        statusTextView = (TextView)findViewById(R.id.statusTextView);
        signalTextView = (TextView)findViewById(R.id.signalTextView);

        // Set auto-return false for throttle
        dualJoystickView.setAutoReturnToCenter(true, false);

        // Set thread comm handlers
        app.handler.setmActivity(this);

        // Init views
        rollTextView.setText("" + roll);
        pitchTextView.setText("" + pitch);
        yawTextView.setText("" + yaw);
        throttleTextView.setText("" + throttle);
        dualJoystickView.setMovementRange(40, RCData.RC_MID_THROTTLE);
        armToggleBtn.setTextOn("ARMED");
        armToggleBtn.setTextOff("ARM");
        armToggleBtn.setChecked(false);

        // Set listeners
        dualJoystickView.setOnJostickMovedListener(new LeftJoyStickListener(), new RightJoyStickListener());
        armToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!app.isTransmitting()) {
                    Toast.makeText(getApplicationContext(), "Connect First!", Toast.LENGTH_LONG).show();
                    armToggleBtn.setChecked(!isChecked);
                    return;
                }
                if (isChecked) {
                    app.armCopter();
                } else {
                    app.disarmCopter();
                }
            }
        });
        accCalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.accCalCopter();
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettings();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Let worker threads run to completion
        app.killConnection();
    }

    // Used in UI handler for info updates
    public void setStatusTextView(String text) {
        statusTextView.setText(text);
    }
    public void setSignalTextView(String text) {
        signalTextView.setText(text);
    }
    public void setRcValues(int[] data) {
        rollTextView.setText("" + data[0]);
        pitchTextView.setText("" + data[1]);
        yawTextView.setText("" + data[2]);
        throttleTextView.setText("" + data[3]);
    }

    // ------------------ Private Methods -------------------------//
    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, DEFAULT_SETTINGS_CODE);
    }

    // ------------------ Private Classes -------------------------//
    // Listeners
    private class LeftJoyStickListener implements JoystickMovedListener {
        @Override
        public void OnMoved(int pan, int tilt) {
            // Refine values
            roll = pan + RCData.RC_MID;
            pitch = (-tilt) + RCData.RC_MID;

            // Show in UI
            rollTextView.setText("" + roll);
            pitchTextView.setText("" + pitch);

            // Set raw RC values
            app.rc.setRoll(roll);
            app.rc.setPitch(pitch);
        }

        @Override
        public void OnReleased() {
        }

        @Override
        public void OnReturnedToCenter() {
        }
    }
    private class RightJoyStickListener implements JoystickMovedListener {
        @Override
        public void OnMoved(int pan, int tilt) {
            // adjust values
            yaw = (int)(pan * YAW_SENSITIVITY) + RCData.RC_MID;
            throttle = (-tilt) + RCData.RC_MID_THROTTLE;

            yawTextView.setText("" + yaw);
            throttleTextView.setText("" + throttle);

            app.rc.setYaw(yaw);
            app.rc.setThrottle(throttle);
        }

        @Override
        public void OnReleased() {
        }

        @Override
        public void OnReturnedToCenter() {
        }
    }
}
