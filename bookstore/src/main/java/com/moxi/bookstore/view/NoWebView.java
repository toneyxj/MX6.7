package com.moxi.bookstore.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.moxi.bookstore.R;

/**
 * Created by Administrator on 2016/10/18.
 */
public class NoWebView extends WebView {

    private ProgressBar progressbar;

    @SuppressWarnings("deprecation")
    public NoWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressbar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                5, 0, 0));
        progressbar.setProgressDrawable(context.getResources().getDrawable(
                R.drawable.prosser));
        addView(progressbar);
        //setWebChromeClient(new WebChromeClient());
        setScrollContainer(false);
//        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
//        lp.x = l;
//        lp.y = t;
//        progressbar.setLayoutParams(lp);
//        super.onScrollChanged(l, t, oldl, oldt);
    }
//    private int downY;
//    private int mTouchSlop;
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                downY = (int) ev.getRawY();
//                APPLog.e("downY=" + downY);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int moveY = (int) ev.getRawY();
//                if (Math.abs(downY - moveY) > mTouchSlop) {
//                    return super.onInterceptTouchEvent(ev);
//                }else {
//                    return true;
//                }
//        }
//        return super.onInterceptTouchEvent(ev);
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return (MotionEvent.ACTION_MOVE==event.getAction());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
