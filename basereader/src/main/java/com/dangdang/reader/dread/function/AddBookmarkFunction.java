package com.dangdang.reader.dread.function;

import com.dangdang.reader.cloud.MarkNoteManager;
import com.dangdang.reader.cloud.MarkNoteManager.OperateType;
import com.dangdang.reader.cloud.Status;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.core.base.IEpubReaderController;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.BookMark;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Chapter;

public class AddBookmarkFunction extends MFunctionImpl {

	public AddBookmarkFunction(BaseReaderApplicaion mA) {
		super(mA);
	}

	@Override
	protected void runFunction(Object... params) {

		try {
			ReaderAppImpl readerApps = (ReaderAppImpl) getReaderApp();
			IEpubReaderController controller = (IEpubReaderController) readerApps.getReaderController();
			Chapter markChapter = controller.getCurrentChapter();

			BaseNavPoint nPoint = ((Book) readerApps.getBook()).getNavPoint(markChapter);
			final String chapterName = nPoint != null ? nPoint.lableText : "";

			ReadInfo readInfo = (ReadInfo) readerApps.getReadInfo();
			BookMark mark = new BookMark();
			mark.pId = readInfo.getDefaultPid();
			mark.isBought = readInfo.isBoughtToInt();
			mark.bookPath = readInfo.getBookDir();
			mark.chapterIndex = readInfo.getChapterIndex();

			mark.elementIndex = readInfo.getElementIndex();
			mark.chapterName = chapterName;
			mark.markTime = System.currentTimeMillis();
			mark.markText = controller.getPageText();
			mark.status = String.valueOf(Status.COLUMN_NEW);
			mark.cloudStatus = String.valueOf(Status.CLOUD_NO);
			mark.bookModVersion = readInfo.getEpubModVersion();
			// mark.modifyTime = String.valueOf(nowTime);

			MarkNoteManager markNoteManager = readerApps.getMarkNoteManager();
			markNoteManager.operationBookMark(mark, OperateType.NEW);

			//readerApps.getReaderWidget().reset();
			//readerApps.getReaderWidget().repaint();
			readerApps.getReaderWidget().repaintSync(false, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
