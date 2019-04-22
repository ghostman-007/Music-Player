package com.ghostman.musicplayer.RecyclerViewAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghostman.musicplayer.PlayerHelper.Genres;
import com.ghostman.musicplayer.R;

import java.util.ArrayList;


public class AdapterGenres extends RecyclerView.Adapter<AdapterGenres.ViewHolder> {
    private ArrayList<Genres> mGenresList;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private Context mcontext;

    public AdapterGenres(ArrayList<Genres> listOfGenres, Context context) {
        mGenresList = listOfGenres;
        mPref = context.getSharedPreferences("song", Context.MODE_PRIVATE);
        mcontext = context;
        mEditor = mPref.edit();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGenreName, textNumberOfSongs;

        public ViewHolder(View view) {
            super(view);
            textViewGenreName = view.findViewById(R.id.tvGenreName_listgenres);
            //textNumberOfSongs = view.findViewById(R.id.genres_nos);
        }
    }

    //Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new value
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_genres,parent,false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AdapterGenres.ViewHolder holder, int position) {
        holder.textViewGenreName.setText(mGenresList.get(position).getGenre_name());
        //String string = mGenresList.get(position).getGenreNumbers() > 1 ?  + mGenresList.get(position).getGenreNumbers() + " songs" :  mGenresList.get(position).getGenreNumbers() + " song";
        //holder.textNumberOfSongs.setText(string);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mGenresList.size();
    }
}
