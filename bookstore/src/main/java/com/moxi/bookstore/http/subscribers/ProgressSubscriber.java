package com.moxi.bookstore.http.subscribers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.moxi.bookstore.BookstoreApplication;
import com.moxi.bookstore.http.exception.HttpTimeException;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.utils.ToolUtils;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.Toastor;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by cl on 2016/9/16.
 */
public class ProgressSubscriber<T> extends Subscriber<T> {
    //    回调接口
    private HttpOnNextListener mSubscriberOnNextListener;
    //    弱引用反正内存泄露
    private WeakReference<Context> mActivity;
    //    是否能取消请求
    private boolean cancel;
    //    加载框可自己定义
    private ProgressDialog pd;
    //private LoadingProgressDialog pd;
    private String progressTitle;
    public ProgressSubscriber(HttpOnNextListener mSubscriberOnNextListener, Context context) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.mActivity = new WeakReference<>(context);
        this.cancel = false;
        //initProgressDialog();
    }

    /*public ProgressSubscriber(HttpOnNextListener mSubscriberOnNextListener, Context context, boolean cancel) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.mActivity = new WeakReference<>(context);
        this.cancel = cancel;
        initProgressDialog();
    }*/

   /* public ProgressSubscriber(HttpOnNextListener mSubscriberOnNextListener, Context context,String progressTitle) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.mActivity = new WeakReference<>(context);
        this.cancel = false;
        this.progressTitle=progressTitle;
        initProgressDialog();
    }*/

    /**
     * 初始化加载框
     */
    private void initProgressDialog() {
        Context context = mActivity.get();
        if (pd == null && context != null) {
            pd=new ProgressDialog(context);
            pd.setCancelable(cancel);
            if (cancel) {
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        onCancelProgress();
                    }
                });
            }
            if (null!=progressTitle){}
                pd.setMessage(progressTitle);
        }
    }


    /**
     * 显示加载框
     */
    private void showProgressDialog() {
        Context context = mActivity.get();
        if (pd == null || context == null) return;
        if (!pd.isShowing()) {
            pd.show();
        }
    }


    /**
     * 隐藏
     */
    private void dismissProgressDialog() {
        if (null==mActivity.get()){
            return;
        }
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }


    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        showProgressDialog();
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
        dismissProgressDialog();
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        Context context = mActivity.get();
        if (context == null) return;
        try {
            if (e instanceof SocketTimeoutException) {
                Toast.makeText(context, "网络中断，请检查您的网络状态", Toast.LENGTH_SHORT).show();
            } else if (e instanceof ConnectException) {
                Toast.makeText(context, "网络中断，请检查您的网络状态", Toast.LENGTH_SHORT).show();
            } else if (e instanceof HttpTimeException){
                mSubscriberOnNextListener.onError(e.toString());
                return;
            }else {
               String erroMsg=e.getMessage();
                e.printStackTrace();
                APPLog.e(e.toString(),erroMsg);
                if (null!=erroMsg&&erroMsg.length()<30) {
                    if (erroMsg.contains("未登录")){
                        ToolUtils.getIntence().startDDUserBind(BookstoreApplication.getContext());
                    }else {
                        Toastor.getToast(context, erroMsg).show();
                    }
                }
                Log.i(mActivity.getClass().getSimpleName(), "error----------->" + e.toString());

            }
            if (mSubscriberOnNextListener != null&&null!=e) {
              /*  if (e instanceof NullPointerException) {
                    APPLog.e("progresssubscriber Nullpoiinter");
                    return;
                }
                else*/
                    mSubscriberOnNextListener.onError();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onNext(t);
        }
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }
}