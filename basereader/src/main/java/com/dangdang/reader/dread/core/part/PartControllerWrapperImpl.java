package com.dangdang.reader.dread.core.part;

import com.dangdang.reader.cloud.CloudSyncConfig;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.core.base.IReaderController;
import com.dangdang.reader.dread.core.epub.ControllerWrapperImpl;
import com.dangdang.reader.dread.data.PartReadInfo;
import com.dangdang.reader.dread.format.BaseBookManager;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.part.IPartChapterHandle;
import com.dangdang.reader.dread.format.part.PartBuyInfo;
import com.dangdang.reader.dread.format.part.PartChapter;
import com.dangdang.reader.dread.format.part.PartChapterHandleImpl;
import com.dangdang.zframework.task.BaseTask;
import com.dangdang.zframework.task.TaskManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 原创 绘制及排版
 * @author luxu
 */
public class PartControllerWrapperImpl extends ControllerWrapperImpl {
    private IPartChapterHandle partChapterHandle;
    private LoadTaskManager mLoadTaskManager;
    private CloudSyncConfig mCloudSyncConfig;
    private HashMap<Integer, ArrayList<ILoadChapterListener>> mLoadChapters = new HashMap<Integer, ArrayList<ILoadChapterListener>>();

    public PartControllerWrapperImpl(BaseReaderApplicaion readApp) {
        super(readApp);
        mLoadTaskManager = new LoadTaskManager();
        partChapterHandle = new PartChapterHandleImpl(getReadInfo().getDefaultPid());
        mCloudSyncConfig = new CloudSyncConfig(readApp.getContext());
    }

    public int getChapterPageCount(final Chapter chapter) {
        int pageCount = mBookManager.getChapterPageCount(chapter, false);
        return pageCount;
    }

    public Chapter getPrevOrNextChapter(IReaderController.DPageIndex pageIndex, Chapter currentChapter) {

        Chapter chapter = currentChapter;
        List<Chapter> chapters = getBook().getChapterList();
        if (chapters == null) {
            return null;
        }
        int htmlInChapters = chapters.indexOf(chapter);
        int tmpHtmlIndex = -1;
        if (pageIndex == IReaderController.DPageIndex.Previous) {
            tmpHtmlIndex = htmlInChapters - 1;
        } else if (pageIndex == IReaderController.DPageIndex.Next) {
            tmpHtmlIndex = htmlInChapters + 1;
        } else if (pageIndex == IReaderController.DPageIndex.Current) {
            tmpHtmlIndex = htmlInChapters;
        }
        printLog(" getPrevOrNextHtml tmpHtmlIndex = " + tmpHtmlIndex + ", chaptersize = " + chapters.size());
        if (tmpHtmlIndex >= 0 && tmpHtmlIndex < chapters.size()) {
            return chapters.get(tmpHtmlIndex);
        }
        return null;
    }

    @Override
    protected void asynDrawPage(final BaseBookManager.DrawPageAsycCommand command) {
        final PartChapter chapter = (PartChapter) command.getChapter();

        if (partChapterHandle.checkChapterExist(chapter)) {
            mBookManager.drawPage(command);
        } else {
            partChapterHandle.downloadChapter(chapter.getId() + "", chapter.getPath(), false, new IPartChapterHandle.IDownLoadChapterListener() {
                @Override
                public void onDownloadChapter(int code, String chapterId, PartBuyInfo buyInfo) {
                    chapter.setCode(code);
                    chapter.setPartBuyInfo(buyInfo);
                    mBookManager.drawPage(command);
                }
            });
        }

    }

    @Override
    protected void asynGotoPage(final BaseBookManager.GotoPageCommand command) {
        final PartChapter chapter = (PartChapter) command.getChapter();
        if (partChapterHandle.checkChapterExist(chapter)) {
            mBookManager.asynGoto(command);
        } else {
            partChapterHandle.downloadChapter(chapter.getId() + "", chapter.getPath(), false, new IPartChapterHandle.IDownLoadChapterListener() {
                @Override
                public void onDownloadChapter(int code, String chapterId, PartBuyInfo buyInfo) {
                    chapter.setCode(code);
                    chapter.setPartBuyInfo(buyInfo);
                    mBookManager.asynGoto(command);
                }
            });
        }
    }

