package com.ghostman.musicplayer.RecyclerViewAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghostman.musicplayer.PlayerHelper.Playlists;
import com.ghostman.musicplayer.R;

import java.util.ArrayList;

public class AdapterPlaylists extends RecyclerView.Adapter<AdapterPlaylists.ViewHolder> {

    private ArrayList<Playlists> mPlaylist;
    private Context mcontext;

    public AdapterPlaylists(ArrayList<Playlists> listofPlaylist, Context context)  {
        mPlaylist = listofPlaylist;
        mcontext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView textViewPlaylistName, textViewPlaylistNOS;

        public ViewHolder(View view) {
            super(view);

            textViewPlaylistName = view.findViewById(R.id.tvPlaylistName_listplaylists);
            textViewPlaylistNOS = view.findViewById(R.id.tvPlaylistNOS_listplaylists);
        }
    }

    @Override
    public AdapterPlaylists.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_playlists,parent,false);
        AdapterPlaylists.ViewHolder viewHolder = new AdapterPlaylists.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterPlaylists.ViewHolder holder, int position) {

        holder.textViewPlaylistName.setText(mPlaylist.get(position).getPlaylist_name());
        String string = mPlaylist.get(position).getPlaylist_nos() > 1 ?  + mPlaylist.get(position).getPlaylist_nos() + " songs" :  mPlaylist.get(position).getPlaylist_nos() + " song";
        holder.textViewPlaylistNOS.setText(string);
    }

    @Override
    public int getItemCount() {
        return mPlaylist.size();
    }

}
