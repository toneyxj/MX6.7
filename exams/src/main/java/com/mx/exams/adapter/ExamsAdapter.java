package com.mx.exams.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mx.exams.R;
import com.mx.exams.model.ExamModel;
import com.mx.mxbase.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by Archer on 16/8/10.
 */
public class ExamsAdapter extends RecyclerView.Adapter {

    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<ExamModel> exams;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ExamsAdapter(Context context, List<ExamModel> exams) {
        this.context = context;
        this.exams = exams;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (exams.size() == 4) {
            view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_exams_item, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_exams_more_item, parent, false);
        }
        return new ExamsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((ExamsViewHolder) holder).tvExamsTitle.setText(exams.get(position).getExamName());
        ((ExamsViewHolder) holder).tvExamsSubject.setText(exams.get(position).getExamSubjects());
        ((ExamsViewHolder) holder).tvExamsPoint.setText(exams.get(position).getExamPoint() + "分");
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
        return exams != null ? exams.size() : 0;
    }

    class ExamsViewHolder extends RecyclerView.ViewHolder {
        TextView tvExamsTitle;
        TextView tvExamsPoint;
        TextView tvExamsSubject;

        public ExamsViewHolder(View itemView) {
            super(itemView);
            tvExamsTitle = (TextView) itemView.findViewById(R.id.tv_exams_item_title);
            tvExamsPoint = (TextView) itemView.findViewById(R.id.tv_exams_item_point);
            tvExamsSubject = (TextView) itemView.findViewById(R.id.tv_exams_item_subject);
        }
    }
}
