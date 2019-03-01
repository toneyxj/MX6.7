package com.dangdang.reader.view;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;

import com.dangdang.zframework.view.DDImageView;

/**
 * Created by liuboyu on 2015/6/3.
 */
public class CropImageView extends DDImageView {
    public CropImageView(Context context) {
        super(context);
        setScaleType(ScaleType.MATRIX);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.MATRIX);
    }

    public CropImageView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        Matrix matrix = getImageMatrix();
        float scaleFactor = getLayoutParams().width / (float) getDrawable().getIntrinsicWidth();
        matrix.setScale(scaleFactor, scaleFactor, 0, 0);
        setImageMatrix(matrix);
        return super.setFrame(l, t, r, b);
    }
}
