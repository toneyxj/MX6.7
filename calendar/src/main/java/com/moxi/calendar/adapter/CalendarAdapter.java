package com.moxi.calendar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.calendar.R;
import com.moxi.calendar.model.DateInfo;
import com.mx.mxbase.adapter.BAdapter;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class CalendarAdapter extends BAdapter<DateInfo> {
    public static int selectedPosition = -1;
    private HashSet<Integer> eventDateBeens;

    /**
     * 设置选中位置
     */
    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }
    public void changeSelect(){
        if (selectedPosition>28){
            int maxDay=0;
            for (int i = 27; i < getList().size(); i++) {
                DateInfo info = getList().get(i);
                if (info.date>maxDay){
                    maxDay=info.date;
                }
            }
            if (selectedPosition>maxDay)selectedPosition=maxDay;
        }
        notifyDataSetChanged();
    }

    public CalendarAdapter(Context context, List<DateInfo> list, HashSet<Integer> eventDateBeens) {
        super(context, list);
        this.eventDateBeens=eventDateBeens;
    }

    public void setEventDateBeens(HashSet<Integer> eventDateBeens) {
        this.eventDateBeens = eventDateBeens;
    }

    @Override
    public int getContentView() {
        return R.layout.gridview_item;
    }

    @Override
    public void onInitView(View view, int position, boolean firstAdd) {
        ViewHolder viewHolder;
        if (firstAdd) {
            viewHolder = new ViewHolder();
            viewHolder.date = (TextView) view.findViewById(R.id.item_date);
            viewHolder.nongliDate = (TextView) view.findViewById(R.id.item_nongli_date);
            viewHolder.point_hitn = (ImageView) view.findViewById(R.id.point_hitn);

            view.setTag(viewHolder);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        DateInfo info = getList().get(position);
        viewHolder.date.setText(info.date + "");
        viewHolder.nongliDate.setText(info.NongliDate);

        if (selectedPosition == info.date&&info.isThisMonth) {
            viewHolder.date.setBackgroundResource(R.drawable.arc_big_di_white_bian_font_calendar);
        } else {
            viewHolder.date.setBackgroundColor(Color.WHITE);
        }
        if (info.isThisMonth) {
            viewHolder.point_hitn.setVisibility(eventDateBeens.contains(info.date) ? View.VISIBLE : View.INVISIBLE);
        }else {
            viewHolder.point_hitn.setVisibility(View.INVISIBLE);
        }
        if (info.isHoliday) {
            viewHolder.nongliDate.setTextColor(Color.BLACK);
        } else {
            viewHolder.nongliDate.setTextColor(Color.GRAY);
        }

        if (!info.isThisMonth) {
            viewHolder.date.setTextColor(Color.rgb(210, 210, 210));
        } else if (info.isWeekend) {
            viewHolder.date.setTextColor(Color.rgb(0, 0, 0));
        } else {
            viewHolder.date.setTextColor(Color.rgb(63, 81, 181));
        }

//        if (info.NongliDate.length() > 3)
//            viewHolder.nongliDate.setTextSize(10);
//        if (info.NongliDate.length() >= 5)
//            viewHolder.nongliDate.setTextSize(8);
    }

    private class ViewHolder {
        TextView date;
        TextView nongliDate;
        ImageView point_hitn;
    }
}
