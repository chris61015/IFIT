package com.dartmouth.cs.ifit.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by chris61015 on 5/14/17.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String TABLE_COLLECTION = "collectioninfo";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_COLLECTION_NAME = "collectionname";
    public static final String KEY_ICON = "icon";


    public static final String TABLE_TIMELINE = "timelineinfo";
    public static final String KEY_GROUP_ID = "groupid";
    public static final String KEY_IS_REMIND = "isremind";
    public static final String KEY_REMIND_TEXT = "remindtext";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_BODY_FAT_RATE = "bodyfatrate";
    public static final String KEY_DATE_TIME = "datetime";

    private static final String DB_NAME = "ifit.db";
    private static final int DB_VERSION = 1;

    // SQL statement to create database
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_COLLECTION
            + " ("
            + KEY_ROWID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_COLLECTION_NAME
            + " TEXT, "
            + KEY_ICON
            + " BLOB "
            + ");";


    // SQL statement to create database
    public static final String CREATE_TABLE2 = "CREATE TABLE IF NOT EXISTS "
            + TABLE_TIMELINE
            + " ("
            + KEY_ROWID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_GROUP_ID
            + " INTEGER, "
            + KEY_IS_REMIND
            + " INTEGER NOT NULL, "
            + KEY_REMIND_TEXT
            + " TEXT, "
            + KEY_PHOTO
            + " BLOB, "
            + KEY_WEIGHT
            + " TEXT, "
            + KEY_BODY_FAT_RATE
            + " TEXT, "
            + KEY_DATE_TIME
            + " DATETIME NOT NULL"
            + ");";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String info = "Upgrade DB from version "+oldVersion+" to "+newVersion+", which will destroy all old data";
        Log.w(DBHelper.class.getName(),info);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLLECTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMELINE);
        onCreate(db);
    }


}
