package com.tranquangduy.api_service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tranquangduy.loadvideo.MainActivity2;
import com.tranquangduy.loadvideo.R;
import com.tranquangduy.model.Song;


import static com.tranquangduy.pushnotification.PushNotification.CHANNEL_ID;

public class MyService extends Service {
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
            sendNotification(song);
        }

        return START_NOT_STICKY;
    }



    private void sendNotification(Song song) {
        Intent intent = new Intent(this, MainActivity2.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.tvSong, song.getTittle());
        remoteViews.setImageViewResource(R.id.btnPlay, R.drawable.ic_play);
        remoteViews.setImageViewResource(R.id.btnNext, R.drawable.ic_next);
        remoteViews.setImageViewResource(R.id.btnPrevious, R.drawable.ic_previous);

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
}
