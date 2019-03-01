package com.mx.exams.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Archer on 16/10/13.
 */
public class NoPaintInvalidateRectView extends PaintInvalidateRectView {
    public NoPaintInvalidateRectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NoPaintInvalidateRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoPaintInvalidateRectView(Context context) {
        super(context);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
