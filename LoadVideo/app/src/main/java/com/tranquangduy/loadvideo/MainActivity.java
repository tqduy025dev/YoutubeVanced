package com.tranquangduy.loadvideo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.tranquangduy.adapter.CustomAdapter;
import com.tranquangduy.api.APIService;
import com.tranquangduy.model.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    String StrAPI = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&q=the%20weeknd&key=AIzaSyD8rO5MjZrxJhqHfS2Lub5l4KNX5FhQs7o";
    String API_KEY = "AIzaSyD8rO5MjZrxJhqHfS2Lub5l4KNX5FhQs7o";
    String API_KEY1 = "AIzaSyC-gfCdT68Q50VaItTKL_QqWgI-HsbnV8E";

    private static final String part = "snippet";
    private static final String type = "video";
    private static final int maxResults = 40;
    private static final int REQUEST_VIDEO = 1;

    private EditText txtSearch;
    private ImageButton btnSearch;
    private YouTubePlayerView youTubePlayerView;
    private ListView listViewPlayer;

    private Root rootAPI = null;
    private final List<Root.Items> listIdVideo = new ArrayList<>();
    private CustomAdapter customAdapter;
    private String videoPlaying = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addView();
        addEvent();

    }


    private void addView() {
        txtSearch = findViewById(R.id.edtSearchVideo);
        btnSearch = findViewById(R.id.btnSearch);
        listViewPlayer = findViewById(R.id.listViewPlayer);
    }

    private void loadList() {
        customAdapter = new CustomAdapter(MainActivity.this, R.layout.items_video, listIdVideo);
        listViewPlayer.setAdapter(customAdapter);
    }

    private void addEvent() {
        //onlick button search
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtSearch.getText().length() != 0) {
                    listIdVideo.clear();
                    clickCallAPI();
                }
            }
        });

        listViewPlayer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Root.Items temp = (Root.Items) customAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("ListVideo", (Serializable) listIdVideo);
                bundle.putString("IDPlaying", temp.id.getVideoId());
                intent.putExtra("BUNDLE", bundle);
                startActivity(intent);

            }
        });

    }

    private void clickCallAPI() {
        String resultSearch = txtSearch.getText() + "";

        APIService.api.searchVideo(part, API_KEY1, maxResults, type, resultSearch).enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                if (response.isSuccessful()) {
                    rootAPI = response.body();
                    if (rootAPI.getItems().size() != 0) {
                        listIdVideo.addAll(rootAPI.items);
                        loadList();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Reponse Fail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call Fail !!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}