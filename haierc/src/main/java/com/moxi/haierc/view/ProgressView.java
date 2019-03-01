package com.moxi.haierc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 进度条View
 * Created by Administrator on 2016/10/25.
 */
public class ProgressView extends View {
    private int maxNumber;
    private int curNumber;
    private RectF rect = new RectF();
    private Paint paint;
    private ProgressListener listener;
    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
        invalidate();
    }
    public void setCurNumber(int curNumber) {
        if (curNumber>maxNumber){
            curNumber=maxNumber;
        }else if(curNumber<0) {
            curNumber=0;
        }
        this.curNumber = curNumber;
        invalidate();
        if (listener!=null){
            listener.onProgress(curNumber);
        }
    }
    public void subOrAdd(boolean add){
        if (add){
            curNumber++;
        }else {
            curNumber--;
        }
        setCurNumber(curNumber);
    }
    /**
     * 初始化控件
     * @param maxNumber 最大值
     * @param curNumber 当前值
     */
    public void initView(ProgressListener listener,int maxNumber,int curNumber){
        this.listener=listener;
        this.maxNumber=maxNumber;
        this.curNumber=curNumber;
        invalidate();
    }


    public ProgressView(Context context) {
        super(context);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        paint = new Paint();
        paint.setAntiAlias(true);
    }
    public int getCurNumber(){
        return curNumber;
    }

    private int getKitWidth(){
      return getMeasuredWidth()- getPaddingLeft()-getPaddingRight();
    }
    private int getKitHeight(){
        return getMeasuredHeight()-getPaddingBottom()-getPaddingTop();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width=getWidth();
        int height=getHeight();
        paint.setStrokeWidth(2);
        int jiange=10;
        int recw=(width-(maxNumber-2)*jiange)/(maxNumber-1);
        rect.top =0;
//        rect.left = 0;
        rect.bottom = height;
//        rect.right =0;
        for (int i = 1; i <= maxNumber; i++) {
            //绘制框
            rect.left = recw*(i-1)+(i-1)*jiange;
            rect.right =rect.left+recw;
            if (curNumber>=i){
                paint.setStyle(Paint.Style.FILL);
            }else {
                paint.setStyle(Paint.Style.STROKE);

            }
            canvas.drawRect(rect,paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventX= (int) event.getX();
        int eventY= (int) event.getY();

        if (eventX<0||eventX>getKitWidth())return false;
        if (eventY<0||eventY>getKitHeight())return false;
//        int  size=(eventX*maxNumber)/getKitWidth();
        int size=0;
        int width=getWidth();
        int jiange=10;
        int recw=(width-(maxNumber-2)*jiange)/(maxNumber-1);
        int oneSize=recw+jiange;
        size=eventX/oneSize;
//        size+=eventX%oneSize>0?1:0;

        Log.d("ProgressView-Event", String.valueOf(size));
        setCurNumber(size+1);
        return true;
    }

    public interface  ProgressListener{
       void onProgress(int size);
    }
}
