package ru.alexandertsebenko.yr_mind_fixer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_TEXT_NOTES = "text_notes";
    public static final String TABLE_TEXT_NOTES_IN_ROOM = "text_notes_in_room";
    public static final String TABLE_ROOMS = "rooms";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEXT_NOTE = "text_note";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_ROOM_ID = "room_id";
    public static final String COLUMN_TEXT_NOTE_ID = "text_note_id";

    private static final String DATABASE_NAME = "yr.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TEXT_NOTES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TEXT_NOTE
            + " text not null);";
    private static final String CREATE_TABLE_TNINROOM = "create table "
            + TABLE_TEXT_NOTES_IN_ROOM + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TEXT_NOTE_ID
            + " integer, " + COLUMN_ROOM_ID
            + " integer);";
    private static final String CREATE_TABLE_ROOM = "create table "
            + TABLE_ROOMS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_ROOM
            + " text not null);";
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        database.execSQL(CREATE_TABLE_TNINROOM);
        database.execSQL(CREATE_TABLE_ROOM);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEXT_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEXT_NOTES_IN_ROOM);
        onCreate(db);
    }
}
