package com.dangdang.reader.dread.function;

import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.core.base.GoToParams;
import com.dangdang.reader.dread.core.base.IEpubReaderController;
import com.dangdang.reader.dread.core.base.IReaderController.DDirection;

public class GotoPageByChapterFunction extends MFunctionImpl {

	public GotoPageByChapterFunction(BaseReaderApplicaion app) {
		super(app);
	}

	@Override
	protected void runFunction(Object... params) {


		if(params != null && params.length >= 1){

			GoToParams toParams = (GoToParams) params[0];
			getReaderApp().getReaderWidget().startManualScrolling(0, 0, DDirection.LeftToRight);
			IEpubReaderController epubController = getController();
			epubController.gotoPage(toParams);

			/*final Chapter chapter = toParams.getChapter();
			final int elementIndexInHtml = Integer.valueOf(params[1].toString());
			final int sourceType = Integer.valueOf(params[2].toString());
			final Object param = params[3];
			LogM.i(getClass().getSimpleName(), " GoToPageNew elementIndex = " + elementIndexInHtml);
			if(elementIndexInHtml >= 0){
				IEpubReaderController epubController = getController();
				epubController.gotoPage(chapter, elementIndexInHtml);
				epubController.gotoPage(toParams);
			} else {
				LogM.e(getClass().getSimpleName(), " GoToPageNew eIndex = " + elementIndexInHtml);
			}*/
		}
	
	}

	private IEpubReaderController getController() {
		return (IEpubReaderController) getReaderApp().getReaderController();
	}

}
