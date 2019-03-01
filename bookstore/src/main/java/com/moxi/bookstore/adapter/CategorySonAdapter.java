package com.moxi.bookstore.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.modle.mediaCategory.CatetoryList;

import java.util.List;

/**
 * Created by xj on 2017/11/13.
 */

public class CategorySonAdapter extends BaseAdapter {

    private  Context context;
    private  List<CatetoryList> data;
    public String code;

    /**
     * 当前内购code值设置
     * @param code
     */
    public void setCode(String code) {
        if (this.code!=null&&this.code.equals(code))return;
        this.code = code;
        //刷新选中状态
        notifyDataSetChanged();
    }

    public CategorySonAdapter(Context context, List<CatetoryList> data,String code) {
        this.context = context;
        this.data = data;
        this.code=code;
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
            v = View.inflate(context, R.layout.adapter_category_son, null);
            holder.category_son_item = (TextView) v.findViewById(R.id.category_son_item);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        CatetoryList category = data.get(position);
        //设置是否选中
        setChecked(holder.category_son_item,null!=code&&code.equals(category.code));
        holder.category_son_item.setText(category.name);
        return v;
    }
    private void setChecked(TextView view, boolean is){
        view.setBackgroundResource(is?R.drawable.di_white_bian_font:R.drawable.transparent);
        view.setTextColor(context.getResources().getColor(is?R.color.colorBlack:R.color.color_normal));
    }

    class ViewHolder {
        TextView category_son_item;
    }
}