package com.mx.timetable.view;

import android.view.View;

/**
 * Created by Archer on 16/8/8.
 */
public class MeasureUtils {
    /**
     * 测量宽度
     *
     * @param measureSpec
     * @return
     */
    public static int measureWidth(int measureSpec) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        //设置一个默认值，就是这个View的默认宽度为500，这个看我们自定义View的要求
        int result = 500;
        if (specMode == View.MeasureSpec.AT_MOST) {//相当于我们设置为wrap_content
            result = specSize;
        } else if (specMode == View.MeasureSpec.EXACTLY) {//相当于我们设置为match_parent或者为一个具体的值
            result = specSize;
        }
        return result;
    }

    /**
     * 测量高度
     *
     * @param measureSpec
     * @return
     */
    public static int measureHeight(int measureSpec) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        int result = 500;
        if (specMode == View.MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }
}
