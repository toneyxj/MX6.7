package com.moxi.haierc.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.haierc.R;
import com.moxi.haierc.model.AppModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by King on 2017/7/6.
 */

public class DynamicMainMenuAdapter extends BaseAdapter {

    private Context context;
    private GridView gridView;
    private boolean needChange;
    private List<AppModel> listApps;
    private PackageManager pm;

    private int[] menuImgs = {R.mipmap.img_new_main_rl, R.mipmap.img_new_main_sxbw, R.mipmap.img_new_main_wssc,
            R.mipmap.img_new_main_yy, R.mipmap.img_new_main_wjglq, R.mipmap.img_new_main_setting, R.mipmap.img_new_main_tk,
            R.mipmap.img_new_main_yingyong, R.mipmap.img_new_main_htttl, R.mipmap.img_new_main_kcb, R.mipmap.img_new_main_zd,
            R.mipmap.img_new_main_yx, R.mipmap.img_new_main_etsc, R.mipmap.img_new_main_zyb};
    private String[] menuNames = {"日历", "手写备忘", "网上书城", "音乐", "文件管理器", "设置", "图库", "应用", "好题天天练", "课程表",
            "字典", "邮箱", "儿童书城", "作业帮"};
    private List<String> listAppName = new ArrayList<>();

    public void changeAdapter(boolean needChange) {
        this.needChange = needChange;
        this.notifyDataSetChanged();
    }

    public DynamicMainMenuAdapter(Context context, GridView gridView, List<AppModel> listApps) {
        this.context = context;
        this.gridView = gridView;
        pm = context.getPackageManager();
        this.listApps = listApps;
        listAppName = Arrays.asList(menuNames);
    }

    @Override
    public int getCount() {
        return listApps != null ? listApps.size() : 0;
    }

    @Override
    public AppModel getItem(int position) {
        return listApps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.dynamic_main_menu_item, null);
        TextView tvMenuName = (TextView) convertView.findViewById(R.id.tv_new_main_item_menu);
        ImageView imgMenu = (ImageView) convertView.findViewById(R.id.img_new_main_item_menu);
        TextView tvNeed = (TextView) convertView.findViewById(R.id.update_tv);
        int height = gridView.getHeight() - 10;
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height / 2);
        convertView.setLayoutParams(param);
        int temp = getDrawableIndex(listApps.get(position).getAppName());
        tvMenuName.setText(listApps.get(position).getAppName());
        if (temp != -1) {
            imgMenu.setImageResource(menuImgs[temp]);
        } else {
            try {
                Drawable drawable = getAppIcon(listApps.get(position).getAppPackageName());
                if (drawable != null) {
                    imgMenu.setImageDrawable(drawable);
                } else {
                    imgMenu.setImageResource(R.mipmap.ic_launcher);
                }
            } catch (Exception e) {
                imgMenu.setImageResource(R.mipmap.ic_launcher);
            }
        }
        if (tvMenuName.getText().toString().equals("设置") && needChange) {
            tvNeed.setVisibility(View.VISIBLE);
        } else {
            tvNeed.setVisibility(View.GONE);
        }

        return convertView;
    }

    public Drawable getAppIcon(String packname) {
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            return info.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return context.getDrawable(R.mipmap.ic_launcher);
            } else {
                return null;
            }
        }
    }

    private int getDrawableIndex(String appName) {
        return listAppName.indexOf(appName);
    }
}
