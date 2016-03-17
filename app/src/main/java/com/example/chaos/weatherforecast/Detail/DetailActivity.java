package com.example.chaos.weatherforecast.Detail;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.chaos.weatherforecast.BackgroundService.WeatherDatabaseHelper;
import com.example.chaos.weatherforecast.BackgroundService.WeatherService;
import com.example.chaos.weatherforecast.R;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ArrayList<WeaInfo>  arrayList;
    private WeaInfoAdapter weaInfoAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //------------建立天气信息数组-----------
        arrayList=new ArrayList<WeaInfo>();
        arrayList.clear();
        SQLiteDatabase db=new WeatherDatabaseHelper(DetailActivity.this,"base.db",null,1).getWritableDatabase();
        Cursor cursor=db.query("wea_hour",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                String hour=String.valueOf(cursor.getInt(cursor.getColumnIndex("id"))-1);
                String tq=cursor.getString(cursor.getColumnIndex("tianqi"));
                String temp=cursor.getString(cursor.getColumnIndex("temp"));
                arrayList.add(new WeaInfo(hour,tq,temp));
            }while (cursor.moveToNext());
        }
        cursor.close();
        //----------为listView设置adapter---------
        weaInfoAdapter=new WeaInfoAdapter(this,R.layout.layout_item,arrayList);
        listView= (ListView) findViewById(R.id.listView);
        listView.setAdapter(weaInfoAdapter);
        //----------知道了按钮 按下后退出----------------
        Button button= (Button) findViewById(R.id.btn_know);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //----------稍后提醒按钮 5分钟后弹出通知------------
        button= (Button) findViewById(R.id.btn_remind);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager alarmManager= (AlarmManager) getSystemService(DetailActivity.this.ALARM_SERVICE);
                PendingIntent pendingIntent=PendingIntent.getService(DetailActivity.this, 0,
                        new Intent(DetailActivity.this, WeatherService.class),
                        PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime()+5*60*1000,
                        pendingIntent);
                finish();
            }
        });
    }
}
