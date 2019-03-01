package com.moxi.bookstore.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.mx.mxbase.utils.SharePreferceUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Administrator on 2017/2/22.
 */
public class ScanReadFileAsy extends AsyncTask<Void, Void, List<File>> {
    private WeakReference<Context> context;
    public String filePath;
    public SharePreferceUtil share;
    public ScanReadFileAsy(Context context,String filePath){
        this.context=new WeakReference<>(context);
        this.filePath=filePath;
        share= SharePreferceUtil.getInstance(getcontext());
    }
    public Context getcontext() {
        final Context context = this.context.get();
        if (context == null) {
            return null;
        }
        return context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<File> doInBackground(Void... voids) {
        String bookStr = share.getString("mx_read_local_books");
        String middlepath=filePath+ "@#@";
        bookStr.replace(middlepath,"");
        bookStr=middlepath+bookStr;
        share.setCache("mx_read_local_books", bookStr);
        return null;
    }

    @Override
    protected void onPostExecute(List<File> list) {
        super.onPostExecute(list);
    }
}