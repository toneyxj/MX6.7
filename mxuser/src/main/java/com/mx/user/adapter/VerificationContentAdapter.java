package com.mx.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mx.user.R;

import java.util.List;

/**
 * Created by King on 2017/11/29.
 */

public class VerificationContentAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;

    public VerificationContentAdapter(Context context, List<String> list) {
        this.list = list;
        this.context = context;
    }

    public void dataChange(List<String> list){
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 15;
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.mx_item_popwindow_veritry_content, null);
        TextView tvVerification = (TextView) convertView.findViewById(R.id.tv_verification);
        try {
            String verificaitonCalue = list.get(position);
            tvVerification.setText(verificaitonCalue == null ? "" : verificaitonCalue);
        } catch (Exception e) {
            e.printStackTrace();
            tvVerification.setText("");
        }
        return convertView;
    }
}
