package com.dangdang.reader.dread.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.dangdang.reader.dread.core.epub.ControllerWrapperImpl;
import com.dangdang.reader.dread.holder.PageBitmap;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.BitmapUtil;

/**
 * 阅读页缓存
 *
 * @author Yhyu
 * @date 2014-11-29 下午4:09:33
 */
public class PageBitmapCache extends LruCache<ControllerWrapperImpl.PageIndexKey, PageBitmap> {
    private static PageBitmapCache instance;

    private PageBitmapCache() {
        this(getDefaultLruCacheSize());
    }

    private PageBitmapCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(ControllerWrapperImpl.PageIndexKey key, PageBitmap value) {
        return 1;
    }

    private static int getDefaultLruCacheSize() {
        return 5;
    }

    public synchronized static PageBitmapCache getInstance() {
        if (instance == null) {
            instance = new PageBitmapCache();
        }
        return instance;
    }

    public synchronized PageBitmap getPageBitmap(ControllerWrapperImpl.PageIndexKey key) {
        PageBitmap pageBitmap = get(key);
        printLog("getPageBitmap key = " + key);
        printLog("getPageBitmap bitmap-" + pageBitmap);
        if (pageBitmap != null) {
            Bitmap bitmap = pageBitmap.getBitmap();
            if (BitmapUtil.isAvailable(bitmap))
                return pageBitmap;
            remove(key);
        }
        return null;
    }

    public synchronized void putPageBitmap(ControllerWrapperImpl.PageIndexKey key, PageBitmap pageBitmap) {
        printLog("putPageBitmap key = " + key);
        printLog("putPageBitmap bitmap-" + pageBitmap);
        if (pageBitmap != null) {
            Bitmap bitmap = pageBitmap.getBitmap();
            if (BitmapUtil.isAvailable(bitmap))
                put(key, pageBitmap);
        }
    }

    @Override
    protected void entryRemoved(boolean evicted, ControllerWrapperImpl.PageIndexKey key, PageBitmap oldValue, PageBitmap newValue) {
        // TODO Auto-generated method stub
        super.entryRemoved(evicted, key, oldValue, newValue);
        if (oldValue != null) {
            printLog("entryRemoved key = " + key);
            printLog("old = " + oldValue);
            printLog("new = " + newValue);
            oldValue.free();
        }
    }

    public synchronized void clear() {
        evictAll();
    }

    public synchronized void release() {
        evictAll();
        instance = null;
        System.gc();
    }

    private void printLog(String msg) {
        LogM.d(getClass().getSimpleName(), msg);

    }
}
