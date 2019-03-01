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
public class WeekTitleView extends View {

    private Context context;
    private float titleSize;
    private String title;
    private int titleColor;
    private int realWidth, realHeight;

    public float getTitleSize() {
        return titleSize;
    }

    public void setTitleSize(float titleSize) {
        this.titleSize = getResources().getDimension(R.dimen.moxi_text_size_24dp);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public WeekTitleView(Context context) {
        super(context);
        this.context = context;
    }

    public WeekTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public WeekTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        Paint textPaint = new Paint();
        textPaint.setTextSize(Utils.sp2px(context, getTitleSize()));
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(8);
        textPaint.setStyle(Paint.Style.FILL);

        float tempTitle = textPaint.measureText(getTitle());
        canvas.drawText(getTitle(), (realWidth - tempTitle) / 2,
                (realHeight + getTitleSize()) / 2, textPaint);
    }
}
