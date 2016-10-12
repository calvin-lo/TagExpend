package com.uoit.calvin.thesis2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mainDB";
    private static final String TABLE_TRANSACTIONS = "transactions";

    private static  final String KEY_ID = "id";
    private static final String KEY_TAG = "tag";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY," +
                    KEY_TAG + TEXT_TYPE+ " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addTransactions(String tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TAG, tag); // Contact Name

        // Inserting Row
        db.insert(TABLE_TRANSACTIONS, null, values);
        db.close(); // Closing database connection
    }

    // Getting All Contacts
    public List<String> getAllData() {
        List<String> tagList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Adding contact to list
                tagList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // return contact list
        return tagList;
    }

}
