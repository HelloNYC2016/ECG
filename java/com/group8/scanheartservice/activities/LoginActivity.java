package com.group8.scanheartservice.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.group8.scanheartservice.DB.DBhelper;
import com.group8.scanheartservice.R;

public class LoginActivity extends Activity {

    private DBhelper dbh = new DBhelper(this);
    private SQLiteDatabase db = null;
    Cursor db_cur;

    EditText username, password;
    Button login, register;

    private static final String SHAREDPREFERENCES_NAME = "user_state";
    private static final String USER_ID = "user_id";
    private void setUserId(int user_id){
        SharedPreferences settings = getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(USER_ID, user_id);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        username = (EditText) this.findViewById(R.id.et_username);
        password = (EditText) this.findViewById(R.id.et_password);
        login = (Button) this.findViewById(R.id.bt_login);
        register = (Button) this.findViewById(R.id.bt_register);

    }

    public void onLogin(View v) {
        db = dbh.getReadableDatabase();

        String[] query_column = {"user_id", "password"};

        db_cur = db.query("user", query_column, "username=?", new String[]{username.getText().toString()}, null, null, null);
        String check_password = "";
        if (db_cur.moveToNext()) {
            setUserId(db_cur.getInt(0));
            check_password = db_cur.getString(1);
        }
        db.close();
        String str_username = username.getText().toString();
        String str_password = password.getText().toString();
        if (username.getText().toString().equals("") || password.getText().toString().equals("")) {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            if (password.getText().toString().equals(check_password)) {
                Intent in = new Intent(LoginActivity.this, DeviceScanActivity.class);
                Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
                startActivity(in);
                finish();
            } else {
                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        }
    }// onLogin()

    public void onRegister(View v) {
        db = dbh.getReadableDatabase();

        String[] query_column = {"user_id", "password"};

        db_cur = db.query("user", query_column, "username=?", new String[]{username.getText().toString()}, null, null, null);

        if (username.getText().toString().equals("") || password.getText().toString().equals("")) {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            if (db_cur.moveToNext()) {
                Toast.makeText(this, "用户已经存在", Toast.LENGTH_SHORT).show();
                db.close();
            } else {
                ContentValues cv = new ContentValues();
                cv.put("username", username.getText().toString());
                cv.put("password", password.getText().toString());

                db.insert("user", null, cv);
                db_cur = db.query("user", new String[]{"user_id"}, "username=?", new String[]{username.getText().toString()}, null, null, null);


                if (db_cur.moveToNext()) {
                    setUserId(db_cur.getInt(0));
                }
                db.close();

                Intent in = new Intent(LoginActivity.this, DeviceScanActivity.class);
                Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
                startActivity(in);
                finish();
            }
        }
    }// onRegister()

}
