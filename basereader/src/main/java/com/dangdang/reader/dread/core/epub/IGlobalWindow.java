package com.dangdang.reader.dread.core.epub;


public interface IGlobalWindow {

	public boolean isShowingWindow();
	
	public void hideWindow(boolean bHideFloatWindow);
	
	public void setOnDismissCallBack(IOnDisMissCallBack l);
	
	
	public static interface IOnDisMissCallBack {
		public void onDismissCallBack();
	}
	
}
