package com.uoit.calvin.thesis_2016;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.List;

public class UserDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "userDB";
    private static final String TABLE_NAME = "users";
    private Context context;

    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_DISPLAYNAME = "displayName";
    private static final String KEY_ProfilePicture = "profilePicture";
    private static final String KEY_ProfileImageUrl = "profileImageUrl";
    private static final String KEY_COUNT = "count";
    private static final String KEY_SINCE = "since";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME+ " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_USERNAME + " TEXT UNIQUE," +
                    KEY_DISPLAYNAME + " TEXT," +
                    KEY_ProfilePicture+ " REAL," +
                    KEY_ProfileImageUrl + " TEXT," +
                    KEY_COUNT + " INTEGER," +
                    KEY_SINCE + " INTEGER" + ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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

    boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (!checkDuplicate(user)) {
            ContentValues values = new ContentValues();
            values.put(KEY_DISPLAYNAME, user.getDisplayName());
            values.put(KEY_USERNAME, user.getUsername());
            values.put(KEY_ProfilePicture, user.getProfileImage());
            values.put(KEY_ProfileImageUrl, user.getProfileImageUrl());
            values.put(KEY_COUNT, user.getCount());
            values.put(KEY_SINCE, user.getSinceID());

            saveImage(user);

            // Inserting Row
            db.insert(TABLE_NAME, null, values);
        } else{
            updateUser(user);
        }
        db.close();
        return true;
    }

    void deleteTUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_USERNAME + " = '" + username +"'", null);
        db.close();
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DISPLAYNAME, user.getDisplayName());
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_ProfilePicture, user.getProfileImage());
        values.put(KEY_ProfileImageUrl, user.getProfileImageUrl());
        values.put(KEY_COUNT, user.getCount());
        values.put(KEY_SINCE, user.getSinceID());

        saveImage(user);

        db.update(TABLE_NAME, values,  KEY_USERNAME+"='"+ user.getUsername() +"'", null);

        db.close();
    }

    List<User> getAllUser() {
        List<User> userList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            User user = getUser(cursor);
            userList.add(user);
            cursor.moveToNext();
        }

        cursor.close();
        return userList;
    }

    User getUserByUsername(String username) {

        User user = new User();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            if(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)).equals(username)) {
                user = getUser(cursor);
                return user;
            }
            cursor.moveToNext();
        }
        cursor.close();

        return user;
    }

    private User getUser(Cursor cursor) {
        User user = new User(context,
                                cursor.getString(cursor.getColumnIndex(KEY_DISPLAYNAME)),
                                cursor.getString(cursor.getColumnIndex(KEY_USERNAME)
                                ));
        user.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        user.setCount(cursor.getInt(cursor.getColumnIndex(KEY_COUNT)));
        user.setProfileImage(cursor.getInt(cursor.getColumnIndex(KEY_ProfilePicture)));
        user.setProfileImageUrl(cursor.getString(cursor.getColumnIndex(KEY_ProfileImageUrl)));
        user.setSinceID(cursor.getLong(cursor.getColumnIndex(KEY_SINCE)));
        return user;
    }


    public boolean checkDuplicate(User user) {

        List<String> userList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            userList.add(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
            cursor.moveToNext();
        }

        cursor.close();

        return (userList.contains(user.getUsername()));
    }


    private void saveImage(User user) {
        Helper helper = new Helper(context);
        helper.runDownloadImageTask(user);
    }





}
