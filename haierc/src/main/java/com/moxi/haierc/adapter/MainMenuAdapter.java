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
import com.moxi.haierc.util.CheckVersionCode;

/**
 * Created by King on 2017/7/6.
 */

public class MainMenuAdapter extends BaseAdapter {

    private Context context;
    private GridView gridView;
    private boolean needChange;
    private String stuFlag;

    private int[] menuImgs = {R.mipmap.img_new_main_rl, R.mipmap.img_new_main_sxbw, R.mipmap.img_new_main_wssc,
            R.mipmap.img_new_main_yy, R.mipmap.img_new_main_wjglq, R.mipmap.img_new_main_setting, R.mipmap.img_new_main_tk, R.mipmap.img_new_main_yingyong};
    private int[] menuImgsStu = {R.mipmap.img_new_main_wssc, R.mipmap.img_new_main_sxbw, R.mipmap.img_new_main_htttl,
            R.mipmap.img_new_main_zd, R.mipmap.img_new_main_wjglq, R.mipmap.img_new_main_setting, R.mipmap.img_new_main_kcb, R.mipmap.img_new_main_yingyong};
    private String[] menuNames = {"日历", "手写备忘", "网上书城", "音乐", "文件管理器", "设置", "图库", "应用"};
    private String[] menuNamesStu = {"网上书城", "手写备忘", "好题天天练", "字典", "文件管理器", "设置", "课程表", "应用"};

    public void changeAdapter(boolean needChange) {
        this.needChange = needChange;
        this.notifyDataSetChanged();
    }

    public void changeVersion(String stuFlag) {
        this.stuFlag = stuFlag;
        this.notifyDataSetChanged();
    }

    public MainMenuAdapter(Context context, GridView gridView, String stuFlag) {
        this.context = context;
        this.gridView = gridView;
        this.stuFlag = stuFlag;
    }

    @Override
    public int getCount() {
        return menuImgs.length;
    }

    @Override
    public Object getItem(int position) {
        return menuNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.new_main_menu_item, null);
        TextView tvMenuName = (TextView) convertView.findViewById(R.id.tv_new_main_item_menu);
        ImageView imgMenu = (ImageView) convertView.findViewById(R.id.img_new_main_item_menu);
        TextView tvNeed = (TextView) convertView.findViewById(R.id.update_tv);
        int height = gridView.getHeight() - 10;
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height / 2);
        convertView.setLayoutParams(param);
        if (stuFlag.contains("标准")) {//标准版
            tvMenuName.setText(menuNames[position]);
            imgMenu.setImageResource(menuImgs[position]);
        } else {//学生版
            tvMenuName.setText(menuNamesStu[position]);
            imgMenu.setImageResource(menuImgsStu[position]);
        }
        if (tvMenuName.getText().toString().equals("设置") && needChange) {
            tvNeed.setVisibility(View.VISIBLE);
        } else {
            tvNeed.setVisibility(View.GONE);
        }
        return convertView;
    }
}
