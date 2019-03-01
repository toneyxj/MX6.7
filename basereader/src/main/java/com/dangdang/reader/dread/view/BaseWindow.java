package com.dangdang.reader.dread.view;

import com.dangdang.zframework.log.LogM;

/**
 * @author luxu
 */
public abstract class BaseWindow {

	protected void printLog(String msg) {
		LogM.i(getClass().getSimpleName(), msg);
	}
	
	protected void printLogE(String msg) {
		LogM.e(getClass().getSimpleName(), msg);
	}

}
