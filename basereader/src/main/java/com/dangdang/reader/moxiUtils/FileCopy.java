package com.dangdang.reader.moxiUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.mx.mxbase.utils.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/9/1.
 */
public class FileCopy extends AsyncTask<String, Void, Boolean> {
    private CopyListener back;// 传入接口
    private String assetsPath;// 查询文件夹
    private String toSdPath;// 查询文件夹
    private String name;// 查询文件夹
    private WeakReference<Context> context;
    private   ProgressDialog dialog;

    /**
     * 复制assets下面的文件
     */
    public FileCopy(Context context,String assetsPath, String toSdPath, String name,CopyListener back) {


        this.back = back;
        this.toSdPath = toSdPath;
        this.name = name;
        this.assetsPath = assetsPath;
        this.context = new WeakReference<Context>(context);
    }

    private Context isFinish() {
        Context context = this.context.get();
        return context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (isFinish()==null)return;
        dialog = new ProgressDialog(isFinish());
        dialog.setMessage("字体准备中...");
        dialog.setCancelable(false);// 是否可以关闭dialog
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        if ((assetsPath==null||assetsPath.equals("")))return true;
        File file=new File(toSdPath);
        if(file.exists()){
            return true;
        }
        //获得所有目录的觉得路径
        try {
           copyBigDataToSD(toSdPath,assetsPath);
            return true;
        } catch (IOException e) {
            StringUtils.deleteFile(toSdPath);
            return false;
        }

    }


    @Override
    protected void onPostExecute(Boolean result) {// 在doInBackground执行完成后系统会自动调用，result是返回值
        if (dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
            dialog=null;
        }
        if (isFinish() != null&&back!=null)
            back.CopyListener(result,toSdPath,name);
    }

    /**
     * 复制字体
     * @param strOutFileName
     * @throws IOException
     */
    private void copyBigDataToSD(String strOutFileName,String sourceFont) throws IOException
    {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(strOutFileName);
        myInput = context.get().getAssets().open(sourceFont);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }
    /**
     * Created by Administrator on 2016/4/1.
     */
    public interface CopyListener {
        /*
         *
         * 是否移动成功
         */
        public void CopyListener(boolean results,String path,String name);
    }
}