package com.dangdang.reader.dread.core.base;

import com.dangdang.zframework.log.LogM;

/**
 * @author luxu
 */
public abstract class BaseReaderController implements IEpubReaderController, IMediaInterface,IVideoInterface {

	
	protected void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
	protected void printLogD(String log){
		LogM.d(getClass().getSimpleName(), log);
	}
	
	protected void printLogE(String log){
		LogM.e(getClass().getSimpleName(), log);
	}
	
	protected void printLogV(String log){
		LogM.v(getClass().getSimpleName(), log);
	}
	
	protected void printLogW(String log){
		LogM.w(getClass().getSimpleName(), log);
	}
	
}
