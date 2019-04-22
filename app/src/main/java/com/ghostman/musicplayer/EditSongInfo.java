package com.ghostman.musicplayer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ghostman.musicplayer.Others.Functions;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerHelper.Songs;

import static com.ghostman.musicplayer.Fragments.FragmentSongs.EXTRA_MESSAGE;

public class EditSongInfo extends AppCompatActivity {
    Intent intent;
    Long SONG_ID;

    ImageView IV_IMAGE;
    EditText ET_TITLE, ET_ALBUM, ET_ARTIST, ET_LYRICS;
    Button B_SAVE, B_CANCEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_track);

        intent = getIntent();
        SONG_ID = intent.getLongExtra(EXTRA_MESSAGE,0);

        final Drawable arrow = getResources().getDrawable(R.drawable.ic_arrow_back_24px);
        getSupportActionBar().setHomeAsUpIndicator(arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("EDIT SONG DETAILS");

        register_ui_components();
        set_listeners();
        fill_song_details();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);

        return super.onOptionsItemSelected(item);
    }

    void register_ui_components()   {
        IV_IMAGE = findViewById(R.id.song_image);
        ET_TITLE = findViewById(R.id.song_title);
        ET_ALBUM = findViewById(R.id.song_album);
        ET_ARTIST = findViewById(R.id.song_artist);
        //ET_LYRICS = (EditText)findViewById(R.id.song_lyrics);
        B_SAVE = findViewById(R.id.et_save);
        B_CANCEL = findViewById(R.id.et_cancel);
    }

    void set_listeners()    {

        B_SAVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, MediaStore.Audio.Media.IS_MUSIC + "!= 0", null, null);
                if(cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        long _id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                        if(_id == SONG_ID)  {
                            Uri tempUri = android.provider.MediaStore.Audio.Media.getContentUri("external");
                            ContentValues songValues = new ContentValues();
                            songValues.put(MediaStore.Audio.Media.TITLE, ET_TITLE.getText().toString());
                            songValues.put(MediaStore.Audio.Media.ALBUM, ET_ALBUM.getText().toString());
                            songValues.put(MediaStore.Audio.Media.ARTIST, ET_ARTIST.getText().toString());

                            getApplicationContext().getContentResolver().update(tempUri, songValues, "_id="+SONG_ID,null);
                        }
                    }
                }cursor.close();

                PlayerConstants.SONGS_LIST = Functions.listOfSongs(getApplicationContext());
                PlayerConstants.ALBUMS_LIST = Functions.listOfAlbums(getApplicationContext());
                PlayerConstants.ARTIST_LIST = Functions.listOfArtists(getApplicationContext());
                PlayerConstants.GENRES_LIST = Functions.listOfGenres(getApplicationContext());

                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

        B_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
    }

    void fill_song_details()    {
        for(Songs s : PlayerConstants.SONGS_LIST)   {
            if(s.getSong_id() == SONG_ID)   {
                Bitmap albumArt = Functions.getAlbumart(this, s.getSong_albumId());
                if(albumArt != null)
                    IV_IMAGE.setBackground(new BitmapDrawable(albumArt));

                ET_TITLE.setText(s.getSong_title());
                ET_ALBUM.setText(s.getSong_album());
                ET_ARTIST.setText(s.getSong_artist());
                //ET_LYRICS.setText(s.getLyrics);
            }
        }
    }
}
