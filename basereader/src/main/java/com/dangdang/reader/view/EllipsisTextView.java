package com.dangdang.reader.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;

import com.dangdang.zframework.view.DDTextView;

public class EllipsisTextView extends DDTextView {

    public interface EllipsizeListener {
        void ellipsizeStateChanged(boolean ellipsized);
    }

    public EllipsisTextView(Context context) {
        super(context);
        init(context);
    }

    public EllipsisTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EllipsisTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void addEllipsizeListener(EllipsizeListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        mEllipsizeListeners.add(listener);
    }

    public void removeEllipsizeListener(EllipsizeListener listener) {
        mEllipsizeListeners.remove(listener);
    }

    public boolean isEllipsized() {
        return mIsEllipsized;
    }

    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
        this.mMaxLines = maxLines;
        mIsStale = true;
    }

	public int getMaxLine() {
    	return mMaxLines;
    }
	
	public int getLineCount(){
		return createWorkingLayout(mFullText).getLineCount();
	}

    @Override
    public void setLineSpacing(float add, float mult) {
        this.mLineAdditionalVerticalPadding = add;
        this.mLineSpacingMultiplier = mult;
        super.setLineSpacing(add, mult);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before,
            int after) {
        super.onTextChanged(text, start, before, after);
        if (!mProgrammaticChange) {
            mFullText = text.toString();
            mIsStale = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mIsStale) {
        	super.setEllipsize(null);
        	resetText();
        }
        super.onDraw(canvas);
    }

    private void resetText() {
    	int maxLines = getMaxLine();
        String workingText = mFullText;
        boolean ellipsized = false;
        if (maxLines != -1) {
            Layout layout = createWorkingLayout(workingText);
            if (layout.getLineCount() > maxLines) {
                workingText = mFullText.substring(0,
                        layout.getLineEnd(maxLines - 1)).trim();
                while (createWorkingLayout(workingText + ELLIPSIS)
                        .getLineCount() > maxLines) {
                    workingText = workingText.substring(0, workingText.length() - 1);
                }
                workingText = workingText + ELLIPSIS;
                ellipsized = true;
            }
        }
        if (!workingText.equals(getText())) {
            mProgrammaticChange = true;
            try {
                setText(workingText);
            } finally {
                mProgrammaticChange = false;
            }
        }
        mIsStale = false;
        if (ellipsized != mIsEllipsized) {
            mIsEllipsized = ellipsized;
            for (EllipsizeListener listener : mEllipsizeListeners) {
                listener.ellipsizeStateChanged(ellipsized);
            }
        }
    }

    private Layout createWorkingLayout(String workingText) {
        return new StaticLayout(workingText, getPaint(), getWidth()
                - getPaddingLeft() - getPaddingRight(),
                Alignment.ALIGN_NORMAL, mLineSpacingMultiplier,
                mLineAdditionalVerticalPadding, false);
    }

    @Override
    public void setEllipsize(TruncateAt where) {
    }

    private final List<EllipsizeListener> mEllipsizeListeners = new ArrayList<EllipsizeListener>();
    private boolean mProgrammaticChange;
    private boolean mIsEllipsized;
    private boolean mIsStale;
    private String mFullText;
    private int mMaxLines = -1;
    private float mLineSpacingMultiplier = 1.0f;
    private float mLineAdditionalVerticalPadding = 0.0f;
    private static final String ELLIPSIS = "...";
}
