package com.dangdang.reader.dread.format.part;

import com.dangdang.reader.dread.format.Chapter;

/**
 * 分章阅读-章节下载、章节购买接口
 * Created by Yhyu on 2015/5/21.
 */
public interface IPartChapterHandle {
    int DWONLOAD_SUCCESS = 10010;
    int DWONLOAD_NETERROR = 10011;
    int PARSE_SUCEESS = 10013;
    int PARSE_ERROR = 10014;
    int NEED_LOGIN = 10003;
    /**
     * 购买
     */
    int PERMISSION_DENINED = 10004;
    /**
     * 强制下载
     */
    int MEDIA_FORCE_UNSHELVE = 10009;
    int MEDIA_NOT_FOUND = 12002;

    void downloadChapter(String chapterId, String chapterPath,boolean needBuy, IDownLoadChapterListener listener);

    /**
     * 检查文件存不存在
     * @param chapter
     * @return
     */
    boolean checkChapterExist(Chapter chapter);

    /**
     * 是否需要解压
     * @param filePath
     * @return
     */
    boolean isNeedUnZip(String filePath);

    /**
     * 章节下载回调接口
     */
    interface IDownLoadChapterListener {
        /**
         * 回调函数
         *
         * @param code      状态code，包括失败，成功，登陆，购买等
         * @param chapterId 章节id
         * @param buyInfo   仅当code为购买时有意义
         */
        void onDownloadChapter(int code, String chapterId, PartBuyInfo buyInfo);
    }

}
