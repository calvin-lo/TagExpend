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
    private static final String KEY_NAME = "name";
    private static final String KEY_USER = "user";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_TAG + " TEXT" + "UNIQUE," +
                    KEY_AMOUNT + " REAL," +
                    KEY_USER + " TEXT," +
                    KEY_NAME + " TEXT," +
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
            values.put(KEY_TAG, tag.getTitle());
            values.put(KEY_AMOUNT, tag.getAmount());
            values.put(KEY_TYPE, tag.getType());
            values.put(KEY_NAME, tag.getName());
            values.put(KEY_USER, tag.getUser());

            // Inserting Row
            db.insert(TABLE_NAME, null, values);

        } else {
            float amount = getAmount(tag.getTitle(), tag.getUser()) + tag.getAmount();
            values.put(KEY_AMOUNT, amount);
            db.update(TABLE_NAME, values, KEY_TAG + " =?", new String[] {tag.getTitle()});
        }
        db.close();
        return true;
    }

    private float getAmount(String tag, String user) {
        SQLiteDatabase database = this.getReadableDatabase();

        String query;
        if (user.equals("*")) {
                query = "SELECT " + KEY_AMOUNT + " FROM " + TABLE_NAME + " WHERE " + KEY_TAG + "='" + tag + "'";
        } else {
            query = "SELECT " + KEY_AMOUNT + " FROM " + TABLE_NAME + " WHERE " + KEY_TAG + "='" + tag + "'" + " AND " + KEY_USER + "='" + user +"'";
        }
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

        float amount = getAmount(tag.getTitle(), tag.getUser()) - tag.getAmount();
        values.put(KEY_AMOUNT, amount);
        db.update(TABLE_NAME, values, KEY_TAG + " =?", new String[] {tag.getTitle()});
        db.close();
    }

    private boolean checkDuplicate(Tag tag) {
        return (new ArrayList<>(Arrays.asList(getTagsStringList(tag.getUser())))).contains(tag.toString());
    }

    private boolean checkDuplicateAll(Tag tag, List<Tag> tagList) {
        List<String> tagStrList = new ArrayList<>();
        for (Tag t : tagList) {
            tagStrList.add(t.toString());
        }
        return (tagStrList.contains(tag.toString()));
    }

    boolean deleteTag(String tag) {
        SQLiteDatabase db = this.getWritableDatabase();
        String type = String.valueOf(tag.charAt(0));
        tag = tag.substring(1,tag.length());
        db.delete(TABLE_NAME, KEY_TAG + " = ? AND " + KEY_TYPE + " = ? ", new String[] {tag, type});
        db.close();
        return true;
    }

    boolean clearUser(String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_NAME + " = ? ", new String[] {user});
        db.close();
        return true;
    }


    public void updateUser(String newUser, String oldUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, newUser);
        db.update(TABLE_NAME, values, KEY_NAME + "='" + oldUser + "'", null);
        db.close();

    }

    List<Tag> getTagsList(String type, String user) {
        List<Tag> tagList = new ArrayList<>();
        String selectQuery;

        if (type.equals("*") && user.equals("*")) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME;
        } else if (!type.equals("*") && user.equals("*")) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY_TYPE + "='" + type + "'";
        }
        else if (type.equals("*") && !user.equals("*")) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY_USER + "='" + user +"'";
        } else {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY_TYPE + "='" + type + "'" + " AND " + KEY_USER + "='" + user +"'";
        }


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            Tag tag = new Tag(cursor.getString(cursor.getColumnIndex(KEY_TAG)), cursor.getString(cursor.getColumnIndex(KEY_TYPE)), cursor.getFloat(cursor.getColumnIndex(KEY_AMOUNT)));
            tag.setUser(cursor.getString(cursor.getColumnIndex(KEY_USER)));
            tag.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            if (!checkDuplicateAll(tag, tagList)) {
                tagList.add(tag);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return tagList;
    }


    String[] getTagsStringList(String user) {
        List<String> tagList = new ArrayList<>();
        String selectQuery;
        if (user.equals("*")) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME;
        } else {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY_USER + "='" + user + "'";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            Tag tag = new Tag(cursor.getString(cursor.getColumnIndex(KEY_TAG)),
                                    cursor.getString(cursor.getColumnIndex(KEY_TYPE)),
                                    cursor.getFloat(cursor.getColumnIndex(KEY_AMOUNT)));
            tag.setUser(cursor.getString(cursor.getColumnIndex(KEY_USER)));
            tag.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));

            String tagStr = tag.toString();
            tagList.add(tagStr);
            cursor.moveToNext();
        }
        cursor.close();
        return tagList.toArray(new String[tagList.size()]);
    }

}