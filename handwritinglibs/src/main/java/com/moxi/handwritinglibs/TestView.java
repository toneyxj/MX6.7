package com.moxi.handwritinglibs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.moxi.handwritinglibs.device.DeviceConfig;
import com.moxi.handwritinglibs.listener.DbPhotoListener;
import com.moxi.handwritinglibs.model.CodeAndIndex;
import com.moxi.handwritinglibs.utils.DbPhotoLoader;
import com.moxi.handwritinglibs.utils.StringUtils;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.api.PenReader;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiajun on 2017/2/24.
 */

public class TestView extends View implements PenReader.PenReaderCallback {
    public TestView(Context context) {
        super(context);
        initView();
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * 绘制线的宽度集合，删除线宽为最后一个宽度值
     */
    private List<Integer> paintStrokeWidths = new ArrayList<>();
    /**
     * 绘制线的画笔
     */
    private Paint paintDraw = null;
    /**
     * 擦除画笔
     */
    private Paint paintClear = null;
    /**
     * 绘制路径集合
     */
    private List<Path> paths = new ArrayList<>();
    /**
     * 绘制线的索引
     */
    private int DrawIndex = 0;
    /**
     * 是否采用笔头擦除线
     */
    private boolean nibWipe = false;
    /**
     * 当前绘制的图片
     */
    private Bitmap bitmap = null;
    /**
     * 是否有画线
     */
    private boolean isCross = false;

    public void setCross(boolean cross) {
        isCross = cross;
    }

    private PenReader penReader;

    /**
     * 保存唯一标识
     */
    private CodeAndIndex codeAndIndex;

    public void setCodeAndIndex(CodeAndIndex codeAndIndex) {
        this.codeAndIndex = codeAndIndex;
    }

    public void setSaveCode(String saveCode) {
        if (saveCode.equals(codeAndIndex.saveCode)) return;
        //更改缓存里面的图片信息
        DbPhotoLoader.getInstance().addBitmapToLruCache(saveCode, getIndex(), getBitmap(false));
        DbPhotoLoader.getInstance().clearBitmap(getSaveCode(), getIndex());
        codeAndIndex.saveCode = saveCode;
    }

    public String getSaveCode() {
        return null != codeAndIndex ? codeAndIndex.saveCode : null;
    }

    /**
     * 获得信息保存的index值
     *
     * @return
     */
    public int getIndex() {
        return null != codeAndIndex ? codeAndIndex.index : 0;
    }

    /**
     * 获得缓存的code值
     *
     * @return
     */
    public String getChacheCode() {
        return null != codeAndIndex ? codeAndIndex.saveCode + codeAndIndex.index : null;
    }

    /**
     * 设置绘制画笔的宽度
     *
     * @param paintStrokeWidths 绘制线的宽度集合，删除线宽为最后一个宽度值
     */
    public void setPaintStrokeWidths(List<Integer> paintStrokeWidths) {
        this.paintStrokeWidths.clear();
        this.paintStrokeWidths.addAll(paintStrokeWidths);
        initPaint();
    }

    /**
     * 绘制线的索引值
     *
     * @param drawIndex 索引值 对应绘制线的宽度设置 ，默认0为绘制线-1为擦除
     */
    public void setDrawIndex(int drawIndex) {
        DrawIndex = drawIndex;
    }

    private void initView() {
        //关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, new Paint());
        //初始化绘制paint
        if (paintStrokeWidths.size() == 0) {
            paintStrokeWidths.add(3);
            //删除线宽
            paintStrokeWidths.add(16);
        }
        penReader = new PenReader(getContext());
        penReader.setPenReaderCallback(this);
        initPaint();

        //临时绘制
        EpdController.setStrokeColor(Color.BLACK);
        EpdController.setStrokeStyle(0);
        EpdController.setPainterStyle(false,   // antiAlias or not
                Paint.Style.STROKE,         // stroke style
                Paint.Join.ROUND,                    // join style
                Paint.Cap.ROUND);

    }
    private float maxwidth;
    private float maxHeight;
    private float padingLeft;
    private float padingTop;
    private float padingRight;
    private float padingBottom;
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateViewMatrix();
        this.maxwidth=getWidth();
        this.maxHeight=getHeight();
        padingLeft=getPaddingLeft();
        padingTop=getPaddingTop();
        padingRight=getPaddingRight();
        padingBottom=getPaddingBottom();
    }

    /**
     * 初始化绘制画笔的宽度
     */
    private void initPaint() {
        if (paintDraw == null) {
            paintDraw = new Paint();
            paintClear = new Paint();

            //初始化绘制线的画笔
            paintDraw.setStrokeJoin(Paint.Join.ROUND);
            paintDraw.setAntiAlias(false); // 去除锯齿
            paintDraw.setStrokeCap(Paint.Cap.ROUND);
            paintDraw.setStyle(Paint.Style.STROKE);
            paintDraw.setColor(Color.BLACK);
            //初始化擦除线的画笔
            paintClear.setAntiAlias(false); // 去除锯齿
            paintClear.setStyle(Paint.Style.STROKE);
            paintClear.setAlpha(0);
            Xfermode xFermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
            paintClear.setXfermode(xFermode);

        }
        paintDraw.setStrokeWidth(paintStrokeWidths.get(0));
        paintClear.setStrokeWidth(paintStrokeWidths.get(paintStrokeWidths.size() - 1));

        resetPath();
    }

