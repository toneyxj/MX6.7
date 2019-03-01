package com.moxi.bookstore.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.bean.Media;
import com.moxi.bookstore.bean.Sale;
import com.mx.mxbase.utils.GlideUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 * Chanel页面下的bookAdapter
 */
public class BookChanelAdapter extends BaseAdapter{
    private Context cxt;
    private List<Sale> data;
    public BookChanelAdapter(Context context){
        this.cxt=context;
    }
    public void setData(List<Sale> list){
        this.data=list;
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
        if (null==convertView){
            holder=new ViewHolder();
            convertView=View.inflate(cxt, R.layout.item_chanel_salebook,null);
            holder.bookico=(ImageView) convertView.findViewById(R.id.book_ico);
            holder.is_lease=(ImageView) convertView.findViewById(R.id.is_lease);
            holder.title=(TextView)convertView.findViewById(R.id.book_title_tv);
            holder.author=(TextView)convertView.findViewById(R.id.author_tv);
            holder.descs=(TextView)convertView.findViewById(R.id.descb_tv);
            holder.proprice=(TextView)convertView.findViewById(R.id.pro_price_tv);
            holder.orgprice=(TextView)convertView.findViewById(R.id.org_price_tv);
            holder.free=(TextView)convertView.findViewById(R.id.free_tv);
            holder.price_group=(LinearLayout)convertView.findViewById(R.id.price_ll);
            convertView.setTag(holder);
        }else
            holder=(ViewHolder)convertView.getTag();
            holder.orgprice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
            Media item=data.get(position).getMediaList().get(0);

        GlideUtils.getInstance().loadGreyImage(cxt,holder.bookico,item.getCoverPic());
        //如果是租阅书籍可以提示
        if (item.vipMediaPic==null){
            holder.is_lease.setVisibility(View.INVISIBLE);
        }else {
            holder.is_lease.setVisibility(View.VISIBLE);
            GlideUtils.getInstance().loadGreyImageNoBack(cxt,holder.is_lease,item.vipMediaPic);
        }
        String title;
        if (TextUtils.isEmpty(item.getTitle())){
            title="不详";
        }else
          title=item.getTitle();
        holder.title.setText(title);
        holder.author.setText(item.getAuthorPenname());
        holder.descs.setText("   "+item.getDescs());
        if(null==item.getLowestPrice()||(item.getOriginalPrice().equals(item.getSalePrice()))){
            holder.proprice.setText("售价：￥" + item.getSalePrice());
//            holder.orgprice.setText("￥" + ToolUtils.getIntence().formatPrice(item.getPrice()));
            holder.orgprice.setVisibility(View.INVISIBLE);
        }else {
            holder.orgprice.setVisibility(View.VISIBLE);
            holder.proprice.setText("电子书:￥" + item.getSalePrice());
            holder.orgprice.setText("纸书标价:￥" + item.getOriginalPrice());
        }
        if (null!=item.getPromotionId()&&item.getPromotionId()==3||item.getPrice()==0){
            holder.price_group.setVisibility(View.GONE);
            holder.free.setVisibility(View.VISIBLE);
        }else {
            holder.price_group.setVisibility(View.VISIBLE);
            holder.free.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder{
        public ImageView bookico;
        public ImageView is_lease;
        public TextView title;
        public TextView author;
        public TextView descs;
        public TextView proprice;
        public TextView orgprice;
        public TextView free;
        public LinearLayout price_group;
    }


}
