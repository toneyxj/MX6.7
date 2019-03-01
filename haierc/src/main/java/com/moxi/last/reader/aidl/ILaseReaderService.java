package com.moxi.last.reader.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.SharePreferceUtil;
import com.mx.mxbase.utils.Toastor;

/**
 * Created by Archer on 16/10/19.
 */
public class ILaseReaderService extends Service {
    private ILastReaderOne iLastReader = new ILastReaderOne();
    private SharePreferceUtil share;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iLastReader;
    }

    public class ILastReaderOne extends ILaseReader.Stub {

        @Override
        public void lasetReaderFile(String filePath) throws RemoteException {
            try {
                Log.e("lasetReaderFile", filePath + "测试链接啊");
                share = SharePreferceUtil.getInstance(ILaseReaderService.this);
                share.setCache("mx_last_reader_file", filePath);
            } catch (Exception e) {
                e.printStackTrace();
                Toastor.showToast(ILaseReaderService.this, "未找到最近阅读书籍");
            }
        }
    }
}
