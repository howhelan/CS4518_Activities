package com.example.hugh.hw3;

/**
 * Created by Hugh on 2/8/17.
 */

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

public class DBHelper extends SQLiteOpenHelper{

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "activities.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ActivitySchema.Entry.TABLE_NAME + "(" +
                ActivitySchema.Entry.ACTIVITY + " TEXT" + "," +
                ActivitySchema.Entry.TIME + "  INTEGER" + "," +
                ActivitySchema.Entry.CONFIDENCE + " INTEGER" + ")"
        );
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        onCreate(db);
    }

}
