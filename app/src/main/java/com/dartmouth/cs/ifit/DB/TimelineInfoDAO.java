package com.dartmouth.cs.ifit.DB;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dartmouth.cs.ifit.model.TimelineEntry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
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
            DBHelper.KEY_IS_REMIND,
            DBHelper.KEY_REMIND_TEXT,
            DBHelper.KEY_PHOTO,
            DBHelper.KEY_WEIGHT,
            DBHelper.KEY_BODY_FAT_RATE,
            DBHelper.KEY_DATE_TIME
    };

    Context context;

    public TimelineInfoDAO(Context context) {
        dbHelper = new DBHelper(context);
        this.context = context;
    }

    public void open() {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void close() {
        dbHelper.close();
    }


    // Insert icon item given each column value
    public TimelineEntry insertEntry(TimelineEntry entry) {
        if (!db.isOpen())
            open();
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_GROUP_ID, entry.getGroudId());
        values.put(DBHelper.KEY_IS_REMIND, entry.getRemind());
        values.put(DBHelper.KEY_REMIND_TEXT, entry.getRemindText());
        values.put(DBHelper.KEY_PHOTO, saveToInternalStorage(entry));
        values.put(DBHelper.KEY_WEIGHT, entry.getWeight() + "");
        values.put(DBHelper.KEY_BODY_FAT_RATE, entry.getBodyFatRate() + "");
        values.put(DBHelper.KEY_DATE_TIME, entry.getDateTime().getTimeInMillis());

        long id = db.insert(DBHelper.TABLE_TIMELINE, null, values);
        entry.setId(id);

        return entry;
    }

    public void updateEntry(TimelineEntry entry) {
        if (!db.isOpen())
            open();

        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_GROUP_ID, entry.getGroudId());
        values.put(DBHelper.KEY_IS_REMIND, entry.getRemind());
        values.put(DBHelper.KEY_REMIND_TEXT, entry.getRemindText());
        values.put(DBHelper.KEY_PHOTO, saveToInternalStorage(entry));
        values.put(DBHelper.KEY_WEIGHT, entry.getWeight() + "");
        values.put(DBHelper.KEY_BODY_FAT_RATE, entry.getBodyFatRate() + "");
        values.put(DBHelper.KEY_DATE_TIME, entry.getDateTime().getTimeInMillis());

        db.update(DBHelper.TABLE_TIMELINE, values, DBHelper.KEY_ROWID + " = " + entry.getId(), null);
    }

    // Remove an entry by giving its index
    public void removeEntry(long id) {
        if (!db.isOpen())
            open();
        db.delete(DBHelper.TABLE_TIMELINE, DBHelper.KEY_ROWID + " = " + id, null);
    }

    public TimelineEntry getEntryById(long id) {
        Cursor cursor = db.query(DBHelper.TABLE_TIMELINE, allColumns, "_id=?",
                new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        TimelineEntry entry = new TimelineEntry();
        if (!cursor.isAfterLast()) {
            entry.setId(cursor.getLong(0));
            entry.setGroudId(cursor.getLong(1));
            entry.setRemind(cursor.getInt(2));
            entry.setRemindText(cursor.getString(3));
            loadImageFromStorage(entry, cursor.getString(4));
            entry.setWeight(Double.parseDouble(cursor.getString(5)));
            entry.setBodyFatRate(Double.parseDouble(cursor.getString(6)));

            // Calendar
            Calendar calendar= GregorianCalendar.getInstance();
            calendar.setTimeInMillis(cursor.getLong(7));
            entry.setDateTime(calendar);

            cursor.moveToNext();
        }
        else
            entry = null;
        // Make sure to close the cursor
        cursor.close();
        return entry;
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
            entry.setRemind(cursor.getInt(2));
            entry.setRemindText(cursor.getString(3));
            if (entry.getRemind().equals(1)){
                entry.setPhoto(null);
                entry.setWeight(0.0);
                entry.setBodyFatRate(0.0);
            } else {
                loadImageFromStorage(entry, cursor.getString(4));
                entry.setWeight(Double.parseDouble(cursor.getString(5)));
                entry.setBodyFatRate(Double.parseDouble(cursor.getString(6)));
            }

            // Calendar
            Calendar calendar= GregorianCalendar.getInstance();
            calendar.setTimeInMillis(cursor.getLong(7));
            entry.setDateTime(calendar);

            entries.add(entry);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        close();
        return entries;
    }

    // Query the entire table, return all rows
    public  ArrayList<TimelineEntry> fetchEntries() {
        open();
        ArrayList<TimelineEntry> entries = new ArrayList<>();

        Cursor cursor = db.query(DBHelper.TABLE_TIMELINE, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TimelineEntry entry = new TimelineEntry();
            entry.setId(cursor.getLong(0));
            entry.setGroudId(cursor.getLong(1));
            entry.setRemind(cursor.getInt(2));
            entry.setRemindText(cursor.getString(3));
            loadImageFromStorage(entry, cursor.getString(4));
            entry.setWeight(Double.parseDouble(cursor.getString(5)));
            entry.setBodyFatRate(Double.parseDouble(cursor.getString(6)));

            // Calendar
            Calendar calendar= GregorianCalendar.getInstance();
            calendar.setTimeInMillis(cursor.getLong(7));
            entry.setDateTime(calendar);

            entries.add(entry);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        close();
        return entries;
    }

    private String saveToInternalStorage(TimelineEntry entry){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir

        if (entry.getPath().length() > 0) {
            File file = new File(entry.getPath());
            if (file.exists())
                file.delete();
        }

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, String.valueOf(System.currentTimeMillis()) + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            fos.write(entry.getPhoto());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        entry.setPath(mypath.getAbsolutePath());
        return mypath.getAbsolutePath();
    }

    private void loadImageFromStorage(TimelineEntry entry, String path) {
        try {
            entry.setPath(path);
            RandomAccessFile f = new RandomAccessFile(path, "r");
            byte[] b = new byte[(int)f.length()];
            f.readFully(b);
            entry.setPhoto(b);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
