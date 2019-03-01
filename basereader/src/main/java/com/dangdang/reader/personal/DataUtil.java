package com.dangdang.reader.personal;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;

import com.dangdang.reader.Constants;
import com.dangdang.reader.DDApplication;
import com.dangdang.reader.R;
import com.dangdang.reader.db.ShelfBookDBColumn;
import com.dangdang.reader.db.service.BuyBookService;
import com.dangdang.reader.db.service.ShelfBookService;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.util.DrmWrapUtil;
import com.dangdang.reader.personal.domain.GroupItem;
import com.dangdang.reader.personal.domain.GroupType;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.reader.personal.domain.ShelfBook.BookType;
import com.dangdang.reader.personal.domain.ShelfBook.TryOrFull;
import com.dangdang.reader.request.BaseStringRequest;
import com.dangdang.reader.request.GetCertificateRequest;
import com.dangdang.reader.request.GetPublishedCertificateRequest;
import com.dangdang.reader.request.RequestConstants;
import com.dangdang.reader.request.RequestResult;
import com.dangdang.reader.request.UpdateFollowBookListRequest;
import com.dangdang.reader.utils.AccountManager;
import com.dangdang.reader.utils.DROSUtility;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.reader.utils.ImageConfig;
import com.dangdang.reader.utils.InbuildBooks;
import com.dangdang.reader.utils.ReadBook;
import com.dangdang.reader.utils.ReadBook.OldBookListener;
import com.dangdang.reader.utils.ResourceManager;
import com.dangdang.reader.utils.Utils;
import com.dangdang.reader.view.ConfirmDialog;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.network.download.DownloadConstant.Status;
import com.dangdang.zframework.network.image.ImageManager;
import com.dangdang.zframework.plugin.AppUtil;
import com.dangdang.zframework.utils.MemoryStatus;
import com.dangdang.zframework.utils.UiUtil;
import com.dangdang.zframework.view.DDImageView;
import com.dangdang.zframework.view.DDTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DataUtil implements OldBookListener {

    private static DataUtil util;
    private Context mContext;
    //	private String TAG;
    private ShelfBookService mService;
    private List<ShelfBook> mList;
    private List<GroupItem> mGroupList;

    private Set<String> mSet;
    private AccountManager mConfig;
    private ConfirmDialog mDeletedialog;
    private ShelfReceiver mReceiver;
//    private ShelfDownloadManager mDownloadManager;
    private Handler mHandler;

    private Drawable bgDrawable;

    public synchronized static DataUtil getInstance(Context context) {
        if (util == null)
            util = new DataUtil(context);
        return util;
    }

    private DataUtil(Context context) {
        this.mContext = context.getApplicationContext();
        init(mContext);
    }

    private void init(Context context) {
//		TAG = this.getClass().getName();
        mService = ShelfBookService.getInstance(context);
        mList = Collections.synchronizedList(new ArrayList<ShelfBook>());
        mGroupList = Collections.synchronizedList(new ArrayList<GroupItem>());

        mSet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        mConfig = new AccountManager(context);
        mReceiver = new ShelfReceiver();
        mReceiver.init(mContext);
//        mDownloadManager = new ShelfDownloadManager(mContext, this);
        mHandler = new MyHandler(this);

        if (isOrderByTime())
            getShelfList(true);
        else{
//            getGroupList(true);
        }
    }

    public void release() {
        try {
            if (mReceiver != null) {
                mContext.unregisterReceiver(mReceiver);
                mReceiver = null;
            }
            if (mService != null) {
                mService.release();
                mService = null;
            }
            bgDrawable = null;
            util = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public DangUserInfo getCurrentUser() {
//        String id = mConfig.getLoginID();
//        if (id.equals(""))
//            return null;
//        LoginType type = mConfig.getLoginType();
//        return mService.getUserInfo(id, type);
//    }

    public boolean isLogin() {
        String id = mConfig.getLoginID();
        if (id.equals(""))
            return false;
        return true;
    }

    public boolean isOrderByTime() {
        return mConfig.isOrderByTime();
    }

    private void setOrderType(boolean bo) {
        mConfig.setOrderType(bo);
    }

    /**
     * 书架时间排序列表
     *
     * @param reload
     * @return
     */
    public List<ShelfBook> getShelfList(boolean reload) {
        if (reload || mList.isEmpty()) {
            mList.clear();
            mSet.clear();
            mList.addAll(mService.getAllShelfBookList());
            for (ShelfBook book : mList) {
                mSet.add(book.getMediaId());
            }
        }
        return mList;
    }


    public HashSet<String> getImportBookPathSet() {
        return mService.getImportBookPathSet();
    }


    public void deleteFile(ShelfBook book, boolean delete) {
//        mSet.remove(book.getMediaId());
//        // 删除书，先删除下载任务
//        if (book.getBookFinish() != 1) {
//            mDownloadManager.removeDownloadTask(book.getMediaId());
//        }
//        if (!delete)
//            return;
//        // 不删本地导入的
//        String id = book.getMediaId();
//        if (id != null && id.contains("_"))
//            return;
//        File f = new File(book.getBookDir());
//        try {
//            if (f.exists()) {
//                File to = new File(f.getAbsolutePath() + System.currentTimeMillis());
//                f.renameTo(to);
//                if (to.isFile())
//                    to.delete();
//                else if (to.isDirectory())
//                    FileUtil.deleteDirectory(to);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public GroupItem getDefaultGroupItem() {
        GroupType type = new GroupType();
        type.setId(Constants.UNKNOW_TYPE);
        type.setName(mContext.getString(R.string.bookshelf_no_group));
        GroupItem item = new GroupItem(type, new ArrayList<ShelfBook>());

        return item;
    }

    private void addTxtBookListToShelf() {
        DDApplication ddApplication = (DDApplication) mContext;
        List<ShelfBook> list = ddApplication.getmImportBookList();
        List<ShelfBook> aim = null;
        ;
        if (isOrderByTime()) {
            aim = mList;
        } else {
            if (mGroupList.isEmpty() || mGroupList.get(0).type.getId() != Constants.UNKNOW_TYPE) {
                mGroupList.add(0, getDefaultGroupItem());
            }
            aim = mGroupList.get(0).list;
        }

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ShelfBook info = list.get(i);
                if (mSet.contains(info.getMediaId()))
                    continue;
                aim.add(0, info);
                mSet.add(info.getMediaId());
            }
            ddApplication.setmImportBookList(null);
        }
    }


    @Override
    public void reDownloadOldData(ShelfBook info, Context context) {
        // TODO Auto-generated method stub

    }

    private int openBook(ShelfBook book, String refer, Activity ac) {
        if (ac == null) {
            LogM.l("open book activity is null");
            return -1;
        }
        if (book.getBookFinish() == 1) {
            ReadBook data = new ReadBook(ac, book);
            data.setmOldBookListener(this);
            data.readBook(refer);
            return 0;
        } else {
            ;//downloadClickEvent(book, context);
        }
        return -1;
    }


    public ShelfBook getShelfBookFromList(String bookId) {
        if (isOrderByTime()) {
            for (ShelfBook book : mList)
                if (book.getMediaId().equals(bookId))
                    return book;
        } else {
            for (GroupItem group : mGroupList)
                for (ShelfBook book : group.list)
                    if (book.getMediaId().equals(bookId))
                        return book;
        }
        return null;
    }

    /**
     * 匹配 书名，作者，暂时屏蔽简介
     *
     * @param book
     * @param word
     * @return
     */
    private boolean hasKeyWord(ShelfBook book, String word) {
        if (book == null)
            return false;
        word = word.toLowerCase();
        if (!TextUtils.isEmpty(book.getTitle()) && book.getTitle().toLowerCase().contains(word))
            return true;
        if (!TextUtils.isEmpty(book.getAuthorPenname()) && book.getAuthorPenname().toLowerCase().contains(word))
            return true;
        /*if(!TextUtils.isEmpty(book.getDescs()) && book.getDescs().toLowerCase().contains(word))
            return true;*/
        return false;
    }

    /**
     * 搜索书架
     *
     * @param word
     * @return
     */
    public List<ShelfBook> getShelfBooksByKeyWord(String word) {
        List<ShelfBook> list = new LinkedList<ShelfBook>();
        if (TextUtils.isEmpty(word))
            return list;

        if (isOrderByTime()) {
            for (ShelfBook book : mList)
                if (hasKeyWord(book, word))
                    list.add(book);
        } else {
            for (GroupItem group : mGroupList)
                for (ShelfBook book : group.list)
                    if (hasKeyWord(book, word))
                        list.add(book);
        }
        return list;
    }


    public void startReadActivity(ShelfBook item, Object tag, Activity ac) {
        if (item == null) {
            return;
        }

        if (item.getBookFinish() == 1 || item.getBookType() != BookType.BOOK_TYPE_NOT_NOVEL) {
            if (item.getBookType() != BookType.BOOK_TYPE_NOT_NOVEL)
                openBook(item, "", ac);
            else if (DangdangFileManager.isImportBook(item.getMediaId()))
                openBook(item, "", ac);
            else if (item.getBookKey() == null)
                getKeyOrDownload(item, tag);
            else {
                openBook(item, "", ac);
//                //非训练的书做验证
//                if ((item.getTryOrFull() == TryOrFull.MONTH_FULL && item.getDeadline() != 0)) {
//                    //新的VIP包月 进行权限校验
//                    ArrayList<String> ids = new ArrayList<String>();
//                    ids.add(item.getMediaId());
//                    GetCloudMonthBookListRequest req = new GetCloudMonthBookListRequest(mHandler, ids, false);
//                    AppUtil.getInstance(mContext).getRequestQueueManager().sendRequest(req, tag);
//                }
            }

        } else
            getKeyOrDownload(item, tag);
    }


    private void getKeyOrDownload(ShelfBook book, Object tag) {
        if (book.getBookKey() == null) {
            if (book.getBookType() != BookType.BOOK_TYPE_IS_FULL_NO)
                book.setDownloadStatus(Status.WAIT);
            getBookKeyRequest(book, tag);
        } else
            dealDownload(book);
    }

    public void getBookKeyRequest(ShelfBook book, Object tag) {
        BaseStringRequest request;
        if (book.getBookType() == BookType.BOOK_TYPE_NOT_NOVEL) {
            request = new GetPublishedCertificateRequest(mContext, null, mHandler, book);
        } else {
            request = new GetCertificateRequest(book.getMediaId(), null, mHandler);
            ((GetCertificateRequest) request).setShelfBook(book);
        }
        AppUtil.getInstance(mContext).getRequestQueueManager().sendRequest(request, tag);
    }

    private void dealDownload(ShelfBook book) {
//        if (book.getBookFinish() == 1)
//            return;
//        ShelfDownload down = new ShelfDownload(mDownloadManager.getModule(), book);
//        mDownloadManager.download(down);
    }

    /**
     * 是否存在同名分组
     *
     * @param name
     * @return
     */
    public boolean isGroupExist(String name) {
        return mService.isGroupExist(name);
    }

    public void clearGroupList() {
        mGroupList.clear();
    }

    public void removeGroupItems(List<GroupItem> list) {
        if (mGroupList == null || list == null)
            return;
        mGroupList.removeAll(list);
        mService.deleteGroupByItems(list);
    }

    public GroupItem getGroupItem(int index) {
        if (mGroupList == null || index >= mGroupList.size())
            return null;
        return mGroupList.get(index);
    }

    public void removeGroupItem(int index) {
        if (mGroupList == null || index >= mGroupList.size())
            return;
        int id = mGroupList.get(index).type.getId();
        mGroupList.remove(index);
        mService.deleteGroupById(id);
    }

    public void deleteBooksInGroup(int index, boolean deleteFile, boolean deleteLocal) {
        if (mGroupList == null || index >= mGroupList.size() || index < 0)
            return;
        GroupItem item = mGroupList.get(index);
        List<ShelfBook> list = item.list;
        int id = item.type.getId();
        if (deleteFile) {
            // 初始化删除的正在追更的书，告诉服务器取消这些书的追更，否则会继续收到服务端推送的追更消息
            List<ShelfBook> followStatusChangeList = new ArrayList<ShelfBook>();
            for (ShelfBook sb : list) {
                if (!sb.isSelect() || !sb.isFollow()) {
                    continue;
                } else {
                    followStatusChangeList.add(sb);
                }
            }
            updateServerFollowStatus(followStatusChangeList, 0);

            mService.deleteMultiShelfBook(list);
        } else {
            mService.updateGroup(list, Constants.UNKNOW_TYPE);
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            ShelfBook book = list.get(i);
            if (!book.isSelect())
                continue;
            list.remove(i);
            if (!deleteFile) {
                book.setSelect(false);
                mGroupList.get(0).list.add(0, book);
            } else
                deleteFile(book, deleteLocal);
        }
        // 删空分组
        if (id != Constants.UNKNOW_TYPE && list.isEmpty()) {
            mGroupList.remove(index);
            mService.deleteGroupById(id);
        }
    }

    public void deleteOneBook(int index, ShelfBook book, boolean delete) {
        if (mGroupList == null || index >= mGroupList.size())
            return;

        // 初始化删除的正在追更的书，告诉服务器取消这些书的追更，否则会继续收到服务端推送的追更消息
        if (book != null && book.isFollow()) {
            List<ShelfBook> followStatusChangeList = new ArrayList<ShelfBook>();
            followStatusChangeList.add(book);
            updateServerFollowStatus(followStatusChangeList, 0);
        }
        if (book == null)
            return;
        mService.deleteOneBook(book.getMediaId());
        GroupItem item = mGroupList.get(index);
        int id = item.type.getId();
        List<ShelfBook> list = item.list;
        list.remove(book);
        deleteFile(book, delete);
        // 删空分组
        if (id != Constants.UNKNOW_TYPE && list.isEmpty()) {
            mGroupList.remove(index);
            mService.deleteGroupById(id);
        }
    }

    public boolean changeSort() {
        setOrderType(!isOrderByTime());
        return this.isOrderByTime();
    }

    public void deleteBookByDir(String dir, boolean delete) {
        if (isOrderByTime()) {
            for (int i = 0; i < mList.size(); i++) {
                ShelfBook book = mList.get(i);
                if (book.getBookDir().equals(dir)) {
                    mList.remove(book);
                    mService.deleteOneBook(book.getMediaId());
                    deleteFile(book, delete);
                    break;
                }
            }
        } else {
            boolean found = false;
            for (GroupItem item : mGroupList) {
                for (int i = 0; i < item.list.size(); i++) {
                    ShelfBook book = item.list.get(i);
                    if (book.getBookDir().equals(dir)) {
                        item.list.remove(book);
                        mService.deleteOneBook(book.getMediaId());
                        deleteFile(book, delete);
                        found = true;
                        break;
                    }
                }
                if (found) {
                    // 删空分组
                    int tmp = item.type.getId();
                    if (tmp != Constants.UNKNOW_TYPE && item.list.isEmpty()) {
                        mGroupList.remove(item);
                        mService.deleteGroupById(tmp);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 需求变了，赠书领成功后不删书架，废弃
     * 根据ID删书架书，给赠书用
     *
     * @param set
     */
    private void deleteBooksById(HashSet<String> set) {
        // 先去掉书架上没有的ID
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String str = it.next();
            if (!mSet.contains(str))
                it.remove();
        }
        if (set.isEmpty())
            return;

        // 遍历删掉目标书
        List<ShelfBook> list = new ArrayList<ShelfBook>();
        if (isOrderByTime()) {
            for (int i = mList.size() - 1; i >= 0; i--) {
                ShelfBook book = mList.get(i);
                if (!set.contains(book.getMediaId()))
                    continue;
                mList.remove(i);
                list.add(book);
                set.remove(book.getMediaId());
                if (set.isEmpty())
                    break;
            }
        } else {
            for (int j = mGroupList.size() - 1; j >= 0; j--) {
                GroupItem item = mGroupList.get(j);
                for (int i = item.list.size() - 1; i >= 0; i--) {
                    ShelfBook book = item.list.get(i);
                    if (!set.contains(book.getMediaId()))
                        continue;
                    item.list.remove(i);
                    list.add(book);
                    set.remove(book.getMediaId());
                    if (set.isEmpty())
                        break;
                }
                int tmp = item.type.getId();
                if (tmp != Constants.UNKNOW_TYPE && item.list.isEmpty()) {
                    mGroupList.remove(item);
                    mService.deleteGroupById(tmp);
                }
                if (set.isEmpty())
                    break;
            }
        }
        // 删掉书架记录
        mService.deleteMultiShelfBookById(list);

        // 更新追更状态为不追更
        List<String> tmp = new ArrayList<String>();
        for (ShelfBook book : list) {
            if (book.getBookType() != BookType.BOOK_TYPE_NOT_NOVEL && book.isFollow())
                tmp.add(book.getMediaId());
            // 删掉文件
            deleteFile(book, true);
        }
        updateServerFollowStatuss(tmp, 0);

        // 通知书架刷新
        Intent intent = new Intent(Constants.ACTION_REFRESH_ADAPTER);
        mContext.sendBroadcast(intent);
    }

    /**
     * @param groupId
     * @param groupName
     */
    public void updateGroupName(int groupId, String groupName) {
        long time = mService.updateGroupName(groupId, groupName);
        if (time > 0) {
            moveGroupToHeadWithName(groupId, time, groupName);
        }
    }

    private void moveGroupToHeadWithName(int id, long time, String name) {
        GroupType result = moveGroupToHead(id, time);
        if (result != null)
            result.setName(name);
    }

    private GroupType moveGroupToHead(int groupId, long time) {
        GroupType result = null;
        if (groupId == 0 || time < 0) {
            return null;
        }
        for (GroupItem group : mGroupList) {
            if (group.type.getId() == groupId) {
                group.type.setCreateTime(time);
                mGroupList.remove(group);
                mGroupList.add(1, group);
                result = group.type;
                break;
            }
        }
        return result;
    }

    /**
     * 退出阅读，重新排序
     *
     * @param bookId
     * @param progress
     * @param key
     * @param isFollow
     * @param lastindex
     */
    public void reorderBook(String bookId, String progress, byte[] key, boolean isFollow, boolean preload, int lastindex) {
        boolean changeFollow = false;
        HashMap<String, Long> map = mService.updateLastTime(bookId, progress, key, isFollow, preload, lastindex);
        long time = map.get("time");
        long groupId = map.get("groupId");
        if (isOrderByTime()) {
            for (ShelfBook book : mList) {
                if (book.getMediaId().equals(bookId)) {
                    mList.remove(book);
                    mList.add(0, book);
                    book.setLastTime(time);
                    book.setReadProgress(progress);
                    if (key != null)
                        book.setBookKey(key);
                    if (book.isFollow() != isFollow) {
                        book.setFollow(isFollow);
                        changeFollow = true;
                    }
                    if (lastindex != -1) {
                        book.setServerLastIndexOrder(lastindex);
                        book.setLocalLastIndexOrder(lastindex);
                    } else
                        book.setLocalLastIndexOrder(book.getServerLastIndexOrder());
                    book.setPreload(preload);
                    break;
                }
            }
        } else {
            for (GroupItem group : mGroupList) {
                if (group.type.getId() == groupId) {
                    for (ShelfBook book : group.list) {
                        if (book.getMediaId().equals(bookId)) {
                            group.list.remove(book);
                            group.list.add(0, book);
                            book.setLastTime(time);
                            book.setReadProgress(progress);
                            if (key != null)
                                book.setBookKey(key);
                            if (book.isFollow() != isFollow) {
                                book.setFollow(isFollow);
                                changeFollow = true;
                            }
                            book.setPreload(preload);
                            if (lastindex != -1) {
                                book.setServerLastIndexOrder(lastindex);
                                book.setLocalLastIndexOrder(lastindex);
                            } else
                                book.setLocalLastIndexOrder(book.getServerLastIndexOrder());
                            break;
                        }
                    }
                    group.type.setCreateTime(time);
                    if (groupId != Constants.UNKNOW_TYPE) {
                        mGroupList.remove(group);
                        mGroupList.add(1, group);
                    }
                    break;
                }
            }
        }
        if (changeFollow) {
            List<String> list = new ArrayList<String>();
            list.add(bookId);
            if (isFollow)
                updateServerFollowStatuss(list, 1);
            else
                updateServerFollowStatuss(list, 0);
        }
        mContext.sendBroadcast(new Intent(Constants.BROADCAST_FINISH_REORDER));
        mContext.sendBroadcast(new Intent(Constants.BROADCAST_FINISH_READ));

        ReadConfig.getConfig().setReadProgress(mContext, progress);
    }

    /**
     * 退出阅读，重新排序
     *
     * @param bookId
     * @param progress
     * @param key
     * @param isFollow
     */
    public void reorderBook(String bookId, String progress, byte[] key, boolean isFollow, boolean preload) {
        reorderBook(bookId, progress, key, isFollow, preload, -1);
    }


    /**
     * 从数据库取（未）追更列表
     *
     * @param isFollow
     * @return
     */
    protected List<ShelfBook> getFollowListFromDB(boolean isFollow) {
        return mService.getFollowList(isFollow);
    }

    /**
     * 记录是否追更
     *
     * @param list
     * @param isFollow
     */
    public void updateFollowStatus(List<ShelfBook> list, boolean isFollow) {
        if (list == null || list.size() <= 0) {
            return;
        }
        // 更新数据库中的追更状态
        mService.updateFollowStatus(list, isFollow);

        // 更新服务器的追更列表
        int operationType = isFollow ? 1 : 0;
        updateServerFollowStatus(list, operationType);
    }

    /**
     * 更新服务器的追更状态
     *
     * @param list
     * @param operationType 0：取消追更；1：添加追更
     */
    private void updateServerFollowStatus(List<ShelfBook> list, int operationType) {
        List<String> mediaIdList = new ArrayList<String>();
        for (ShelfBook sb : list) {
            mediaIdList.add(sb.getMediaId());
        }
        updateServerFollowStatuss(mediaIdList, operationType);
    }

    /**
     * 更新服务器的追更状态
     *
     * @param list
     * @param operationType 0：取消追更；1：添加追更
     */
    private void updateServerFollowStatuss(List<String> list, int operationType) {
        if (list == null || list.isEmpty())
            return;
        UpdateFollowBookListRequest request = new UpdateFollowBookListRequest(mContext, operationType, list);
        AppUtil.getInstance(mContext).getRequestQueueManager().sendRequest(request, UpdateFollowBookListRequest.class.getSimpleName());
    }

    /**
     * 快速分组
     */
    public int quickGroup() {
        List<ShelfBook> list = new ArrayList<ShelfBook>();
        if (isOrderByTime()) {
            for (ShelfBook book : mList) {
                if (book.getGroupId() == Constants.UNKNOW_TYPE)
                    list.add(book);
            }
        } else {
            list = mGroupList.get(0).list;
        }
        if (list.isEmpty())
            return 0;
        int size = list.size();
        SparseArray<GroupItem> array = new SparseArray<GroupItem>();
        for (ShelfBook book : list) {
            GroupItem group = array.get(book.getGroupType().getId(), null);
            if (group == null) {
                ArrayList<ShelfBook> tmp = new ArrayList<ShelfBook>();
                tmp.add(book);
                GroupType type = new GroupType();
                type.setId(book.getGroupType().getId());
                type.setName(book.getGroupType().getName());
                GroupItem item = new GroupItem(type, tmp);
                array.put(type.getId(), item);
            } else {
                group.list.add(book);
            }
        }
        mService.quickGroup(array);
        if (!isOrderByTime()) {
            mGroupList.get(0).list.clear();
            int len = array.size();
            for (int i = 0; i < len; i++) {
                GroupItem item = array.valueAt(i);
                if (item.type.getId() == Constants.UNKNOW_TYPE) {
                    mGroupList.get(0).list.addAll(item.list);
                } else if (item.isNew)
                    mGroupList.add(1, item);
                else {
                    for (GroupItem group : mGroupList) {
                        if (group.type.getId() == item.type.getId()) {
                            group.list.addAll(0, item.list);
                            break;
                        }
                    }
                }
            }
        }
        GroupItem item = array.get(Constants.UNKNOW_TYPE, null);
        if (item != null)
            return size - item.list.size();
        else
            return size;
    }

    public void installApk(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

//    public void removeDownloadListener(IShelfDownloadListener l) {
//        if (mDownloadManager != null) {
//            mDownloadManager.removeDownloadListener(l);
//        }
//    }
//
//    public void addDownloadListener(IShelfDownloadListener l) {
//        if (mDownloadManager != null) {
//            mDownloadManager.addDownloadListener(l);
//        }
//    }

    /**
     * 下载一本书
     *
     * @param book
     * @param tag
     */
    public void downloadBook(ShelfBook book, Object tag) {
        int value = Utils.hasAvailable(MemoryStatus.MIN_SPACE * 5,
                1024 * 1024 * 1, MemoryStatus.MIN_SPACE);
        if (value == -1) {
            UiUtil.showToast(mContext, R.string.string_mounted_error);
            return;
        } else if (value == 0) {
            UiUtil.showToast(mContext, R.string.externalmemory_few);
            return;
        }

//		DangUserInfo info = this.getCurrentUser();
//		if(info != null){
//			if(this.hasBookOnShelf(book.getMediaId())){
//				String id = mMap.get(book.getMediaId());
//				if(!TextUtils.isEmpty(id) && !id.equals(info.id)){
//					UiUtil.showToast(mContext, "请登录正确账号下载");
//					Intent intent = new Intent(Constants.BROADCAST_DOWNLOAD_BOOK_FINISH);
//					intent.putExtra("bookId", book.getMediaId());
//					intent.putExtra("success", false);
//					mContext.sendBroadcast(intent);
//					return;
//				}
//			}
//		}

//		if(book.getLastTime() == 0)
        book.setLastTime(System.currentTimeMillis());

        // 包月信息
//		book.setMonthlyPaymentType(MonthlyPaymentType.valueOf(intent.getIntExtra("borrowType", 0)));
//		book.setDeadline(intent.getLongExtra("deadline", 0));

        if (book.getBookType() == BookType.BOOK_TYPE_IS_FULL_NO)
            book.setBookFinish(1);

//        if (TextUtils.isEmpty(book.getUserId())) {
//            DangUserInfo user = this.getCurrentUser();
//            if (user == null) {
//                book.setUserId(Constants.DANGDANG_DEFAULT_USER);
//                book.setUserName(Constants.DANGDANG_DEFAULT_USER);
//            } else {
//                book.setUserId(user.id);
//                book.setUserName(user.ddAccount);
//            }
//        }
        ShelfBook tmp = mService.saveOneBook(book);
        if (tmp == null) {
            mSet.add(book.getMediaId());
            if (isOrderByTime())
                mList.add(0, book);
            else
                mGroupList.get(0).list.add(0, book);
        } else {
            book = tmp;
            if (book.isUpdate()) {
                updateBookInList(book);
            }
            if (book.getBookFinish() == 1)
                return;
        }
        getKeyOrDownload(book, tag);
    }

    public void updateBookInList(ShelfBook newinfo) {
        if (isOrderByTime()) {
            for (int i = 0; i < mList.size(); i++) {
                if (update(mList.get(i), newinfo)) {
                    mList.set(i, newinfo);
                    return;
                }
            }
        } else {
            for (GroupItem group : mGroupList) {
                for (int i = 0; i < group.list.size(); i++) {
                    if (update(group.list.get(i), newinfo)) {
                        group.list.set(i, newinfo);
                        return;
                    }
                }
            }
        }
    }

    private boolean update(ShelfBook info, ShelfBook newinfo) {
        if (info.getMediaId().equals(newinfo.getMediaId())
                || info.getMediaId().equals(
                InbuildBooks.PUBLIC_KEY_PREFIX + "_" + newinfo.getMediaId())) {
            return true;
        }
        return false;
    }

    public boolean hasBookOnShelf(String bookId) {
        if (TextUtils.isEmpty(bookId))
            return false;
        return mSet.contains(bookId);
    }

    class ShelfReceiver extends BroadcastReceiver {

        public void init(Context context) {
            IntentFilter mRefreshFilter = new IntentFilter();
            mRefreshFilter.addAction(Constants.BROADCAST_REFRESH_BOOKLIST);        //本地导入
            mRefreshFilter.addAction(Constants.BROADCAST_DELETE_BOOK);            //wifi删除

            /**
             * 读书4.0全本和试读取证书是两个接口，5.0未知，暂不区分
             */
//			mRefreshFilter.addAction(Constants.BROADCAST_DOWNLOAD_BOOK);		//下载原创全本
//			mRefreshFilter.addAction(Constants.ACTION_DOWNLOAD_FULLREAD);		//下载读书全本
//			mRefreshFilter.addAction(Constants.ACTION_DOWNLOAD_TRYREAD);		//下载读书试读

            context.registerReceiver(this, mRefreshFilter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (Constants.BROADCAST_REFRESH_BOOKLIST.equals(intent.getAction())) {
                    addTxtBookListToShelf();
                } else if (Constants.BROADCAST_DELETE_BOOK.equals(intent.getAction())) {
                    String bookDir = intent.getStringExtra(ShelfBookDBColumn.BOOK_DIR);
                    deleteBookByDir(bookDir, true);
                }
//				else if (Constants.BROADCAST_DOWNLOAD_BOOK.equals(intent.getAction())
//						|| Constants.ACTION_DOWNLOAD_FULLREAD.equals(intent.getAction())
//						|| Constants.ACTION_DOWNLOAD_TRYREAD.equals(intent.getAction())) {
//					ShelfBook book = getShelfBook((ShelfBook) intent.getSerializableExtra("book"));
//					downloadBook(book, TAG);
//				}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getCertSuccess(ShelfBook book, RequestResult result) {
        // 取到key，开始下载
        book.setBookKey(DrmWrapUtil.getPartBookCertKey(result.getResult().toString()));
        mService.saveBookKey(book);
        if (book.getBookType() != BookType.BOOK_TYPE_IS_FULL_NO) {
            dealDownload(book);
        } else {
            startReadActivity(book, "", null);
        }
    }

    private void getCertError(ShelfBook book, RequestResult result) {
        book.setDownloadStatus(Status.UNSTART);
        Intent intent = new Intent(Constants.BROADCAST_REFRESH_LIST);
        intent.putExtra("bookId", book.getMediaId());
        mContext.sendBroadcast(intent);

        intent = new Intent(Constants.BROADCAST_DOWNLOAD_BOOK_FINISH);
        intent.putExtra("bookId", book.getMediaId());
        intent.putExtra("success", false);
        mContext.sendBroadcast(intent);

        UiUtil.showToast(mContext, result.getExpCode().errorMessage);
    }

    // 设置封面
    public void setCoverViewSrc(DDImageView view, ShelfBook book,
                                DDTextView tv, DDImageView label, DDImageView icon, String size, int id) {
        if (tv != null)
            tv.setText("");
        if (book.getMediaId().startsWith(InbuildBooks.PUBLIC_KEY_PREFIX)) {
            initInbuildBooksCover(view, book);
            if (icon != null)
                //icon 导入的扩展名
                icon.setVisibility(View.INVISIBLE);
            if (label != null) {
                if (book.getTryOrFull() == TryOrFull.INNER_TRY) {
                    label.setVisibility(View.VISIBLE);
                    label.setImageResource(R.drawable.ribbon_sample);
                } else
                    label.setVisibility(View.INVISIBLE);
            }
            return;
        } else if (DangdangFileManager.isImportBook(book.getMediaId())) {
            if (icon != null)
                icon.setVisibility(View.VISIBLE);
            if (label != null)
                label.setVisibility(View.INVISIBLE);
            initImportBookCover(book, view, tv, icon, size);
            return;
        } else {
            if (icon != null)
                icon.setVisibility(View.INVISIBLE);
            if (label != null) {
                label.setVisibility(View.VISIBLE);

                //非训练的书走之前流程
                if (book.getBookType() != BookType.BOOK_TYPE_NOT_NOVEL) {
                    //原创类型
                    if (book.getTryOrFull() == TryOrFull.MONTH_FULL)
                        label.setImageResource(R.drawable.month_flag);
                    else
                        label.setImageDrawable(null);
                } else {
                    // liuzhuo, add flags
                    TryOrFull type = book.getTryOrFull();
                    switch (type) {
                        case TRY:
                            if (book.getIsOthers()) {
                                label.setImageResource(R.drawable.ribbon_ta);
                            } else
                                label.setImageResource(R.drawable.ribbon_sample);
                            break;
                        case BORROW_FULL:
                            if (book.getOverDue() == 1) {
                                if (!book.canBorrow())
                                    label.setImageResource(R.drawable.overdue_flag);
                                else
                                    label.setImageResource(R.drawable.reborrow);
                            } else {
                                long time = getLastTime(book);
                                if (time > 0)
                                    label.setImageResource(R.drawable.borrow_flag);
                                else {
                                    if (!book.canBorrow())
                                        label.setImageResource(R.drawable.overdue_flag);
                                    else
                                        label.setImageResource(R.drawable.reborrow);
                                }
                            }
                            break;
                        case MONTH_FULL:
                            label.setImageResource(R.drawable.month_flag);
                            break;
                        case GIFT_FULL:
                            label.setImageResource(R.drawable.gift_flag);
                            break;
                        default:
                            label.setImageDrawable(null);
                    }
                }
            }
            String url = ImageConfig.getBookCoverBySize(book.getCoverPic(), size);
            ImageManager.getInstance().dislayImage(url, view, id);
        }
    }

    public long getLastTime(ShelfBook info) {
        if (info.getOverDue() == 1)
            return -1;
        long time = getLastTime(info.getBorrowStartTime(),
                info.getBorrowTotalTime());
        if (time < 0) {
            info.setOverDue(1);// 过期
            DDApplication ddApplication = (DDApplication) mContext;
            if (!ddApplication.isKeyExist(info.getMediaId())) {
                info.setOverDue(1);
                ddApplication.addValueToSet(info.getMediaId());
            }
        }
        return time;
    }

    public long getLastTime(long start, long duration) {
        if (Utils.isBorrowInvalidate(start, duration)) {
            return -1;
        } else {
            long last = start + duration;
            if (Utils.serverTime == 0) {
                last -= System.currentTimeMillis();
            } else {
                long tmp = System.currentTimeMillis()
                        - Utils.localTime;
                last -= Utils.serverTime;
                if (tmp > 0)
                    last -= tmp;
            }
            return last;
        }
    }

    public void initImportBookCover(ShelfBook book, DDImageView view,
                                    DDTextView tv, DDImageView icon, String size) {
        if (book.getMediaId().startsWith(DangdangFileManager.TXT_BOOK_ID_PRE)) {
            initTxtBookCover(book, view, tv, icon);
        } else if (book.getMediaId().startsWith(
                DangdangFileManager.EPUB_BOOK_THIRD_ID_PRE)) {
            initEpubBookCover(book, view, tv, icon);
        }
    }

    private void initEpubBookCover(ShelfBook book, DDImageView view,
                                   DDTextView tv, DDImageView icon) {
        if (icon != null) {
            icon.setVisibility(View.VISIBLE);
            icon.setBackgroundResource(R.drawable.epub_icon);
        }
        String path = getThirdEpubCoverCachePath(book.getBookDir());
        if (tv != null) {
            if (TextUtils.isEmpty(path)) {
                tv.setText(book.getTitle());
            } else {
                File file = new File(path);
                if (!file.exists())
                    tv.setText(book.getTitle());
            }
        }

        new ResourceManager().setCover(view, path, 1);
    }

    public static String getThirdEpubCoverCachePath(String bookDir) {
        return DangdangFileManager.getBookCachePath()
                + DROSUtility.getMd5(bookDir.getBytes());
    }

    private void initTxtBookCover(ShelfBook book, DDImageView view,
                                  DDTextView tv, DDImageView icon) {
        if (tv != null)
            tv.setText(book.getTitle());
        if (icon != null)
            icon.setBackgroundResource(R.drawable.txt);
        view.setImageResource(R.drawable.default_cover_blue);
    }

    private void initInbuildBooksCover(DDImageView view,
                                       ShelfBook book) {
        String path = "file://" + book.getBookDir() + "/" + DangdangFileManager.ITEM_BOOK_COVER;
        ImageManager.getInstance().dislayImage(path, view, R.drawable.default_cover);
    }

    public String getMyBookList() {
        int count = 0;
        try {
            if (!mConfig.checkTokenValid()) {
                return "";
            }
            JSONObject obj = new JSONObject();
            obj.put("token", mConfig.getToken());
            JSONArray array = new JSONArray();
            if (isOrderByTime()) {
                JSONArray carray;
                HashMap<String, Integer> map = new HashMap<String, Integer>();
                for (ShelfBook book : mList) {
                    if (DangdangFileManager.isImportBook(book.getMediaId()) || DangdangFileManager.isInbuildBook(book.getMediaId()))
                        continue;
                    if (book.getBookType() != BookType.BOOK_TYPE_NOT_NOVEL)
                        continue;
                    String group = book.getGroupType().getName();
                    if (TextUtils.isEmpty(group))
                        group = "dd_no_group";
                    if (map.containsKey(group)) {
                        int index = map.get(group);
                        carray = array.getJSONObject(index).getJSONArray("books");
                    } else {
                        carray = new JSONArray();
                        JSONObject json = new JSONObject();
                        json.put("books", carray);
                        json.put("categoryName", group);
                        json.put("lastChangedDate", book.getLastTime() / 1000);
                        map.put(group, array.length());
                        array.put(json);
                    }
                    JSONObject cjson = new JSONObject();
                    cjson.put("productId", book.getMediaId());
                    cjson.put("saleId", book.getSaleId());
                    cjson.put("lastChangedDate", book.getLastTime() / 1000);
                    carray.put(cjson);
                    if (++count >= 200)
                        break;
                }
            } else {
                for (GroupItem group : mGroupList) {
                    JSONObject json = new JSONObject();
                    JSONArray carray = new JSONArray();
                    for (ShelfBook info : group.list) {
                        if (DangdangFileManager.isImportBook(info.getMediaId()) || DangdangFileManager.isInbuildBook(info.getMediaId()))
                            continue;
                        if (info.getBookType() != BookType.BOOK_TYPE_NOT_NOVEL)
                            continue;
                        JSONObject cjson = new JSONObject();
                        cjson.put("productId", info.getMediaId());
                        cjson.put("saleId", info.getSaleId());
                        cjson.put("lastChangedDate", info.getLastTime() / 1000);
                        carray.put(cjson);
                        if (++count >= 200)
                            break;
                    }
                    if (carray.length() == 0)
                        continue;

                    if (group.type.getId() == Constants.UNKNOW_TYPE || TextUtils.isEmpty(group.type.getName()))
                        json.put("categoryName", "dd_no_group");
                    else
                        json.put("categoryName", group.type.getName());
                    json.put("lastChangedDate", group.type.getCreateTime() / 1000);
                    json.put("books", carray);
                    array.put(json);
                }
            }
            // 结果
            if (array.length() > 0) {
                obj.put("data", array);
                return obj.toString();
            } else
                return "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 保存已购列表，同时更新书架借阅为已购
     *
     * @param list
     */
    public void saveBuyBookList(List<ShelfBook> list) {
        HashMap<String, ShelfBook> onShelf = new HashMap<String, ShelfBook>();
        // 缓存书架列表中的书
        for (int i = list.size() - 1; i >= 0; i--) {
            ShelfBook book = list.get(i);
            // 去掉不支持的书
            if (book.getBookType() == BookType.BOOK_TYPE_NOT_NOVEL && book.isValid == 0) {
                list.remove(i);
                continue;
            }
            if (hasBookOnShelf(book.getMediaId())) {
                ShelfBook sb = getShelfBookFromList(book.getMediaId());
                onShelf.put(sb.getMediaId(), sb);
            }
        }
        List<String> result = BuyBookService.getInstance(mContext).saveBuyBooks(list);
        // 更新书架列表的借阅状态为已购
        for (String id : result) {
            ShelfBook sb = onShelf.remove(id);
            if (sb != null)
                sb.setTryOrFull(TryOrFull.FULL);
        }
    }

    public ShelfBook getLastBook() {
        List<ShelfBook> list = getLastBook(1);
        if (list == null || list.isEmpty())
            return null;
        return list.get(0);
    }

    public List<ShelfBook> getLastBook(int num) {
        if (mService != null)
            return mService.getLastBook(num);
        return null;
    }

//    public boolean isOpenBuy() {
//        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Activity.ACTIVITY_SERVICE);
//        List<RunningTaskInfo> list = activityManager.getRunningTasks(20);
//        for (RunningTaskInfo info : list) {
//            if (info.topActivity.getPackageName().equals(mContext.getPackageName())) {
//                if (info.topActivity.getShortClassName().endsWith("ShelfCloudActivity"))
//                    return true;
//                return false;
//            }
//        }
//        return false;
//    }

    public String getBuyGroupName(String str) {
        if ("1001".equals(str))
            str = "已购";
        else if ("1002".equals(str))
            str = "免费";
        else if ("1004".equals(str))
            str = "赠书";
        else if ("2000".equals(str))
            str = "试读";
        return str;
    }



    private static class MyHandler extends Handler {
        private final WeakReference<DataUtil> mFragmentView;

        MyHandler(DataUtil view) {
            this.mFragmentView = new WeakReference<DataUtil>(
                    view);
        }

        @Override
        public void handleMessage(Message msg) {
            DataUtil service = mFragmentView.get();
            if (service != null) {
                super.handleMessage(msg);
                try {
                    switch (msg.what) {
                        case Constants.MSG_WHAT_GETCERT_SUCCESS:
                            service.getCertSuccess((ShelfBook) msg.getData().getSerializable("book"), (RequestResult) msg.obj);
                            break;
                        case Constants.MSG_WHAT_GETCERT_FAILED:
                            service.getCertError((ShelfBook) msg.getData().getSerializable("book"), (RequestResult) msg.obj);
                            break;
                        case RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS: {
                        }
                        break;
                        case RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL: {
                        }
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
