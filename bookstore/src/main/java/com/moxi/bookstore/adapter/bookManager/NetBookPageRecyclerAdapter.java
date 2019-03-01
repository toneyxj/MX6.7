package com.moxi.bookstore.adapter.bookManager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.bean.Media;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.GlideUtils;

import java.util.List;

/**
 * Created by Archer on 16/8/4.
 */
public class NetBookPageRecyclerAdapter extends RecyclerView.Adapter {
    private List<Media> listPage;
    private int index;
    private OnItemClickListener onItemClickListener;
    private Context context;
public void setList(List<Media> listPage){
    this.listPage.clear();
    this.listPage.addAll(listPage);
    notifyDataSetChanged();
}
    public NetBookPageRecyclerAdapter(List<Media> listPage,
                                      Context context, int index) {
        this.listPage = listPage;
        this.context = context;
        this.index = index;
    }

    public void setOnItemClickLIstener(OnItemClickListener onItemClickLIstener) {
        this.onItemClickListener = onItemClickLIstener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_net_book_type_item, parent, false);
        return new BookTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((BookTypeViewHolder) holder).tvBookType.setText("《"+listPage.get(position).getTitle()+"》");
        ((BookTypeViewHolder) holder).tv_recycler_item_book_type_author.setText(listPage.get(position).getAuthorPenname());
        GlideUtils.getInstance().loadGreyImage(context,((BookTypeViewHolder) holder).imgBookType,listPage.get(position).getCoverPic());
        GlideUtils.getInstance().loadImage(context, ((BookTypeViewHolder) holder).imgBookType,listPage.get(position).getCoverPic());
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.onItemLongClick(holder.itemView, position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(listPage==null){
            return 0;
        }else {
            return listPage.size();
        }
    }

    public class BookTypeViewHolder extends RecyclerView.ViewHolder {

        TextView tvBookType;
        ImageView imgBookType;
        ImageView tui_jian;
        TextView tv_recycler_item_book_type_author;

        public BookTypeViewHolder(View itemView) {
            super(itemView);
            tvBookType = (TextView) itemView.findViewById(R.id.tv_recycler_item_book_type);
            tv_recycler_item_book_type_author = (TextView) itemView.findViewById(R.id.tv_recycler_item_book_type_author);
            imgBookType = (ImageView) itemView.findViewById(R.id.img_recycler_item_book_type);
            tui_jian = (ImageView) itemView.findViewById(R.id.tui_jian);
            tui_jian.setVisibility(View.VISIBLE);
        }
    }
}
