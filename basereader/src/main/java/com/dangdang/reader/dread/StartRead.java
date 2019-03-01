package com.dangdang.reader.dread;

import android.content.Context;
import android.content.Intent;

import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.util.DrmWrapUtil;
import com.dangdang.reader.dread.util.IntentK;
import com.dangdang.reader.moxiUtils.ExternalFile;
import com.dangdang.reader.utils.DangdangFileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


/**
 * 打开阅读接口封装
 *
 * @author luxu
 */
public class StartRead {

    /**
     * 参数获取请参考 DialogForOldData.readBook() 或者  咨询刘卓
     * 保留原有阅读的参数
     */

    public void startRead(Context context, ReadParams params) {

        if (context == null) {
            throw new NullPointerException(" context not null ");
        }
        if (params == null) {
            throw new NullPointerException(" params not null ");
        }

        Class<?> rClazz = ReadMainActivity.class;

        Intent intent = new Intent(context, rClazz);
        intent.putExtra(IntentK.BookCertKey, params.getBookCertKey());
        initIntent(intent, params);
        context.startActivity(intent);
    }

    /**
     * 新增一些参数，分章阅读使用
     */
    public void startPartRead(Context context, PartReadParams params) {

        if (context == null) {
            throw new NullPointerException(" context not null ");
        }
        if (params == null) {
            throw new NullPointerException(" params not null ");
        }

        Class<?> rClazz = ReadMainActivity.class;
        Intent intent = new Intent(context, rClazz);
        initIntent(intent, params);
        intent.putExtra(IntentK.SaleId, params.getSaleId());

        intent.putExtra(IntentK.Cover, params.getBookCover());
        intent.putExtra(IntentK.Category, params.getBookCategories());
        intent.putExtra(IntentK.IsFollow, params.isFollow());
        intent.putExtra(IntentK.IndexOrder, params.getIndexOrder());
        intent.putExtra(IntentK.IsFull, params.isFull());
        intent.putExtra(IntentK.IsSurpportFull, params.isSurpportFull());
        intent.putExtra(IntentK.TargetChapterId, params.getTargetChapterId());
        intent.putExtra(IntentK.BookCertKey, params.getBookCertKey());
        context.startActivity(intent);
    }

    public void startOffPrintRead(Context context) {

        if (context == null) {
            throw new NullPointerException(" context not null ");
        }

        Class<?> rClazz = ReadMainActivity.class;
        Intent intent = new Intent(context, rClazz);
        intent.putExtra(IntentK.ProductId, ReadConfig.PreSet_OffPrint_ProductId);
        intent.putExtra(IntentK.BookName, ReadConfig.bookName);
        if (ReadConfig.bPart) {
            //TODO ??
            intent.putExtra(IntentK.BookDir, DangdangFileManager.getPreSetOffPrintDir());
            intent.putExtra(IntentK.BookEpub, DangdangFileManager.getPreSetOffPrintDir());
            intent.putExtra(IntentK.BookType, BaseJniWarp.BOOKTYPE_DD_DRM_HTML);
        }
        else {
            intent.putExtra(IntentK.BookDir, DangdangFileManager.getPreSetOffPrintDir());
            intent.putExtra(IntentK.BookEpub, DangdangFileManager.getPreSetOffPrintEpubBook());
            intent.putExtra(IntentK.BookType, BaseJniWarp.BOOKTYPE_DD_DRM_EPUB);
        }
//        intent.putExtra(IntentK.IsBought, params.isBought());
//        intent.putExtra(IntentK.ReadProgress, params.getReadProgress());
//        intent.putExtra(IntentK.FileType, params.getFileType());
//        intent.putExtra(IntentK.Author, params.getBookAuthor());
//        intent.putExtra(IntentK.Desc, params.getBookDesc());
//        intent.putExtra(IntentK.SaleId, params.getSaleId());
//
//        intent.putExtra(IntentK.Cover, params.getBookCover());
//        intent.putExtra(IntentK.Category, params.getBookCategories());
//        intent.putExtra(IntentK.IsFollow, params.isFollow());
//        intent.putExtra(IntentK.IndexOrder, params.getIndexOrder());
//        intent.putExtra(IntentK.IsFull, params.isFull());
//        intent.putExtra(IntentK.IsSurpportFull, params.isSurpportFull());
//        intent.putExtra(IntentK.TargetChapterId, params.getTargetChapterId());
        intent.putExtra(IntentK.BookCertKey, DrmWrapUtil.getPartBookCertKey(getBookCert(DangdangFileManager.getPreSetOffPrintCert())));
        context.startActivity(intent);
    }
    public void startExteralFile(Context context,ExternalFile file){
        Class<?> rClazz = ReadMainActivity.class;
        Intent intent = new Intent(context, rClazz);
        intent.putExtra(IntentK.ProductId, file.id);
        intent.putExtra(IntentK.BookName, file.bookName);

        intent.putExtra(IntentK.BookDir, file.filePath);
        intent.putExtra(IntentK.BookEpub,  file.filePath);
        intent.putExtra(IntentK.BookType,  file.getFileType());
//        intent.putExtra(IntentK.BookCertKey, DrmWrapUtil.getPartBookCertKey(getBookCert(file.filePath)));
        context.startActivity(intent);
    }

    private String getBookCert(String filePath) {
        File file = new File(filePath);
        FileInputStream fis = null;
        ObjectInputStream inStream = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            int ret = fis.read(buffer);
            if(ret > -1){
                String cert = new String(buffer, "utf-8");
                return cert;
            }
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

        return "";
    }

