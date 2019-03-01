package com.mx.main.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.mx.main.R;
import com.mx.mxbase.utils.DensityUtil;
import com.mx.mxbase.utils.MXReaderManager;

/**
 * 圆形菜单
 * Created by Archer on 16/8/16.
 */
public class RoundView extends View {

    private Paint mPaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint userPaint = new Paint();
    private Rect uRect;
    private PaintFlagsDrawFilter pfd;
    private BigStone[] mStones;// stone列表
    private int mPointX = 0, mPointY = 0;// 圆心坐标
    private int mRadius = 0;// 半径
    private int mDegreeDelta;// 每两个点间隔的角度
    private GestureDetector mGestureDetector;// Touch detection
    private OnRoundMenuViewListener mListener;//自定义事件监听器
    private Context context;

    private String userName;// 用户名
    private String week;// 当前星期
    private String date;// 当前日期
    private int[] menuImg;
    private Rect innerRect;
    private boolean hasUpdate = false;

    public int[] getMenuImg() {
        return menuImg;
    }

    public void setMenuImg(int[] menuImg) {
        this.menuImg = menuImg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        invalidate();
    }

    public void setHasUpdate(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
        invalidate();
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public interface OnRoundMenuViewListener {
        void onSingleTapUp(int position);//监听每个菜单的单击事件
    }

    public RoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        mPaint.setPathEffect(effects);
        mGestureDetector = new GestureDetector(getContext(),
                new MyGestureListener());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mPointX = this.getMeasuredWidth() / 2 + 20;//设置向右偏移
        mPointY = this.getMeasuredHeight() / 2 + 50;
        mRadius = mPointX - mPointX * 4 / 13;//初始化半径
        //设置
    }

