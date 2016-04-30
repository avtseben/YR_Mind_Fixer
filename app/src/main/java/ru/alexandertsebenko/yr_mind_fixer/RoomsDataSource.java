package ru.alexandertsebenko.yr_mind_fixer;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class RoomsDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_ROOM };
    public RoomsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }
    public Room createRoom(String textNote) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ROOM, textNote);
        long insertId = database.insert(MySQLiteHelper.TABLE_ROOMS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ROOMS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Room newRoom = cursorToRoom(cursor);
        cursor.close();
        return newRoom;
    }

    public void deleteRoom(Room textNote) {
        long id = textNote.getId();
        System.out.println("Room deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_ROOMS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }
    public void deleteRoomByText(String textNote) {
        System.out.println("Room deleted with text: " + textNote);
        database.delete(MySQLiteHelper.TABLE_ROOMS, MySQLiteHelper.COLUMN_ROOM
                + " = " + textNote, null);
    }
    public void deleteRoomByID(long _id) {
        System.out.println("Room deleted with id: " + _id);
        database.delete(MySQLiteHelper.TABLE_ROOMS, MySQLiteHelper.COLUMN_ID
                + " = " + _id, null);
    }
    public List<Room> getAllRooms() {
        List<Room> textNotes = new ArrayList<Room>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ROOMS,
                allColumns, null, null, null, null, "_id");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Room textNote = cursorToRoom(cursor);
            textNotes.add(textNote);
            cursor.moveToNext();
        }
        cursor.close();
        return textNotes;
    }

    private Room cursorToRoom(Cursor cursor) {
        Room room = new Room();
        room.setId(cursor.getLong(0));
        room.setRoom(cursor.getString(1));
        return room;
    }
}
