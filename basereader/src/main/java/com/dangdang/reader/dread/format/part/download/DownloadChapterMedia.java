package com.dangdang.reader.dread.format.part.download;

import com.dangdang.reader.utils.DangdangConfig;
import com.dangdang.zframework.network.download.DownloadManagerFactory.DownloadModule;
import com.dangdang.zframework.network.download.IDownload;
import com.dangdang.zframework.utils.StringUtil;

import java.io.File;

public class DownloadChapterMedia extends IDownload.GetDownload {
    private File mLocalFile;
    private DownloadModule module;
    private String pId;
    private String chapterId;
    private boolean autoBuy;
    private String columnType;

    public DownloadChapterMedia(DownloadModule module, String pId, String chapterId, String chapterPath, boolean autoBuy,
                                String columnType) {
        this.module = module;
        this.pId = pId;
        this.chapterId = chapterId;
        mLocalFile = new File(chapterPath);
        this.autoBuy = autoBuy;
        this.columnType = columnType;
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
        return mLocalFile;
    }

    @Override
    public DownloadModule getDownloadModule() {
        return module;
    }

    @Override
    public String getUrl() {
        StringBuilder buff = new StringBuilder(DangdangConfig.SERVER_MEDIA_API2_URL);
        buff.append("action=downloadMedia");
        //fromPaltform	String	否	ds_android(当当读书安卓平台)，ds_ios(当当读书ios平台)，yc_android(当读小说安卓平台)，yc_ios(当读小说ios平台),若参数为空则默认查询当读小说安卓平台数据
        buff.append("&fromPaltform="+DangdangConfig.ParamsType.getFromPaltform());
        buff.append("&mediaId=");
        buff.append(pId);
        buff.append("&chapterId=");
        buff.append(chapterId);
        if (autoBuy) {
            buff.append("&autoBuy=1");
        }
        if (!StringUtil.isEmpty(columnType)) {
            buff.append("&columnType=");
            buff.append(columnType);
        }
        return  buff.toString();
    }
}
