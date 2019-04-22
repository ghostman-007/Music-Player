package com.ghostman.musicplayer.Others;

import android.util.Log;

import com.ghostman.musicplayer.PlayerHelper.Songs;

import java.util.Random;

import static com.ghostman.musicplayer.MainActivity.updateUI;

public class Controls {

    public static void nextControl() {
        if(PlayerConstants.SONGS_LIST.size() > 1) {
            if(PlayerConstants.SHUFFLE) {
                // TODO : Use complete ArrayList Shuffle Once and use till shuffle is ON.
                Random random = new Random();
                int random_number = random.nextInt(PlayerConstants.SONGS_LIST.size());
                PlayerConstants.CURRENT_SONG_ID = PlayerConstants.SONGS_LIST.get(random_number).getSong_id();
            } else {
                for(Songs songs : PlayerConstants.SONGS_LIST) {
                    if(songs.getSong_id() == PlayerConstants.CURRENT_SONG_ID) {
                        Log.d("CURRENT_SONG_INDEX ","" + PlayerConstants.SONGS_LIST.indexOf(songs));
                        if(PlayerConstants.SONGS_LIST.indexOf(songs) < PlayerConstants.SONGS_LIST.size() - 1)
                            PlayerConstants.CURRENT_SONG_ID = PlayerConstants.SONGS_LIST
                                    .get(PlayerConstants.SONGS_LIST.indexOf(songs) + 1)
                                    .getSong_id();
                        else
                            PlayerConstants.CURRENT_SONG_ID = PlayerConstants.SONGS_LIST
                                    .get(0)
                                    .getSong_id();
                        break;
                    }
                }
            }
        }

        PlayerConstants.CURRENT_SONG_POSITION = 0;
        PlayerConstants.PLAY_HANDLER.sendMessage(PlayerConstants.PLAY_HANDLER.obtainMessage());
        updateUI();
    }

    public static void previousControl() {
        if(PlayerConstants.SONGS_LIST.size() > 1) {
            if(PlayerConstants.SHUFFLE) {
                // TODO : Use complete ArrayList Shuffle Once and use till shuffle is ON.
                Random random = new Random();
                int random_number = random.nextInt(PlayerConstants.SONGS_LIST.size());
                PlayerConstants.CURRENT_SONG_ID = PlayerConstants.SONGS_LIST.get(random_number).getSong_id();
            } else {
                for(Songs songs : PlayerConstants.SONGS_LIST) {
                    if(songs.getSong_id() == PlayerConstants.CURRENT_SONG_ID) {
                        Log.d("CURRENT_SONG_INDEX ","" + PlayerConstants.SONGS_LIST.indexOf(songs));
                        if(PlayerConstants.SONGS_LIST.indexOf(songs) > 0)
                            PlayerConstants.CURRENT_SONG_ID = PlayerConstants.SONGS_LIST
                                    .get(PlayerConstants.SONGS_LIST.indexOf(songs) - 1)
                                    .getSong_id();
                        else
                            PlayerConstants.CURRENT_SONG_ID = PlayerConstants.SONGS_LIST
                                    .get(PlayerConstants.SONGS_LIST.size() - 1)
                                    .getSong_id();
                        break;
                    }
                }
            }
        }

        PlayerConstants.CURRENT_SONG_POSITION = 0;
        PlayerConstants.PLAY_HANDLER.sendMessage(PlayerConstants.PLAY_HANDLER.obtainMessage());
        updateUI();
    }


    public static void repeatComplete() {
        if(PlayerConstants.SONGS_LIST.size() > 1) {
            if(PlayerConstants.SHUFFLE) {
                // TODO : Use complete ArrayList Shuffle Once and use till shuffle is ON.
                Random random = new Random();
                int random_number = random.nextInt(PlayerConstants.SONGS_LIST.size());
                PlayerConstants.CURRENT_SONG_ID = PlayerConstants.SONGS_LIST.get(random_number).getSong_id();
            } else {
                for(Songs songs : PlayerConstants.SONGS_LIST) {
                    if(songs.getSong_id() == PlayerConstants.CURRENT_SONG_ID) {
                        Log.d("CURRENT_SONG_INDEX ","" + PlayerConstants.SONGS_LIST.indexOf(songs));
                        if(PlayerConstants.SONGS_LIST.indexOf(songs) < PlayerConstants.SONGS_LIST.size() - 1) {
                            PlayerConstants.CURRENT_SONG_ID = PlayerConstants.SONGS_LIST
                                    .get(PlayerConstants.SONGS_LIST.indexOf(songs) + 1)
                                    .getSong_id();
                            PlayerConstants.CURRENT_SONG_POSITION = 0;
                            PlayerConstants.PLAY_HANDLER.sendMessage(PlayerConstants.PLAY_HANDLER.obtainMessage());
                            updateUI();
                        } else {
                            PlayerConstants.EXIT_HANDLER.sendMessage(PlayerConstants.EXIT_HANDLER.obtainMessage());
                        }
                        break;
                    }
                }
            }
        }
    }
}
