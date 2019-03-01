package com.dangdang.reader.dread.view;

import java.io.File;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.zframework.plugin.AppUtil;
import com.dangdang.zframework.utils.DRUiUtility;

public class ProgressWheel extends View {

    private int barWidth;
    private float textSizeBig;
    private float textSizeSmall;
    private int barColor = 0xFFE7E7E7;
    private int txtColor = 0xFF0092F7;
    private Paint barPaint = new Paint();
    private Paint txtPaintBig = new Paint();
    private Paint txtPaintSmall = new Paint();
    private RectF circleBounds = new RectF();
    private int mCurrProgress;
    private int mMaxProgress;
    private int mStep;
    private float mAngle;
    private int mTextX1;
    private int mTextX2;
    private int mTextY;
    static final Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
    public static final int ANIM_DURATION = 800;
    private long mStartTime;

    public ProgressWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        barWidth = (int) (DRUiUtility.getDensity() * 30 + 1);
        textSizeBig = DRUiUtility.getDensity() * 45;
        textSizeSmall = DRUiUtility.getDensity() * 15;
        mTextY = (int) (DRUiUtility.getDensity() * 111);
        mTextX1 = (int) (DRUiUtility.getDensity() * 66);
        mTextX2 = (int) (DRUiUtility.getDensity() * 123.5);
        mStep = 2;
        spinHandler = new MyHandler(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = 0;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heigthWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        if (widthWithoutPadding > heigthWithoutPadding) {
            size = heigthWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        setupBounds();
        setupPaints();
        invalidate();
    }

    private void setupPaints() {
        barPaint.setColor(barColor);
        barPaint.setAntiAlias(true);
        barPaint.setStyle(Style.STROKE);
        barPaint.setStrokeWidth(barWidth);

        txtPaintBig.setColor(txtColor);
        txtPaintBig.setAntiAlias(true);
        txtPaintBig.setStyle(Style.FILL);
        txtPaintBig.setTextSize(textSizeBig);
        txtPaintSmall.setColor(txtColor);
        txtPaintSmall.setAntiAlias(true);
        txtPaintSmall.setStyle(Style.FILL);
        txtPaintSmall.setTextSize(textSizeSmall);
        String path = DangdangFileManager.getPreSetTTF();
        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
            Typeface ty = AppUtil.getInstance(getContext()).getTypeface();
            if (ty != null) {
                txtPaintBig.setTypeface(ty);
                txtPaintSmall.setTypeface(ty);
            }
        }

    }

    /**
     * Set the bounds of the component
     */
    private void setupBounds() {

        int width = getWidth();
        int height = getHeight();

        int offset = barWidth / 2;
        circleBounds = new RectF(offset, offset, width - offset, height - offset);

    }

    private Handler spinHandler;
    
    private void dealMsg(Message msg){
    	float t = interpolate();
        mCurrProgress = (int) (mMaxProgress * t);
        if (mCurrProgress > mMaxProgress)
            mCurrProgress = mMaxProgress;
        mAngle = (100 - mCurrProgress) * 3.6f;
        invalidate();
        if (mCurrProgress >= mMaxProgress)
            return;
        spinHandler.sendEmptyMessage(0);
    }
    
    private static class MyHandler extends Handler {
		private final WeakReference<ProgressWheel> mFragmentView;

		MyHandler(ProgressWheel view) {
			this.mFragmentView = new WeakReference<ProgressWheel>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			ProgressWheel service = mFragmentView.get();
			if (service != null) {
				super.handleMessage(msg);
				try {
					service.dealMsg(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(circleBounds, -90, mAngle, false, barPaint);
        if (mCurrProgress != 100) {
            canvas.drawText(String.format("%02d", mCurrProgress), mTextX1, mTextY, txtPaintBig);
            canvas.drawText("%", mTextX2, mTextY, txtPaintSmall);
        }
    }

    public void reset() {
        mAngle = 360f;
        mCurrProgress = 0;
        invalidate();
    }

    public void startProgress(int maxProgress) {
        mAngle = 360f;
        mCurrProgress = 0;
        mMaxProgress = maxProgress;
        spinHandler.sendEmptyMessageDelayed(0, 600);
        mStartTime = System.currentTimeMillis() + 600;
    }

    private float interpolate() {
        float t = 1f * (System.currentTimeMillis() - mStartTime) / ANIM_DURATION;
        t = Math.min(1f, t);
        t = sInterpolator.getInterpolation(t);
        return t;
    }
}
