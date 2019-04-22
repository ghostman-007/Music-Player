package com.ghostman.musicplayer.RecyclerViewAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerHelper.Songs;
import com.ghostman.musicplayer.R;

import java.util.ArrayList;

public class AdapterSongAdd extends  RecyclerView.Adapter<AdapterSongAdd.ViewHolder> {

    private ArrayList<Songs> mSongsList;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private Context mcontext;

    public AdapterSongAdd(ArrayList<Songs> listOfSongs, Context context) {
        mSongsList = listOfSongs;
        mPref = context.getSharedPreferences("song", Context.MODE_PRIVATE);
        mcontext = context;
        mEditor = mPref.edit();

        for(Songs song : PlayerConstants.CURRENT_PLAYLIST_SONGS)    {
            for(Songs song1 : PlayerConstants.TEMP_SONG_LIST_ADD)   {
                if(song1.getSong_title().compareToIgnoreCase(song.getSong_title()) == 0)
                    song1.setSong_selected(true);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSongName, textViewArtistName;
        LinearLayout row;

        public ViewHolder(View view) {
            super(view);
            textViewSongName = (TextView)view.findViewById(R.id.pl_add_song_name);
            textViewArtistName = (TextView) view.findViewById(R.id.pl_add_artist_name);
            row = (LinearLayout)view.findViewById(R.id.pl_add_list_item_background);
        }
    }

    //Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new value
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_songs_add,parent,false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final AdapterSongAdd.ViewHolder holder, int position) {
        holder.textViewSongName.setText(mSongsList.get(position).getSong_title());
        holder.textViewArtistName.setText(mSongsList.get(position).getSong_artist());

        Log.e("selection", "" + mSongsList.get(position).isSong_selected());
        if(mSongsList.get(position).isSong_selected()) {
            holder.row.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else {
            holder.row.setBackgroundColor(Color.parseColor("#33691E"));
        }
    }


    public int setSelected(int pos, int noss) {
        try {
            if(mSongsList.get(pos).isSong_selected()) {
                mSongsList.get(pos).setSong_selected(false);
                noss = noss - 1;
            }
            else {
                mSongsList.get(pos).setSong_selected(true);
                noss = noss + 1;
            }
            notifyDataSetChanged();
        }catch (Exception e) { e.printStackTrace(); }

        return noss;
    }

    @Override
    public int getItemCount() {
        return mSongsList.size();
    }
}
