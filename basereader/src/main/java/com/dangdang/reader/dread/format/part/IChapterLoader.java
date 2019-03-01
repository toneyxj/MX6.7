package com.dangdang.reader.dread.format.part;

import android.os.Handler;


/**
 * Created by liuboyu on 2014/11/21.
 */
public interface IChapterLoader {

	 void getChapterList(String pId, int start, int count, Handler handler, int pickStart, int pickCount);

	 void downloadChapter(String pId, String chapterId, Handler handler);

	 void buyChapter(String pId, String chapterId, Handler handler);
}
