package com.dangdang.reader.dread.function;

import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.core.base.GoToParams;
import com.dangdang.reader.dread.core.base.IEpubReaderController;
import com.dangdang.reader.dread.core.base.IEpubReaderController.GoToType;
import com.dangdang.reader.dread.core.base.IReaderController.DDirection;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Chapter;

public class ToReadEndFunction extends MFunctionImpl {

	public ToReadEndFunction(BaseReaderApplicaion app) {
		super(app);
	}

	@Override
	protected void runFunction(Object... params) {
		
		GoToParams toParams = new GoToParams();
		Chapter lastChapter = getBook().getLastChapter();
		toParams.setChapter(lastChapter);
		toParams.setType(GoToType.LastPage);
		
		getReaderApp().getReaderWidget().startManualScrolling(0, 0, DDirection.LeftToRight);
		IEpubReaderController epubController = getController();
		epubController.gotoPage(toParams);
		
	}

	private IEpubReaderController getController() {
		return (IEpubReaderController) getReaderApp().getReaderController();
	}
	
	private Book getBook(){
		return (Book) getReaderApp().getBook();
	}

}
