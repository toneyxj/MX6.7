package com.moxi.bookstore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.interfacess.ClickPosition;
import com.moxi.bookstore.modle.BookRack;
import com.mx.mxbase.utils.GlideUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 书架的适配器
 * Created by Administrator on 2016/9/12.
 */
public class BookRackAdapter extends RecyclerView.Adapter<BookRackAdapter.ViewHolder> {
    private List<BookRack> list;
    private ClickPosition listener;
    private LayoutInflater mInflater;
    private int width;
    private int height;

    public BookRackAdapter(Context context, List<BookRack> list, int width, int height, ClickPosition listener) {
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.width = width;
        this.height = height;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(
                R.layout.adapter_book_item, parent, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookRack rack = list.get(position);
//        ImageLoader.getInstance().displayImage(rack.imagePath, holder.book_image, PictureUtils.getoptions());
        GlideUtils.getInstance().loadImage( holder.book_image.getContext(), holder.book_image,rack.imagePath);
        holder.progress.setText(rack.progress);
        holder.book_name.setText(rack.bookName);

        holder.click_layout.setTag(position);
        holder.click_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.click((Integer) v.getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.click_layout)
        LinearLayout click_layout;
        @Bind(R.id.book_image)
        ImageView book_image;
        @Bind(R.id.progress)
        TextView progress;
        @Bind(R.id.book_name)
        TextView book_name;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
