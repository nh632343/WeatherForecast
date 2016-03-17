package com.example.chaos.weatherforecast.Detail;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.chaos.weatherforecast.R;

import java.util.List;

/**
 * Created by chaos on 2016/2/16.
 */
public class WeaInfoAdapter extends ArrayAdapter<WeaInfo> {
    private int layout_id;
    public WeaInfoAdapter(Context context, int resource, List<WeaInfo> objects) {
        super(context, resource, objects);
        layout_id=resource;
    }
    //------------内部类 用于重复加载时节省时间 -----------
    class ViewHolder{
       TextView tv_hour;
        TextView tv_tianqi;
        TextView tv_qiwen;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WeaInfo weaInfo=getItem(position);
        ViewHolder viewHolder;
        View view;
        if(convertView==null){  //布局没有缓存
            view= LayoutInflater.from(getContext()).inflate(layout_id,null); //载入布局
            viewHolder=new ViewHolder();
            viewHolder.tv_hour=(TextView)view.findViewById(R.id.tv_hour); //存入控件id
            viewHolder.tv_tianqi=(TextView)view.findViewById(R.id.tv_tianqi); //存入控件id
            viewHolder.tv_qiwen=(TextView)view.findViewById(R.id.tv_qiwen); //存入控件id
            view.setTag(viewHolder);
        }
        else { //布局已缓存  直接获取
            view=convertView;
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.tv_hour.setText(weaInfo.getHour());
        viewHolder.tv_tianqi.setText(weaInfo.getTq());
        viewHolder.tv_qiwen.setText(weaInfo.getTemp());
        if(weaInfo.getTq().contains("雨")){
            viewHolder.tv_hour.setTextColor(Color.BLUE);
            viewHolder.tv_tianqi.setTextColor(Color.BLUE);
            viewHolder.tv_qiwen.setTextColor(Color.BLUE);
        }
        else{
            viewHolder.tv_hour.setTextColor(Color.BLACK);
            viewHolder.tv_tianqi.setTextColor(Color.BLACK);
            viewHolder.tv_qiwen.setTextColor(Color.BLACK);
        }
        return view;
    }
}
