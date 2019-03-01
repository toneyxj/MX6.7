package com.moxi.bookstore.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.modle.mediaCategory.CatetoryList;
import com.moxi.bookstore.modle.mediaCategory.ZYCategotyModel;

import java.util.List;

/**
 * Created by xj on 2017/11/13.
 */

public class CategoryMainAdapter extends BaseAdapter {

    Context context;
    List<ZYCategotyModel> data;
    private String code;
    private SelectCategoryListener listener;

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
    public ZYCategotyModel getCategory(){
        for (ZYCategotyModel model:data){
            if (model.code.equals(code)){
                ZYCategotyModel model1=new ZYCategotyModel();
                model1.code=model.code;
                model1.id=model.id;
                model1.image=model.image;
                model1.leaf=model.leaf;
                model1.name=model.name;
                model1.parentId=model.parentId;
                model1.parsed=model.parsed;
                return model1;
            }
        }
        return null;
    }

    public CategoryMainAdapter(Context context, List<ZYCategotyModel> data,String code,SelectCategoryListener listener) {
        this.context = context;
        this.data = data;
        this.code=code;
        this.listener=listener;
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
            v = View.inflate(context, R.layout.adapter_category_main, null);
            holder.category_main_item = (TextView) v.findViewById(R.id.category_main_item);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        ZYCategotyModel category = data.get(position);
        //设置是否选中
        boolean is=null!=code&&code.equals(category.code);
        setChecked(holder.category_main_item,is);
        if (listener!=null&&is){
            listener.onSelectCategory(category.catetoryList);
        }
        holder.category_main_item.setText(category.name);
        return v;
    }
    private void setChecked(TextView view,boolean is){
        view.setBackgroundResource(is?R.drawable.di_white_bian_font:R.drawable.transparent);
        view.setTextColor(context.getResources().getColor(is?R.color.colorBlack:R.color.color_normal));
    }

    class ViewHolder {
        TextView category_main_item;
    }

    public interface SelectCategoryListener{
       void onSelectCategory(List<CatetoryList> catetoryList);
    }
}