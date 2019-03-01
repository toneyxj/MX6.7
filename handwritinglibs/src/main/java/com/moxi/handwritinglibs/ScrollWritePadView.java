package com.moxi.handwritinglibs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowManager;

import com.moxi.handwritinglibs.asy.SaveCommonWrite;
import com.moxi.handwritinglibs.listener.DbPhotoListener;
import com.moxi.handwritinglibs.model.CodeAndIndex;
import com.moxi.handwritinglibs.utils.DbPhotoLoader;
import com.moxi.handwritinglibs.utils.StringUtils;

import java.util.List;

import static android.graphics.Bitmap.createBitmap;

/**
 * 可滑动的绘制view
 * Created by 夏君 on 2017/4/17 0017.
 */

public class ScrollWritePadView extends WritePadBaseView {

    private int screenWidth;

    public ScrollWritePadView(Context context) {
        super(context);
    }

    public ScrollWritePadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * scroll高度
     */
    private int screenHeight = 0;
    private Bitmap ScroolBitmap = null;
    private int screenY;
    private boolean isHaveCompound = false;

    /**
     * 设置当前页面需要缓存绘制图片的高度，必须在调用设置首张显示图片前设置
     *
     * @param screenHeight 图片高度
     */
    public void setScreenHeight(int screenWidth, int screenHeight) {
        if (screenWidth<=0){
            WindowManager wm = (WindowManager) getContext()
                    .getSystemService(Context.WINDOW_SERVICE);
             screenWidth = wm.getDefaultDisplay().getWidth();
        }
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        isHaveCompound = false;
        screenY = 0;
        ScroolBitmap = createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);

        setScreenY(screenY);
    }

    public int getScreenY() {
        return screenY;
    }

    public void setScreenY(int screenY) {
        if (screenY < 0) {
            screenY = 0;
        }
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        if (screenY + height > screenHeight) {
            screenY = screenHeight - height;
        } else if (screenY + height == screenHeight) {
            return;
        }
        if (screenY == this.screenY) return;
        this.screenY = screenY;
        setScroolDrawBitmap();
    }

    public void addScreenY(int addheight) {
        setScreenY(screenY + addheight);
    }

    /**
     * 设置保存绘制的唯一标识
     *
     * @param saveCode 保存绘制的唯一标识
     */
    public void setSaveCode(final String saveCode) {
        Log.e("saveCode==", saveCode);
        setCodeAndIndex(new CodeAndIndex(saveCode, 0));
        DbPhotoLoader.getInstance().loaderPhoto(saveCode, 0, new DbPhotoListener() {
            @Override
            public void onLoaderSucess(String saveCodes, int index, Bitmap bitmap) {
                if (saveCode == saveCodes) {
                    if (ScroolBitmap==null){
                        ScroolBitmap = createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
                    }
                    if (bitmap!=null){
                        Canvas canvas=new Canvas(ScroolBitmap);
                        canvas.drawBitmap(bitmap,0,0,new Paint());
                    }
                    setScroolDrawBitmap();
                }
            }
        });
    }

    private void setScroolDrawBitmap() {
        if (getHeight()<=0){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setScroolDrawBitmap();
                }
            },100);
            return;
        }
        if (ScroolBitmap==null){
            ScroolBitmap = createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        }
        Bitmap bitmap= Bitmap.createBitmap(ScroolBitmap, 0, screenY, screenWidth,  getHeight());
//        setCross(false);
        setleaveScribbleMode();
        StringUtils.recycleBitmap(getBaseBitmap());
        setBitmap(bitmap);
    }

    @Override
    public void drawRawData(List<Point> drawPoints, Paint paint) {
        if (drawPoints.size()==0)return;
        isHaveCompound=true;
        Path path=new Path();
        path.moveTo(drawPoints.get(0).x, drawPoints.get(0).y+screenY);
        for (int i = 1; i < drawPoints.size(); i++) {
            Point point=drawPoints.get(i);
            path.lineTo(point.x,point.y+screenY);
        }
        Canvas canvas=new Canvas(ScroolBitmap);
        canvas.drawPath(path,paint);
    }

    @Override
    public void clearScreen() {
        super.clearScreen();
        StringUtils.recycleBitmap(ScroolBitmap);
        ScroolBitmap = createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        setScroolDrawBitmap();
    }

    @Override
    public void clearLauch(Bitmap bitmap1) {
        StringUtils.recycleBitmap(bitmap1);
    }

    /**
     * 保存笔记
     *
     * @param name 笔记名称
     */
    public void saveWritePad(String name) {
        Log.e("saveWritePad"+getSaveCode(),String.valueOf(isHaveCompound));
        if (null == getSaveCode()||!isHaveCompound) {
            StringUtils.recycleBitmap(ScroolBitmap);
            ScroolBitmap=null;
            return;
        }
        //更改缓存里面的图片信息
        DbPhotoLoader.getInstance().addBitmapToLruCache(getSaveCode(), ScroolBitmap);
        //异步线程修改保存图片数据信息
        new SaveCommonWrite(name, getSaveCode(), ScroolBitmap).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
