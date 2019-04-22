package com.ghostman.musicplayer.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ghostman.musicplayer.Others.Functions;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerHelper.Artists;
import com.ghostman.musicplayer.PlayerHelper.Genres;
import com.ghostman.musicplayer.R;
import com.ghostman.musicplayer.RecyclerViewAdapter.AdapterArtist;
import com.ghostman.musicplayer.RecyclerViewAdapter.AdapterGenres;
import com.ghostman.musicplayer.RecyclerViewAdapter.TouchListener;
import com.ghostman.musicplayer.dummy.DummyContent;
import com.ghostman.musicplayer.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FragmentArtist extends Fragment {

    View rootView;
    RecyclerView recyclerView;
    public AdapterArtist adapterArtist;

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
    public FragmentArtist() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentArtist newInstance(int columnCount) {
        FragmentArtist fragment = new FragmentArtist();
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
        rootView = inflater.inflate(R.layout.fragment_artist, container, false);

        register_ui_components();
        set_listeners();


        return rootView;
    }

    void register_ui_components() {
        adapterArtist = new AdapterArtist(PlayerConstants.ARTIST_LIST, getContext());
        // Set the adapter
        if (rootView instanceof RecyclerView) {
            Context context = rootView.getContext();
            recyclerView = (RecyclerView) rootView;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(adapterArtist);
        }
    }

    void set_listeners() {
        recyclerView.addOnItemTouchListener(new TouchListener(getActivity(), recyclerView, new TouchListener.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                view.setSelected(true);
                Artists data = PlayerConstants.ARTIST_LIST.get(position);
                PlayerConstants.CURRENT_ARTIST_NAME = data.getArtist_name();

                PlayerConstants.ARTIST_ALBUM_LIST = Functions.listOfArtistsAlbums(rootView.getContext());

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, new FragmentAlbumsLayout()).addToBackStack("tag").commit();
            }


            @Override
            public void onLongItemClick(View view, int position) {
                //Snackbar.make(view,"ON LONG CLICK WORKING", Snackbar.LENGTH_SHORT)
                //        .setAction("ACTION",null).show();
            }
        }));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnListFragmentInteractionListener) {
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
}
