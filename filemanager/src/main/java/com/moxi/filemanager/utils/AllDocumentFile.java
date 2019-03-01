package com.moxi.filemanager.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.moxi.filemanager.model.FileModel;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.constant.PhotoConfig;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取文件夹文件
 */
public class AllDocumentFile extends AsyncTask<String, Void, List<FileModel>> {
    private DocumentFileListener back;// 传入接口
    private String floder;// 查询文件夹
    private WeakReference<Context> context;
    private  List<String> filetypes=PhotoConfig.getAllFileType();


    /**
     * 请求构造方法
     */
    public AllDocumentFile(Context context, String floder, DocumentFileListener back) {
        this.back = back;
        this.floder = floder;
        this.context = new WeakReference<Context>(context);
    }

    private Context isFinish() {
        Context context = this.context.get();
        return context;
    }

    @Override
    protected List<FileModel> doInBackground(String... arg0) {
        return getAllDocuemntFiles(new File(floder));
    }

    private List<FileModel> getAllDocuemntFiles(File file) {
//        List<File> listdata = new ArrayList<>();
        List<FileModel> listdata = new ArrayList<>();
        if (file==null){
            MyApplication.Toast("没有找到内部存储卡！！");
            return listdata;
        }
        if (file.getName().isEmpty())return listdata;
        if (file.getName().substring(0,1).equals("."))return listdata;
        File[] files = file.listFiles();
        if (null!=files)
        for (int i = 0; i < files.length; i++) {
            File son = files[i];
            if (ExceptionFile.getIscontains(son.getAbsolutePath()))continue;

            if (son.isDirectory()) {
                listdata.addAll(getAllDocuemntFiles(son));
            } else if (son.canRead()){

                String filename = son.getName();
                String prefix = filename.substring(filename.lastIndexOf(".") + 1);
                prefix = prefix.toLowerCase();
                if (filetypes.contains(prefix)) {
                    FileModel model = new FileModel(files[i]);
                    listdata.add(model);
                }

            }
        }
        return listdata;
    }

    @Override
    protected void onPostExecute(List<FileModel> result) {// 在doInBackground执行完成后系统会自动调用，result是返回值
        if (isFinish() != null)
            back.getFileSucess(result);
    }

    /**
     * Created by Administrator on 2016/4/1.
     */
    public interface DocumentFileListener {
        /*
         *
         * 图片处理成功
         */
        public void getFileSucess(List<FileModel> results);
    }
}