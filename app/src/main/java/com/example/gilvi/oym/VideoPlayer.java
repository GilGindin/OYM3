package com.example.gilvi.oym;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

//we integrated the youtube player API a directed here: https://developers.google.com/youtube/android/player/
public class VideoPlayer extends YouTubeFailureRecoveryActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);


        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);

        // we need the api key to allow us to connect to the youtube API.
        youTubeView.initialize(DeveloperKey.DEVELOPER_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {

        // here we take the parameter we passed in our intent (see lines 57-58 in VideosAdapter.java)
        String videoId = getIntent().getExtras().get("videoId").toString();
        if (!wasRestored) {
            player.cueVideo(videoId);
        }
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }
}
