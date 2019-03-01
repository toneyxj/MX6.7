package com.dangdang.reader.dread;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dangdang.reader.R;
import com.dangdang.reader.base.BaseReaderActivity;
import com.dangdang.reader.dread.core.base.BaseGlobalApplication;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.DDFile;
import com.dangdang.reader.dread.view.ReaderScrollView.OnScrollStatusListener;
import com.dangdang.reader.dread.view.ReaderScrollView.ScrollEvent;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.view.DDTextView;

public abstract class BaseReadActivity extends BaseReaderActivity implements
		OnScrollStatusListener {
	public ReadMainActivity getReadMain() {

		ReadMainActivity mainAct = (ReadMainActivity) getParent();

		return mainAct;
	}

    @Override
    public boolean isTransparentSystemBar() {
        return false;
    }

    public boolean isCurrentRead() {
		return getReadMain().isCurrentRead();
	}

	@SuppressWarnings("rawtypes")
	public void snapToScreen(Class clazz) {

		getReadMain().snapToScreen(clazz);
	}

	@SuppressWarnings("rawtypes")
	public void snapToReadScreen() {
		getReadMain().snapToReadScreen();
	}

	public void initFullScreenStatus(boolean isFullScreen) {
		getReadMain().initFullScreenStatus(isFullScreen);
	}

	/**
	 * return: true: 自己处理touch事件 false: 交给父级处理
	 */
	@Override
	public boolean isSelfProcessTouch() {
		return false;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void shareBook() {
		getReadMain().shareBook();
	}

	public void exportBookNote(String strContent) {
		getReadMain().exportBookNote(strContent);
	}

	@Override
	public void onScrollStart(ScrollEvent e) {

	}

	@Override
	public void onScrollComplete(ScrollEvent e) {

	}

	public DDFile getDDFile() {
		return getReadMain().getDDFile();
	}

	public boolean isPdf() {
		return getReadMain().isPdf();
	}
	public boolean isPart() {
		return getReadMain().isPart();
	}

	public boolean isComics(){
		return getReadMain().isComicsNoOpen();
	}
	public boolean isPartComics(){
		return getReadMain().isPartComics();
	}

	public boolean isPdfReflow() {
		return getReadMain().isPdfReflow();
	}

	public boolean isPdfAndNotReflow() {
		return getReadMain().isPdfAndNotReflow();
	}

	public boolean isSwitchPdf() {
		return getReadMain().isSwitchPdf();
	}

	public BaseGlobalApplication getGlobalApp() {
		return getReadMain().getGlobalApp();
	}

	public Book getBook() {
		return getReadMain().getBook();
	}

	public BaseReadInfo getReadInfo() {
		return getReadMain().getReadInfo();
	}

	public boolean isDangEpub() {
		return getReadMain().isDangEpub();
	}

	protected void hideInputMgr(View view) {
		if (view == null) {
			printLogE(" hideInputMgr view == null ");
			return;
		}
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	/**
	 * 友盟统计：计数
	 * 
	 * @param eventId
	 */
	protected void umStatis(String eventId) {
//		UmengStatistics.onEvent(getApplicationContext(), eventId);
	}

	public void printLogE(String log) {
		LogM.e(getClass().getSimpleName(), log);
	}

	public void replaceFragmentAnim(Fragment f, int containerId,
			String stackName, boolean isRightIn, boolean isCover) {
		if(f.isAdded()){
			return;
		}
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//		if (isRightIn) {
//			if (isCover)
//				ft.setCustomAnimations(R.anim.book_review_activity_in,
//						R.anim.book_review_group_activity_out,
//						R.anim.book_review_group_activity_in,
//						R.anim.book_review_activity_out);
//			else
//				ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_in, R.anim.fade_in, R.anim.book_review_activity_out);
//			/*	ft.setCustomAnimations(R.anim.reader_base_push_right_in,
//						R.anim.reader_base_push_left_out, R.anim.reader_base_push_left_in,
//						R.anim.reader_base_push_right_out);*/
//		} else {
//			ft.setCustomAnimations(R.anim.reader_base_push_left_in, R.anim.reader_base_push_right_out,
//					R.anim.reader_base_push_right_in, R.anim.reader_base_push_left_out);
//		}
		if (isCover)
			ft.add(containerId, f);
		else
			ft.replace(containerId, f);
		if (stackName != null)
			ft.addToBackStack(stackName);
		ft.commitAllowingStateLoss();
	}
	
	protected void processTokenBad(){
		
	}


	private Toast mToast;
	private DDTextView mTextView;
	@Override
	public void showToast(int resid) {
		showToast(mContext.getString(resid));
	}
	@Override
	public void showToast(String msg) {
		if(mToast == null||mTextView==null){
			mToast =  new Toast(mContext);
			mTextView = new DDTextView(mContext);
			mTextView.setGravity(17);
			mTextView.setTextColor(mContext.getResources().getColor(R.color.white));
			try {
				mTextView.setBackgroundResource(R.drawable.toast_frame);
			} catch (Throwable var6) {
				var6.printStackTrace();
			}

			mToast.setView(mTextView);
			mTextView.setText(msg);
		} else {
			mToast.setDuration(Toast.LENGTH_SHORT);
			mTextView.setText(msg);
		}
		mToast.show();
	}
	protected void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
}
