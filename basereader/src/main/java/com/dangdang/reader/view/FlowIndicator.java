package com.dangdang.reader.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.dangdang.reader.R;
import com.dangdang.zframework.utils.UiUtil;

/**
 * Created by liupan on 2016/6/15.
 */
public class FlowIndicator extends View {
    private int count;
    private float space, radius;
    private int point_normal_color, point_seleted_color;
    //	public static boolean DEBUG = true;
//	private Context context;
    private int seleted = 0;
    Paint paint = new Paint();
    private int mWidth;
    private int mHeight;

    public FlowIndicator(Context context, int point_normal_color_resid, int point_seleted_color_resid) {
        super(context);
        init(context, point_normal_color_resid, point_seleted_color_resid);
    }

    private void init(Context context, int point_normal_color_resid, int point_seleted_color_resid) {
        space = UiUtil.dip2px(context, 6);
        radius = UiUtil.dip2px(context, 3);
        point_normal_color = getResources()
                .getColor(point_normal_color_resid);
        point_seleted_color = getResources().getColor(
                point_seleted_color_resid);
    }

    public FlowIndicator(Context context, AttributeSet attrs) {

        super(context, attrs);
//		this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.FlowIndicator);

        count = a.getInteger(R.styleable.FlowIndicator_count, 4);
        space = a.getDimension(R.styleable.FlowIndicator_space, 9);
        radius = a.getDimension(R.styleable.FlowIndicator_point_radius, 9);

        point_normal_color = a.getColor(R.styleable.FlowIndicator_point_normal_color, 0xc8c8c8);
        point_seleted_color = a.getColor(R.styleable.FlowIndicator_point_seleted_color, 0x323232);

//		int sum = attrs.getAttributeCount();
//		if (DEBUG) {
//			String str = "";
//			for (int i = 0; i < sum; i++) {
//				String name = attrs.getAttributeName(i);
//				String value = attrs.getAttributeValue(i);
//				str += "attr_name :" + name + ": " + value + "\n";
//			}
//			Log.i("attribute", str);
//		}
        a.recycle();
    }

    public void setSize(Rect rect) {
        if (rect != null) {
            this.mWidth = rect.right - rect.left;
            this.mHeight = rect.bottom - rect.top;
        }
    }

    public void setSeletion(int index) {
        this.seleted = index;
        invalidate();
    }

    public void setCount(int count) {
        this.count = count;
        invalidate();
    }

    public void next() {
        if (seleted < count - 1)
            seleted++;
        else
            seleted = 0;
        invalidate();
    }

    public void previous() {
        if (seleted > 0)
            seleted--;
        else
            seleted = count - 1;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        float width = (getWidth() - ((radius * 2 * count) + (space * (count - 1)))) / 2.f;

        for (int i = 0; i < count; i++) {
            if (i == seleted)
                paint.setColor(point_seleted_color);
            else
                paint.setColor(point_normal_color);
            canvas.drawCircle(width + getPaddingLeft() + radius + i
                    * (space + radius + radius), getHeight() / 2, radius, paint);

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWidth != 0 && mHeight != 0) {
            setMeasuredDimension(mWidth, mHeight);
        } else {
            setMeasuredDimension(measureWidth(widthMeasureSpec),
                    measureHeight(heightMeasureSpec));
        }
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int) (getPaddingLeft() + getPaddingRight()
                    + (count * 2 * radius) + (count - 1) * radius + 1);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int) (2 * radius + getPaddingTop() + getPaddingBottom() + 1);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
}
