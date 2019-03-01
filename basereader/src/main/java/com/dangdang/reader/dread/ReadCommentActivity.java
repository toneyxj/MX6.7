package com.dangdang.reader.dread;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.dangdang.reader.Constants;
import com.dangdang.reader.R;
import com.dangdang.reader.base.BaseStatisActivity;
import com.dangdang.reader.db.service.DDStatisticsService;
import com.dangdang.reader.utils.AccountManager;
import com.dangdang.reader.utils.SystemLib;
import com.dangdang.reader.view.StarRate;
import com.dangdang.zframework.view.DDCheckBox;
import com.dangdang.zframework.view.DDEditText;
import com.dangdang.zframework.view.DDTextView;

import java.lang.ref.WeakReference;
import java.net.URLEncoder;

public class ReadCommentActivity extends BaseStatisActivity {
	
	//private final  DangDang_Method AddBookComment = DangDang_Method.AddBookComment;
	
	public static final String PRODUCT_LINK_PREFIX = "http://product.dangdang.com/product.aspx?product_id=";

	public static final int MAX_NUM = 300;
	
	public static final int URL_LENGTH = 25;
	public static final int MAX_CONTENT_LENGTH = 140;
	
	private final static int MSG_WHAT_ADD_SUCCESS =0;
	private final static int MSG_WHAT_ADD_FALIED = 0x01;
	private final static int MSG_WHAT_NET_FULL= 0x02;
	
	protected String mBookDir;
	protected String mBookName;
	protected String mBookId;
	protected String mDefaultStr;
	
	private boolean isFirstEntry = true;
	private View mTip1;
	private View mTip2;
	private View mTip3;
	private View mTip4;
	
	protected boolean mChildActivity = false;
	
	protected DDEditText mTitleEdit;
	protected DDEditText mInputText;
	protected DDTextView mLastNum;
	protected DDTextView mSubmitView;
	protected DDTextView mReadNoteContent;
	protected DDCheckBox mCheckBooknoteShare;
	protected View mBackView;
	protected StarRate mStarRate;
	
	private DDStatisticsService mDDService;
	private Handler handler;
	
