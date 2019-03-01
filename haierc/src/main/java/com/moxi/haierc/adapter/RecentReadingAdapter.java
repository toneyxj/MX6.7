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
import com.moxi.haierc.model.BookStoreFile;
import com.mx.mxbase.constant.LocationBookReadProgressUtils;
import com.mx.mxbase.model.LocationBookInfo;
import com.mx.mxbase.utils.BookProgressUtils;
import com.mx.mxbase.utils.DensityUtil;
import com.mx.mxbase.utils.GlideUtils;

import java.util.List;

/**
 * Created by King on 2017/7/6.
 */

public class RecentReadingAdapter extends BaseAdapter {

    private Context context;
    private GridView gridView;
    private List<BookStoreFile> listRecent;

    public void dataChange(List<BookStoreFile> listRecent) {
        this.listRecent = listRecent;
        this.notifyDataSetChanged();
    }

    public RecentReadingAdapter(Context context, GridView gridView, List<BookStoreFile> listRecent) {
        this.context = context;
        this.gridView = gridView;
        this.listRecent = listRecent;
    }

    @Override
    public int getCount() {
        return listRecent == null ? 0 : listRecent.size();
    }

    @Override
    public BookStoreFile getItem(int position) {
        return listRecent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookStoreFile mode = listRecent.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.new_main_recent_reading_item, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img_new_main_recent);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_new_main_recent_name);
        TextView read_progress = (TextView) convertView.findViewById(R.id.read_progress);
        int height = 0;
//            height = gridView.getHeight() - 10;
        height = (gridView.getHeight() - DensityUtil.dip2px(context, 80)) / 2;
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);
        convertView.setLayoutParams(param);
        read_progress.setVisibility(View.INVISIBLE);
        if (mode.getIsDdBook() == 1) {
            GlideUtils.getInstance().loadGreyImage(context, imageView, mode.bookImageUrl);
            BookProgressUtils.setDDReadBookProgress(true,mode.progress,read_progress);
        } else {
            BookProgressUtils.setShowBookPic(imageView,mode.getName());
           LocationBookReadProgressUtils.getInstance(context).addProgress(mode.getFilePath(),read_progress);

//            ReadManagerPicUtils.getInstance().setLocationBookPic(context, imageView, mode.getFilePath());
        }
        String name = mode.filePath;
        name = name.substring(name.lastIndexOf("/") + 1);
        textView.setText(name);
        return convertView;
    }

    public void updateSelect(LocationBookInfo info, GridView listView) {
        int position=-1;
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).filePath.equals(info.getPath())){
                position=i;
                break;
            }
        }
        if (position==-1)return;
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        if (position >= visibleFirstPosi && position <= visibleLastPosi) {

            View view = listView.getChildAt(position - visibleFirstPosi);
            TextView select_image = (TextView) view.findViewById(R.id.read_progress);
            select_image.setVisibility(View.VISIBLE);
            BookProgressUtils.setReadBookProgress(info.getTotalpage(), info.getCurrentpage(), select_image);
        }
    }

    @Override
    public void notifyDataSetChanged() {
//        LocationBookReadProgressUtils.getInstance(context).ClearView();
        super.notifyDataSetChanged();
    }
}
