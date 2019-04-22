package com.ghostman.musicplayer.RecyclerViewAdapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghostman.musicplayer.Fragments.FragmentAlbums.OnListFragmentInteractionListener;
import com.ghostman.musicplayer.Fragments.dummy.DummyContent.DummyItem;
import com.ghostman.musicplayer.PlayerHelper.Albums;
import com.ghostman.musicplayer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterAlbums extends RecyclerView.Adapter<AdapterAlbums.ViewHolder> {


    private ArrayList<Albums> mAlbumList;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private Context mcontext;

    public AdapterAlbums(ArrayList<Albums> listOfAlbums, Context context) {
        mAlbumList = listOfAlbums;
        mPref = context.getSharedPreferences("song", Context.MODE_PRIVATE);
        mcontext = context;
        mEditor = mPref.edit();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAlbumName, textViewArtist, textNumberOfSongs;
        ImageView imageViewAlbumImage;

        public ViewHolder(View view) {
            super(view);

            textViewAlbumName = view.findViewById(R.id.tvAlbumName_listAlbums);
            textViewArtist = view.findViewById(R.id.tvArtistName_listAlbums);
            textNumberOfSongs = view.findViewById(R.id.tvNOS_listAlbums);
            imageViewAlbumImage = view.findViewById(R.id.ivAlbumImage_listAlbums);
        }
    }

    //Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new value
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_albums,parent,false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final AdapterAlbums.ViewHolder holder, final int position) {
        holder.textViewAlbumName.setText(mAlbumList.get(position).getAlbum_name());
        holder.textViewArtist.setText(mAlbumList.get(position).getArtist_name());
        String string = mAlbumList.get(position).getNumber_of_Songs() > 1 ?  + mAlbumList.get(position).getNumber_of_Songs() + " songs" :  mAlbumList.get(position).getNumber_of_Songs() + " song";
        holder.textNumberOfSongs.setText(string);

        final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, mAlbumList.get(position).getAlbum_id());
        // ▼ Debugging ▼ alt 31
        // holder.imageViewAlbumImage.setBackground(null);
        Picasso.get()
                .load(uri)
                //.placeholder(R.drawable.ic_headset_48px)
                //.error(R.drawable.ic_album)
                .into(holder.imageViewAlbumImage);
    }

    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }
}
