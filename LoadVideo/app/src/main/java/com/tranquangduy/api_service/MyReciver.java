package com.tranquangduy.api_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra("action_music", -1);

        Intent intent_to_service = new Intent(context, MyService.class);
        intent_to_service.putExtra("action_music_broadcast", action);

        context.startService(intent_to_service);
    }
}
