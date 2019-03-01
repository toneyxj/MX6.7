package com.moxi.calendar.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.moxi.calendar.R;
import com.mx.mxbase.adapter.BAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/9/6.
 */
public class DateAdapter extends BAdapter<String> {
    public String selectedPosition = "01";
    private SelectIndex selectIndex;

    /**
     * 设置选中位置
     */
    public void setSelectedPosition(String position) {
        selectedPosition = position;
        selectIndex.select(DateAdapter.this,selectedPosition );
        notifyDataSetChanged();
    }
    public void setOne(){
        setSelectedPosition(getList().get(0));
    }

    public DateAdapter(Context context, List<String> list, SelectIndex selectIndex) {
        super(context, list);
        this.selectIndex = selectIndex;
    }

    @Override
    public int getContentView() {
        return R.layout.select_item;
    }

    @Override
    public void onInitView(View view, int position, boolean firstAdd) {
        ViewHolder viewHolder;
        if (firstAdd) {
            viewHolder = new ViewHolder();
            viewHolder.item = (TextView) view.findViewById(R.id.item);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String info = getList().get(position);
        if (info.equals("-1")){
            viewHolder.item.setVisibility(View.INVISIBLE);
            return;
        }else{
            viewHolder.item.setVisibility(View.VISIBLE);
        }
        if (info .equals( selectedPosition)) {
            viewHolder.item.setTextColor(context.getResources().getColor(R.color.colorWihte));
            viewHolder.item.setBackgroundResource(R.drawable.arc_lit_di_black);
        }else{
            viewHolder.item.setTextColor(context.getResources().getColor(R.color.colorBlack));
            viewHolder.item.setBackgroundResource(R.drawable.arc_di_width_bian_font);
        }
        viewHolder.item.setText(info);

        viewHolder.item.setTag(info);
        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag=  v.getTag().toString();
                setSelectedPosition(tag);
                selectIndex.select(DateAdapter.this,tag );
            }
        });
    }

    private class ViewHolder {
        TextView item;
    }

    public interface SelectIndex {
        public void select(DateAdapter adapter, String value);
    }
}
