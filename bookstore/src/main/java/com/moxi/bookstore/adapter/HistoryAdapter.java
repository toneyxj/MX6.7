package com.moxi.bookstore.adapter;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.modle.SearchHistory;
import com.mx.mxbase.adapter.BAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 */
public class HistoryAdapter extends BAdapter<SearchHistory> {
    private AdapterView.OnItemClickListener clickListener;

    public void setClickListener(AdapterView.OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public HistoryAdapter(Context context, List<SearchHistory> list) {
        super(context, list);
    }

    @Override
    public int getContentView() {
        return R.layout.adapter_history_item;
    }

    @Override
    public void onInitView(View view, int position, boolean firstAdd) {
        ViewHolder holder;
        if (firstAdd) {
            holder = new ViewHolder();
            holder.history_item = (TextView) view.findViewById(R.id.history_item);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.history_item.setText(getList().get(position).searchContent);
        holder.history_item.setTag(position);
        holder.history_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(null,v,(int)v.getTag(),0);
            }
        });
    }
    public class ViewHolder {
        TextView history_item;
    }
}
