package com.dangdang.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mx.mxbase.constant.APPLog;

/**
 * 进度条View
 * Created by Administrator on 2016/10/25.
 */
public class DDProgressView extends View{
    private int maxNumber;
    private int curNumber;
    private RectF rect = new RectF();
    private Paint paint;
    private ProgressListener listener;

    private int ratio=6;

    public void setRatio(int ratio) {
        this.ratio = ratio;
        invalidate();
    }

    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
        invalidate();
    }
    public void setCurNumber(int curNumber) {
        setCurNumber(curNumber,false);
    }
    public void setCurNumber(int curNumber,boolean isUp) {
        if (curNumber>maxNumber){
            curNumber=maxNumber;
        }else if(curNumber<0) {
            curNumber=0;
        }
        this.curNumber = curNumber;
        invalidate();
        if (listener!=null){
            listener.onProgress(curNumber,isUp);
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



    public DDProgressView(Context context) {
        this(context,null);
    }

    public DDProgressView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DDProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
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
    public int getPaddingLeft() {
        return super.getPaddingLeft()+getKitHeight()/2;
    }

    @Override
    public int getPaddingRight() {
        return super.getPaddingRight()+getKitHeight()/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        int height=getKitHeight();
        int backHeight=height/ratio;
        //绘制背景颜色
        rect.top = getPaddingTop()+(height-backHeight)/2;
        rect.left = getPaddingLeft();
        rect.bottom = rect.top+backHeight;
        rect.right = getPaddingLeft()+getKitWidth();
        paint.setColor(Color.BLACK);
        canvas.drawRoundRect(rect, getKitHeight() / 2, getKitHeight() / 2, paint);
        paint.setStyle(Paint.Style.FILL);


        //绘制进度值
        float progress=curNumber/(float)maxNumber;
        rect.top = getPaddingTop()+(height-backHeight)/2;
        rect.left = getPaddingLeft();
        rect.bottom =  rect.top+backHeight;
        rect.right = getPaddingLeft()+getKitWidth()*progress;
        paint.setColor(Color.BLACK);
        canvas.drawRoundRect(rect, getKitHeight()/2, getKitHeight()/2, paint);

        //绘制进度圆点
        int c_y= (int) (rect.bottom-backHeight/2);
        int c_x= (int) rect.right;
        canvas.drawCircle(c_x,c_y, height/2, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventX= (int) event.getX();
        int eventY= (int) event.getY();

//        if (eventX<0||eventX>getWidth())return false;
//        if (eventY<0||eventY>getHeight())return false;
        int  size=((eventX-getPaddingLeft())*maxNumber)/getKitWidth();
        boolean isup=(event.getAction()==MotionEvent.ACTION_UP||event.getAction()==MotionEvent.ACTION_CANCEL);
        APPLog.e("is_up="+isup+"    index="+size,"event.getAction()="+event.getAction());
        setCurNumber(size,isup);
        return true;
    }

    public interface  ProgressListener{
       void onProgress(int size,boolean isUp);
    }
}
