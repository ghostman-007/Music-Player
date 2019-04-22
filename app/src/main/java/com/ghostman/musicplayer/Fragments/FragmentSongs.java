package com.ghostman.musicplayer.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.ghostman.musicplayer.EditSongInfo;
import com.ghostman.musicplayer.Fragments.dummy.DummyContent.DummyItem;
import com.ghostman.musicplayer.MainActivity;
import com.ghostman.musicplayer.Others.DBHandler;
import com.ghostman.musicplayer.Others.Functions;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerHelper.Playlists;
import com.ghostman.musicplayer.PlayerHelper.Songs;
import com.ghostman.musicplayer.PlayerService;
import com.ghostman.musicplayer.R;
import com.ghostman.musicplayer.RecyclerViewAdapter.AdapterSongs;
import com.ghostman.musicplayer.RecyclerViewAdapter.TouchListener;

import java.util.ArrayList;

import static com.ghostman.musicplayer.MainActivity.EXTRA_MESSAGE;

/**
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FragmentSongs extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    public static AdapterSongs adapterSongs;

    public static Boolean Fav = false;

    public final static String EXTRA_MESSAGE = "com.ghostman.musicplayer.fragments.FragmentSongs.MESSAGE";

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
            public void onLongItemClick(View view, final int position) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.song_list_extras);
                Button PLAY = dialog.findViewById(R.id.play);
                Button ADD_TO_FAV = dialog.findViewById(R.id.add_to_fav);
                Button ADD_TO_PLAYLIST = dialog.findViewById(R.id.add_to_playlist);
                Button EDIT_TRACK_INFO = dialog.findViewById(R.id.edit_song_info);
                Button DELETE_TRACK = dialog.findViewById(R.id.delete_song);

                DBHandler db = new DBHandler(getActivity().getApplicationContext());
                PlayerConstants.FAV_LIST = db.listOfFavouriteSongs();
                for (Songs song : PlayerConstants.FAV_LIST)   {
                    if(PlayerConstants.SONGS_LIST.get(position).getSong_id() == song.getSong_id())    {
                        ADD_TO_FAV.setText("REMOVE FROM FAVOURITES");
                        Fav = true;
                        break;
                    }
                }

                PLAY.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlayerConstants.SONG_PAUSED = false;

                        PlayerConstants.CURRENT_SONG_ID = PlayerConstants.SONGS_LIST.get(position).getSong_id();

                        boolean isServiceRunning = Functions.isServiceRunning(PlayerService.class.getName(), getActivity().getApplicationContext());
                        if (!isServiceRunning) {
                            Intent i = new Intent(getActivity().getApplicationContext(), PlayerService.class);
                            i.putExtra(EXTRA_MESSAGE, true);
                            getActivity().startService(i);
                        } else
                            PlayerConstants.PLAY_HANDLER.sendMessage(PlayerConstants.PLAY_HANDLER.obtainMessage());


                        adapterSongs.notifyDataSetChanged();
                        MainActivity.updateUI();

                        dialog.dismiss();
                    }
                });

                ADD_TO_FAV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Fav)  {
                            Fav = false;
                            Songs data = PlayerConstants.SONGS_LIST.get(position);
                            PlayerConstants.FAV_LIST = Functions.addFavSongs(getActivity().getApplicationContext(), data.getSong_id(), false);
                        }
                        else {
                            Songs data = PlayerConstants.SONGS_LIST.get(position);
                            PlayerConstants.FAV_LIST = Functions.addFavSongs(getActivity().getApplicationContext(), data.getSong_id(), true);
                        }

                        dialog.dismiss();
                    }
                });

                ADD_TO_PLAYLIST.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Add to playlist");

                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
                        for(Playlists playlists : PlayerConstants.PLAYLIST_LIST)
                            arrayAdapter.add(playlists.getPlaylist_name());

                        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long PLAYLIST_ID = 0;
                                for(Playlists playlists : PlayerConstants.PLAYLIST_LIST)    {
                                    if(playlists.getPlaylist_name().compareTo(arrayAdapter.getItem(which)) == 0)
                                        PLAYLIST_ID = playlists.getPlaylist_id();

                                }
                                ArrayList<Songs> songToAdd = new ArrayList<>();

                                songToAdd.add(PlayerConstants.SONGS_LIST.get(position));
                                Functions.listOfPlaylistSongsAdd(getActivity(), PLAYLIST_ID, songToAdd, true);
                            }
                        });

                        builder.show();

                        dialog.dismiss();
                    }
                });

                EDIT_TRACK_INFO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(),EditSongInfo.class);
                        final Songs data = PlayerConstants.SONGS_LIST.get(position);
                        Long song_id = data.getSong_id();
                        i.putExtra(EXTRA_MESSAGE , song_id);
                        startActivity(i);

                        dialog.dismiss();
                    }
                });

                DELETE_TRACK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        final Songs data = PlayerConstants.SONGS_LIST.get(position);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(false);
                        builder.setTitle("ALERT");
                        builder.setMessage("\"" + data.getSong_title() + "\" will be permanently deleted from the SD card /Phone Storage.");

                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Functions.deleteSong(getActivity().getApplicationContext(), data.getSong_id());
                                PlayerConstants.SONGS_LIST = Functions.listOfSongs(getActivity());
                                PlayerConstants.ALBUMS_LIST = Functions.listOfAlbums(getActivity());
                                PlayerConstants.ARTIST_LIST = Functions.listOfArtists(getActivity());
                                PlayerConstants.GENRES_LIST = Functions.listOfGenres(getActivity());
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {    }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });

                try {
                    dialog.show();
                }catch (Exception e)    {   Log.d("DIALOG","FRAG_SONG",e);  }

                adapterSongs.notifyDataSetChanged();
                MainActivity.updateUI();
            }
        }));
    }
}
