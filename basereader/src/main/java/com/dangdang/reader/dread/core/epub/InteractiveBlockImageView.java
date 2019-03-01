package com.dangdang.reader.dread.core.epub;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

import com.dangdang.reader.dread.InteractiveBlockViewActivity;

public class InteractiveBlockImageView extends ImageView {

	Context context;
	
	float nLastWidth = 0;
	float nLastHeight = 0;
	float nRenderWidth = 0;
	float nRenderHeight = 0;
	float nImageWidth = 0;
	float nImageHeight = 0;
	
	int ScreenWidth;
	int ScreenHeight;
	
    private Matrix matrix=new Matrix();  
    private Matrix savedMatrix=new Matrix();  
      
    static final int NONE = 0;    
    static final int DRAG = 1;    
    static final int ZOOM = 2;    
    int mode = NONE;    
  
    // Remember some things for zooming    
    PointF start = new PointF();    
    PointF mid = new PointF();    
    float oldDist = 1f;
    
    float ScrollX = 0;
    float ScrollY = 0;
    
    InteractiveBlockHScrollView hScrollView;
    InteractiveBlockScrollView ScrollView;
    
    public InteractiveBlockImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public InteractiveBlockImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public InteractiveBlockImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public void Init() {
		getScreenSize();
		hScrollView = (InteractiveBlockHScrollView)getParent();
		ScrollView = (InteractiveBlockScrollView)hScrollView.getParent();
	}
	
	public void getScreenSize() {
		DisplayMetrics dm = new DisplayMetrics();
		Display d = ((Activity)context).getWindowManager().getDefaultDisplay();
		d.getMetrics(dm);
		ScreenWidth = dm.widthPixels;
		ScreenHeight = dm.heightPixels;	
	}
 
	public float getRenderWidth() {
		return nRenderWidth;
	}

	public void setRenderWidth(float nRenderWidth) {
		this.nRenderWidth = nRenderWidth;
	}

	public float getRenderHeight() {
		return nRenderHeight;
	}

	public void setRenderHeight(float nRenderHeight) {
		this.nRenderHeight = nRenderHeight;
	}

	public float getImageWidth() {
		return nImageWidth;
	}

	public void setImageWidth(float nImageWidth) {
		this.nImageWidth = nImageWidth;
		this.nRenderWidth = nImageWidth;
	}

	public float getImageHeight() {
		return nImageHeight;
	}

	public void setImageHeight(float nImageHeight) {
		this.nImageHeight = nImageHeight;
		this.nRenderHeight = nImageHeight;
	}
	
    private float spacing(MotionEvent event) {
    	int nCount = event.getPointerCount();
    	if (nCount > 1) {
            float x = event.getX(0) - event.getX(1);    
            float y = event.getY(0) - event.getY(1);    
            return (float) Math.sqrt(x * x + y * y);
    	}
    	else
    		return 0;
     }    
  
   
      private void midPoint(PointF point, MotionEvent event) {    
        float x = event.getX(0) + event.getX(1);    
        float y = event.getY(0) + event.getY(1);    
        point.set(x / 2, y / 2);    
     }   
  
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
        if(event.getActionMasked()==MotionEvent.ACTION_POINTER_UP)  
        	;  
        switch(event.getActionMasked()) {  
        case MotionEvent.ACTION_DOWN:  
//			matrix.set(getImageMatrix());  
//			savedMatrix.set(matrix);  
//			start.set(event.getX(),event.getY());  
        	mode=DRAG;  
        	break;  
        case MotionEvent.ACTION_POINTER_DOWN:  //多点触控  
        	oldDist=this.spacing(event);  
        	if (oldDist > 10f) {  
				savedMatrix.set(matrix);  
				midPoint(mid,event);  
        		nLastWidth = nRenderWidth;
        		nLastHeight = nRenderHeight;
        		mode=ZOOM;  
        		ScrollX = hScrollView.getScrollX();
        		ScrollY = ScrollView.getScrollY();
            }  
        	break;  
        case MotionEvent.ACTION_UP:
        	if (mode != ZOOM) {
        		((InteractiveBlockViewActivity)context).finish();
//        		((InteractiveBlockViewActivity)context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        	}
        	mode = NONE;
        	break;
        case MotionEvent.ACTION_POINTER_UP:  
//        	mode=NONE;  
        	return false;
        case MotionEvent.ACTION_MOVE:  
            if (mode == DRAG) {         //此实现图片的拖动功能...  
//				matrix.set(savedMatrix);  
//				matrix.postTranslate(event.getX()-start.x, event.getY()-start.y);
            }  
            else if (mode == ZOOM) {// 此实现图片的缩放功能...  
            	float newDist = spacing(event);  
            	if (newDist > 10 && oldDist > 10) {      
            		float scale = newDist / oldDist;  
					matrix.set(savedMatrix);  
					matrix.postScale(scale, scale, mid.x, mid.y);   
//					setImageMatrix(matrix);  
            		float nWidth = nLastWidth * scale;
            		float nHeight = nLastHeight * scale;
            		if (ScreenWidth <= (int)nWidth || ScreenHeight <= (int)nHeight || scale > 1) {
            			nRenderWidth = nWidth;
            			nRenderHeight = nHeight;
            			LayoutParams param = new LayoutParams(getLayoutParams());//getLayoutParams();
            			param.width = (int)nRenderWidth;
            			param.height = (int)nRenderHeight;
            			
            			setLayoutParams(param);
            			
            			float XPosTo = mid.x * scale - (mid.x - ScrollX);
            			float YPosTo = mid.y * scale - (mid.y - ScrollY);
           			
            			hScrollView.smoothScrollTo((int)XPosTo, 0);
            			ScrollView.smoothScrollTo(0, (int)YPosTo);
            		}
            	}  
            }  
            break;  
        }  
        
        return true;  
    }  
    
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int displayHeight = MeasureSpec.getSize(heightMeasureSpec);
//		int displayWidth = MeasureSpec.getSize(widthMeasureSpec);
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension((int)nRenderWidth, (int)nRenderHeight);
	}
	
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
}
