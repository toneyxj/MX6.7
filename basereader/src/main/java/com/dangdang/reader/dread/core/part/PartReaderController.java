package com.dangdang.reader.dread.core.part;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.dangdang.reader.R;
import com.dangdang.reader.base.BaseReaderActivity;
import com.dangdang.reader.cloud.CloudSyncConfig;
import com.dangdang.reader.dread.core.base.GoToParams;
import com.dangdang.reader.dread.core.epub.EpubReaderController;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.PartReadInfo;
import com.dangdang.reader.dread.dialog.BuyDialogManager;
import com.dangdang.reader.dread.format.BaseBookManager;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.IndexRange;
import com.dangdang.reader.dread.format.part.IPartChapterHandle;
import com.dangdang.reader.dread.format.part.PartBuyInfo;
import com.dangdang.reader.dread.format.part.PartChapter;
import com.dangdang.reader.dread.holder.PromptResource;
import com.dangdang.reader.utils.Constant;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.NetUtil;

import java.lang.ref.WeakReference;

/**
 * 原创控制器
 * @author luxu
 */
public class PartReaderController extends EpubReaderController {

    public PartReaderController(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mAsyncHandler = new MyHandler(this);
	}

	private Handler mAsyncHandler;

    /**
     * 提示信息或退出阅读
     *
     * @param exit  是否退出
     * @param resId 提示信息
     */
    private void tipsAndExit(final boolean exit, final int resId) {
        if (mAsyncHandler == null) {
            return;
        }
        mAsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                showToast(resId);
                if (exit) {
                    final Activity activity = ReaderAppImpl.getApp().getContext();
                    if (activity != null) {
                        Intent intent = new Intent();
                        intent.setAction(Constant.ACTION_FINISH_READ);
                        activity.sendBroadcast(intent);
                    }
                }
            }
        });
    }

    /**
     * 解析完成够跳转页面
     *
     * @param params
     */
    private void gotoPageAfterLoad(GoToParams params) {
        if (getCurrentChapter() != null) {
            reset();
            resetUI();
            repaintUI();
            setCurrentChapter(null);
        }
        BaseBookManager.GotoPageCommand gotoCommand = new BaseBookManager.GotoPageCommand();
        gotoCommand.setElementIndex(params.getElementIndex());
        gotoCommand.setChapter(params.getChapter());
        gotoCommand.setType(GoToType.convertInt(GoToType.ElementIndex));
        getCWrapper().asynGotoPage(gotoCommand, gotoPageListener);
//        Chapter chapter = params.getChapter();
//        int pageIndexInChapter = getCWrapper().getPageIndexInChapter(chapter,params.getElementIndex());
//        IndexRange pageRange = mBookManager.getPageStartAndEndIndex(chapter,params.getElementIndex());
//        gotoPageFinish(chapter, pageIndexInChapter, pageRange);
    }

    @Override
    public boolean canScroll(DPageIndex pageIndex) {

        final int chapterPageCount = getCurrentChapterPageCount();
        final int currentPageIndexInChapter = getCurrentPageIndexInChapter();
        final Chapter chapter = mCurrentChapter;

        LogM.i(getClass().getSimpleName(),
                "lxu canScroll 0 mCurrentHtml.path = " + chapter
                        + ", CurrentPageRange = " + getCurrentPageRange()
                        + ", CurrentPageIndexInChapter = "
                        + currentPageIndexInChapter + ", chapterPageCount = "
                        + chapterPageCount + ", pageIndex = " + pageIndex);
        if (chapter == null) {
            return false;
        }

        if (pageIndex == DPageIndex.Next) {


			/*
             * if(isLoadingPage){ return false; }
			 */
            final boolean isLastHtml = isLastChapter(chapter);
            if (isLastHtml) {
                if (isLastPageInChapter()) {
                    showToast(PromptResource.LastPagePrompt);
                }
                return currentPageIndexInChapter < chapterPageCount;
            } else {
//                非最后一章
                if (chapterPageCount == getCurrentPageIndexInChapter()) {
                    //最后一页
                    PartChapter nextChapter = (PartChapter) getCWrapper().getPrevOrNextChapter(DPageIndex.Next, chapter);
                    if (getCWrapper().getChapterPageCount(nextChapter) <= 0) {
                        if (isNeedLogin(nextChapter)) {
                            login(nextChapter, false, DChapterIndex.Next);
                            return false;
                        }
                        // 购买信息
                        boolean buy = isShowBuyInfo(nextChapter);
                        if (buy) {
                            showBuyInfo(nextChapter, false, DChapterIndex.Next);
                            return false;
                        }
                        // 本地解析出错
                        if (nextChapter != null && nextChapter.getPageCount() <= 0) {
                            showToast(R.string.read_error_tips);
                            return false;
                        }
                    }

                }
                return true;
            }
        } else if (pageIndex == DPageIndex.Previous) {

            /*
             * if(mReComposing){ return false; }
			 */
            // TODO 如果当前是loadingPage 点击上一页 ？
            final boolean isFirstHtml = isFirstChapter(chapter);
            if (isFirstHtml) {
                if (isFirstPageInChapter()) {
                    printLog(" first page in book ");
                    showToast(PromptResource.FirstPagePrompt);
                }
                return currentPageIndexInChapter > 1;
            } else {
                //非第一章
                if (1 == getCurrentPageIndexInChapter()) {
                    //第一页
                    // 滑倒上一章，检查购买信息
                    PartChapter preChapter = (PartChapter) getCWrapper().getPrevOrNextChapter(DPageIndex.Previous, chapter);
                    printLogE("preChapter = " + preChapter + " buyinfo = " + preChapter.getPartBuyInfo());
                    // 登录信息
                    if (isNeedLogin(preChapter)) {
                        login(preChapter, true, DChapterIndex.Previous);
                        return false;
                    }
                    // 购买信息
                    if (isShowBuyInfo(preChapter)) {
                        showBuyInfo(preChapter, true, DChapterIndex.Previous);
                        return false;
                    }
                    // 本地解析出错
                    if (preChapter != null && preChapter.getPageCount() <= 0) {
                        showToast(R.string.read_error_tips);
                        return false;
                    }
                }
                return true;
            }
        }

        return true;
    }

    /**
     * 是否需要登录
     *
     * @param chapter
     * @return
     */
    private boolean isNeedLogin(PartChapter chapter) {
        if (chapter == null) {
            return false;
        }
        return chapter.getCode() == IPartChapterHandle.NEED_LOGIN;
    }

    private synchronized void login(PartChapter chapter, boolean isPre, DChapterIndex index) {
//        if (DataUtil.getInstance(getContext()).getCurrentUser() != null) {
//            tryLoadAgain(chapter, isPre, index);
//        } else {
//            LaunchUtils.launchLogin(getContext());
//        }
    }

    /**
     * 是否显示某一章的购买信息
     *
     * @param chapter
     * @return
     */
    private boolean isShowBuyInfo(PartChapter chapter) {
        if (chapter == null) {
            return false;
        }
        return chapter.getCode() != IPartChapterHandle.PARSE_SUCEESS;
    }

    /**
     * 显示购买信息，当前章显示。上一章下一章，尝试再次获取
     *
     * @param chapter
     * @param isPre
     * @param index
     */
    private void showBuyInfo(PartChapter chapter, boolean isPre, DChapterIndex index) {
        printLog("showBuyInfo ,chapter=" + chapter);
        PartBuyInfo buyInfo = chapter.getPartBuyInfo();
        switch (index) {
            case Previous:
                if (buyInfo == null) {
                    tryLoadAgain(chapter, isPre, index);
                } else {
                    if (isAutoBuy(buyInfo)) {
                        GoToParams params = new GoToParams();
                        params.setChapter(chapter);
                        params.setElementIndex(0);
                        if (isPre) {
                            params.setGotoLast(true);
                        }
                        params.setBuy(true);
                        gotoPage(params);
                    } else {
                        BuyDialogManager.getInstance().showBuyInfo(chapter, isPre);
                    }
                }

                break;
            case Next:
                if (buyInfo == null) {
                    printLog("showBuyInfo , chapterindex=" + chapter.getIndex());
                    tryLoadAgain(chapter, isPre, index);
                } else {
                    BuyDialogManager.getInstance().showBuyInfo(chapter, isPre);
                }
                break;
            case Current:
                BuyDialogManager.getInstance().showBuyInfo(chapter, isPre);
                break;
            default:
                break;
        }

    }

    /**
     * 是否自动购买。开关设置。余额充足，网络可用
     *
     * @param buyInfo
     * @return
     */
    private boolean isAutoBuy(PartBuyInfo buyInfo) {
        PartReadInfo partReadInfo = (PartReadInfo) mReaderApp.getReadInfo();
        boolean result = partReadInfo.isAutoBuy() && new CloudSyncConfig(getContext()).getNovelPreload();
        if ((buyInfo.getMainBalance() + buyInfo.getSubBalance()) < buyInfo.getSalePrice()) {
            result = false;
        }
        if (!NetUtil.isNetworkConnected()) {
            result = false;
        }
        return result;
    }

    /**
     * 再次获取章节信息
     *
     * @param chapter
     * @param isPre
     * @param index
     */
    private void tryLoadAgain(PartChapter chapter, boolean isPre, DChapterIndex index) {
        if (getCurrentChapter() != null) {
            showGifLoadingByUi();
        }
        printLog("tryLoadAgain ,chapter=" + chapter);
        GoToParams params = new GoToParams();
        params.setChapter(chapter);
        params.setGotoLast(isPre);
        getCWrapper().asynLoadChapter(chapter, new SimpleLoadChapterListener(params), false, index);
    }

    @Override
    public void onScrollingEnd(DPageIndex pageIndex) {
        super.onScrollingEnd(pageIndex);
        int pageIndexInChapter = getCurrentPageIndexInChapter();
        Chapter currentChapter = getCurrentChapter();
        if (currentChapter != null) {
            int tPageIndex = pageIndexInChapter;
            if (hasReadEndPage() && isLastPageInBook()) {
                tPageIndex = pageIndexInChapter - 1;
            }
            IndexRange pageRange = getPageRange(currentChapter, tPageIndex);
            if (pageRange != null && pageRange.getStartIndex() != pageRange.getEndIndex()) {
                setCurrentPageRange(pageRange);
                updateReadProgress(currentChapter, pageRange.getStartIndexToInt());

            }
            if (pageIndexInChapter == getCWrapper().getChapterPageCount(currentChapter)) {
                PartChapter preChapter = (PartChapter) getCWrapper().getPrevOrNextChapter(DPageIndex.Previous, currentChapter);
                if (preChapter != null) {
                    getCWrapper().asynLoadChapter(preChapter, mLoadChapterListener, false, DChapterIndex.Previous);
                }
            }
            if (pageIndexInChapter == 1) {
                PartChapter nextChapter = (PartChapter) getCWrapper().getPrevOrNextChapter(DPageIndex.Next, currentChapter);
                if (nextChapter != null) {
                    getCWrapper().asynLoadChapter(nextChapter, mLoadChapterListener, false, DChapterIndex.Next);
                }
            }
        }

    }

    @Override
    public void gotoPage(final Chapter chapter, final int elementIndex) {
        if (chapter == null) {
            LogM.e(getClass().getSimpleName(), " gotoPage chapter is null ");
            return;
        }

        GoToParams params = new GoToParams();
        params.setChapter(chapter);
        params.setElementIndex(elementIndex);
        params.setType(GoToType.ElementIndex);
        gotoPage(params);


    }

    /**
     * 排版当前章回调
     */
    private class SimpleLoadChapterListener implements PartControllerWrapperImpl.ILoadChapterListener {
        GoToParams params;

        public SimpleLoadChapterListener(GoToParams params) {
            this.params = params;
        }

        @Override
        public void onLoadFinish(final PartChapter chapter, int result) {
            printLog("onLoadFinish ,chapter=" + chapter + ",result=" + result + ",params=" + (params == null ? "params= null" : params));
            hideGifLoadingByUi();
            if (result > 0) {
                dealLoadSuccess(chapter, result, params);
            } else {
                chapter.setPageCount(0);
                if (params != null) {
                    mAsyncHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dealLoadFailed(chapter, params);
                        }
                    });
                }
                printLog("dealLoadOriginalFailed ,chapter=" + chapter);
            }

        }
    }

    private void dealLoadSuccess(PartChapter chapter, int result, final GoToParams params) {
        printLog("dealLoadSuccess ChapterIndex=" + chapter.getIndex() + "result=" + result + "params" + params);
        chapter.setPartBuyInfo(null);
        chapter.setCode(IPartChapterHandle.PARSE_SUCEESS);
        if (result > 0) {
            int count = result;
            if (isLastChapter(chapter)) {
                printLog("onLoadSuccess islastchapter=true,pagecount++");
                count++;
            }
            chapter.setPageCount(count);
            if (params != null) {
                if (params.isGotoLast()) {
                    IndexRange ir = getPageRange(chapter, chapter.getPageCount());
                    params.setElementIndex(ir.getEndIndexToInt());
                }
                mAsyncHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        gotoPageAfterLoad(params);
                    }
                });
            }
        } else {
            if (params != null) {
                tipsAndExit(getCurrentChapter() == null, R.string.read_error_tips);
            }
        }

    }

    /**
     * 收到msg后，处理排版原创书失败结果
     */
    private void dealLoadFailed(PartChapter chapter, GoToParams params) {
        printLogE("dealLoadFailed----chapter = " + chapter);
        switch (chapter.getCode()) {
            case IPartChapterHandle.NEED_LOGIN:
                login(chapter, params.isGotoLast(), DChapterIndex.Current);
                break;
            case IPartChapterHandle.PERMISSION_DENINED:
                if (chapter != null && isShowBuyInfo(chapter)) {
                    showBuyInfo(chapter, false, DChapterIndex.Current);
                }
                break;
            case IPartChapterHandle.MEDIA_FORCE_UNSHELVE:
                    tipsAndExit(getCurrentChapter()==null, R.string.read_force_unshelve_tips);
                break;
            case IPartChapterHandle.MEDIA_NOT_FOUND:
                //查不到资源
                tipsAndExit(getCurrentChapter() == null, R.string.read_media_not_found);
                break;
             case IPartChapterHandle.DWONLOAD_NETERROR:
                 // 网络链接错误
                tipsAndExit(getCurrentChapter() == null, R.string.no_net_tip_try_again);
                break;
            default:
                tipsAndExit(getCurrentChapter() == null, R.string.read_error_tips);
                break;
        }

    }

    /**
     * 排版前后章回调
     */
    private PartControllerWrapperImpl.ILoadChapterListener mLoadChapterListener = new PartControllerWrapperImpl.ILoadChapterListener() {
        @Override
        public void onLoadFinish(PartChapter chapter, int result) {
            if (result > 0) {
                dealLoadSuccess(chapter, result, null);
            } else {
//             dealLoadOriginalFailed(chapter.getCode(),chapter, chapter.getPartBuyInfo(), null);
                chapter.setPageCount(0);
            }
        }
    };

    @Override
    public PartControllerWrapperImpl getCWrapper() {
        return (PartControllerWrapperImpl) super.getCWrapper();
    }

    @Override
    protected void gotoPageFinish(Chapter chapter, int pageIndexInChapter, IndexRange pageRange) {
        hideGifLoadingByUi();
        super.gotoPageFinish(chapter, pageIndexInChapter, pageRange);
    }

    @Override
    public void gotoPage(int pageIndexInBook) {
        super.gotoPage(pageIndexInBook);
    }

    @Override
    public void gotoPage(GoToParams params) {
        Chapter chapter = params.getChapter();
        if (chapter == null) {
            printLogE("gotoPage  chapter is  null");
            return;
        }
        boolean isBuy = params.isBuy();
        if (isBuy) {
            PartReadInfo partReadInfo = (PartReadInfo) mReaderApp.getReadInfo();
            partReadInfo.setIsBoughtChapter(true);
            partReadInfo.setIsFollow(true);
            reset();//
            resetUI();
            repaintUI();
            setCurrentChapter(null);
        }
        if (getCurrentChapter() != null) {
            showGifLoadingByUi();
        }
        getCWrapper().asynLoadChapter(chapter, new SimpleLoadChapterListener(params), isBuy, DChapterIndex.Current);
        printLog("gotoPage,currentChapterIndex=" + chapter.getPath());
        Chapter nextChapter = getCWrapper().getPrevOrNextChapter(DPageIndex.Next, chapter);
        if (nextChapter != null) {
            // 预排版后一章
            printLog("gotoPage,nextChapterIndex=" + nextChapter.getPath());
            getCWrapper().asynLoadChapter(nextChapter, mLoadChapterListener, false, DChapterIndex.Next);
        }
        Chapter preChapter = getCWrapper().getPrevOrNextChapter(DPageIndex.Previous, chapter);
        if (preChapter != null) {
            // 预排版前一章
            printLog("gotoPage,preChapterIndex=" + preChapter.getPath());
            getCWrapper().asynLoadChapter(preChapter, mLoadChapterListener, false, DChapterIndex.Previous);
        }
    }

    private void showGifLoadingByUi() {
        final BaseReaderActivity activity = (BaseReaderActivity) ReaderAppImpl.getApp().getContext();
        if (activity != null) {
            mAsyncHandler.post(new Runnable() {
                @Override
                public void run() {
                    activity.showGifLoadingByUi();
                }
            });

        }
    }

    private void hideGifLoadingByUi() {
        final BaseReaderActivity activity = (BaseReaderActivity) ReaderAppImpl.getApp().getContext();
        if (activity != null) {
            mAsyncHandler.post(new Runnable() {
                @Override
                public void run() {
                    activity.hideGifLoadingByUi();
                }
            });
        }
    }
    
    private static class MyHandler extends Handler {
		private final WeakReference<PartReaderController> mFragmentView;

		MyHandler(PartReaderController view) {
			this.mFragmentView = new WeakReference<PartReaderController>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			PartReaderController service = mFragmentView.get();
			if (service != null) {
				super.handleMessage(msg);
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
