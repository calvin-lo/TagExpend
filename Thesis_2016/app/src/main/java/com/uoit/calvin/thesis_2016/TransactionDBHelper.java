package com.uoit.calvin.thesis_2016;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

class TransactionDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "transDB";
    private static final String TABLE_NAME = "transactions";
    private Context context;

    private static final String KEY_ID = "id";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TIMESTAMPS = "timestamps";
    private static final String KEY_YEAR = "year";
    private static final String KEY_MONTH = "month";
    private static final String KEY_DAY = "day";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_USER = "user";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_GENERAL = "general";
    private static final String KEY_NAME = "name";
    private static final String KEY_COLOR = "color";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME+ " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_MESSAGE + " TEXT," +
                    KEY_TIMESTAMPS + " TEXT," +
                    KEY_GENERAL + " TEXT," +
                    KEY_LOCATION + " TEXT," +
                    KEY_CATEGORY + " TEXT," +
                    KEY_AMOUNT + " REAL, " +
                    KEY_USER + " TEXT," +
                    KEY_NAME + " TEXT, " +
                    KEY_YEAR + " INTEGER," +
                    KEY_MONTH + " INTEGER," +
                    KEY_DAY + " INTEGER," +
                    KEY_COLOR + " INTEGER" + ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    TransactionDBHelper(Context context) {
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

    boolean addTransactions(Transaction trans) {
        SQLiteDatabase db = this.getWritableDatabase();

        Helper helper = new Helper(context);

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE, trans.getMessage());
        values.put(KEY_TIMESTAMPS, trans.getTimestamp());
        values.put(KEY_AMOUNT, trans.getAmount());
        values.put(KEY_GENERAL, trans.getGeneral());
        values.put(KEY_LOCATION, trans.getLocation());
        values.put(KEY_CATEGORY, trans.getCategory());
        values.put(KEY_YEAR, helper.getYear(trans.getDate()));
        values.put(KEY_MONTH, helper.getMonth(trans.getDate()));
        values.put(KEY_DAY, helper.getDay(trans.getDate()));
        values.put(KEY_USER, trans.getUser().getUsername());
        values.put(KEY_NAME, trans.getUser().getDisplayName());
        values.put(KEY_COLOR, trans.getColor());

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close();
        return true;
    }

    public void updateTransaction(Transaction trans) {
        SQLiteDatabase db = this.getWritableDatabase();

        Helper helper = new Helper(context);

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE, trans.getMessage());
        values.put(KEY_TIMESTAMPS, trans.getTimestamp());
        values.put(KEY_AMOUNT, trans.getAmount());
        values.put(KEY_GENERAL, trans.getGeneral());
        values.put(KEY_LOCATION, trans.getLocation());
        values.put(KEY_CATEGORY, trans.getCategory());
        values.put(KEY_YEAR, helper.getYear(trans.getDate()));
        values.put(KEY_MONTH, helper.getMonth(trans.getDate()));
        values.put(KEY_DAY, helper.getDay(trans.getDate()));
        values.put(KEY_USER, trans.getUser().getUsername());
        values.put(KEY_NAME, trans.getUser().getDisplayName());
        values.put(KEY_COLOR, trans.getColor());

        db.update(TABLE_NAME, values,  KEY_ID+"="+ trans.getId(), null);
        db.close();
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getDisplayName());
        db.update(TABLE_NAME, values, KEY_USER + "='" + user.getUsername() + "'", null);
        db.close();

    }

    void deleteTransactions(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = " + id, null);
        db.close();
    }

    void clearUser(String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_USER+ " = ? ", new String[] {user});
        db.close();
    }

    List<Transaction> getAllData(String user) {
        List<Transaction> transList = new ArrayList<>();
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
            Transaction trans = getTransaction(cursor);
            transList.add(trans);
            cursor.moveToNext();
        }

        cursor.close();

        return transList;
    }

    Transaction getTransByID(long id, String user) {
        Transaction trans = new Transaction(context);
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery;
        if (user.equals("*")) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY_ID + "='" + id + "'";
        } else {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY_ID + "='" + id + "'" + " AND " + KEY_USER + "='" + user + "'";
        }
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){

            trans = getTransaction(cursor);
            cursor.moveToNext();
        }
        cursor.close();
        return trans;
    }

    List<Transaction> getTransByTag(String tag, String user) {
        List<Transaction> transList = new ArrayList<>();
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
            String s = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
            Helper helper = new Helper(context);
            List<String> tagStrList = (helper.tagListToString(helper.parseTag(s)));
            if (tagStrList.contains(tag)) {
                Transaction trans = getTransaction(cursor);
                transList.add(trans);
            }
            cursor.moveToNext();
        }

        cursor.close();
        return transList;
    }

    List<Transaction> getTransByTime(int year, int month, String user) {
        List<Transaction> transList = new ArrayList<>();
        String selectQuery = selectQuery = "SELECT  * FROM " + TABLE_NAME;;
        if (user.equals("*") && year == 0 && month == 0) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME;
        } else if (!user.equals("*") && year == 0 && month == 0) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY_USER + "='" + user + "'";
        } else if (user.equals("*") && year != 0 && month != 0) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME +
                            " WHERE " + KEY_YEAR + "=" + year +
                            " AND " + KEY_MONTH + "=" + month;
        } else if (!user.equals("*") && year != 0 && month != 0) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME +
                    " WHERE " + KEY_YEAR + "=" + year +
                    " AND " + KEY_MONTH + "=" + month +
                    " AND " + KEY_USER + "='" + user + "'";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Transaction trans = getTransaction(cursor);
            transList.add(trans);
            cursor.moveToNext();
        }

        cursor.close();
        return transList;
    }


    List<Tag> getTransTagsByTime(int year, int month, String user) {
        List<Tag> tagList = new ArrayList<>();
        List<String> tagStrList = new ArrayList<>();
        String selectQuery = selectQuery = "SELECT  * FROM " + TABLE_NAME;;
        if (user.equals("*") && year == 0 && month == 0) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME;
        } else if (!user.equals("*") && year == 0 && month == 0) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY_USER + "='" + user + "'";
        } else if (user.equals("*") && year != 0 && month != 0) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME +
                    " WHERE " + KEY_YEAR + "=" + year +
                    " AND " + KEY_MONTH + "=" + month;
        } else if (!user.equals("*") && year != 0 && month != 0) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME +
                    " WHERE " + KEY_YEAR + "=" + year +
                    " AND " + KEY_MONTH + "=" + month +
                    " AND " + KEY_USER + "='" + user + "'";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            //List<Tag> temp = new ArrayList<>();
            //temp.addAll(tagList);
            //temp.addAll(new Helper(context).parseTag(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE))));
            //tagList = temp;

            List<Tag> temp = new Helper(context).parseTag(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE)));
            for (Tag t : temp) {
                if(!tagStrList.contains(t.toString())) {
                    t.setAmount(cursor.getFloat(cursor.getColumnIndex(KEY_AMOUNT)));
                    tagList.add(t);
                    tagStrList.add(t.toString());
                } else {
                    int index = tagStrList.indexOf(t.toString());
                    tagList.get(index).setAmount(t.getAmount() + cursor.getFloat(cursor.getColumnIndex(KEY_AMOUNT)));
                }
            }
            cursor.moveToNext();
        }

        cursor.close();
        return tagList;
    }

    String[] getUser() {
        List<String> userList = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + KEY_USER + " FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String s = cursor.getString(cursor.getColumnIndex(KEY_USER));
            userList.add(s);
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        SharedPreferences sharedpreferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        userList.remove(context.getResources().getString(R.string.user_default));
        return userList.toArray(new String[userList.size()]);
    }

    private Transaction getTransaction(Cursor cursor) {
        Transaction trans = new Transaction(context);

        trans.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        trans.setMessage(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE)));
        trans.setTags(new Helper(context).parseTag(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE))));
        trans.setGeneral(cursor.getString(cursor.getColumnIndex(KEY_GENERAL)));
        trans.setLocation(cursor.getString(cursor.getColumnIndex(KEY_LOCATION)));
        trans.setCategory(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));
        trans.setTimestamp(cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMPS)));
        trans.setAmount(cursor.getFloat(cursor.getColumnIndex(KEY_AMOUNT)));
        User user = new UserDBHelper(context).getUserByUsername(cursor.getString(cursor.getColumnIndex(KEY_USER)));
        trans.setUser(user);
        trans.setColor(cursor.getInt(cursor.getColumnIndex(KEY_COLOR)));

        return trans;
    }


}
