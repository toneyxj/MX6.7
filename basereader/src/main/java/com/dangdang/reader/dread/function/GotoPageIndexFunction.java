package com.dangdang.reader.dread.function;

import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;

public class GotoPageIndexFunction extends MFunctionImpl {

	public GotoPageIndexFunction(BaseReaderApplicaion app) {
		super(app);
	}

	@Override
	protected void runFunction(Object... params) {

		if(params != null && params.length > 1){
			try {
				int pageIndexInBook = (Integer) params[0];
				getReaderApp().getReaderController().gotoPage(pageIndexInBook);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	}

}
