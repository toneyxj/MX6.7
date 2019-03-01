package com.dangdang.reader.dread.function;

import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.core.base.IReaderWidget;
import com.dangdang.zframework.utils.DRUiUtility;

public class NextPageFunction extends MFunctionImpl {

	public NextPageFunction(BaseReaderApplicaion app) {
		super(app);
	}

	@Override
	protected void runFunction(Object... params) {
		try {
			int x = (DRUiUtility.getScreenWith()*5) / 6;
			int y = 0;
			boolean sourceAnim = false;
			if(params.length >= 2){
				x = (Integer) params[0];
				y = (Integer) params[1];
				try {
					sourceAnim = (Boolean)params[2];
				} catch (Exception e) {
				}
			}
			IReaderWidget widget = getReaderApp().getReaderWidget();
			widget.startAnimatedScrolling(x, y, 1, sourceAnim);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