	//书架搜索文本框 响应键盘输入事件
	private TextWatcher mContentWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void afterTextChanged(Editable s) {
			initLastNumTip(MAX_NUM - mInputText.getText().toString().length());
			/*try {
				byte[] bytes = s.toString().getBytes("GBK"); // 用默认的utf-8转换，一个汉字占三个字节
				if (bytes.length > 16) {
					s.delete(s.length() - 1, s.length());
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}*/
		}
	};
	
	protected void initLastNumTip(int num) {
		mLastNum.setText(String.format(getResources().getString(R.string.read_comment_last_num), num));
	}
	
	private OnClickListener mListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int i = v.getId();
			if (i == R.id.read_comment_tip1) {
				initCommentContent(getResources().getString(R.string.read_comment_tip1));

			} else if (i == R.id.read_comment_tip2) {
				initCommentContent(getResources().getString(R.string.read_comment_tip2));

			} else if (i == R.id.read_comment_tip3) {
				initCommentContent(getResources().getString(R.string.read_comment_tip3));

			} else if (i == R.id.read_comment_tip4) {
				initCommentContent(getResources().getString(R.string.read_comment_tip4));

			} else if (i == R.id.common_menu_tv) {
				submitEvent();

			} else if (i == R.id.common_back) {
				destroy();

			} else {
			}
		}
	};
	
	private void dealMsg(Message msg){
		switch (msg.what) {
		case MSG_WHAT_ADD_SUCCESS:  
			SystemLib.showTip(getApplicationContext(), R.string.string_comment_send_success);
			finish();
			break;
		case MSG_WHAT_ADD_FALIED:
			SystemLib.showTip(getApplicationContext(), msg.obj.toString());
			break;
		case MSG_WHAT_NET_FULL:
			SystemLib.showTip(getApplicationContext(), R.string.time_out_tip);
			break;
		default:
			break;
		}
	}
	
	private static class MyHandler extends Handler {
		private final WeakReference<ReadCommentActivity> mFragmentView;

		MyHandler(ReadCommentActivity view) {
			this.mFragmentView = new WeakReference<ReadCommentActivity>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			ReadCommentActivity service = mFragmentView.get();
			if (service != null) {
				super.handleMessage(msg);
				try {
					service.dealMsg(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void initCommentContent(String content) {
		mTitleEdit.setText(content);
		mTitleEdit.setSelection(content.length());
	}
	
	private void submitEvent() {
		
		AccountManager amager = new AccountManager(this);
    	if(!amager.checkTokenValid() || TextUtils.isEmpty(amager.getToken())) {
			gotoLogin();
	    	return;
    	}
		
		String title = mTitleEdit.getText().toString();
		String content = mInputText.getText().toString();
		if(title.trim().length() == 0) {
			SystemLib.showTip(getApplicationContext(), R.string.string_comment_title_tip_null);
			return;
		}
		
		if(content.trim().length() == 0) {
			SystemLib.showTip(getApplicationContext(), R.string.string_comment_content_tip_null);
			return;
		}
		/*showLoading() ;*/
		
		int score = mStarRate.getStar();
		content = URLEncoder.encode(content);
		//sendCommand(AddBookComment, URLEncoder.encode(getSubString(mBookId, "_", true)), mBookName, content, score);
		//handleOptionAddBookComment(URLEncoder.encode(getSubString(mBookId, "_", true)),title, content, score);
	}
	
	
	@Override
	protected void onCreateImpl(Bundle savedInstanceState) {
		setContentView(R.layout.read_comment_input_layout);
		handler = new MyHandler(this);
		mDDService = DDStatisticsService.getDDStatisticsService(this);
		
		mStarRate = (StarRate) findViewById(R.id.read_comment_stars);
		mStarRate.setStarFocusable(true);
		mStarRate.setStar(5);
		
		mTip1 = findViewById(R.id.read_comment_tip1);
		mTip1.setOnClickListener(mListener);
		
		mTip2 = findViewById(R.id.read_comment_tip2);
		mTip2.setOnClickListener(mListener);
		
		mTip3 = findViewById(R.id.read_comment_tip3);
		mTip3.setOnClickListener(mListener);
		
		mTip4 = findViewById(R.id.read_comment_tip4);
		mTip4.setOnClickListener(mListener);
		
		mTitleEdit = (DDEditText) findViewById(R.id.read_comment_title_edit);
		mInputText = (DDEditText) findViewById(R.id.read_comment_input_edit);
		mInputText.addTextChangedListener(mContentWatcher);
		
		mLastNum = (DDTextView) findViewById(R.id.read_comment_last_num);
		
		mSubmitView = (DDTextView) findViewById(R.id.common_menu_tv);
		mSubmitView.setOnClickListener(mListener);
		mReadNoteContent = (DDTextView) findViewById(R.id.read_note_content);
		mReadNoteContent.setVisibility(View.GONE);
		mBackView = findViewById(R.id.common_back);
        ((TextView) findViewById(R.id.common_title)).setText("评论");
		mBackView.setOnClickListener(mListener);
		
		mBookDir = getIntent().getStringExtra(Constants.BOOK_DIR);
		mBookName = getIntent().getStringExtra(Constants.BOOK_NAME);
		mDefaultStr = String.format(getResources().getString(R.string.string_comment_default_content), mBookName);

        //updateModeSetToolbarScreenLight();
	}
	
	/*protected void updateModeSetToolbarScreenLight() {
		float light = ReadConfig.getConfig().getLight();
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = light;
		getWindow().setAttributes(lp);
	}*/


	String getProductLink() {
		String link = PRODUCT_LINK_PREFIX + getSubString(mBookId, "_", true);
		return "\r\n" + getString(R.string.string_comment_from) + "" + link;
	}

	/**
	 * 
	 * @param ori
	 * @param filter
	 * @param bo true 获取当前 filter的后面一段,false,获取前面一段
	 * @return
	 */
	public String getSubString(String ori,String filter,boolean bo){
		try {
			if(TextUtils.isEmpty(ori)){
				return "";
			}
			int index = ori.lastIndexOf(filter);
			if(index == -1){
				return ori;
			}
			if(bo){
				return ori.substring(index + 1);
			}
			return ori.substring(0, index);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	

	protected void sendMsg1Toast(String txt){
		Message msg = handler.obtainMessage(MSG_WHAT_ADD_FALIED);
		msg.obj = txt;
		handler.sendMessage(msg);
	}
	
	/*@Override
	public void onCommandResult(CommandResult result) {
		super.onCommandResult(result);
		DangDang_Method action = result.getCommand().getAction();
		if (result.getResultType() == ResultType.Success) {
			printLog(" onCommandResult=" + result.getResult());
			ResultExpCode resultCode = result.getResultCode();
			if (action == AddBookComment) {
				if (resultCode.getResultStatus())
					handler.sendEmptyMessage(MSG_WHAT_ADD_SUCCESS);
				else {	
					String error = resultCode.getResultErrorMessage();
					if(error == null || "".equals(error)) {
						error = getString(R.string.string_comment_send_fial);
					}
					sendMsg1Toast(error);
				}
			}
		} else {
			if(action == AddBookComment) {
				handler.sendEmptyMessage(MSG_WHAT_NET_FULL);
			}
		}
	}*/
	
	/*@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent != null) {
			String productID = intent.getStringExtra(BookColumn.BOOK_ID);
			if (productID != null && !"-1".equals(productID)) {
				getIntent().putExtra(BookColumn.BOOK_ID, productID);
			}
		}
	}*/

	@Override
	protected void onResume() {
		super.onResume();
//		UmengStatistics.onPageStart(getClass().getSimpleName());
		if (mChildActivity) return;
		/*AccountManager amager = new AccountManager(this);
    	if(!amager.checkTokenValid() || amager.getToken() == null) {
    		if(isFirstEntry){
				isFirstEntry = false;
	    		Intent intent = new Intent(this,RegisterAndLoginActivity.class);
	    		startActivity(intent);	
			}else{
				finish();
			}
    		return;
    	}*/
		if (getIntent() != null) {
			String productID =getIntent().getStringExtra(Constants.BOOK_ID);
			if (productID != null && !"-1".equals(productID)) {
				mBookId = productID;
				getIntent().putExtra(Constants.BOOK_ID, "-1");
			}
		}
	}
	
	protected void onPause() {
//		UmengStatistics.onPageEnd(getClass().getSimpleName());
		super.onPause();
	}
    
	protected void destroy() {
		hideSoftKeyBoard();
		finish();
	}

	// 隐藏输入法
	public void hideSoftKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isopen = imm.isActive();
		if (isopen && this.getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			destroy();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	protected void onDestroyImpl() {
		try{
			mInputText.removeTextChangedListener(mContentWatcher);
		}catch(Exception e){
			e.printStackTrace();
		}		
		if (mChildActivity) return;
	}
}
