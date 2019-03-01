package com.moxi.bookstore.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.moxi.bookstore.interfacess.MoveListener;

/**
 * Created by Administrator on 2016/9/12.
 */
public class MyrecycleView extends RecyclerView {
    private MoveListener layoutInter;

    public void setLayoutInter(MoveListener layoutInter) {
        this.layoutInter = layoutInter;
    }
    public MyrecycleView(Context context) {
        super(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public MyrecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }
    private int downX;
    private int downY;
    private int tempX;
    private int mTouchSlop;
    private boolean moveLeft;
    private  boolean join=false;
    /**
     * 事件拦截操作
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = tempX = (int) ev.getRawX();
                downY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getRawX();
                if (Math.abs(downX - moveX) > mTouchSlop
                        && Math.abs((int) ev.getRawY() - downY) < mTouchSlop) {
                    join=true;
                    moveLeft = (downX - moveX) < 0;
                    return true;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_UP){
            if (layoutInter != null&&join) {
                if (moveLeft) {
                    layoutInter.moveLeft();
                } else {
                    layoutInter.moveRight();
                }
                join=false;
            }
        }else if(event.getAction()==MotionEvent.ACTION_MOVE&&!join){
            int moveX = (int) event.getRawX();
            if (Math.abs(downX - moveX) > mTouchSlop
                    && Math.abs((int) event.getRawY() - downY) < mTouchSlop) {
                join=true;
                moveLeft = (downX - moveX) < 0;
                return true;
            }
        }
        return true;
    }
}
