package com.ghostman.musicplayer.PlayerHelper;

public class Playlists {

    private Long playlist_id;
    private String playlist_name;
    private int playlist_nos;

    // Getter Functions
    public Long getPlaylist_id() {
        return playlist_id;
    }

    public String getPlaylist_name() {
        return playlist_name;
    }

    public int getPlaylist_nos() {
        return playlist_nos;
    }


    // Setter Functions
    public void setPlaylist_id(Long playlist_id) {
        this.playlist_id = playlist_id;
    }

    public void setPlaylist_name(String playlist_name) {
        this.playlist_name = playlist_name;
    }

    public void setPlaylist_nos(int playlist_nos) {
        this.playlist_nos = playlist_nos;
    }
}
