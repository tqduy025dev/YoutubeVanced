package com.tranquangduy.loadvideo;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MainActivity2 extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private ListView listView;
    private ImageButton btnDownload;
    private ProgressBar progDownload, progLoading;
    private TextView tvTitle, tvChanelTitle;
    private ImageView imgLoop, imgPlayBackground;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer tubePlayer;

    private List<Root.Items> listId = new ArrayList<>();
    private String idVideo;
    private String titleVideo;
    private String chanelTitle;
    private final String urlPath = "https://www.youtube.com/watch?v=";
    private String urlDownload = "";

    private long mLastClickTime = 0;
    private int t = 0;
    private boolean checkLoop = false;
    private boolean isPlaying = false;


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }

            isPlaying = bundle.getBoolean("status_player");
            int actionMusic = bundle.getInt("action_music");

            handleLayoutMusic(actionMusic);

        }
    };


    private final Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            progDownload.setProgress(msg.arg1);
            progDownload.setVisibility(View.VISIBLE);
            if (msg.arg2 == -1) {
                Toast.makeText(MainActivity2.this, "Lỗi!!! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                progDownload.setVisibility(View.GONE);
            }
            if (msg.obj != null) {
                if (msg.obj.equals("done")) {
                    progDownload.setProgress(0);
                    progDownload.setVisibility(View.GONE);
                    Toast.makeText(MainActivity2.this, "Download thành công !", Toast.LENGTH_SHORT).show();
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
        loadList();
        loadVideoFirst();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));

    }


    private void handleLayoutMusic(int action) {
        switch (action) {
            case MyService.ACTION_CLEAR:
                break;
            case MyService.ACTION_PAUSE:
                setStatusPlayOrPause();
                break;
            case MyService.ACTION_RESUME:
                setStatusPlayOrPause();
                break;
            case MyService.ACTION_START:
                setStatusPlayOrPause();
                break;

        }


        imgLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    if (!checkLoop) {
                        imgLoop.setImageResource(R.drawable.ic_loopped);
                        checkLoop = true;
                        sendActionToService(MyService.ACTION_LOOP);

                    } else {
                        imgLoop.setImageResource(R.drawable.ic_loop);
                        checkLoop = false;
                        sendActionToService(MyService.ACTION_LOOPPED);
                    }
                }

            }
        });


    }


    private void setStatusPlayOrPause() {
        if (isPlaying) {
            tubePlayer.pause();
        }
        tubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {
                if (isPlaying) {
                    sendActionToService(MyService.ACTION_PAUSE);
                }
            }

            @Override
            public void onPaused() {

            }

            @Override
            public void onStopped() {

            }

            @Override
            public void onBuffering(boolean b) {

            }

            @Override
            public void onSeekTo(int i) {

            }
        });
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("action_music_broadcast", action);
        startForegroundService(intent);
    }


    private void loadVideoFirst() {
        if (idVideo != null && listId.size() != 0) {
            urlDownload = urlPath.concat(idVideo);
            youTubePlayerView.initialize(String.valueOf(R.string.text_key), MainActivity2.this);
        }
    }

    private void loadList() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("BUNDLE");
        listId = (List<Root.Items>) bundle.getSerializable("ListVideo");
        idVideo = bundle.getString("IDPlaying");
        titleVideo = bundle.getString("TitleVideo");
        chanelTitle = bundle.getString("ChanelTitle");
        tvChanelTitle.setText(chanelTitle);
        tvTitle.setText(titleVideo);
        CustomAdapter customAdapter = new CustomAdapter(MainActivity2.this, R.layout.items_video, listId);
        listView.setAdapter(customAdapter);
    }

    private void linkView() {
        listView = findViewById(R.id.listView);
        btnDownload = findViewById(R.id.btnDownload);
        progDownload = findViewById(R.id.progDownload);
        tvTitle = findViewById(R.id.tvTitle);
        tvChanelTitle = findViewById(R.id.tvChanelTitle);
        imgLoop = findViewById(R.id.imgLoop);
        youTubePlayerView = findViewById(R.id.youtubePlayerView);
        imgPlayBackground = findViewById(R.id.imgPlayBackground);
        progLoading = findViewById(R.id.progLoading);
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


        imgPlayBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnService();
                        }
                    }).start();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }

    private void runOnService() {
        try {
            YoutubeDL.getInstance().init(getApplication());
            FFmpeg.getInstance().init(getApplication());

            YoutubeDLRequest request = new YoutubeDLRequest(urlDownload);
            request.addOption("-f", "best");
            VideoInfo streamInfo = YoutubeDL.getInstance().getInfo(request);

            Intent intent = new Intent(MainActivity2.this, MyService.class);
            Song song = new Song(streamInfo.getTitle(), streamInfo.getThumbnail(), streamInfo.getUrl());
            Bundle bundle = new Bundle();
            bundle.putSerializable("obj_song", song);
            bundle.putSerializable("list_video", (Serializable) listId);
            bundle.putString("id_playing", idVideo);
            bundle.putString("title", titleVideo);
            bundle.putString("channel_title", chanelTitle);
            intent.putExtras(bundle);
            startForegroundService(intent);


        } catch (InterruptedException | YoutubeDLException e) {
            e.printStackTrace();
            Message msg = mainHandler.obtainMessage();
            msg.arg2 = -1;
            mainHandler.sendMessage(msg);
        }

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

                    YoutubeDL.getInstance().execute(request, callback);


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
    protected void onDestroy() {
        if (tubePlayer != null) {
            tubePlayer.release();
        }
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(!isPlaying){
            youTubePlayer.loadVideo(idVideo);
        }
        tubePlayer = youTubePlayer;
        progLoading.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Root.Items items = listId.get(position);
                if (!idVideo.equals(items.id.getVideoId())) {
                    idVideo = items.id.getVideoId();
                    youTubePlayer.loadVideo(idVideo);

                    titleVideo = "";
                    chanelTitle = "";
                    urlDownload = "";
                    titleVideo = items.snippet.title;
                    chanelTitle = items.snippet.channelTitle;
                    urlDownload = urlPath.concat(idVideo);
                }

            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Init Fail!", Toast.LENGTH_SHORT).show();
    }

}

