package com.dangdang.reader.dread.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class CustomScrollView extends HorizontalScrollView{
    private Runnable mScrollerTask;
    private int mIntitPosition;
    private int mNewCheck = 100;
    private int mChildWidth = 0;

    public interface OnScrollStopListner{
        /**
         * scroll have stoped
         */
        void onScrollStoped();
        /**
         * scroll have stoped, and is at left edge
         */
        void onScrollToLeftEdge();
        /**
         * scroll have stoped, and is at right edge
         */
        void onScrollToRightEdge();
        /**
         * scroll have stoped, and is at middle
         */
        void onScrollToMiddle();
    }

    private OnScrollStopListner onScrollstopListner;

    public CustomScrollView(Context context, AttributeSet attrs){
        super(context, attrs);
        mScrollerTask = new Runnable(){
            @Override
            public void run(){
                int newPosition = getScrollX();
                if (mIntitPosition - newPosition == 0){
                    if(onScrollstopListner == null){
                        return;
                    }
                    onScrollstopListner.onScrollStoped();
                    Rect outRect = new Rect();
                    getDrawingRect(outRect);
                    if(getScrollX() == 0){
                        onScrollstopListner.onScrollToLeftEdge();
                    }else if(mChildWidth + getPaddingLeft() + getPaddingRight() == outRect.right){
                        onScrollstopListner.onScrollToRightEdge();
                    }else{
                        onScrollstopListner.onScrollToMiddle();
                    }
                } else{
                    mIntitPosition = getScrollX();
                    postDelayed(mScrollerTask, mNewCheck);
                }
            }
        };
    }
    

    public void setOnScrollStopListner(OnScrollStopListner listner){
        onScrollstopListner = listner;
    }

    public void startScrollerTask(){
        mIntitPosition = getScrollX();
        postDelayed(mScrollerTask, mNewCheck);
        checkTotalWidth();
    }
    private void checkTotalWidth(){
        if(mChildWidth > 0){
            return;
        }
        for(int i = 0; i < getChildCount(); i++){
            mChildWidth += getChildAt(i).getWidth();
        }
    }

	public int getChildWidth() {
		if(mChildWidth == 0) {
			checkTotalWidth();
		}
		return mChildWidth;
	}

	public int getIntitPosition() {
		return mIntitPosition;
	}
	
} 


