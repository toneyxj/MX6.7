package com.dangdang.reader.base;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.PopupWindow;

import com.dangdang.reader.R;
import com.dangdang.reader.utils.Utils;
import com.dangdang.reader.view.MyPopupWindow;
import com.dangdang.zframework.utils.UiUtil;
import com.dangdang.zframework.view.DDWebView;

public class WebBrowserActivity extends BaseStatisActivity {

	public static final String KEY_URL = "url";
	public static final String KEY_FULLSCREEN = "fullscreen";
	private String mUrl;
	private boolean mFullscreen;
	private DDWebView mWebView;
	private PopupWindow mPop;

	@Override
	protected void onCreateImpl(Bundle savedInstanceState) {
		getIntentData();
//		if (mFullscreen)
//			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_web_browser);
		initView();
		loadWebURL();
	}

    @Override
    public boolean isTransparentSystemBar() {
        return false;
    }

    private void loadWebURL() {
		mWebView.loadUrl(mUrl);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		mWebView = (DDWebView) findViewById(R.id.web_browser_webview);
		Button close = (Button) findViewById(R.id.web_browser_btn_close);
		Button refresh = (Button) findViewById(R.id.web_browser_btn_refresh);
		Button forward = (Button) findViewById(R.id.web_browser_btn_forward);
		Button back = (Button) findViewById(R.id.web_browser_btn_back);
		Button more = (Button) findViewById(R.id.web_browser_btn_more);
		close.setOnClickListener(mListener);
		refresh.setOnClickListener(mListener);
		forward.setOnClickListener(mListener);
		back.setOnClickListener(mListener);
		more.setOnClickListener(mListener);
		
		WebSettings settings = mWebView.getSettings();
		try {
			settings.setJavaScriptEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private View.OnClickListener mListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int i = v.getId();
			if (i == R.id.web_browser_btn_close) {
				finish();

			} else if (i == R.id.web_browser_btn_refresh) {
				mWebView.reload();

			} else if (i == R.id.web_browser_btn_forward) {
				mWebView.goForward();

			} else if (i == R.id.web_browser_btn_back) {
				mWebView.goBack();

			} else if (i == R.id.web_browser_btn_more) {
				showOrHideMenu(v);

			} else if (i == R.id.external) {
				showOrHideMenu(v);
				openExternal();

			} else if (i == R.id.copy) {
				showOrHideMenu(v);
				copyURL();

			} else {
			}
		}
	};

	private void copyURL() {
		ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		manager.setText(mWebView.getUrl());
		showToast(R.string.web_browser_menu_copy_url_toast);
	}

	private void openExternal() {
		try{
			String url = mWebView.getUrl();
			if(TextUtils.isEmpty(mWebView.getUrl())){
				url = "";
			}
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}catch(ActivityNotFoundException e){
            UiUtil.showToast(mContext, R.string.no_web_browser);
		} catch(Exception e2){
			e2.printStackTrace();
		}
	}

	private void showOrHideMenu(View aim) {
		if (mPop == null) {
			View view = LayoutInflater.from(this).inflate(R.layout.web_browser_pop_menu, null);
			view.findViewById(R.id.external).setOnClickListener(mListener);
			view.findViewById(R.id.copy).setOnClickListener(mListener);

			view.setFocusableInTouchMode(true);
			mPop = new MyPopupWindow(view, Utils.dip2px(this, 180), ViewGroup.LayoutParams.WRAP_CONTENT);
			mPop.setTouchable(true);
			mPop.setFocusable(true);
			mPop.setBackgroundDrawable(new BitmapDrawable());
			mPop.setOutsideTouchable(true);
		}
		if (mPop.isShowing())
			mPop.dismiss();
		else {
			mPop.showAsDropDown(aim, 0, Utils.dip2px(this, 13));
		}
	}

	private void getIntentData() {
		Intent intent = getIntent();
		mUrl = intent.getStringExtra(KEY_URL);
		mFullscreen = intent.getBooleanExtra(KEY_FULLSCREEN, false);
	}

	@Override
	protected void onDestroyImpl() {
		try {
			mWebView.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
