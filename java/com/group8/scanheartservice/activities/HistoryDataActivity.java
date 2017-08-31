package com.group8.scanheartservice.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.group8.scanheartservice.DB.DBhelper;
import com.group8.scanheartservice.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryDataActivity extends Activity {

    private ListView lv_history;

    private int userId;
    Cursor db_cur;

    // 数据库
    private DBhelper dbh = new DBhelper(this);
    private SQLiteDatabase db = null;

    private static final String SHAREDPREFERENCES_NAME = "user_state";
    private static final String USER_ID = "user_id";

    private List<HashMap<String,Object>> history_items = new ArrayList<HashMap<String,Object>>();

    private SimpleAdapter adapter;
    private int item_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_data);

        SharedPreferences mUserStatePref = this.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE);
        userId = mUserStatePref.getInt(USER_ID, 0);
        db = dbh.getReadableDatabase();

        db_cur = db.query("ecgdata", new String[]{"data", "data_time" , "data_id"}, "user_id=?", new String[]{"" + userId}, null, null, null);

        lv_history = (ListView) findViewById(R.id.lv_history);
        int i = 1;
        while (db_cur.moveToNext()) {
            HashMap<String,Object>map = new HashMap<String,Object>();
            map.put("data", db_cur.getString(0));
            map.put("data_num", "心电历史 " + i++);
            map.put("data_time", db_cur.getString(1));
            map.put("data_id", db_cur.getInt(2));
            history_items.add(map);
        }
        adapter = new SimpleAdapter(this, history_items, R.layout.history_item, new String[]{"data_num", "data_time"}, new int[]{R.id.tv_data_num,R.id.tv_data_time});

        lv_history.setAdapter(adapter);

        lv_history.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String datas = (String) history_items.get(i).get("data");
                String[] dataArray = datas.split(",");
                int[] intDataArray = new int[dataArray.length];
                for (int m = 0; m < dataArray.length; m++) {
                    intDataArray[m] = Integer.parseInt(dataArray[m]);
                }
                GraphicalView history_chart;
                XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
                XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                XYSeries series = new XYSeries("chart");
                for (int k = 0; k < intDataArray.length; k++) {
                    series.add(k, intDataArray[k]);
                }
                dataset.addSeries(series);

                XYSeriesRenderer xyRenderer = new XYSeriesRenderer();
                xyRenderer.setColor(Color.DKGRAY);
                renderer.addSeriesRenderer(xyRenderer);
                renderer.setXAxisMin(0);
                renderer.setXAxisMax(1500);
                renderer.setYAxisMin(0);
                renderer.setYAxisMax(200);

                history_chart = ChartFactory.getLineChartView(HistoryDataActivity.this, dataset, renderer);

                new AlertDialog.Builder(HistoryDataActivity.this).setTitle((String) history_items.get(i).get("data_time"))
                        .setView(history_chart)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
            }
        });

        lv_history.setOnCreateContextMenuListener(new OnCreateContextMenuListener(){

            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_history_item, contextMenu);
            }
        });

        lv_history.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                lv_history.showContextMenu();
                item_id = i;
                return true;
            }
        });
    }



    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        db = dbh.getReadableDatabase();

        int data_id= (Integer) history_items.get(item_id).get("data_id");
        switch (item.getItemId()) {
            case R.id.delete_item:
                db.delete("ecgdata", "data_id=?", new String[]{"" + data_id});

                history_items.clear();
                db_cur = db.query("ecgdata", new String[]{"data", "data_time" , "data_id"}, "user_id=?", new String[]{"" + userId}, null, null, null);

                lv_history = (ListView) findViewById(R.id.lv_history);
                int i = 1;
                while (db_cur.moveToNext()) {
                    HashMap<String,Object>map = new HashMap<String,Object>();
                    map.put("data", db_cur.getString(0));
                    map.put("data_num", "心电历史 " + i++);
                    map.put("data_time", db_cur.getString(1));
                    map.put("data_id", db_cur.getInt(2));
                    history_items.add(map);
                }

                adapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
