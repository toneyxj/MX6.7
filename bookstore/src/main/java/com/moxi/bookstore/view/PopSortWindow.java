package com.moxi.bookstore.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.moxi.bookstore.R;

/**
 * Created by Administrator on 2016/9/12.
 */
public class PopSortWindow extends PopupWindow {
    public PopSortWindow(final Activity activity,View view){
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(activity).inflate(
                R.layout.pop_sort, null);
    }

}
