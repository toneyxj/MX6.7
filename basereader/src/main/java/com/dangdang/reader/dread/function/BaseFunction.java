package com.dangdang.reader.dread.function;

import com.dangdang.zframework.log.LogM;

/**
 * Function模型
 * 封装阅读中细小功能，功能细化，重用
 * 比如：上一页、下一页、跳转等
 * @author luxu
 */
public abstract class BaseFunction {

	
	public boolean isEnable(){
		return true;
	}
	
	public final boolean doFunction(Object... params){
		
		boolean ret = isEnable();
		if(ret){
			runFunction(params);
		}
		
		return ret;
	}
	
	protected void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
	protected abstract void runFunction(Object... params);
	
	
}
