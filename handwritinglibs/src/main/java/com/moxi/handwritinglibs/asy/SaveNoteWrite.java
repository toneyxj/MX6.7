package com.moxi.handwritinglibs.asy;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;

import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.handwritinglibs.listener.NoteSaveWriteListener;
import com.moxi.handwritinglibs.listener.UpLogInformationInterface;
import com.moxi.handwritinglibs.utils.BitmapOrStringConvert;
import com.moxi.handwritinglibs.utils.ToastUtils;

import java.io.File;

/**
 * 保存笔记
 * Created by 夏君 on 2017/2/17.
 */
public class SaveNoteWrite extends AsyncTask<String, Void, Boolean> {
    private WritPadModel model;
    private Bitmap bitmap;
    private NoteSaveWriteListener listener;
    private UpLogInformationInterface upListener;
    private String log="";

    public SaveNoteWrite(WritPadModel model, Bitmap bitmap,NoteSaveWriteListener listener,UpLogInformationInterface upListener) {
        this.model=new WritPadModel(model.name,model.saveCode,model.isFolder,model.parentCode,model._index,model.extend);
        this.bitmap=bitmap;
        this.listener=listener;
        this.upListener=upListener;
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        if (null==bitmap){
            model.imageContent="";
            return true;
        }
        if (bitmap.isRecycled()){
            return false;
        }
        log="SaveNoteWrite-start-1="+System.currentTimeMillis();
        model.imageContent=BitmapOrStringConvert.convertIconToString(bitmap);
        log+="-end-2"+System.currentTimeMillis();
//        Log.e(" model.imageContent=",String.valueOf( model.imageContent.getBytes().length));
        return WritePadUtils.getInstance().saveData(model);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        model.imageContent=null;
//        Log.e("SaveCommonWrite-"+model.saveCode,String.valueOf(aBoolean));
        if (readSystem()<1024&&!aBoolean){
            ToastUtils.getInstance().showToastShort("内存不足存储失败");
        }
        log+="-onPostExecute-time-"+System.currentTimeMillis();
        if (upListener!=null)upListener.onUpLog(log,System.currentTimeMillis());
        if (null==listener)return;
        //设置回调
        listener.isSucess(aBoolean,model);
    }
  private   long  readSystem() {
        File root = Environment.getRootDirectory();
        StatFs sf = new  StatFs(root.getPath());
        long  blockSize = sf.getBlockSize();
        long  blockCount = sf.getBlockCount();
        long  availCount = sf.getAvailableBlocks();
      return availCount*blockSize/ 1024;
//        Log.d("" ,  "block大小:" + blockSize+ ",block数目:" + blockCount+ ",总大小:" +blockSize*blockCount/ 1024 + "KB" );
//        Log.d("" ,  "可用的block数目：:" + availCount+ ",可用大小:" + availCount*blockSize/ 1024 + "KB" );
    }
}
