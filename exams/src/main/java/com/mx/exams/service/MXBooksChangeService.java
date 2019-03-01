package com.mx.exams.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.mx.exams.model.ExamModel;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.DateUtil;
import com.mx.mxbase.utils.FileGetByDir;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.RandomUtil;
import com.mx.mxbase.utils.SdCardUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Archer on 16/8/24.
 */
public class MXBooksChangeService extends Service {

    private List<File> listFiel = new ArrayList<>();
    private Handler handler = new Handler();
    private int currentSize = 0;

    @Override
    public void onCreate() {
        Log.e("service onCreate", "启动service");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("service onStartCommand", "启动service成功开始工作");
        handler.post(getBooks);
        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable getBooks = new Runnable() {
        @Override
        public void run() {
            String dir = SdCardUtil.getNormalSDCardPath() + Constant.EXAMS_PATH;
            FileUtils.getInstance().createMks(dir);
            listFiel.clear();
            listFiel = FileGetByDir.getFileDir(dir);
            if (currentSize != listFiel.size()) {
                Log.e("扫描文件", "发现新增文件开始修改数据库");
                DataSupport.deleteAll(ExamModel.class);
                saveBooks(listFiel);
            } else {
                Log.e("扫描文件", "没有新增文件");
                if (currentSize == 0) {
                    saveBooks(listFiel);
                }
            }
            currentSize = listFiel.size();
            handler.postDelayed(this, 12 * 1000);
        }
    };

    /**
     * 保存书籍到数据库
     *
     * @param list
     */
    public static void saveBooks(List<File> list) {
        for (int i = 0; i < list.size(); i++) {
//            DataSupport.findBySQL("select * from ");
            ExamModel em = new ExamModel();
            em.setExamDesc("");
            String tempName = list.get(i).getName();
            tempName = tempName.substring(0, tempName.lastIndexOf("."));
            em.setExamName(tempName);
            em.setExamPoint(RandomUtil.getRandom(0, 100) + "");
            em.setExamState(RandomUtil.getRandom(0, 99) + "");
            String tempKm = "";
            if (tempName.indexOf("数学") > 0) {
                tempKm = "数学";
            } else if (tempName.indexOf("语文") > 0) {
                tempKm = "语文";
            } else if (tempName.indexOf("英语") > 0) {
                tempKm = "英语";
            }
            em.setExamSubjects(tempKm);
            em.setWriteDate(DateUtil.getCurDateStr());
            em.setExamFileJson(GsonTools.obj2json(list.get(i)));
            em.save();
        }
    }
}
