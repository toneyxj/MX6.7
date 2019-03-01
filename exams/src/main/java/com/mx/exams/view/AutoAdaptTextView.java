package com.mx.exams.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Archer on 16/9/21.
 */
public class AutoAdaptTextView extends TextView {
    private boolean calculatedLines = false;

    public AutoAdaptTextView(Context context) {
        super(context);
    }

    public AutoAdaptTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoAdaptTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!calculatedLines) {
            calculateLines();
            calculatedLines = true;
        }

        super.onDraw(canvas);
    }

    private void calculateLines() {
        int mHeight = getMeasuredHeight();
        int lHeight = getLineHeight();
        int lines = mHeight / lHeight;
        setLines(lines);
    }
}
