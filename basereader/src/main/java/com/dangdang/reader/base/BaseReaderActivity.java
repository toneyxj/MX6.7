package com.dangdang.reader.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dangdang.reader.R;
import com.dangdang.reader.db.service.DDStatisticsService;
import com.dangdang.reader.request.RequestConstants;
import com.dangdang.reader.request.RequestResult;
import com.dangdang.reader.request.ResultExpCode;
import com.dangdang.reader.statis.DDClickHandle;
import com.dangdang.reader.utils.AccountManager;
import com.dangdang.reader.utils.ConfigManager;
import com.dangdang.reader.utils.DangdangConfig;
import com.dangdang.reader.utils.ImageConfig;
import com.dangdang.reader.view.ErrView;
import com.dangdang.reader.view.MyProgressLoadingView;
import com.dangdang.thirdpart.ViewServer;
import com.dangdang.zframework.BaseActivity;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.network.image.ImageManager;
import com.dangdang.zframework.utils.ClickUtil;
import com.dangdang.zframework.utils.SystemBarTintManager;
import com.dangdang.zframework.utils.UiUtil;
import com.dangdang.zframework.view.ProgressLoadingView;

import java.lang.ref.WeakReference;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * @author luxu
 */
public abstract class BaseReaderActivity extends BaseActivity implements
		SwipeBackActivityBase {

	public static final int RESULT_DEL = 2;
	public static final int RESULT_REFRESH = 3;
	private static boolean mIsHide;
	protected String TAG;
	protected boolean isPullDown;

	/**
	 * 新DDClick统计
	 */
	protected DDClickHandle mDDClickHandle;
	protected DDStatisticsService mStatisticsService;

	protected AccountManager mAccountManager;
	protected ConfigManager mConfigManager;
	protected Handler mRootHandler;
	protected ViewGroup mRootView;
	protected boolean mIsLoading = false;
	protected Context mContext;
	private int mHeaderId;
	protected SystemBarTintManager mTiniManager;

	private SparseArray<ErrView> mMap = new SparseArray<ErrView>();

	private SwipeBackActivityHelper mHelper;
	final int MY_EDGE_SIZE = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			if (DangdangConfig.isDevelopEnv())
				ViewServer.get(this).addWindow(this);
		}catch(Throwable e){
			e.printStackTrace();
			finish();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}

	@Override
	protected void parentInit() {
		super.parentInit();
		TAG = this.getClass().getName();
		mContext = this;
		this.setLoadingViewType(LoadingViewType.ProgressBar);
		mRootHandler = new RootHandler(this);
		mIsHide = isAutoHideLoading();
		mDDClickHandle = new DDClickHandle(this);
		mStatisticsService = DDStatisticsService.getDDStatisticsService(this);
		final Context context = getApplicationContext();
		mAccountManager = new AccountManager(context);
		mConfigManager = new ConfigManager(context);
//		if (isAnimation())
//			overridePendingTransition(R.anim.book_review_activity_in,
//					R.anim.book_review_group_activity_out);
		// overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_left_out);
		setSwipeBack();
		initSystemBar();
	}

	private void setSwipeBack() {
		mHelper = new SwipeBackActivityHelper(this);
		mHelper.onActivityCreate();
		final float density = getResources().getDisplayMetrics().density;// 获取屏幕密度PPI
		getSwipeBackLayout().setEdgeSize((int) (MY_EDGE_SIZE * density + 0.5f));// 10dp
		int pos = SwipeBackLayout.EDGE_LEFT;
		getSwipeBackLayout().setEdgeTrackingEnabled(pos);

		setSwipeBackEnable(isSwipeBack());
	}

	private void initSystemBar() {
		if (!isTransparentSystemBar())
			return;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
		}
	}

	/**
	 * 是否支持状态栏透明
	 * 
	 * @return
	 */
	public boolean isTransparentSystemBar() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
			return false;
		return true;
	}

	/**
	 * 否考虑系统窗口布局，若为false，根布局会被状态栏遮住。非纯色的须要返回false，然后通过padding调整布局
	 * 
	 * @return
	 */
	protected boolean isFitSystemWindow() {
		return true;
	}

	/**
	 * 系统状态栏的颜色，默认为主题色，title_bg，如果isFitSystemWindow返回false，这里可能需要返回透明色
	 * 
	 * @return
	 */
	protected int getSystemBarColor() {
		return R.color.title_bg;
	}

	@TargetApi(19)
	protected void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	/**
	 * 是否支持进入和退出的平移动画
	 * 
	 * @return
	 */
	protected boolean isAnimation() {
		return true;
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(getContentView(layoutResID));
	}

	@Override
	public void setContentView(View view) {
		mRootView = (ViewGroup) view;
		setFitsSystemWindows(view);
		super.setContentView(view);
	}

	private View getContentView(int layoutResID) {
		View view = LayoutInflater.from(this).inflate(layoutResID, null);
		mRootView = (ViewGroup) view;
		// 透明状态
		setFitsSystemWindows(mRootView);

		return view;
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void setFitsSystemWindows(View view) {
		if (isTransparentSystemBar() && isFitSystemWindow())
			view.setFitsSystemWindows(true);
	}

	@Override
	public void finish() {
		super.finish();
		if (isAnimation())
//			overridePendingTransition(R.anim.book_review_group_activity_in,
//					R.anim.book_review_activity_out);
		// overridePendingTransition(R.anim.push_left_in,
		// R.anim.push_right_out);
		UiUtil.hideInput(this);
	}

	protected void setImageSrc(ImageView coverView, String url, int id,
			String size) {
		ImageManager.getInstance().dislayImage(
				ImageConfig.getBookCoverBySize(url, size), coverView, id);
	}


	protected boolean isStatisDDClick() {
		return false;
	}

	public void showToast(String msg) {
		UiUtil.showToast(mContext, msg);
	}

	public void showToast(int resid) {
		UiUtil.showToast(mContext, resid);
	}

	protected void printLog(String log) {
		LogM.i(getClass().getSimpleName(), log);
	}

	protected void printLogE(String log) {
		LogM.e(getClass().getSimpleName(), log);
	}

	/**
	 * @param resourseId
	 *            设置顶部id
	 */
	protected void setHeaderId(int resourseId) {
		mHeaderId = resourseId;
	}

	protected void showNormalErrorView(RelativeLayout rootRl,
			RequestResult requestResult, int topId) {
		View view = getNormalErrorView(rootRl, requestResult);
		if (view == null)
			return;
		showErrorView(rootRl, view, topId);
	}

	protected View getNormalErrorView(RelativeLayout rootRl,
			RequestResult requestResult) {
		if (rootRl == null || requestResult == null
				|| requestResult.getExpCode() == null) {
			return null;
		}
		ResultExpCode code = requestResult.getExpCode();

		int propmtImageResid = 0;
		int propmtTextResid = 0;
		int propmtButtonTextResid = R.string.refresh;
		if (ResultExpCode.ERRORCODE_NONET.equals(code.errorCode)) {
			propmtImageResid = R.drawable.icon_error_no_net;
			propmtTextResid = R.string.error_no_net;
		} else if (ResultExpCode.ERRORCODE_TIME_OUT.equals(code.errorCode)) {
			propmtImageResid = R.drawable.icon_error_no_net;
			propmtTextResid = R.string.error_net_time_out;
		} else {
			propmtImageResid = R.drawable.icon_error_server;
			propmtTextResid = R.string.error_server;
		}
		View view = getErrorView(rootRl, propmtImageResid, propmtTextResid,
				propmtButtonTextResid, mOnClickListener);
		return view;
	}
	protected View getSmallIconNormalErrorView(RelativeLayout rootRl,
									  RequestResult requestResult) {
		if (rootRl == null || requestResult == null
				|| requestResult.getExpCode() == null) {
			return null;
		}
		ResultExpCode code = requestResult.getExpCode();

		int propmtImageResid = 0;
		int propmtTextResid = 0;
		int propmtButtonTextResid = R.string.refresh;
		if (ResultExpCode.ERRORCODE_NONET.equals(code.errorCode)) {
			propmtImageResid = R.drawable.icon_error_no_net_small;
			propmtTextResid = R.string.error_no_net;
		} else if (ResultExpCode.ERRORCODE_TIME_OUT.equals(code.errorCode)) {
			propmtImageResid = R.drawable.icon_error_no_net_small;
			propmtTextResid = R.string.error_net_time_out;
		} else {
			propmtImageResid = R.drawable.icon_error_server_small;
			propmtTextResid = R.string.error_server;
		}
		View view = getErrorView(rootRl, propmtImageResid, propmtTextResid,
				propmtButtonTextResid, mOnClickListener);
		return view;
	}
	protected void showNormalErrorView(RelativeLayout rootRl,
			RequestResult requestResult) {
		showNormalErrorView(rootRl, requestResult, 0);
	}

	protected void showErrorView(RelativeLayout rootRl, int propmtImageResid,
			int propmtTextResid, int propmtButtonTextResid) {
		showErrorView(rootRl, propmtImageResid, propmtTextResid,
				propmtButtonTextResid, null, 0);
	}

	protected void showErrorView(RelativeLayout rootRl, int propmtImageResid,
			int propmtTextResid, int propmtButtonTextResid,
			OnClickListener listener, int id) {
		if (rootRl == null) {
			return;
		}
		View mErrorView = getErrorView(rootRl, propmtImageResid,
				propmtTextResid, propmtButtonTextResid, listener);
		showErrorView(rootRl, mErrorView, id);
	}

	protected void showNoDataErrorView(RelativeLayout rootRl, int propmtImageResid,
								 int propmtTextResid, int leftButtonTextResid, int rightButtonTextResid, OnClickListener listener) {
		if(rootRl == null){
			return;
		}

		View errorView = getNoDataErrorView(rootRl, propmtImageResid, propmtTextResid, leftButtonTextResid, rightButtonTextResid, listener);

		showErrorView(rootRl, errorView, 0);
	}

	protected void showErrorView(RelativeLayout rootRl, View mErrorView, int id) {
		if (rootRl == null || mErrorView == null) {
			return;
		}
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		if (id > 0)
			params.addRule(RelativeLayout.BELOW, id);
		else if (mHeaderId > 0)
			params.addRule(RelativeLayout.BELOW, mHeaderId);
		rootRl.addView(mErrorView, params);
	}

	protected View getErrorView(RelativeLayout rootRl, int promptImageResId,
			int promptTextResId, int promptButtonTextResId,
			OnClickListener listener) {
		if (rootRl == null) {
			return null;
		}
		ErrView mErrorView = mMap.get(rootRl.hashCode(), null);
		if (mErrorView == null) {
			mErrorView = new ErrView(this);
			mErrorView.initPromptView(promptImageResId, promptTextResId,
					promptButtonTextResId, listener, mOnClickListener);
			mMap.put(rootRl.hashCode(), mErrorView);
		}else
			mErrorView.refresh(promptImageResId, promptTextResId,
					promptButtonTextResId, listener, mOnClickListener);

		if (mErrorView.getView().getParent() != null) {
			((ViewGroup) mErrorView.getView().getParent()).removeView(mErrorView.getView());
		}
		mErrorView.getView().setVisibility(View.VISIBLE);

		return mErrorView.getView();
	}

	/**
	 * 获取数据被删除或者下架时的错误View
	 */
	protected View getNoDataErrorView(RelativeLayout rootRl, int propmtImageResid,
			int propmtTextResid, int leftButtonTextResid, int rightButtonTextResid, OnClickListener listener) {
		if (rootRl == null) {
			return null;
		}
		ErrView errorView = mMap.get(rootRl.hashCode(), null);
		if (errorView == null) {
			errorView = new ErrView(this);
			errorView.initNoDataPromptView(propmtImageResid, propmtTextResid, leftButtonTextResid, rightButtonTextResid, listener, mOnClickListener);
			mMap.put(rootRl.hashCode(), errorView);
		}else {
			errorView.refreshNoDataPromptView(propmtImageResid, propmtTextResid, leftButtonTextResid, rightButtonTextResid, listener, mOnClickListener);
		}

		if (errorView.getView().getParent() != null) {
			((ViewGroup) errorView.getView().getParent()).removeView(errorView.getView());
		}
		errorView.getView().setVisibility(View.VISIBLE);

		return errorView.getView();
	}
	protected View getNoDataErrorView(RelativeLayout rootRl, int propmtImageResid,
									  int propmtTextResid, int propmtButtonResid,int leftButtonTextResid, int rightButtonTextResid, OnClickListener listener) {
		if (rootRl == null) {
			return null;
		}
		ErrView errorView = mMap.get(rootRl.hashCode(), null);
		if (errorView == null) {
			errorView = new ErrView(this);
			errorView.initNoDataPromptView(propmtImageResid, propmtTextResid, propmtButtonResid,leftButtonTextResid, rightButtonTextResid, listener, mOnClickListener);
			mMap.put(rootRl.hashCode(), errorView);
		}else {
			errorView.refreshNoDataPromptView(propmtImageResid, propmtTextResid, propmtButtonResid,leftButtonTextResid, rightButtonTextResid, listener, mOnClickListener);
		}

		if (errorView.getView().getParent() != null) {
			((ViewGroup) errorView.getView().getParent()).removeView(errorView.getView());
		}
		errorView.getView().setVisibility(View.VISIBLE);

		return errorView.getView();
	}
	protected void hideErrorView(RelativeLayout rootRl) {
		if (rootRl == null) {
			return;
		}
		ErrView mErrorView = mMap.get(rootRl.hashCode(), null);
		if (mErrorView == null || mErrorView.getView() == null)
			return;
		else
			mMap.remove(rootRl.hashCode());
		rootRl.removeView(mErrorView.getView());
		mErrorView = null;
	}

	protected void gotoLogin() {
	}

	protected void gotoLogin(int requestCode) {
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

	@Override
	public void showGifLoadingByUi(ViewGroup view, int id) {
		if (view == null) {
			return;
		}
		if (view instanceof RelativeLayout)
			this.hideErrorView((RelativeLayout) view);
		super.showGifLoadingByUi(view, id);
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(ClickUtil.checkFastClick()){
				return;
			}

			List<Fragment> fragments = getSupportFragmentManager().getFragments();
			int i = v.getId();
			if (i == R.id.prompt_btn) {
				onRetryClick();
				dispatchRetryClickToChildFragment(fragments);

			} else if (i == R.id.left_btn) {
				onLeftClick();
				dispatchLeftClickToChildFragment(fragments);

			} else if (i == R.id.right_btn) {
				onRightClick();
				dispatchRightClickToChildFragment(fragments);

			}
		}
	};

	private void dispatchRetryClickToChildFragment(List<Fragment> fragments) {
		if (fragments != null && fragments.size() > 0) {
			for (Fragment f : fragments) {
				if (f instanceof BaseReaderFragment) {
					((BaseReaderFragment) f).onRetryClick();
				}
				if (f instanceof BaseReaderGroupFragment) {
					List<Fragment> list = ((BaseReaderGroupFragment) f).getChildFragmentManager().getFragments();
					dispatchRetryClickToChildFragment(list);
				}
			}
		}
	}

	private void dispatchLeftClickToChildFragment(List<Fragment> fragments) {
		if (fragments != null && fragments.size() > 0) {
			for (Fragment f : fragments) {
				if (f instanceof BaseReaderFragment) {
					((BaseReaderFragment) f).onLeftClick();
				}
				if (f instanceof BaseReaderGroupFragment) {
					List<Fragment> list = ((BaseReaderGroupFragment) f).getChildFragmentManager().getFragments();
					dispatchRetryClickToChildFragment(list);
				}
			}
		}
	}

	private void dispatchRightClickToChildFragment(List<Fragment> fragments) {
		if (fragments != null && fragments.size() > 0) {
			for (Fragment f : fragments) {
				if (f instanceof BaseReaderFragment) {
					((BaseReaderFragment) f).onRightClick();
				}
				if (f instanceof BaseReaderGroupFragment) {
					List<Fragment> list = ((BaseReaderGroupFragment) f).getChildFragmentManager().getFragments();
					dispatchRetryClickToChildFragment(list);
				}
			}
		}
	}

	/**
	 * 错误页按钮点击回调
	 */
	protected void onRetryClick() {
	}

	/**
	 * 错误页按钮点击回调
	 */
	protected void onLeftClick() {
	}

	/**
	 * 错误页按钮点击回调
	 */
	protected void onRightClick() {
	}

	public boolean isLogin() {
		return mAccountManager.isLogin();
	}

	protected ViewGroup getRootview() {
		return mRootView;
	}

	static class RootHandler extends Handler {
		private final WeakReference<BaseReaderActivity> mActivity;

		RootHandler(BaseReaderActivity view) {
			this.mActivity = new WeakReference<BaseReaderActivity>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			BaseReaderActivity activity = mActivity.get();
			if (activity == null)
				return;
			try {
				if (mIsHide) {
					activity.hideGifLoadingByUi(activity.mRootView);
				}
				activity.mIsLoading = false;
				if (msg == null || !(msg.obj instanceof RequestResult)) {
					return;
				}
				switch (msg.what) {
				case RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS:
					activity.onSuccess(msg);
					break;
				case RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL:
					activity.onFail(msg);
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	// 是否响应后自动处理隐藏loading图 默认是自动
	public boolean isAutoHideLoading() {
		return true;
	}

	public void onSuccess(Message msg) {
	}

	public void onFail(Message msg) {
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		try {
			return super.dispatchTouchEvent(ev);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public AccountManager getAccountManager() {
		return mAccountManager;
	}

	@Override
	protected ProgressLoadingView getProgressLoadingView(int id) {
		MyProgressLoadingView view = new MyProgressLoadingView(this);
		view.setMessage(id);

		return view;
	}

	public void switchContent(int containerId, Fragment from, Fragment to) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (!to.isAdded()) { // 先判断是否被add过
			transaction.hide(from).add(containerId, to)
					.commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
		} else {
			transaction.hide(from).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
		}
	}

	@Override
	public void sendBroadcast(Intent intent) {
		if (intent == null)
			return;
		boolean isSystem = intent.getBooleanExtra("is_system_broadcast",false);
		if (!isSystem)
			intent.setPackage(this.getPackageName());
		super.sendBroadcast(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		onStatisticsPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		onStatisticsResume();
		if (DangdangConfig.isDevelopEnv())
			ViewServer.get(this).setFocusedWindow(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (DangdangConfig.isDevelopEnv())
			ViewServer.get(this).removeWindow(this);
	}

	/**
	 * 添加友盟统计，配合fragment使用的activity需要重写此方法，仅需要调用
	 * <p>
	 * <b>UmengStatistics.onResume(this);</b>
	 * </p>
	 */
	protected void onStatisticsResume() {
//		// 添加友盟统计
//		UmengStatistics.onPageStart(getClass().getSimpleName());
//		UmengStatistics.onResume(this);
	}

	/**
	 * 添加友盟统计，配合fragment使用的activity需要重写此方法，仅需要调用
	 * <p>
	 * <b>UmengStatistics.onPause(this);</b>
	 * </p>
	 */
	protected void onStatisticsPause() {
//		// 添加友盟统计
//		UmengStatistics.onPageEnd(getClass().getSimpleName());
//		UmengStatistics.onPause(this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate();
		mTiniManager = new SystemBarTintManager(this);
		mTiniManager.setStatusBarTintEnabled(isFitSystemWindow());
		mTiniManager.setStatusBarTintResource(getSystemBarColor());
	}

	@Override
	public View findViewById(int id) {
		View v = super.findViewById(id);
		if (v != null)
			return v;
		return mHelper.findViewById(id);
	}

	@Override
	public SwipeBackLayout getSwipeBackLayout() {
		return mHelper.getSwipeBackLayout();
	}

	@Override
	public void setSwipeBackEnable(boolean enable) {
		getSwipeBackLayout().setEnableGesture(enable);
	}

	@Override
	public void scrollToFinishActivity() {
		getSwipeBackLayout().scrollToFinishActivity();
	}

	/**
	 * 是否支持滑动边缘返回
	 * 
	 * @return
	 */
	public boolean isSwipeBack() {
		return true;
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		try {
			super.startActivityForResult(intent, requestCode);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startActivity(Intent intent) {
		try {
			super.startActivity(intent);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