    /**
     * 重置绘制路径
     */
    private void resetPath() {
        paths.clear();
        for (int i = 0; i < paintStrokeWidths.size(); i++) {
            paths.add(new Path());
        }
    }

    /**
     * 获得当前保存绘制路径
     *
     * @return
     */
    private Path getPath() {
        int size = paths.size();
        //笔头擦除或选择了删除返回最后一个绘制路径保存path
        if (nibWipe || DrawIndex == -1) return paths.get(size - 1);
        return paths.get(DrawIndex);
    }

    /**
     * 获得画笔绘制线的宽度
     *
     * @return 返回宽度值
     */
    private int getPaintwidth() {
        int size = paintStrokeWidths.size();
        //笔头擦除或选择了删除返回最后一个绘制路径保存path
        if (nibWipe || DrawIndex == -1) return paintStrokeWidths.get(size - 1);
        return paintStrokeWidths.get(DrawIndex);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            Paint paint = new Paint();
            if (bitmap.isRecycled() && codeAndIndex != null) {
                //重新加载
                DbPhotoLoader.getInstance().loaderPhoto(codeAndIndex.saveCode, codeAndIndex.index, new DbPhotoListener() {
                    @Override
                    public void onLoaderSucess(String saveCode, int index, Bitmap bitmap) {
                        TestView.this.bitmap = bitmap;
                    }
                });
            }
            if (!bitmap.isRecycled())
                canvas.drawBitmap(bitmap, 0, 0, paint);
        }
        for (int i = 0; i < (paths.size() - 1); i++) {
            if (paths.get(i).isEmpty()) continue;
            paintDraw.setStrokeWidth(paintStrokeWidths.get(i));
            canvas.drawPath(paths.get(i), paintDraw);
        }
        int size = paintStrokeWidths.size();
        //擦除线绘制
        if (!(paths.get(size - 1)).isEmpty()) {
            paintClear.setStrokeWidth(paintStrokeWidths.get(size - 1));
            canvas.drawPath(paths.get(size - 1), paintClear);

        }
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        resetPath();
        invalidate();
    }


    private float mLastTouchX = 0;
    private float mLastTouchY = 0;

    private final RectF mDirtyRect = new RectF();

    private float[] mapPoint(float x, float y) {
        x = Math.min(Math.max(0, x), this.getWidth());
        y = Math.min(Math.max(0, y), this.getHeight());

        final int viewLocation[] = {0, 0};
        this.getLocationOnScreen(viewLocation);
        final Matrix viewMatrix = new Matrix();
        DeviceConfig deviceConfig = DeviceConfig.sharedInstance(getContext(), "note");
        viewMatrix.postRotate(deviceConfig.getViewPostOrientation());//270
        viewMatrix.postTranslate(deviceConfig.getViewPostTx(), deviceConfig.getViewPostTy());//0,825

        float screenPoints[] = {viewLocation[0] + x, viewLocation[1] + y};
        float dst[] = {0, 0};
        viewMatrix.mapPoints(dst, screenPoints);
        return dst;
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

    /**
     * 擦除
     */
    private boolean isDraaw = false;

    private void CurrentBitmap() {
        Bitmap bitmap1 = bitmap;
        bitmap = getBitmap(false);
        if (bitmap1 != null && !bitmap1.isRecycled()) {
            StringUtils.recycleBitmap(bitmap1);
        }
        if (codeAndIndex != null)
            DbPhotoLoader.getInstance().clearBitmap(codeAndIndex.saveCode, codeAndIndex.index);
        resetPath();
        invalidate();
    }

    public void clearScreen() {
        Bitmap bitmap1 = bitmap;
        bitmap = null;
        this.isCross = true;
        if (bitmap1 != null && !bitmap1.isRecycled()) {
            StringUtils.recycleBitmap(bitmap1);
        }
        resetPath();
        if (codeAndIndex != null)
            DbPhotoLoader.getInstance().clearBitmap(codeAndIndex.saveCode, codeAndIndex.index);
        invalidate();
    }

    public Bitmap getBitmap(boolean extral) {
        if (!isCross) {
            return null;
        }
        int w = getWidth();
        int h = getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        /** 如果不设置canvas画布为白色，则生成透明 */

        layout(getLeft(), getTop(), getRight(), getBottom());
        draw(c);
        if (extral) {
            isCross = false;
        }
        return bmp;
    }

    /**
     * 获得绘制的图片
     *
     * @return
     */
    public Bitmap getBitmap() {
        return getBitmap(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EpdController.leaveScribbleMode(this);
        penReader.stop();
    }

    private Matrix viewMatrix;

    private void updateViewMatrix() {
        int viewPosition[] = {0, 0};
        getLocationOnScreen(viewPosition);
        viewMatrix = new Matrix();
        viewMatrix.postTranslate(-viewPosition[0], -viewPosition[1]);
    }
    public void onResume() {
        EpdController.enterScribbleMode(this);
        penReader.start();
        penReader.resume();
        onPause();
    }

    public void onPause() {
        EpdController.leaveScribbleMode(this);
    }
    public void  onstop(){
        EpdController.leaveScribbleMode(this);
        penReader.stop();
    }

    final float pressure = 1;
    final float size = 1;
    boolean begin = false;
    private boolean isDown=false;

    @Override
    public void onBeginRawData() {
        begin = true;
        isCross = true;
        isDraaw = true;
        nibWipe = false;
    }

    @Override
    public void onEndRawData() {
        if (!begin&&isDown){
                invalidate();
        }
        isDown=false;
        EpdController.leaveScribbleMode(TestView.this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDown=true;
                if (isDraaw){
                    EpdController.enterScribbleMode(this);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onRawTouchPointListReceived(TouchPointList touchPointList) {
        if (!isDown)return;
        int paintSize = getPaintwidth();
        int middleSize = paintSize / 2;
        for (TouchPoint touchPoint : touchPointList.getPoints()) {
            TouchPoint point = mapScreenPointToPage(touchPoint);
            float eventX = point.getX();
            float eventY = point.getY();
            float dst[] = mapPoint(point.getX(), point.getY());
            if (begin) {
                if (eventX < 0 || eventY < 0 || eventX > getWidth() || eventY > getHeight()) {
                    continue;
                }
                getPath().moveTo(eventX, eventY);
                EpdController.startStroke(paintSize, dst[0], dst[1], pressure, size, System.currentTimeMillis());
            } else {
                if (eventX < (padingLeft + middleSize)) {
                    eventX = padingLeft + middleSize;
                } else if (eventX > (maxwidth - (padingRight + middleSize))) {
                    eventX = maxwidth - (padingRight + middleSize);
                }
                if (eventY < (padingTop + middleSize)) {
                    eventY = padingTop + middleSize;
                } else if (eventY > (maxHeight - (padingBottom + middleSize))) {
                    eventY = maxHeight - (padingBottom + middleSize);
                }
                getPath().lineTo(eventX, eventY);
                EpdController.addStrokePoint(paintSize, dst[0], dst[1], pressure, size, System.currentTimeMillis());
            }
            begin = false;
        }
    }

    private TouchPoint mapScreenPointToPage(final TouchPoint touchPoint) {
        float dstPoint[] = {0, 0};
        float srcPoint[] = {0, 0};
        dstPoint[0] = touchPoint.x;
        dstPoint[1] = touchPoint.y;
        if (viewMatrix != null) {
            srcPoint[0] = touchPoint.x;
            srcPoint[1] = touchPoint.y;
            viewMatrix.mapPoints(dstPoint, srcPoint);
        }
        touchPoint.x = dstPoint[0];
        touchPoint.y = dstPoint[1];
        return touchPoint;
    }

    @Override
    public void onBeginErasing() {
        EpdController.leaveScribbleMode(this);
        begin = true;
        isCross = true;
        nibWipe = true;
        isDraaw = false;
    }

    @Override
    public void onEndErasing() {
        if (!begin&&isDown){
            CurrentBitmap();
        }
        isDown=false;
        EpdController.leaveScribbleMode(TestView.this);
    }

    @Override
    public void onEraseTouchPointListReceived(TouchPointList touchPointList) {
        if (!isDown)return;
        int size=touchPointList.getPoints().size();
        for (int i = 0; i < size; i++) {
            TouchPoint touchPoint = touchPointList.getPoints().get(i);
            TouchPoint point = mapScreenPointToPage(touchPoint);
            float eventX = point.getX();
            float eventY = point.getY();
            int paintSize = getPaintwidth();
            if (begin) {
                if (eventX < 0 || eventY < 0 || eventX > getWidth() || eventY > getHeight()) {
                    begin = true;
                    continue;
                }
                mLastTouchX = eventX;
                mLastTouchY = eventY;
                getPath().moveTo(eventX, eventY);
            } else {
                getPath().lineTo(eventX, eventY);
                if (i == (size - 1)) {
                    resetDirtyRect(eventX, eventY);
                    postInvalidate((int) (mDirtyRect.left - paintSize),
                            (int) (mDirtyRect.top - paintSize),
                            (int) (mDirtyRect.right + paintSize),
                            (int) (mDirtyRect.bottom + paintSize));
                    mLastTouchX = eventX;
                    mLastTouchY = eventY;
                } else {
                    getDirtyRect(eventX, eventY);
                }
            }
            begin = false;
        }
    }

}

