package com.dangdang.reader.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dangdang.reader.db.service.DDStatisticsService;
import com.dangdang.reader.request.RequestConstants;
import com.dangdang.reader.request.RequestResult;
import com.dangdang.reader.utils.AccountManager;
import com.dangdang.reader.utils.ConfigManager;
import com.dangdang.zframework.BaseFragment;
import com.dangdang.zframework.utils.UiUtil;

import java.lang.ref.WeakReference;

/**
 * @author luxu
 */
public abstract class BaseReaderFragment extends BaseFragment implements
		OnScrollListener {

	protected String TAG;
	protected ConfigManager mConfigManager;
	protected AccountManager mAccountManager;
	protected Handler mRootHandler;
	protected boolean isVisible;
	protected ViewGroup mRootView;
    protected Context mContext;
    protected boolean isPullDown = false;
	protected boolean mIsLoading = false;
    protected DDStatisticsService mStatisticsService;
	@Override
	protected void parentInit() {
		TAG = this.getClass().getName();
		Activity ac = getActivity();
        mContext=ac.getApplicationContext();
		mRootHandler = new RootHandler(this);
		final Context context = getActivity().getApplicationContext();
		mConfigManager = new ConfigManager(context);
		mAccountManager = new AccountManager(context);
        mStatisticsService = DDStatisticsService.getDDStatisticsService(getActivity());
		super.parentInit();
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);
		mRootView = (ViewGroup) view;
        if (isFitSystemWindow())
            view.setFitsSystemWindows(true);
        return view;
    }
    /**
     * 否考虑系统窗口布局，若为false，根布局会被状态栏遮住。非纯色的须要返回false，然后通过padding调整布局
     * @return
     */
    protected boolean isFitSystemWindow(){
        return false;
    }
    @Override
	public void onScrollPrepare() {
	}

	@Override
	public void onScrollEnd() {
		onReady();
	}

	public void showGifLoadingByUi() {
		if (mRootView == null)
			return;
		showGifLoadingByUi(mRootView, -1);
	}

	public void hideGifLoadingByUi() {
		if (mRootView == null)
			return;
		super.hideGifLoadingByUi(mRootView);
	}
	public void showToast(String msg) {
        UiUtil.showToast(mContext, msg);
	}

	public void showToast(int resid) {
        UiUtil.showToast(mContext, resid);
	}

	public void cancalToast() {
	}

	protected void setHeaderId(int resourseId){
		Activity ac = getActivity();
		if (ac instanceof BaseReaderActivity) {
			((BaseReaderActivity) ac).setHeaderId(resourseId);
		}
	}

	protected void showNormalErrorView(RelativeLayout rootRl, RequestResult requestResult){
		showNormalErrorView(rootRl, requestResult, 0);
	}
	
	protected void showNormalErrorView(RelativeLayout rootRl, RequestResult requestResult, int topId){
		Activity ac = getActivity();
		if (ac instanceof BaseReaderActivity) {
			((BaseReaderActivity) ac).showNormalErrorView(rootRl, requestResult, topId);
		}
	}
	
	protected void showErrorView(RelativeLayout rootRl, int promptImageResId, int promptTextResId, int promptButtonTextResId){
		showErrorView(rootRl, promptImageResId, promptTextResId, promptButtonTextResId, null, 0);
	}
	
	protected void showErrorView(RelativeLayout rootRl, int promptImageResId, int promptTextResId, int promptButtonTextResId, OnClickListener listener, int id){
		Activity ac = getActivity();
		if (ac instanceof BaseReaderActivity) {
			((BaseReaderActivity) ac).showErrorView(rootRl, promptImageResId, promptTextResId, promptButtonTextResId, listener, id);
		}
	}
	
	protected View getErrorView(RelativeLayout rootRl, int promptImageResId,
            int promptTextResId, int promptButtonTextResId, OnClickListener listener){
		Activity ac = getActivity();
		if (ac instanceof BaseReaderActivity) {
			return ((BaseReaderActivity) ac).getErrorView(rootRl, promptImageResId, promptTextResId, promptButtonTextResId, listener);
		}
		return null;
	}
	
	protected View getNormalErrorView(RelativeLayout rootRl, RequestResult requestResult) {
		Activity ac = getActivity();
		if (ac instanceof BaseReaderActivity) {
			return ((BaseReaderActivity) ac).getNormalErrorView(rootRl, requestResult);
		}
		return null;
	}

	protected void showNoDataErrorView(RelativeLayout rootRl, int propmtImageResid,
									   int propmtTextResid, int leftButtonTextResid, int rightButtonTextResid, OnClickListener listener) {
		Activity ac = getActivity();
		if (ac instanceof BaseReaderActivity) {
			((BaseReaderActivity) ac).showNoDataErrorView(rootRl, propmtImageResid, propmtTextResid, leftButtonTextResid, rightButtonTextResid, listener);
		}
	}

	protected void hideErrorView(RelativeLayout rootRl){
		Activity ac = getActivity();
		if (ac instanceof BaseReaderActivity) {
			((BaseReaderActivity) ac).hideErrorView(rootRl);
		}
	}

	protected void gotoLogin() {
	}

	/**
	 * 错误按钮点击回调
	 */
	public void onRetryClick() {
	}

	public void onLeftClick() {
	}

	public void onRightClick() {
	}

	public void onSuccess(Message msg){
	}

	public void onFail(Message msg){

	}

	protected void lazyLoad() {

	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		if (getUserVisibleHint()) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInvisible();
		}

	}

	public boolean isFragmentVisible(){
		return isVisible;
	}

	/**
	 * 可见
	 */
	protected void onVisible() {
		lazyLoad();
	}

	/**
	 * 不可见
	 */
	protected void onInvisible() {

	}

	public boolean isLogin(){
		return mAccountManager.isLogin();
	}
	
	protected void setImageSrc(ImageView coverView, String url, int id,
            String size) {
		Activity ac = getActivity();
		if (ac instanceof BaseReaderActivity) {
			((BaseReaderActivity) ac).setImageSrc(coverView, url, id, size);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//添加友盟统计
//		UmengStatistics.onPageEnd(getClass().getSimpleName());
	}

	@Override
	public void onResume() {
		super.onResume();
		//添加友盟统计
//		UmengStatistics.onPageStart(getClass().getSimpleName());
	}

	static class RootHandler extends Handler{
		private final WeakReference<BaseReaderFragment> mActivity;

		RootHandler(BaseReaderFragment view) {
			this.mActivity = new WeakReference<BaseReaderFragment>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			BaseReaderFragment fragment = mActivity.get();
			if (fragment == null)
				return;
			fragment.hideGifLoadingByUi(fragment.mRootView);
			fragment.mIsLoading = false;
			if (msg == null || !(msg.obj instanceof RequestResult)) {
				return;
			}
			switch (msg.what) {
				case RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS:
					fragment.onSuccess(msg);
					break;
				case RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL:
					fragment.onFail(msg);
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	}
}
