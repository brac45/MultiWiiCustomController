package com.apps.jhl.wifiremote.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.apps.jhl.wifiremote.R;

public class SettingsActivity extends AppCompatActivity {
    private Switch connectionSwitch;
    private Button trimUpBtn;
    private Button trimDwnBtn;
    private Button trimRgtBtn;
    private Button trimLftBtn;
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        app = (App)getApplication();

        connectionSwitch = (Switch)findViewById(R.id.connectionSwitch);
        trimUpBtn   = (Button)findViewById(R.id.trimeUpBtn);
        trimDwnBtn  = (Button)findViewById(R.id.trimDownBtn);
        trimRgtBtn  = (Button)findViewById(R.id.trimRgtBtn);
        trimLftBtn  = (Button)findViewById(R.id.trimLftBtn);

        connectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startTransmit();
                    connectionSwitch.setText(R.string.settings_activity_disconnect_btn_text);
                } else {
                    stopTransmit();
                    connectionSwitch.setText(R.string.settings_activity_connect_btn_text);
                }
            }
        });
        TrimButtonListener listener = new TrimButtonListener();
        trimUpBtn.setOnClickListener(listener);
        trimDwnBtn.setOnClickListener(listener);
        trimRgtBtn.setOnClickListener(listener);
        trimLftBtn.setOnClickListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (app.isTransmitting()) {
            connectionSwitch.setText(R.string.settings_activity_disconnect_btn_text);
            connectionSwitch.setChecked(true);
        } else {
            connectionSwitch.setText(R.string.settings_activity_connect_btn_text);
            connectionSwitch.setChecked(false);
        }
    }

    private void startTransmit() {
        app.tryConnection();
    }

    private void stopTransmit() {
        app.killConnection();
    }

    private class TrimButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.trimeUpBtn:
                    app.trimUpCopter();
                    break;
                case R.id.trimDownBtn:
                    app.trimDownCopter();
                    break;
                case R.id.trimRgtBtn:
                    app.trimRightCopter();
                    break;
                case R.id.trimLftBtn:
                    app.trimLeftCopter();
                    break;
            }
        }
    }
}
