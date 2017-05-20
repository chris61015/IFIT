package com.dartmouth.cs.ifit.DB;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;

import com.dartmouth.cs.ifit.model.CollectionEntry;
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


/**
 * Created by chris61015 on 5/15/17.
 */

public class CollectionInfoDAO {
    // Database fields
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private String[] allColumns = {
            DBHelper.KEY_ROWID,
            DBHelper.KEY_COLLECTION_NAME,
            DBHelper.KEY_ICON
    };

    Context context;

    public CollectionInfoDAO(Context context) {
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
    public CollectionEntry insertEntry(CollectionEntry entry) {
        if (!db.isOpen())
            open();
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_COLLECTION_NAME, entry.getCollectionName());
        values.put(DBHelper.KEY_ICON, saveToInternalStorage(entry));

        long id = db.insert(DBHelper.TABLE_COLLECTION, null, values);
        entry.setId(id);
        return entry;
    }

    // Remove an entry by giving its index
    public void removeEntry(long id) {
        if (!db.isOpen())
            open();
        db.delete(DBHelper.TABLE_COLLECTION, DBHelper.KEY_ROWID + " = " + id, null);
    }

    public void updateEntryName(CollectionEntry entry) {
        if (!db.isOpen())
            open();
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_COLLECTION_NAME, entry.getCollectionName());
        db.update(DBHelper.TABLE_COLLECTION, values, DBHelper.KEY_ROWID + " = " + entry.getId(), null);
    }

    public void updateEntryIcon(CollectionEntry entry) {
        if (!db.isOpen())
            open();
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_ICON, saveToInternalStorage(entry));
        db.update(DBHelper.TABLE_COLLECTION, values, DBHelper.KEY_ROWID + " = " + entry.getId(), null);
    }

    // Query the entire table, return all rows
    public synchronized ArrayList<CollectionEntry> fetchEntries() {
        open();
        ArrayList<CollectionEntry> entries = new ArrayList<>();

        Cursor cursor = db.query(DBHelper.TABLE_COLLECTION, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CollectionEntry entry = new CollectionEntry();
            entry.setId(cursor.getLong(0));
            entry.setCollectionName(cursor.getString(1));
            loadImageFromStorage(entry, cursor.getString(2));

            entries.add(entry);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        close();
        return entries;
    }

    private String saveToInternalStorage(CollectionEntry entry){
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
            fos.write(entry.getIcon());
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

    private void loadImageFromStorage(CollectionEntry entry, String path) {
        try {
            entry.setPath(path);
            RandomAccessFile f = new RandomAccessFile(path, "r");
            byte[] b = new byte[(int)f.length()];
            f.readFully(b);
            entry.setIcon(b);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
