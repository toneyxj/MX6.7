package com.dangdang.reader.dread.core.epub;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.HorizontalScrollView;

public class InteractiveBlockHScrollView extends HorizontalScrollView {

    static final int NONE = 0;    
    static final int DRAG = 1;    
    static final int ZOOM = 2;    
    int mode = NONE;    

    public InteractiveBlockHScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public InteractiveBlockHScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch(ev.getActionMasked()){  
        case MotionEvent.ACTION_DOWN:  		
        	mode = DRAG;
        	break;
        case MotionEvent.ACTION_POINTER_DOWN:
        	mode = ZOOM;
        	break;
        case MotionEvent.ACTION_POINTER_UP:
        	mode = NONE;
        	break;
        case MotionEvent.ACTION_MOVE:
			if (mode == ZOOM) {
				super.onInterceptTouchEvent(ev);
				return false;
			}
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int displayHeight = MeasureSpec.getSize(heightMeasureSpec);
		int displayWidth = MeasureSpec.getSize(widthMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

}
