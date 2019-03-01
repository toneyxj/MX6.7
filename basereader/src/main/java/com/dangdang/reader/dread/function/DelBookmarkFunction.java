package com.dangdang.reader.dread.function;

import com.dangdang.reader.cloud.MarkNoteManager;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.core.base.IEpubReaderController;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.IndexRange;

public class DelBookmarkFunction extends MFunctionImpl {

	public DelBookmarkFunction(BaseReaderApplicaion mA) {
		super(mA);
	}

	@Override
	protected void runFunction(Object... params) {

		try {
			ReaderAppImpl readerApps = (ReaderAppImpl) getReaderApp();
			IEpubReaderController controller = (IEpubReaderController) readerApps.getReaderController();

			ReadInfo readInfo = (ReadInfo) readerApps.getReadInfo();
			final String pId = readInfo.getDefaultPid();
			final int isBought = readInfo.isBoughtToInt();

			final int chapterIndex = readInfo.getChapterIndex();
			IndexRange range = controller.getCurrentPageRange();
			final int startIndex = range.getStartIndexToInt();
			final int endIndex = range.getEndIndexToInt();
			final long operateTime = System.currentTimeMillis();
			final String modVersion = readInfo.getEpubModVersion();

			MarkNoteManager markNoteManager = readerApps.getMarkNoteManager();
			markNoteManager.deleteBookMark(pId, modVersion, isBought, chapterIndex, startIndex, endIndex, operateTime);

			//readerApps.getReaderWidget().reset();
			//readerApps.getReaderWidget().repaint();
			readerApps.getReaderWidget().repaintSync(false, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
