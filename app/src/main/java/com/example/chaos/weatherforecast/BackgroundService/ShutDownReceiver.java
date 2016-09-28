package com.example.chaos.weatherforecast.BackgroundService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ShutDownReceiver extends BroadcastReceiver {
    public ShutDownReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       SharedPreferences weaPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=weaPreferences.edit();
        editor.putBoolean("get",false);
        editor.apply();
    }
}
