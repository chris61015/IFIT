package com.dartmouth.cs.ifit.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by chris61015 on 5/14/17.
 */

public class CollectionEntryDbHelper extends SQLiteOpenHelper {
    public static final String TABLE_COLLECTION = "collectioninfo";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_COLLECTION_NAME = "collectionname";

    private static final String DB_NAME = "ifit.db";
    private static final int DB_VERSION = 1;

    // SQL statement to create database
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_COLLECTION
            + " ("
            + KEY_ROWID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_COLLECTION_NAME
            + " TEXT"
            + ");";

    public CollectionEntryDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String info = "Upgrade DB from version "+oldVersion+" to "+newVersion+", which will destroy all old data";
        Log.w(TimelineEntryDbHelper.class.getName(),info);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLLECTION);
        onCreate(db);
    }


}