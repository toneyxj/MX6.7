package com.moxi.filemanager.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.moxi.filemanager.model.FileModel;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.StringUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 获得所有的图片文件file集合
 * Created by Administrator on 2016/8/30.
 */
public class AllImageFile extends AsyncTask<String, Void, List<FileModel>> {
    private ImageFileListener back;// 传入接口
    private String floder;// 查询文件夹
    private boolean isAllPhoto=true;//是否是读取文件夹里面-包括里面的文件下的图片
    private WeakReference<Context> context;


    /**
     * 请求构造方法
     */
    public AllImageFile(Context context, String floder, boolean isAllPhoto,ImageFileListener back) {
        this.back = back;
        this.floder = floder;
        this.isAllPhoto=isAllPhoto;
        this.context = new WeakReference<Context>(context);
    }
    /**
     * 请求构造方法
     */
    public AllImageFile(Context context, String floder, ImageFileListener back) {
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
        return getAllImageFiles(new File(floder));

    }

    private List<FileModel> getAllImageFiles(File file) {
        List<FileModel> listdata = new ArrayList<>();
        if (file==null){
            MyApplication.Toast("没有找到内部存储卡！！");
            return listdata;
        }
        if (file.getName().length()<=0||file.getName().substring(0,1).equals("."))return listdata;

        File[] files = file.listFiles();
        if(null!=files)
        for (int i = 0; i < files.length; i++) {
            File son = files[i];
            if (ExceptionFile.getIscontains(son.getAbsolutePath()))continue;

            if (son.isDirectory()) {
                if (isAllPhoto) {
                    listdata.addAll(getAllImageFiles(son));
                }
            } else if (son.canRead()){
                String filename = son.getName();
                filename= filename.toLowerCase();
                if (filename.endsWith(".jpg")
                        || filename.endsWith(".png")
                        || filename.endsWith(".jpeg")
                        || filename.endsWith(".bmp")) {
                    if (files[i].getAbsolutePath().equals(StringUtils.getSDCardPath()+"/ad.jpg"))continue;
                    FileModel model = new FileModel(files[i]);
                    listdata.add(model);
                }

            }
        }
        return listdata;
    }

    @Override
    protected void onPostExecute(List<FileModel> result) {// 在doInBackground执行完成后系统会自动调用，result是返回值
        APPLog.e(result.size());
        if (isFinish() != null)
            back.getFileSucess(result);
    }

    /**
     * Created by Administrator on 2016/4/1.
     */
    public interface ImageFileListener {
        /*
         *
         * 图片处理成功
         */
        public void getFileSucess(List<FileModel> results);
    }
}
