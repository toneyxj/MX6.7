package com.dangdang.reader.dread.format.part.download;

import com.dangdang.zframework.network.download.DownloadManager;
import com.dangdang.zframework.network.download.DownloadManagerFactory;
import com.dangdang.zframework.network.download.DownloadQueue;

/**
 * Created by Yhyu on 2015/6/2.
 */
public class DownloadChapterManager extends DownloadManager {
    public DownloadChapterManager(DownloadManagerFactory.DownloadModule module) {
        super(module);
    }

    @Override
    public DownloadQueue initQueue(int taskingSize) {
        return new DownloadChapterQueue(mModule, taskingSize);
    }
}
