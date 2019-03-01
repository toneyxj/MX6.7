package com.mx.timetable.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.timetable.R;

/**
 * Created by Archer on 2016/11/24.
 */
public class NewAddCourseAdapter extends RecyclerView.Adapter {

    private int[] normal = new int[]{R.mipmap.new_add_1, R.mipmap.new_add_2, R.mipmap.new_add_3, R.mipmap.new_add_4, R.mipmap.new_add_5, R.mipmap.new_add_6, R.mipmap.new_add_7, R.mipmap.new_add_8};
    private int[] pressed = new int[]{R.mipmap.new_add_1_selected, R.mipmap.new_add_2_selected, R.mipmap.new_add_3_selected, R.mipmap.new_add_4_selected, R.mipmap.new_add_5_selected, R.mipmap.new_add_6_selected, R.mipmap.new_add_7_selected, R.mipmap.new_add_8_selected};
    private Context context;
    private int currentChose = -99;
    private OnItemClickListener onItemClickListener;

    public NewAddCourseAdapter(Context context) {
        this.context = context;
    }

    public void setCurrentChose(int currentChose) {
        this.currentChose = currentChose;
        this.notifyDataSetChanged();
    }

    public int getCurrentChose() {
        return currentChose;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mx_recyclerview_add_new_course_item, null);
        return new NewAddCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (currentChose != -99) {
            if (currentChose == position) {
//                ((NewAddCourseViewHolder) holder).imgCourse.setImageResource(pressed[position]);
                ((NewAddCourseViewHolder) holder).imgCourse.setImageResource(normal[position]);
                ((NewAddCourseViewHolder) holder).rlCourse.setBackgroundResource(R.color.colorCourse);
                ((NewAddCourseViewHolder) holder).imgChose.setVisibility(View.VISIBLE);
            } else {
                ((NewAddCourseViewHolder) holder).imgCourse.setImageResource(normal[position]);
                ((NewAddCourseViewHolder) holder).rlCourse.setBackgroundResource(R.color.colorWihte);
                ((NewAddCourseViewHolder) holder).imgChose.setVisibility(View.GONE);
            }
        } else {
            ((NewAddCourseViewHolder) holder).imgChose.setVisibility(View.GONE);
            ((NewAddCourseViewHolder) holder).imgCourse.setImageResource(normal[position]);
            ((NewAddCourseViewHolder) holder).rlCourse.setBackgroundResource(R.color.colorWihte);
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
        return 8;
    }

    class NewAddCourseViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCourse;
        ImageView imgChose;
        RelativeLayout rlCourse;

        public NewAddCourseViewHolder(View itemView) {
            super(itemView);
            imgCourse = (ImageView) itemView.findViewById(R.id.img_course);
            imgChose = (ImageView) itemView.findViewById(R.id.img_chose);
            rlCourse = (RelativeLayout) itemView.findViewById(R.id.rl_course);
        }
    }
}