    /**
     * 初始化每个点
     */
    private void setupStones() {
        mStones = new BigStone[menuImg.length];
        BigStone stone;
        int angle = 100;
        mDegreeDelta = 360 / menuImg.length;
        for (int index = 0; index < menuImg.length; index++) {
            stone = new BigStone();
            if (angle >= 360) {
                angle -= 360;
            } else if (angle < 0) {
                angle += 360;
            }
            stone.angle = angle;
            Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), menuImg[index]);
            stone.bitmap = tempBitmap;
            stone.radius = DensityUtil.px2dip(context, tempBitmap.getWidth()) * 2 / 5;
            angle += mDegreeDelta;
            mStones[index] = stone;
        }
        computeCoordinates();
    }

    /**
     * 计算每个点的坐标
     */
    private void computeCoordinates() {
        BigStone stone;
        for (int index = 0; index < menuImg.length; index++) {
            stone = mStones[index];
            stone.x = mPointX
                    + (float) ((mRadius + stone.radius / 3) * Math.cos(Math.toRadians(stone.angle)));
            stone.y = mPointY
                    + (float) ((mRadius + stone.radius / 3) * Math.sin(Math.toRadians(stone.angle)));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int cur = getInCircle((int) e.getX(), (int) e.getY());
            if (cur != -1) {
                if (mListener != null) {
                    mListener.onSingleTapUp(cur);
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        setupStones();
        //画虚线圆
        mPaint.setColor(Color.BLACK);
        canvas.drawCircle(mPointX, mPointY, mRadius, mPaint);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20);
        textPaint.setStyle(Paint.Style.FILL);
        BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.mipmap.mx_img_main_user);
        drawCenterUser(canvas, bd.getBitmap());
        drawDateArea(canvas, R.mipmap.mx_img_main_date);
        //将每个菜单画出来
        for (int index = 0; index < menuImg.length; index++) {
            if (!mStones[index].isVisible)
                continue;
            drawInCenter(canvas, mStones[index].bitmap, mStones[index].x,
                    mStones[index].y, index);
        }
    }

    /**
     * 绘制日期显示区域
     *
     * @param canvas 画板
     * @param res    资源
     */
    private void drawDateArea(Canvas canvas, int res) {
        BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(res);
        Bitmap tempbp = bd.getBitmap();
        int uWidth = tempbp.getWidth();
        int uHeight = tempbp.getHeight();
        Rect uRect = new Rect();
        uRect.left = this.getMeasuredWidth() - uWidth * 10 / 11;
        uRect.top = 0;
        uRect.right = this.getMeasuredWidth();
        uRect.bottom = uHeight * 7 / 8;
        canvas.setDrawFilter(pfd);
        canvas.drawBitmap(tempbp, null, uRect, mPaint);
        if (getWeek() == null) {
            week = "";
        } else {
            week = getWeek();
        }
        float weekLenth = textPaint.measureText(getWeek());
        canvas.drawText(week, uRect.left + uWidth * 7 / 16 - weekLenth / 2,
                uRect.bottom * 7 / 16, textPaint);
        if (getDate() == null) {
            date = "";
        } else {
            date = getDate();
        }
        float dateLenth = textPaint.measureText(week);
        canvas.drawText(date, uRect.left + uWidth * 7 / 16 - dateLenth / 2,
                uRect.bottom * 7 / 16 + 46, textPaint);
    }

    /**
     * 绘制中心用户信息
     *
     * @param canvas
     * @param bitmap
     */
    private void drawCenterUser(Canvas canvas, Bitmap bitmap) {
        userPaint.setStyle(Paint.Style.STROKE);
        userPaint.setColor(Color.BLACK);
        uRect = new Rect();
        int uWidth = bitmap.getWidth();
        int uHeight = bitmap.getHeight();
        uRect.left = mPointX - uWidth / 2;
        uRect.top = mPointY - uHeight / 2;
        uRect.right = mPointX + uWidth / 2;
        uRect.bottom = mPointY + uHeight / 2;
        canvas.setDrawFilter(pfd);
        canvas.drawBitmap(bitmap, null, uRect, mPaint);
        RectF infoR = new RectF(mPointX - uWidth / 2, mPointY + uHeight / 2 + 10
                , mPointX + uWidth / 2, mPointY + uHeight / 2 + 50);
        canvas.drawRoundRect(infoR, 20, 20, userPaint);
        String tempName = "";
        if (getUserName() == null) {
            tempName = "用户名：";
        } else {
            tempName = "用户名：" + getUserName();
        }
        float nameLenth = textPaint.measureText(tempName);
        canvas.drawText(tempName, mPointX - nameLenth / 2, mPointY + uHeight / 2 + 36, textPaint);
    }

    /**
     * 把中心点放到中心处
     *
     * @param canvas
     * @param bitmap
     * @param left
     * @param top
     */
    private void drawInCenter(Canvas canvas, Bitmap bitmap, float left,
                              float top, int index) {
        //Todo  现在没有写课外阅读那个图，根据index判断
        Rect dst = new Rect();
        dst.left = (int) (left - mStones[index].radius);
        dst.right = (int) (left + mStones[index].radius);
        dst.top = (int) (top - mStones[index].radius);
        dst.bottom = (int) (top + mStones[index].radius);
        canvas.setDrawFilter(pfd);
        canvas.drawBitmap(bitmap, null, dst, mPaint);
        if (index == 2) {
            BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.mipmap.menu_to_reader);
            Bitmap bMap = bd.getBitmap();
            bMap.getWidth();
            bMap.getHeight();
            innerRect = new Rect();
            innerRect.left = (int) (left - mStones[index].radius) + bitmap.getWidth() / 2 - 34;
            innerRect.right = (int) (left + bMap.getWidth()) - 34;
            innerRect.top = (int) (top - bMap.getHeight() / 2) + 34;
            innerRect.bottom = (int) (top + bMap.getHeight() / 2) + 30;
            canvas.setDrawFilter(pfd);
            canvas.drawBitmap(bMap, null, innerRect, mPaint);

            String temp = MXReaderManager.queryReadFile(context);
            temp = temp.substring(temp.lastIndexOf("/") + 1);
            TextPaint tp = new TextPaint();
            tp.setColor(Color.WHITE);
            tp.setStyle(Paint.Style.FILL);
            tp.setTextSize(20);
            Point point = new Point(innerRect.left + 50, innerRect.top - 40);
            textCenter(temp, tp, canvas, point, 150, Layout.Alignment.ALIGN_CENTER, 1.0f, 0, false);
        } else if (index == 5 && hasUpdate) {
            Paint update = new Paint();
            update.setStyle(Paint.Style.FILL);
            update.setStrokeWidth(2.0f);
            update.setAntiAlias(false);
            canvas.drawCircle(left + mStones[index].radius / 2 - 6, top - mStones[index].radius, 10, update);

            Paint number = new Paint();
            number.setStyle(Paint.Style.FILL);
            number.setStrokeWidth(3.0f);
            number.setColor(Color.WHITE);
            number.setTextSize(20);
            number.setAntiAlias(false);
            canvas.drawText("1", left + mStones[index].radius / 2 - 10, top - mStones[index].radius + 8, number);
        }
    }

    /**
     * 自动换行剧中文本绘制
     *
     * @param string      要绘制的文本
     * @param textPaint   画笔
     * @param canvas      画布
     * @param point       起始点
     * @param width       设置宽度
     * @param align       排版方式
     * @param spacingmult 行间距
     * @param spacingadd  左右间距
     * @param includepad
     */
    private void textCenter(String string, TextPaint textPaint, Canvas canvas, Point point, int width,
                            Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        StaticLayout staticLayout = new StaticLayout(string, textPaint, width, align, spacingmult, spacingadd, includepad);
        canvas.save();
        canvas.translate(-staticLayout.getWidth() / 2 + point.x, -staticLayout.getHeight() / 2 + point.y);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    /**
     * x y是否在点击范围,没有精确计算，只是计算矩形范围
     *
     * @param x
     * @param y
     * @return
     */
    private int getInCircle(int x, int y) {
        int temp = -1;
        for (int i = 0; i < menuImg.length; i++) {
            BigStone stone = mStones[i];
            int mx = (int) stone.x;
            int my = (int) stone.y;
            if ((x >= mx - stone.radius && x <= mx + stone.radius)
                    && (y >= my - stone.radius && y <= my + stone.radius)) {
                if (i == 2) {
                    if ((x >= innerRect.left && x <= innerRect.right) && (y >= innerRect.top && y <= innerRect.bottom)) {
                        temp = 99;
                    } else {
                        temp = i;
                    }
                } else {
                    temp = i;
                }
            }
        }
        if ((x >= mPointX - uRect.width() / 2 && x <= mPointX + uRect.width() / 2)
                && (y >= mPointY - uRect.height() / 2 && y <= mPointY + uRect.height() / 2)) {
            temp = mStones.length;
        }
        return temp;
    }

    public void setOnRoundMenuViewListener(OnRoundMenuViewListener listener) {
        this.mListener = listener;
    }

    class BigStone {
        Bitmap bitmap;// 图片
        int angle;// 角度
        float x;// x坐标
        float y;// y坐标
        int radius; // 菜单的半径
        boolean isVisible = true;// 是否可见
    }
}
