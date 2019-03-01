package com.dangdang.reader.dread.format.part;

import android.os.Bundle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.DDApplication;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.request.GetAllChapterByMediaIdRequest;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.reader.utils.NetUtils;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.network.command.RequestQueueManager;
import com.dangdang.zframework.network.command.StringRequest;
import com.dangdang.zframework.plugin.AppUtil;
import com.dangdang.zframework.task.BaseTask;
import com.dangdang.zframework.task.TaskManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 分章阅读目录处理
 * Created by Yhyu on 2015/5/21.
 */
public class PartDirHandleImpl implements IPartDirHandle {
    protected RequestQueueManager mRequestQueueManager;
    private IParseDirListener mListener;
    private String mPid;
    private String mBookdir;
    private int mPartType = IPartDirHandle.PART_TYPE_HTML;
    private static PartDirHandleImpl intance;
    private List<Chapter> chapters = new ArrayList<Chapter>();
    private List<Book.BaseNavPoint> partNavPoints = new ArrayList<Book.BaseNavPoint>();
    private List<Book.BaseNavPoint> partVolumeNavPoints = new ArrayList<Book.BaseNavPoint>();
    private int mVersion;
    private int mLastIndexoOrder;
    private TaskManager mTaskManager;

    public static PartDirHandleImpl getIntance() {
        if (intance == null)
            intance = new PartDirHandleImpl();
        return intance;
    }

    private PartDirHandleImpl() {
        init();
    }

    private void init() {
        mTaskManager = new TaskManager();
        mRequestQueueManager = AppUtil.getInstance(DDApplication.getApplication())
                .getRequestQueueManager();
    }

    public void resetListener() {
        mListener = null;
    }

    public IParseDirListener getListener() {
        return mListener;
    }

    /**
     * 获取目录
     *
     * @param pId
     * @param version
     * @param lastIndexOrder
     * @param listener
     */
    @Override
    public void getChapterList(String pId, int version, int lastIndexOrder, IParseDirListener listener) {
        getChapterList(pId, version, lastIndexOrder, PART_TYPE_HTML, listener);
    }

    /**
     * 获取目录
     *
     * @param pId
     * @param version
     * @param lastIndexOrder
     * @param listener
     */
    @Override
    public void getChapterList(String pId, int version, int lastIndexOrder, int partType, IParseDirListener listener) {
        //同一本书，同一个版本，同样的最大索引并且已经解析过了
        mPartType = partType;

        printLog("getChapterList ,pid = " + pId + ",version = " + version + ",lastIndexOrder = " + lastIndexOrder);
        mPid = pId;
        mVersion = version;
        mLastIndexoOrder = lastIndexOrder;
        mListener = listener;
        mBookdir = DangdangFileManager.getPartBookDir(mPid);
        resetList();
        if (checkDirExist(mPid)) {
            getChapterListFromLocal();
        } else {
            getChapterListFromNet();
        }
    }

    /**
     * 获取本地缓存目录
     */
    private void getChapterListFromLocal() {
        ParseLocalTask task = new ParseLocalTask();
        mTaskManager.putTaskAndRun(task);
    }

    /**
     * 解析缓存目录
     */
    private class ParseLocalTask extends BaseTask<Integer> {

