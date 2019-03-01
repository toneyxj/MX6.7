package com.moxi.bookreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.bookreader.R;
import com.moxi.bookreader.model.BookModel;
import com.mx.mxbase.utils.GlideUtils;

import java.util.List;

/**
 * Created by King on 2017/8/28.
 */

public class HJBookTypeAdapter extends BaseAdapter {

    private List<BookModel.BookBean> listBookType;
    private GridView gridView;
    private Context context;

    public HJBookTypeAdapter(Context context, List<BookModel.BookBean> listBookType, int page, GridView gridView) {
        this.context = context;
        this.listBookType = listBookType;
        this.gridView = gridView;
        int temp = listBookType.size() % 3;
        if (temp != 0) {
            int add = 3 - temp;
            for (int i = 0; i < add; i++) {
                listBookType.add(null);
            }
        }
    }

    @Override
    public int getCount() {
        return listBookType == null ? 0 : listBookType.size();
    }

    @Override
    public Object getItem(int position) {
        return listBookType.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_hj_book_type, null);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_item_hj_dir_name);
        TextView tvNumber = (TextView) convertView.findViewById(R.id.tv_item_hj_dir_files);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.item_img_hj_book_type);
        BookModel.BookBean hjPrePathDir = listBookType.get(position);
        if (hjPrePathDir != null) {
            textView.setText(hjPrePathDir.getC_name());
            GlideUtils.getInstance().loadGreyImage(context, imageView, hjPrePathDir.getC_img_path());
            tvNumber.setText("(共" + hjPrePathDir.getCount() + "本)");
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }
        int height = gridView.getMeasuredHeight();
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (height - 20) / 3);
        convertView.setLayoutParams(param);
        return convertView;
    }
}