package com.dangdang.reader.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.dangdang.reader.R;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.reader.utils.Utils;
import com.dangdang.zframework.plugin.AppUtil;

import java.io.File;
import java.util.Vector;

public class CYTextView extends View {

    public int m_iTextHeight; // 文本的高度
    public int m_iTextWidth;// 文本的宽度

    private int maxLines = Integer.MAX_VALUE;
    private Paint mPaint = null;
    private String string = "";
    private float LineSpace = 0;// 行间距
    private Vector<String> m_String;

    public CYTextView(Context context, AttributeSet set) {
        super(context, set);

        TypedArray typedArray = context.obtainStyledAttributes(set,
                R.styleable.CYTextView);

        int width = typedArray.getInt(R.styleable.CYTextView_textwidth, 320);
        float textsize = typedArray.getDimension(
                R.styleable.CYTextView_cytextSize, 13);
        int textcolor = typedArray.getColor(R.styleable.CYTextView_textColor,
                Color.GRAY);
        float linespace = typedArray.getDimension(
                R.styleable.CYTextView_lineSpacingExtra, 0);

        m_String = new Vector<String>();

        typedArray.recycle();

        // 设置 CYTextView的宽度和行间距
        m_iTextWidth = width;
        LineSpace = linespace;

        // 构建paint对象
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(textcolor);
        mPaint.setTextSize(Utils.dip2px(context, textsize));
        setType(context);
    }

    private void setType(Context context) {
        try {
            String path = DangdangFileManager.getPreSetTTF();
            if(!TextUtils.isEmpty(path) && new File(path).exists()){
                Typeface ty = AppUtil.getInstance(context).getTypeface();
                if(ty != null)
                    mPaint.setTypeface(ty);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWidth(int width){
        m_iTextWidth = width;
    }

    public void setTextColor(int color){
        mPaint.setColor(color);
    }

    public void setMaxLines(int line){
        this.maxLines = line;
    }

    public int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        char ch;
        int w = 0;
        int istart = 0;
        int m_iFontHeight;
        int m_iRealLine = 0;
        int x = 2;
        int y = 30;

        m_String.clear();

        FontMetrics fm = mPaint.getFontMetrics();
        m_iFontHeight = (int) Math.ceil(fm.descent - fm.top) + (int) LineSpace;// 计算字体高度（字体高度＋行间距）
        y = m_iFontHeight;

        for (int i = 0; i < string.length(); i++) {
            ch = string.charAt(i);
            float[] widths = new float[1];
            String srt = String.valueOf(ch);
            mPaint.getTextWidths(srt, widths);

            if (ch == '\n') {
                m_iRealLine++;
                m_String.addElement(string.substring(istart, i));
                istart = i + 1;
                w = 0;
            } else {
                w += (int) (Math.ceil(widths[0]));
                if (w > m_iTextWidth) {
                    m_iRealLine++;
                    m_String.addElement(string.substring(istart, i));
                    istart = i;
                    i--;
                    w = 0;
                } else {
                    if (i == (string.length() - 1)) {
                        m_iRealLine++;
                        m_String.addElement(string.substring(istart,
                                string.length()));
                    }
                }
            }
        }
        m_iTextHeight = m_iRealLine * m_iFontHeight + 2;
        // canvas.setViewport(m_iTextWidth, m_iTextWidth);
        int i, j;
        for (i = 0, j = 0; i < m_iRealLine; i++, j++) {
            if(j == maxLines)
                break;
            canvas.drawText((String) (m_String.elementAt(i)), x, y
                    + m_iFontHeight * j, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredHeight = measureHeight(heightMeasureSpec);
        int measuredWidth = measureWidth(widthMeasureSpec);
        this.setMeasuredDimension(measuredWidth, measuredHeight);
        //	this.setLayoutParams(new LinearLayout.LayoutParams(measuredWidth,
        //			measuredHeight));
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        // Default size if no limits are specified.
        initHeight();
        int result = m_iTextHeight;
        if (specMode == MeasureSpec.AT_MOST) {
            // Calculate the ideal size of your
            // control within this maximum size.
            // If your control fills the available
            // space return the outer bound.
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            // If your control can fit within these bounds return that value.
            result = specSize;
        }
        return m_iTextHeight;
    }

    private void initHeight() {
        // 设置 CY TextView的初始高度为0
        m_iTextHeight = 0;

        // 大概计算 CY TextView所需高度
        FontMetrics fm = mPaint.getFontMetrics();
        int m_iFontHeight = (int) Math.ceil(fm.descent - fm.top)
                + (int) LineSpace;
        int line = 0;
        int istart = 0;

        int w = 0;
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            float[] widths = new float[1];
            String srt = String.valueOf(ch);
            mPaint.getTextWidths(srt, widths);

            if (ch == '\n') {
                line++;
                istart = i + 1;
                w = 0;
            } else {
                w += (int) (Math.ceil(widths[0]));
                if (w > m_iTextWidth) {
                    line++;
                    istart = i;
                    i--;
                    w = 0;
                } else {
                    if (i == (string.length() - 1)) {
                        line++;
                    }
                }
            }
        }
        if(line > maxLines)
            line = maxLines;
        m_iTextHeight = (int)((line + 0.5) * m_iFontHeight);
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // Default size if no limits are specified.
        int result = 500;
        if (specMode == MeasureSpec.AT_MOST) {
            // Calculate the ideal size of your control
            // within this maximum size.
            // If your control fills the available space
            // return the outer bound.
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            // If your control can fit within these bounds return that value.
            result = specSize;
        }
        return result;
    }

    public void setText(String text) {
        string = text;
        // requestLayout();
        // invalidate();
    }

    public String getText(){
        return string;
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        Drawable drawable = this.getBackground();
        if(drawable != null)
            drawable.setCallback(null);
    }
}
