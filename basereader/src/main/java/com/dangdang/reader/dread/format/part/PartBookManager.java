package com.dangdang.reader.dread.format.part;

import java.io.FileNotFoundException;
import java.util.List;

import android.content.Context;

import com.dangdang.reader.dread.config.ParserStatus;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.data.PartReadInfo;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.BaseEBookManager;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.part.IPartDirHandle.IParseDirListener;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.util.DreaderConstants;

/**
 * 原创书的数据管理
 * @author Yhyu
 */
public class PartBookManager extends BaseEBookManager {


    public PartBookManager(Context context, Book oneBook) {
        super(context, oneBook);

    }

    @Override
    protected void initReadInfo(ReadInfo readInfo) {
        this.mReadInfo = readInfo;
        this.mBookFile = readInfo.getBookFile();
        // this.mBookDir = bookDir;
        this.mBookDir = mBookFile;
        this.mOneBook.setModVersion(DreaderConstants.BOOK_MODIFY_VERSION);
    }

    public void startRead(ReadInfo readInfo) {
        super.startRead(readInfo);
        try {
            initNative();
            buildBookStruct(null, BaseJniWarp.BOOKTYPE_DD_DRM_HTML, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isInitKey(ReadInfo readInfo) {
        return readInfo.getEBookType() == BaseJniWarp.BOOKTYPE_DD_DRM_HTML;
    }

    /**
     * 初始化目录
     * @param bookFile
     * @param bookType
     * @param hasNotChapter
     * @return
     * @throws FileNotFoundException
     */
    protected Book buildBookStruct(final String bookFile, int bookType,
                                   final boolean hasNotChapter) throws FileNotFoundException {
        IPartDirHandle handle = PartDirHandleImpl.getIntance();
        PartReadInfo partReadInfo = (PartReadInfo) mReadInfo;
        handle.getChapterList(mReadInfo.getDefaultPid(), 0, partReadInfo.getIndexOrder(), mListener);
        return null;
    }

    @Override
    public void destroy(){
    	super.destroy();
    	IParseDirListener l = PartDirHandleImpl.getIntance().getListener();
    	if(l != null && l.equals(mListener))
    		PartDirHandleImpl.getIntance().resetListener();
    }

    /**
     * 目录回调
     */
    private PartDirHandleImpl.IParseDirListener mListener = new PartDirHandleImpl.IParseDirListener() {
        @Override
        public void onGetSuccess(int version, int total, List<BaseNavPoint> volumes, List<Chapter> chapters, List<BaseNavPoint> partNavPoints) {
            //TODO 知否支持全本
            if (chapters == null || chapters.size() == 0) {
                onComposingError(ParserStatus.FILE_ERROR);
            } else {
                PartReadInfo partReadInfo = (PartReadInfo) mReadInfo;
                int targetChapterId = partReadInfo.getTargetChapterId();
                if (targetChapterId != -1) {
                    for (int i = 0; i < chapters.size(); i++) {
                        PartChapter partChapter = (PartChapter) chapters.get(i);
                        if (targetChapterId==partChapter.getId()) {
                            mReadInfo.initChapterIndexAndElementIndex(i, 0);
                            break;
                        }

                    }
                }
                partReadInfo.setIndexOrder(chapters.size()-1);
                mOneBook.setChapterList(chapters);
                mOneBook.setNavPointList(partNavPoints);
//                ((PartBook) mOneBook).setVolumeList(volumes);
                onStructFinish(mOneBook);
                mDrwrap.openFile(null, BaseJniWarp.BOOKTYPE_DD_DRM_EPUB, null);
                afterOpenFile();
                final boolean isFirst = preStartLoad();
                startLoadChapters(chapters, false, isFirst);
            }
        }

        @Override
        public void onGetFailed(int errorCode, String msg) {
            onComposingError(ParserStatus.FILE_ERROR);
        }

    };


    protected void loadChapterList(List<Chapter> chapters, int chapterSize,
                                   boolean isReLoad, boolean isFirst) {

    }
}
