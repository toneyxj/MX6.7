package com.mx.exams.adapter;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mx.exams.R;
import com.mx.exams.cache.ACache;
import com.mx.exams.model.ExaModel;
import com.mx.mxbase.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengdelong on 16/9/29.
 */

public class ExaAdapter extends BaseAdapter {

    private List<ExaModel> data = new ArrayList<ExaModel>();
    private Context context;

    public ExaAdapter(Context context, List<ExaModel> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_exams_more_item, null);
            viewHolder.tv_exams_item_title = (TextView) view.findViewById(R.id.tv_exams_item_title);
            viewHolder.tv_exams_item_subject = (TextView) view.findViewById(R.id.tv_exams_item_subject);
            viewHolder.state_img = (ImageView) view.findViewById(R.id.state_img);
            viewHolder.tv_exams_item_point = (TextView) view.findViewById(R.id.tv_exams_item_point);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tv_exams_item_title.setText(data.get(position).getTitle());
        viewHolder.tv_exams_item_subject.setText(data.get(position).getSubjectName());
//        viewHolder.tv_recycler_over_all_exams_state.setText(data.get(position).getType() + "");
        if (ACache.get(context).getAsString(data.get(position).getTitle() + data.get(position).getId()) == null) {
            //未做
            viewHolder.tv_exams_item_point.setVisibility(View.INVISIBLE);
//            viewHolder.state_img.setVisibility(View.INVISIBLE);
        } else {
            //已做
            viewHolder.tv_exams_item_point.setVisibility(View.VISIBLE);
//            viewHolder.state_img.setImageResource(R.mipmap.mx_img_practice_done);
        }
        viewHolder.tv_exams_item_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileUrl = Environment.getExternalStorageDirectory() + "/mx_exams/" +
                        data.get(position).getTitle();
                File file = new File(fileUrl + "/imgs");
                if (file.exists() && file.isDirectory()) {
                    File[] subFile = file.listFiles();
                    int ssss = subFile.length;
                    if (ssss > 0) {
                        FileUtils.getInstance().openFile(context, subFile[1]);
                    }
                }
            }
        });
        return view;
    }

    static class ViewHolder {
        TextView tv_exams_item_title;
        TextView tv_exams_item_subject;
        TextView tv_exams_item_point;
        ImageView state_img;
    }
}
