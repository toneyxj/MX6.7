package com.dangdang.reader.dread.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.dangdang.reader.Constants;
import com.dangdang.reader.R;
import com.dangdang.reader.cloud.CloudSyncConfig;
import com.dangdang.reader.dread.core.base.GoToParams;
import com.dangdang.reader.dread.core.base.IEpubReaderController;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.PartReadInfo;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.part.PartBook;
import com.dangdang.reader.dread.format.part.PartBuyInfo;
import com.dangdang.reader.utils.Utils;
import com.dangdang.reader.view.IDialog;
import com.dangdang.zframework.utils.NetUtil;
import com.dangdang.zframework.utils.UiUtil;
import com.dangdang.zframework.view.DDTextView;

public class BuyChapterDialog extends IDialog implements IBuyDialog {
    private PartBuyInfo mBuyInfo;
    private TextView mNameTv;
    private TextView mPriceTv;
    private TextView mBalanceTv;
    private TextView mBuyTv;
    private TextView mAutoBuyCheckbox;
    private TextView mRechargeTv;

    private boolean isPre;
    private int mFrom;
    private CloudSyncConfig cloudSyncConfig;

    public BuyChapterDialog(Context context) {
        super(context, R.style.Dialog_NoBackground);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                if (mFrom == BuyDialogManager.DIALOG_BUY_FULL) {
                    BuyDialogManager.getInstance().getBuyFullDialog().show();
                } else {
                    sendCancelBroadcast();
                }
                dismiss();
                return true;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCreateD() {
        setContentView(R.layout.dialog_buy_chapter);
        cloudSyncConfig = new CloudSyncConfig(getContext());
        initViews();
    }

    private void initViews() {
        mNameTv = (TextView) findViewById(R.id.dialog_buy_chapter_name_tv);
        mPriceTv = (TextView) findViewById(R.id.dialog_buy_chapter_price_tv);
        mBalanceTv = (TextView) findViewById(R.id.dialog_buy_chapter_balance_tv);
        mBuyTv = (TextView) findViewById(R.id.dialog_buy_chapter_buy_chapter);
        mBuyTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBuy();
            }
        });
        findViewById(R.id.dialog_buy_chapter_cancel_chapter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFrom == BuyDialogManager.DIALOG_BUY_FULL) {
                    BuyFullDialog dialog = BuyDialogManager.getInstance().getBuyFullDialog();
                    dialog.show();
                }
                dismiss();
            }
        });
        mAutoBuyCheckbox = (DDTextView) findViewById(R.id.dialog_buy_chapter_auto_buy);
        mAutoBuyCheckbox.setSelected(cloudSyncConfig.getNovelPreload());
        mAutoBuyCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutoBuyCheckbox.setSelected(!mAutoBuyCheckbox.isSelected());
            }
        });

        mRechargeTv = (TextView) findViewById(R.id.dialog_buy_chapter_recharge);
        mRechargeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                StoreChooseSmallBellRechargeActivity.launch((ReadActivity) mContext, -1);
            }
        });
    }

    private void sendCancelBroadcast() {
        Intent intent = new Intent(Constants.BROADCAST_BUY_DIALOG_CANCEL);
        mContext.sendBroadcast(intent);
    }

    protected void onBuy() {
        if (!NetUtil.isNetworkConnected()){
            UiUtil.showToast(mContext,R.string.network_exp);
            return;
        }
        if (mRechargeTv.getVisibility()==View.VISIBLE){
//            StoreChooseSmallBellRechargeActivity.launch((ReadActivity) mContext, -1);
            return;
        }
        PartReadInfo partReadInfo = (PartReadInfo) ReaderAppImpl.getApp().getReadInfo();
        boolean isSelect = mAutoBuyCheckbox.isSelected();
        partReadInfo.setIsAutoBuy(isSelect);
        if (isSelect)
            cloudSyncConfig.setNovelPreload(isSelect);
        PartBook partBook = (PartBook) ReaderAppImpl.getApp().getBook();
        Chapter chapter = partBook.getChapterById(mBuyInfo.getChapterId());
        GoToParams params = new GoToParams();
        params.setChapter(chapter);
        params.setElementIndex(0);
        if (isPre) {
            params.setGotoLast(true);
        }
        params.setBuy(true);
        IEpubReaderController controller = (IEpubReaderController) ReaderAppImpl.getApp().getReaderController();
        controller.gotoPage(params);
        dismiss();
    }

    public void setBuyInfo(PartBuyInfo info, boolean isPre) {
        mBuyInfo = info;
        this.isPre = isPre;
        fillDatas();
    }

    @Override
    public void setBalanceInfo(int main, int sub,int type) {
        if (mBuyInfo == null)
            return;
        if (main != -1) {
            if(type==0){
                mBuyInfo.setMainBalance(main+mBuyInfo.getMainBalance());
                mBuyInfo.setSubBalance(sub+mBuyInfo.getSubBalance());
            }else{
                mBuyInfo.setMainBalance(main);
                mBuyInfo.setSubBalance(sub);
            }
        } else {
            mBuyInfo.setSubBalance(mBuyInfo.getSubBalance() + sub);
        }
        int total = mBuyInfo.getMainBalance() + mBuyInfo.getSubBalance();
        mBalanceTv.setText(Utils.getNewNumber(total, false) + "" + mContext.getText(R.string.lingdang));
        updateButtonStatus();
    }

    private void fillDatas() {
        if (mBuyInfo != null) {
            mNameTv.setText(mBuyInfo.getChapterTitle());
            mPriceTv.setText(Utils.getNewNumber(mBuyInfo.getChapterPrice(), false) + mContext.getString(R.string.lingdang));
            setBalanceInfo(mBuyInfo.getMainBalance(), mBuyInfo.getSubBalance(),1);
            mAutoBuyCheckbox.setSelected(true);
        }
    }

    private void updateButtonStatus() {
        if ((mBuyInfo.getMainBalance() + mBuyInfo.getSubBalance()) < mBuyInfo.getChapterPrice()) {
            mRechargeTv.setVisibility(View.VISIBLE);
        } else {
            mRechargeTv.setVisibility(View.GONE);
        }
    }

    public void setFrom(int from) {
        mFrom = from;

    }

    @Override
    public void show() {
    	try{
    		Window window = getWindow();
            window.setWindowAnimations(R.style.style_popup_alpha_anim);
            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = UiUtil.dip2px(mContext, 253);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            super.show();
    	}catch(Throwable e){
    		e.printStackTrace();
    	}
    }
}
