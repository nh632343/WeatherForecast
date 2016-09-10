package com.example.chaos.weatherforecast.Main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chaos.weatherforecast.R;
import com.example.chaos.weatherforecast.BackgroundService.WeatherService;

public class MainActivity extends AppCompatActivity {

    LinearLayout runningLinear;
    LinearLayout stoppingLinear;
    Button button;
    SharedPreferences weaPreferences;
    SharedPreferences.Editor editor;
    EditText et_placeNum;
    EditText et_placeName;
    TextView tv_placeNumber;
    TextView tv_placeName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_placeNum= (EditText) findViewById(R.id.et_placeNum);
        et_placeName= (EditText) findViewById(R.id.et_placeName);
        tv_placeNumber= (TextView) findViewById(R.id.tv_placeNumber);
        tv_placeName= (TextView) findViewById(R.id.tv_placeName);
        runningLinear= (LinearLayout) findViewById(R.id.runningLinear);
        stoppingLinear= (LinearLayout) findViewById(R.id.stoppingLinear);
        weaPreferences=getSharedPreferences("wea", MODE_APPEND);
        editor=weaPreferences.edit();
        //-----------是否正在运行 决定显示哪一个layout
        if (weaPreferences.getBoolean("run",false)){
            stoppingLinear.setVisibility(View.GONE);
        }
        else{
            runningLinear.setVisibility(View.GONE);
        }
        //-------------显示当前城市------------
        tv_placeNumber.setText(weaPreferences.getString("placeNum","57494"));
        tv_placeName.setText(weaPreferences.getString("placeName","武汉"));
        //----------停止运行按钮--------------
        button= (Button) findViewById(R.id.btn_stop);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把run置为false
                editor.putBoolean("run",false);
                editor.putBoolean("get",false);
                editor.apply();
                AlarmManager alarmManager= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                //取消定时启动
                PendingIntent pendingIntent=PendingIntent.getService(MainActivity.this, 0,
                        new Intent(MainActivity.this, WeatherService.class),
                        PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.cancel(pendingIntent);
                //转换布局
                runningLinear.setVisibility(View.GONE);
                stoppingLinear.setVisibility(View.VISIBLE);
            }
        });
        //--------------启动按钮----------------------
        button= (Button) findViewById(R.id.btn_run);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("run", true);
                editor.apply();
                //转换布局
                stoppingLinear.setVisibility(View.GONE);
                runningLinear.setVisibility(View.VISIBLE);
                Intent intent=new Intent(MainActivity.this,WeatherService.class);
                startService(intent);

            }
        });
        //-------------更改城市按钮------------------
        button= (Button) findViewById(R.id.btn_placenum);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placeNum=et_placeNum.getText().toString();
                String placeName=et_placeName.getText().toString();
                if(placeName.equals("")||placeNum.equals("")){
                    Toast.makeText(MainActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                }
                else{
                    editor.putString("placeName",placeName);
                    editor.putString("placeNum", placeNum);
                    editor.apply();
                    tv_placeNumber.setText(weaPreferences.getString("placeNum","57494"));
                    tv_placeName.setText(weaPreferences.getString("placeName","武汉"));
                    Toast.makeText(MainActivity.this,"城市更改为"+placeNum+" "+placeName,Toast.LENGTH_SHORT).show();
                    et_placeName.setText("");
                    et_placeNum.setText("");
                }
            }

        });
    }
}
