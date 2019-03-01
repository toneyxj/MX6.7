package com.moxi.bookstore.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.bean.ChargeInfor;

import java.util.List;

/**
 * Created by Administrator on 2016/11/23.
 */

public class ChargeAdapter extends BaseAdapter {
    Context ctx;
    List<ChargeInfor> data;

    public ChargeAdapter(Context ctx, List<ChargeInfor> data) {
        this.ctx = ctx;
        this.data = data;
    }

    @Override
    public int getCount() {
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
        if (null==v){
            holder=new ViewHolder();
            v=View.inflate(ctx, R.layout.item_charge,null);
            holder.goldbal=(TextView)v.findViewById(R.id.goldbalence_tv);
            holder.mony=(TextView)v.findViewById(R.id.mony_tv);
            holder.readbal=(TextView)v.findViewById(R.id.gift_tv);
            v.setTag(holder);
        }else
            holder=(ViewHolder)v.getTag();
           ChargeInfor item= data.get(position);
            int mony=item.getDepositMoney();
            holder.goldbal.setText(mony+"");
            holder.mony.setText("¥ "+mony/100);
            if (500<mony)
                holder.readbal.setText("送 "+item.getDepositGiftReadPrice()+"银铃铛");
            else
                holder.readbal.setText("");

        return v;
    }

    class ViewHolder{
        TextView goldbal,mony,readbal;
    }
}
