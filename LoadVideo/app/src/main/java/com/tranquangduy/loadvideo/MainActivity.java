package com.tranquangduy.loadvideo;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.tranquangduy.adapter.CustomAdapter;
import com.tranquangduy.api_service.InterfaceAPI;
import com.tranquangduy.model.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{
    String API_KEY = "AIzaSyD8rO5MjZrxJhqHfS2Lub5l4KNX5FhQs7o";
    String API_KEY1 = "AIzaSyC-gfCdT68Q50VaItTKL_QqWgI-HsbnV8E";

    private static final String part = "snippet";
    private static final String type = "video";
    private static final int maxResults = 40;

    private EditText txtSearch;
    private ImageButton btnSearch;
    private ListView listViewPlayer;
    private ProgressBar progressBar;

    private Root rootAPI = null;
    private final List<Root.Items> listIdVideo = new ArrayList<>();
    private CustomAdapter customAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addView();
        addEvent();
        txtSearch.setText("Tát nươc đầu đình!");


    }

    //nhan nut back tren android
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void addView() {
        txtSearch = findViewById(R.id.edtSearchVideo);
        progressBar = findViewById(R.id.progressBar);
        btnSearch = findViewById(R.id.btnSearch);
        listViewPlayer = findViewById(R.id.listViewPlayer);
    }

    private void loadList() {
        customAdapter = new CustomAdapter(MainActivity.this, R.layout.items_video, listIdVideo);
        listViewPlayer.setAdapter(customAdapter);
    }

    private void addEvent() {
        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    btnSearch.callOnClick();
                    return true;
                }
                return false;
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtSearch.getText().length() != 0) {
                    // khong cho phep nguoi dung thao tac khi progress dang xu ly
                    progressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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
        InterfaceAPI.api.searchVideo(part, API_KEY, maxResults, type, resultSearch).enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                if (response.isSuccessful()) {
                    rootAPI = response.body();
                    if (rootAPI.getItems().size() != 0) {
                        listIdVideo.addAll(rootAPI.items);
                        loadList();

                        // thoat progress cho phep nguoi dung thao tac lai
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    }
                } else {
                    Toast.makeText(MainActivity.this, "Reponse Fail", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call Fail !!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }


}