package com.dangdang.reader.dread.view;

import java.io.File;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.SweepGradient;
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

public class ProgressTrainingWheel extends View {

    private int barWidth;
    private int barOutWidth;
    private float textSizeBig;
    private float textSizeSmall;
    private int bgColor = 0xE5000000;
    private int barColor = 0xFFFFFFFF;
    private int barColorDone = 0xFFFE605F;
    private int progressStartColor = 0xFFfe9a69;
    private int progressEndColor = 0xFFfe605f;
    private int txtColor = 0xFFFE605F;
    private Paint bgPaint = new Paint();
    private Paint barPaint = new Paint();
    private Paint barPaintDone = new Paint();
    private Paint txtPaintBig = new Paint();
    private Paint txtPaintSmall = new Paint();
    private RectF circleBounds = new RectF();
    private RectF processBounds = new RectF();
    private int mCurrProgress = 0;
    private int mMaxProgress;
    private int mStep;
    private float mAngle = 360f;
    private int mTextX1;
    private int mTextX2;
    private int mTextY;
    static final Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
    public static final int ANIM_DURATION = 800;
    private long mStartTime;

    public ProgressTrainingWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        barWidth = (int) (DRUiUtility.getDensity() * 2);
        barOutWidth = (int) (DRUiUtility.getDensity() * 4);
        textSizeBig = DRUiUtility.getDensity() * (float)12.5;
        textSizeSmall = DRUiUtility.getDensity() * (float)12.5;
        mTextY = (int) (DRUiUtility.getDensity() * 32);
        mTextX1 = (int) (DRUiUtility.getDensity() * 15);
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
        bgPaint.setColor(bgColor);
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Style.FILL);

        barPaint.setColor(barColor);
        barPaint.setAntiAlias(true);
        barPaint.setStyle(Style.STROKE);
        barPaint.setStrokeWidth(barWidth);

        barPaintDone.setColor(barColorDone);
        barPaintDone.setAntiAlias(true);
        barPaintDone.setStyle(Style.STROKE);
        barPaintDone.setStrokeWidth(barWidth);
        SweepGradient shader = new SweepGradient(getWidth() / 2, getHeight() / 2, progressStartColor, progressEndColor);
//        LinearGradient shader = new LinearGradient(3, 3, (mWidth - 3)
//                * section, mHeight - 3, colors, null,
//                Shader.TileMode.MIRROR);
        barPaintDone.setShader(shader);

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
        processBounds = new RectF(offset + barOutWidth, offset + barOutWidth, width - offset - barOutWidth, height - offset - barOutWidth);
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
		private final WeakReference<ProgressTrainingWheel> mFragmentView;

		MyHandler(ProgressTrainingWheel view) {
			this.mFragmentView = new WeakReference<ProgressTrainingWheel>(view);
		}

		@Override
		public void handleMessage(Message msg) {
            ProgressTrainingWheel service = mFragmentView.get();
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
        Matrix mtx = new Matrix();
        mtx.setTranslate(-getWidth() / 2, -getHeight() / 2);
        mtx.postRotate(270);
        mtx.postTranslate(getWidth() / 2, getHeight() / 2);
        canvas.setMatrix(mtx);
        canvas.drawCircle((circleBounds.left + circleBounds.right) / 2, (circleBounds.top + circleBounds.bottom) / 2,
                circleBounds.width() / 2, bgPaint);
        canvas.drawArc(processBounds, 360 - mAngle, mAngle, false, barPaint);
        canvas.drawArc(processBounds, 0, 360 - mAngle, false, barPaintDone);
        canvas.setMatrix(new Matrix());
//        canvas.drawText(String.format("%02d%%", mCurrProgress), mTextX1, mTextY, txtPaintBig);
//            canvas.drawText("%", mTextX2, mTextY, txtPaintSmall);
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