    /**
     * 初始化公共部分
     *
     * @param intent
     * @param params
     */
    private void initIntent(Intent intent, ReadParams params) {
        intent.putExtra(IntentK.ProductId, params.getBookId());
        intent.putExtra(IntentK.BookName, params.getBookName());
        intent.putExtra(IntentK.BookDir, params.getBookDir());
        intent.putExtra(IntentK.BookEpub, params.getBookFile());
        intent.putExtra(IntentK.BookType, params.getBookType());
        intent.putExtra(IntentK.IsBought, params.isBought());
        intent.putExtra(IntentK.ReadProgress, params.getReadProgress());
        intent.putExtra(IntentK.FileType, params.getFileType());
        intent.putExtra(IntentK.Author, params.getBookAuthor());
        intent.putExtra(IntentK.Desc, params.getBookDesc());
//        APPLog.e("IntentK.ProductId="+params.getBookId());
//        APPLog.e("IntentK.BookName="+params.getBookName());
//        APPLog.e("IntentK.BookDir="+params.getBookDir());
//        APPLog.e("IntentK.BookEpub="+params.getBookFile() );
//        APPLog.e("IntentK.BookType="+params.getBookType() );
//        APPLog.e("IntentK.IsBought="+params.isBought() );
//        APPLog.e("IntentK.ReadProgress="+params.getReadProgress());
//        APPLog.e("IntentK.FileType="+params.getFileType());
//        APPLog.e("IntentK.Author="+params.getBookAuthor());
//        APPLog.e("IntentK.Desc="+params.getBookDesc());
//        APPLog.e(params.getBookId()+":开始阅读进度="+params.getReadProgress());
    }

    public static class PartReadParams extends ReadParams {
        private String saleId;

        private String bookCategories;
        private boolean isFollow;
        private boolean isFull;
        private boolean isSurpportFull;
        private boolean isPreload;
        private int indexOrder;
        private int targetChapterId;

        public int getTargetChapterId() {
            return targetChapterId;
        }

        public void setTargetChapterId(int targetChapterId) {
            this.targetChapterId = targetChapterId;
        }

        public String getSaleId() {
            return saleId;
        }

        public void setSaleId(String saleId) {
            this.saleId = saleId;
        }

        public String getBookCategories() {
            return bookCategories;
        }

        public void setBookCategories(String bookCategories) {
            this.bookCategories = bookCategories;
        }

        public boolean isFollow() {
            return isFollow;
        }

        public void setIsFollow(boolean isFollow) {
            this.isFollow = isFollow;
        }

        public int getIndexOrder() {
            return indexOrder;
        }

        public void setIndexOrder(int indexOrder) {
            this.indexOrder = indexOrder;
        }

        public boolean isFull() {
            return isFull;
        }

        public void setIsFull(boolean isFull) {
            this.isFull = isFull;
        }

        public boolean isSurpportFull() {
            return isSurpportFull;
        }

        public void setIsSurpportFull(boolean isSurpportFull) {
            this.isSurpportFull = isSurpportFull;
        }
        
        public boolean isPreload(){
        	return isPreload;
        }
        
        public void setPreload(boolean bo){
        	isPreload = bo;
        }
    }

    /**
     * 进入阅读需要的参数
     *
     * @author luxu
     */
    public static class ReadParams {


        public final static int FTYPE_EPUB = 0X1;
        public final static int FTYPE_TXT = 0X2;
        public final static int FTYPE_PDF = 0X3;
        public final static int FTYPE_PART = 0X4;
        public final static int FTYPE_COMICS = 0X5;
        /**
         * 书id
         */
        private String bookId;
        /**
         * 书名
         */
        private String bookName;
        /**
         * 书的全路径
         */
        private String bookFile;
        /**
         * 书所在目录，可选
         */
        private String bookDir;
        /**
         * 书籍类型
         */
        private int bookType;
        /**
         * 阅读进度：通过 ShelfBookInfo.getmReadProgress()获取
         */
        private String readProgress;
        private boolean isBought = false;//是否为试读
        private int fileType = FTYPE_EPUB;
        private String bookDesc;
        private byte[] bookCertKey;

        private String bookAuthor;

        public byte[] getBookCertKey() {

            return bookCertKey;
        }

        public void setBookCertKey(byte[] bookCertKey) {
            this.bookCertKey = bookCertKey;
        }

        public String getBookAuthor() {
            return bookAuthor;
        }

        public void setBookAuthor(String bookAuthor) {
            this.bookAuthor = bookAuthor;
        }

        public String getBookId() {
            return bookId;
        }

        public void setBookId(String bookId) {
            this.bookId = bookId;
        }

        public String getBookName() {
            return bookName;
        }

        public void setBookName(String bookName) {
            this.bookName = bookName;
        }

        public String getBookDir() {
            return bookDir;
        }

        public void setBookDir(String bookDir) {
            this.bookDir = bookDir;
        }

        public String getBookFile() {
            return bookFile;
        }

        public void setBookFile(String bookFile) {
            this.bookFile = bookFile;
        }

        public int getBookType() {
            return bookType;
        }

        public void setBookType(int bookType) {
            this.bookType = bookType;
        }

        public String getReadProgress() {
            return readProgress;
        }

        public void setReadProgress(String readProgress) {
            this.readProgress = readProgress;
        }

        public int getFileType() {
            return fileType;
        }

        public void setFileType(int fileType) {
            this.fileType = fileType;
        }

        public boolean isBought() {
            return isBought;
        }

        public void setIsBought(boolean isBought) {
            this.isBought = isBought;
        }

        public boolean isPdf() {
            return getFileType() == FTYPE_PDF;
        }

        private String bookCover;

        public String getBookCover() {
            return bookCover;
        }

        public void setBookCover(String bookCover) {
            this.bookCover = bookCover;
        }

        public String getBookDesc() {
            return bookDesc;
        }

        public void setBookDesc(String bookDesc) {
            this.bookDesc = bookDesc;
        }
    }

}
