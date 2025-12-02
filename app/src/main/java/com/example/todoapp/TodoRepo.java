package com.example.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TodoRepo extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Todo.db";
    public static final String TABLE_NAME = "todos";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_IS_SELECTED = "is_selected";
    public static final String COLUMN_CONTACT_NAME = "contact_name";
    public static final String COLUMN_CONTACT_PHONE = "contact_phone";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TEXT + " TEXT, " +
            COLUMN_IS_SELECTED + " INTEGER, " +
            COLUMN_CONTACT_NAME + " TEXT, " +
            COLUMN_CONTACT_PHONE + " TEXT" +
            ")";

    public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public TodoRepo(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }

    public long addNew(TodoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEXT, item.getText());
        values.put(COLUMN_IS_SELECTED, item.isSelected() ? 1 : 0);
        values.put(COLUMN_CONTACT_NAME, item.getContactName());
        values.put(COLUMN_CONTACT_PHONE, item.getContactPhone());
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public boolean update(TodoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEXT, item.getText());
        values.put(COLUMN_IS_SELECTED, item.isSelected() ? 1 : 0);
        values.put(COLUMN_CONTACT_NAME, item.getContactName());
        values.put(COLUMN_CONTACT_PHONE, item.getContactPhone());
        int rowAffected = db.update(TABLE_NAME, values, COLUMN_ID + "= ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
        return rowAffected > 0;
    }

    public boolean delete(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowAffected = db.delete(TABLE_NAME, COLUMN_ID + "= ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowAffected > 0;
    }

    public ArrayList<TodoItem> loadAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                COLUMN_ID,
                COLUMN_TEXT,
                COLUMN_IS_SELECTED,
                COLUMN_CONTACT_NAME,
                COLUMN_CONTACT_PHONE
        };

        Cursor cursor = db.query(TABLE_NAME, projection, null, null, null, null, null);
        ArrayList<TodoItem> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            String text = cursor.getString(1);
            boolean isSelected = cursor.getInt(2) == 1;
            String contactName = cursor.getString(3);
            String contactPhone = cursor.getString(4);

            TodoItem item = new TodoItem(text);
            item.setId(id);
            item.setSelected(isSelected);
            item.setContactName(contactName);
            item.setContactPhone(contactPhone);
            items.add(item);
        }
        cursor.close();
        db.close();
        return items;
    }

    public TodoItem getById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                COLUMN_ID,
                COLUMN_TEXT,
                COLUMN_IS_SELECTED,
                COLUMN_CONTACT_NAME,
                COLUMN_CONTACT_PHONE
        };

        Cursor cursor = db.query(TABLE_NAME, projection, COLUMN_ID + "= ?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            String text = cursor.getString(1);
            boolean isSelected = cursor.getInt(2) == 1;
            String contactName = cursor.getString(3);
            String contactPhone = cursor.getString(4);

            TodoItem item = new TodoItem(text);
            item.setId(id);
            item.setSelected(isSelected);
            item.setContactName(contactName);
            item.setContactPhone(contactPhone);

            cursor.close();
            db.close();
            return item;
        }
        cursor.close();
        db.close();
        return null;
    }
}
