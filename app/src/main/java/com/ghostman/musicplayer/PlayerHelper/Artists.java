package com.ghostman.musicplayer.PlayerHelper;

public class Artists {

    private Long artist_id;
    private String artist_name;
    private Integer artist_not;

    // Getter Functions
    public Long getArtist_id() {
        return artist_id;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public Integer getArtist_not() {
        return artist_not;
    }

    // Setter Functions
    public void setArtist_id(Long artist_id) {
        this.artist_id = artist_id;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public void setArtist_not(Integer artist_not) {
        this.artist_not = artist_not;
    }
}
