package com.group8.scanheartservice.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper {

	public static final int VERSION = 1;
	public static final String TABLE1_NAME = "user";
	public static final String TABLE2_NAME = "ecgdata";
	public static final String DATABASE_NAME = "shrs.db";

	public DBhelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
		// TODO Auto-generated constructor stub

	}


//	public DBhelper(Context context, int version) {
//		super(context, DATABASE_NAME, null, version);
//		// TODO Auto-generated constructor stub
//	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String str = "create table " + TABLE1_NAME +
				"(user_id integer primary key autoincrement , username varchar , password varchar , " +
				"name varchar default '无' , age integer default 0, gender varchar default '男' , " +
				"height integer default 0 , weight integer default 0);";
		db.execSQL(str);

		db.execSQL("CREATE TABLE " + TABLE2_NAME + "(data_id INTEGER PRIMARY KEY AUTOINCREMENT , user_id integer , data text , data_time varchar);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		System.out.println("UPgrade!");
	}

}
