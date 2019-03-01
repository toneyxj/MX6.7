package com.moxi.bookstore.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.bookstore.BookstoreApplication;
import com.moxi.bookstore.R;
import com.moxi.bookstore.db.EbookDB;
import com.mx.mxbase.utils.GlideUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/9/26.
 */
public class MyLibGvAdapter extends BaseAdapter {

    Context context;
    List<EbookDB> data;
    private int GridviewItemHeight;

    public MyLibGvAdapter(Context context, List<EbookDB> data, int  GridviewItemHeight){
        this.context=context;
        this.GridviewItemHeight=GridviewItemHeight;
        this.data=data;
    }

    public void setData(List<EbookDB> data){
        this.data=data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (data==null){
            return 0;
        } else if (8<data.size())
            return 8;
        else
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
            v=View.inflate(context, R.layout.item_mylib_book_gv,null);
            holder.item_layout=(LinearLayout) v.findViewById(R.id.item_layout);
            RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) holder.item_layout.getLayoutParams();
            params.width= BookstoreApplication.ScreenWidth/4;
            params.height=GridviewItemHeight;
            holder.item_layout.setLayoutParams(params);

            holder.ico=(ImageView) v.findViewById(R.id.ico_iv);
            holder.title=(TextView)v.findViewById(R.id.title_tv);
            holder.author=(TextView)v.findViewById(R.id.author_tv);
            v.setTag(holder);
        }else
            holder=(ViewHolder)v.getTag();

        EbookDB media=data.get(position);
        GlideUtils.getInstance().loadGreyImage(context,holder.ico,media.iconUrl);
        holder.title.setText(media.name);
        holder.author.setText(media.author);
        return v;
    }

    class ViewHolder{
        LinearLayout item_layout;
        ImageView ico;
        TextView title;
        TextView author;


    }
}
