package com.moxi.calendar.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * ViewPager wrapContent解决方案
 */
public class MyViewPager extends ViewPager {
    private ViewpagerListener layoutInter;

    public void setLayoutInter(ViewpagerListener layoutInter) {
        this.layoutInter = layoutInter;
    }

    /**
     * Constructor
     *
     * @param context the context
     */
    public MyViewPager(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor
     *
     * @param context the context
     * @param attrs the attribute set
     */
    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight() * 6;
            if(h > height) height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }
    private boolean result = false;
    private int downX;
    private int downY;
    private int tempX;
    private int mTouchSlop;
    private boolean moveLeft;
    private  boolean join=false;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (result)
            return super.onInterceptTouchEvent(ev);
        else{
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
                    }else{
                        return false;
                    }
            }
            return super.onInterceptTouchEvent(ev);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (result)
            return super.onTouchEvent(event);
        else{
            if (event.getAction()==MotionEvent.ACTION_UP){
                if (layoutInter != null&&join) {
                    if (moveLeft) {
                        layoutInter.moveLast();
                    } else {
                        layoutInter.moveNext();
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
        }
            return false;
    }

    public interface ViewpagerListener {
        /**
         * 下一页
         */
        public void moveNext();

        /**
         * 上一页
         */
        public void moveLast();
    }
}
