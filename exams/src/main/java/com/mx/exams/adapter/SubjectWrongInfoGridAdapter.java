package com.mx.exams.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mx.exams.R;
import com.mx.exams.model.WrongExamsModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengdelong on 16/9/20.
 */

public class SubjectWrongInfoGridAdapter extends BaseAdapter {

    private List<WrongExamsModel> data = new ArrayList<WrongExamsModel>();
    private Context context;

    public SubjectWrongInfoGridAdapter(Context context, List<WrongExamsModel> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_over_all_item, null);
            viewHolder.examsName = (TextView) view.findViewById(R.id.tv_recycler_over_all_exams_name);
            viewHolder.examsTime = (TextView) view.findViewById(R.id.tv_recycler_over_all_exams_date);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.examsName.setText(data.get(position).getSubjectName() + "   " + data.get(position).getSubjectId());
//        ExamsDetails ed = GsonTools.getPerson(data.get(position).getExamsDetails(), ExamsDetails.class);
//        viewHolder.examsTime.setText(ed.getUpdatetime());
        return view;
    }

    static class ViewHolder {
        TextView examsName;
        TextView examsTime;
    }


    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
