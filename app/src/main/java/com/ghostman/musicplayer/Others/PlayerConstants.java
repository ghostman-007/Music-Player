package com.ghostman.musicplayer.Others;

import android.os.Handler;

import com.ghostman.musicplayer.PlayerHelper.Albums;
import com.ghostman.musicplayer.PlayerHelper.Artists;
import com.ghostman.musicplayer.PlayerHelper.Genres;
import com.ghostman.musicplayer.PlayerHelper.Playlists;
import com.ghostman.musicplayer.PlayerHelper.Songs;

import java.util.ArrayList;


public class PlayerConstants {
    public static ArrayList<Songs> SONGS_LIST = new ArrayList<>();
    public static ArrayList<Albums> ALBUMS_LIST = new ArrayList<>();
    public static ArrayList<Songs> ALBUMS_SONG_LIST = new ArrayList<>();
    public static ArrayList<Artists> ARTIST_LIST = new ArrayList<>();
    public static ArrayList<Albums> ARTIST_ALBUM_LIST = new ArrayList<>();
    public static ArrayList<Genres> GENRES_LIST = new ArrayList<>();
    public static ArrayList<Playlists> PLAYLIST_LIST = new ArrayList<>();
    public static ArrayList<Songs> CURRENT_PLAYLIST_SONGS = new ArrayList<>();
    public static ArrayList<Songs> TEMP_SONG_LIST_ADD = new ArrayList<>();
    public static ArrayList<Songs> FAV_LIST = new ArrayList<>();
    public static ArrayList<Songs> CURRENT_SONGS_LIST = new ArrayList<>();

    public static long CURRENT_SONG_ID = 0;
    public static int CURRENT_SONG_POSITION = 0;
    public static long CURRENT_AlBUM_ID = 0;
    public static long CURRENT_PLAYLIST_ID = 0;
    public static String CURRENT_ARTIST_NAME;
    public static long CURRENT_GENRE_ID = 0;

    public static boolean SONG_PAUSED = true;
    public static boolean PLAYLIST_ADD_SONGS = false;
    public static boolean FAVOURITES_FRAG = false;
    public static boolean FAVOURITE = false;
    public static int REPEAT = 2;
    public static boolean SHUFFLE = false;

    public static int SLEEP_TIMER = -1;

    // Definition in PlayerService.class
    public static Handler PLAY_HANDLER;
    public static Handler PAUSE_HANDLER;
    public static Handler STOP_HANDLER;
    public static Handler EXIT_HANDLER;
    public static Handler SEEK_START_HANDLER;
    public static Handler SEEK_TO_HANDLER;
    // Definition in MainActivity.class
    public static Handler SEEK_BAR_HANDLER;
}
