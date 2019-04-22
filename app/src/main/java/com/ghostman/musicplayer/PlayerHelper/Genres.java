package com.ghostman.musicplayer.PlayerHelper;

public class Genres {

    private Long genre_id;
    private String genre_name;
    private Integer genre_numbers;

    // Getter Functions

    public Long getGenre_id() {
        return genre_id;
    }

    public String getGenre_name() {
        return genre_name;
    }

    public Integer getGenre_numbers() {
        return genre_numbers;
    }


    // Setter Functions
    public void setGenre_id(Long genre_id) {
        this.genre_id = genre_id;
    }

    public void setGenre_name(String genre_name) {
        this.genre_name = genre_name;
    }

    public void setGenre_numbers(Integer genre_numbers) {
        this.genre_numbers = genre_numbers;
    }
}
