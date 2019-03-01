package com.dangdang.reader.dread;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

import com.dangdang.reader.Constants;
import com.dangdang.reader.DDApplication;
import com.dangdang.reader.R;
import com.dangdang.reader.domain.TimeFreeInfo;
import com.dangdang.reader.dread.core.base.GoToParams;
import com.dangdang.reader.dread.core.base.IEpubReaderController;
import com.dangdang.reader.dread.core.part.PartReaderController;
import com.dangdang.reader.dread.data.PartReadInfo;
import com.dangdang.reader.dread.dialog.BuyDialogManager;
import com.dangdang.reader.dread.dialog.ExitReadDialog;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.IBook;
import com.dangdang.reader.dread.format.part.PartBook;
import com.dangdang.reader.dread.function.FunctionCode;
import com.dangdang.reader.dread.util.DrmWrapUtil;
import com.dangdang.reader.dread.util.ReadBookUtil;
import com.dangdang.reader.personal.DataUtil;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.reader.request.GetCertificateRequest;
import com.dangdang.reader.request.GetTimeFreeInfoRequest;
import com.dangdang.reader.request.RequestConstants;
import com.dangdang.reader.request.RequestResult;
import com.dangdang.reader.utils.Constant;
import com.dangdang.reader.utils.Utils;
import com.dangdang.reader.view.MobilNetDownloadPromptDialog;
import com.dangdang.zframework.utils.NetUtil;
import com.dangdang.zframework.utils.UiUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 原创书阅读页
 */
public class PartReadActivity extends ReadActivity {

    /**
     * 获取限时免费信息，包括书，是否全本，是否支持全本购买
     */
    protected void getTimeFreeInfo() {
        GetTimeFreeInfoRequest request = new GetTimeFreeInfoRequest(mReadInfo.getProductId(), handler);
        sendRequest(request);
    }

    protected boolean isInitVideo() {
        return !isPart();
    }

    /**
     * 开始阅读
     */
    protected void prepareRead() {
        byte[] bookCertKey = mReadInfo.getBookCertKey();
        if (bookCertKey == null || bookCertKey.length == 0) {
            GetCertificateRequest request = new GetCertificateRequest(mReadInfo.getDefaultPid(), null, handler);
            sendRequest(request);
        } else {
            startRead();
        }
    }

    /**
     * 进度条处理
     */
    protected void onComposingFinishUpdateProgress() {
        PartBook partBook = (PartBook) mReaderApps.getBook();
        List<Chapter> chapters = partBook.getChapterList();
        PartReaderController controller = (PartReaderController) mReaderApps.getReaderController();
        int index = chapters.indexOf(controller.getCurrentChapter());
        if (index == chapters.size() - 1)
            index = chapters.size();
        mToolbar.updateProgress(index, chapters.size());
    }

