package com.uoit.calvin.thesis_2016;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class TransactionDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "transDB";
    private static final String TABLE_NAME = "transactions";

    private static final String KEY_ID = "id";
    private static final String KEY_TRANS = "trans";
    private static final String KEY_TIMESTAMPS = "timestamps";
    private static final String KEY_YEAR = "year";
    private static final String KEY_MONTH = "month";
    private static final String KEY_DAY = "day";
    private static final String KEY_AMOUNT = "amount";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME+ " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_TRANS+ " TEXT," +
                    KEY_AMOUNT + " REAL," +
                    KEY_TIMESTAMPS+ " TEXT," +
                    KEY_YEAR + " REAL," +
                    KEY_MONTH + " REAL," +
                    KEY_DAY + " REAL" + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    TransactionDBHelper(Context context) {
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

    boolean addTransactions(Transaction trans) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRANS, trans.getTagsStr());
        values.put(KEY_TIMESTAMPS, trans.getTimestamp());
        values.put(KEY_AMOUNT, trans.getAmount());
        values.put(KEY_YEAR, trans.getMyDate().getYear());
        values.put(KEY_MONTH, trans.getMyDate().getMonth());
        values.put(KEY_DAY, trans.getMyDate().getDay());

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close();
        return true;
    }

    public void updateTransaction(Transaction trans) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRANS, trans.getTagsStr());
        values.put(KEY_TIMESTAMPS, trans.getTimestamp());
        values.put(KEY_AMOUNT, trans.getAmount());
        values.put(KEY_YEAR, trans.getMyDate().getYear());
        values.put(KEY_MONTH, trans.getMyDate().getMonth());
        values.put(KEY_DAY, trans.getMyDate().getDay());

        db.update(TABLE_NAME, values,  KEY_ID+"="+ trans.getId(), null);
    }


    void deleteTransactions(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = " + id, null);
        db.close();
    }

    List<Transaction> getAllData() {
        List<Transaction> transList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            Helper helper = new Helper();
            Transaction trans = new Transaction();

            trans.setId(cursor.getLong((cursor.getColumnIndex(KEY_ID))));
            trans.setTags(helper.parseTagUnicode(cursor.getString(cursor.getColumnIndex(KEY_TRANS))));
            trans.setTimestamp(cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMPS)));
            trans.setAmout(cursor.getFloat(cursor.getColumnIndex(KEY_AMOUNT)));

            transList.add(trans);
            cursor.moveToNext();
        }

        cursor.close();

        return transList;
    }

    Transaction getTransByID(long id) {
        Transaction trans = new Transaction();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY_ID + "='" + id + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){

            Helper helper = new Helper();
            trans.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
            trans.setTags(helper.parseTagUnicode(cursor.getString(cursor.getColumnIndex(KEY_TRANS))));
            trans.setTimestamp(cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMPS)));
            trans.setAmout(cursor.getFloat(cursor.getColumnIndex(KEY_AMOUNT)));

            cursor.moveToNext();
        }
        cursor.close();
        return trans;
    }

    List<Transaction> getTransByTag(String tag) {
        List<Transaction> transList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String s = cursor.getString(cursor.getColumnIndex(KEY_TRANS));
            Helper helper = new Helper();
            List<String> tagStrList = (helper.tagListToString(helper.parseTagUnicode(s)));
            if (tagStrList.contains(tag)) {
                Transaction trans = new Transaction();
                trans.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                trans.setTags(helper.parseTagUnicode(cursor.getString(cursor.getColumnIndex(KEY_TRANS))));
                trans.setTimestamp(cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMPS)));
                trans.setAmout(cursor.getFloat(cursor.getColumnIndex(KEY_AMOUNT)));

                transList.add(trans);
            }
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return transList;
    }


}
