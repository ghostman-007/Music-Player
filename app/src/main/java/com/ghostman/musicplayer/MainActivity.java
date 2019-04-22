package com.ghostman.musicplayer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ghostman.musicplayer.Fragments.FragmentAlbums;
import com.ghostman.musicplayer.Fragments.FragmentArtist;
import com.ghostman.musicplayer.Fragments.FragmentFavourites;
import com.ghostman.musicplayer.Fragments.FragmentGenres;
import com.ghostman.musicplayer.Fragments.FragmentPlaylists;
import com.ghostman.musicplayer.Fragments.FragmentSongs;
import com.ghostman.musicplayer.Others.Controls;
import com.ghostman.musicplayer.Others.DBHandler;
import com.ghostman.musicplayer.Others.Functions;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerHelper.Songs;
import com.ghostman.musicplayer.Settings.SettingsActivity;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LocalBroadcastManager localBroadcastManager;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.ghostman.musicplayer.action.close")) {
                finish();
            }
        }
    };

    static final Integer STORAGE_PERMISSION = 101;
    public final static String EXTRA_MESSAGE = "com.ghostman.musicplayer.EXTRA_MESSAGE";

    View view;

    DrawerLayout drawer;

    static final int TIME_DELAY = 2000;
    static long back_pressed;

    SlidingUpPanelLayout MA_SPL_SLIDING_PANEL;
    ConstraintLayout MA_CL_BOTTOM_NAV_BAR;
    static ImageButton MA_IB_PLAY, MA_IB_PAUSE, MA_IB_NEXT;
    static ImageButton AP_IB_PLAY, AP_IB_PAUSE, AP_IB_PREVIOUS, AP_IB_NEXT, AP_IB_REPEAT, AP_IB_SHUFFLE, AP_IB_FAV;
    ImageButton AP_IB_SONG_QUEUE;
    static TextView MA_TV_SONG_NAME;
    static ProgressBar MA_PB_PROGRESS_BAR;
    static TextView AP_TV_SONG_NAME, AP_TV_ARTIST_NAME, AP_TV_DURATION_START, AP_TV_DURATION_END;
    static ImageView AP_IV_SONG_IMAGE;
    static LinearLayout AP_LL_BOTTOM_BACKGROUND;
    static SeekBar AP_SB_SEEK_BAR;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        requestStoragePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else {
            if(MA_SPL_SLIDING_PANEL.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED)
                MA_SPL_SLIDING_PANEL.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            else {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    getSupportFragmentManager().popBackStack();
                else {
                    if (back_pressed + TIME_DELAY > System.currentTimeMillis())
                        this.moveTaskToBack(true);  //super.onBackPressed();
                    else
                        Toast.makeText(getBaseContext(),
                                "Tap again to push the app to background", Toast.LENGTH_SHORT).show();
                    back_pressed = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_songs) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_frame,new FragmentSongs(),"FRAGMENT_SONGS");
            fragmentTransaction.commit();

        } else if (id == R.id.nav_albums) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_frame,new FragmentAlbums(),"FRAGMENT_ALBUMS");
            fragmentTransaction.commit();

        } else if (id == R.id.nav_artist) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_frame,new FragmentArtist(),"FRAGMENT_ARTIST");
            fragmentTransaction.commit();

        } else if (id == R.id.nav_genre) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_frame,new FragmentGenres(),"FRAGMENT_GENRES");
            fragmentTransaction.commit();

        } else if (id == R.id.nav_playlist) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_frame,new FragmentPlaylists(),"FRAGMENT_PLAYLIST");
            fragmentTransaction.commit();

        } else if (id == R.id.nav_favourite) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_frame,new FragmentFavourites(),"FRAGMENT_FAVOURITES");
            fragmentTransaction.commit();

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));

        } else if (id == R.id.nav_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("☻☻☻ Music Player v1.0 ☻☻☻");
            builder.setMessage(Html.fromHtml("<b>■ Developers</b><br><br>Shubham <br>Sunny <br>Vinjit <br>Piyush <br>Shivam"));
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {    }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame, new FragmentSongs())
                .commit();
    }

    private void chicken_dinner() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().clear().apply();
        SharedPreferences settingsValue = getSharedPreferences("com.ghostman.musicplayer.settings",MODE_PRIVATE);
        settingsValue.edit().clear().apply();
        // NOT WORKING ---> android.support.v7.preference.PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        // For Toast >>>>
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Boolean shakeSwitchPref = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_SHAKE_SWITCH, false);

        register_ui_components();
        set_listeners();

        PlayerConstants.SONGS_LIST = Functions.listOfSongs(this);
        PlayerConstants.CURRENT_SONGS_LIST = PlayerConstants.SONGS_LIST;
        PlayerConstants.ALBUMS_LIST = Functions.listOfAlbums(getApplicationContext());
        PlayerConstants.ARTIST_LIST = Functions.listOfArtists(getApplicationContext());
        PlayerConstants.GENRES_LIST = Functions.listOfGenres(getApplicationContext());
        PlayerConstants.PLAYLIST_LIST = Functions.listOfPlaylists(getApplicationContext());
        DBHandler db = new DBHandler(getApplicationContext());
        PlayerConstants.FAV_LIST = db.listOfFavouriteSongs();


        if(!Functions.isServiceRunning(PlayerService.class.getName(),this)) {
            SharedPreferences sharedPreferences = getSharedPreferences("currentSongDetails",MODE_PRIVATE);
            PlayerConstants.CURRENT_SONG_ID = sharedPreferences.getLong("songID",0);
            PlayerConstants.CURRENT_SONG_POSITION = sharedPreferences.getInt("songPosition",0);


            if(PlayerConstants.CURRENT_SONG_ID == 0) {
                try{
                    PlayerConstants.CURRENT_SONG_ID = PlayerConstants.SONGS_LIST.get(0).getSong_id();
                }catch (IndexOutOfBoundsException ioobe) {
                    Toast.makeText(context, "No Song Found on Your Device...", Toast.LENGTH_LONG).show();
                }

            } else {
                boolean flag = false;
                for ( Songs songs : PlayerConstants.SONGS_LIST) {
                    if(songs.getSong_id() == PlayerConstants.CURRENT_SONG_ID) {
                        // updateUI();
                        flag = true;
                        break;
                    }
                }
                if(!flag) {
                    PlayerConstants.CURRENT_SONG_ID = PlayerConstants.SONGS_LIST.get(0).getSong_id();
                    PlayerConstants.CURRENT_SONG_POSITION = 0;
                }
            }
        }

        updateUI();
    }

    void register_ui_components() {
        MA_SPL_SLIDING_PANEL = findViewById(R.id.splSlidingPanel_abm);
        MA_CL_BOTTOM_NAV_BAR = findViewById(R.id.clBottomNavBar_abm);
        MA_IB_PLAY = findViewById(R.id.ibPlay_bnb);
        MA_IB_PAUSE = findViewById(R.id.ibPause_bnb);
        MA_IB_NEXT = findViewById(R.id.ibNext_bnb);
        MA_TV_SONG_NAME = findViewById(R.id.tvSongName_bnb);
        MA_PB_PROGRESS_BAR = findViewById(R.id.progressBar_bnb);

        AP_IB_PLAY = findViewById(R.id.ibPlay_ap);
        AP_IB_PAUSE = findViewById(R.id.ibPause_ap);
        AP_IB_PREVIOUS = findViewById(R.id.ibPrevious_ap);
        AP_IB_NEXT = findViewById(R.id.ibNext_ap);
        AP_IB_REPEAT = findViewById(R.id.ibRepeat_ap);
        AP_IB_SHUFFLE = findViewById(R.id.ibShuffle_ap);
        AP_IB_FAV = findViewById(R.id.ibFav_ap);
        AP_IB_SONG_QUEUE = findViewById(R.id.ibSongQueue_ap);
        AP_TV_SONG_NAME = findViewById(R.id.tvSongName_ap);
        AP_TV_ARTIST_NAME = findViewById(R.id.tvArtistName_ap);
        AP_TV_DURATION_START = findViewById(R.id.tvDurationStart_ap);
        AP_TV_DURATION_END = findViewById(R.id.tvDurationEnd_ap);
        AP_IV_SONG_IMAGE = findViewById(R.id.ivSongImage_ap);
        AP_LL_BOTTOM_BACKGROUND = findViewById(R.id.llBottom_ap);
        AP_SB_SEEK_BAR = findViewById(R.id.seekBar_ap);
    }

    void set_listeners() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.ghostman.musicplayer.action.close");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        MA_SPL_SLIDING_PANEL.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                MA_CL_BOTTOM_NAV_BAR.setVisibility(View.VISIBLE);
                MA_CL_BOTTOM_NAV_BAR.setAlpha( 1 - slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {
                switch (newState) {
                    case EXPANDED: MA_CL_BOTTOM_NAV_BAR.setVisibility(View.GONE);
                        drawer.setDrawerLockMode(1);
                        break;
                    case ANCHORED: MA_SPL_SLIDING_PANEL.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        break;
                    case COLLAPSED:
                        drawer.setDrawerLockMode(0);
                }
            }
        });

        MA_IB_PLAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerConstants.SONG_PAUSED = false;
                updateUI();
                boolean isServiceRunning = Functions.isServiceRunning(PlayerService.class.getName(),context);
                if (!isServiceRunning) {
                    Intent i = new Intent(context,PlayerService.class);
                    i.putExtra(EXTRA_MESSAGE , true);
                    startService(i);
                } else {
                    PlayerConstants.PLAY_HANDLER.sendMessage(PlayerConstants.PLAY_HANDLER.obtainMessage());
                }
            }
        });

        MA_IB_PAUSE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerConstants.SONG_PAUSED = true;
                updateUI();
                PlayerConstants.PAUSE_HANDLER.sendMessage(PlayerConstants.PAUSE_HANDLER.obtainMessage());
            }
        });

        MA_IB_NEXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isServiceRunning = Functions.isServiceRunning(PlayerService.class.getName(),context);
                if (isServiceRunning) {
                    PlayerConstants.SONG_PAUSED = false;
                    Controls.nextControl();
                }
            }
        });

        // Audio Player Button Listeners
        AP_IB_PLAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerConstants.SONG_PAUSED = false;
                updateUI();
                boolean isServiceRunning = Functions.isServiceRunning(PlayerService.class.getName(),context);
                if (!isServiceRunning) {
                    Intent i = new Intent(context,PlayerService.class);
                    i.putExtra(EXTRA_MESSAGE , true);
                    startService(i);
                }
                else
                    PlayerConstants.PLAY_HANDLER.sendMessage(PlayerConstants.PLAY_HANDLER.obtainMessage());
            }
        });

        AP_IB_PAUSE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerConstants.SONG_PAUSED = true;
                updateUI();
                PlayerConstants.PAUSE_HANDLER.sendMessage(PlayerConstants.PAUSE_HANDLER.obtainMessage());
            }
        });

        AP_IB_PREVIOUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isServiceRunning = Functions.isServiceRunning(PlayerService.class.getName(),context);
                if (isServiceRunning) {
                    PlayerConstants.SONG_PAUSED = false;
                    Controls.previousControl();
                }
            }
        });

        AP_SB_SEEK_BAR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if(fromUser) {
                    int PROGRESS = seekBar.getProgress();
                    AP_TV_DURATION_START.setText(Functions.getDuration(PROGRESS));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                boolean isServiceRunning = Functions.isServiceRunning(PlayerService.class.getName(), getApplicationContext());
                if (isServiceRunning)
                    PlayerConstants.SEEK_START_HANDLER.sendMessage(PlayerConstants.SEEK_START_HANDLER.obtainMessage());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlayerConstants.CURRENT_SONG_POSITION = seekBar.getProgress();
                boolean isServiceRunning = Functions.isServiceRunning(PlayerService.class.getName(), getApplicationContext());
                if (isServiceRunning)
                    PlayerConstants.SEEK_TO_HANDLER.sendMessage(PlayerConstants.SEEK_TO_HANDLER.obtainMessage());
                // else
                // MA_PROGRESS_BAR.setProgress(PlayerConstants.CURRENT_SONG_POSITION);
            }
        });

        AP_IB_NEXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isServiceRunning = Functions.isServiceRunning(PlayerService.class.getName(),context);
                if (isServiceRunning) {
                    PlayerConstants.SONG_PAUSED = false;
                    Controls.nextControl();
                }
            }
        });

        AP_IB_FAV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!PlayerConstants.FAVOURITE) {
                    AP_IB_FAV.setImageResource(R.drawable.ic_favorite_24px);
                    PlayerConstants.FAV_LIST = Functions.addFavSongs(getApplicationContext(), PlayerConstants.CURRENT_SONG_ID, true);
                } else {
                    AP_IB_FAV.setImageResource(R.drawable.ic_favorite_border_24px);
                    PlayerConstants.FAV_LIST = Functions.addFavSongs(getApplicationContext(), PlayerConstants.CURRENT_SONG_ID, false);
                }
                PlayerConstants.FAVOURITE = !PlayerConstants.FAVOURITE;
            }
        });

        AP_IB_REPEAT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PlayerConstants.REPEAT == 2) {
                    PlayerConstants.REPEAT = 1;
                    AP_IB_REPEAT.setImageResource(R.drawable.ic_repeat_one_24px);
                }
                else if(PlayerConstants.REPEAT == 1) {
                    PlayerConstants.REPEAT = 0;
                    AP_IB_REPEAT.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));
                    AP_IB_REPEAT.setImageResource(R.drawable.ic_repeat_24px);
                }
                else {
                    PlayerConstants.REPEAT = 2;
                    AP_IB_REPEAT.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.black));
                    AP_IB_REPEAT.setImageResource(R.drawable.ic_repeat_24px);
                }
            }
        });

        AP_IB_SHUFFLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!PlayerConstants.SHUFFLE)
                    AP_IB_SHUFFLE.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.black));
                else
                    AP_IB_SHUFFLE.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));
                PlayerConstants.SHUFFLE = !PlayerConstants.SHUFFLE;
            }
        });

        AP_IB_SONG_QUEUE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SongQueue.class));
            }
        });
    }

    public static void updateUI() {
        if(PlayerConstants.SONG_PAUSED) {
            MA_IB_PAUSE.setVisibility(View.GONE);
            MA_IB_PLAY.setVisibility(View.VISIBLE);
            AP_IB_PAUSE.setVisibility(View.GONE);
            AP_IB_PLAY.setVisibility(View.VISIBLE);
        } else {
            MA_IB_PLAY.setVisibility(View.GONE);
            MA_IB_PAUSE.setVisibility(View.VISIBLE);
            AP_IB_PLAY.setVisibility(View.GONE);
            AP_IB_PAUSE.setVisibility(View.VISIBLE);
        }

        for (Songs songs : PlayerConstants.SONGS_LIST) {
            if(songs.getSong_id() == PlayerConstants.CURRENT_SONG_ID) {
                MA_TV_SONG_NAME.setText(songs.getSong_title());
                AP_TV_SONG_NAME.setText(songs.getSong_title());
                AP_TV_ARTIST_NAME.setText(songs.getSong_artist());
                boolean isServiceRunning = Functions.isServiceRunning(PlayerService.class.getName(),context);
                if (!isServiceRunning) {
                    AP_TV_DURATION_START.setText(Functions.getDuration(PlayerConstants.CURRENT_SONG_POSITION));
                    AP_SB_SEEK_BAR.setProgress(PlayerConstants.CURRENT_SONG_POSITION);
                    MA_PB_PROGRESS_BAR.setProgress(PlayerConstants.CURRENT_SONG_POSITION);
                }
                AP_TV_DURATION_END.setText(Functions.getDuration(songs.getSong_duration()));
                AP_SB_SEEK_BAR.setMax((int)songs.getSong_duration());
                MA_PB_PROGRESS_BAR.setMax((int)songs.getSong_duration());
                MA_TV_SONG_NAME.setSelected(true);

                try {
                    Bitmap albumArt = Functions.getAlbumart(context, songs.getSong_albumId());
                    if(albumArt != null) {
                        updateAudioPlayerBottom(albumArt);
                        AP_IV_SONG_IMAGE.setBackground(new BitmapDrawable(albumArt));
                    }
                    else {
                        Bitmap defaultAlbumArt = Functions.getDefaultAlbumArt(context);
                        updateAudioPlayerBottom(defaultAlbumArt);
                        AP_IV_SONG_IMAGE.setBackground(new BitmapDrawable(defaultAlbumArt));
                    }

                }catch(Exception e){    Log.d("AUDIO PLAYER ",":: SONG IMAGE");
                    e.printStackTrace();
                }

                break;
            }
        }

        // Favourite Update
        boolean flag = false;
        for (Songs song : PlayerConstants.FAV_LIST)   {
            if(PlayerConstants.CURRENT_SONG_ID == song.getSong_id())    {
                PlayerConstants.FAVOURITE = true;
                AP_IB_FAV.setImageResource(R.drawable.ic_favorite_24px);
                flag = true;
                break;
            }
        }
        if(!flag)   {
            PlayerConstants.FAVOURITE = false;
            AP_IB_FAV.setImageResource(R.drawable.ic_favorite_border_24px);
        }

        // TODO: Modify it
        try {
            FragmentSongs.adapterSongs.notifyDataSetChanged();
        }catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        if(getIntent().getBooleanExtra("com.ghostman.musicplayer.playerservice.notificaton",false))
            MA_SPL_SLIDING_PANEL.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

        updateUI();

        PlayerConstants.SEEK_BAR_HANDLER = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Integer[] integer = (Integer[])message.obj;
                MA_PB_PROGRESS_BAR.setMax(integer[1]);
                MA_PB_PROGRESS_BAR.setProgress(integer[0]);

                AP_TV_DURATION_START.setText(Functions.getDuration(integer[0]));
                AP_TV_DURATION_END.setText(Functions.getDuration(integer[1]));
                AP_SB_SEEK_BAR.setMax(integer[1]);
                AP_SB_SEEK_BAR.setProgress(integer[0]);
                return false;
            }
        });

        //Toast.makeText(this,"On Resume",Toast.LENGTH_SHORT).show();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    private static void updateAudioPlayerBottom(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getDominantSwatch();
                if (swatch == null)
                    swatch = palette.getMutedSwatch();  //Sometime vibrant swatch is not available
                if (swatch != null) {
                    //window.setStatusBarColor(swatch.getRgb());
                    AP_LL_BOTTOM_BACKGROUND.setBackgroundColor(swatch.getRgb());
                    AP_TV_SONG_NAME.setTextColor(swatch.getBodyTextColor());
                    AP_TV_ARTIST_NAME.setTextColor(swatch.getTitleTextColor());
                    AP_TV_DURATION_START.setTextColor(swatch.getBodyTextColor());
                    AP_TV_DURATION_END.setTextColor(swatch.getBodyTextColor());
                    AP_TV_SONG_NAME.setSelected(true);
                }
            }
        });
    }

    private void requestStoragePermission(final String permission,final Integer requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED)
        {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission))
            {   //Show an explanation  to the user *asynchronously* -- don't block this thread
                //waiting for the user's response! After the user sees the explanation, try again
                // to request the permission.

                AlertDialog.Builder builder =  new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage("To read songs on the device, allow access to your External Storage.");
                builder.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[] {permission}, requestCode);
                    }
                });
                builder.setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity mainActivity = new MainActivity();
                        mainActivity.finish();
                        System.exit(0);

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            } else {
                //No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        }
        else {
            // We get the permission
            chicken_dinner();
            setFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 101: {
                //If request is cancelled, the result arrays are empty.
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    MainActivity.this.recreate();
                    //permission was granted, yay! Move forward.
                    //init();
                    chicken_dinner();
                }
                else
                {
                    Snackbar snackbar = Snackbar.make(view, "Permission to access external storage denied.",Snackbar.LENGTH_SHORT)
                            .setAction("permission", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Snackbar snackbar1 = Snackbar.make(view, "Snackbar with Callback called.", Snackbar.LENGTH_SHORT);
                                    snackbar1.show();
                                }
                            });
                    snackbar.show();

                    //permission denied, boo! Disable the functionality that depends on this permission.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setCancelable(false);
                        builder.setMessage("Please enable permission for this app. Until disabled the app cannot work properly.");
                        builder.setPositiveButton("EXIT App", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity mainActivity = new MainActivity();
                                mainActivity.finish();
                                System.exit(0);
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setCancelable(false);
                        builder.setMessage("Please enable permission for this app. Tap Settings > App Permissions > Storage \"ON\".");
                        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(),null);
                                intent.setData(uri);
                                startActivityForResult(intent,STORAGE_PERMISSION);
                            }
                        });
                        builder.setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity mainActivity = new MainActivity();
                                mainActivity.finish();
                                System.exit(0);
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                }
                return;
            }
            //Other 'case' lines to check for other permission this app might request
            default:
                Toast.makeText(getApplicationContext(),"ON REQUEST PERMISSION ERROR",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            MainActivity.this.recreate();
            //init();
            chicken_dinner();
        }

        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("Please enable permission for this app. Tap Settings > App Permissions > Storage \"ON\".");
            builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, STORAGE_PERMISSION);
                }
            });
            builder.setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.finish();
                    System.exit(0);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
