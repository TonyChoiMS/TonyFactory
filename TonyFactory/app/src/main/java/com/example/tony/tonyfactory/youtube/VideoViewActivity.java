package com.example.tony.tonyfactory.youtube;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.example.tony.tonyfactory.Constants;
import com.example.tony.tonyfactory.R;

/**
 * Created by Administrator on 2016-11-28.
 */

public class VideoViewActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayerView playerView;
    private YouTubePlayer player;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_video);
        playerView = (YouTubePlayerView) findViewById(R.id.youtubePlayer);
        playerView.initialize(Constants.DeveloperKey, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        player = youTubePlayer;

        Intent intent = getIntent();
        //유투브 Id값을 이용해서 비디오를 실행
        player.loadVideo(intent.getStringExtra("id"));
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "네트워크를 확인해주세요.", Toast.LENGTH_SHORT).show();
    }
}
