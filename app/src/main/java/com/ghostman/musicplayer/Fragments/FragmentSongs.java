package com.ghostman.musicplayer.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ghostman.musicplayer.EditSongInfo;
import com.ghostman.musicplayer.Fragments.dummy.DummyContent.DummyItem;
import com.ghostman.musicplayer.MainActivity;
import com.ghostman.musicplayer.Others.Functions;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerService;
import com.ghostman.musicplayer.R;
import com.ghostman.musicplayer.RecyclerViewAdapter.AdapterSongs;
import com.ghostman.musicplayer.RecyclerViewAdapter.TouchListener;

import static com.ghostman.musicplayer.MainActivity.EXTRA_MESSAGE;

/**
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FragmentSongs extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    public static AdapterSongs adapterSongs;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentSongs() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentSongs newInstance(int columnCount) {
        FragmentSongs fragment = new FragmentSongs();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_songs, container, false);

        register_ui_components();
        set_listeners();

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);/*
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }

    private void register_ui_components() {
        // Set the adapter
        adapterSongs = new AdapterSongs(PlayerConstants.SONGS_LIST, getContext());

        if (rootView instanceof RecyclerView) {
            Context context = rootView.getContext();
            recyclerView = (RecyclerView) rootView;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            // To add divider between recycler view items
            //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

            recyclerView.setAdapter(adapterSongs);
        }
    }

    private void set_listeners() {
        recyclerView.addOnItemTouchListener(new TouchListener(getActivity(), recyclerView, new TouchListener.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PlayerConstants.SONG_PAUSED = false;
                PlayerConstants.CURRENT_SONG_ID = PlayerConstants.SONGS_LIST.get(position).getSong_id();
                PlayerConstants.CURRENT_SONG_POSITION = 0;

                boolean isServiceRunning = Functions.isServiceRunning(PlayerService.class.getName(),getContext());
                if (!isServiceRunning) {
                    Intent i = new Intent(getContext(),PlayerService.class);
                    i.putExtra(EXTRA_MESSAGE , true);
                    getActivity().startService(i);
                } else
                    PlayerConstants.PLAY_HANDLER.sendMessage(PlayerConstants.PLAY_HANDLER.obtainMessage());

                adapterSongs.notifyDataSetChanged();
                MainActivity.updateUI();
            }

            @Override
            public void onLongItemClick(View view, int position) {

                //startActivity(new Intent(getActivity(),EditSongInfo.class));

                /*
                Snackbar.make(view,"ON LONG CLICK WORKING",Snackbar.LENGTH_SHORT)
                        .setAction("ACTION",null).show();
            */
            }
        }));
    }
}
