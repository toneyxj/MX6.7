package com.moxi.haierc.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.moxi.haierc.R;
import com.moxi.haierc.model.AppModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by King on 2017/10/10.
 */

public class AppListAdapter extends BaseAdapter {
    private List<AppModel> listApps;
    private Context context;
    private PackageManager pm;
    private int page, current;
    private int pageSize = 20;
    private int[] menuImgs = {R.mipmap.img_new_main_rl, R.mipmap.img_new_main_sxbw, R.mipmap.img_new_main_wssc,
            R.mipmap.img_new_main_yy, R.mipmap.img_new_main_wjglq, R.mipmap.img_new_main_setting, R.mipmap.img_new_main_tk,
            R.mipmap.img_new_main_yingyong, R.mipmap.img_new_main_htttl, R.mipmap.img_new_main_kcb, R.mipmap.img_new_main_zd,
            R.mipmap.img_new_main_yx, R.mipmap.img_new_main_etsc};
    private String[] menuNames = {"日历", "手写备忘", "网上书城", "音乐", "文件管理器", "设置", "图库", "应用", "好题天天练", "课程表",
            "字典", "邮箱", "儿童书城"};
    private List<String> listAppName = new ArrayList<>();

    public AppListAdapter(Context context, int page, int current, List<AppModel> listApps) {
        this.context = context;
        this.listApps = listApps;
        this.current = current;
        this.page = page;
        pm = context.getPackageManager();
        listAppName = Arrays.asList(menuNames);
    }

    public void updateAdapter(int page, int current) {
        this.page = page;
        this.current = current;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listApps == null ? 0 : pageSize;
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
        convertView = LayoutInflater.from(context).inflate(R.layout.app_list_item, null);
        TextView title = (TextView) convertView.findViewById(R.id.tv_app_name);
        ImageView image = (ImageView) convertView.findViewById(R.id.img_app_icon);
        ImageView choose = (ImageView) convertView.findViewById(R.id.img_choose);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, wm.getDefaultDisplay().getHeight() / 10);
        convertView.setLayoutParams(param);
        if (page * pageSize + position == current) {
            choose.setVisibility(View.VISIBLE);
        } else {
            choose.setVisibility(View.INVISIBLE);
        }
        if (page * pageSize + position < listApps.size()) {
            convertView.setVisibility(View.VISIBLE);
            title.setText(listApps.get(page * pageSize + position).getAppName());
            int temp = getDrawableIndex(listApps.get(page * pageSize + position).getAppName());
            if (temp != -1) {
                image.setImageResource(menuImgs[temp]);
            } else {
                try {
                    Drawable drawable = getAppIcon(listApps.get(page * pageSize + position).getAppPackageName());
                    if (drawable != null) {
                        image.setImageDrawable(drawable);
                    } else {
                        image.setImageResource(R.mipmap.ic_launcher);
                    }
                } catch (Exception e) {
                    image.setImageResource(R.mipmap.ic_launcher);
                }
            }
        } else {
            convertView.setVisibility(View.INVISIBLE);
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