package com.ghostman.musicplayer.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ghostman.musicplayer.Fragments.dummy.DummyContent.DummyItem;
import com.ghostman.musicplayer.Others.Functions;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerHelper.Genres;
import com.ghostman.musicplayer.R;
import com.ghostman.musicplayer.RecyclerViewAdapter.AdapterGenres;
import com.ghostman.musicplayer.RecyclerViewAdapter.TouchListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FragmentGenres extends Fragment {

    View rootView;
    RecyclerView recyclerView;
    public AdapterGenres adapterGenres;

    private FragmentTransaction fragmentTransaction;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentGenres() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentGenres newInstance(int columnCount) {
        FragmentGenres fragment = new FragmentGenres();
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
        rootView = inflater.inflate(R.layout.fragment_genres, container, false);

        register_ui_components();
        set_listeners();

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }*/
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

    void register_ui_components() {
        adapterGenres = new AdapterGenres(PlayerConstants.GENRES_LIST, getContext());

        // Set the adapter
        if (rootView instanceof RecyclerView) {
            Context context = rootView.getContext();
            recyclerView = (RecyclerView) rootView;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(adapterGenres);
        }
    }

    void set_listeners() {
        recyclerView.addOnItemTouchListener(new TouchListener(getActivity(), recyclerView, new TouchListener.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                view.setSelected(true);

                Genres data = PlayerConstants.GENRES_LIST.get(position);
                PlayerConstants.CURRENT_GENRE_ID = data.getGenre_id();

                PlayerConstants.ALBUMS_SONG_LIST = Functions.listOfGenreSongs(getActivity().getApplicationContext());

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,new FragmentSongsLayout()).addToBackStack("tag").commit();
            }


            @Override
            public void onLongItemClick(View view, int position) {
                //Snackbar.make(view,"ON LONG CLICK WORKING", Snackbar.LENGTH_SHORT)
                  //      .setAction("ACTION",null).show();
            }
        }));
    }
}
