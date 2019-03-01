package com.mx.exams.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mx.exams.R;
import com.mx.exams.activity.MXErrorExamsActivity;
import com.mx.exams.cache.ACache;
import com.mx.exams.model.ChoseExamsModel;
import com.mx.exams.model.ExamsDetails;
import com.mx.exams.model.ExamsDetailsModel;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.GsonTools;

import java.util.List;

/**
 * Created by Archer on 16/9/9.
 */
public class ReplyResultAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ChoseExamsModel> listData;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ReplyResultAdapter(Context context, List<ChoseExamsModel> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_reply_result_item, parent, false);
        return new HomeWorkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        String resultKey = listData.get(position).getResultKey() + listData.get(position).getIndex();
        String tempResult = ACache.get(context).getAsString(resultKey);
        ExamsDetails examsDetails = listData.get(position).getExamsDetails();
        try {
            if (listData.get(position).getExamsDetails().getAnswer().equals(tempResult) || examsDetails.getResult().equals(examsDetails.answer)) {
                ((HomeWorkViewHolder) holder).imgIndex.setBackgroundResource(R.drawable.moxi_shape_black_corner_35);
                ((HomeWorkViewHolder) holder).tvIndex.setTextColor(context.getResources().getColor(R.color.colorWihte));
            } else {
                ((HomeWorkViewHolder) holder).imgIndex.setBackgroundResource(R.drawable.moxi_shape_white_corner_35);
                ((HomeWorkViewHolder) holder).tvIndex.setTextColor(context.getResources().getColor(R.color.colorBlack));
            }
        } catch (Exception e) {
            ((HomeWorkViewHolder) holder).imgIndex.setBackgroundResource(R.drawable.moxi_shape_white_corner_35);
            ((HomeWorkViewHolder) holder).tvIndex.setTextColor(context.getResources().getColor(R.color.colorBlack));
            e.printStackTrace();
        }
        ((HomeWorkViewHolder) holder).tvIndex.setText((listData.get(position).getIndex() + 1) + "");

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
        return listData == null ? 0 : listData.size();
    }

    class HomeWorkViewHolder extends RecyclerView.ViewHolder {

        TextView tvIndex;
        ImageView imgIndex;

        public HomeWorkViewHolder(View itemView) {
            super(itemView);
            tvIndex = (TextView) itemView.findViewById(R.id.tv_reply_result_index);
            imgIndex = (ImageView) itemView.findViewById(R.id.img_reply_result);
        }
    }
}
