package com.dangdang.reader.dread.function;

import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.data.ReadInfo;

public class ReComposingFunction extends MFunctionImpl {
	

	public ReComposingFunction(BaseReaderApplicaion app) {
		super(app);
	}

	@Override
	protected void runFunction(Object... params) {

		BaseReaderApplicaion readerApp = getReaderApp();
		final ReadInfo readInfo = (ReadInfo) readerApp.getReadInfo();
		
		int chapterIndex = 0;
		int elementIndex = 0;
		try {
			if(params != null && params.length > 1){
				chapterIndex = (Integer) params[0];
				elementIndex = (Integer) params[1];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(chapterIndex >= 0 && elementIndex >= 0){
			readInfo.setChapterIndex(chapterIndex,2);
			readInfo.setElementIndex(elementIndex);
		}
		readerApp.getReaderController().reset();
		//readerApp.getReaderWidget().reset();
		//readerApp.getReaderWidget().repaint();
		readerApp.reStartRead(readInfo);
	}

}
