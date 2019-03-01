package com.moxi.filemanager.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.mx.mxbase.utils.StringUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 */
public class DeleteFile extends AsyncTask<String, Void, Boolean> {
    private DeleteFileListener back;// 传入接口
    private List<File> files;// 删除的文件集合
    private WeakReference<Context> context;


    /**
     * 请求构造方法
     */
    public DeleteFile(Context context, List<File> files, DeleteFileListener back) {
        this.back = back;
        this.files = files;
        this.context = new WeakReference<>(context);
    }

    private Context isFinish() {
        Context context = this.context.get();
        return context;
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        //删除文件
        for (File file:files){
            StringUtils.deleteFile(file);
        }
        return true;

    }



    @Override
    protected void onPostExecute(Boolean result) {// 在doInBackground执行完成后系统会自动调用，result是返回值
        if (null!=isFinish())
            back.DeleteSucess(result);
    }

    /**
     * Created by Administrator on 2016/4/1.
     */
    public interface DeleteFileListener {
        /*
         *
         * 图片处理成功
         */
        public void DeleteSucess(boolean results);
    }
}
