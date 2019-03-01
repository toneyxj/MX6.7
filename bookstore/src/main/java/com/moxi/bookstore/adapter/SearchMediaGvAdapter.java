package com.moxi.bookstore.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.bean.SearchMedia;
import com.mx.mxbase.utils.GlideUtils;
import com.moxi.bookstore.utils.ToolUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/9/26.
 */
public class SearchMediaGvAdapter extends BaseAdapter {

    Context context;
    List<SearchMedia> data;
    private int GridviewItemHeight;

    public SearchMediaGvAdapter(Context context,List<SearchMedia> data,int  GridviewItemHeight){
        this.context=context;
        this.GridviewItemHeight=GridviewItemHeight;
        this.data=data;
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
            /*params.width= BookstoreApplication.ScreenWidth/4;
            params.height=GridviewItemHeight;*/
            params.width=270;
            params.height=360;

            holder.item_layout.setLayoutParams(params);

            holder.ico=(ImageView) v.findViewById(R.id.ico_iv);
            holder.title=(TextView)v.findViewById(R.id.title_tv);
            holder.lowprice=(TextView)v.findViewById(R.id.lowest_price_tv);
            holder.orgprice=(TextView)v.findViewById(R.id.org_price_tv);
            holder.author=(TextView)v.findViewById(R.id.author_tv);
            holder.prog=(TextView)v.findViewById(R.id.prog_tv);
            v.setTag(holder);
        }else
            holder=(ViewHolder)v.getTag();

        SearchMedia media=data.get(position);
        GlideUtils.getInstance().loadGreyImage(context,holder.ico,media.getMediaPic());
        holder.title.setText(media.getTitle());
        holder.author.setText(media.getAuthor());
        holder.prog.setVisibility(View.GONE);
        holder.lowprice.setText("￥"+ ToolUtils.getIntence().formatPrice(media.getSalePrice()));
//        holder.orgprice.setText("￥"+ ToolUtils.getIntence().formatPrice(media.getLowestPrice()));
//        holder.lowprice.setText("￥"+ ToolUtils.getIntence().formatPrice(media.getLowestPrice()));
//        holder.orgprice.setText("￥"+ ToolUtils.getIntence().formatPrice(media.getSalePrice()));
        holder.orgprice.setVisibility(View.INVISIBLE);
//        holder.orgprice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
        return v;
    }

    class ViewHolder{
        LinearLayout item_layout;
        ImageView ico;
        TextView title,author,prog;
        TextView lowprice;
        TextView orgprice;

    }
}
