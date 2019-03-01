package com.mx.timetable.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mx.timetable.R;

/**
 * Created by Archer on 16/8/8.
 */
public class CustomPeriods extends View {

    private Context context;
    private int realWidth, realHeight;
    private String[] periods;

    // = new String[]{"第一节", "第二节", "第三节", "第四节"}
    public void setPeriods(String[] periods) {
        this.periods = periods;
    }

    public String[] getPeriods() {
        return periods;
    }

    public CustomPeriods(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 通知view重绘
     *
     * @param periods
     */
    public void updateView(String[] periods) {
        this.periods = periods;
        invalidate();
    }

    public CustomPeriods(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CustomPeriods(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        realHeight = MeasureUtils.measureHeight(heightMeasureSpec);
        realWidth = MeasureUtils.measureWidth(widthMeasureSpec);
        setMeasuredDimension(realWidth, realHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float size=getResources().getDimension(R.dimen.moxi_text_size_24dp);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        Paint textPaint = new Paint();
        textPaint.setTextSize(Utils.sp2px(context, size));
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(8);
        textPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < periods.length; i++) {
            float tempTitle = textPaint.measureText(getPeriods()[i]);
            canvas.drawText(periods[i], (realWidth - tempTitle) / 2,
                    realHeight / (2 * periods.length) + Utils.sp2px(context, size) / 2 + realHeight * i / periods.length, textPaint);
            if (i != periods.length - 1) {
                canvas.drawLine(0, realHeight * (i + 1) / periods.length, realWidth, realHeight * (i + 1) / periods.length, paint);
            }
        }
    }
}