    /**
     * 排版一个章节，包括自动购买，下载的逻辑
     * @param chapter
     * @param listener
     * @param needBuy
     * @param chapterIndex
     */
    protected synchronized void asynLoadChapter(Chapter chapter, final ILoadChapterListener listener, boolean needBuy, IReaderController.DChapterIndex chapterIndex) {
        final PartChapter partChapter = (PartChapter) chapter;
        if (mLoadChapters.containsKey(partChapter.getId())) {
            ArrayList<ILoadChapterListener> listeners = mLoadChapters.get(partChapter.getId());
            listeners.add(listener);
        } else {
            ArrayList<ILoadChapterListener> listeners = new ArrayList<ILoadChapterListener>();
            listeners.add(listener);
            mLoadChapters.put(partChapter.getId(), listeners);
        }
        if (partChapterHandle.checkChapterExist(chapter)) {
            LoadChapterTask task = new LoadChapterTask(partChapter);
            mLoadTaskManager.putTaskAndRun(task);
        } else {
            if (!needBuy) {
                //自动购买逻辑
                PartReadInfo partReadInfo = (PartReadInfo) mReadApp.getReadInfo();
                if (partReadInfo.isAutoBuy() && mCloudSyncConfig.getNovelPreload()) {
                    if (chapterIndex == IReaderController.DChapterIndex.Current || chapterIndex == IReaderController.DChapterIndex.Next) {
                        needBuy = true;
                    }
                }
            }
            partChapterHandle.downloadChapter(partChapter.getId() + "", chapter.getPath(), needBuy, new IPartChapterHandle.IDownLoadChapterListener() {
                @Override
                public void onDownloadChapter(int code, String chapterId, PartBuyInfo buyInfo) {
                    printLogE("onDownloadChapter code=" + code + ",chapterid = " + chapterId + ",buyInfo = " + buyInfo);
                    if (code == IPartChapterHandle.DWONLOAD_SUCCESS) {
                        LoadChapterTask task = new LoadChapterTask(partChapter);
                        mLoadTaskManager.putTaskAndRun(task);
                    } else {
                        partChapter.setCode(code);
                        partChapter.setPartBuyInfo(buyInfo);
                        List<ILoadChapterListener> listeners = mLoadChapters.remove(partChapter.getId());
                        if (listeners != null) {
                            for (ILoadChapterListener l : listeners) {
                                l.onLoadFinish(partChapter, 0);
                            }
                        }
                    }

                }
            });
        }
    }

    /**
     * 排版一个章节的task
     */
    private class LoadChapterTask extends BaseTask<Integer> {
        PartChapter chapter;

        public LoadChapterTask(PartChapter chapter) {
            this.chapter = chapter;
        }

        @Override
        public Integer processTask() throws Exception {
            int count = getChapterPageCount(chapter);
            printLog("LoadChapterTask chapter--" + chapter.getId() + "count,= " + count);
            chapter.setPageCount(count);
            return count;
        }

        @Override
        public void handleResult(Integer result) {
            if (result <= 0) {
                printLog("  getChapterPageCount count<=0,delete " + result);
                File file = new File(chapter.getPath());
                file.delete();
                chapter.setCode(IPartChapterHandle.PARSE_ERROR);
            } else {
                chapter.setCode(IPartChapterHandle.PARSE_SUCEESS);
                chapter.setPageCount(result);
            }
            printLogE("mLoadChapters = " + mLoadChapters + "..id" + chapter.getId());
            List<ILoadChapterListener> listeners = mLoadChapters.remove(chapter.getId());
            printLogE("listeners = " + listeners);
            if (listeners != null) {
                for (ILoadChapterListener l : listeners) {
                    l.onLoadFinish(chapter, result);
                }
            }
        }
    }

    /**
     * 分章排版任务管理器
     */
    public static class LoadTaskManager extends TaskManager {
        @Override
        protected int getThreadPoolSize() {
            return 1;
        }

        @Override
        protected int getPriority() {
            return Thread.NORM_PRIORITY;
        }
    }

    /**
     * 章节load回调接口，导入书为排盘回调，原创则本地网络排版回调
     *
     * @author Yhyu
     * @date 2015-1-13 上午11:07:11
     */
    public interface ILoadChapterListener {
        void onLoadFinish(PartChapter chapter, int pageCount);
    }
}
