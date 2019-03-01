package com.dangdang.reader.dread.view;

/**
* Created by liuboyu on 2015/1/31.
*/
public interface IAddOrDeleteBookMarkCallBack {
	public void addOrDelete();

	public void setMarkVisiable(boolean isVisiable);

	public void resetScrollState();
}
