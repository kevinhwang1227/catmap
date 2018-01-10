package com.example.user.catmap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by user on 2018-01-10.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Cats.db";
    private SQLiteDatabase database;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String TABLE_NAME = "cat_table";
    public static final String CATNAME = "name";
    //public static final String CATIMG = "img";
    //public static final String CATINFO = "info";
    public static final String CATLAT = "lat";
    public static final String CATLNG = "lng";
    public static final String ID = "ID";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CATNAME + " TEXT, " + CATLAT + " REAL, " + CATLNG + " REAL);";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldV, int newV) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertRecord(CatListDB cat) {
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATNAME, cat.getCats().get("name"));
        //contentValues.put(CATIMG, DbBitmapUtility.getBytes(cat.getimage()));
        //contentValues.put(CATINFO, cat.getCats().get("info"));
        contentValues.put(CATLAT, cat.getlat());
        contentValues.put(CATLNG, cat.getlng());
        database.insert(TABLE_NAME,null,contentValues);
        database.close();
    }

    public void updateRecord(CatListDB cat) {
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATNAME, cat.getCats().get("name"));
        //contentValues.put(CATIMG, DbBitmapUtility.getBytes(cat.getimage()));
        //contentValues.put(CATINFO, cat.getCats().get("info"));
        contentValues.put(CATLAT, cat.getlat());
        contentValues.put(CATLNG, cat.getlng());
        database.update(TABLE_NAME, contentValues, ID + " = ?", new String[]{cat.getID()} );
        database.close();
    }

    public void deleteRecord(CatListDB cat) {
        database = this.getReadableDatabase();
        database.execSQL("delete from " + TABLE_NAME + " where " + ID + " = '" + cat.getID() + "'");
        database.close();
    }

    public ArrayList<CatListDB> getAllRecords() {
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,null,null,null,null,null,null,null);
        ArrayList<CatListDB> cats = new ArrayList<>();
        CatListDB cat;
        if (cursor.getCount() > 0) {
            for (int i=0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                cat = new CatListDB();
                cat.setID(cursor.getString(0));
                HashMap<String,String> nameinfo = new HashMap<>();
                nameinfo.put("name",cursor.getString(1));
                //nameinfo.put("info",cursor.getString(3));
                //cat.setCats(nameinfo);
                //Bitmap image = DbBitmapUtility.getImage(cursor.getBlob(2));
                //cat.setimage(image);
                cat.setlat(cursor.getFloat(2));
                cat.setlng(cursor.getFloat(3));
                cats.add(cat);
            }
        }
        cursor.close();
        database.close();
        return cats;
    }

}

