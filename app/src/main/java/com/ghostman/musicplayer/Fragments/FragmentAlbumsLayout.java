package com.ghostman.musicplayer.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ghostman.musicplayer.Others.Functions;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerHelper.Albums;
import com.ghostman.musicplayer.R;
import com.ghostman.musicplayer.RecyclerViewAdapter.AdapterAlbums;
import com.ghostman.musicplayer.RecyclerViewAdapter.TouchListener;

public class FragmentAlbumsLayout extends Fragment {
    View rootView;
    public RecyclerView RECYCLER_VIEW;
    public AdapterAlbums mAdapter;

    private FragmentTransaction fragmentTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_albums, container, false);
        register_ui_components();
        set_listeners();
        mAdapter = new AdapterAlbums(PlayerConstants.ARTIST_ALBUM_LIST, getActivity());
        RECYCLER_VIEW.setAdapter(mAdapter);
        RECYCLER_VIEW.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }


    private void register_ui_components() {
        RECYCLER_VIEW = rootView.findViewById(R.id.recycler_view_albums);
    }

    private void set_listeners() {
        RECYCLER_VIEW.addOnItemTouchListener(new TouchListener(getActivity().getApplicationContext(), RECYCLER_VIEW, new TouchListener.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                view.setSelected(true);

                Albums data = PlayerConstants.ARTIST_ALBUM_LIST.get(position);
                PlayerConstants.CURRENT_AlBUM_ID = data.getAlbum_id();

                PlayerConstants.ALBUMS_SONG_LIST = Functions.listOfAlbumSongs(getActivity().getApplicationContext());

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,new FragmentSongsLayout()).addToBackStack("tag").commit();

                Log.d("TAG", "TAG Tapped INOUT(OUT)");
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

}
