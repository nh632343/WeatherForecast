package com.example.chaos.weatherforecast.Detail;

/**
 * Created by chaos on 2016/2/16.
 */
public class WeaInfo {
    private final String hour;
    private final String tq;
    private final String temp;

    public WeaInfo(String hour, String tq, String temp) {
        this.hour = hour;
        this.tq = tq;
        this.temp = temp;
    }

    public String getHour() {
        return hour;
    }

    public String getTq() {
        return tq;
    }

    public String getTemp() {
        return temp;
    }
}
