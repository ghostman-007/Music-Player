package com.ghostman.musicplayer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ghostman.musicplayer.Others.Functions;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerHelper.Songs;
import com.ghostman.musicplayer.RecyclerViewAdapter.AdapterSongAdd;
import com.ghostman.musicplayer.RecyclerViewAdapter.TouchListener;

import java.util.ArrayList;

public class AddListSongs extends AppCompatActivity {

    MenuItem BTN_DONE;

    public AdapterSongAdd mAdapter;
    private RecyclerView RECYCLER_VIEW;
    private int noss = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_list_songs);

        final Drawable arrow = getResources().getDrawable(R.drawable.ic_arrow_back_24px);
        getSupportActionBar().setHomeAsUpIndicator(arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Songs");

        register_ui_components();
        set_listeners();
        PlayerConstants.TEMP_SONG_LIST_ADD = Functions.listOfSongs(getApplicationContext());
        mAdapter = new AdapterSongAdd(PlayerConstants.TEMP_SONG_LIST_ADD, getApplicationContext());
        RECYCLER_VIEW.setAdapter(mAdapter);
        RECYCLER_VIEW.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public void onBackPressed() {
        for(Songs songs : PlayerConstants.TEMP_SONG_LIST_ADD)
            songs.setSong_selected(false);

        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_list_songs_menu, menu);

        BTN_DONE = menu.findItem(R.id.button_done);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //handle arrow click
        //if(id == android.R.id.home) {

       if(id == R.id.button_done) {
           ArrayList<Songs> songsToAdd = new ArrayList<>();
           ArrayList<Songs> songsToRemove = new ArrayList<>();
           ArrayList<Songs> songsToAddUnique = new ArrayList<>();
           boolean flag = false;
           for (Songs songs : PlayerConstants.TEMP_SONG_LIST_ADD) {
               if (songs.isSong_selected())
                   songsToAdd.add(songs);
           }

           for (Songs song : songsToAdd) {
               for (Songs song1 : PlayerConstants.CURRENT_PLAYLIST_SONGS) {
                   if (song.getSong_id() == song1.getSong_id()) {
                       flag = true;
                       break;
                   }
               }
               if (!flag)
                   songsToAddUnique.add(song);

               flag = false;
           }

           for (Songs song : PlayerConstants.CURRENT_PLAYLIST_SONGS) {
               for (Songs song1 : songsToAdd) {
                   if (song.getSong_id() == song1.getSong_id())
                       flag = true;
               }
               if (!flag) {
                   songsToRemove.add(song);
               }

               flag = false;
           }

           Functions.listOfPlaylistSongsAdd(getApplicationContext(), PlayerConstants.CURRENT_PLAYLIST_ID, songsToAddUnique, true);
           Functions.listOfPlaylistSongsAdd(getApplicationContext(), PlayerConstants.CURRENT_PLAYLIST_ID, songsToRemove, false);

           String toast_add, toast_remove;
           if (songsToAddUnique.size() > 0) {
               toast_add = songsToAddUnique.size() > 1 ? songsToAddUnique.size() + " songs added" : songsToAddUnique.size() + " song added";
               if(songsToRemove.size() > 0)
                   Toast.makeText(getApplicationContext(), toast_add + ", " + songsToRemove.size() + " removed.", Toast.LENGTH_SHORT).show();
               else
                   Toast.makeText(getApplicationContext(), toast_add, Toast.LENGTH_SHORT).show();
           } else if (songsToAddUnique.size() == 0) {
               toast_remove = songsToRemove.size() > 1 ? songsToRemove.size() + " songs removed" : songsToRemove.size() + " song removed";
               if (songsToRemove.size() > 0)
                   Toast.makeText(getApplicationContext(), toast_remove, Toast.LENGTH_SHORT).show();
               else {
                   toast_add = songsToAddUnique.size() > 1 ?  "Songs already present." : "Song already present.";
                   Toast.makeText(getApplicationContext(),toast_add , Toast.LENGTH_SHORT).show();
               }
           }
       }

       for(Songs songs : PlayerConstants.TEMP_SONG_LIST_ADD)
           songs.setSong_selected(false);

       Intent i = new Intent(this,MainActivity.class);
       startActivity(i);

       return super.onOptionsItemSelected(item);
    }

    private void register_ui_components() {
        RECYCLER_VIEW = (RecyclerView) findViewById(R.id.recycler_view_add_songs);
    }

    private void set_listeners() {
        RECYCLER_VIEW.addOnItemTouchListener(new TouchListener(getApplicationContext(),RECYCLER_VIEW, new TouchListener.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                noss = mAdapter.setSelected(position, noss);

                /*Songs data = PlayerConstants.TEMP_SONG_LIST_ADD.get(position);
                PlayerConstants.CURRENT_SONG_ID = data.getSong_id();*/

                if(noss > 0)
                    getSupportActionBar().setTitle("Song Selected : " + noss);
                else
                    getSupportActionBar().setTitle("Select Songs");
            }

            @Override
            public void onLongItemClick(View view, int position) {}
        }));
    }

}
