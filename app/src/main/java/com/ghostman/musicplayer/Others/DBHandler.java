package com.ghostman.musicplayer.Others;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ghostman.musicplayer.PlayerHelper.Songs;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ghostman";

    // Table name
    private static final String TABLE_NAME = "favourites";

    // Table Columns
    private static final String SONG_ID = "song_id";
    private static final String SONG_NAME = "song_name";
    private static final String SONG_ALBUM = "song_album";
    private static final String SONG_ALBUM_ID = "song_album_id";
    private static final String SONG_ARTIST = "song_artist";
    private static final String SONG_COMPOSER = "song_composer";
    private static final String SONG_DURATION = "song_duration";
    private static final String SONG_DATA = "song_data";

    public DBHandler(Context context)   {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + SONG_ID + " LONG PRIMARY KEY," +
                SONG_NAME + " TEXT," + SONG_ALBUM + " TEXT," + SONG_ALBUM_ID + " LONG," +
                SONG_ARTIST + " TEXT," + SONG_COMPOSER + " TEXT," + SONG_DURATION + " LONG," +
                SONG_DATA + " LONG" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Creating table again
        onCreate(db);
    }

    // Adding new Row
    void addRow(Long song_id, String song_name, String album, Long album_id, String artist, String composer, Long duration, String data)   {
        SQLiteDatabase db = this.getWritableDatabase();

        //onUpgrade(db,1,1);

        ContentValues values = new ContentValues();
        values.put(SONG_ID, song_id);
        values.put(SONG_NAME, song_name);
        values.put(SONG_ALBUM, album);
        values.put(SONG_ALBUM_ID, album_id);
        values.put(SONG_ARTIST, artist);
        values.put(SONG_COMPOSER, composer);
        values.put(SONG_DURATION, duration);
        values.put(SONG_DATA, data);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Remove Row
    void removeRow(Long _id)    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, SONG_ID + "=" + _id, null);
        db.close();
    }

    // Reading table rows
    public Songs getSong(long id)   {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{  SONG_ID, SONG_NAME}, SONG_ID + "=?",
                new String[]{   String.valueOf(id)}, null, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        //Songs songs = new Songs(Long.parseLong(cursor.getString(0)), cursor.getString(1));

        return null;
    }

    // Getting all songs in favourite table
     public ArrayList<Songs> listOfFavouriteSongs() {
        ArrayList<Songs> fav_song_list = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst())    {
            do{
                Songs songData = new Songs();
                songData.setSong_id(Long.parseLong(cursor.getString(0)));
                songData.setSong_title(cursor.getString(1));
                songData.setSong_album(cursor.getString(2));
                songData.setSong_albumId(Long.parseLong(cursor.getString(3)));
                songData.setSong_artist(cursor.getString(4));
                songData.setSong_composer(cursor.getString(5));
                songData.setSong_duration(Long.parseLong(cursor.getString(6)));
                songData.setSong_path(cursor.getString(7));

                fav_song_list.add(songData);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return fav_song_list;
    }
}
