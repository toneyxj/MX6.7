package com.moxi.calendar.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.calendar.R;
import com.moxi.calendar.ThisApplication;
import com.moxi.calendar.model.EventDateBeen;
import com.mx.mxbase.adapter.BAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/9.
 */
public class ListEventAdapter  extends BAdapter<EventDateBeen> {

    public ListEventAdapter(Context context, List<EventDateBeen> list) {
        super(context, list);
    }

    @Override
    public int getContentView() {
        return R.layout.adapter_list_event;
    }

    @Override
    public void onInitView(View view, int position, boolean firstAdd) {
        ViewHolder holder;
        if (firstAdd) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        EventDateBeen info = getList().get(position);
        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) holder.layout_set.getLayoutParams();

        if (info.isMonth){
            params.height= ThisApplication.dip2px(50);
//            holder.line_and_date_layout.setVisibility(View.INVISIBLE);
            holder.set_time.setVisibility(View.INVISIBLE);
            holder.title.setVisibility(View.INVISIBLE);
            holder.remark.setVisibility(View.INVISIBLE);
            holder.line_view.setVisibility(View.VISIBLE);
            holder.show_back.setVisibility(View.INVISIBLE);

            holder.set_month.setVisibility(View.VISIBLE);
            holder.show_month_text.setVisibility(View.VISIBLE);

            holder.set_month.setText(info.getMonth());
        }else{
            params.height= ThisApplication.dip2px(100);
//            holder.line_and_date_layout.setVisibility(View.VISIBLE);
            holder.set_time.setVisibility(View.VISIBLE);
            holder.title.setVisibility(View.VISIBLE);
            holder.remark.setVisibility(View.VISIBLE);
            holder.line_view.setVisibility(View.VISIBLE);
            holder.show_back.setVisibility(View.VISIBLE);

            holder.set_month.setVisibility(View.INVISIBLE);
            holder.show_month_text.setVisibility(View.INVISIBLE);
            holder.remark.setTextColor(context.getResources().getColor(R.color.colorGray));
            holder.set_time.setText(info.getWeek());
            holder.date_day.setText(info.getDay());
            holder.title.setText(info.getTitle());
            if (!info.getRemark().equals("")){
                holder.remark.setText("备注："+info.getRemark());
            }
        }
        holder.layout_set.setLayoutParams(params);
        if ((position==getCount()-1)||(position==0&&info.isMonth)){
            holder.line_view.setVisibility(View.INVISIBLE);
        }

    }

    static class ViewHolder {
        @Bind(R.id.set_month)
        TextView set_month;
        @Bind(R.id.show_month_text)
        TextView show_month_text;
        @Bind(R.id.set_time)
        TextView set_time;
        @Bind(R.id.date_day)
        TextView date_day;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.remark)
        TextView remark;
        @Bind(R.id.line_view)
        View line_view;
        @Bind(R.id.show_back)
        ImageView show_back;
        @Bind(R.id.layout_set)
        LinearLayout layout_set;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
