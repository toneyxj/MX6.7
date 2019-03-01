package com.moxi.bookstore.adapter.bookManager;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.modle.bookManager.BookTypesModel;
import com.mx.mxbase.interfaces.OnItemClickListener;

import java.util.List;

/**
 * 网上书城顶部适配器
 * Created by Archer on 16/8/3.
 */
public class BookListTopAdapter extends RecyclerView.Adapter {
    private List<BookTypesModel.BookType> listTop;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public BookListTopAdapter(Context context, List<BookTypesModel.BookType> listTop) {
        this.listTop = listTop;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_net_book_list_top_item, parent, false);
        return new TopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (listTop.get(position).isIndex()) {
            ((TopViewHolder) holder).tvTypeName.setBackgroundResource(R.drawable.moxi_shape_black_corner_10);
            ((TopViewHolder) holder).tvTypeName.setTextColor(Color.WHITE);
        } else {
            ((TopViewHolder) holder).tvTypeName.setBackgroundResource(R.drawable.moxi_shape_grayish_corner_10);
            ((TopViewHolder) holder).tvTypeName.setTextColor(Color.BLACK);
        }
        ((TopViewHolder) holder).tvTypeName.setText(listTop.get(position).getName());
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listTop != null ? listTop.size() : 0;
    }

    class TopViewHolder extends RecyclerView.ViewHolder {

        TextView tvTypeName;

        public TopViewHolder(View itemView) {
            super(itemView);
            tvTypeName = (TextView) itemView.findViewById(R.id.tv_net_book_list_top);
        }
    }
}
