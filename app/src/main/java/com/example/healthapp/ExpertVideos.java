package com.example.healthapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class ExpertVideos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_videos);

        YouTubePlayerView youTubePlayerView1 = findViewById(R.id.youtubePlayer1);
        getLifecycle().addObserver(youTubePlayerView1);
        loadYouTubeVideo(youTubePlayerView1, "oMfuX_bhrDw");


    }

    // Helper method to load the YouTube video
    private void loadYouTubeVideo(YouTubePlayerView youTubePlayerView, String videoId) {
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
            }
        });
    }
}
