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
import com.moxi.haierc.hjbook.hjdata.HJPrePathDir;

import java.util.List;

//import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by King on 2017/8/28.
 */

public class HJBookTypeAdapter extends BaseAdapter {

    private List<HJPrePathDir> listBookType;
    private GridView gridView;
    private Context context;
    private int page = 0;
    private int[] preImageId = {R.mipmap.img_pre_path_photo_100, R.mipmap.img_pre_path_photo_99, R.mipmap.img_pre_path_photo_98, R.mipmap.img_pre_path_photo_97,
            R.mipmap.img_pre_path_photo_96, R.mipmap.img_pre_path_photo_95, R.mipmap.img_pre_path_photo_94, R.mipmap.img_pre_path_photo_93,
            R.mipmap.img_pre_path_photo_92, R.mipmap.img_pre_path_photo_91, R.mipmap.img_pre_path_photo_90, R.mipmap.img_pre_path_photo_89,
            R.mipmap.img_pre_path_photo_88, R.mipmap.img_pre_path_photo_87, R.mipmap.img_pre_path_photo_86, R.mipmap.img_pre_path_photo_85};

    public HJBookTypeAdapter(Context context, List<HJPrePathDir> listBookType, int page, GridView gridView) {
        this.context = context;
        this.listBookType = listBookType;
        this.gridView = gridView;
        this.page = page;
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
        int temp = page * 9 + position;
//        if (temp == 0) {
//            imageView.setImageResource(preImageId[temp]);
//            textView.setText("个人书籍");
//        } else {
        HJPrePathDir hjPrePathDir = listBookType.get(position);
        if (hjPrePathDir != null) {
            textView.setText(hjPrePathDir.getDirName());
//            ImageLoader.getInstance().displayImage("file://" + hjPrePathDir.getPhotoPath(), imageView, ImageLoadUtils.getoptions());
            imageView.setImageResource(preImageId[temp]);
            tvNumber.setText("(共" + hjPrePathDir.getNumber() + "本)");
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }
//        }
        int height = gridView.getMeasuredHeight();
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (height - 20) / 3);
        convertView.setLayoutParams(param);
        return convertView;
    }
}