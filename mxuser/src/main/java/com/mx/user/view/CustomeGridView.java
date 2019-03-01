package com.mx.user.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by King on 2017/11/30.
 */

public class CustomeGridView extends GridView {
    public CustomeGridView(Context context) {
        super(context);
    }

    public CustomeGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomeGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置上下不滚动
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;//true:禁止滚动
        }

        return super.dispatchTouchEvent(ev);
    }
}
