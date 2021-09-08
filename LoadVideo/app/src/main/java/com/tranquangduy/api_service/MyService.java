package com.tranquangduy.api_service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tranquangduy.loadvideo.MainActivity2;
import com.tranquangduy.loadvideo.R;
import com.tranquangduy.model.Root;
import com.tranquangduy.model.Song;


import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static com.tranquangduy.pushnotification.PushNotification.CHANNEL_ID;

public class MyService extends Service{
    public MediaPlayer mediaPlayer;
    public static final int ACTION_CLEAR = 0;
    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_START = 3;
    public static final int ACTION_LOOP = 4;
    public static final int ACTION_LOOPPED = 5;
    private static final int REQUEST_CODE = 0;
    private boolean isPlaying = false;
    private Song mSong;
    private List<Root.Items> listId;
    private String IDPlaying, Title, ChannelTitle;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Song song = (Song) bundle.get("obj_song");

            listId = (List<Root.Items>) bundle.get("list_video");
            IDPlaying = bundle.getString("id_playing");
            Title = bundle.getString("title");
            ChannelTitle = bundle.getString("channel_title");


            if (song != null) {
                mSong = song;

                starMusic(song);
                sendNotification(song);
            }

        }

        int action = intent.getIntExtra("action_music_broadcast", -1);
        handleActionMusic(action);

        return START_NOT_STICKY;
    }


    private void handleActionMusic(int action) {
        switch (action) {
            case ACTION_PAUSE:
                pause();
                break;
            case ACTION_RESUME:
                resume();
                break;
            case ACTION_CLEAR:
                stopSelf();
                sendActionToActivity(ACTION_CLEAR);
                break;
            case ACTION_LOOP:
                mediaPlayer.setLooping(true);
                break;
            case ACTION_LOOPPED:
                mediaPlayer.setLooping(false);
                break;
        }
    }


    private void resume() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
            sendNotification(mSong);
            sendActionToActivity(ACTION_RESUME);
        }
    }

    private void pause() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            sendNotification(mSong);
            sendActionToActivity(ACTION_PAUSE);
        }
    }


    private void starMusic(Song song) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.getUrl());
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    isPlaying = true;
                }
            });
            isPlaying = true;


            sendActionToActivity(ACTION_START);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void sendNotification(Song song) {
        Intent intent = new Intent(this, MainActivity2.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ListVideo", (Serializable) listId);
        bundle.putString("IDPlaying", IDPlaying);
        bundle.putString("TitleVideo", Title);
        bundle.putString("ChanelTitle", ChannelTitle);
        intent.putExtra("BUNDLE", bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.tvSong, song.getTittle());
        remoteViews.setTextViewText(R.id.tvSingle, song.getTittle());
        remoteViews.setImageViewResource(R.id.btnPlay, R.drawable.ic_pause);
        remoteViews.setImageViewResource(R.id.btnClose, R.drawable.ic_close);

        // su kien click notification
        if (isPlaying) {
            remoteViews.setOnClickPendingIntent(R.id.btnPlay, getPendingItent(this, ACTION_PAUSE));
            remoteViews.setImageViewResource(R.id.btnPlay, R.drawable.ic_pause);
        } else {
            remoteViews.setOnClickPendingIntent(R.id.btnPlay, getPendingItent(this, ACTION_RESUME));
            remoteViews.setImageViewResource(R.id.btnPlay, R.drawable.ic_play);
        }

        remoteViews.setOnClickPendingIntent(R.id.btnClose, getPendingItent(this, ACTION_CLEAR));


        // load anh tren notification
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(song.getImage())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        remoteViews.setImageViewBitmap(R.id.imgSong, resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(null)
                .setCustomContentView(remoteViews)
                .setSound(null)
                .build();

        startForeground(1, notification);
    }


    private PendingIntent getPendingItent(Context context, int action) {
        Intent intent = new Intent(this, MyReciver.class);
        intent.putExtra("action_music", action);

        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private void sendActionToActivity(int action){
        Intent intent = new Intent("send_data_to_activity");
        Bundle bundle = new Bundle();
        bundle.putBoolean("status_player", isPlaying);
        bundle.putInt("action_music", action);

        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
