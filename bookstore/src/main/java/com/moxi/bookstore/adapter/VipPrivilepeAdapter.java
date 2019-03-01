package com.moxi.bookstore.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.modle.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xj on 2017/11/14.
 */

public class VipPrivilepeAdapter extends BaseAdapter {

    private Context context;
    private List<KeyValue> data=new ArrayList<>();

    public VipPrivilepeAdapter(Context context, List<String> datas) {
        this.context = context;
        int index=0;
        if (datas.size()%2==1){
            index=1;
        }
        for (int i = index; i < datas.size(); i+=2) {
            data.add(new KeyValue(datas.get(i),datas.get(i+1)));
        }
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        }
        return data.size()>4?4:data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;
        if (null == v) {
            holder = new ViewHolder();
            v = View.inflate(context, R.layout.adapter_vip_privilepe, null);
            holder.icon = (ImageView) v.findViewById(R.id.icon);
            holder.title = (TextView) v.findViewById(R.id.title);
            holder.content = (TextView) v.findViewById(R.id.content);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        KeyValue model=data.get(position);
        int icoid=R.mipmap.vip_icon1;
        switch (position){
            case 1:
                icoid=R.mipmap.vip_icon2;
                break;
            case 2:
                icoid=R.mipmap.vip_icon3;
                break;
            default:
                break;
        }
        holder.icon.setImageResource(icoid);
        holder.title.setText(model.getKey());
        holder.content.setText(model.getValue());
        return v;
    }

    class ViewHolder {
        ImageView icon;
        TextView title;
        TextView content;
    }
}