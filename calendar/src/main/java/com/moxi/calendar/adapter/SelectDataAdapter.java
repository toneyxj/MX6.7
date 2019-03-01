package com.moxi.calendar.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.calendar.R;
import com.moxi.calendar.model.SelectDataBeen;
import com.mx.mxbase.adapter.BAdapter;
import com.mx.mxbase.base.MyApplication;

import java.util.List;

import static com.moxi.calendar.R.id.content_min_layout;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

public class SelectDataAdapter extends BAdapter<SelectDataBeen> {
    private int screenWidth= MyApplication.ScreenWidth;
    public SelectDataAdapter(Context context, List<SelectDataBeen> list) {
        super(context, list);
    }

    @Override
    public int getContentView() {
        return R.layout.adapter_select_data;
    }

    @Override
    public void onInitView(View view, int position, boolean firstAdd) {
        ViewHolder viewHolder;
        if (firstAdd) {
            viewHolder = new ViewHolder();
            viewHolder.move_layout = (LinearLayout) view.findViewById(R.id.move_layout);
            viewHolder.last_image = (ImageView) view.findViewById(R.id.last_image);
            viewHolder.move_txt = (TextView) view.findViewById(R.id.move_txt);
            viewHolder.next_image = (ImageView) view.findViewById(R.id.next_image);

            viewHolder.content_layout = (RelativeLayout) view.findViewById(R.id.content_layout);
            viewHolder.content_min_layout = (LinearLayout) view.findViewById(content_min_layout);
            viewHolder.content = (TextView) view.findViewById(R.id.content);
            viewHolder.unit = (TextView) view.findViewById(R.id.unit);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        SelectDataBeen info = getList().get(position);
        switch (info.type){
            case 0://上一年
                viewHolder.content_layout.setVisibility(View.GONE);
                viewHolder.move_layout.setVisibility(View.VISIBLE);

                viewHolder.next_image.setVisibility(View.GONE);
                viewHolder.last_image.setVisibility(View.VISIBLE);
                viewHolder.move_txt.setText("上一年");
                break;
            case 1://下一年
                viewHolder.content_layout.setVisibility(View.GONE);
                viewHolder.move_layout.setVisibility(View.VISIBLE);

                viewHolder.next_image.setVisibility(View.VISIBLE);
                viewHolder.last_image.setVisibility(View.GONE);

                viewHolder.move_txt.setText("下一年");
                break;
            case 2://年
                viewHolder.content_layout.setVisibility(View.VISIBLE);
                viewHolder.move_layout.setVisibility(View.GONE);

                viewHolder.content_min_layout.getLayoutParams().width=MyApplication.dip2px(200);

                viewHolder.content_layout.getLayoutParams().height=MyApplication.dip2px(120);
                viewHolder.unit.setText(" 年");
                viewHolder.content.setText(String.valueOf(info.dataIndex));

                viewHolder.content_min_layout.setBackgroundDrawable(null);
                viewHolder.unit.setTextColor(context.getResources().getColor(R.color.colorGray));
                viewHolder.content.setTextColor(context.getResources().getColor(R.color.colorBlack));
                break;
            case 3://月
                viewHolder.content_layout.setVisibility(View.VISIBLE);
                viewHolder.move_layout.setVisibility(View.GONE);

                viewHolder.content_min_layout.getLayoutParams().width=MyApplication.dip2px(75);
                viewHolder.content_layout.getLayoutParams().height=MyApplication.dip2px(100);
                viewHolder.unit.setText(" 月");
                viewHolder.content.setText(String.valueOf(info.dataIndex));
                if (info.Select) {
                    viewHolder.content_min_layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.arc_di_black));
                    viewHolder.unit.setTextColor(context.getResources().getColor(R.color.colorWihte));
                    viewHolder.content.setTextColor(context.getResources().getColor(R.color.colorWihte));
                }else {
                    viewHolder.content_min_layout.setBackgroundDrawable(null);
                    viewHolder.unit.setTextColor(context.getResources().getColor(R.color.colorGray));
                    viewHolder.content.setTextColor(context.getResources().getColor(R.color.colorBlack));
                }
                break;
            default:
                break;
        }

    }
    private class ViewHolder {
        LinearLayout move_layout;
        ImageView last_image;
        TextView move_txt;
        ImageView next_image;

        RelativeLayout content_layout;
        LinearLayout content_min_layout;
        TextView content;
        TextView unit;
    }
}
