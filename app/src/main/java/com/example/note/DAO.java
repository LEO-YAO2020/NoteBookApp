package com.example.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
/*
 *
 * Name: Leo Yao
 * ID: 16287261
 *
 * */

//Designed to add, delete and modify the database
public class DAO {
    SQLiteOpenHelper sqLiteOpenHelper;
    SQLiteDatabase sqLiteDatabase;



    private static final String[] columns ={
            NoteDatabase.ID,
            NoteDatabase.PATH,
            NoteDatabase.CONTENT,
            NoteDatabase.TIME,
    };

    public DAO(Context context) {
        sqLiteOpenHelper = new NoteDatabase(context);
    }
    public void Write(){
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
    }

    public void close(){
        sqLiteOpenHelper.close();
    }

    // add new note to database
    public NoteObj add(NoteObj noteObj){
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDatabase.PATH,noteObj.getPath());
        contentValues.put(NoteDatabase.CONTENT,noteObj.getContent());
        contentValues.put(NoteDatabase.TIME,noteObj.getTime());
        long insert = sqLiteDatabase.insert(NoteDatabase.TABLE_NAME, null, contentValues);
        noteObj.setId(insert);
        return noteObj;
    }
    // get all notes
    public List<NoteObj> getAll(){

        Cursor cursor = sqLiteDatabase.query(NoteDatabase.TABLE_NAME,columns,null,null,null,null,NoteDatabase.TIME+" DESC");
        List<NoteObj> noteObjs = new ArrayList<>();
        while (cursor.moveToNext()){
            NoteObj noteObj=new NoteObj();
            noteObj.setId(cursor.getLong(cursor.getColumnIndex( NoteDatabase.ID)));
            if (cursor.getString(cursor.getColumnIndex(NoteDatabase.PATH))!=null) {
                noteObj.setPath(cursor.getString(cursor.getColumnIndex(NoteDatabase.PATH)));
            }
            noteObj.setContent(cursor.getString(cursor.getColumnIndex(NoteDatabase.CONTENT)));
            noteObj.setTime(cursor.getString(cursor.getColumnIndex(NoteDatabase.TIME)));
            noteObjs.add(noteObj);
        }
        return noteObjs;
    }

    //update note if note has changed
    public int update(NoteObj noteObj){
        String[] idS = {
                String.valueOf(noteObj.getId())
        };
        ContentValues contentValues = new ContentValues();
        if (noteObj.getPath() !=null) {
            contentValues.put(NoteDatabase.PATH, noteObj.getPath());
        }
        contentValues.put(NoteDatabase.CONTENT,noteObj.getContent());
        contentValues.put(NoteDatabase.TIME,noteObj.getTime());
        //update row
        return sqLiteDatabase.update(NoteDatabase.TABLE_NAME,contentValues,NoteDatabase.ID+"=?",
                idS);
    }
    //remove note from database
    public void remove(NoteObj noteObj){
        //remove a row
        sqLiteDatabase.delete(NoteDatabase.TABLE_NAME,NoteDatabase.ID+" = "+noteObj.getId(),null);
    }
    // remove all notes
    public void removeAll(){
        sqLiteDatabase.delete(NoteDatabase.TABLE_NAME,null,null);
    }
}
