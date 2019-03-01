package com.dangdang.reader.dread.core.base;

import com.dangdang.reader.dread.format.BaseBookManager.DrawPageAsycCommand;
import com.dangdang.reader.dread.format.BaseBookManager.DrawPageResult;
import com.dangdang.zframework.log.LogM;

public abstract class BasePageAdapter implements IPageAdapter {

	
	
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
	
	public interface IDrawPageListener {
		
		public void onDrawPage(final DrawPageAsycCommand command, final DrawPageResult result);
		
	}
	
}
