package com.ghostman.musicplayer.PlayerHelper;

public class Songs {

    private long song_id;
    private long song_albumId;
    private String song_path;
    private String song_title;
    private String song_artist;
    private String song_album;
    private String song_composer;
    private long song_duration;
    private boolean song_selected;
    private boolean song_isFavorite;

    // Getter Functions
    public long getSong_id() {
        return song_id;
    }

    public long getSong_albumId() {
        return song_albumId;
    }

    public String getSong_path() {
        return song_path;
    }

    public String getSong_title() {
        return song_title;
    }

    public String getSong_artist() {
        return song_artist;
    }

    public String getSong_album() {
        return song_album;
    }

    public String getSong_composer() {
        return song_composer;
    }

    public long getSong_duration() {
        return song_duration;
    }

    public boolean isSong_selected() {
        return song_selected;
    }

    public boolean isSong_isFavorite() {
        return song_isFavorite;
    }


    // Setter Functions
    public void setSong_id(long song_id) {
        this.song_id = song_id;
    }

    public void setSong_albumId(long song_albumId) {
        this.song_albumId = song_albumId;
    }

    public void setSong_path(String song_path) {
        this.song_path = song_path;
    }

    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }

    public void setSong_artist(String song_artist) {
        this.song_artist = song_artist;
    }

    public void setSong_album(String song_album) {
        this.song_album = song_album;
    }

    public void setSong_composer(String song_composer) {
        this.song_composer = song_composer;
    }

    public void setSong_duration(long song_duration) {
        this.song_duration = song_duration;
    }

    public void setSong_selected(boolean song_selected) {
        this.song_selected = song_selected;
    }

    public void setSong_Favorite(boolean song_isFavorite) {
        this.song_isFavorite = song_isFavorite;
    }
}
