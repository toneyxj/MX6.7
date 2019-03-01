package com.moxi.bookstore.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.bean.Cart;
import com.moxi.bookstore.interfacess.ClickPosition;
import com.mx.mxbase.utils.GlideUtils;
import com.moxi.bookstore.utils.ToolUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 * Chanel页面下的bookAdapter
 */
public class CartAdapter extends BaseAdapter {
    private Context cxt;
    private List<Cart.ProductsBean> data;
    ClickPosition listener;
    public CartAdapter(Context context,ClickPosition listener) {
        this.cxt = context;
        this.listener=listener;
    }

    public void setData(List<Cart.ProductsBean> list) {
        this.data = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if (null==data||data.size()==0)
            return 0;
        else if (data.size()>4)
            return 4;
        else
            return data.size();
    }

    public List<Cart.ProductsBean> getChickItems() {
        List<Cart.ProductsBean> items = new ArrayList<>();
        if (null != data && data.size() > 0) {
            for (Cart.ProductsBean bean : data) {
                if (bean.isChecked()) {
                    items.add(bean);
                }
            }
        }
        return items;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = View.inflate(cxt, R.layout.item_cart_salebook, null);
            holder.bookico = (ImageView) convertView.findViewById(R.id.book_ico);
            holder.title = (TextView) convertView.findViewById(R.id.book_title_tv);
            holder.author = (TextView) convertView.findViewById(R.id.author_tv);
            holder.proprice = (TextView) convertView.findViewById(R.id.pro_price_tv);
            holder.orgprice = (TextView) convertView.findViewById(R.id.org_price_tv);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox2);
            holder.checkBox.setOnClickListener(checkBoxClick);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        holder.orgprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        Cart.ProductsBean item = data.get(position);
        //加载图片
        GlideUtils.getInstance().loadGreyImage(cxt, holder.bookico, item.getCoverPic());
        String title;
        if (TextUtils.isEmpty(item.getTitle())) {
            title = "不详";
        } else
            title = item.getTitle();
        holder.title.setText(title);
        holder.author.setText(item.getAuthorPenname());
      //  holder.descs.setText("   " + item.getCategorys());
        double price= Double.parseDouble(item.getPrice())/100;
        holder.proprice.setText("￥" +ToolUtils.getIntence().formatPrice(price));
        holder.checkBox.setChecked(item.isChecked());
        holder.checkBox.setTag(item);
         holder.orgprice.setText("￥" +ToolUtils.getIntence().formatPrice(price));
        return convertView;
    }

    View.OnClickListener checkBoxClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Cart.ProductsBean item = (Cart.ProductsBean) view.getTag();
            CheckBox checkBox = (CheckBox) view;
            item.setChecked(checkBox.isChecked());
            listener.click(0);
        }
    };

    class ViewHolder {
        public ImageView bookico;
        public TextView title;
        public TextView author;
       // public TextView descs;
        public TextView proprice;
        public TextView orgprice;
        CheckBox checkBox;
    }


}
