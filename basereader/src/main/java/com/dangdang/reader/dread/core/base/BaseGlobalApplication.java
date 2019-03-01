package com.dangdang.reader.dread.core.base;

import com.dangdang.reader.dread.core.base.IFunctionManager.FunctionManager;

public abstract class BaseGlobalApplication extends FunctionManager implements IReaderApplication {

	
	public abstract boolean isCanExit();
	
	public abstract void requestAbort(IAbortParserListener l);

	public abstract boolean isBookComposingDone();
}
