package com.tranquangduy.loadvideo;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.tranquangduy.adapter.CustomAdapter;
import com.tranquangduy.api_service.MyService;
import com.tranquangduy.model.Root;
import com.tranquangduy.model.Song;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private YouTubePlayerView youTubePlayerView;
    private ListView listView;

    private List<Root.Items> listId = new ArrayList<>();
    private String idVideo = "";
    private CustomAdapter customAdapter;
    String API_KEY1 = "AIzaSyC-gfCdT68Q50VaItTKL_QqWgI-HsbnV8E";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        youTubePlayerView = findViewById(R.id.youtubePlayer);
        listView = findViewById(R.id.listView);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("BUNDLE");
        listId = (List<Root.Items>) bundle.getSerializable("ListVideo");
        idVideo = bundle.getString("IDPlaying").trim();


        customAdapter = new CustomAdapter(MainActivity2.this, R.layout.items_video, listId);
        listView.setAdapter(customAdapter);

        if (!idVideo.isEmpty() && listId.size() != 0) {
            youTubePlayerView.initialize(API_KEY1, MainActivity2.this);
            youTubePlayerView.setClipToPadding(true);
        }

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo(idVideo);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Root.Items items = listId.get(position);
                if (!idVideo.equals(items.id.getVideoId())) {
                    youTubePlayer.loadVideo(items.id.getVideoId());
                    idVideo = items.id.getVideoId();

                    Intent intent = new Intent(MainActivity2.this, MyService.class);
                    Song song = new Song(items.snippet.title,items.snippet.thumbnails.high.url,1);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("SongObj", song);
                    intent.putExtras(bundle);

                    startService(intent);

                }

            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Init fail", Toast.LENGTH_SHORT).show();
    }



}