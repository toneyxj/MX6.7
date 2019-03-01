package com.moxi.bookstore.adapter.bookManager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.db.EbookDB;
import com.moxi.bookstore.db.TableConfig;
import com.moxi.bookstore.db.TableOperate;
import com.moxi.bookstore.modle.BookStoreFile;
import com.moxi.bookstore.utils.ReadManagerPicUtils;
import com.mx.mxbase.constant.PhotoConfig;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.GlideUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Archer on 16/8/4.
 */
public class LocalBookPageRecyclerAdapter extends RecyclerView.Adapter {
    private List<BookStoreFile> listFile;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public LocalBookPageRecyclerAdapter(List<BookStoreFile> listPage, Context context) {
        this.listFile = listPage;
        this.context = context;
    }

    public void setOnItemClickLIstener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_book_type_item, parent, false);
        return new BookTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        BookStoreFile mode = listFile.get(position);
        File file = new File(mode.filePath);
        ((BookTypeViewHolder) holder).tvBookType.setText(file.getName());
        EbookDB ebookDB = TableOperate.getInstance().queryByPath(TableConfig.TABLE_NAME, mode.filePath);

        if (ebookDB == null) {
            ReadManagerPicUtils.getInstance().setLocationBookPic(context,((BookTypeViewHolder) holder).imgBookType,mode.filePath);
        } else {
            GlideUtils.getInstance().loadGreyImage(context,((BookTypeViewHolder) holder).imgBookType,ebookDB.iconUrl);
        }

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
                    return true;
                }
            });
        }
    }

    private int getSources(File file) {
        return getSources(file.getAbsoluteFile());

    }

    private int getSources(String fileNmae) {
      return PhotoConfig.getSources(fileNmae);
    }

    @Override
    public int getItemCount() {
        if (listFile == null) {
            return 0;
        } else if (listFile.size() > 8) {
            return 8;
        } else {
            return listFile.size();
        }
    }

    public class BookTypeViewHolder extends RecyclerView.ViewHolder {

        TextView tvBookType;
        ImageView imgBookType;

        public BookTypeViewHolder(View itemView) {
            super(itemView);
            tvBookType = (TextView) itemView.findViewById(R.id.tv_recycler_item_book_type);
            imgBookType = (ImageView) itemView.findViewById(R.id.img_recycler_item_book_type);
        }
    }
}
