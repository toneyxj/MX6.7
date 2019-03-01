package com.moxi.systemapp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.systemapp.R;
import com.moxi.systemapp.model.AppInfo;
import com.mx.mxbase.adapter.BAdapter;
import com.mx.mxbase.base.MyApplication;

import java.util.List;

/**
 * Created by xj on 2018/4/24.
 * Unknown option "--disable-ffserver".
 */

public class AppsAdapter extends BAdapter<AppInfo> {

    public AppsAdapter(Context context, List<AppInfo> list) {
        super(context, list);
    }

    @Override
    public int getContentView() {
            return R.layout.adapter_apps;
    }

    @Override
    public void onInitView(View view, int position, boolean firstAdd) {
        ViewHolder holder;
        if (firstAdd) {
            holder = new ViewHolder();
            holder.show_image = (ImageView) view.findViewById(R.id.show_image);
            holder.file_name = (TextView) view.findViewById(R.id.file_name);
            holder.show_image.getLayoutParams().width= MyApplication.ScreenWidth/10;
            holder.show_image.getLayoutParams().height= MyApplication.ScreenWidth/10;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        AppInfo been = getItem(position);
        holder.show_image.setImageDrawable(been.getAppIcon());
        holder.file_name.setText(been.getAppLabel());
    }
    public class ViewHolder {
        ImageView show_image;
        TextView file_name;
    }
}
