package com.group8.scanheartservice.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import com.group8.scanheartservice.R;

/**
 * 功能：使用ViewPager实现初次进入应用时的引导页
 *
 * (1)判断是否是首次加载应用--采取读取SharedPreferences的方法
 * (2)是，则进入引导activity；否，则进入MainActivity
 * (3)5s后执行(2)操作
 *
 * @author sz082093
 *
 */
public class StartActivity extends Activity {
    private final static int SWITCH_LOGINACTIVITY = 1000;
    private final static int SWITCH_GUIDACTIVITY = 1001;
    private final static int SWITCH_DEVICESCANACTIVITY = 1002;
    private final static int SWITCH_MAINACTIVITY = 1003;


    private static final String SHAREDPREFERENCES_NAME = "user_state";
    private static final String KEY_GUIDE_ACTIVITY = "guide_activity";
    private static final String USER_ID = "user_id";
    private static final String BLUETOOTH_ADDRESS = "bluetooth_address";


    private boolean isThereABluetooth;
    private String bluetooth_address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start);

        boolean mFirst = isFirstEnter(StartActivity.this, StartActivity.this.getClass().getName());
        boolean mUser = isThereAUser(StartActivity.this, StartActivity.this.getClass().getName());
        bluetooth_address = getBluetoothAddress(StartActivity.this, StartActivity.this.getClass().getName());


        if (mFirst)
            mHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY, 2000);
        else {
            if (mUser) {
                if (bluetooth_address.equals("")) {
                    mHandler.sendEmptyMessageDelayed(SWITCH_DEVICESCANACTIVITY, 2000);
                } else {
                    mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY, 2000);
                }
            } else
                mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY, 2000);
        }
    }

    //****************************************************************
    // 判断应用是否初次加载，读取SharedPreferences中的guide_activity字段
    //****************************************************************
    private boolean isFirstEnter(Context context,String className){
        if(context==null || className==null||"".equalsIgnoreCase(className))return false;
        SharedPreferences mUserStatePref = context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE);
        String mResultStr = mUserStatePref.getString(KEY_GUIDE_ACTIVITY, "");//取得所有类名 如 com.my.MainActivity
        if(mResultStr.equalsIgnoreCase("false"))
            return false;
        else
            return true;
    }

    //****************************************************************
    // 判断应用是否初次加载，读取SharedPreferences中的guide_activity字段
    //****************************************************************
    private String getBluetoothAddress(Context context,String className){
        if(context==null || className==null||"".equalsIgnoreCase(className))return "";
        SharedPreferences mUserStatePref = context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE);
        String mResultStr = mUserStatePref.getString(BLUETOOTH_ADDRESS, "");//取得蓝牙地址 如 com.my.MainActivity

        return mResultStr;
    }

    //****************************************************************
    // 判断应用是否已经有用户登录，读取SharedPreferences中的user_id字段
    //****************************************************************
    private boolean isThereAUser(Context context,String className){
        if(context==null || className==null||"".equalsIgnoreCase(className)) return false;
        int userId = context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE)
                .getInt(USER_ID, 0);//取得user_id的值
        if(userId == 0)
            return false;
        else
            return true;
    }


    //*************************************************
    // Handler:跳转至不同页面
    //*************************************************
    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case SWITCH_LOGINACTIVITY:
                    Intent mIntent = new Intent();
                    mIntent.setClass(StartActivity.this, LoginActivity.class);
                    StartActivity.this.startActivity(mIntent);
                    StartActivity.this.finish();
                    break;
                case SWITCH_DEVICESCANACTIVITY:
                    mIntent = new Intent();
                    mIntent.setClass(StartActivity.this, DeviceScanActivity.class);
                    StartActivity.this.startActivity(mIntent);
                    StartActivity.this.finish();
                    break;
                case SWITCH_GUIDACTIVITY:
                    mIntent = new Intent();
                    mIntent.setClass(StartActivity.this, GuideActivity.class);
                    StartActivity.this.startActivity(mIntent);
                    StartActivity.this.finish();
                    break;
                case SWITCH_MAINACTIVITY:
                    mIntent = new Intent();
                    mIntent.setClass(StartActivity.this, MainActivity.class);
                    StartActivity.this.startActivity(mIntent);
                    StartActivity.this.finish();
                    break;
            }

            super.handleMessage(msg);
        }
    };
}
