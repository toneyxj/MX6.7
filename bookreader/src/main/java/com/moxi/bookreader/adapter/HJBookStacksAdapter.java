package com.moxi.bookreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.bookreader.R;
import com.moxi.bookreader.model.BookList;
import com.moxi.bookreader.service.DownDbService;
import com.mx.mxbase.utils.GlideUtils;

import java.io.File;
import java.util.List;

/**
 * Created by King on 2017/8/28.
 */

public class HJBookStacksAdapter extends BaseAdapter {

    private List<BookList.BookDetail> listBookStacks;
    private GridView gridView;
    private Context context;

    public HJBookStacksAdapter(Context context, List<BookList.BookDetail> listBookStacks, GridView gridView) {
        this.context = context;
        this.listBookStacks = listBookStacks;
        this.gridView = gridView;
    }

    public void onRefresh(List<BookList.BookDetail> listBookStacks) {
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
        BookList.BookDetail hjBookData = listBookStacks.get(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.item_hj_book_stacks, null);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_item_hj_file_name);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img_item_hj_file);
        RelativeLayout rlDown = (RelativeLayout) convertView.findViewById(R.id.rl_to_do_down);
        File file = new File(DownDbService.getFilePathWithId(hjBookData.getId()));
        if (file.exists()) {
            rlDown.setVisibility(View.GONE);
        } else {
            rlDown.setVisibility(View.VISIBLE);
        }
//        ImageLoader.getInstance().displayImage(hjBookData.getImgPath(), imageView, ImageLoadUtils.getoptions());
        GlideUtils.getInstance().loadGreyImage(context, imageView, hjBookData.getImgPath());
        textView.setText(hjBookData.getName());

        int height = gridView.getMeasuredHeight();
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (height - 20) / 3);
        convertView.setLayoutParams(param);
        return convertView;
    }
}