package com.dangdang.reader.dread.function;

import com.dangdang.reader.dread.core.base.BaseGlobalApplication;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;

/**
 * Function模型
 * 封装阅读中细小功能，功能细化，重用
 * 比如：上一页、下一页、跳转等
 * @author luxu
 */
public abstract class MFunctionImpl extends BaseFunction {

	
	protected BaseReaderApplicaion mApplication;

	public MFunctionImpl(BaseReaderApplicaion app) {
		this.mApplication = app;
	}
	
	public BaseReaderApplicaion getReaderApp(){
		return mApplication;
	}
	
}
