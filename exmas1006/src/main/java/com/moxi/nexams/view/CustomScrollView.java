package com.moxi.nexams.view;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.Log;

/**
 * Created by Archer on 2017/1/10.
 */
public class CustomScrollView extends NestedScrollView {

    private int downX;
    private int downY;
    private int tempX;
    private int mTouchSlop;
    private boolean join = false;
    private SlideListener slideListener;
    private int scrollHigh = 0;

    public void setSlideListener(SlideListener slideListener) {
        this.slideListener = slideListener;
    }

    public void reloadView() {
        this.scrollTo(0, 0);
        invalidate();
    }

    public void setScrollHigh(int scrollHigh) {
        this.scrollHigh = scrollHigh;
    }

    /**
     * 滑动拦截方位0左滑动，1上滑动，2右滑动，3下滑动
     */
    private int direction = -1;

    public CustomScrollView(Context context) {
        super(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int iDeviceId = ev.getDeviceId();
        if (iDeviceId == MotionEvent.TOOL_TYPE_FINGER || iDeviceId >= MotionEvent.TOOL_TYPE_ERASER) {
            Log.e("笔", "回调回去了");
            return false;
        } else {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = tempX = (int) ev.getRawX();
                    downY = (int) ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveY = (int) ev.getRawY();
                    if (Math.abs(downY - moveY) > mTouchSlop
                            && Math.abs((int) ev.getRawX() - downX) < mTouchSlop) {
                        direction = ((downY - moveY) < 0) ? 1 : 3;
                        join = true;
                        return true;
                    }
                    int moveX = (int) ev.getRawX();
                    if (Math.abs(downX - moveX) > mTouchSlop
                            && Math.abs((int) ev.getRawY() - downY) < mTouchSlop) {
                        direction = ((downX - moveX) < 0) ? 0 : 2;
                        join = true;
                        return true;
                    }
                    break;
            }
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int iDeviceId = event.getDeviceId();
        if (iDeviceId == MotionEvent.TOOL_TYPE_FINGER || iDeviceId >= MotionEvent.TOOL_TYPE_ERASER) {
            Log.e("笔", "回调回去了onTouchEvent");
            return false;
        } else {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (join && direction != -1) {
                    setListener(direction);
                    join = false;
                    direction = -1;
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE && !join) {
                //左右
                int moveX = (int) event.getRawX();
                if (Math.abs(downX - moveX) > mTouchSlop
                        && Math.abs((int) event.getRawY() - downY) < mTouchSlop) {
                    join = true;
                    direction = ((downX - moveX) < 0) ? 0 : 2;
                    return true;
                }
                //上下
                int moveY = (int) event.getRawY();
                if (Math.abs(downY - moveY) > mTouchSlop
                        && Math.abs((int) event.getRawX() - downX) < mTouchSlop) {
                    direction = ((downY - moveY) < 0) ? 1 : 3;
                    join = true;
                    return true;
                }
            }
            return true;
        }
    }

    private void setListener(int direction) {
        APPLog.e("外层scrollview", direction);
        switch (direction) {
            case 0:
                if (slideListener != null)
                    slideListener.moveDirection(true, false, false, false);
                break;
            case 1:
                moveUp();
                break;
            case 2:
                if (slideListener != null)
                    slideListener.moveDirection(false, false, true, false);
                break;
            case 3:
                moveDown();
                break;
            default:
                break;
        }
    }

    public void moveUp() {
        int temp = this.getChildAt(0).getHeight();
        Log.e("我来看看高度", temp + "");
        int dada = this.getScrollY();
        Log.e("我来看看尾部", dada + "");
        scrollHigh -= 300;
        if (scrollHigh < 0) {
            scrollHigh = 0;
        }
        this.scrollTo(0, scrollHigh);
    }

    public void moveDown() {
        int temp = this.getChildAt(0).getHeight();
        int aaa = this.getMeasuredHeight();
        scrollHigh += 300;
        if (scrollHigh > temp - aaa) {
            scrollHigh = temp - aaa;
        }
        this.scrollTo(0, scrollHigh);
    }

    /**
     * 上下滑动监听
     */
    public interface SlideListener {
        /**
         * 滑动方向
         *
         * @param left  左滑动
         * @param up    上滑动
         * @param right 右滑动
         * @param down  下滑动
         */
        public void moveDirection(boolean left, boolean up, boolean right, boolean down);

        public void toBootom();

        public void toTop();
    }
}
