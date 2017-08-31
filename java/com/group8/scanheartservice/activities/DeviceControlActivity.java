package com.group8.scanheartservice.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.group8.scanheartservice.DB.DBhelper;
import com.group8.scanheartservice.R;
import com.group8.scanheartservice.services.BluetoothLeService;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * For a given BLE device, this Activity provides the user interface to connect,
 * display data, and display GATT services and characteristics supported by the
 * device. The Activity communicates with {@code BluetoothLeService}, which in
 * turn interacts with the Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
	private final static String TAG = DeviceControlActivity.class
			.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private TextView mConnectionState;
    private Button bt_save;
	private String mDeviceAddress;
	private BluetoothLeService mBluetoothLeService;
    private int userId;
	private boolean mConnected = false;

    // 数据库
    private DBhelper dbh = new DBhelper(this);
    private SQLiteDatabase db = null;


    // 画图
    private String title = "Heart Rate Measurement";
    private XYSeries series;
    private XYMultipleSeriesDataset dataset;
    private GraphicalView chart;
    private XYMultipleSeriesRenderer renderer;
    private Context context;
    private int addX[] = new int[20];
    private int addY[] = new int[20];
    private static int count = 0;

    int[] x = new int[1500];
    int[] y = new int[1500];

	private static final String SHAREDPREFERENCES_NAME = "user_state";
	private static final String BLUETOOTH_ADDRESS = "bluetooth_address";
    private static final String USER_ID = "user_id";


	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();		// Service中实现，获取蓝牙Service
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	// Handles various events fired by the Service.
	// ACTION_GATT_CONNECTED: connected to a GATT server.
	// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
	// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
	// ACTION_DATA_AVAILABLE: received data from the device. This can be a
	// result of read
	// or notification operations.
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnected = true;
				updateConnectionState(R.string.connected);
				invalidateOptionsMenu();
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				mConnected = false;
				updateConnectionState(R.string.disconnected);
				invalidateOptionsMenu();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				// Show all the supported services and characteristics on the
				// user interface.
//				displayGattServices(mBluetoothLeService
//						.getSupportedGattServices());
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				ArrayList<Integer> strHeartRate = new ArrayList<Integer>();
				strHeartRate = intent.getIntegerArrayListExtra(BluetoothLeService.EXTRA_DATA);
				// 画图

				// 更新
				displayData(strHeartRate);
			}
		}
	};

	// If a given GATT characteristic is selected, check for supported features.
	// This sample
	// demonstrates 'Read' and 'Notify' features. See
	// http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for
	// the complete
	// list of supported characteristic features.

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gatt_services_characteristics);

        bt_save = (Button) findViewById(R.id.bt_save);

        SharedPreferences mUserStatePref = this.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE);
        mDeviceAddress = mUserStatePref.getString(BLUETOOTH_ADDRESS, "");//取得所有类名 如 com.my.MainActivity
        userId = mUserStatePref.getInt(USER_ID, 0);

		// Sets up UI references.
		((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
		mConnectionState = (TextView) findViewById(R.id.connection_state);

		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        // 绘图
        context = getApplicationContext();
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
        series = new XYSeries(title);
        dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.BLACK);

        renderer.setZoomEnabled(true);
        renderer.setChartTitle(title);
        renderer.setXAxisMin(0);
        renderer.setXAxisMax(1500);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(200);
        renderer.setChartTitleTextSize(60);
        renderer.setLabelsColor(Color.RED);
        renderer.setShowGrid(true);
        renderer.setGridColor(Color.BLUE);

        renderer.setPointSize(5);
		renderer.setBackgroundColor(Color.WHITE);
		renderer.setApplyBackgroundColor(true);
        renderer.setAxisTitleTextSize(30);
        renderer.setXTitle("time");
        renderer.setYTitle("heart rate");
        renderer.addSeriesRenderer(r);

        chart = ChartFactory.getCubeLineChartView(context,
                dataset,renderer, 1.0f);
        //FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layout.addView(chart);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null) {
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
		mBluetoothLeService = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gatt_services, menu);
		if (mConnected) {
			menu.findItem(R.id.menu_connect).setVisible(false);
			menu.findItem(R.id.menu_disconnect).setVisible(true);
		} else {
			menu.findItem(R.id.menu_connect).setVisible(true);
			menu.findItem(R.id.menu_disconnect).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_connect:
			mBluetoothLeService.connect(mDeviceAddress);
			return true;
		case R.id.menu_disconnect:
			mBluetoothLeService.disconnect();
			return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateConnectionState(final int resourceId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mConnectionState.setText(resourceId);
			}
		});
	}

	private void displayData(ArrayList<Integer> data) {
		if (data != null) {
            dataset.removeSeries(series);

            int length = series.getItemCount();
            if(length>1500){
                length = 1500;
            }
            //将旧的典籍中的x和y的数值取出来放入备份中，x值增加20
            for(int i = 0;i < length;i++){
                x[i] = (int)series.getX(i) + 20;
                y[i] = (int)series.getY(i);
            }
            //清空series集
            series.clear();

            for (int i = 0; i < 20; i++) {
                series.add(i, data.get(19-i));
            }

            for(int k = 0;k < length;k++){
                series.add(x[k],y[k]);
            }
            dataset.addSeries(series);

			chart.invalidate();


		}
	}
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

    public void onSaveData(View v) {
        db = dbh.getReadableDatabase();
        String str = "";
        Calendar cal = Calendar.getInstance();

        String time = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" +
                cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" +
                cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);

        for (int i = 0; i < y.length - 1; i++) {
            str += y[i] + ",";
        }

        str += y[y.length - 1];

        ContentValues cv = new  ContentValues();
        cv.put("user_id", userId);
        cv.put("data", str);
        cv.put("data_time", time);

        db.insert("ecgdata", null, cv);

        db.close();

        Toast.makeText(DeviceControlActivity.this, "保存完成", Toast.LENGTH_SHORT).show();


    }
}
