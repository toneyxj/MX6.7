package com.dangdang.reader.dread.format.part;

import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Chapter;

import java.util.List;

/**
 * 分章阅读目录相关处理，获取更新返回等等
 * Created by Yhyu on 2015/5/21.
 */
public interface IPartDirHandle {
    int PART_TYPE_HTML=0;
    int PART_TYPE_ZIP=1;
    String PART_CHAPTER_EXT = ".html";//原创小说
    String PART_CHAPTER_EXT_ZIP = ".zip";//漫画
    String PART_CHAPTER_FILE = "chapters.obj";

    boolean checkDirExist(String pid);

    void getChapterList(String pId, int version, int lastIndexOrder, IParseDirListener listener);
    void getChapterList(String pId, int version, int lastIndexOrder, int partType,IParseDirListener listener);

    interface IParseDirListener {
        void onGetSuccess(int version, int total, List<Book.BaseNavPoint> volumes, List<Chapter> chapters, List<Book.BaseNavPoint> partNavPoints);

        void onGetFailed(int errorCode, String msg);
    }
}
