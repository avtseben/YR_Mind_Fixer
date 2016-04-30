package ru.alexandertsebenko.yr_mind_fixer;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TextNoteDataSource {
    // Database fields
    private SQLiteDatabase database;//обращение к глобальной SQLite БД на Android
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,//массив в которов все имена колонок таблицы
            MySQLiteHelper.COLUMN_TEXT_NOTE };
    public TextNoteDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }
    public TextNote createTextNote(String textNote) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TEXT_NOTE, textNote);
        long insertId = database.insert(MySQLiteHelper.TABLE_TEXT_NOTES, null,//Вставляем в таблицу
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TEXT_NOTES,//имя таблицы в Srting
                allColumns,/*какие колонки выбирать аналог select * from*/ MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        TextNote newTextNote = cursorToTextNote(cursor);
        cursor.close();
        return newTextNote;
    }

    public void deleteTextNote(TextNote textNote) {
        long id = textNote.getId();
        System.out.println("TextNote deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_TEXT_NOTES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }
    public void deleteTextNoteByText(String textNote) {
        System.out.println("TextNote deleted with text: " + textNote);
        database.delete(MySQLiteHelper.TABLE_TEXT_NOTES, MySQLiteHelper.COLUMN_TEXT_NOTE
                + " = " + textNote, null);
    }
    public void deleteTextNoteByID(long _id) {
        System.out.println("TextNote deleted with id: " + _id);
        database.delete(MySQLiteHelper.TABLE_TEXT_NOTES, MySQLiteHelper.COLUMN_ID
                + " = " + _id, null);
    }
    public List<TextNote> getAllTextNotes() {
        List<TextNote> textNotes = new ArrayList<TextNote>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_TEXT_NOTES,
                allColumns, null, null, null, null, "_id" + " DESC");//Выборка всех колонок в обратном порядке TODO: убрать из hardcode

        cursor.moveToFirst();//cursor это очень похоже на result set
        while (!cursor.isAfterLast()) {
            TextNote textNote = cursorToTextNote(cursor);
            textNotes.add(textNote);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return textNotes;
    }

    private TextNote cursorToTextNote(Cursor cursor) {
        TextNote textNote = new TextNote();
        textNote.setId(cursor.getLong(0));
        textNote.setTextNote(cursor.getString(1));
        return textNote;
    }
}
