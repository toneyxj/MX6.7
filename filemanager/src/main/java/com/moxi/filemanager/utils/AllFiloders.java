package com.moxi.filemanager.utils;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 获得处理后的所有文件夹路径
 * Created by Administrator on 2016/8/31.
 */
public class AllFiloders extends AsyncTask<String, Void, List<String>> {
    private AllFiloderListener back;// 传入接口
    private List<File> folders;// 查询文件夹
    private WeakReference<Context> context;
    private List<String> filePaths = new ArrayList<>();
    private String checkFloder;


    /**
     * @param context
     * @param files       查询文件夹集合
     * @param checkFloder 扫描的主文件夹目录
     * @param back        接口返回结果
     */
    public AllFiloders(Context context, List<File> files, String checkFloder, AllFiloderListener back) {
        this.back = back;
        this.folders = files;
        this.context = new WeakReference<Context>(context);
        this.checkFloder = checkFloder;

        for (File file : files) {
            filePaths.add(file.getAbsolutePath());
        }
    }

    private Context isFinish() {
        Context context = this.context.get();
        return context;
    }

    @Override
    protected List<String> doInBackground(String... arg0) {
        //获得所有目录的觉得路径
        List<String> alls = getDocuemntFiles(new File(checkFloder));
        if (!(folders.get(0).getParent()).equals(checkFloder)) {
            alls.add(0, checkFloder+"/");
        }
        //先按创建事件排序
        Collections.sort(alls, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                File file1 = new File(lhs);
                File file2 = new File(rhs);
                if (file1.lastModified() < file2.lastModified()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        //文件排序
        Collections.sort(alls, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                int lL = lhs.split("/").length;
                int rL = rhs.split("/").length;
                if (lL > rL) {
                    return 1;
                } else if (lL < rL) {
                    return -1;
                }
                return 0;
            }
        });


        return alls;
    }

    private List<String> getDocuemntFiles(File file) {
        List<String> listdata = new ArrayList<>();
        if (file.getName().substring(0, 1).equals(".")) return listdata;
        if (filePaths.contains(file.getAbsolutePath())) return listdata;


        File[] files = file.listFiles();
        if (null != files)
            for (int i = 0; i < files.length; i++) {
                File son = files[i];
                if (son.isDirectory()) {

                    if (son.getName().substring(0, 1).equals(".")||son.getName().equals("com.onyx.android.data")) continue;
                    if (ExceptionFile.getIscontains(son.getAbsolutePath()))continue;

                    if (filePaths.contains(son.getAbsolutePath())) continue;

                    if (!folders.get(0).getParent().equals(son.getAbsolutePath())) {
                        listdata.add(son.getAbsolutePath());
                    }
                    listdata.addAll(getDocuemntFiles(son));
                }
            }
        return listdata;
    }

    @Override
    protected void onPostExecute(List<String> result) {// 在doInBackground执行完成后系统会自动调用，result是返回值
        if (isFinish() != null)
            back.folderSucess(result);
    }

    /**
     * Created by Administrator on 2016/4/1.
     */
    public interface AllFiloderListener {
        /*
         *
         * 图片处理成功
         */
        public void folderSucess(List<String> results);
    }
}