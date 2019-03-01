package com.dangdang.reader.moxiUtils.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.moxiUtils.TTFModel;
import com.mx.mxbase.adapter.BAdapter;
import com.mx.mxbase.constant.APPLog;

import java.util.List;

/**
 * 字体adapter
 * Created by xj on 2017/10/30.
 */

public class FontItemAdapter extends BAdapter<TTFModel> {
public String selectedFonts;

    /**
     *
     * @param context
     * @param list
     * @param selectedFonts 选中的字体文件
     */
    public FontItemAdapter(Context context, List<TTFModel> list,String selectedFonts) {
        super(context, list);
        setSelectedFonts(selectedFonts);
    }

    public void setSelectedFonts(String selectedFonts) {
        if (selectedFonts==null||selectedFonts.isEmpty()||selectedFonts.equals("系统字体")){
            this.selectedFonts="标准";
        }else {
            this.selectedFonts = selectedFonts;
        }
        APPLog.e("selectedFonts",this.selectedFonts);
    }

    @Override
    public int getContentView() {
        return R.layout.adapter_font_item;
    }

    @Override
    public void onInitView(View view, int position, boolean firstAdd) {
        ViewHolder holder;
        if (firstAdd) {
            holder = new ViewHolder();
            holder.select_id = (ImageView) view.findViewById(R.id.select_id);
            holder.font_text = (TextView) view.findViewById(R.id.font_text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        TTFModel been = getItem(position);
        holder.select_id.setImageResource(isFile(been.ttfName)?R.drawable.select_have:R.drawable.select_non);
        holder.font_text.setText(been.ttfName);
        Typeface tf =been.getTypeface(context);
        holder.font_text.setTypeface(tf);
    }

    public class ViewHolder {
        ImageView select_id;
        TextView font_text;
    }

    /**
     * 更新选择字体文件提示
     * @param ttfName
     * @param listView
     */
    public void updateSelect(String ttfName, GridView listView) {
        setSelectedFonts(ttfName);
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        for (int i = visibleFirstPosi; i <= visibleLastPosi; i++) {
            View view = listView.getChildAt(i - visibleFirstPosi);
            ImageView select_id = (ImageView) view.findViewById(R.id.select_id);
            select_id.setImageResource(isFile(getItem(i).ttfName)?R.drawable.select_have:R.drawable.select_non);
        }
    }
    private boolean isFile(String ttfName){
        return ttfName.equals(selectedFonts);
    }
}