    /**
     * 进度处理，和出版物有区别。为章节进度显示
     *
     * @return
     */
    protected float getProgressFloat() {

        float progress = mReadInfo.getProgressFloat();
        try {
            PartBook partBook = (PartBook) mReaderApps.getBook();
            List<Chapter> chapters = partBook.getChapterList();
            if (chapters == null) {
                return progress;
            }
            PartReaderController controller = (PartReaderController) mReaderApps.getReaderController();
            int index = chapters.indexOf(controller.getCurrentChapter()) + 1;
            if (index > 0) {
                progress = index * 100f / chapters.size();
                progress = Utils.retainDecimal(progress, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        printLog(" getProgressFloat " + progress);
        return progress;
    }

    private void bulkPurchase() {

    }

    /**
     * 购买全本
     *
     * @param bookId
     */
    protected void oneKeyBuy(String bookId) {
//        if ((mReadInfo instanceof PartReadInfo) && (!((PartReadInfo) mReadInfo).isSupportFull())) {
//            bulkPurchase();
//        } else {
//            BuyBookHandle buyBookHandle = new BuyBookHandle(this, ((PartReadInfo) mReadInfo).getSaleId(), bookId, REQUEST_CODE_BUY, StoreEBookBuyHandle.FROM_EBOOK_DETAIL, mRootView);
//            buyBookHandle.buy();
//        }

    }

    /**
     * 购买成功
     */
    protected void dealBuySuccess() {
        if (mToolbar.isShowing())
            toolBarSwitchShowing(1);
        mReadInfo.setTryOrFull(ShelfBook.TryOrFull.FULL.ordinal());
        mReadInfo.setBought(true);
        if (NetUtil.isMobileConnected(mContext) && !DDApplication.getApplication().isMobileNetAllowDownload()) {
            showMobilNetDownloadPromptDialog();
        } else {
            ReadBookUtil.addBook2Shelf(PartReadActivity.this, (PartReadInfo) mReadInfo, true, true);
            UiUtil.showToast(mContext, R.string.buy_success);
        }

    }

    /**
     * 网络提示
     */
    private void showMobilNetDownloadPromptDialog() {
        final MobilNetDownloadPromptDialog dialog = new MobilNetDownloadPromptDialog(mContext);
        dialog.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DDApplication.getApplication().setIsMobileNetAllowDownload(true);
                ReadBookUtil.addBook2Shelf(PartReadActivity.this, (PartReadInfo) mReadInfo, true, true);
                UiUtil.showToast(mContext, R.string.buy_success);
                dialog.dismiss();
            }
        });
        dialog.setOnLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadBookUtil.addBook2Shelf(PartReadActivity.this, (PartReadInfo) mReadInfo, true, false);
                UiUtil.showToast(mContext, R.string.buy_success);
            }
        });
        dialog.show();
    }

    @Override
    public void onProgressBarChangeEnd(int progress) {
        // final int pageSize = bookManager.getPageSize();
        int chapterIndex = progress;// * pageSize / 100;
        if (chapterIndex > 1)
            chapterIndex--;
        PartBook partBook = (PartBook) mReaderApps.getBook();
        List<Chapter> chapters = partBook.getChapterList();
        if (chapterIndex < 0 || chapterIndex > chapters.size() - 1)
            return;
        gotoReadChapterProgress(chapters.get(chapterIndex));
    }

    /**
     * 进度条跳转
     *
     * @param chapter
     */
    private void gotoReadChapterProgress(Chapter chapter) {
        GoToParams goParams = new GoToParams();
        goParams.setType(IEpubReaderController.GoToType.Anchor);
        goParams.setChapter(chapter);

        mReaderApps.doFunction(FunctionCode.FCODE_GOTO_PAGECHAPTER,
                goParams);
    }

    protected boolean changeLocalReadProgress(int chapterIndex, int elementIndex,
                                           int cloudPageIndex) {
        chapterIndex = chapterIndex > 0 ? chapterIndex : 0;
        PartBook partBook = (PartBook) mReaderApps.getBook();
        List<Chapter> chapters = partBook.getChapterList();
        gotoReadChapterProgress(chapters.get(chapterIndex));
        clearFloatLayer();
        updateProgress(false,1);
        return true;
    }

    @Override
    protected void handleOtherWhat(Message msg) {
        super.handleOtherWhat(msg);
        switch (msg.what) {
            case Constants.MSG_WHAT_GETCERT_SUCCESS:
                onGetCertSuccess((RequestResult) msg.obj);
                break;
            case Constants.MSG_WHAT_GETCERT_FAILED:
                onGetCertFailed((RequestResult) msg.obj);
                break;
            case RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS:
                RequestResult result = (RequestResult) msg.obj;
                if ("getTimeFreeInfo".equals(result.getAction())) {
                    onGetTimeFreeInfoSuccess(result.getResult());
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理限时免费结果
     *
     * @param result
     */
    protected void onGetTimeFreeInfoSuccess(Object result) {
        if (result == null)
            return;
        TimeFreeInfo timeFreeInfo = (TimeFreeInfo) result;
        PartReadInfo partReadInfo = (PartReadInfo) mReadInfo;
        partReadInfo.setIsFull(ShelfBook.BookType.BOOK_TYPE_IS_FULL_YES == ShelfBook.BookType.valueOf(timeFreeInfo.getIsFull()));
        partReadInfo.setIsSupportFull(1 == timeFreeInfo.getIsSupportFullBuy());
        partReadInfo.setIndexOrder(timeFreeInfo.getLastIndexOrder());
        partReadInfo.setIsTimeFree(1 == timeFreeInfo.getIsTimeFree());
    }

    /**
     * 取证书成功
     *
     * @param result
     */
    protected void onGetCertSuccess(RequestResult result) {
        byte[] key = DrmWrapUtil.getPartBookCertKey((String) result.getResult());
        printLog("onGetCertSuccess  key = " + Arrays.toString(key));
        mReadInfo.setBookCertKey(key);
//        DrmWarp.getInstance().initBookKey(null, key, mReadInfo.getDefaultPid(), false);
        startRead();
    }

    /**
     * 取证书失败
     *
     * @param result
     */
    protected void onGetCertFailed(RequestResult result) {
        String str = result.getExpCode().errorMessage;
        showToast(str);
        finish();
    }

    protected void refreshCurrentProgress(IBook book) {
        Chapter chapter = getBook(book).getChapter(mReadInfo.getChapterIndex());
        if (!mBookManager.isCacheChapter(chapter)) {
            mBookManager.getChapterPageCount(chapter);
        }
        printLogE("refreshCurrentProgress ,chapter = " + chapter.getPath());
        getController().gotoPage(chapter, mReadInfo.getElementIndex());
    }

    /**
     * 当前状态是否可以退出
     *
     * @return
     */
    protected boolean canExit() {
        return true;
    }

    /**
     * 自动添加到书架
     *
     * @return
     */
    private boolean isAutoAdd2Shelf() {
        PartReadInfo partReadInfo = (PartReadInfo) mReadInfo;
        return partReadInfo.isFollow() || partReadInfo.isBoughtChapter();
    }

    private ExitReadDialog mExitDialog;

    /**
     * 显示添加书签dialog
     */
    private void showConfirmDialog() {
        if (mExitDialog == null) {
            mExitDialog = new ExitReadDialog(this);
            mExitDialog.setListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = v.getId();
                    if (i == R.id.dialog_exit_read_ok_btn) {
                        add2Shelf(false);

                    } else {
                    }
                    mExitDialog.dismiss();
                    attemptExit(true);
                }
            });

        }
        mExitDialog.show();
    }

    @Override
    protected void sendBroadCastToShelf(String bid, String progressInfo, long lasttime) {
        PartBook partBook = (PartBook) mReaderApps.getBook();
        List<Chapter> chapters = partBook.getChapterList();
        int lastIndex = chapters.size() - 1;
        if (lastIndex < 0)
            lastIndex = 0;
        DataUtil.getInstance(this).reorderBook(bid, progressInfo, mReadInfo.getBookCertKey(), ((PartReadInfo) mReadInfo).isFollow(), ((PartReadInfo) mReadInfo).isAutoBuy()/*是否自动购买*/, lastIndex);
    }

    private void add2Shelf(boolean isAuto) {
        //已登录用户自动追更
        boolean result = ReadBookUtil.addBook2Shelf(PartReadActivity.this, (PartReadInfo) mReadInfo);
        if (result && !isAuto) {
            UiUtil.showToast(mContext, R.string.add2shelf_success);
        }
    }

    @Override
    public void follow() {
        PartReadInfo partReadInfo = (PartReadInfo) mReadInfo;
        partReadInfo.setIsFollow(true);
        showToast(R.string.follow_success);
        sendFollowBroadCast();
    }

    protected void getCloudReadProgress() {

    }

    @Override
    protected void submitReadProgressToCloud() {

    }

    @Override
    protected void submitReadInfoToCloud(String marksJson, String notesJson) {

    }

    protected void getCloudReadInfo() {

    }

    @Override
    public void unFlollow() {
        PartReadInfo partReadInfo = (PartReadInfo) mReadInfo;
        partReadInfo.setIsFollow(false);
        showToast(R.string.unfollow_success);
        sendFollowBroadCast();
    }

    /**
     * 更新结束页的追更状态用
     */
    private void sendFollowBroadCast() {
        Intent intent = new Intent(Constant.ACTION_PART_READ_FOLLOW);
        sendBroadcast(intent);
    }

    /**
     * 充值成功
     *
     * @param intent
     */
    protected void onRechargeSucess(Intent intent) {
        int main = intent.getIntExtra("mainBalance", 0);
        int sub = intent.getIntExtra("subBalance", 0);
        int type = intent.getIntExtra("rechargeType", 1);
        BuyDialogManager.getInstance().updateBalanceInfo(main, sub, type);
    }

    @Override
    protected void startBookDetailActivity() {
//        BuyBookStatisticsUtil.getInstance().setShowType(BuyBookStatisticsUtil.ShowType.SHOW_TYPE_READ);
//        BuyBookStatisticsUtil.getInstance().setShowTypeId("");
//
//        StoreEBookDetailActivity.launch(this, ((PartReadInfo) mReadInfo).getSaleId(), mReadInfo.getProductId(), MediaOrderSource.DEFAULT);
    }
}
