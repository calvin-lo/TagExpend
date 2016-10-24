package com.uoit.calvin.thesis2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.widget.CursorAdapter;

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
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
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

    public boolean addTransactions(Tag tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_ID,tag.getId());
        values.put(KEY_TAG, tag.getTag());

        // Inserting Row
        db.insert(TABLE_TRANSACTIONS, null, values);
        db.close();
        return true;
    }

    public void deleteTransactions(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, KEY_ID + " = " + id, null);
        db.close();
    }

    // Getting All Data
    public List<Tag> getAllData() {
        List<Tag> tagList = new ArrayList<Tag>();
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTIONS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            Tag tag = new Tag();
            tag.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
            tag.setTag(cursor.getString(cursor.getColumnIndex(KEY_TAG)));
            tagList.add(tag);
            cursor.moveToNext();
        }

        return tagList;
    }

}
