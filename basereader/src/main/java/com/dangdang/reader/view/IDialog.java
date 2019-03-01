package com.dangdang.reader.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.dangdang.zframework.log.LogM;

public abstract class IDialog extends Dialog implements DialogInterface {

    private final static LogM logger = LogM.getLog(IDialog.class);
    protected Context mContext;

    public IDialog(Context context, boolean cancelable,
                   OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
        onCreateD();
    }

    public IDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        onCreateD();
    }

    public IDialog(Context context) {
        super(context);
        mContext = context;
        onCreateD();
    }

    public abstract void onCreateD();

    public void printLog(String log) {
        logger.i(false, log);
    }

    public void printLogE(String log) {
        logger.e(false, log);
    }
}
