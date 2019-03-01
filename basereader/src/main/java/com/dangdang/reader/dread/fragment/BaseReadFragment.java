package com.dangdang.reader.dread.fragment;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.dangdang.reader.base.OnScrollListener;
import com.dangdang.reader.dread.BaseReadActivity;
import com.dangdang.reader.dread.core.base.BaseGlobalApplication;
import com.dangdang.reader.R;
import com.dangdang.zframework.BaseFragment;
import com.dangdang.zframework.log.LogM;

public abstract class BaseReadFragment extends BaseFragment implements OnScrollListener{

	protected boolean mNeedReload;

	public BaseReadActivity getBaseReadActivity() {
		return (BaseReadActivity) getActivity();
	}
	
	public BaseGlobalApplication getGlobalApp() {
		return getBaseReadActivity().getGlobalApp();
	}
	
	public void snapToReadScreen() {
		getBaseReadActivity().snapToReadScreen();
	}
	
	protected boolean isPdf() {
		return getBaseReadActivity().isPdf();
	}
	
	protected boolean isPdfReflow() {
		return getBaseReadActivity().isPdfReflow();
	}

	protected boolean isPdfAndNotReflow() {
		return getBaseReadActivity().isPdfAndNotReflow();
	}

	public Dialog getBookOperationDialog() {
		Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.bookshelf_book_dialog);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER);

		return dialog;
	}

	@Override
	public void onReady() {

	}

	public void setNeedReload(boolean needReload) {
		mNeedReload = needReload;
	}

	public void reloadIfNeed() {
		if (mNeedReload)
			reload();
	}
	
	protected void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}

	public abstract void reload();

	@Override
	public void onScrollPrepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public View onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroyImpl() {
		// TODO Auto-generated method stub
		
	}
	
	
}
