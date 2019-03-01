package com.mx.timetable.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mx.timetable.R;

/**
 * Created by Archer on 16/8/7.
 */
public class TableTitleView extends View {
    private Context context;
    private float titleSize;
    private String leftTitle;
    private int leftTitleColor;
    private String rightTitle;
    private int rightTitleColor;
    private int realWidth, realHeight;

    public float getTitleSize() {
        return titleSize;
    }

    public void setTitleSize(float titleSize) {
        this.titleSize = getResources().getDimension(R.dimen.moxi_text_size_24dp);
    }

    public String getLeftTitle() {
        return leftTitle;
    }

    public void setLeftTitle(String leftTitle) {
        this.leftTitle = leftTitle;
    }

    public int getLeftTitleColor() {
        return leftTitleColor;
    }

    public void setLeftTitleColor(int leftTitleColor) {
        this.leftTitleColor = leftTitleColor;
    }

    public String getRightTitle() {
        return rightTitle;
    }

    public void setRightTitle(String rightTitle) {
        this.rightTitle = rightTitle;
    }

    public int getRightTitleColor() {
        return rightTitleColor;
    }

    public void setRightTitleColor(int rightTitleColor) {
        this.rightTitleColor = rightTitleColor;
    }

    public TableTitleView(Context context) {
        super(context);
        this.context = context;
    }

    public TableTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public TableTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
        Paint paint = new Paint();
        Paint textPaint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        textPaint.setTextSize(Utils.sp2px(context, getTitleSize()));
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(8);
        textPaint.setStyle(Paint.Style.FILL);

        float tempTitle = textPaint.measureText(getLeftTitle());
        canvas.drawText(getLeftTitle(), ((realWidth / 2) - tempTitle) / 2,
                realHeight * 2 / 3, textPaint);
        canvas.drawRect(0, 0, realWidth, realHeight, paint);
        canvas.drawLine(0, 0, realWidth, realHeight, paint);
        canvas.drawText(getRightTitle(), (realWidth * 3 - 2 * tempTitle) / 4,
                realHeight / 3, textPaint);
    }
}
