package com.ghostman.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.ghostman.musicplayer.Others.Controls;
import com.ghostman.musicplayer.Others.PlayerConstants;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if(!PlayerConstants.SONG_PAUSED){
                        Log.d("TAG", "TAG: KEYCODE_MEDIA_PAUSE");
                        PlayerConstants.SONG_PAUSED = true;
                        PlayerConstants.PAUSE_HANDLER.sendMessage(PlayerConstants.PAUSE_HANDLER.obtainMessage());
                    }else{
                        PlayerConstants.SONG_PAUSED = false;
                        PlayerConstants.PLAY_HANDLER.sendMessage(PlayerConstants.PLAY_HANDLER.obtainMessage());
                        Log.d("TAG", "TAG: KEYCODE_MEDIA_PLAY");
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    PlayerConstants.SONG_PAUSED = false;
                    PlayerConstants.PLAY_HANDLER.sendMessage(PlayerConstants.PLAY_HANDLER.obtainMessage());
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    PlayerConstants.SONG_PAUSED = true;
                    PlayerConstants.PAUSE_HANDLER.sendMessage(PlayerConstants.PAUSE_HANDLER.obtainMessage());
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    PlayerConstants.SONG_PAUSED = true;
                    PlayerConstants.STOP_HANDLER.sendMessage(PlayerConstants.STOP_HANDLER.obtainMessage());
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    PlayerConstants.SONG_PAUSED = false;
                    Controls.nextControl();
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_NEXT");
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    PlayerConstants.SONG_PAUSED = false;
                    Controls.previousControl();
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_PREVIOUS");
                    break;
            }
        }  else {
            if (intent.getAction().equals(PlayerService.NOTIFY_PLAY)) {
                PlayerConstants.SONG_PAUSED = false;
                PlayerConstants.PLAY_HANDLER.sendMessage(PlayerConstants.PLAY_HANDLER.obtainMessage());
            } else if (intent.getAction().equals(PlayerService.NOTIFY_PAUSE)) {
                Log.d("...", "TAG: KEYCODE_MEDIA_PAUSE");
                PlayerConstants.SONG_PAUSED = true;
                PlayerConstants.PAUSE_HANDLER.sendMessage(PlayerConstants.PAUSE_HANDLER.obtainMessage());
            } else if (intent.getAction().equals(PlayerService.NOTIFY_NEXT)) {
                PlayerConstants.SONG_PAUSED = false;
                Controls.nextControl();
            } else if (intent.getAction().equals(PlayerService.NOTIFY_PREVIOUS)) {
                PlayerConstants.SONG_PAUSED = false;
                Controls.previousControl();
            } else if (intent.getAction().equals(PlayerService.NOTIFY_EXIT)) {
                PlayerConstants.SONG_PAUSED = true;
                Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                context.sendBroadcast(it);
                PlayerConstants.PAUSE_HANDLER.sendMessage(PlayerConstants.PAUSE_HANDLER.obtainMessage());
                PlayerConstants.EXIT_HANDLER.sendMessage(PlayerConstants.EXIT_HANDLER.obtainMessage());
            }
        }

        try {
            MainActivity.updateUI();
        }catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }
}
