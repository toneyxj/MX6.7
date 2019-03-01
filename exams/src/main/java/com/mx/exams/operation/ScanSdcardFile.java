package com.mx.exams.operation;

import android.os.AsyncTask;


import com.mx.exams.mxinterface.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Archer on 16/8/26.
 */
public class ScanSdcardFile extends AsyncTask<Void, Void, Void> {

    private String[] type;
    private String root;
    private FileCallBack fileCallBack;
    private List<File> listFiles = new ArrayList<>();

    public ScanSdcardFile(String root, String[] type, FileCallBack fileCallBack) {
        this.root = root;
        this.type = type;
        this.fileCallBack = fileCallBack;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        searchFile(new File(root), type);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        fileCallBack.fileBack(listFiles);
    }

    private void searchFile(File root, String[] keyword) {
        List<File> temp = new ArrayList<>();
        File[] files = root.listFiles();
        for (File file : files) {
            if (file.isDirectory() && file.canRead()) {//判断是否为文件夹并为keduwenjian
                searchFile(file, keyword);
            } else {
                if (file.getName().indexOf(keyword[0]) >= 0) {
                    temp.add(file);
                }
            }
        }
        listFiles.addAll(temp);
        temp.clear();
    }
}
