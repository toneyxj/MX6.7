package com.dangdang.reader.dread.dialog;

import android.content.Context;

import com.dangdang.reader.dread.format.part.IPartChapterHandle;
import com.dangdang.reader.dread.format.part.PartBuyInfo;
import com.dangdang.reader.dread.format.part.PartChapter;
import com.dangdang.reader.view.IDialog;


/**
 * Created by liuboyu on 2015/1/14.
 */
public class BuyDialogManager {

    public static final int DIALOG_NONE = 0;
    public static final int DIALOG_BUY_CHAPTER = 1;
    public static final int DIALOG_BUY_FULL = 2;
    public static final int DIALOG_BUY_MONTH = 3;

    private static BuyDialogManager mInstance;
    private Context mContext;

    private BuyDialogManager() {

    }

    private BuyChapterDialog mBuyChapterDialog;
    private BuyMonthDialog mBuyMonthDialog;
    private BuyFullDialog mBuyFullDialog;
    private IDialog mCurrentDialog;

    public void showBuyInfo(PartChapter baseChapter, boolean isPre) {
        if (baseChapter == null) {
            return;
        }
        if (!(baseChapter instanceof PartChapter)) {
            return;
        }
        PartChapter chapter = (PartChapter) baseChapter;
        if (chapter.getCode() == IPartChapterHandle.PARSE_SUCEESS) {
            return;
        }
        PartBuyInfo buyinfo = chapter.getPartBuyInfo();
        if (buyinfo == null) {
            return;
        }
        if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
            return;
        }

        if (buyinfo.getIsSupportFullBuy() == 1) {
            mCurrentDialog = getBuyFullDialog();
        } else {
            mCurrentDialog = getBuyChapterDialog();
        }
        ((IBuyDialog) mCurrentDialog).setBuyInfo(buyinfo, isPre);
        mCurrentDialog.show();
    }

    public synchronized BuyChapterDialog getBuyChapterDialog() {
        if (mBuyChapterDialog == null)
            mBuyChapterDialog = new BuyChapterDialog(mContext);
        mBuyChapterDialog.setFrom(DIALOG_NONE);
        mCurrentDialog = mBuyChapterDialog;
        return mBuyChapterDialog;
    }

    public synchronized BuyFullDialog getBuyFullDialog() {
        if (mBuyFullDialog == null)
            mBuyFullDialog = new BuyFullDialog(mContext);
        mCurrentDialog = mBuyFullDialog;
        return mBuyFullDialog;
    }

    public synchronized BuyMonthDialog getBuyMonthDialog() {
        if (mBuyMonthDialog == null)
            mBuyMonthDialog = new BuyMonthDialog(mContext);
        mCurrentDialog = mBuyMonthDialog;
        return mBuyMonthDialog;
    }

    public void init(Context context) {
        mContext = context;
    }

    public synchronized static BuyDialogManager getInstance() {
        if (mInstance == null)
            mInstance = new BuyDialogManager();
        return mInstance;
    }

    public synchronized void clear() {
        mBuyChapterDialog = null;
        mBuyFullDialog = null;
        mBuyMonthDialog = null;
        mInstance = null;
    }

    public void updateBalanceInfo(int main, int sub,int type) {
        if (mBuyChapterDialog != null) {
            mBuyChapterDialog.setBalanceInfo(main, sub,type);
        }
        if (mBuyFullDialog != null) {
            mBuyFullDialog.setBalanceInfo(main, sub,type);
        }
        if (mBuyMonthDialog != null) {
            mBuyMonthDialog.setBalanceInfo(main, sub,type);
        }
    }
}
