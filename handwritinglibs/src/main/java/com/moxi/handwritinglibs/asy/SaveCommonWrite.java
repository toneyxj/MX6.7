package com.moxi.handwritinglibs.asy;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.moxi.handwritinglibs.db.WriteCommonModel;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.handwritinglibs.listener.CommonSaveWriteListener;
import com.moxi.handwritinglibs.utils.BitmapOrStringConvert;

/**
 * 普通保存绘制信息
 * Created by 夏君 on 2017/2/9.
 */
public class SaveCommonWrite extends AsyncTask<String, Void, Boolean> {
    private String name;
    private String saveCode;
    private Bitmap bitmap;
    private CommonSaveWriteListener listener;

    public SaveCommonWrite(String name, String saveCode, Bitmap bitmap) {
        this.name = name;
        this.saveCode = saveCode;
        this.bitmap = bitmap;
    }

    public SaveCommonWrite(String name, String saveCode, Bitmap bitmap, CommonSaveWriteListener listener) {
        this.name = name;
        this.saveCode = saveCode;
        this.bitmap = bitmap;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        WriteCommonModel model = new WriteCommonModel(name, saveCode, BitmapOrStringConvert.convertIconToString(bitmap));
        return WritePadUtils.getInstance().saveData(model);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        Log.e("SaveCommonWrite-"+saveCode,String.valueOf(aBoolean));
        if (null==listener)return;
        //设置回调
        listener.isSucess(aBoolean,saveCode);
    }
}
