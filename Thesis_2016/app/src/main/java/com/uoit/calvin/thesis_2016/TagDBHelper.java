package com.uoit.calvin.thesis_2016;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TagDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tagCloudDB";
    private static final String TABLE_NAME = "tagCloud";

    private static  final String KEY_ID = "id";
    private static final String KEY_TAG = "tag";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_TYPE = "type";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_TAG + " TEXT" + "UNIQUE," +
                    KEY_AMOUNT + " REAL," +
                    KEY_TYPE + " TEXT" + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    TagDBHelper(Context context) {
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

    boolean addTag(Tag tag) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!checkDuplicate(tag)) {
            values.put(KEY_TAG, tag.getName());
            values.put(KEY_AMOUNT, tag.getAmount());
            values.put(KEY_TYPE, tag.getType());

            // Inserting Row
            db.insert(TABLE_NAME, null, values);

        } else {
            float amount = getAmount(tag.getName()) + tag.getAmount();
            values.put(KEY_AMOUNT, amount);
            db.update(TABLE_NAME, values, KEY_TAG + " =?", new String[] {tag.getName()});
        }
        db.close();
        return true;
    }

    private float getAmount(String tag) {
        SQLiteDatabase database = this.getReadableDatabase();

        String query = "SELECT " + KEY_AMOUNT +" FROM " + TABLE_NAME + " WHERE " + KEY_TAG + "='" + tag + "'";
        Cursor cursor = database.rawQuery(query,null);

        if (cursor !=null)  {
            cursor.moveToFirst();
        }
        float output = cursor.getFloat(cursor.getColumnIndex(KEY_AMOUNT));

        cursor.close();
        return output;
    }

    public void updateTag(Tag tag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        float amount = getAmount(tag.getName()) - tag.getAmount();
        values.put(KEY_AMOUNT, amount);
        db.update(TABLE_NAME, values, KEY_TAG + " =?", new String[] {tag.getName()});
        db.close();
    }

    private boolean checkDuplicate(Tag tag) {
        return (new ArrayList<>(Arrays.asList(getTagsStringList()))).contains(tag.toString());
    }

    boolean deleteTag(String tag) {
        SQLiteDatabase db = this.getWritableDatabase();
        String type = String.valueOf(tag.charAt(0));
        tag = tag.substring(1,tag.length());
        db.delete(TABLE_NAME, KEY_TAG + " = ? AND " + KEY_TYPE + " = ? ", new String[] {tag, type});
        db.close();
        return true;
    }


    List<Tag> getTagsList(String type) {
        List<Tag> tagList = new ArrayList<>();
        String selectQuery;

        if (type.equals("*")) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME;
        } else {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY_TYPE + "='" + type + "'";
        }


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            Tag tag = new Tag(cursor.getString(cursor.getColumnIndex(KEY_TAG)), cursor.getString(cursor.getColumnIndex(KEY_TYPE)), cursor.getFloat(cursor.getColumnIndex(KEY_AMOUNT)));
            tagList.add(tag);
            Log.i("MYHERE", "HERE");
            cursor.moveToNext();
        }
        cursor.close();
        return tagList;
    }


    String[] getTagsStringList() {
        List<String> tagList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            String tag = new Tag(cursor.getString(cursor.getColumnIndex(KEY_TAG)),
                                    cursor.getString(cursor.getColumnIndex(KEY_TYPE)),
                                    cursor.getFloat(cursor.getColumnIndex(KEY_AMOUNT))).toString();
            tagList.add(tag);
            cursor.moveToNext();
        }
        cursor.close();
        return tagList.toArray(new String[tagList.size()]);
    }

}