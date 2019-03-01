package com.dangdang.reader.dread.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.dangdang.reader.base.BaseReaderFragment;
import com.dangdang.reader.dread.FontsActivity;
import com.dangdang.reader.dread.data.FontDomain;
import com.dangdang.reader.request.RequestConstants;
import com.dangdang.reader.request.ResultExpCode;
import com.dangdang.zframework.log.LogM;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class FontBaseFragment extends BaseReaderFragment {
    public static final int REQUEST_CODE_BUY = 101;
    public static final int REQUEST_CODE_LOGIN = 102;
    protected View mView;
    protected Context mContext;
    protected boolean isLoadData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        onCreateInit(savedInstanceState);
    }

    @Override
    public View onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        if (mView == null) {
            throw new RuntimeException("please init mView in onCreateInit");
        }
        ViewParent vp = mView.getParent();
        if (vp != null) {
            ((ViewGroup) vp).removeView(mView);
        }
        return mView;
    }

    protected View findViewById(int resId) {
        if (mView == null) {
            throw new RuntimeException("please init mView in onCreateInit");
        }
        return mView.findViewById(resId);
    }

    public FontsActivity getFontsActivity() {
        return (FontsActivity) getActivity();
    }

    @Override
    public void onReady() {

    }

    /**
     * init mViewFontsActivity
     *
     * @param savedInstanceState
     */
    public abstract void onCreateInit(Bundle savedInstanceState);

    public abstract void handleSuccess(List<FontDomain> fontList);

    public boolean isLoadData() {
        return isLoadData;
    }

    public void setLoadData(boolean isLoad) {
        isLoadData = isLoad;
    }

    public void printLog(String msg) {
        LogM.d(getClass().getSimpleName(), msg);
    }

    static class MyHandler extends Handler {
        private final WeakReference<FontBaseFragment> mFragment;

        public MyHandler(FontBaseFragment mFragment) {
            this.mFragment = new WeakReference<FontBaseFragment>(mFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mFragment.get() == null || mFragment.get().getFontsActivity() == null)
                return;
            mFragment.get().getFontsActivity().hideGifLoadingByUi();
            switch (msg.what) {
                case RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS:
                    List<FontDomain> fontList = (List<FontDomain>) msg.obj;
                    mFragment.get().handleSuccess(fontList);
                    break;
                case RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL:
                    mFragment.get().handleFail((ResultExpCode) msg.obj);
                    break;
                default:
                    break;
            }

        }
    }

    protected abstract void handleFail(ResultExpCode obj);
}
