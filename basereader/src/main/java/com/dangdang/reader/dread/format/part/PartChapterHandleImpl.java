package com.dangdang.reader.dread.format.part;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.DDApplication;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.format.BaseBookManager;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.part.download.DownloadChapterMangagerFactory;
import com.dangdang.reader.dread.format.part.download.DownloadChapterMedia;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.network.command.Request;
import com.dangdang.zframework.network.command.RequestQueueManager;
import com.dangdang.zframework.network.download.DownloadConstant;
import com.dangdang.zframework.network.download.DownloadManagerFactory;
import com.dangdang.zframework.network.download.IDownloadManager;
import com.dangdang.zframework.plugin.AppUtil;
import com.dangdang.zframework.utils.URLUtil;

/**
 * 原创的章节购买和下载
 * Created by Yhyu on 2015/5/21.
 */
public class PartChapterHandleImpl implements IPartChapterHandle {
    private DownloadConstant.Status mDownStatus = DownloadConstant.Status.UNSTART;
    protected RequestQueueManager mRequestQueueManager;
    protected String mPid;
    private DownloadManagerFactory.DownloadModule module;
    private IDownloadManager mDownloadManager;
    private Map<String, IDownLoadChapterListener> listeners = new HashMap<String, IDownLoadChapterListener>();

    public PartChapterHandleImpl(String pid) {
        mPid = pid;
        init();
    }

    private void init() {
        mRequestQueueManager = AppUtil.getInstance(DDApplication.getApplication())
                .getRequestQueueManager();
        module = new DownloadManagerFactory.DownloadModule("service");
        module.setTaskingSize(3);
//        mDownloadManager = DownloadManagerFactory.getFactory().create(
//                module);
        mDownloadManager = DownloadChapterMangagerFactory.getFactory().create(
                module);
        mDownloadManager.registerDownloadListener(PartChapterHandleImpl.class,
                downloadListener);
    }

    @Override
    public synchronized void downloadChapter(String chapterId, String chapterPath, boolean needBuy,IDownLoadChapterListener listener) {
        if (listeners.containsKey(chapterId)) {
            return;
        }
        listeners.put(chapterId, listener);
        DownloadChapterMedia download = new DownloadChapterMedia(module,mPid, chapterId + "", chapterPath, needBuy, null);
        mDownloadManager.startDownload(download);

    }

    @Override
    public boolean checkChapterExist(Chapter chapter) {
        if (chapter == null)
            return false;
        return checkChapterExist(chapter.getPath());
    }
    public boolean checkChapterExist(String chapterPath) {
        File file = new File(chapterPath);
        if (file.exists() && file.length() != 0)
            return true;
        file.delete();
        return false;
    }
    final IDownloadManager.IDownloadListener downloadListener = new IDownloadManager.IDownloadListener() {

        @Override
        public void onDownloading(IDownloadManager.DownloadInfo info) {
            printLog(" onDownloading " + info);
            mDownStatus = DownloadConstant.Status.DOWNLOADING;
        }

        @Override
        public void onPauseDownload(IDownloadManager.DownloadInfo info) {
            printLog(" onPauseDownload " + info);
            mDownStatus = DownloadConstant.Status.PAUSE;
        }

        @Override
        public void onDownloadFinish(IDownloadManager.DownloadInfo info) {
            printLog(" onDownloadFinish " + info);
            Map<String, String> keyPairs = URLUtil.splitQuery(info.url);
            String chapterId = keyPairs.get("chapterId");
            if (info.url.contains("downloadMedia")) {
//                msg.what = DOWNLOAD_CHAPTER_MSG;
                  checkZip(chapterId,info.file.getAbsolutePath());
                //下载完成
            } else if (info.url.contains("buyChapter")) {
                //购买完成
//                msg.what = SUBSCRIBE_CHAPTER_MSG;
            }
            IDownLoadChapterListener listener = listeners.get(chapterId);
            if (listener != null) {
                listener.onDownloadChapter(DWONLOAD_SUCCESS, chapterId, null);
                listeners.remove(chapterId);
            }
        }

        @Override
        public void onFileTotalSize(IDownloadManager.DownloadInfo info) {
            printLog(" onFileTotalSize " + info);
        }

        @Override
        public void onDownloadFailed(IDownloadManager.DownloadInfo info,
                                     IDownloadManager.DownloadExp exp) {
            printLog(" onDownloadFailed " + info);
            printLog(" onDownloadFailed exp=" + exp);
            Map<String, String> keyPairs = URLUtil.splitQuery(info.url);
            String chapterId = keyPairs.get("chapterId");
            int responseCode = exp.responseCode;
            int code = -1;
            PartBuyInfo buyInfo = null;
            if (responseCode == 200) {
                //未登录，没有权限等
                JSONObject jsonObject = JSON.parseObject(exp.errMsg);
                code = jsonObject.getJSONObject("status").getInteger("code");
                buyInfo = jsonObject.getJSONObject("data").getObject("buyInfo", PartBuyInfo.class);
                if (buyInfo!=null){
                    code=PERMISSION_DENINED;
                }

            } else {
                code = DWONLOAD_NETERROR;
            }
            IDownLoadChapterListener listener = listeners.get(chapterId);
            if (listener != null) {
                listener.onDownloadChapter(code, chapterId, buyInfo);
                listeners.remove(chapterId);
            }
        }
    };

    private void checkZip(String chapterId,String path) {
        printLog("download chapter success,chapterId=" + chapterId);
        if (checkChapterExist(path)) {
            // check zip file and unzip
            if (isNeedUnZip(path)) {
                try {
                    String zipPath = path.substring(0, path.length() - 4) + "zip";
                    File oldFile = new File(path);
                    File zipFile = new File(zipPath);
                    oldFile.renameTo(zipFile);
                    unZip(zipPath, mPid, chapterId);
//                    zipFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean isNeedUnZip(String filePath) {
        return isZip(filePath);
    }

    /**
     * 判断文件是否为ZIP文件
     *
     * @param filePath 文件路径
     * @return 是则返回 true 不是则返回 false
     */
    public static Boolean isZip(String filePath) {
        try {
            new ZipFile(filePath).close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    /**
     * 解压
     *
     * @param zipPath
     * @throws Exception
     */
    private   void unZip(String zipPath, String pId, String chapterId) throws Exception {
        ZipFile zf = null;
        try {
            zf = new ZipFile(zipPath);
            String destDir = DangdangFileManager.getPartBookDir(pId);
            Enumeration<? extends ZipEntry> entries = zf.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                File f;
                if (entry.getName().endsWith("html"))
                    f = new File(destDir + mPid+"_"+chapterId + IPartDirHandle.PART_CHAPTER_EXT);
                else
                    f = new File(destDir + entry.getName());

                File parent = f.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }

                InputStream inStream = null;
                FileOutputStream outStream = null;
                try {

                    inStream = zf.getInputStream(entry);
                    outStream = new FileOutputStream(f);
                    int len = 0;
                    int size = 1024 * 6;
                    byte[] bs = new byte[size];
                    while ((len = inStream.read(bs, 0, size)) > 0) {
                        outStream.write(bs, 0, len);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception(e);
                } finally {
                    if (inStream != null) {
                        inStream.close();
                    }
                    if (outStream != null) {
                        outStream.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    protected void sendRequest(Request<?> request, Object tag) {
        mRequestQueueManager.sendRequest(request, tag);
    }

    private void printLog(String log) {
        LogM.i(getClass().getSimpleName(), log);
    }
}
