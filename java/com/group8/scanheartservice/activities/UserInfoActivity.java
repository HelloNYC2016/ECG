package com.group8.scanheartservice.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.group8.scanheartservice.DB.DBhelper;
import com.group8.scanheartservice.R;

public class UserInfoActivity extends Activity {

    private TextView tv_id_value;
    private TextView tv_name_value;
    private TextView tv_age_value;
    private TextView tv_gender_value;
    private TextView tv_height_value;
    private TextView tv_weight_value;

    private Button bt_quit;

    private DBhelper dbh = new DBhelper(this);
    private SQLiteDatabase db = null;

    private static final String SHAREDPREFERENCES_NAME = "user_state";
    private static final String USER_ID = "user_id";
    private void setUserId(int user_id){
        SharedPreferences settings = getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(USER_ID, user_id);
        editor.commit();
    }

    private int userId;

    private Cursor db_cur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        db = dbh.getReadableDatabase();
        userId = this.getSharedPreferences("user_state", Context.MODE_WORLD_READABLE)
                .getInt("user_id", 0);//取得user_id的值
        String[] query_column = {"username", "name", "age", "gender", "height", "weight"};

        db_cur = db.query("user", query_column, "user_id=?", new String[]{"" + userId}, null, null, null);


        tv_id_value = (TextView) findViewById(R.id.tv_id_value);
        tv_name_value = (TextView) findViewById(R.id.tv_name_value);
        tv_age_value = (TextView) findViewById(R.id.tv_age_value);
        tv_gender_value = (TextView) findViewById(R.id.tv_gender_value);
        tv_height_value = (TextView) findViewById(R.id.tv_height_value);
        tv_weight_value = (TextView) findViewById(R.id.tv_weight_value);

        bt_quit = (Button) findViewById(R.id.bt_quit);

        if (db_cur.moveToNext()) {
            tv_id_value.setText(db_cur.getString(0));
            tv_name_value.setText(db_cur.getString(1));
            tv_age_value.setText(db_cur.getString(2) + " 岁");
            tv_gender_value.setText(db_cur.getString(3));
            tv_height_value.setText(db_cur.getString(4) + " cm");
            tv_weight_value.setText(db_cur.getString(5) + " kg");
        }

        db.close();

    }

    public void onChangeUserInfo(View v) {
        int viewId = v.getId();
        db = dbh.getReadableDatabase();
        final EditText et;
        switch (viewId) {
            case R.id.tv_name:
            case R.id.tv_name_value:
                et = new EditText(this);
                et.setHint(tv_name_value.getText().toString());
                new AlertDialog.Builder(this).setTitle("姓名")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tv_name_value.setText(et.getText().toString());
                                ContentValues cv = new ContentValues();
                                cv.put("name", et.getText().toString());
                                db.update("user", cv, "user_id=?", new String[]{"" + userId});
                                db.close();
                            }
                        })
                        .show();
                break;
            case R.id.tv_age:
            case R.id.tv_age_value:
                et = new EditText(this);
                et.setHint(tv_age_value.getText().toString());
                new AlertDialog.Builder(this).setTitle("年龄")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tv_age_value.setText(et.getText().toString() + " 岁");
                                ContentValues cv = new ContentValues();
                                cv.put("age", et.getText().toString());
                                db.update("user", cv, "user_id=?", new String[]{"" + userId});
                                db.close();
                            }
                        })
                        .show();
                break;
            case R.id.tv_gender:
            case R.id.tv_gender_value:
                et = new EditText(this);
                et.setHint("请输入‘男’或者‘女’");
                new AlertDialog.Builder(this).setTitle("性别")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tv_gender_value.setText(et.getText().toString());
                                ContentValues cv = new ContentValues();
                                cv.put("gender", et.getText().toString());
                                db.update("user", cv, "user_id=?", new String[]{"" + userId});
                                db.close();
                            }
                        })
                        .show();
                break;
            case R.id.tv_height:
            case R.id.tv_height_value:
                et = new EditText(this);
                et.setHint(tv_height_value.getText().toString());
                new AlertDialog.Builder(this).setTitle("身高")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tv_height_value.setText(et.getText().toString() + " cm");
                                ContentValues cv = new ContentValues();
                                cv.put("height", et.getText().toString());
                                db.update("user", cv, "user_id=?", new String[]{"" + userId});
                                db.close();
                            }
                        })
                        .show();
                break;
            case R.id.tv_weight:
            case R.id.tv_weight_value:
                et = new EditText(this);
                et.setHint(tv_weight_value.getText().toString());
                new AlertDialog.Builder(this).setTitle("体重")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tv_weight_value.setText(et.getText().toString() + " kg");
                                ContentValues cv = new ContentValues();
                                cv.put("weight", et.getText().toString());
                                db.update("user", cv, "user_id=?", new String[]{"" + userId});
                                db.close();
                            }
                        })
                        .show();
                break;
        }

    }

    public void onQuit(View v) {
        Intent in = new Intent(UserInfoActivity.this, LoginActivity.class);
        setUserId(0);
        startActivity(in);
        finish();
    }
}
