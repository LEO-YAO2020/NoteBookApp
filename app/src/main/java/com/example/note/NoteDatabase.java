package com.example.note;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/*
 *
 * Name: Leo Yao
 * ID: 16287261
 *
 * */
public class NoteDatabase extends SQLiteOpenHelper {

    public static final String TABLE_NAME="notes";
    public static final String CONTENT="content";
    public static final String PATH="path";
    public static final String ID="id";
    public static final String TIME="time";



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PATH + " TEXT," + CONTENT + " TEXT NOT NULL," + TIME + " TEXT NOT NULL)");
    }

    public NoteDatabase(@Nullable Context context) {
        super(context, "notes", null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
