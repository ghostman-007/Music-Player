package com.ghostman.musicplayer.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ghostman.musicplayer.Others.DBHandler;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.R;

public class FragmentFavourites extends Fragment {

    View rootView;

    public FragmentFavourites() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favourites, container, false);

//        PlayerConstants.ALBUMS_SONG_LIST = PlayerConstants.FAV_LIST;
        DBHandler db = new DBHandler(rootView.getContext());
        PlayerConstants.ALBUMS_SONG_LIST = db.listOfFavouriteSongs();

        PlayerConstants.FAVOURITES_FRAG = true;

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,new FragmentSongsLayout()).commit();
        return rootView;
    }

    @Override
    public void onResume() {
        PlayerConstants.ALBUMS_SONG_LIST = PlayerConstants.FAV_LIST;

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,new FragmentSongsLayout()).commit();
        super.onResume();
    }
}
