package com.ghostman.musicplayer.RecyclerViewAdapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghostman.musicplayer.Others.Functions;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerHelper.Songs;
import com.ghostman.musicplayer.R;

import java.util.ArrayList;

public class AdapterSongs extends RecyclerView.Adapter<AdapterSongs.ViewHolder> {

    private final ArrayList<Songs> songs_list;
    private Context context;

    public AdapterSongs(ArrayList<Songs> items, Context context) {
        songs_list = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_songs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.songName.setText(songs_list.get(position).getSong_title());
        holder.artistName.setText(songs_list.get(position).getSong_artist());
        holder.duration.setText(Functions.getDuration(songs_list.get(position).getSong_duration()));
        if(songs_list.get(position).getSong_id() == PlayerConstants.CURRENT_SONG_ID)
            holder.row.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackground));
        else
            holder.row.setBackgroundColor(ContextCompat.getColor(context, R.color.colorContent));
    }

    @Override
    public int getItemCount() {
        return songs_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView songName, artistName, duration;
        final ConstraintLayout row;

        public ViewHolder(View view) {
            super(view);
            songName = view.findViewById(R.id.tvSongName_listSongs);
            artistName = view.findViewById(R.id.tvArtistName_listSongs);
            duration = view.findViewById(R.id.tvDuration_listSongs);
            row = view.findViewById(R.id.row_listSongs);
        }
    }

}
