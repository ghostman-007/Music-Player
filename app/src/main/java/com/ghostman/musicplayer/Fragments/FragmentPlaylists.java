package com.ghostman.musicplayer.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.ghostman.musicplayer.Fragments.dummy.DummyContent;
import com.ghostman.musicplayer.Fragments.dummy.DummyContent.DummyItem;
import com.ghostman.musicplayer.Others.Functions;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerHelper.Playlists;
import com.ghostman.musicplayer.R;
import com.ghostman.musicplayer.RecyclerViewAdapter.AdapterPlaylists;
import com.ghostman.musicplayer.RecyclerViewAdapter.TouchListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FragmentPlaylists extends Fragment {

    View rootView;
    RecyclerView recyclerView;
    AdapterPlaylists adapterPlaylists;
    String pl_name;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentPlaylists() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentPlaylists newInstance(int columnCount) {
        FragmentPlaylists fragment = new FragmentPlaylists();
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
        rootView = inflater.inflate(R.layout.fragment_playlists, container, false);

        PlayerConstants.PLAYLIST_LIST = Functions.listOfPlaylists(rootView.getContext());

        register_ui_components();
        set_listeners();

        setHasOptionsMenu(true);
        PlayerConstants.PLAYLIST_ADD_SONGS = false;

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.playlist_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.playlistAdd) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            builder.setTitle("New Playlist");
            final EditText pl_name = new EditText(getActivity().getApplicationContext());
            pl_name.setHint("Enter Playlist Name");
            pl_name.setHintTextColor(ContextCompat.getColor(getActivity(), android.R.color.darker_gray));
            pl_name.setTextColor(ContextCompat.getColor(getActivity(),android.R.color.black));
            pl_name.setSingleLine();

            FrameLayout frameLayout = new FrameLayout(getActivity().getApplicationContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.dialog_margin);
            pl_name.setLayoutParams(params);
            frameLayout.addView(pl_name);

            builder.setView(frameLayout);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Functions.listOfPlaylists(rootView.getContext(), pl_name.getText().toString(),"add");
                   // Snackbar.make(rootView, "Playlist will be added soon...", Snackbar.LENGTH_SHORT)
                     //       .setAction("ACTION",null).show();

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_frame, new FragmentPlaylists())
                            .commit();

                    /*
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame,new FragmentPlaylists());
                    fragmentTransaction.commit();*/
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        }
        return super.onOptionsItemSelected(item);
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

    void register_ui_components() {
        adapterPlaylists = new AdapterPlaylists(PlayerConstants.PLAYLIST_LIST, getActivity());
        // Set the adapter
        if (rootView instanceof RecyclerView) {
            Context context = rootView.getContext();
            recyclerView = (RecyclerView) rootView;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(adapterPlaylists);
        }
    }

    void set_listeners() {
        recyclerView.addOnItemTouchListener(new TouchListener(getActivity().getApplicationContext(), recyclerView, new TouchListener.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                view.setSelected(true);

                PlayerConstants.CURRENT_PLAYLIST_ID = PlayerConstants.PLAYLIST_LIST.get(position).getPlaylist_id();

                PlayerConstants.ALBUMS_SONG_LIST = Functions.listOfPlaylistSongs(getActivity().getApplicationContext(), PlayerConstants.CURRENT_PLAYLIST_ID);
                PlayerConstants.CURRENT_PLAYLIST_SONGS = PlayerConstants.ALBUMS_SONG_LIST;

                PlayerConstants.PLAYLIST_ADD_SONGS = true;
                PlayerConstants.FAVOURITES_FRAG = false;
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,new FragmentSongsLayout()).addToBackStack("tag_playlist").commit();
            }

            @Override
            public void onLongItemClick(View view, int position) {
                Playlists data = PlayerConstants.PLAYLIST_LIST.get(position);
                PlayerConstants.CURRENT_PLAYLIST_ID = data.getPlaylist_id();
                pl_name = data.getPlaylist_name();

                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.playlist_context_menu);
                Button et_rename = dialog.findViewById(R.id.pl_context_menu_rename);
                Button et_delete = dialog.findViewById(R.id.pl_context_menu_delete);

                et_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Functions.listOfPlaylists(getActivity().getApplicationContext(), pl_name, "remove");

                        //Reload current fragment
                        /*Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("Fragment Playlist");
                        final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.detach(fragment);
                        fragmentTransaction.attach(fragment);
                        fragmentTransaction.commit();*/

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_frame, new FragmentPlaylists())
                                .commit();
                    }
                });

                et_rename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("RENAME");
                        final EditText new_pl_name = new EditText(getActivity().getApplicationContext());
                        new_pl_name.setSingleLine();
                        FrameLayout frameLayout = new FrameLayout(getActivity().getApplicationContext());
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.topMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                        params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.dialog_margin);
                        new_pl_name.setLayoutParams(params);
                        frameLayout.addView(new_pl_name);
                        new_pl_name.setHint("Enter New Name");
                        new_pl_name.setHintTextColor(getResources().getColor(R.color.colorPrimary));
                        new_pl_name.setTextColor(getResources().getColor(R.color.colorBlack));
                        builder.setView(frameLayout);
                        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Functions.listOfPlaylists(getActivity().getApplicationContext(),new_pl_name.getText().toString(),"rename");

                                //Reload current fragment
                                /*Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("Fragment Playlist");
                                final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.detach(fragment);
                                fragmentTransaction.attach(fragment);
                                fragmentTransaction.commit();*/

                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.main_frame, new FragmentPlaylists())
                                        .commit();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                });
                dialog.show();
            }
        }));
    }
}
