package com.dangdang.reader.dread.util;

import android.content.Context;
import android.text.TextUtils;

import com.dangdang.reader.dread.data.PartReadInfo;

/**
 * 阅读器内部工具类，处理跟书相关的业务
 * Created by Yhyu on 2015/5/25.
 */
public class ReadBookUtil {
    public static boolean addBook2Shelf(Context context, PartReadInfo readInfo, boolean isBuy, boolean isDownload) {
        boolean result = false;
//        ShelfBook book = new ShelfBook();
//        book.setSaleId(readInfo.getSaleId());
//        book.setMediaId(readInfo.getDefaultPid());
//        book.setTitle(readInfo.getBookName());
//        book.setBookDir(readInfo.getBookDir());
//        book.setCoverPic(readInfo.getInternetBookCover());
//        book.setDescs(readInfo.getBookDesc());
//        book.setAuthorPenname(readInfo.getBookAuthor());
//        book.setBookFinish(1);
//        book.setFollow(false);
////        book.setFollow(readInfo.isFollow());
//        book.setReadProgress(readInfo.buildProgressInfo(false));
//        book.setBookKey(readInfo.getBookCertKey());
//        PartBook partBook = (PartBook) ReaderAppImpl.getApp().getBook();
//        if (partBook != null && partBook.getChapterList() != null) {
//            book.setLocalLastIndexOrder(partBook.getChapterList().size() - 1);
//            book.setServerLastIndexOrder(partBook.getChapterList().size() - 1);
//        }
//        book.setCategorys(readInfo.getBookCategories());
//
//        DangUserInfo info = DataUtil.getInstance(context).getCurrentUser();
//        if (info != null) {
//            book.setUserId(info.id);
//            book.setUserName(info.ddAccount);
//        } else {
//            book.setUserId(Constants.DANGDANG_DEFAULT_USER);
//            book.setUserName(Constants.DANGDANG_DEFAULT_USER);
//        }
//
//        GroupType type = new GroupType();
//        type.setName(book.getCategorys());
//        book.setGroupType(type);
//        if (isBuy) {
//            book.setTryOrFull(ShelfBook.TryOrFull.FULL);
//            readInfo.setBought(true);
//            readInfo.setTryOrFull(ShelfBook.TryOrFull.FULL.ordinal());
//        } else
//            book.setTryOrFull(ShelfBook.TryOrFull.TRY);
//        if (readInfo.isFull()) {
//            book.setBookType(BookType.BOOK_TYPE_IS_FULL_YES);
//        } else {
//            book.setBookType(BookType.BOOK_TYPE_IS_FULL_NO);
//        }
//
//        book.setGroupId(Constants.UNKNOW_TYPE);
//        book.setLastTime(System.currentTimeMillis());
//        DangUserInfo user = DataUtil.getInstance(context).getCurrentUser();
//        if (user == null) {
//            book.setUserId(Constants.DANGDANG_DEFAULT_USER);
//            book.setUserName(Constants.DANGDANG_DEFAULT_USER);
//        } else {
//            book.setUserId(user.id);
//            book.setUserName(user.ddAccount);
//        }
//
//        if (isDownload) {
//            book.setLastTime(System.currentTimeMillis());
//            book.setBookKey(null);
//            File f = DownloadBookHandle.getHandle(context).getBookDest(true, book.getMediaId(), book.getBookType());
//            book.setBookDir(f.getParent());
//            book.setBookFinish(0);
//            DataUtil.getInstance(context).downloadBook(book, "ReadBookUtil");
//        } else {
//            ShelfBook tmp = ShelfBookService.getInstance(context).saveOneBook(book);
//            if (tmp == null) {
//                result = true;
//                List<ShelfBook> list = new ArrayList<ShelfBook>();
//                list.add(book);
//                DDApplication ddApplication = (DDApplication) (context.getApplicationContext());
//                ddApplication.setmImportBookList(list);
//
//                Intent tent = new Intent(Constants.BROADCAST_REFRESH_BOOKLIST);
//                tent.setPackage(context.getPackageName());
//                context.sendBroadcast(tent);
//                if (DataUtil.getInstance(context).getCurrentUser() != null)
//                    readInfo.setIsFollow(true);
//                updateFollowStatus(context, readInfo.getDefaultPid(), readInfo.isFollow());
//            }
//        }
        return result;
    }

    public static boolean addBook2Shelf(Context context, PartReadInfo readInfo) {
        return addBook2Shelf(context, readInfo, false, false);
    }

//    private static void updateFollowStatus(Context context, String bookId, boolean isFollow) {
//        DataUtil util = DataUtil.getInstance(context);
//        ShelfBook sb = ShelfBookService.getInstance(context).getShelfBookById(bookId);
//        if (sb != null && sb.isFollow() != isFollow) {
//            List<ShelfBook> list = new ArrayList<ShelfBook>();
//            list.add(sb);
//            util.updateFollowStatus(list, isFollow);
//        }
//    }

    /**
     * 去书吧
     *
     * @param context
     * @param defaultPid 书id
     * @param toJoin     是否发帖
     * @param bookName   书名称
     * @param type       类型   //1.原创  2出版物  3 纸书
     */
    public static void startBar(Context context, String defaultPid, boolean toJoin, String bookName, int type) {
        //内置书
        if (TextUtils.isEmpty(defaultPid))
            return;
        defaultPid = defaultPid.replace("has_key_", "");
    }
}
