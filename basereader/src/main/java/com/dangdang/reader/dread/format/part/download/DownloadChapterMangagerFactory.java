package com.dangdang.reader.dread.format.part.download;

import com.dangdang.zframework.network.download.DownloadManager;
import com.dangdang.zframework.network.download.DownloadManagerFactory;

/**
 * Created by Yhyu on 2015/6/2.
 */
public class DownloadChapterMangagerFactory extends DownloadManagerFactory {
    private static DownloadChapterMangagerFactory mFactory;

    public DownloadChapterMangagerFactory(int value) {
        super(value);
        // TODO Auto-generated constructor stub
    }

    public static synchronized DownloadChapterMangagerFactory getFactory() {
        if (mFactory == null) {
            mFactory = new DownloadChapterMangagerFactory(0);
        }
        return mFactory;
    }

    @Override
    protected DownloadManager newDownloadManager(DownloadManagerFactory.DownloadModule module) {
        return new DownloadChapterManager(module);
    }
}
