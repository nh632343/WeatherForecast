package com.example.chaos.weatherforecast.BackgroundService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by chaos on 2016/2/15.
 */
public class PowerOnReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent=new Intent(context,WeatherService.class);
        context.startService(myIntent);
    }
}
