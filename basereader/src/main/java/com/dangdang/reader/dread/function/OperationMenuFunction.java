package com.dangdang.reader.dread.function;

import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion.IReaderEventListener;

public class OperationMenuFunction extends MFunctionImpl {

	public OperationMenuFunction(BaseReaderApplicaion app) {
		super(app);
	}

	@Override
	protected void runFunction(Object... params) {
		IReaderEventListener readEventL = getReaderApp().getReaderEventListener();
		if(readEventL != null){
			readEventL.onMenuEvent();
		} 
		
	}

	

}
