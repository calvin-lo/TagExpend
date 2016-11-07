package com.uoit.calvin.thesis2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

class TransactionDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "transDB";
    private static final String TABLE_NAME = "transactions";

    private static  final String KEY_ID = "id";
    private static final String KEY_TRANS = "trans";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME+ " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_TRANS+ TEXT_TYPE+ " )";

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
        values.put(KEY_TRANS, trans.toString());

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close();
        return true;
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
            String s = cursor.getString(cursor.getColumnIndex(KEY_TRANS));
            Helper helper = new Helper();
            Transaction trans = new Transaction(helper.parseTag(s));
            trans.setId(cursor.getLong((cursor.getColumnIndex(KEY_ID))));
            transList.add(trans);
            cursor.moveToNext();
        }

        cursor.close();

        return transList;
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
            List<Tag> tagList = helper.parseTag(s);
            List<String> tagStrList = helper.tagListToString(helper.parseTag(s));
            if (tagStrList.contains(tag)) {
                Transaction trans = new Transaction(tagList);
                trans.setId(cursor.getLong((cursor.getColumnIndex(KEY_ID))));
                transList.add(trans);
            }
            cursor.moveToNext();
        }

        cursor.close();
        return transList;
    }

}
