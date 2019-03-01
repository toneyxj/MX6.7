package com.moxi.bookstore.utils;

import android.app.Activity;
import android.content.Context;
import android.os.storage.StorageManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StorageList {

    private Context mActivity;
    private StorageManager mStorageManager;
    private Method mMethodGetPaths;

    public StorageList(Context activity) {
        mActivity = activity;
        if (mActivity != null) {
            mStorageManager = (StorageManager)mActivity
                    .getSystemService(Activity.STORAGE_SERVICE);
            try {
                mMethodGetPaths = mStorageManager.getClass()
                        .getMethod("getVolumePaths");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public String[] getVolumePaths() {
        String[] paths = null;
        try {
            paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return paths;
    }
}

