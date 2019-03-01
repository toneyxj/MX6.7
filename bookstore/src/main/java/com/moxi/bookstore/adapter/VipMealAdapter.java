package com.moxi.bookstore.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.modle.mediaCategory.ChannelMonthlyStrategy;

import java.util.List;

import static com.moxi.bookstore.R.id.orignal_price;

/**
 * Created by xj on 2017/11/14.
 */

public class VipMealAdapter extends BaseAdapter {

    Context context;
    List<ChannelMonthlyStrategy> data;
    private int selectIndex=0;

    public void setSelectIndex(int selectIndex) {
        if (this.selectIndex==selectIndex)return;
        this.selectIndex = selectIndex;
        notifyDataSetChanged();
    }

    public ChannelMonthlyStrategy getChannelMonth(){
        return (ChannelMonthlyStrategy) getItem(selectIndex);
    }

    public VipMealAdapter(Context context, List<ChannelMonthlyStrategy> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;
        if (null == v) {
            holder = new ViewHolder();
            v = View.inflate(context, R.layout.adapter_vip_meal, null);
            holder.item_back = (LinearLayout) v.findViewById(R.id.item_back);
            holder.name = (TextView) v.findViewById(R.id.name);
            holder.discounts_item = (ImageView) v.findViewById(R.id.discounts_item);
            holder.orignal_price = (TextView) v.findViewById(orignal_price);
            holder.cur_price = (TextView) v.findViewById(R.id.cur_price);
            holder.draw_line = (View) v.findViewById(R.id.draw_line);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        ChannelMonthlyStrategy category = data.get(position);

        holder.item_back.setBackgroundResource(position==selectIndex?R.drawable.di_white_bian_font:R.color.transparent);

        if (selectIndex>0){
            holder.draw_line.setVisibility((position==(selectIndex-1))?View.INVISIBLE:View.VISIBLE);
        }
        holder.draw_line.setVisibility(position==selectIndex?View.INVISIBLE:View.VISIBLE);

        if (category.originalPrice==category.newPrice){
            holder.orignal_price.setVisibility(View.INVISIBLE);
            holder.discounts_item.setVisibility(View.INVISIBLE);
        }else {
            holder.orignal_price.setVisibility(View.VISIBLE);
            holder.discounts_item.setVisibility(View.VISIBLE);
        }
        holder.name.setText(category.name);
        holder.orignal_price.setText("原价"+String.valueOf(category.originalPrice/100)+"元");
        holder.orignal_price.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); //中划线

        holder.cur_price.setText(String.valueOf(category.newPrice/100)+"元");
        return v;
    }

    class ViewHolder {
        LinearLayout item_back;
        TextView name;
        ImageView discounts_item;
        TextView orignal_price;
        TextView cur_price;
        View draw_line;
    }
}