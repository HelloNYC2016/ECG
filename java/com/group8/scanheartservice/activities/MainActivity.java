package com.group8.scanheartservice.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.group8.scanheartservice.R;

public class MainActivity extends Activity {

    ImageButton bt_ecg, bt_history, bt_userinfo, bt_bluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        bt_ecg = (ImageButton) findViewById(R.id.bt_ecg);
        bt_history = (ImageButton) findViewById(R.id.bt_history);
        bt_userinfo = (ImageButton) findViewById(R.id.bt_userinfo);
        bt_bluetooth = (ImageButton) findViewById(R.id.bt_bluetooth);
    }

    public void onDrawEcg(View v) {
        Intent in = new Intent(MainActivity.this, DeviceControlActivity.class);

        startActivity(in);
    }

    public void onHistory(View v) {
        Intent in = new Intent(MainActivity.this, HistoryDataActivity.class);

        startActivity(in);
    }

    public void onUserInfo(View v) {
        Intent in = new Intent(MainActivity.this, UserInfoActivity.class);

        startActivity(in);
    }

    public void onBtSetting(View v) {
        Intent in = new Intent(MainActivity.this, DeviceScanActivity.class);

        startActivity(in);
    }
}
