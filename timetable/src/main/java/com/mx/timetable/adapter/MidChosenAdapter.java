package com.mx.timetable.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.timetable.R;
import com.mx.timetable.model.ScheduleModel;
import com.mx.timetable.view.Utils;

import java.util.List;

/**
 * Created by Archer on 16/8/9.
 */
public class MidChosenAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ScheduleModel> list;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MidChosenAdapter(Context context, List<ScheduleModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_time_table_mid_chosen_item, parent, false);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.height = (int) Utils.dp2px(context.getResources(), 240 / (list != null ? list.size() : 10000000));
        view.setLayoutParams(params);
        return new MidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (list.get(position).isChosen()) {
            ((MidViewHolder) holder).imgChosen.setImageResource(R.mipmap.mx_img_chosen);
        } else {
            ((MidViewHolder) holder).imgChosen.setImageResource(R.mipmap.mx_img_unchecked);
        }
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
        return list != null ? list.size() : 0;
    }

    class MidViewHolder extends RecyclerView.ViewHolder {
        ImageView imgChosen;

        public MidViewHolder(View itemView) {
            super(itemView);
            imgChosen = (ImageView) itemView.findViewById(R.id.img_recycler_item_time_table_mid_chosen);
        }
    }
}
