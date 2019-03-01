package com.dangdang.reader.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.dangdang.zframework.log.LogM;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Created by wanghaiming on 2016/4/26.
 */
public class ForegroundV2 implements  Application.ActivityLifecycleCallbacks {

    public interface Listener {
        public void onBecameForeground();
        public void onBecameBackground();
    }

    private static ForegroundV2 sInstance;
    private List<WeakReference<Listener>> mListenerList;
    private boolean    mIsForeground;
    private Stack<Activity> mActivityStack;

    private ForegroundV2(){
        mListenerList = new LinkedList<WeakReference<Listener>>();
        mActivityStack = new Stack<Activity>();
    }

    public static final void init(Application app){
        if(sInstance == null){
            sInstance = new ForegroundV2();
            app.registerActivityLifecycleCallbacks(sInstance);
        }

    }
    public static final ForegroundV2 getInstance(){
        return sInstance;
    }

    private void notifyToForeground(){
        for(WeakReference<Listener> listenerRef : mListenerList){
            Listener listener = listenerRef.get();
            if(listener != null){
                listener.onBecameForeground();
            }
        }
    }

    private void notifyToBackground(){
        for(WeakReference<Listener> listenerRef : mListenerList){
            Listener listener = listenerRef.get();
            if(listener != null){
                listener.onBecameBackground();
            }
        }
    }

    public Activity popTopActivity(){
        LogM.d("whm","popTopActivity------topActivity is: "+mActivityStack.peek().getClass().getSimpleName());
        return mActivityStack.pop();
    }
    public Activity getTopActivity(){
        LogM.d("whm","getTopActivity-------topActivity is: "+mActivityStack.peek().getClass().getSimpleName());
        return mActivityStack.peek();
    }
    public void addListener(Listener listener){
        WeakReference<Listener> listenerWeakRef = new WeakReference<Listener>(listener);
        mListenerList.add(listenerWeakRef);
    }
    public void removeListener(Listener listener){
        for(WeakReference<Listener> listenerRef : mListenerList){
            Listener tempListener = listenerRef.get();
            if(tempListener == listener){
                mListenerList.remove(listenerRef);
                return;
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        LogM.d("whm",activity.getClass().getSimpleName()+"started");
        if(!mIsForeground){
            mIsForeground = true;
            notifyToForeground();
        }
        mActivityStack.push(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        LogM.d("whm",activity.getClass().getSimpleName()+"stopped");
        mActivityStack.remove(activity);
        if((mActivityStack.isEmpty())&&(!activity.isChangingConfigurations())){
            mIsForeground = false;
            notifyToBackground();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }




}
