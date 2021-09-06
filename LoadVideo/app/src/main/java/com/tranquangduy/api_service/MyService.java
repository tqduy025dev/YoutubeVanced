package com.tranquangduy.api_service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.tranquangduy.loadvideo.MainActivity2;
import com.tranquangduy.loadvideo.R;
import com.tranquangduy.model.Song;


import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.tranquangduy.pushnotification.PushNotification.CHANNEL_ID;

public class MyService extends Service {
    private MediaPlayer mediaPlayer;
    private static final int ACTION_PAUSE = 1;
    private static final int ACTION_RESUME = 2;
    private static final int ACTION_CLEAR = 0;
    private static final int REQUEST_CODE = 0;
    private boolean isPlaying = false;
    private boolean isActive = false;
    private Song mSong;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
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
            Song song = (Song) bundle.get("SongObj");

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
                break;
        }
    }


    private void resume() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
            sendNotification(mSong);
        }
    }

    private void pause() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            sendNotification(mSong);
        }
    }


    private void starMusic(Song song) {
        try {
            if (!isPlaying) {
                mediaPlayer.setDataSource(song.getUrl());
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        isPlaying = true;
                    }
                });
            } else {
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
            }
            isPlaying = true;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void sendNotification(Song song) {
        Intent intent = new Intent(this, MainActivity2.class);
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
                .setContentIntent(pendingIntent)
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
