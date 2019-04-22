package com.ghostman.musicplayer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ghostman.musicplayer.DragViewHelper.OnStartDragListener;
import com.ghostman.musicplayer.DragViewHelper.SimpleItemTouchHelperCallback;
import com.ghostman.musicplayer.Fragments.dummy.DummyContent;
import com.ghostman.musicplayer.RecyclerViewAdapter.AdapterSongQueue;

public class SongQueue extends AppCompatActivity implements OnStartDragListener {

    RecyclerView recyclerView;
    AdapterSongQueue madapter;

    ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_queue);

        register_ui_components();

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(madapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.song_queue_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.mi_savePlaylist_songQueueMenu) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New Playlist");
            final EditText pl_name = new EditText(this);
            pl_name.setHint("Enter Playlist Name");
            pl_name.setHintTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            pl_name.setTextColor(ContextCompat.getColor(this,android.R.color.black));
            pl_name.setSingleLine();

            FrameLayout frameLayout = new FrameLayout(this.getApplicationContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.dialog_margin);
            pl_name.setLayoutParams(params);
            frameLayout.addView(pl_name);

            builder.setView(frameLayout);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(SongQueue.this, "Playlist will be added soon...", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        } else if(id == R.id.mi_shuffle_songQueueMenu) {
            Toast.makeText(this, "UNDER CONSTRUCTION", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    void register_ui_components() {
        recyclerView = findViewById(R.id.rv_songQueue);
        madapter = new AdapterSongQueue(getApplicationContext(), DummyContent.ITEMS, this);
        recyclerView.setAdapter(madapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }
}
