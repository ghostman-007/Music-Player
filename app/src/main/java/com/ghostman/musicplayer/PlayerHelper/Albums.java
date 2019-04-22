package com.ghostman.musicplayer.PlayerHelper;

public class Albums {

    private long album_id;
    private String album_name;
    private String artist_name;
    private int number_of_Songs;
    private String album_image;

    // Getter Functions
    public long getAlbum_id() {
        return album_id;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public int getNumber_of_Songs() {
        return number_of_Songs;
    }

    public String getAlbum_image() {
        return album_image;
    }

    // Setter Functions
    public void setAlbum_id(long album_id) {
        this.album_id = album_id;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public void setNumber_of_Songs(int number_of_Songs) {
        this.number_of_Songs = number_of_Songs;
    }

    public void setAlbum_image(String album_image) {
        this.album_image = album_image;
    }
}
