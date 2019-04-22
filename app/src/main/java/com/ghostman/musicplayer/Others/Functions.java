package com.ghostman.musicplayer.Others;

import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.ghostman.musicplayer.PlayerHelper.Albums;
import com.ghostman.musicplayer.PlayerHelper.Artists;
import com.ghostman.musicplayer.PlayerHelper.Genres;
import com.ghostman.musicplayer.PlayerHelper.Playlists;
import com.ghostman.musicplayer.PlayerHelper.Songs;
import com.ghostman.musicplayer.R;

import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Functions {

    public static boolean isServiceRunning(String serviceName, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Songs> listOfSongs(Context context){
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC";
        Cursor cursor = context.getContentResolver().query(uri, null, MediaStore.Audio.Media.IS_MUSIC + "!= 0", null, sortOrder);
        ArrayList<Songs> listOfSongs = new ArrayList<>();
        if(cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Songs songs = new Songs();
                long _id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String composer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                songs.setSong_id(_id);
                songs.setSong_albumId(albumId);
                songs.setSong_path(path);
                songs.setSong_title(title);
                songs.setSong_artist(artist);
                songs.setSong_album(album);
                songs.setSong_composer(composer);
                songs.setSong_duration(duration);

                listOfSongs.add(songs);
            }
        }cursor.close();
        Log.d("listOfSongs", "SIZE: " + listOfSongs.size());
        return listOfSongs;
    }

    public static ArrayList<Albums> listOfAlbums(Context context) {
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String sortOrder = "LOWER (" + MediaStore.Audio.Albums.ALBUM + ") ASC";

        String album_id = MediaStore.Audio.Albums._ID;
        String album_name = MediaStore.Audio.Albums.ALBUM;
        String artist = MediaStore.Audio.Albums.ARTIST;
        String album_art = MediaStore.Audio.Albums.ALBUM_ART;
        String tracks = MediaStore.Audio.Albums.NUMBER_OF_SONGS;

        String[] columns = { album_id, album_name, artist, album_art, tracks };

        Cursor cursor = context.getContentResolver().query(uri, columns,null, null, sortOrder);
        ArrayList<Albums> listOfAlbums = new ArrayList<>();
        if(cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Albums albumsData = new Albums();
                Long _id = cursor.getLong(cursor.getColumnIndex(album_id));
                String _name = cursor.getString(cursor.getColumnIndex(album_name));
                String _artist = cursor.getString(cursor.getColumnIndex(artist));
                String _art = cursor.getString(cursor.getColumnIndex(album_art));
                int _noa = Integer.parseInt(cursor.getString(cursor.getColumnIndex(tracks)));

                albumsData.setAlbum_id(_id);
                albumsData.setAlbum_name(_name);
                albumsData.setArtist_name(_artist);
                albumsData.setAlbum_image(_art);
                albumsData.setNumber_of_Songs(_noa);

                listOfAlbums.add(albumsData);
            }
        }
        cursor.close();
        Log.d("ALBUM", "SIZE : " + listOfAlbums.size());
        return listOfAlbums;
    }

    public static ArrayList<Songs> listOfAlbumSongs(Context context)    {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC";
        Cursor cursor = context.getContentResolver().query(uri, null, /*MediaStore.Audio.Media.IS_MUSIC + "!= 0"*/ null, null, sortOrder);
        ArrayList<Songs> listOfAlbumSongs = new ArrayList<>();
        if(cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Songs songData = new Songs();
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                Long _id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String composer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                if(albumId == PlayerConstants.CURRENT_AlBUM_ID) {
                    songData.setSong_id(_id);
                    songData.setSong_title(title);
                    songData.setSong_album(album);
                    songData.setSong_artist(artist);
                    songData.setSong_duration(duration);
                    songData.setSong_path(data);
                    songData.setSong_albumId(albumId);
                    songData.setSong_composer(composer);
                    listOfAlbumSongs.add(songData);
                }
            }
        }
        cursor.close();
        Log.d("SIZE", "SIZE: " + listOfAlbumSongs.size());
        return listOfAlbumSongs;
    }

    public static ArrayList<Artists> listOfArtists(Context context)   {
        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String sortOrder = "LOWER (" + MediaStore.Audio.Artists.ARTIST + ") ASC";

        String artist_key = MediaStore.Audio.ArtistColumns.ARTIST_KEY;
        String artist_name = MediaStore.Audio.ArtistColumns.ARTIST;
        String tracks = MediaStore. Audio.ArtistColumns.NUMBER_OF_ALBUMS;

        String[] columns = { artist_key, artist_name, tracks };

        Cursor cursor = context.getContentResolver().query(uri, columns,null, null, sortOrder);
        ArrayList<Artists> listOfArtist = new ArrayList<>();

        if(cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Artists artistData = new Artists();
                Long _key = cursor.getLong(cursor.getColumnIndex(artist_key));
                String _name = cursor.getString(cursor.getColumnIndex(artist_name));
                int _not = Integer.parseInt(cursor.getString(cursor.getColumnIndex(tracks)));

                artistData.setArtist_id(_key);
                artistData.setArtist_name(_name);
                artistData.setArtist_not(_not);

                listOfArtist.add(artistData);

            }
        }
        cursor.close();
        Log.d("SIZE", "SIZE: " + listOfArtist.size());
        return listOfArtist;

    }

    public static ArrayList<Albums> listOfArtistsAlbums(Context context)   {
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String sortOrder = "LOWER (" + MediaStore.Audio.Albums.ALBUM + ") ASC";

        String album_id = MediaStore.Audio.Albums._ID;
        String album_name = MediaStore.Audio.Albums.ALBUM;
        String artist = MediaStore.Audio.Albums.ARTIST;
        String artist_id = MediaStore.Audio.ArtistColumns.ARTIST_KEY;
        String album_art = MediaStore.Audio.Albums.ALBUM_ART;
        String tracks = MediaStore. Audio.Albums.NUMBER_OF_SONGS;

        String[] columns = { album_id, album_name, artist, artist_id, album_art, tracks };

        Cursor cursor = context.getContentResolver().query(uri, columns,null, null, sortOrder);
        ArrayList<Albums> listOfArtistAlbums = new ArrayList<>();
        if(cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Albums albumsData = new Albums();
                Long _id = cursor.getLong(cursor.getColumnIndex(album_id));
                String _name = cursor.getString(cursor.getColumnIndex(album_name));
                String _artist = cursor.getString(cursor.getColumnIndex(artist));
                Long _artist_id = cursor.getLong(cursor.getColumnIndex(artist_id));
                String _art = cursor.getString(cursor.getColumnIndex(album_art));
                int _noa = Integer.parseInt(cursor.getString(cursor.getColumnIndex(tracks)));

                if(PlayerConstants.CURRENT_ARTIST_NAME.equalsIgnoreCase(_artist))
                {
                    albumsData.setAlbum_id(_id);
                    albumsData.setAlbum_name(_name);
                    albumsData.setArtist_name(_artist);
                    albumsData.setAlbum_image(_art);
                    albumsData.setNumber_of_Songs(_noa);

                    listOfArtistAlbums.add(albumsData);
                }
            }
        }
        cursor.close();
        Log.d("SIZE", "SIZE: " + listOfArtistAlbums.size());
        return listOfArtistAlbums;
    }

    public static ArrayList<Genres> listOfGenres(Context context)       {
        Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        String sortOrder = "LOWER (" + MediaStore.Audio.Genres.NAME + ") ASC";

        String genres_id = MediaStore.Audio.Genres._ID;
        String genres_name = MediaStore.Audio.Genres.NAME;
        String genres_number = MediaStore.Audio.Genres._COUNT;

        String[] columns = { genres_id, genres_name };

        Cursor cursor = context.getContentResolver().query(uri, columns,null, null, sortOrder);
        ArrayList<Genres> listOfGenres = new ArrayList<>();

        if(cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Genres genresData = new Genres();
                Long _id = cursor.getLong(cursor.getColumnIndex(genres_id));
                String _name = cursor.getString(cursor.getColumnIndex(genres_name));
                //Integer _nog = cursor.getInt(cursor.getColumnIndex(genres_number));

                Uri tempUri = MediaStore.Audio.Genres.Members.getContentUri("external", _id);

                Cursor tempCursor = context.getContentResolver().query(tempUri, null, null, null, null);

                if (tempCursor.getCount() != 0) {
                    genresData.setGenre_id(_id);
                    genresData.setGenre_name(_name);
                    //genresData.setGenreNumbers(_nog);

                    listOfGenres.add(genresData);
                }
                tempCursor.close();
            }
        }
        cursor.close();
        Log.d("SIZE", "SIZE: " + listOfGenres.size());
        return listOfGenres;
    }

    public static ArrayList<Songs> listOfGenreSongs(Context context)   {
        int index;
        Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;

        String genres_id = MediaStore.Audio.Genres._ID;
        String genres_name = MediaStore.Audio.Genres.NAME;
        //String genres_number = MediaStore.Audio.Genres._COUNT;

        String[] columns = { genres_id, genres_name};

        Cursor cursor = context.getContentResolver().query(uri, columns,null, null, null);
        ArrayList<Songs> listOfGenreSongs = new ArrayList<>();

        if(cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Genres genresData = new Genres();
                Long _id = cursor.getLong(cursor.getColumnIndex(genres_id));
                String _name = cursor.getString(cursor.getColumnIndex(genres_name));
                //int _nog = Integer.parseInt(cursor.getString(cursor.getColumnIndex(genres_number)));

                if (PlayerConstants.CURRENT_GENRE_ID == _id) {
                    Uri tempUri = MediaStore.Audio.Genres.Members.getContentUri("external", _id);

                    Cursor tempCursor = context.getContentResolver().query(tempUri, null, null, null, null);
                    //Toast.makeText(context.getApplicationContext(), "NOS = " + tempCursor.getCount(), Toast.LENGTH_SHORT).show();
                    //genresData.setGenreNumbers(tempCursor.getCount());

                    if (tempCursor != null && tempCursor.getCount() > 0) {
                        while (tempCursor.moveToNext()) {
                            Songs songData = new Songs();

                            String data = tempCursor.getString(tempCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                            Long song_id = tempCursor.getLong(tempCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                            String title = tempCursor.getString(tempCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                            String artist = tempCursor.getString(tempCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                            String album = tempCursor.getString(tempCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                            String composer = tempCursor.getString(tempCursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER));
                            long duration = tempCursor.getLong(tempCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                            long albumId = tempCursor.getLong(tempCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                            songData.setSong_id(song_id);
                            songData.setSong_title(title);
                            songData.setSong_album(album);
                            songData.setSong_artist(artist);
                            songData.setSong_duration(duration);
                            songData.setSong_path(data);
                            songData.setSong_albumId(albumId);
                            songData.setSong_composer(composer);

                            listOfGenreSongs.add(songData);
                        }
                    }
                    tempCursor.close();
                }
            }
        }
        cursor.close();
        Log.d("SIZE", "SIZE: " + listOfGenreSongs.size());
        return listOfGenreSongs;
    }

    public static ArrayList<Playlists> listOfPlaylists(Context context)  {
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String sortOrder = "LOWER (" + MediaStore.Audio.Playlists.NAME + ") ASC";

        String pl_id = MediaStore.Audio.Playlists._ID;
        String pl_name = MediaStore.Audio.Playlists.NAME;

        String[] columns = { pl_id, pl_name};

        Cursor cursor = context.getContentResolver().query(uri, columns,null, null, sortOrder);
        ArrayList<Playlists> listOfPlaylist = new ArrayList<>();

        if(cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Playlists plData = new Playlists();
                Long _id = cursor.getLong(cursor.getColumnIndex(pl_id));
                String _name = cursor.getString(cursor.getColumnIndex(pl_name));

                Uri tempUri = MediaStore.Audio.Playlists.Members.getContentUri("external", _id);
                Cursor tempCursor = context.getContentResolver().query(tempUri, null, null, null, null);

                plData.setPlaylist_id(_id);
                plData.setPlaylist_name(_name);
                plData.setPlaylist_nos(tempCursor.getCount());

                listOfPlaylist.add(plData);

                tempCursor.close();
            }
        }
        cursor.close();
        Log.d("SIZE", "SIZE: " + listOfPlaylist.size());
        return listOfPlaylist;
    }

    public static void listOfPlaylists(Context context, String name, String result)    {
        Uri uri = android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

        String pl_id = MediaStore.Audio.Playlists._ID;
        String pl_name = MediaStore.Audio.Playlists.NAME;
        String[] columns = { pl_id, pl_name};

        if(result.equalsIgnoreCase("add"))  {
            Log.d("TAG", "CREATING PLAYLIST: " + name);
            ContentValues v1 = new ContentValues();
            v1.put(MediaStore.Audio.Playlists.NAME, name);
            Uri new_pl = context.getContentResolver().insert(uri, v1);
            Log.d("TAG", "Added Playlist : " + new_pl);

            Toast.makeText(context.getApplicationContext(),"Playlist Added " + new_pl,Toast.LENGTH_SHORT).show();
        }else if (result.equalsIgnoreCase("remove")) {
            long playlistId = 0;
            Cursor cursor = context.getContentResolver().query(uri, columns,null, null, null);

            if(cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String _name = cursor.getString(cursor.getColumnIndex(pl_name));

                    if(_name.equalsIgnoreCase(name))    {
                        playlistId = cursor.getLong(cursor.getColumnIndex(pl_id));
                        break;
                    }
                }
            }
            cursor.close();

            if (playlistId != 0) {
                //Uri deleteUri = ContentUris.withAppendedId(uri, playlistId);
                Log.d("TAG", "Removing Existing Playlist: " + playlistId);
                //context.getContentResolver().delete(deleteUri, null, null);   //OR
                context.getContentResolver().delete(uri, "_id="+playlistId, null);

                Toast.makeText(context.getApplicationContext(),"Playlist Deleted",Toast.LENGTH_SHORT).show();
            }
        }else if(result.equalsIgnoreCase("rename")) {
            long playlistId = 0;
            Cursor cursor = context.getContentResolver().query(uri, columns,null, null, null);

            if(cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    playlistId = cursor.getLong(cursor.getColumnIndex(pl_id));

                    if(playlistId == PlayerConstants.CURRENT_PLAYLIST_ID)    {
                        playlistId = cursor.getLong(cursor.getColumnIndex(pl_id));
                        break;
                    }
                }
            }
            cursor.close();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Playlists.NAME, name);
            context.getContentResolver().update(uri, values, "_id="+playlistId, null);

            Toast.makeText(context.getApplicationContext(),"Playlist Renamed",Toast.LENGTH_SHORT).show();
        }
    }

    public static ArrayList<Songs> listOfPlaylistSongs(Context context, long PLAYLIST_ID) {
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String sortOrder = "LOWER (" + MediaStore.Audio.Playlists.NAME + ") ASC";

        String pl_id = MediaStore.Audio.Playlists._ID;
        String pl_name = MediaStore.Audio.Playlists.NAME;

        String[] columns = { pl_id, pl_name};

        Cursor cursor = context.getContentResolver().query(uri, columns,null, null, sortOrder);
        ArrayList<Songs> listOfPlaylistSongs = new ArrayList<>();

        if(cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Playlists plData = new Playlists();
                Long _id = cursor.getLong(cursor.getColumnIndex(pl_id));
                String _name = cursor.getString(cursor.getColumnIndex(pl_name));

                //if(PlayerConstants.CURRENT_PLAYLIST_ID == _id)  {
                if(PLAYLIST_ID == _id)    {
                    Uri tempUri = MediaStore.Audio.Playlists.Members.getContentUri("external", _id);
                    String sortOrder1 = "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC";

                    Cursor tempCursor = context.getContentResolver().query(tempUri, null, null, null, sortOrder1);
                    //Toast.makeText(context.getApplicationContext(), "NOS = " + tempCursor.getCount(), Toast.LENGTH_SHORT).show();

                    if (tempCursor != null && tempCursor.getCount() > 0) {
                        while (tempCursor.moveToNext()) {
                            Songs songData = new Songs();

                            String data = tempCursor.getString(tempCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                            Long song_id = tempCursor.getLong(tempCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID));
                            String title = tempCursor.getString(tempCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                            String artist = tempCursor.getString(tempCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                            String album = tempCursor.getString(tempCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                            String composer = tempCursor.getString(tempCursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER));
                            long duration = tempCursor.getLong(tempCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                            long albumId = tempCursor.getLong(tempCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                            songData.setSong_id(song_id);
                            songData.setSong_title(title);
                            songData.setSong_album(album);
                            songData.setSong_artist(artist);
                            songData.setSong_duration(duration);
                            songData.setSong_path(data);
                            songData.setSong_albumId(albumId);
                            songData.setSong_composer(composer);

                            listOfPlaylistSongs.add(songData);
                        }
                    }
                    tempCursor.close();
                }
            }
        }
        cursor.close();
        Log.d("SIZE", "SIZE: " + listOfPlaylistSongs.size());
        return listOfPlaylistSongs;

    }

    public static void listOfPlaylistSongsAdd(Context context, long PLAYLIST_ID, ArrayList<Songs> songsToAdd_remove, boolean add_remove)  {
        Uri uri =  android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

        String pl_id = MediaStore.Audio.Playlists._ID;
        String pl_name = MediaStore.Audio.Playlists.NAME;
        String[] columns = { pl_id, pl_name};
        Cursor cursor = context.getContentResolver().query(uri, columns,null, null, null);

        if(cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Long _id = cursor.getLong(cursor.getColumnIndex(pl_id));

                if(PLAYLIST_ID == _id)  {
                    Uri tempUri = android.provider.MediaStore.Audio.Playlists.Members.getContentUri("external", _id);
                    if(add_remove)  {
                        int songOrder = 1;
                        for (Songs song : songsToAdd_remove) {
                            ContentValues songValues = new ContentValues();
                            songValues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, song.getSong_id());
                            songValues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, songOrder);
                            context.getContentResolver().insert(tempUri, songValues);
                            songOrder++;
                        }
                    }
                    else    {
                        for (Songs song : songsToAdd_remove)
                            context.getContentResolver().delete(tempUri, "audio_id="+song.getSong_id(), null );
                    }
                }
            }
        }
        cursor.close();
    }

    public static ArrayList<Songs> addFavSongs(Context context, long song_id, boolean ADD_REMOVE) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC";
        Cursor cursor = context.getContentResolver().query(uri, null,null,null, sortOrder);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Songs songData = new Songs();
                long _id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String composer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                if (_id == song_id) {

                    if(ADD_REMOVE)  {
                        songData.setSong_Favorite(true);
                        Log.d("INSERT : ","Inserting Row...");
                        DBHandler db = new DBHandler(context);
                        db.addRow(_id, title, album, albumId,artist, composer, duration, data);
                        Toast.makeText(context.getApplicationContext(),"Song Added",Toast.LENGTH_SHORT).show();

                        PlayerConstants.FAV_LIST = db.listOfFavouriteSongs();
                        db.close();

                    } else  {
                        songData.setSong_Favorite(false);
                        Log.d("REMOVE : ","Removing Row...");
                        DBHandler db = new DBHandler(context);
                        db.removeRow(_id);
                        Toast.makeText(context.getApplicationContext(),"Song Removed",Toast.LENGTH_LONG).show();

                        PlayerConstants.FAV_LIST = db.listOfFavouriteSongs();
                        db.close();
                    }
                }
            }
        }
        cursor.close();
        Log.d("SIZE", "SIZE: " + PlayerConstants.FAV_LIST.size());
        return PlayerConstants.FAV_LIST;
    }

    public static void deleteSong(Context context, Long SONG_ID)   {
        ArrayList<Songs> songToDelete = new ArrayList<>();

        for (Songs s : PlayerConstants.FAV_LIST)    {
            if(s.getSong_id() == SONG_ID)   {
                Functions.addFavSongs(context, SONG_ID, false);
                break;
            }
        }

        for (Playlists playlists : PlayerConstants.PLAYLIST_LIST)   {
            ArrayList<Songs> Playlist_songs = Functions.listOfPlaylistSongs(context, playlists.getPlaylist_id());
            for(Songs s : Playlist_songs)   {
                if(s.getSong_id() == SONG_ID)   {
                    Functions.listOfPlaylistSongsAdd(context, playlists.getPlaylist_id() , songToDelete  ,false );
//                    Toast.makeText(context.getApplicationContext(),playlists.getPlaylistName() + s.getTitle(),Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }

        for (Songs s : PlayerConstants.SONGS_LIST)    {
            if(s.getSong_id() == SONG_ID)   {
                songToDelete.add(s);
                File file = new File(s.getSong_path());
                file.delete();

                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String sortOrder = "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC";
                Cursor cursor = context.getContentResolver().query(uri, null, MediaStore.Audio.Media.IS_MUSIC + "!= 0", null, sortOrder);
                if(cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        long _id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                        if(_id == SONG_ID)  {
                            Uri tempUri = android.provider.MediaStore.Audio.Media.getContentUri("external");
                            context.getContentResolver().delete(tempUri, "_id="+_id, null );
                        }
                    }
                }cursor.close();

                Toast.makeText(context.getApplicationContext(),"Song Deleted Successfully...",Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }


    // Method to convert milliseconds to min and sec
    public static String getDuration(long millis)
    {
        if(millis<0) {
            return null;
            //throw new IllegalArgumentException("Duration must be greater than zero");
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder stringBuilder = new StringBuilder(8);
        stringBuilder.append(minutes < 10 ? "0" + minutes : minutes);
        stringBuilder.append(":");
        stringBuilder.append(seconds < 10 ? "0" + seconds : seconds);
        //stringBuilder.append("sec");
        return stringBuilder.toString();
    }


    public static Bitmap getAlbumart(Context context, Long album_id){
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try{
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null){
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
                pfd = null;
                fd = null;
            }
        } catch(Error ee){ Log.d("ERROR EE","ERROR"); }
        catch (Exception e) { Log.d("EXCEPTION","ERROR"); }
        return bm;
    }

    public static Bitmap getDefaultAlbumArt(Context context){
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try{
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_songs_24px, options);
        } catch(Error ee){}
        catch (Exception e) {}
        return bm;
    }
}
