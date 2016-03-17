package com.example.chaos.weatherforecast.BackgroundService;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.example.chaos.weatherforecast.Detail.DetailActivity;
import com.example.chaos.weatherforecast.R;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by chaos on 2016/2/15.
 */
public class WeatherService extends Service {
    private SharedPreferences weaPreferences;
    private NetReceiver netReceiver;
    private SharedPreferences.Editor editor;
    private SQLiteDatabase db;
    private ContentValues values;


    @Nullable


    //--------------此方法发出通知----------------------
    void noti(String title,String text,int color,Class c,int id){
        PendingIntent pendingIntent=null;
       //---------设置点击通知的转跳-------------
        if(c!=null){
        Intent intent=new Intent(WeatherService.this,c);
        pendingIntent=PendingIntent.getActivity(WeatherService.this,0,
                intent,PendingIntent.FLAG_CANCEL_CURRENT);}
        //----------生成notification--------------
        Notification.Builder builder=new Notification.Builder(WeatherService.this)
                .setContentTitle(title)
                .setContentText(text);
        if(c!=null){
            builder.setContentIntent(pendingIntent);
        }
        if(id==1){
            builder.setSmallIcon(R.drawable.rain_small);
            Bitmap bm= BitmapFactory.decodeResource(getResources(),R.drawable.rain);
            builder.setLargeIcon(bm);
        }
        else{
            builder.setSmallIcon(R.drawable.warn);
        }
        Notification notification=builder.build();
        notification.ledARGB=color;
        notification.ledOnMS=2000;
        notification.ledOffMS=1000;
        //----------闪光和点击取消-----------------
        notification.flags=Notification.FLAG_SHOW_LIGHTS|Notification.FLAG_AUTO_CANCEL;
        //----------发出通知------------------
        NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(id,notification);
    }

    //--------此方法修改SharedPreferences库，value为boolean类型-------
    void editPreferencesBoolean(String key,Boolean value){
        if(editor==null){    //若editor未初始化，则初始化
            editor=weaPreferences.edit();
        }
        editor.putBoolean(key,value);

        editor.apply();   //提交

    }

    //--------此方法修改SharedPreferences库，value为String类型-------
    void editPreferencesString(String key,String value){
        if(editor==null){    //若editor未初始化，则初始化
            editor=weaPreferences.edit();
        }
        editor.putString(key, value);
        editor.apply();   //提交

    }

    //-----------自定异常------------------
    class ContentException extends Exception{}
    class NoConnectionException extends Exception{}

    //----------网络-------------------
    class GetWea extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            String text="";
            try{
            HttpClient httpClient=new DefaultHttpClient();
            HttpGet httpGet=new HttpGet("http://tianqi.2345.com/t/wea_hour_js/"+params[0]+"_1.js");
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if(httpResponse.getStatusLine().getStatusCode()==200){
                HttpEntity httpEntity=httpResponse.getEntity();
                text= EntityUtils.toString(httpEntity,"gbk");
                }
            else{
                throw new NoConnectionException();
            }
            }catch (Exception e){
                noti("Connection Problem","网络连接出错",
                        Color.RED,null,2);
                editPreferencesBoolean("run", false);
                e.printStackTrace();
            }

            return text;
        }

        @Override
        protected void onPostExecute(String s) {

                try {
                    //从内容中提取出JSON
                    Pattern pattern=Pattern.compile("\\[\\{.+\\}\\]");
                    Matcher matcher=pattern.matcher(s);
                    if(matcher.find()){
                        s=matcher.group();
                    }
                    else{
                        throw new ContentException();
                    }
                    //JSON解析
                    JSONArray jsonArray=new JSONArray(s);
                    if(jsonArray.length()!=24){
                        throw new ContentException();
                    }
                    //  把get置为true
                    editPreferencesBoolean("get",true);
                    //天气遍历
                    int i;
                    for (i=6;i<22;++i){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        //如果有雨
                        if(jsonObject.getString("tq").contains("雨")){
                            break;
                        }
                    }
                    if(i!=22){
                        editPreferencesString("hour", String.valueOf(i));
                        //把小时天气放入数据库
                        db=new WeatherDatabaseHelper(WeatherService.this,"base.db",null,1).getWritableDatabase();
                        Cursor cursor=db.query("wea_hour",null,null,null,null,null,null);
                        if(!cursor.moveToFirst()){
                            ContentValues contentValues=new ContentValues();
                            for(int k=0;k<24;++k){
                                contentValues.clear();
                                contentValues.put("temp", "");
                                contentValues.put("tianqi","");
                                db.insert("wea_hour",null,contentValues);
                            }
                        }
                        cursor.close();
                        values=new ContentValues();
                        for(int j=0;j<jsonArray.length();++j){
                            JSONObject object=jsonArray.getJSONObject(j);
                            values.clear();
                            values.put("temp", object.getString("temp"));
                            values.put("tianqi",object.getString("tq"));
                            db.update("wea_hour",values,
                                    "id=?",new String[]{String.valueOf(j+1)});
                        }
                        noti("有雨","明天"+weaPreferences.getString("hour","0")+"点有雨", Color.GREEN,DetailActivity.class,1);
                    }
                } catch (ContentException e){
                    noti("Content Problem","获取内容出错",
                            Color.RED,null,3);
                    editPreferencesBoolean("run", false);
                    e.printStackTrace();
                }
                catch (Exception e){
                    noti("Problem","出现了错误",
                            Color.RED,null,4);
                    editPreferencesBoolean("run", false);
                    e.printStackTrace();
                }
               finally {
                    stopSelf();
                }
            }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        weaPreferences= getSharedPreferences("wea", MODE_APPEND);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(weaPreferences.getBoolean("run",false)){     //app是否要运行
            Calendar calendar=Calendar.getInstance();
            if(calendar.get(Calendar.HOUR_OF_DAY)>21){       //是否到了22点
                if(weaPreferences.getBoolean("get",false)){//获取了天气
                    noti("有雨","明天"+weaPreferences.getString("hour","0")+"点有雨", Color.GREEN,DetailActivity.class,1);
                    stopSelf();
                }
                else{    //未获取天气
                    ConnectivityManager connectivityManager= (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
                    if(networkInfo!=null&&networkInfo.isAvailable()){  //有网络
                        if(netReceiver!=null){   //不为null说明已注册网络监听，需要解绑
                            unregisterReceiver(netReceiver);
                        }
                        String place=weaPreferences.getString("placeNum","57494");
                        new GetWea().execute(place);
                    }
                    else{        //无网络
                        if (netReceiver==null){
                            //注册网络监听器
                            IntentFilter intentFilter=new IntentFilter();
                            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                            netReceiver=new NetReceiver();
                            registerReceiver(netReceiver,intentFilter);

                        }
                    }
                }
            }
            else{    //未到22
                AlarmManager alarmManager= (AlarmManager) getSystemService(WeatherService.this.ALARM_SERVICE);
                PendingIntent pendingIntent=PendingIntent.getService(WeatherService.this, 0,
                        new Intent(WeatherService.this, WeatherService.class),
                        PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime()+25*60*1000,
                        pendingIntent);
                stopSelf();
            }
        }
        else{     //run为false
            if(netReceiver!=null){   //不为null说明已注册网络监听，需要解绑
                unregisterReceiver(netReceiver);
            }
            stopSelf();}
        return super.onStartCommand(intent, flags, startId);
    }

}
