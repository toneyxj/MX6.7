package com.mx.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.mx.user.R;

import java.util.List;

/**
 * Created by King on 2017/12/4.
 */

public class VerificationAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;
    private GridView gridView;

    public VerificationAdapter(Context context, List<String> list, GridView gridView) {
        this.list = list;
        this.context = context;
        this.gridView = gridView;
    }

    @Override
    public int getCount() {
        return 4;
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
        convertView = LayoutInflater.from(context).inflate(R.layout.mx_item_popwindow_veritry_code, null);
        TextView tvVerification = (TextView) convertView.findViewById(R.id.tv_verification);
        try {
            String verificaitonCalue = list.get(position);
            tvVerification.setText(verificaitonCalue == null ? "" : verificaitonCalue);
        } catch (Exception e) {
            e.printStackTrace();
            tvVerification.setText("");
        }
        int height = gridView.getHeight();
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);
        convertView.setLayoutParams(param);
        return convertView;
    }
}
