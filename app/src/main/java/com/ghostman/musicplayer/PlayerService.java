package com.ghostman.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ghostman.musicplayer.Others.Controls;
import com.ghostman.musicplayer.Others.Functions;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.PlayerHelper.Songs;

import java.io.IOException;

import static com.ghostman.musicplayer.MainActivity.EXTRA_MESSAGE;

public class PlayerService extends Service implements AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private final String CHANNEL_ID = "musicPlayer_notification";
    private final int NOTIFICATION_ID = 7;
    public static final String NOTIFY_PREVIOUS = "com.ghostman.musicplayer.previous";
    public static final String NOTIFY_PAUSE = "com.ghostman.musicplayer.pause";
    public static final String NOTIFY_PLAY = "com.ghostman.musicplayer.play";
    public static final String NOTIFY_NEXT = "com.ghostman.musicplayer.next";
    public static final String NOTIFY_EXIT = "com.ghostman.musicplayer.exit";

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    @Override
    public void onAudioFocusChange(int i) {
        //Invoked when the audio focus of the system is updated.
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                // if (!mp.isPlaying())
                // Controls.playControl(getApplicationContext());
                mediaPlayer.setVolume(1.0f,1.0f);
                Log.d("AudioFocus : ", "GAIN");
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                // save_current_song();
                // Controls.stopControl(getApplicationContext());
                PlayerConstants.STOP_HANDLER.sendMessage(PlayerConstants.STOP_HANDLER.obtainMessage());
                Log.d("AudioFocus : ", "LOSS");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying())
                    PlayerConstants.PAUSE_HANDLER.sendMessage(PlayerConstants.PAUSE_HANDLER.obtainMessage());
                Log.d("AudioFocus : ", "TRANSIENT");
                    break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing at an attenuated level
                if (mediaPlayer.isPlaying())
                    mediaPlayer.setVolume(0.1f, 0.1f);
                // PlayerConstants.PAUSE_HANDLER.sendMessage(PlayerConstants.PAUSE_HANDLER.obtainMessage());
                Log.d("AudioFocus : ", "TCanDuck");
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        if(PlayerConstants.REPEAT == 2) {
            Controls.nextControl();
        }
        else if(PlayerConstants.REPEAT == 1) {
            PlayerConstants.PLAY_HANDLER.sendMessage(PlayerConstants.PLAY_HANDLER.obtainMessage());
        }
        else if(PlayerConstants.REPEAT == 0) {
            Controls.repeatComplete();
        }

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        switch (i) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK :
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + i1);
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED :
                Log.d("MediaPlayer Error", "MEDIA ERROR UNSUPPORTED " + i1);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN :
                Log.d("Media Player Error", "MEDIA ERROR UNKNOWN " + i1);
                break;
        }
        Toast.makeText(this,"FILE NOT SUPPORTED", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);

        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);

        registerBecomingNoisyReceiver();

        // For making earphone/headphone media button respond
        ComponentName receiverComponentName = new ComponentName(this, NotificationBroadcastReceiver.class);
        audioManager.registerMediaButtonEventReceiver(receiverComponentName);

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        boolean request = intent.getBooleanExtra(EXTRA_MESSAGE,false);
        if(!request) {
            PlayerConstants.SONGS_LIST = Functions.listOfSongs(this);

            readCurrentSongDetails();

            if(PlayerConstants.CURRENT_SONG_ID == 0) {
                try{
                    PlayerConstants.CURRENT_SONG_ID = PlayerConstants.SONGS_LIST.get(0).getSong_id();
                }catch (IndexOutOfBoundsException ioobe) {
                    ioobe.printStackTrace();
                    // Stop Service Immediately
                    stopSelf();
                    System.exit(0);
                }
            } else {
                boolean flag = false;
                for ( Songs songs : PlayerConstants.SONGS_LIST) {
                    if(songs.getSong_id() == PlayerConstants.CURRENT_SONG_ID) {
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

        // PLAY SONG
        for(Songs songs : PlayerConstants.SONGS_LIST) {
            if(songs.getSong_id() == PlayerConstants.CURRENT_SONG_ID) {
                String songPath = songs.getSong_path();
                playSong(songPath);
                mediaPlayer.seekTo(PlayerConstants.CURRENT_SONG_POSITION);
                PlayerConstants.SONG_PAUSED = false;
                createNotification();
                break;
            }
        }

        // Handlers
        PlayerConstants.PLAY_HANDLER = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if(mediaPlayer != null) {
                    // PLAY SONG
                    for(Songs songs : PlayerConstants.SONGS_LIST) {
                        if(songs.getSong_id() == PlayerConstants.CURRENT_SONG_ID) {
                            String songPath = songs.getSong_path();
                            playSong(songPath);
                            mediaPlayer.seekTo(PlayerConstants.CURRENT_SONG_POSITION);
                            //PlayerConstants.SONG_PAUSED = false;
                            break;
                        }
                    }
                }

                createNotification();
                return false;
            }
        });

        PlayerConstants.PAUSE_HANDLER = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                mediaPlayer.pause();
                PlayerConstants.SONG_PAUSED = true;
                PlayerConstants.CURRENT_SONG_POSITION = mediaPlayer.getCurrentPosition();
                createNotification();
                saveCurrentSongDetails();
                return false;
            }
        });

        PlayerConstants.STOP_HANDLER = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                mediaPlayer.stop();
                PlayerConstants.SONG_PAUSED = true;
                saveCurrentSongDetails();
                try {
                    MainActivity.updateUI();
                }catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
                createNotification();
                return false;
            }
        });

        PlayerConstants.EXIT_HANDLER = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                saveCurrentSongDetails();
                stopSelf();
                //stopForeground(true);
                //stopService(intent);
                return false;
            }
        });

        PlayerConstants.SEEK_START_HANDLER = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                handler.removeCallbacks(runnable);
                return false;
            }
        });

        PlayerConstants.SEEK_TO_HANDLER = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                mediaPlayer.seekTo(PlayerConstants.CURRENT_SONG_POSITION);
                handler.postDelayed(runnable,50);
                return false;
            }
        });

        //return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }


    private void playSong(String songPath) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            handler.postDelayed(runnable,50);
            audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
            handler.postDelayed(this,50);
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            if(mediaPlayer != null) {
                Integer integer[] = new Integer[2];
                integer[0] = mediaPlayer.getCurrentPosition();
                integer[1] = mediaPlayer.getDuration();
                PlayerConstants.SEEK_BAR_HANDLER.sendMessage(PlayerConstants.SEEK_BAR_HANDLER.obtainMessage(0,integer));
            }
        }
    };


    private void createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra( "com.ghostman.musicplayer.playerservice.notificaton" , true);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        String songName = "Unable to get Song Name";
        String albumName = "Unable to get Album Name";
        long albumID = 0;
        for(Songs songs : PlayerConstants.SONGS_LIST) {
            if (songs.getSong_id() == PlayerConstants.CURRENT_SONG_ID) {
                songName = songs.getSong_title();
                albumName = songs.getSong_album();
                albumID = songs.getSong_albumId();
            }
        }


        createNotificationChannel();
        RemoteViews notificationSmall = new RemoteViews(getPackageName(), R.layout.notification_small);
        RemoteViews notificationBig = new RemoteViews(getPackageName(), R.layout.notification_big);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(songName)
                .setContentText(albumName)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationSmall)
                .setCustomBigContentView(notificationBig)
                .build();

        setListeners(notificationSmall);
        setListeners(notificationBig);
        notification.contentView = notificationSmall;

        if(currentVersionSupportBigNotification())
            notification.bigContentView = notificationBig;

        try{
            Bitmap albumArt = Functions.getAlbumart(getApplicationContext(), albumID);
            if(albumArt != null){
                notification.contentView.setImageViewBitmap(R.id.current_playing_song_image, albumArt);
                if(currentVersionSupportBigNotification())
                     notification.bigContentView.setImageViewBitmap(R.id.current_playing_song_image, albumArt);
            }/*else{
                notification.contentView.setImageViewResource(R.id.current_playing_song_image, R.drawable.default_album_art);
                notification.bigContentView.setImageViewResource(R.id.current_playing_song_image, R.drawable.default_album_art);
            }*/
        }catch(Exception e){
            e.printStackTrace();
        }


        if(PlayerConstants.SONG_PAUSED){
            notification.contentView.setViewVisibility(R.id.ib_pause, View.GONE);
            notification.contentView.setViewVisibility(R.id.ib_play, View.VISIBLE);
            if(currentVersionSupportBigNotification()) {
                notification.bigContentView.setViewVisibility(R.id.ib_pause, View.GONE);
                notification.bigContentView.setViewVisibility(R.id.ib_play, View.VISIBLE);
            }
        }else {
            notification.contentView.setViewVisibility(R.id.ib_pause, View.VISIBLE);
            notification.contentView.setViewVisibility(R.id.ib_play, View.GONE);
            if(currentVersionSupportBigNotification()) {
                notification.bigContentView.setViewVisibility(R.id.ib_pause, View.VISIBLE);
                notification.bigContentView.setViewVisibility(R.id.ib_play, View.GONE);
            }
        }

        notification.contentView.setTextViewText(R.id.current_playing_song_name, songName);
        notification.contentView.setTextViewText(R.id.current_playing_album_name, albumName);
        if(currentVersionSupportBigNotification()) {
            notification.bigContentView.setTextViewText(R.id.current_playing_song_name, songName);
            notification.bigContentView.setTextViewText(R.id.current_playing_album_name, albumName);
        }

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
        //startForeground(NOTIFICATION_ID, notification);
    }

    public static boolean currentVersionSupportBigNotification() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if(sdkVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN){
            return true;
        }
        return false;
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music Notification";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void setListeners(RemoteViews view) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);
        Intent exit = new Intent(NOTIFY_EXIT);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.ib_previous, pPrevious);

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.ib_pause, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.ib_next, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.ib_play, pPlay);

        PendingIntent pExit = PendingIntent.getBroadcast(getApplicationContext(), 0, exit, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.ib_close, pExit);
    }


    @Override
    public void onDestroy() {
        Log.d("SERVICE","onDestroy");
        if(mediaPlayer != null){
            //saveCurrentSongDetails();
            PlayerConstants.CURRENT_SONG_POSITION = mediaPlayer.getCurrentPosition();
            SharedPreferences sharedPreferences = getSharedPreferences("currentSongDetails",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("songID", PlayerConstants.CURRENT_SONG_ID);
            editor.putInt("songPosition", PlayerConstants.CURRENT_SONG_POSITION);
            editor.apply();
            mediaPlayer.stop();
            mediaPlayer = null;
        }

        // unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(PlayerService.this);
        localBroadcastManager.sendBroadcast(new Intent("com.ghostman.musicplayer.action.close"));
        // System.exit(0);
        super.onDestroy();
    }



   // Register for ACTION_AUDIO_BECOMING_NOISY - headphone in out
    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    // Broadcast Receiver for ACTION_AUDIO_BECOMING_NOISY
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            PlayerConstants.PAUSE_HANDLER.sendMessage(PlayerConstants.PAUSE_HANDLER.obtainMessage());
        }
    };


    // Save Current Song Details
    public void saveCurrentSongDetails() {
        try {
            PlayerConstants.CURRENT_SONG_POSITION = mediaPlayer.getCurrentPosition();
        }catch(Exception e) {
            PlayerConstants.CURRENT_SONG_POSITION = 0;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("currentSongDetails",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("songID", PlayerConstants.CURRENT_SONG_ID);
        editor.putInt("songPosition", PlayerConstants.CURRENT_SONG_POSITION);
        editor.apply();
    }

    // Read Current Song Details
    private void readCurrentSongDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("currentSongDetails",MODE_PRIVATE);
        PlayerConstants.CURRENT_SONG_ID = sharedPreferences.getLong("songID",0);
        PlayerConstants.CURRENT_SONG_POSITION = sharedPreferences.getInt("songPosition",0);
    }
}
