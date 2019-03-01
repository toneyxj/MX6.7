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
import com.moxi.bookstore.utils.ToolUtils;
import com.mx.mxbase.utils.GlideUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/9/26.
 */
public class HistoryGvAdapter extends BaseAdapter {

    Context context;
    List<?> data;
    private int GridviewItemHeight;

    public HistoryGvAdapter(Context context, List<?> data, int  GridviewItemHeight){
        this.context=context;
        this.GridviewItemHeight=GridviewItemHeight;
        this.data= data;
    }
//
//    public void setData(List<SearchMedia> data){
//        this.data=data;
//        notifyDataSetChanged();
//    }
    @Override
    public int getCount() {
        if (data==null){
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
        if (null==v){
            holder=new ViewHolder();
            v=View.inflate(context, R.layout.item_searchbook_gv,null);
            holder.item_layout=(LinearLayout) v.findViewById(R.id.item_layout);
            RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) holder.item_layout.getLayoutParams();
            params.width= BookstoreApplication.ScreenWidth/4;
            params.height=GridviewItemHeight;
//            APPLog.e("historyHeight1:"+params.height);
//            APPLog.e("historyWidth1:"+params.width);
            holder.item_layout.setLayoutParams(params);

            holder.ico=(ImageView) v.findViewById(R.id.ico_iv);
            holder.title=(TextView)v.findViewById(R.id.title_tv);
            holder.author=(TextView)v.findViewById(R.id.author_tv);
            holder.prgress=(TextView)v.findViewById(R.id.prog_tv);
            holder.price_lay=(LinearLayout)v.findViewById(R.id.price_ll);

            v.setTag(holder);
        }else
            holder=(ViewHolder)v.getTag();

        EbookDB media=(EbookDB) data.get(position);
        GlideUtils.getInstance().loadGreyImage(context,holder.ico,media.getIconUrl());
        holder.title.setText(media.getName());
        holder.author.setText(media.author);
        holder.prgress.setText("进度: "+ToolUtils.getIntence().getEbookProgress(media.progress));
        holder.price_lay.setVisibility(View.GONE);

//        APPLog.e("media.getName()="+media.getName()+" media.author="+media.author,"media.saleId"+media.saleId);
        return v;
    }

    class ViewHolder{
        LinearLayout item_layout,price_lay;
        ImageView ico;
        TextView title;

        TextView author;
        TextView prgress;

    }
}
