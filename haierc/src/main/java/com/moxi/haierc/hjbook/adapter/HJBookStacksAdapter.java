package com.moxi.haierc.hjbook.adapter;

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
import com.mx.mxbase.utils.GlideUtils;

import java.util.List;

//import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by King on 2017/8/28.
 */

public class HJBookStacksAdapter extends BaseAdapter {

    private List<HJBookData> listBookStacks;
    private GridView gridView;
    private Context context;

    public HJBookStacksAdapter(Context context, List<HJBookData> listBookStacks, GridView gridView) {
        this.context = context;
        this.listBookStacks = listBookStacks;
        this.gridView = gridView;
    }

    public void onRefresh(List<HJBookData> listBookStacks) {
        this.listBookStacks = listBookStacks;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listBookStacks == null ? 0 : listBookStacks.size();
    }

    @Override
    public Object getItem(int position) {
        return listBookStacks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HJBookData hjBookData = listBookStacks.get(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.item_hj_book_stacks, null);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_item_hj_file_name);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img_item_hj_file);
//        ImageLoader.getInstance().displayImage("file://" + hjBookData.getPhotoPath(), imageView, ImageLoadUtils.getoptions());
        GlideUtils.getInstance().locatonPic(context,imageView,"file://" + hjBookData.getPhotoPath());
        String temp = hjBookData.getFilePath();
        temp = temp.substring(temp.lastIndexOf("/") + 1);
        textView.setText(temp.substring(temp.indexOf("-") + 1));

        int height = gridView.getMeasuredHeight();
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (height - 20) / 3);
        convertView.setLayoutParams(param);
        return convertView;
    }
}