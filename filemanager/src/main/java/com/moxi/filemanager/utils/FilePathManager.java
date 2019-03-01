package com.moxi.filemanager.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;

import com.mx.mxbase.utils.LocationPhotoLoder;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 文件路径管理器
 * Created by xj on 2017/9/18.
 */

public class FilePathManager {

    /**
     * 图片缓存的核心类
     */
    private LruCache<String, File[]> mLruCache;
    /**
     * 线程�?
     */
    private ExecutorService mThreadPool;
    /**
     * 线程池的线程数量，默认为1
     */
    private int mThreadCount = 1;
    /**
     * 队列的调度方�?
     */
    private LocationPhotoLoder.Type mType = LocationPhotoLoder.Type.LIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTasks;
    /**
     * 轮询的线�?
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHander;

    /**
     * 运行在UI线程的handler，用于给ImageView设置图片
     */
    private Handler mHandler;

    /**
     * 引入�?��值为1的信号量，防止mPoolThreadHander未初始化完成
     */
    private volatile Semaphore mSemaphore = new Semaphore(0);

    /**
     * 引入�?��值为1的信号量，由于线程池内部也有�?��阻塞线程，防止加入任务的速度过快，使LIFO效果不明�?
     */
    private volatile Semaphore mPoolSemaphore;

    private static FilePathManager mInstance;

    /**
     * 队列的调度方�?
     *
     * @author zhy
     */
    public enum Type {
        FIFO, LIFO
    }


    /**
     * 单例获得该实例对�?
     *
     * @return
     */
    public static FilePathManager getInstance() {

        if (mInstance == null) {
            synchronized (LocationPhotoLoder.class) {
                if (mInstance == null) {
                    mInstance = new FilePathManager(3, LocationPhotoLoder.Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    private FilePathManager(int threadCount, LocationPhotoLoder.Type type) {
        init(threadCount, type);
    }

    private void init(int threadCount, LocationPhotoLoder.Type type) {
        // loop thread
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();

                mPoolThreadHander = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        mThreadPool.execute(getTask());
                        try {
                            mPoolSemaphore.acquire();
                        } catch (InterruptedException e) {
                        }
                    }
                };
                // 释放�?��信号�?
                mSemaphore.release();
                Looper.loop();
            }
        };
        mPoolThread.start();

        // 获取应用程序�?��可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, File[]>(cacheSize) {
            @Override
            protected int sizeOf(String key, File[] value) {
                return value.length;
            }
        };

        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mPoolSemaphore = new Semaphore(threadCount);
        mTasks = new LinkedList<Runnable>();
        mType = type == null ? LocationPhotoLoder.Type.LIFO : type;

    }

    /**
     * 加载文件列表
     * @param path 文件路径
     * @param sortType 文件排序方式
     * @param lodingListener  文件加载回调监听
     */
    public void loadFiles(final String path, int sortType,final FileLodingListener lodingListener) {
        // set tag
        Handler mHandler = null;
        // UI线程
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {


                }
            };
        }
        File file=new File(path);
        File[] bm = getBitmapFromLruCache(path);
        if (bm != null) {
            Message message = Message.obtain();
            message.obj = bm;
            mHandler.sendMessage(message);
        } else {
            final Handler finalMHandler = mHandler;
            addTask(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

    }

    /**
     * 添加�?��任务
     *
     * @param runnable
     */
    private synchronized void addTask(Runnable runnable) {
        try {
            // 请求信号量，防止mPoolThreadHander为null
            if (mPoolThreadHander == null)
                mSemaphore.acquire();
        } catch (InterruptedException e) {
        }
        mTasks.add(runnable);

        mPoolThreadHander.sendEmptyMessage(0x110);
    }

    /**
     * 取出执行任务
     *
     * @return
     */
    private synchronized Runnable getTask() {
        if (mType == LocationPhotoLoder.Type.FIFO) {
            return mTasks.removeFirst();
        } else if (mType == LocationPhotoLoder.Type.LIFO) {
            return mTasks.removeLast();
        }
        return null;
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null�?
     */
    private File[] getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }

    /**
     * �?ruCache中添加一张图�?
     *
     * @param key
     * @param files
     */
    private void addBitmapToLruCache(String key, File[] files) {
        if (getBitmapFromLruCache(key) == null) {
            if (files != null)
                mLruCache.put(key, files);
        }
    }


    public void clearCatch(String path){
        mLruCache.remove(path);
    }

    /**
     * 判断文件是否存在
     * @param path 文件路径
     * @return 文件存在为true
     */
    public boolean isFileExit(String path){
        try {
            File file=new File(path);
            File[] files=file.listFiles();
            return files!=null;
        }catch (Exception e){
            return false;
        }
    }

    public interface FileLodingListener{
        void onLodingFinish(File[] files);
        void  onLodingFail();
    }
}

