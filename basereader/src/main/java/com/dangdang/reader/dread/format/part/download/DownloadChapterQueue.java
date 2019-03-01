package com.dangdang.reader.dread.format.part.download;

import com.dangdang.zframework.network.download.DownloadManagerFactory;
import com.dangdang.zframework.network.download.DownloadQueue;
import com.dangdang.zframework.network.download.DownloadTask;
import com.dangdang.zframework.network.download.IDownload;

/**
 * Created by Yhyu on 2015/6/2.
 */
public class DownloadChapterQueue extends DownloadQueue {
    public DownloadChapterQueue(DownloadManagerFactory.DownloadModule module, int taskingSize) {
        super(module, taskingSize);
        // TODO Auto-generated constructor stub
    }

    @Override
    public DownloadTask getDownloadTask(IDownload request, DownloadCallback callback) {
        DownloadTask task = new DownloadChapterTask(request, callback);
        return task;
    }
}
