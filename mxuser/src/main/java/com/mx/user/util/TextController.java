package com.mx.user.util;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by King on 2017/12/27.
 */

public class TextController {
    private final TextView mTextView;

    private int mPageIndex;
    private String mText;
    private HashMap<Integer, Boundary> mBoundaries;
    private int mLastPageIndex;

    public TextController(@NonNull TextView textView) {
        mTextView = textView;
        mBoundaries = new HashMap<>();
        mLastPageIndex = -1;
    }

    public void onTextLoaded(@NonNull String text, @NonNull final OnInitializedListener listener) {
        mPageIndex = 0;
        mText = text;

        if (mTextView.getLayout() == null) {
            mTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressLint("NewApi")
                @Override
                public void onGlobalLayout() {
                    ViewTreeObserver obs = mTextView.getViewTreeObserver();
                    obs.removeGlobalOnLayoutListener(this);
//                    obs.removeOnGlobalLayoutListener(this);//版本区分
                    setTextWithCaching(mPageIndex, 0);
                    listener.onInitialized();
                }
            });
        } else {
            setTextWithCaching(mPageIndex, 0);
            listener.onInitialized();
        }
    }

    private void selectPage(int pageIndex) {
        String displayedText;
        if (mBoundaries.containsKey(pageIndex)) {
            Boundary boundary = mBoundaries.get(pageIndex);
            displayedText = mText.substring(boundary.start, boundary.end);
            mTextView.setText(displayedText);
        } else if (mBoundaries.containsKey(pageIndex - 1)) {
            Boundary previous = mBoundaries.get(pageIndex - 1);
            setTextWithCaching(pageIndex, previous.end);
        } else {
            // TODO implement selectPage(n), n - random int
        }
    }

    private void setTextWithCaching(int pageIndex, int pageStartSymbol) {
        String restText = mText.substring(pageStartSymbol);

        mTextView.setText(restText);

        int height = mTextView.getHeight();
        int scrollY = mTextView.getScrollY();
        Layout layout = mTextView.getLayout();
        int firstVisibleLineNumber = layout.getLineForVertical(scrollY);
        int lastVisibleLineNumber = layout.getLineForVertical(height + scrollY);

        if (mTextView.getHeight() < layout.getLineBottom(lastVisibleLineNumber)) {
            lastVisibleLineNumber--;
        }

        int start = pageStartSymbol + mTextView.getLayout().getLineStart(firstVisibleLineNumber);
        int end = pageStartSymbol + mTextView.getLayout().getLineEnd(lastVisibleLineNumber);

        if (end == mText.length()) {
            mLastPageIndex = pageIndex;
        }
        String displayedText = mText.substring(start, end);
        mTextView.setText(displayedText);
        mBoundaries.put(pageIndex, new Boundary(start, end));
    }

    public boolean next() {
        throwIfNotInitialized();
        if (isNextEnabled()) {
            selectPage(++mPageIndex);
            return true;
        }
        return false;
    }

    public boolean previous() {
        throwIfNotInitialized();
        if (isPreviousEnabled()) {
            selectPage(--mPageIndex);
            return true;
        }
        return false;
    }

    public boolean isNextEnabled() {
        throwIfNotInitialized();
        return mPageIndex < mLastPageIndex || mLastPageIndex < 0;
    }

    public boolean isPreviousEnabled() {
        throwIfNotInitialized();
        return mPageIndex > 0;
    }

    void throwIfNotInitialized() {
        if (mText == null) {
            throw new IllegalStateException("未初始化");
        }
    }

    private class Boundary {

        final int start;
        final int end;

        private Boundary(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    public interface OnInitializedListener {
        void onInitialized();
    }
}
