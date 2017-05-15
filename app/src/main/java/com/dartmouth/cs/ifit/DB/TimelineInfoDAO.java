package com.dartmouth.cs.ifit.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dartmouth.cs.ifit.Model.TimelineEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by chris61015 on 5/14/17.
 */

public class TimelineInfoDAO {
    // Database fields
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private String[] allColumns = {
            DBHelper.KEY_ROWID,
            DBHelper.KEY_GROUP_ID,
            DBHelper.KEY_COLLECTION_NAME,
            DBHelper.KEY_IS_REMIND,
            DBHelper.KEY_REMIND_TEXT,
            DBHelper.KEY_PHOTO,
            DBHelper.KEY_WEIGHT,
            DBHelper.KEY_BODY_FAT_RATE,
            DBHelper.KEY_DATE_TIME
    };

    public TimelineInfoDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        try {
            db = dbHelper.getWritableDatabase();
            System.out.println("~~~");
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void close() {
        dbHelper.close();
    }


    // Insert a item given each column value
    public TimelineEntry insertEntry(TimelineEntry entry) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_GROUP_ID, entry.getGroudId());
        values.put(DBHelper.KEY_COLLECTION_NAME, entry.getCollectionName());
        values.put(DBHelper.KEY_IS_REMIND, entry.getRemind());
        values.put(DBHelper.KEY_REMIND_TEXT, entry.getRemindText());
        values.put(DBHelper.KEY_PHOTO, entry.getPhoto());
        values.put(DBHelper.KEY_WEIGHT, entry.getWeight());
        values.put(DBHelper.KEY_BODY_FAT_RATE, entry.getBodyFatRate());
        values.put(DBHelper.KEY_DATE_TIME, entry.getDateTime().getTimeInMillis());

        long id = db.insert(DBHelper.TABLE_TIMELINE, null, values);
        entry.setId(id);

        return entry;
    }

    // Remove an entry by giving its index
    public void removeEntry(long id) {
        db.delete(DBHelper.TABLE_TIMELINE, DBHelper.KEY_ROWID + " = " + id, null);
    }

    // Query the entire table, return all rows
    public synchronized ArrayList<TimelineEntry> fetchEntryByGroupId(long groupId) {
        open();
        ArrayList<TimelineEntry> entries = new ArrayList<>();

        Cursor cursor = db.query(DBHelper.TABLE_TIMELINE, allColumns, "groupid=?",
                new String[]{String.valueOf(groupId)}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TimelineEntry entry = new TimelineEntry();
            entry.setId(cursor.getLong(0));
            entry.setGroudId(cursor.getLong(1));
            entry.setCollectionName(cursor.getString(2));
            entry.setRemind(cursor.getInt(3));
            entry.setRemindText(cursor.getString(4));
            entry.setPhoto(cursor.getBlob(5));
            entry.setWeight(cursor.getFloat(6));
            entry.setBodyFatRate(cursor.getFloat(7));

            // Calendar
            Calendar calendar= GregorianCalendar.getInstance();
            calendar.setTimeInMillis(cursor.getLong(8));
            entry.setDateTime(calendar);


            entries.add(entry);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        close();
        return entries;
    }
}
