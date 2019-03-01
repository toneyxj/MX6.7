package com.mx.mxbase.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * 自定义view解决onclick和ontouch事件冲突
 * Created by Archer on 16/8/3.
 */
public class CustomRecyclerView extends RecyclerView {
    GestureDetector gestureDetector;

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        if (gestureDetector!=null) {
            return gestureDetector.onTouchEvent(ev);
        }else {
            return  super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(ev);
        }
        super.dispatchTouchEvent(ev);
        return true;
    }
}
