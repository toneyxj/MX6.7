package com.dangdang.reader.dread.format.part.download;

import com.dangdang.reader.utils.DangdangConfig;
import com.dangdang.zframework.network.download.DownloadManagerFactory.DownloadModule;
import com.dangdang.zframework.network.download.IDownload;

import java.io.File;

/**
 * Created by wanghaiming on 2015/12/15.
 */
public class BulkDownloadChapterRequest extends IDownload.GetDownload{

    // download args
    private File mLocFile;
    private DownloadModule mModule;

    // request args
    private long mStartChapterId;
    private long mEndChapterId;
    private long  mMediaChannelId;
    private boolean  mIsAutoBuy;

    public BulkDownloadChapterRequest(DownloadModule module, String destPath, long startChapterId, long endChapterId, long mediaChannelId, boolean isAutoBuy ){
        {

            mModule = module;
            mLocFile = new File(destPath);
            mStartChapterId = startChapterId;
            mEndChapterId = endChapterId;
            mMediaChannelId = mediaChannelId;
            mIsAutoBuy = isAutoBuy;
        }
    }
    @Override
    public long getStartPosition() {
        return 0;
    }

    @Override
    public long getTotalSize() {
        return 0;
    }

    @Override
    public File getLoaclFile() {
        return mLocFile;
    }

    @Override
    public DownloadModule getDownloadModule() {
        return mModule;
    }

    @Override
    public String getUrl() {
        StringBuilder buff = new StringBuilder(DangdangConfig.SERVER_MEDIA_API2_URL);
        buff.append("action=downloadMediaBatch");
        buff.append("&startChapterId=").append(mStartChapterId);
        buff.append("&endChapterId=").append(mEndChapterId);
        buff.append("&autoBuy=").append(mIsAutoBuy?1:0);
        if(mMediaChannelId > 0){
            buff.append("&MediaChannelId=").append(mMediaChannelId);
        }

        buff.append("&fromPaltform="+DangdangConfig.ParamsType.getFromPaltform());

        return  buff.toString();
    }
}
