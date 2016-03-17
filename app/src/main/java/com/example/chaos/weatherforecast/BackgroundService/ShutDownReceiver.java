package com.example.chaos.weatherforecast.BackgroundService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ShutDownReceiver extends BroadcastReceiver {
    public ShutDownReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       SharedPreferences weaPreferences= context.getSharedPreferences("wea", context.MODE_APPEND);
        SharedPreferences.Editor editor=weaPreferences.edit();
        editor.putBoolean("get",false);
        editor.apply();
    }
}
