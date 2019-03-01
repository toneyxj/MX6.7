package com.moxi.haierc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.haierc.R;
import com.moxi.haierc.hjbook.hjdata.HJBookData;
import com.mx.mxbase.utils.DensityUtil;
import com.mx.mxbase.utils.GlideUtils;

import java.util.List;

/**
 * Created by King on 2017/7/6.
 */

public class HJRecentReadingAdapter extends BaseAdapter {

    private Context context;
    private GridView gridView;
    private List<HJBookData> listRecent;

    public void dataChange(List<HJBookData> listRecent) {
        this.listRecent = listRecent;
        this.notifyDataSetChanged();
    }

    public HJRecentReadingAdapter(Context context, GridView gridView, List<HJBookData> listRecent) {
        this.context = context;
        this.gridView = gridView;
        this.listRecent = listRecent;
    }

    @Override
    public int getCount() {
        if (listRecent == null) {
            return 0;
        } else if (listRecent.size() > 2) {
            return 2;
        } else {
            return listRecent.size();
        }
    }

    @Override
    public HJBookData getItem(int position) {
        return listRecent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HJBookData mode = listRecent.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.hj_main_recent_reading_item, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img_new_main_recent);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_new_main_recent_name);

        int height = DensityUtil.getScreenW(context) * 4 / 10;
        int width = (DensityUtil.getScreenW(context) - 40) * 3 / 11;
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(width, height - 10);
        gridView.getLayoutParams().height = height;
        gridView.getLayoutParams().width = width * 2;
        convertView.setLayoutParams(param);
//        ImageLoader.getInstance().displayImage("file://" + mode.getPhotoPath(), imageView, ImageLoadUtils.getoptions());
        GlideUtils.getInstance().locatonPic(context,imageView,"file://" + mode.getPhotoPath());

        String name = mode.filePath;
        name = name.substring(name.lastIndexOf("/") + 1);
        textView.setText(name.substring(name.indexOf("-") + 1));
        return convertView;
    }
}