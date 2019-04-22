package com.ghostman.musicplayer.RecyclerViewAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghostman.musicplayer.Others.Functions;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerHelper.Songs;
import com.ghostman.musicplayer.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private ArrayList<Songs> mSongsList;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private Context context;

    public Adapter(ArrayList<Songs> listOfSongs, Context context) {
        mSongsList = listOfSongs;
        mPref = context.getSharedPreferences("song", Context.MODE_PRIVATE);
        mEditor = mPref.edit();
        this.context = context;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView song_name, artist_name, duration;
        LinearLayout row;
        //ImageButton IB_EXTRA_ITEMS;
        //ImageView song_image;

        private ViewHolder(View view) {
            super(view);
            song_name = view.findViewById(R.id.tvSongName_listSongs);
            artist_name = view.findViewById(R.id.tvArtistName_listSongs);
            duration = view.findViewById(R.id.tvDuration_listSongs);
            row = view.findViewById(R.id.list_item_background_listSongs);
            //song_image = (ImageView)view.findViewById(R.id.song_list_song_image);
            //IB_EXTRA_ITEMS = (ImageButton)view.findViewById(R.id.extra_items);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // set the view's size, margins, paddings and layout parameters
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_songs,parent,false);

        return  new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.song_name.setText(mSongsList.get(position).getSong_title());
        holder.artist_name.setText(mSongsList.get(position).getSong_artist());
        holder.duration.setText(Functions.getDuration(mSongsList.get(position).getSong_duration()));

        /*final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, mSongsList.get(position).getAlbumId());
        Picasso.with(context)
                .load(uri)
                .into(holder.song_image);

        //StringBuilder stringBuilder = new StringBuilder();
        //stringBuilder.append("• ");
        //stringBuilder.append(UtilFunctions.getDuration(mSongsList.get(position).getDuration()));
        //holder.duration.setText(stringBuilder);

        /*holder.IB_EXTRA_ITEMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                //popupMenu.getMenuInflater().inflate(R.menu.song_list_extras, popupMenu.getMenu());
                popupMenu.inflate(R.menu.song_list_extras);


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())   {
                            case R.id.edit_song_info:
                                Toast.makeText(context.getApplicationContext(),"WORKING",Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        */

        for (Songs s : mSongsList)
            s.setSong_selected(false);

        for ( Songs s : mSongsList)   {
            if(PlayerConstants.CURRENT_SONG_ID == s.getSong_id())   {
                s.setSong_selected(true);
            }
        }

        //Log.e("selection", "" + mSongsList.get(position).isSelected());
        if(mSongsList.get(position).isSong_selected()) {
            holder.row.setBackgroundColor(context.getResources().getColor(R.color.colorBackground));
        } else {
            holder.row.setBackgroundColor(context.getResources().getColor(R.color.colorContent));
        }
    }

    public void setSelected(int pos) {
        try {
            if(mSongsList.size()>1) {
                mSongsList.get(mPref.getInt("position",0)).setSong_selected(false);
                mEditor.putInt("position", pos);
                mEditor.commit();
            }
            mSongsList.get(pos).setSong_selected(true);
            notifyDataSetChanged();
        }catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public int getItemCount() {
        return mSongsList.size();
    }



    /* ----------- FOR SEARCH ----------------
    public void filterList(ArrayList<Songs> filterSongs) {
        PlayerConstants.CURRENT_SONGS_LIST = filterSongs;
        notifyDataSetChanged();
    }*/

}
