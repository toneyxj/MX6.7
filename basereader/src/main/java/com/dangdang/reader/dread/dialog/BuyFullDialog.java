package com.dangdang.reader.dread.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.dangdang.reader.Constants;
import com.dangdang.reader.R;
import com.dangdang.reader.base.BaseReaderActivity;
import com.dangdang.reader.dread.core.base.GoToParams;
import com.dangdang.reader.dread.core.base.IEpubReaderController;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.part.PartBook;
import com.dangdang.reader.dread.format.part.PartBuyInfo;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.reader.request.RequestConstants;
import com.dangdang.reader.request.RequestResult;
import com.dangdang.reader.utils.BuyBookStatisticsUtil;
import com.dangdang.reader.utils.Utils;
import com.dangdang.reader.view.IDialog;
import com.dangdang.zframework.utils.NetUtil;
import com.dangdang.zframework.utils.UiUtil;

import java.lang.ref.WeakReference;

public class BuyFullDialog extends IDialog implements IBuyDialog {
    private PartBuyInfo mBuyInfo;
    private TextView mNameTv;
    private TextView mPriceTv;
    private TextView mBalanceTv;
    private boolean isPre;

    private TextView mRechargeTv;

    public BuyFullDialog(Context context) {
        super(context, R.style.Dialog_NoBackground);
        mHandler = new MyHandler(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                sendCancelBroadcast();
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
        setContentView(R.layout.dialog_buy_full);
        initViews();
    }

    private void initViews() {
        mNameTv = (TextView) findViewById(R.id.dialog_buy_chapter_name_tv);
        mPriceTv = (TextView) findViewById(R.id.dialog_buy_chapter_price_tv);
        mBalanceTv = (TextView) findViewById(R.id.dialog_buy_chapter_balance_tv);
        findViewById(R.id.dialog_buy_chapter_cancel_chapter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.dialog_buy_chapter_by_chapter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuyChapterDialog dialog = BuyDialogManager.getInstance().getBuyChapterDialog();
                dialog.setBuyInfo(mBuyInfo, isPre);
                dialog.setFrom(BuyDialogManager.DIALOG_BUY_FULL);
                dialog.show();
                dismiss();
            }
        });
        findViewById(R.id.dialog_buy_chapter_buy_chapter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBuy();
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
        BuyBookStatisticsUtil.getInstance().setShowType(BuyBookStatisticsUtil.ShowType.SHOW_TYPE_READ);
//        BuyBookStatisticsUtil.getInstance().setShowTypeId("");
//        BuyBookStatisticsUtil.getInstance().setTradeType(BuyBookStatisticsUtil.TradeType.TRADE_TYPE_READ);
//
//        BuyMediaRequest request = new BuyMediaRequest(mBuyInfo.getSaleId(), mBuyInfo.getMediaId(), "", mHandler);
//        AppUtil.getInstance(mContext).getRequestQueueManager().sendRequest(request, "full");
//        getReaderActivity().showGifLoadingByUi();
    }

    private BaseReaderActivity getReaderActivity() {
        return (BaseReaderActivity) mContext;
    }

    private Handler mHandler;

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

    private void updateButtonStatus() {
        if ((mBuyInfo.getMainBalance() + mBuyInfo.getSubBalance()) < mBuyInfo.getSalePrice()) {
            mRechargeTv.setVisibility(View.VISIBLE);
        } else {
            mRechargeTv.setVisibility(View.GONE);
        }
    }

    private void fillDatas() {
        if (mBuyInfo != null) {
            mNameTv.setText(mBuyInfo.getSaleName());
            mPriceTv.setText(Utils.getNewNumber(mBuyInfo.getSalePrice(), false) + mContext.getString(R.string.lingdang));
            setBalanceInfo(mBuyInfo.getMainBalance(), mBuyInfo.getSubBalance(),1);
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<BuyFullDialog> mFragmentView;

        MyHandler(BuyFullDialog view) {
            this.mFragmentView = new WeakReference<BuyFullDialog>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            BuyFullDialog service = mFragmentView.get();
            if (service != null) {
                service.getReaderActivity().hideGifLoadingByUi();
                super.handleMessage(msg);
                switch (msg.what) {
                    case RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS: {
                        service.onBuySuccess();
                        service.dismiss();
                        break;
                    }
                    case RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL:
                        service.onBuyFailed((RequestResult)msg.obj);
                        break;
                    default:
                        break;
                }
            }
        }
    }
    private void onBuyFailed(RequestResult result){
        UiUtil.showToast(mContext,result.getExpCode().errorMessage);
    }
    private  void onBuySuccess(){
        ReadInfo mReadInfo = (ReadInfo) ReaderAppImpl.getApp().getReadInfo();
        mReadInfo.setTryOrFull(ShelfBook.TryOrFull.FULL.ordinal());
        mReadInfo.setBought(true);
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
    }
}
