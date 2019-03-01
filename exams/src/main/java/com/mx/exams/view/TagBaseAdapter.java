package com.mx.exams.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.mx.exams.R;
import com.mx.exams.model.OptionModel;

import java.util.List;

/**
 * Created by Archer on 16/9/9.
 */
public class TagBaseAdapter extends BaseAdapter {
    private Context mContext;
    private List<OptionModel> mList;

    private View.OnClickListener onItemClickListener;

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public TagBaseAdapter(Context context, List<OptionModel> list) {
        mContext = context;
        mList = list;
    }

    public void setData(List<OptionModel> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public OptionModel getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.mx_tagview, null);
            holder = new ViewHolder();
            holder.tagBtn = (Button) convertView.findViewById(R.id.tag_btn);
            holder.tagBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(view);
                    }
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final OptionModel text = getItem(position);

        if (text.isChosen()) {
            holder.tagBtn.setBackgroundResource(R.drawable.moxi_shape_black_corner_5);
            holder.tagBtn.setTextColor(mContext.getResources().getColor(R.color.colorWihte));
        } else {
            holder.tagBtn.setBackgroundResource(R.drawable.moxi_shape_grayish_corner_5);
            holder.tagBtn.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
        }
        holder.tagBtn.setTag(position);
        holder.tagBtn.setText(text.getOptionName());
        return convertView;
    }

    static class ViewHolder {
        Button tagBtn;
    }
}
