package com.ghostman.musicplayer.Fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.ghostman.musicplayer.AddListSongs;
import com.ghostman.musicplayer.EditSongInfo;
import com.ghostman.musicplayer.MainActivity;
import com.ghostman.musicplayer.Others.DBHandler;
import com.ghostman.musicplayer.Others.Functions;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerHelper.Playlists;
import com.ghostman.musicplayer.PlayerHelper.Songs;
import com.ghostman.musicplayer.PlayerService;
import com.ghostman.musicplayer.R;
import com.ghostman.musicplayer.RecyclerViewAdapter.Adapter;
import com.ghostman.musicplayer.RecyclerViewAdapter.TouchListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FragmentSongsLayout extends Fragment {
    MenuItem mi_add_pl_songs;

    private View rootView;
    public RecyclerView RECYCLER_VIEW;
    public static Adapter mAdapter;

    public static Boolean Fav = false;

    public final static String EXTRA_MESSAGE = "com.ghostman.musicplayer.Fragments.FragmentSongs.MESSAGE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_songs, container, false);
        register_ui_components();
        set_listeners();
        mAdapter = new Adapter(PlayerConstants.ALBUMS_SONG_LIST, getActivity());
        RECYCLER_VIEW.setLayoutManager(new LinearLayoutManager(getActivity()));
        RECYCLER_VIEW.setAdapter(mAdapter);

        if(PlayerConstants.PLAYLIST_ADD_SONGS)  {
        /*    for(Playlists playlists : PlayerConstants.PLAYLIST_LIST)  {
                if(playlists.getPlaylist_id() == PlayerConstants.CURRENT_PLAYLIST_ID)   {
                    ((MainActivity)getActivity()).changeActionBar("Playlist : " + playlists.getPlaylistName());
                    break;
                }
            }*/
            setHasOptionsMenu(true);
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.playlist_add_songs, menu);

        mi_add_pl_songs = menu.findItem(R.id.pl_add_songs);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //if(id == android.R.id.home) {   }

        if (id == R.id.pl_add_songs) {
            PlayerConstants.CURRENT_PLAYLIST_SONGS = Functions.listOfPlaylistSongs(getActivity().getApplicationContext(), PlayerConstants.CURRENT_PLAYLIST_ID);

            Intent intent = new Intent(this.getContext(), AddListSongs.class);
            startActivity(intent);
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void register_ui_components() {
        RECYCLER_VIEW = rootView.findViewById(R.id.recycler_view);
    }

    private void set_listeners() {
        RECYCLER_VIEW.addOnItemTouchListener(new TouchListener(getActivity(), RECYCLER_VIEW, new TouchListener.ClickListener() {
            @Override
            public void onItemClick(View view, int position){
                PlayerConstants.SONG_PAUSED = false;

                //PlayerConstants.CURRENT_SONGS_LIST = PlayerConstants.ALBUMS_SONG_LIST;
                //PlayerConstants.CURRENT_SONG_ID = PlayerConstants.CURRENT_SONGS_LIST.get(position).getSong_id();
                PlayerConstants.CURRENT_SONG_ID = PlayerConstants.ALBUMS_SONG_LIST.get(position).getSong_id();

                //PlayerConstants.CURRENT_SONG_NUMBER = position;

                if(PlayerConstants.PLAYLIST_ADD_SONGS)
                    PlayerConstants.FAVOURITES_FRAG = false;

                if(PlayerConstants.FAVOURITES_FRAG)
                    PlayerConstants.PLAYLIST_ADD_SONGS = false;

                else    {
                    PlayerConstants.PLAYLIST_ADD_SONGS = false;
                    PlayerConstants.FAVOURITES_FRAG = false;
                }

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("currentSongDetails",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("songID", PlayerConstants.CURRENT_SONG_ID);
                editor.putInt("songPosition", PlayerConstants.CURRENT_SONG_POSITION);
                editor.apply();

                boolean isServiceRunning = Functions.isServiceRunning(PlayerService.class.getName(), getActivity());
                if (!isServiceRunning) {
                    Intent i = new Intent(getActivity().getApplicationContext(),PlayerService.class);
                    i.putExtra(EXTRA_MESSAGE , true);
                    getActivity().startService(i);
                } else
                    PlayerConstants.PLAY_HANDLER.sendMessage(PlayerConstants.PLAY_HANDLER.obtainMessage());

                mAdapter.notifyDataSetChanged();
                MainActivity.updateUI();
            }

            @Override
            public void onLongItemClick(View view, final int position) {
                /*
                if (PlayerConstants.FAVOURITES_FRAG) {
                    PlayerConstants.PLAYLIST_ADD_SONGS = false;

                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.song_list_extras);
                    Button PLAY = dialog.findViewById(R.id.play);
                    Button ADD_TO_FAV = dialog.findViewById(R.id.add_to_fav);
                    Button ADD_TO_PLAYLIST = dialog.findViewById(R.id.add_to_playlist);
                    Button EDIT_TRACK_INFO = dialog.findViewById(R.id.edit_song_info);
                    Button DELETE_TRACK = dialog.findViewById(R.id.delete_song);

                    ADD_TO_FAV.setText("REMOVE FROM FAVOURITES");

                    PLAY.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PlayerConstants.SONG_PAUSED = false;

                            PlayerConstants.CURRENT_SONGS_LIST = PlayerConstants.ALBUMS_SONG_LIST;
                            PlayerConstants.CURRENT_SONG_ID = PlayerConstants.CURRENT_SONGS_LIST.get(position).getSong_id();
                            PlayerConstants.CURRENT_SONG_NUMBER = position;

                            boolean isServiceRunning = Functions.isServiceRunning(PlayerService.class.getName(), getActivity().getApplicationContext());
                            if (!isServiceRunning) {
                                Intent i = new Intent(getActivity().getApplicationContext(), PlayerService.class);
                                i.putExtra(EXTRA_MESSAGE, true);
                                getActivity().startService(i);
                            } else
                                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());


                            mAdapter.notifyDataSetChanged();
                            MainActivity.updateUI();

                            dialog.dismiss();
                        }
                    });

                    ADD_TO_FAV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Songs data = PlayerConstants.ALBUMS_SONG_LIST.get(position);
                            PlayerConstants.FAV_LIST = Functions.addFavSongs(getActivity().getApplicationContext(), data.getSong_id(), false);

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

                                    songToAdd.add(PlayerConstants.ALBUMS_SONG_LIST.get(position));
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
                            Intent i = new Intent(getActivity(), EditSongInfo.class);
                            final Songs data = PlayerConstants.ALBUMS_SONG_LIST.get(position);
                            Long song_id = data.getSong_id();
                            i.putExtra(EXTRA_MESSAGE, song_id);
                            startActivity(i);

                            dialog.dismiss();
                        }
                    });

                    DELETE_TRACK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            final Songs data = PlayerConstants.ALBUMS_SONG_LIST.get(position);
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

                                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("Fragment Playlist");
                                    final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.detach(fragment);
                                    fragmentTransaction.attach(fragment);
                                    fragmentTransaction.commit();
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

                    try {
                        dialog.show();
                    } catch (Exception e) {
                        Log.d("DIALOG", "FRAG_SONG", e);
                    }

                    mAdapter.notifyDataSetChanged();
                    MainActivity.updateUI();
                }

                else if(PlayerConstants.PLAYLIST_ADD_SONGS) {
                    PlayerConstants.FAVOURITES_FRAG = false;

                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.song_list_extras);
                    final Button PLAY = (Button)dialog.findViewById(R.id.play);
                    Button ADD_TO_FAV = (Button)dialog.findViewById(R.id.add_to_fav);
                    Button ADD_TO_PLAYLIST = (Button)dialog.findViewById(R.id.add_to_playlist);
                    Button EDIT_TRACK_INFO = (Button)dialog.findViewById(R.id.edit_song_info);
                    Button DELETE_TRACK = (Button)dialog.findViewById(R.id.delete_song);

                    DBHandler db = new DBHandler(getActivity().getApplicationContext());
                    PlayerConstants.FAV_LIST = db.listOfFavouriteSongs();
                    for (Songs song : PlayerConstants.FAV_LIST)   {
                        if(PlayerConstants.ALBUMS_SONG_LIST.get(position).getSong_id() == song.getSong_id())    {
                            ADD_TO_FAV.setText("REMOVE FROM FAVOURITES");
                            Fav = true;
                            break;
                        }
                    }

                    ADD_TO_PLAYLIST.setText("REMOVE FROM PLAYLIST");

                    PLAY.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PlayerConstants.SONG_PAUSED = false;

                            PlayerConstants.CURRENT_SONGS_LIST = PlayerConstants.ALBUMS_SONG_LIST;
                            PlayerConstants.CURRENT_SONG_ID = PlayerConstants.CURRENT_SONGS_LIST.get(position).getSong_id();
                            PlayerConstants.CURRENT_SONG_NUMBER = position;

                            boolean isServiceRunning = Functions.isServiceRunning(PlayerService.class.getName(), getActivity().getApplicationContext());
                            if (!isServiceRunning) {
                                Intent i = new Intent(getActivity().getApplicationContext(), PlayerService.class);
                                i.putExtra(EXTRA_MESSAGE, true);
                                getActivity().startService(i);
                            } else
                                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());


                            mAdapter.notifyDataSetChanged();
                            MainActivity.updateUI();

                            dialog.dismiss();
                        }
                    });

                    ADD_TO_FAV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(Fav)  {
                                Fav = false;
                                Songs data = PlayerConstants.ALBUMS_SONG_LIST.get(position);
                                PlayerConstants.FAV_LIST = Functions.addFavSongs(getActivity().getApplicationContext(), data.getSong_id(), false);
                            }
                            else {
                                Songs data = PlayerConstants.ALBUMS_SONG_LIST.get(position);
                                PlayerConstants.FAV_LIST = Functions.addFavSongs(getActivity().getApplicationContext(), data.getSong_id(), true);
                            }

                            dialog.dismiss();
                        }
                    });

                    ADD_TO_PLAYLIST.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<Songs> songsToRemove = new ArrayList<>();

                            songsToRemove.add(PlayerConstants.ALBUMS_SONG_LIST.get(position));
                            Functions.listOfPlaylistSongsAdd(getActivity(),PlayerConstants.CURRENT_PLAYLIST_ID, songsToRemove, false);

                            dialog.dismiss();
                        }
                    });

                    EDIT_TRACK_INFO.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getActivity(),EditSongInfo.class);
                            final Songs data = PlayerConstants.ALBUMS_SONG_LIST.get(position);
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
                            final Songs data = PlayerConstants.ALBUMS_SONG_LIST.get(position);
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

                                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag_playlist");
                                    final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.detach(fragment);
                                    fragmentTransaction.attach(fragment);
                                    fragmentTransaction.commit();
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

                    mAdapter.notifyDataSetChanged();
                    MainActivity.updateUI();
                }
                */
            }
        }));
    }

}
