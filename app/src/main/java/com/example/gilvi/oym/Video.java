package com.example.gilvi.oym;

// this class represents a video
class Video {

    String title;
    String artist;
    String thumbnailUrl;
    String id; // the video id. we need it if we want to play the video using the YoutubePlayer in Android

    public Video(String title, String artist, String thumbnailUrl, String id) {
        this.title = title;
        this.artist = artist;
        this.thumbnailUrl = thumbnailUrl;
        this.id = id;
    }

}
