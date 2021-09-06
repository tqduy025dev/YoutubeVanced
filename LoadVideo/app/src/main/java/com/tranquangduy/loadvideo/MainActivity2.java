package com.tranquangduy.loadvideo;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.tranquangduy.adapter.CustomAdapter;
import com.tranquangduy.api_service.MyService;
import com.tranquangduy.model.Root;
import com.tranquangduy.model.Song;
import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.DownloadProgressCallback;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;
import com.yausername.youtubedl_android.mapper.VideoInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity2 extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private YouTubePlayerView youTubePlayerView;
    private ListView listView;
    private ImageButton btnDownload;
    private TextView tvSttDownload;
    private ProgressBar progDownload;

    private List<Root.Items> listId = new ArrayList<>();
    private String idVideo = "";
    private final String urlPath = "https://www.youtube.com/watch?v=";
    private String urlDownload = "";
    private final String API_KEY1 = "AIzaSyC-gfCdT68Q50VaItTKL_QqWgI-HsbnV8E";

    private Thread thread;
    private Runnable a;
    private long mLastClickTime = 0;
    private int t = 0;

    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            tvSttDownload.setText(msg.arg1 + "%");
            progDownload.setProgress(msg.arg1);
            if (msg.arg2 == -1) {
                Toast.makeText(MainActivity2.this, "Download Thất Bại!", Toast.LENGTH_SHORT).show();
            }
            if (msg.obj != null) {
                if (msg.obj.equals("done")) {
                    progDownload.setProgress(0);
                    Toast.makeText(MainActivity2.this, "Download Thành Công !", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private final DownloadProgressCallback callback = (progress, etaInSeconds) -> {
        System.out.println(progress + "% (ETA " + etaInSeconds + " seconds)");
        Message msg = mainHandler.obtainMessage();
        msg.arg1 = (int) progress;

        if (((int) progress) == 100) {
            t++;
        }
        if (t == 3 || t == 2) {
            msg.obj = "done";
            t = 0;
        }

        mainHandler.sendMessage(msg);
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        linkView();
        addEvent();

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("BUNDLE");
        listId = (List<Root.Items>) bundle.getSerializable("ListVideo");
        idVideo = bundle.getString("IDPlaying").trim();

        CustomAdapter customAdapter = new CustomAdapter(MainActivity2.this, R.layout.items_video, listId);
        listView.setAdapter(customAdapter);

        if (!idVideo.isEmpty() && listId.size() != 0) {
            youTubePlayerView.initialize(API_KEY1, MainActivity2.this);
            urlDownload = urlPath.concat(idVideo);
        }

    }

    private void linkView() {
        youTubePlayerView = findViewById(R.id.youtubePlayer);
        listView = findViewById(R.id.listView);
        btnDownload = findViewById(R.id.btnDownload);
        tvSttDownload = findViewById(R.id.tvStatusDownload);
        progDownload = findViewById(R.id.progDownload);
    }

    private void addEvent() {

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    Toast.makeText(MainActivity2.this, "Nhấn quá nhiều!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                checkPermission();
            }
        });
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, 1);

            } else {
                startDownload();
            }

        } else {
            startDownload();
        }


    }

    private void startDownload() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    YoutubeDL.getInstance().init(getApplication());
                    FFmpeg.getInstance().init(getApplication());

                    File youtubeDLDir = getDownloadLocation();
                    YoutubeDLRequest request = new YoutubeDLRequest(urlDownload);
                    request.addOption("-f bestvideo[ext=mp4]+bestaudio[ext=m4a]/mp4");
                    request.addOption("-o", youtubeDLDir.getAbsolutePath() + "/%(title)s.%(ext)s");


//                    YoutubeDL.getInstance().execute(request, (progress, etaInSeconds) -> {
//                        System.out.println(progress + "% (ETA " + etaInSeconds + " seconds)");
//                        Message msg = mainHandler.obtainMessage();
//                        msg.arg1 = (int) progress;
//                        mainHandler.sendMessage(msg);
//                        callback.onProgressUpdate(progress, etaInSeconds);
//                    });


                    YoutubeDL.getInstance().execute(request, callback);
//


                } catch (YoutubeDLException | InterruptedException e) {
                    e.printStackTrace();
                    Message msg = mainHandler.obtainMessage();
                    msg.arg2 = -1;
                    mainHandler.sendMessage(msg);
                }


            }
        }).start();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownload();
            } else {
                Toast.makeText(this, "Chưa cấp quyền!!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @NonNull
    private File getDownloadLocation() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File youtubeDLDir = new File(downloadsDir, "youtubedl-android");
        if (!youtubeDLDir.exists()) youtubeDLDir.mkdir();
        return youtubeDLDir;
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

                    urlDownload = "";
                    urlDownload = urlPath.concat(idVideo);

                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {


                                YoutubeDL.getInstance().init(getApplication());
                                FFmpeg.getInstance().init(getApplication());


                                YoutubeDLRequest request = new YoutubeDLRequest(urlDownload);
                                request.addOption("-f", "bestaudio");
                                VideoInfo streamInfo = YoutubeDL.getInstance().getInfo(request);

                                //start foreground
                                Intent intent = new Intent(MainActivity2.this, MyService.class);
                                Song song = new Song(items.snippet.title, items.snippet.thumbnails.high.url, streamInfo.getUrl());
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("SongObj", song);
                                intent.putExtras(bundle);

                                startForegroundService(intent);


                            } catch (YoutubeDLException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();



                }

            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Init fail", Toast.LENGTH_SHORT).show();
    }


}

