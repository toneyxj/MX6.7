package com.moxi.nexams.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.nexams.R;
import com.moxi.nexams.db.SQLUtil;
import com.moxi.nexams.model.OptionModel;
import com.moxi.nexams.model.SynchronousModel;
import com.moxi.nexams.model.papermodel.PaperModelDesc;

import java.util.ArrayList;
import java.util.List;

public class PaperIndexAdapter extends BaseAdapter {

    private List<PaperModelDesc> data = new ArrayList<>();
    private Context context;

    private View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public PaperIndexAdapter(Context context, List<PaperModelDesc> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<PaperModelDesc> data) {
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
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.mx_listview_paper_index, null);
            viewHolder.paperType = (TextView) view.findViewById(R.id.tv_paper_index_type);
            viewHolder.paperDesc = (TextView) view.findViewById(R.id.tv_paper_index_desc);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.paperType.setText((position + 1) + "、" + data.get(position).getPpsMainTitle());
        viewHolder.paperDesc.setText("    " + data.get(position).getPpsDeputyTitle());
        return view;
    }

    static class ViewHolder {
        TextView paperType, paperDesc;
    }
}
