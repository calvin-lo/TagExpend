package com.uoit.calvin.assigment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ProductDBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "productDB";
    private static final String TABLE_NAME = "Product";

    private static final String PRODUCT_ID = "productId";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String PRICE = "price";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    PRODUCT_ID + " INTEGER PRIMARY KEY," +
                    NAME + " TEXT," +
                    DESCRIPTION + " TEXT," +
                    PRICE + " REAL" + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    ProductDBHelper(Context context) {
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

    // Querying the database, finding all products
    List<Product> getAllProducts() {
        List<Product> productsList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            int productID = cursor.getInt(cursor.getColumnIndex(PRODUCT_ID));
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
            float price = cursor.getFloat(cursor.getColumnIndex(PRICE));
            Product product = new Product(productID, name, description, price);
            productsList.add(product);
            cursor.moveToNext();
        }

        cursor.close();
        return productsList;
    }

    // Inserting a new product into the database

    public boolean addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PRODUCT_ID, product.getProductID());
        values.put(NAME, product.getName());
        values.put(DESCRIPTION, product.getDescription());
        values.put(PRICE, product.getPrice());

        db.insert(TABLE_NAME, null, values);
        db.close();
        return true;
    }

    // Deleting a product from the database

    void deleteProduct(int productID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, PRODUCT_ID + " = " + productID, null);
        db.close();
    }

}
