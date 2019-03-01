package com.moxi.bookstore.utils;

import android.content.Context;

import com.dangdang.reader.dread.StartRead;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.util.DrmWrapUtil;
import com.moxi.bookstore.db.EbookDB;
import com.moxi.bookstore.db.SacnReadFileUtils;
import com.moxi.bookstore.db.TableConfig;
import com.moxi.bookstore.db.TableOperate;
import com.mx.mxbase.utils.SharePreferceUtil;
import com.mx.mxbase.utils.ToastUtils;
//import com.onyx.android.sdk.device.DeviceInfo;

import java.io.File;

/**
 * Created by Administrator on 2016/10/12.
 */
public class StartUtils {
    /**
     * 打开书籍前自查数据库数据
     *
     * @param context
     */
    public static boolean OpenDDRead(Context context, String savePath) {
        if (savePath == null || savePath.equals("")) return false;
        EbookDB eBook = TableOperate.getInstance().queryByPath(TableConfig.TABLE_NAME, savePath);
        if (eBook != null) {
            OpenDDRead(context, eBook);
        }
        return eBook != null;
    }

    /**
     * 打开书籍
     *
     * @param context
     * @param dbook
     */
    public static boolean OpenDDRead(Context context, EbookDB dbook) {
        File file = new File(dbook.filePath);
        if (file.length() == 0 || !file.canRead()) {
            ToastUtils.getInstance().showToastShort("文件已损坏，无法打开");
            return false;
        }

        if (ReadConfig.AllScreen) {
//            DeviceInfo.currentDevice.hideSystemStatusBar(context);
        }
//        new ScanReadFileAsy(context,dbook.filePath).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        SharePreferceUtil.getInstance(context).setCache("Recently", dbook.filePath);
        SacnReadFileUtils.getInstance(context).updateIndex(dbook.filePath);

        StartRead.ReadParams readParams = new StartRead.ReadParams();
        readParams.setBookAuthor(dbook.author);
        readParams.setBookName(dbook.name);
        readParams.setBookId(dbook.saleId + "");
        readParams.setBookCertKey(DrmWrapUtil.getPartBookCertKey(dbook.key));
        readParams.setBookCover(dbook.getIconUrl());
        readParams.setBookFile(dbook.filePath);
        readParams.setBookDir(dbook.filePath);
        readParams.setBookDesc(dbook.bookdesc);
        readParams.setBookType(2);
        readParams.setIsBought(dbook.flag == 0);
        readParams.setReadProgress(dbook.getProgress());
        StartRead read = new StartRead();
        read.startRead(context, readParams);
        return true;
    }
}
