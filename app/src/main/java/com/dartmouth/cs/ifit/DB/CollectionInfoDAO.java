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
    private CollectionEntryDbHelper dbHelper;
    private String[] allColumns = {
            CollectionEntryDbHelper.KEY_ROWID,
            CollectionEntryDbHelper.KEY_COLLECTION_NAME
    };

    private static final String TAG = "DB";

    public CollectionInfoDAO(Context context) {
        dbHelper = new CollectionEntryDbHelper(context);
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
        open();
        ContentValues values = new ContentValues();
        values.put(CollectionEntryDbHelper.KEY_COLLECTION_NAME, entry.getCollectionName());

        long id = db.insert(CollectionEntryDbHelper.TABLE_COLLECTION, null, values);
        entry.setId(id);
        close();
        return entry;
    }

    // Remove an entry by giving its index
    public void removeEntry(long id) {
        open();
        db.delete(CollectionEntryDbHelper.TABLE_COLLECTION, CollectionEntryDbHelper.KEY_ROWID + " = " + id, null);
        close();
    }

    public void updateEntryName(CollectionEntry entry) {
        open();
        ContentValues values = new ContentValues();
        values.put(CollectionEntryDbHelper.KEY_COLLECTION_NAME, entry.getCollectionName());
        db.update(CollectionEntryDbHelper.TABLE_COLLECTION, values, CollectionEntryDbHelper.KEY_ROWID + " = " + entry.getId(), null);
        close();
    }

    // Query the entire table, return all rows
    public synchronized ArrayList<CollectionEntry> fetchEntries() {
        open();
        ArrayList<CollectionEntry> entries = new ArrayList<>();

        Cursor cursor = db.query(CollectionEntryDbHelper.TABLE_COLLECTION, allColumns, null, null, null, null, null);
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