        @Override
        public Integer processTask() throws Exception {
            File destFile = new File(mBookdir + PART_CHAPTER_FILE);
            if (!destFile.exists())
                return 0;
            FileInputStream fis = null;
            ObjectInputStream inStream = null;
            int total = 0;
            try {
                fis = new FileInputStream(destFile);
                inStream = new ObjectInputStream(fis);
                List<PartVolumeObj> volumeObjs = (List<PartVolumeObj>) inStream.readObject();
                for (PartVolumeObj pvo : volumeObjs) {
                    partVolumeNavPoints.add(partVolumeObj2PartVolumePoint(pvo));
                }
                List<PartChapterObj> chapterObjs = (List<PartChapterObj>) inStream.readObject();
                for (PartChapterObj pco : chapterObjs) {
                    PartChapter pc = partChapterObj2PartChapter(pco);
                    if (checkChapterExist(pc)) {
                        chapters.add(pc);
                        partNavPoints.add(partChapter2PartNavPoint(pc));
                    }
                }
                total = chapters.size();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                    }
                }
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                    }
                }
            }
            return total;
        }

        @Override
        public void handleResult(Integer o) {
            printLog("parse task handle result o = " + o);
            // 暂时屏蔽比较功能
            //if (o > 0 && o == (mLastIndexoOrder + 1)) {
                //本地缓存存在并且服务端没有新的章节更新
                mListener.onGetSuccess(0, 0, partVolumeNavPoints, chapters, partNavPoints);
            //} else {
            //    getChapterListFromNet();
            //}
        }
    }

    public boolean checkChapterExist(PartChapter partChapter) {
        if (partChapter == null)
            return false;

        File file = new File(partChapter.getPath());
        if (file.exists() && file.length() != 0)
            return true;
        file.delete();
        return false;
    }
    /**
     * 格式化存储目录
     */
    private class FormatLocalTask extends BaseTask<Boolean> {
        @Override
        public Boolean processTask() throws Exception {
            File destFile = new File(mBookdir + PART_CHAPTER_FILE);
            if (destFile.exists())
                destFile.delete();
            FileOutputStream fos = null;
            ObjectOutputStream outStream = null;
            try {
                destFile.createNewFile();
                fos = new FileOutputStream(destFile);
                outStream = new ObjectOutputStream(fos);
                List<PartVolumeObj> volumeObjs = new ArrayList<PartVolumeObj>();
                for (Book.BaseNavPoint b : partVolumeNavPoints) {
                    volumeObjs.add(partVolume2PartVolumeObj((PartBook.PartVolumeNavPoint) b));
                }
                outStream.writeObject(volumeObjs);

                List<PartChapterObj> chapterObjs = new ArrayList<PartChapterObj>();
                for (Chapter c : chapters) {
                    chapterObjs.add(partChapter2PartChapterObj((PartChapter) c));
                }
                outStream.writeObject(chapterObjs);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                    }
                }
                if (outStream != null) {
                    try {
                        outStream.close();
                    } catch (IOException e) {
                    }
                }
            }
            return false;
        }

        @Override
        public void handleResult(Boolean aBoolean) {

        }
    }

    /**
     * 网络请求
     */
    private void getChapterListFromNet() {
        StringRequest req = new GetAllChapterByMediaIdRequest(mPid, 0, 0, callback, 0, 0);
        mRequestQueueManager.sendRequest(req, mPid);
    }

    /**
     * 网络请求callback
     */
    private GetAllChapterByMediaIdRequest.IDirCallback callback = new GetAllChapterByMediaIdRequest.IDirCallback() {
        @Override
        public void onSuccess(JSONObject jsonResult, Bundle data) {
            resetList();
            parseJSON(jsonResult);
        }

        @Override
        public void onFailed(int errorCode, String msg) {
            mListener.onGetFailed(errorCode, msg);
        }
    };

    /**
     * 解析网络请求结果并处理
     */
    private void parseJSON(JSONObject jsonObject) {
        printLog(jsonObject.toString());
        JSONArray contents = jsonObject.getJSONArray("contents");

        for (int i = 0; i < contents.size(); ++i) {
            JSONObject volume = contents.getJSONObject(i);
            //章
            JSONArray chapterArray = volume.getJSONArray("chapterList");
            //卷
            String volumeId = volume.getString("volumeId");
            String title = volume.getString("title");
            PartBook.PartVolumeNavPoint pvnp = new PartBook.PartVolumeNavPoint();
            pvnp.setLableText(title);
            pvnp.setVolumeId(volumeId);
            pvnp.setCount(chapterArray.size());
            partVolumeNavPoints.add(pvnp);
            //章节

            for (int j = 0; j < chapterArray.size(); j++) {
                PartChapter chapter = chapterArray.getObject(j, PartChapter.class);
                chapter.setPath(getChapterPath(mBookdir, mPid, chapter.getId(), mPartType));
                if (checkChapterExist(chapter)) {
                    chapters.add(chapter);
                    partNavPoints.add(partChapter2PartNavPoint(chapter));
                }
            }
        }
        mListener.onGetSuccess(0, 0, partVolumeNavPoints, chapters, partNavPoints);
        FormatLocalTask formatLocalTask = new FormatLocalTask();
        mTaskManager.putTaskAndRun(formatLocalTask);
    }

    @Override
    public boolean checkDirExist(String pid) {
        File file = new File(DangdangFileManager.getPartBookDir(pid) + PART_CHAPTER_FILE);
        if (!file.exists())
            return false;
        if (file.length() == 0) {
            file.delete();
            return false;
        }
        return true;
    }

    /**
     * 将章节转为锚点实体
     *
     * @param chapter
     * @return
     */
    private Book.BaseNavPoint partChapter2PartNavPoint(PartChapter chapter) {
        PartBook.PartNavPoint point = new PartBook.PartNavPoint();
        point.setChapterId(chapter.getId());
        point.setIsFree(chapter.getIsFree());
        point.setFullSrc(getChapterPath(mBookdir, mPid, chapter.getId(), mPartType));
        point.setLableText(chapter.getTitle());
        return point;
    }

    /**
     * 将程序所用实体转为缓存实体
     *
     * @param partChapter
     * @return
     */
    private PartChapterObj partChapter2PartChapterObj(PartChapter partChapter) {
        PartChapterObj chapterObj = new PartChapterObj();
        chapterObj.setTitle(partChapter.getTitle());
        chapterObj.setId(partChapter.getId());
        chapterObj.setIsFree(partChapter.getIsFree());
        chapterObj.setIndex(partChapter.getIndex());
        return chapterObj;
    }

    /**
     * 将程序所用实体转为缓存实体
     *
     * @param partVolumeNavPoint
     * @return
     */
    private PartVolumeObj partVolume2PartVolumeObj(PartBook.PartVolumeNavPoint partVolumeNavPoint) {
        PartVolumeObj volumeObj = new PartVolumeObj();
        volumeObj.setTitle(partVolumeNavPoint.getLableText());
        volumeObj.setId(partVolumeNavPoint.getVolumeId());
        volumeObj.setCount(partVolumeNavPoint.getCount());
        return volumeObj;
    }

    /**
     * 将缓存实体转为程序所用实体
     *
     * @param partChapterObj
     * @return
     */
    private PartChapter partChapterObj2PartChapter(PartChapterObj partChapterObj) {
        PartChapter partChapter = new PartChapter();
        partChapter.setTitle(partChapterObj.getTitle());
        partChapter.setId(partChapterObj.getId());
        partChapter.setIsFree(partChapterObj.getIsFree());
        partChapter.setIndex(partChapterObj.getIndex());
        partChapter.setPath(getChapterPath(mBookdir, mPid, partChapterObj.getId(), mPartType));
        return partChapter;
    }

    /**
     * 连载类型的章节获取完整路径
     *
     * @param dir
     * @param pid
     * @param cid
     * @param partType
     * @return
     */
    public String getChapterPath(String dir, String pid, int cid, int partType) {
        if (partType == PART_TYPE_ZIP)
            return dir + pid + "_" + cid + PART_CHAPTER_EXT_ZIP + ":" + pid + "_" + cid + PART_CHAPTER_EXT;
        if (partType == PART_TYPE_HTML)
            return dir + pid + "_" + cid + PART_CHAPTER_EXT;
        return "";
    }

    /**
     * 将缓存实体转为程序所用实体
     *
     * @param partVolumeObj
     * @return
     */
    private PartBook.PartVolumeNavPoint partVolumeObj2PartVolumePoint(PartVolumeObj partVolumeObj) {
        PartBook.PartVolumeNavPoint pvnp = new PartBook.PartVolumeNavPoint();
        pvnp.setCount(partVolumeObj.getCount());
        pvnp.setVolumeId(partVolumeObj.getId());
        pvnp.setLableText(partVolumeObj.getTitle());
        return pvnp;
    }

    /**
     * 清理
     */
    private void resetList() {
        chapters.clear();
        partNavPoints.clear();
        partVolumeNavPoints.clear();
    }

    private void printLog(String log) {
        LogM.i(getClass().getSimpleName(), log);
    }

}
