package com.dartmouth.cs.ifit.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dartmouth.cs.ifit.Model.CollectionEntry;
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
            DBHelper.KEY_COLLECTION_NAME
    };

    public CollectionInfoDAO(Context context) {
        dbHelper = new DBHelper(context);
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


    // Insert a item given each column value
    public CollectionEntry insertEntry(CollectionEntry entry) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_COLLECTION_NAME, entry.getCollectionName());

        long id = db.insert(DBHelper.TABLE_COLLECTION, null, values);
        entry.setId(id);
        return entry;
    }

    // Remove an entry by giving its index
    public void removeEntry(long id) {
        db.delete(DBHelper.TABLE_COLLECTION, DBHelper.KEY_ROWID + " = " + id, null);
    }

    public void updateEntryName(CollectionEntry entry) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_COLLECTION_NAME, entry.getCollectionName());
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

            entries.add(entry);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        close();
        return entries;
    }
}
