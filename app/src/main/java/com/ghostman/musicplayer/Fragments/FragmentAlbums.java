package com.ghostman.musicplayer.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ghostman.musicplayer.Fragments.dummy.DummyContent;
import com.ghostman.musicplayer.Others.Functions;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerHelper.Albums;
import com.ghostman.musicplayer.R;
import com.ghostman.musicplayer.RecyclerViewAdapter.AdapterAlbums;
import com.ghostman.musicplayer.RecyclerViewAdapter.TouchListener;

public class FragmentAlbums extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private AdapterAlbums adapterAlbums;

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
    public FragmentAlbums() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentAlbums newInstance(int columnCount) {
        FragmentAlbums fragment = new FragmentAlbums();
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

        rootView = inflater.inflate(R.layout.fragment_albums, container, false);
        register_ui_components();
        set_listeners();
        adapterAlbums = new AdapterAlbums(PlayerConstants.ALBUMS_LIST, getActivity());
        recyclerView.setAdapter(adapterAlbums);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //PlayerConstants.FAVOURITES_FRAG = false;


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
        void onListFragmentInteraction(DummyContent.DummyItem item);
    }

    private void register_ui_components() {
        // Set the adapter
        if (rootView instanceof RecyclerView) {
            Context context = rootView.getContext();
            recyclerView = (RecyclerView) rootView;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(adapterAlbums);
        }
    }

    private void set_listeners() {
       recyclerView.addOnItemTouchListener(new TouchListener(getActivity(), recyclerView, new TouchListener.ClickListener() {
           @Override
           public void onItemClick(View view, int position) {
               /*Snackbar.make(view,"ON CLICK WORKING", Snackbar.LENGTH_SHORT)
                       .setAction("ACTION",null).show();
*/
               view.setSelected(true);

               Albums data = PlayerConstants.ALBUMS_LIST.get(position);
               PlayerConstants.CURRENT_AlBUM_ID = data.getAlbum_id();

               PlayerConstants.ALBUMS_SONG_LIST = Functions.listOfAlbumSongs(getActivity().getApplicationContext());

               FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
               fragmentTransaction = fragmentManager.beginTransaction();
               fragmentTransaction.replace(R.id.main_frame,new FragmentSongsLayout()).addToBackStack("tag").commit();

               Log.d("TAG", "TAG Tapped INOUT(OUT)");
           }

           @Override
           public void onLongItemClick(View view, int position) {
/*               Snackbar.make(view,"ON LONG CLICK WORKING", Snackbar.LENGTH_SHORT)
                       .setAction("ACTION",null).show();
    */
           }
       }));
    }
}
