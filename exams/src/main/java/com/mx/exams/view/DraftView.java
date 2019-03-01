package com.mx.exams.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mx.mxbase.utils.StringUtils;

/**
 * 自定义草稿
 * Created by Administrator on 2016/8/10.
 */
public class DraftView extends View {
    public DraftView(Context context, AttributeSet attrs,
                     int defStyle) {
        super(context, attrs, defStyle);
        initPaintView();
    }

    public DraftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaintView();
    }

    public DraftView(Context context) {
        super(context);
        initPaintView();
    }

    private void initPaintView() {
        mPaint1.setStrokeJoin(Paint.Join.ROUND);
        mPaint2.setStrokeJoin(Paint.Join.ROUND);
        mPaint3.setStrokeJoin(Paint.Join.ROUND);
        mPaint4.setStrokeJoin(Paint.Join.ROUND);

        mPaint1.setAntiAlias(false); // 去除锯齿
        mPaint1.setStrokeWidth(2);
        mPaint1.setStyle(Paint.Style.STROKE);
        mPaint1.setColor(Color.BLUE);

        mPaint2.setAntiAlias(false); // 去除锯齿
        mPaint2.setStrokeWidth(3);
        mPaint2.setStyle(Paint.Style.STROKE);
        mPaint2.setColor(Color.BLUE);

        mPaint3.setAntiAlias(false); // 去除锯齿
        mPaint3.setStrokeWidth(5);
        mPaint3.setStyle(Paint.Style.STROKE);
        mPaint3.setColor(Color.BLUE);

        mPaint4.setAntiAlias(false); // 去除锯齿
        mPaint4.setStrokeWidth(12);
        mPaint4.setStyle(Paint.Style.STROKE);
        mPaint4.setColor(Color.WHITE);
    }

    private int paintStyle =1;

    /**
     * 设置绘制线的粗细
     * @param style 0最细，1中等，2最粗
     */
    public boolean setPaint(int style) {
        if (paintStyle == style) return false;
        paintStyle = style;
        return true;
    }

    public Path getmPath() {
        int swi=paintStyle;
        if (isClearLine)swi=3;
        switch (swi) {
            case 0:
                return mPath1;
            case 1:
                return mPath2;
            case 2:
                return mPath3;
            case 3:
                return mPath4;
            default:
                break;
        }
        return mPath1;
    }
    private int getPaintSize(){
        int swi=0;
        if (isClearLine)swi=3;
        switch (paintStyle) {
            case 0:
                swi =2;
                break;
            case 1:
                swi=3;
                break;
            case 2:
                swi=5;
                break;
            case 3:
                swi=12;
                break;
            default:
                break;
        }
        return swi;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    private Bitmap bitmap = null;
    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null) {
            Paint paint = new Paint();
            int w = getWidth();
            int h = getHeight();
            Rect src = new Rect(0, 0, w, h);
            Rect des = new Rect(0, 0, w, h);
            canvas.drawBitmap(bitmap, src, des, paint);
        }
        canvas.drawPath(mPath1, mPaint1);
        canvas.drawPath(mPath2, mPaint2);
        canvas.drawPath(mPath3, mPaint3);
        canvas.drawPath(mPath4, mPaint4);

    }
    private boolean isClearLine=false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        int iDeviceId = event.getDeviceId();
        int tooolType=event.getToolType(0);
        if (iDeviceId != MotionEvent.TOOL_TYPE_FINGER) {
            return false;
        }
        if (tooolType==4){
            isClearLine=true;
        }
        return super.dispatchTouchEvent(event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getToolType(0)==4){
//            isClearLine=true;
//        }
        float eventX = event.getX();
        float eventY = event.getY();
/**
 * 保存绘制路径以及划线风格
 */
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                getmPath().moveTo(eventX, eventY);
                mLastTouchX = eventX;
                mLastTouchY = eventY;
            }
            return true;

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP: {
                resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    getDirtyRect(historicalX, historicalY);
                    getmPath().lineTo(historicalX, historicalY);
                }

                getmPath().lineTo(eventX, eventY);
                int paintSize=getPaintSize();
                invalidate((int) (mDirtyRect.left - paintSize),
                        (int) (mDirtyRect.top - paintSize),
                        (int) (mDirtyRect.right + paintSize),
                        (int) (mDirtyRect.bottom + paintSize));

                mLastTouchX = eventX;
                mLastTouchY = eventY;

                if ((event.getAction()==MotionEvent.ACTION_UP)&&isClearLine){
                    CurrentBitmap();
                    isClearLine=false;
                }
            }
            break;
            default:
                return false;
        }
        return true;
    }

    private void getDirtyRect(float historicalX, float historicalY) {

        if (historicalX < mDirtyRect.left) {
            mDirtyRect.left = historicalX;
        } else if (historicalX > mDirtyRect.right) {
            mDirtyRect.right = historicalX;
        }
        if (historicalY < mDirtyRect.top) {
            mDirtyRect.top = historicalY;
        } else if (historicalY > mDirtyRect.bottom) {
            mDirtyRect.bottom = historicalY;
        }
    }

    private void resetDirtyRect(float eventX, float eventY) {
        mDirtyRect.left = Math.min(mLastTouchX, eventX);
        mDirtyRect.right = Math.max(mLastTouchX, eventX);
        mDirtyRect.top = Math.min(mLastTouchY, eventY);
        mDirtyRect.bottom = Math.max(mLastTouchY, eventY);
    }

    private static final float STROKE_WIDTH = 5f;
    private float mLastTouchX = 0;
    private float mLastTouchY = 0;

    private final RectF mDirtyRect = new RectF();
    private Paint mPaint1 = new Paint();
    private Paint mPaint2 = new Paint();
    private Paint mPaint3 = new Paint();
    private Paint mPaint4 = new Paint();

    private Path mPath1 = new Path();
    private Path mPath2 = new Path();
    private Path mPath3 = new Path();
    private Path mPath4 = new Path();

    private void CurrentBitmap(){
//        if (this.bitmap!=null){
//            StringUtils.recycleBitmap(this.bitmap);
//        }
        bitmap=getBitmap();
        reInitPath();
        invalidate();
    }
    public Bitmap getBitmap() {
        int w = getWidth();
        int h = getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        /** 如果不设置canvas画布为白色，则生成透明 */

        layout(0, 0, w, h);
        draw(c);
        return bmp;
    }
    /**
     * 清空草稿
     */
    public void ClearDraft(){
        if (this.bitmap!=null){
            StringUtils.recycleBitmap(this.bitmap);
        }
        reInitPath();
        invalidate();
    }

    private void reInitPath(){
        mPath1=new Path();
        mPath2=new Path();
        mPath3=new Path();
        mPath4=new Path();
    }
}