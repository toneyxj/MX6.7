package com.moxi.bookstore.asy;

import android.os.Handler;
import android.os.Message;

import com.moxi.bookstore.db.SacnReadFileUtils;
import com.moxi.bookstore.modle.BookStoreFile;
import com.moxi.bookstore.modle.SearchBookModel;
import com.moxi.bookstore.utils.SearchFileUtils;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.PhotoConfig;
import com.mx.mxbase.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xj on 2017/6/15.
 */

public class ScanReadFile implements Runnable {
    private List<String> fileTypes= PhotoConfig.getAllFileType();
    private int size=0;

    private List<SearchBookModel> listSdcard;
    private boolean isRun=true;

    private ScanReadListner listner;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (listner==null)return;
            switch (msg.what){
                case 0:
//                    listner.onScanReading();
                    break;
                case 1:
                    listner.onScanReadFile((BookStoreFile) msg.obj);
                    break;
                case 2:
                    listner.onScanReadEnd();
                    break;
            }
        }
    };

    public void setRun(boolean run) {
        isRun = run;
    }

//    public ScanReadFile(List<String> listSdcard, ScanReadListner listner) {
//        this.listner=listner;
//        this.listSdcard=listSdcard;
//    }

    public ScanReadFile( ScanReadListner listner) {
        this.listner=listner;
        this.listSdcard= SearchFileUtils.getInstance().getFils();
    }

    private void searchFile(SearchBookModel model) {
        File root=new File(model.filePath);
        if (!isRun||!root.exists()) return ;
        String rootPath = root.getAbsolutePath();
        String ex1 = StringUtils.getSDCardPath() + "/mx_exams";
        String ex2 = StringUtils.getSDCardPath() + "/exams";
        String ex3 = StringUtils.getSDCardPath() + "/Exams";

        if (rootPath.equals(ex1) || rootPath.equals(ex2) || rootPath.equals(ex3)||rootPath.contains("-Exported.pdf")) {
            return ;
        }
        File[] files = root.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.getName().startsWith("."))continue;
            if (!isRun)return;
            if (file.isDirectory() && file.canRead()) {//判断是否为文件夹并为keduwenjian
                if (model.addType==0) {
                    searchFile(new SearchBookModel().init(file.getAbsolutePath()));
                }
            } else {
                //文件长度为0的不读入
                if (file.length()==0||!file.canRead())continue;
//                String fileName=file.getName().toLowerCase();
                String prefix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                prefix = prefix.toLowerCase();
                if (fileTypes.contains(prefix)) {
                    BookStoreFile file1=SacnReadFileUtils.getInstance(null).saveMode(file.getAbsolutePath());
                    if (file1!=null) {
                        Message message = new Message();
                        message.what = 1;
                        message.obj =file1;
                        handler.sendMessage(message);
                    }
                }
            }
        }
    }

    private List<String> searchFile(File  root) {
        List<String> list=new ArrayList<>();
        if (!isRun||!root.exists()) return list;
        File[] files = root.listFiles();
        if (files == null) {
            return list;
        }
        for (File file : files) {
            if (file.isDirectory() && file.canRead()) {//判断是否为文件夹并为keduwenjian
                list.addAll(searchFile(file));
            } else {
                list.add(file.getAbsolutePath());
            }
        }
        return list;
    }
    @Override
    public void run() {
        handler.sendEmptyMessage(0);
            for (SearchBookModel path : listSdcard) {
                APPLog.e("searchBookPath="+path.toString());
                searchFile(path);
//                searchFile(new File(path + File.separator + "Books"));
            }
        handler.sendEmptyMessage(2);
        }

   public interface ScanReadListner{
//        void onScanReading();
        void onScanReadEnd();

        /**
         * 读取了一个文件
         * @param file
         */
        void onScanReadFile(BookStoreFile file);
    }
}
