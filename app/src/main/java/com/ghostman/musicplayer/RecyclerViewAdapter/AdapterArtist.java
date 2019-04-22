package com.ghostman.musicplayer.RecyclerViewAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghostman.musicplayer.Fragments.FragmentArtist.OnListFragmentInteractionListener;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerHelper.Artists;
import com.ghostman.musicplayer.PlayerHelper.Genres;
import com.ghostman.musicplayer.R;
import com.ghostman.musicplayer.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AdapterArtist extends RecyclerView.Adapter<AdapterArtist.ViewHolder> {

    private ArrayList<Artists> arrayList;
    private Context mcontext;

    public AdapterArtist(ArrayList<Artists> listOfArtists, Context context) {
        arrayList = listOfArtists;
        mcontext = context;    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_artists, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.tvArtistName.setText(arrayList.get(position).getArtist_name());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvArtistName;

        public ViewHolder(View view) {
            super(view);
            tvArtistName = view.findViewById(R.id.tvArtistName_listArtists);
            //textNumberOfSongs = view.findViewById(R.id.genres_nos);
        }

    }
}
