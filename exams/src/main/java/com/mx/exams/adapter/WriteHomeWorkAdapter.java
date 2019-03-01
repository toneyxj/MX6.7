package com.mx.exams.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mx.exams.R;

/**
 * Created by Archer on 16/9/9.
 */
public class WriteHomeWorkAdapter extends RecyclerView.Adapter {

    private Context context;

    /**
     * 做题适配器
     *
     * @param context 上下文
     */
    public WriteHomeWorkAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_write_home_item, parent, false);
        return new WriteHomeWorkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    /**
     * 让radiogroup失去点击效果
     *
     * @param testRadioGroup 目标radiogroup
     */
    public void disableRadioGroup(RadioGroup testRadioGroup) {
        for (int i = 0; i < testRadioGroup.getChildCount(); i++) {
            testRadioGroup.getChildAt(i).setEnabled(false);
        }
    }

    /**
     * 设置题目value
     *
     * @param page     页码
     * @param position 当点页面序号
     * @param view     显示题目目标view
     */
    private void setTitleValue(int page, int position, TextView view) {
        String head = "<sub><img align=\"middle\" src=\"data:image/png;base64,";
        String behind = "\" /></sub>";
    }

    /**
     * 答题viewholder
     */
    class WriteHomeWorkViewHolder extends RecyclerView.ViewHolder {
        private TextView tvHomeWorkType;
        private TextView tvHomeWorkTitle;
        private RadioButton answer1, answer2, answer3, answer4;
        private RadioGroup radioGroup;

        public WriteHomeWorkViewHolder(View itemView) {
            super(itemView);
        }
    }
}
