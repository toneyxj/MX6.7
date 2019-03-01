package com.dangdang.reader.dread.core.epub;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.GalleryViewActivity;
import com.dangdang.reader.dread.ReadActivity;
import com.dangdang.reader.dread.config.NoteRect;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.config.TmpRect;
import com.dangdang.reader.dread.core.base.BaseEpubPageView;
import com.dangdang.reader.dread.core.base.IReaderController.DAnimType;
import com.dangdang.reader.dread.core.base.IReaderWidget.DrawPoint;
import com.dangdang.reader.dread.core.epub.GalleryView.OnGalleryPageChangeListener;
import com.dangdang.reader.dread.data.GallaryData;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.holder.CommonParam;
import com.dangdang.reader.dread.jni.InteractiveBlockHandler.InteractiveBlock;
import com.dangdang.reader.dread.media.StreamOverHttp.PrepareListener;
import com.dangdang.reader.dread.media.VideoService;
import com.dangdang.reader.dread.media.VideoService.OnVideoListener;
import com.dangdang.reader.dread.view.MagnifView;
import com.dangdang.reader.utils.Utils;
import com.dangdang.reader.view.FlowIndicator;
import com.dangdang.zframework.utils.UiUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

/**
 * 记住封装背景样式 处理loading背景...
 *
 * @author luxu
 *
 */
public class EpubPageView extends BaseEpubPageView {

	private Context mContext;
    private PageImageView mPageImageView;
    private PageHeaderView mHeaderView;
    private PageFooterView mFooterView;
    private View mShapeView;
    private MagnifView mMagnifView;
    private View mMarkView;
    private View mLoadingView;
    private GalleryView[] mGalleryViews;
    private ImageView mVideoImageView;
	private InteractiveBlockIconView[] mInteractiveIconImageView;
    private FlowIndicator[] mFlowIndicators;
    private OnGalleryPageChangeListener mGalleryListener;

    private boolean mDrawHeader = true;
    private boolean mDrawFooter = true;

    private int mShapeWidth = 5;
    private Handler mVideoHandler;
    private Rect mVideoRect;

    public EpubPageView(Context context) {
        super(context);
		mContext = context;
        init();
    }

    private void init() {
        mVideoHandler = new MyHandler(this);
        final int bgColor = getBgColor();
        mPageImageView = new PageImageView(getContext());
        // mPageImageView.setScaleType(DDImageView.ScaleType.FIT_CENTER);
        mPageImageView.setBackgroundColor(bgColor);

        mHeaderView = new PageHeaderView(getContext());
        mFooterView = new PageFooterView(getContext());
        mShapeView = new View(getContext());
        try {
            mShapeView.setBackgroundResource(R.drawable.read_shape);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        mMarkView = new View(getContext());
        try {
            mMarkView.setBackgroundResource(R.drawable.read_bookmark_select);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        mMarkView.setVisibility(View.GONE);
        // mHeaderView.setBackgroundColor(Color.RED);
        // mFooterView.setBackgroundColor(Color.RED);

        addView(mPageImageView);
        addView(mHeaderView);
        addView(mFooterView);
        addView(mShapeView);
        addView(mMarkView);

        animChangeAfter();

        int foreColor = getForeColor();
        mHeaderView.setColor(foreColor);
        mFooterView.setColor(foreColor);
        mHeaderView.setMaxWidth(getScreenWidth() - 2 * getMarginLeft());

        repaintFooter();
    }

    @Override
    public void updatePageStyle() {
        if (mPageImageView != null) {
            mPageImageView.setBackgroundColor(getBgColor());
        }
        int foreColor = getForeColor();
        if (mHeaderView != null) {
            mHeaderView.setColor(foreColor);
        }
        if (mFooterView != null) {
            mFooterView.setColor(foreColor);
        }
    }

    @Override
    public void animChangeAfter() {
        DAnimType animType = ReadConfig.getConfig().getAnimationTypeNew();
        if (animType == DAnimType.Slide) {
            mShapeWidth = (int) (5 * getDensity());
            mShapeView.setVisibility(View.VISIBLE);
        } else {
            mShapeWidth = 0;
            mShapeView.setVisibility(View.GONE);
        }
    }

    @Override
    public void startAnimation(Animation animation) {
        mPageImageView.startAnimation(animation);
    }

	/*
	 * @Override protected void dispatchDraw(Canvas canvas) {
	 * printLogV(" dispatchDraw in " + this); super.dispatchDraw(canvas);
	 * printLogV(" dispatchDraw out " + this); }
	 */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = 0, height = 0;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        switch (widthMode) {
            case View.MeasureSpec.UNSPECIFIED:
                width = getScreenWidth() + mShapeWidth;
                break;
            default:
                width = getScreenWidth() + mShapeWidth;// View.MeasureSpec.getSize(widthMeasureSpec);
        }
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        switch (heightMode) {
            case View.MeasureSpec.UNSPECIFIED:
                height = getScreenHeight();
                break;
            default:
                height = getScreenHeight();// View.MeasureSpec.getSize(heightMeasureSpec);
        }
         printLog(" onMeasure width = " + width + ", height = " + height);
        setMeasuredDimension(width, height);
        int c = getChildCount();
        for (int i = 0; i < c; i++) {
            measureView(getChildAt(i));
        }
    }

    private void measureView(View v) {
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        v.measure(View.MeasureSpec.EXACTLY | (int) (v.getMeasuredWidth()),
                View.MeasureSpec.EXACTLY | (int) (v.getMeasuredHeight()));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        final int screenWidth = getScreenWidth();
        int width = screenWidth;// r - l;
        int height = b - t;
        mPageImageView.layout(0, 0, width, height);

        // printLogV(" onLayout " + changed + "," + l + ", " + t + "," + r + ","
        // + b);

        int marginLeft = getMarginLeft();
        int marginRight = marginLeft;

        if (mHeaderView.isShow()) {
            int headLeft = marginLeft;
            int headTop = getMarginTop();
            int headRight = width - marginRight;
            int headBottom = headTop + mHeaderView.getMeasuredHeight();
            mHeaderView.layout(headLeft, headTop, headRight, headBottom);
        }

        if (mFooterView.isShow()) {
            int footLeft = marginLeft;
            int footTop = height - mFooterView.getMeasuredHeight()
                    - getMarginBottom();
            int footRight = width - marginRight;
            int footBottom = footTop + mFooterView.getMeasuredHeight();
            mFooterView.layout(footLeft, footTop, footRight, footBottom);
        }

        if (mLoadingView != null && mLoadingView.getVisibility() == VISIBLE) {
            int loadingLeft = (width - mLoadingView.getMeasuredWidth()) / 2;
            int loadingTop = (height - mLoadingView.getMeasuredHeight()) / 2;
            int loadingRight = loadingLeft + mLoadingView.getMeasuredWidth();
            int loadingBottom = loadingTop + mLoadingView.getMeasuredHeight();
            mLoadingView.layout(loadingLeft, loadingTop, loadingRight,
                    loadingBottom);
        }
		/*
		 * if(mDynamicView != null && mDynamicView.getVisibility() == VISIBLE){
		 * mDynamicView.store_ebook_pay_activity(0, 0, width, height); } if(mStaticView != null &&
		 * mStaticView.getVisibility() == VISIBLE){ mStaticView.store_ebook_pay_activity(0, 0,
		 * width, height); }
		 */
        if (mShapeView != null && mShapeView.getVisibility() == VISIBLE) {
            // printLog("onLayout  " + width + ", " + height + ", shapeL=" +
            // (width-mShapeWidth));
            mShapeView.layout(width, 0, width + mShapeWidth, height);
        }

        if (mMarkView != null && mMarkView.getVisibility() == VISIBLE) {
            int dip2px = Utils.dip2px(getContext(), 10);
            mMarkView.layout(
                    screenWidth - dip2px - mMarkView.getMeasuredWidth(), 0,
                    screenWidth - dip2px, mMarkView.getMeasuredHeight());
        }
        if (mGalleryViews != null && mGalleryViews.length > 0) {
            for (int i = 0; i < mGalleryViews.length; i++) {
                try {
                    GallaryData galleryData = mGalleryViews[i].getGalleryData();
                    Rect imageRect = galleryData.getImageRect();
                    mGalleryViews[i].layout(imageRect.left, imageRect.top,
                            imageRect.right, imageRect.bottom);
                    Rect pointsRect = galleryData.getPointsRect();
                    mFlowIndicators[i].layout(pointsRect.left, pointsRect.top,
                            pointsRect.right, pointsRect.bottom);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (mMagnifView != null && mMagnifView.getVisibility() == VISIBLE) {
            mMagnifView.layout(0, 0, width, height);
        }
        if (mVideoImageView != null && mVideoRect != null) {
            mVideoImageView.layout(mVideoRect.left, mVideoRect.top,
                    mVideoRect.right, mVideoRect.bottom);
        }
        if (mVideoView != null) {
            mVideoView.layout(l, t, r, b);
        }
		if (mInteractiveIconImageView != null) {
			for (int i = 0; i < mInteractiveIconImageView.length; i++) {
				Rect rect = mInteractiveIconImageView[i].getRect();
				if (mInteractiveIconImageView[i].getVisibility() == View.VISIBLE)
                    mInteractiveIconImageView[i].layout(rect.left, rect.top, rect.right, rect.bottom);
			}
		}
			
    }

    private int getMarginLeft() {
        return getReadConfig().getDefaultPaddingLeftOrRight();// (int)getReadConfig().getPaddingLeft();
    }

    private int getMarginTop() {
        return (int) (12 * getDensity());
    }

    public int getMarginBottom() {
        return (int) (10 * getDensity());
    }

	/*
	 * public void addNote(Rect[]... rectss){ if(mStaticView == null){
	 * mStaticView = new NoteOperationView(getContext()); addView(mStaticView);
	 * } mStaticView.drawRects(DrawingType.Line, null, null, rectss);
	 * 
	 * mPageImageView.drawRects(DrawingType.Line, null, null, rectss); }
	 */

    @Override
    public int doDrawing(DrawingType type, DrawPoint start, DrawPoint end,
                         DrawPoint current, Rect[] rects, int drawLineColor) {

		/*
		 * if(mDynamicView == null){ mDynamicView = new
		 * NoteOperationView(getContext()); addView(mDynamicView); }
		 * mDynamicView.drawRects(type, start, end, rects);
		 */
        if (type == DrawingType.Line || type == DrawingType.Shadow) {
            showMagnifView();
        }
        mPageImageView.doDrawing(type, start, end, current, drawLineColor, rects);
        mPageImageView.invalidate();
        if (mMagnifView != null) {
            mMagnifView.invalidate();
        }
        return 0;
    }

    protected void showMagnifView() {
        if (mMagnifView == null) {
            mMagnifView = new MagnifView(getContext());
            addView(mMagnifView);
            mPageImageView.setMagnifListener(mMagnifView);
            mMagnifView.bringToFront();
        } else {
            if (mMagnifView.getVisibility() != VISIBLE) {
                mMagnifView.setVisibility(View.VISIBLE);
                mMagnifView.bringToFront();
            }
        }
    }

    protected void hideMagnifView() {
        if (mMagnifView != null) {
            mMagnifView.setVisibility(View.GONE);
            mMagnifView.reset();
        }
    }

    @Override
    public int drawFinish(DrawingType type, DrawPoint current) {
        hideMagnifView();
        mPageImageView.drawFinish(type, current);
        return 0;
    }

    public void setTmpRects(DrawingType type, TmpRect... rectss) {
        mPageImageView.drawTmpSearchRects(type, rectss);
    }

    public void updatePageInner(boolean bookmarkExist, Bitmap bitmap,
                                DrawingType type, NoteRect... rectss) {
        mPageImageView.updatePageInner(bitmap, type, rectss);
        operationMarkView(bookmarkExist);
    }

    public void setVideoRect(Rect rect) {
        this.mVideoRect = rect;
        if (mVideoRect != null && mVideoImageView == null) {
            mVideoImageView = new ImageView(getContext());
            mVideoImageView.setImageResource(R.drawable.read_video_start_bg);
            mVideoImageView.setScaleType(ScaleType.CENTER);
            LayoutParams layoutParams = new LayoutParams(mVideoRect.width(),
                    mVideoRect.height());
            addView(mVideoImageView, layoutParams);
        }
    }

	
	public void setInteractiveBlocks(List<InteractiveBlock> listInteractiveBlocks, final Chapter chapter,
			final int pageIndexInChapter) {
		if (listInteractiveBlocks == null)
			return;
		
		if (listInteractiveBlocks.size() == 0)
			return;

        resetInteractiveImageView();
		mInteractiveIconImageView = new InteractiveBlockIconView[listInteractiveBlocks.size()];
		
		int i = 0;
		for (InteractiveBlock block : listInteractiveBlocks) {
			InteractiveBlockIconView view = new InteractiveBlockIconView(getContext());
			view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    //arg0.clearAnimation();
                    arg0.setVisibility(View.GONE);
                    requestLayout();
                    ((InteractiveBlockIconView) arg0).StartInteractiveBlockViewActivity();
                }
            });
			view.setImageResource(R.drawable.read_interactive_btn);
			view.setScaleType(ScaleType.FIT_CENTER);
			view.setChapter(chapter);
			view.setPageIndex(pageIndexInChapter);
			view.setInteractiveBlockIndex(block.getIndex());
			view.setRect(block.getIconRect());
			LayoutParams layoutParams = new LayoutParams(block.getIconRect().width(),
					block.getIconRect().height());
			addView(view, layoutParams);
			mInteractiveIconImageView[i] = view;
		}
		
	}

    public void setGallarys(GallaryData... datas) {
        if (mGalleryViews != null && mGalleryViews.length > 0) {
            return;
        }
        if (datas != null && datas.length > 0) {
            mGalleryViews = new GalleryView[datas.length];
            mFlowIndicators = new FlowIndicator[datas.length];
            for (int i = 0; i < mGalleryViews.length; i++) {
                GalleryView galleryView = new GalleryView(getContext());
                FlowIndicator flowIndicator = new FlowIndicator(getContext(),R.color.point_normal_color,R.color.blue_2390ec);
                flowIndicator.setSize(datas[i].getPointsRect());
                mFlowIndicators[i] = flowIndicator;
                galleryView.setFlowIndicator(flowIndicator);
                galleryView.setGalleryData(datas[i], i);
                String[] files = datas[i].getFiles();
                List<String> list = Arrays.asList(files);
                GalleryViewAdapter adapter = new GalleryViewAdapter(
                        getContext(), list, false);
                galleryView.setAdapter(adapter);
                galleryView.setGalleryPageChangeListener(mGalleryListener);
                mGalleryViews[i] = galleryView;
                galleryView.setOnGalleryClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (v != null && v instanceof GalleryView) {
                            GalleryView galleryView = (GalleryView) v;
                            GallaryData galleryData = galleryView
                                    .getGalleryData();
                            openGallery(galleryData,
                                    galleryView.getGalleryId(),
                                    galleryView.getCurrentPageIndex());
                        }
                    }
                });
                addView(galleryView);
                addView(flowIndicator);
            }
        }
    }

    private void openGallery(GallaryData galleryData, int galleryId,
                             int pageIndex) {
        final Intent intent = new Intent();
        intent.setClass(getContext(), GalleryViewActivity.class);
        intent.putExtra(GalleryViewActivity.KEY_GALLERY_URLS,
                galleryData.getFiles());
        intent.putExtra(GalleryViewActivity.KEY_GALLERY_RECT,
                galleryData.getImageRect());
		intent.putExtra(GalleryViewActivity.KEY_IMGDESC,
				galleryData.getImgTexts());
        intent.putExtra(GalleryViewActivity.KEY_GALLERY_PAGEINDEX, pageIndex);
        intent.putExtra(GalleryViewActivity.KEY_GALLERY_ID, galleryId);
		intent.putExtra(GalleryViewActivity.KEY_IMGBGCOLOR, galleryData.getImgBgColor());
		intent.putExtra(GalleryViewActivity.KEY_LANDSCAPE, ((ReadActivity)mContext).isLandScape());
        getContext().startActivity(intent);
    }

    public boolean isInGalleryRect(int mPressedX, int mPressedY) {
        boolean isInGalleryRect = false;
        if (mGalleryViews != null && mGalleryViews.length > 0) {
            try {
                for (int i = 0; i < mGalleryViews.length; i++) {
                    Rect galleryRect = mGalleryViews[i].getGalleryData()
                            .getGalleryRect();
                    if (galleryRect.contains(mPressedX, mPressedY)) {
                        isInGalleryRect = true;
                        break;
                    }
                }
            } catch (Exception e) {
            }
        }
        return isInGalleryRect;
    }

    public void operationMarkView(boolean bookmarkExist) {
        mMarkView.setVisibility(bookmarkExist ? VISIBLE : GONE);
    }

	/*
	 * public void setBitmap(Bitmap bitmap){
	 * mPageImageView.setImageBitmap(bitmap); }
	 */

    public void setHead(String name) {
        mHeaderView.setName(name);
    }
    /**
     * @param progress 出版物为页进度
     */
    public void setPageProgress(String progress){
        mFooterView.setPageProgress(progress);
    }

    public void setPage(String pageInfo) {
        mFooterView.setPage(pageInfo);
    }

    public boolean isDrawHeader() {
        return mDrawHeader;
    }

    public void setDrawHeader(boolean drawHeader) {
        this.mDrawHeader = drawHeader;
    }

    public boolean isDrawFooter() {
        return mDrawFooter;
    }

    public void setDrawFooter(boolean drawFooter) {
        this.mDrawFooter = drawFooter;
    }

    /**
     * View.VISIBLE, View.GONE
     *
     * @param visibility
     */
    public void setHeaderVisibility(int visibility) {
        mHeaderView.setVisibility(visibility);
    }

    /**
     * View.VISIBLE, View.GONE
     *
     * @param visibility
     */
    public void setFooterVisibility(int visibility) {
        mFooterView.setVisibility(visibility);
    }

    public void showHeaderAndFooter() {
        showHeader();
        showFooter();
    }

    public void hideHeaderAndFooter() {
        hideHeader();
        hideFooter();
    }

    public void showHeader() {
        setHeaderVisibility(View.VISIBLE);
    }

    public void hideHeader() {
        setHeaderVisibility(View.GONE);
    }

    public void showFooter() {
        setFooterVisibility(View.VISIBLE);
    }

    public void hideFooter() {
        setFooterVisibility(View.GONE);
    }

    public void showLoading() {
        if (mLoadingView == null) {
            mLoadingView = View.inflate(getContext(), R.layout.read_loading,
                    null);
            addView(mLoadingView);
        }
        TextView loadingTip = (TextView) mLoadingView
                .findViewById(R.id.read_loading_tip);
        loadingTip.setTextColor(getForeColor());
        mLoadingView.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
    }

    public boolean isLoading() {
        return mLoadingView != null && mLoadingView.getVisibility() == VISIBLE;
    }

    public void resetView() {
        hideLoading();
        hideMagnifView();
    }

    public void reset() {
        mPageImageView.reset();
        operationMarkView(false);
        resetGalleryView();
        resetVedioImageView();
        resetVedioViewInner();
        resetPageState();
        resetInteractiveImageView();
    }

    @Override
    public void repaintFooter() {
        float battery = CommonParam.getInstance().getmBatteryValue();
        String shortTime = CommonParam.getInstance().getmCurTime();
        mFooterView.setBatteryValue(battery);
        mFooterView.setTime(shortTime);
    }

    @Override
    public void clear() {
        printLog(" clear ");
        mPageImageView.clear();
        if (mMagnifView != null) {
            mMagnifView.clear();
        }
        mFooterView.clear();
        resetGalleryView();
        resetVedioImageView();
        resetVedioViewInner();
        resetInteractiveImageView();
    }

    private void resetVedioImageView() {
        if (mVideoImageView != null) {
            removeView(mVideoImageView);
            mVideoImageView = null;
            mVideoRect = null;
        }
    }

    private void resetVedioViewInner() {
        removeVedioView();
        destroyVedioServiceDF();
    }

    public void resetVedioView() {
        removeVedioView();
        destroyVedioService();
    }

    private void resetInteractiveImageView() {
        if (mInteractiveIconImageView != null) {
            for (int i = 0; i < mInteractiveIconImageView.length; i++) {
                removeView(mInteractiveIconImageView[i]);
                mInteractiveIconImageView[i] = null;
            }
            mInteractiveIconImageView = null;
        }
    }
    private void destroyVedioService() {
        if (mVideoService != null) {
            mVideoService.destroy();
            mVideoService = null;
        }
    }

    private void destroyVedioServiceWithOutOrientation() {
        if (mVideoService != null) {
            mVideoService.destroyWithOutOrientation();
            mVideoService = null;
        }
    }

    private void destroyVedioServiceDF() {
        if (mVideoService != null) {
            mVideoService.destroyAndDeleteFile();
            mVideoService = null;
        }
    }

    public void removeVedioView() {
        if (mVideoHandler != null) {
            mVideoHandler.removeMessages(MSG_VIDEO_INIT);
            mVideoHandler = null;
        }
        if (mVideoView != null) {
            removeView(mVideoView);
            mVideoView = null;
        }
    }

    private void resetGalleryView() {
        if (mGalleryViews != null && mGalleryViews.length > 0) {
            for (int i = 0; i < mGalleryViews.length; i++) {
                removeView(mGalleryViews[i]);
                removeView(mFlowIndicators[i]);
                mGalleryViews[i].reset();
            }
            mGalleryViews = null;
            mFlowIndicators = null;
        }
    }

    @Override
    public void setOnGalleryPageChangeListener(OnGalleryPageChangeListener l) {
        mGalleryListener = l;
    }

    @Override
    public String toString() {
        return " [EpubPageView-" + getTag(R.drawable.icon) + "] ";
    }

    public static interface IUpdateMagnifListener {

        public void updateMagnif(Bitmap bitmap, int x, int y);

    }

    private void hidePlayIcon() {
        if (mVideoImageView != null) {
            mVideoImageView.setVisibility(View.INVISIBLE);
        }
    }

    public void showPlayIcon() {
        if (mVideoImageView != null) {
            mVideoImageView.setVisibility(View.VISIBLE);
        }
    }

    public void playVideo(Rect imgRect, String innerPath, String localPath,
                          int bookType) {
        if (mVideoHandler == null) {
           mVideoHandler = new MyHandler(this);
        }
        if (mVideoService == null) {
            addVideoView(imgRect);
            prepareVideoService(innerPath, localPath, bookType);
        } else if (mVideoService != null) {
            if (mVideoView == null) {
                mVideoView = mVideoService.initView(getContext(), imgRect);
                addView(mVideoView, new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));
            }
            if (mVideoService.isPrepared()) {
                initSurfaceView();
            } else if (!mVideoService.isLoading() && !mVideoService.isPrepared()) {
                prepareVideoService(innerPath, localPath, bookType);
            }
        }
    }

    private void addVideoView(Rect imgRect) {
        mVideoService = new VideoService();
        mVideoService.setOnVideoListener(new OnVideoListener() {

            @Override
            public void onPrepare() {
                hidePlayIcon();
            }

            @Override
            public void onCompletion() {
                showPlayIcon();
                removeVedioView();
            }

            @Override
            public void reset() {
                showPlayIcon();
                resetVedioView();
            }

        });
        mVideoView = mVideoService.initView(getContext(), imgRect);
        addView(mVideoView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
    }

    private void prepareVideoService(String innerPath, String localPath,
                                     int bookType) {
        try {
            mVideoService.prepare(innerPath, localPath, bookType,
                    new PrepareListener() {

                        @Override
                        public void prepareFinish(boolean status) {
                            if (status && mVideoHandler != null) {
                                mVideoHandler.sendEmptyMessageDelayed(
                                        MSG_VIDEO_INIT, 200);
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            UiUtil.showToast(getContext().getApplicationContext(), R.string.fileexception_noread);
        }
    }

    private static final int MSG_VIDEO_INIT = 0;
    private VideoService mVideoService;
    private View mVideoView;

    private static class MyHandler extends Handler {
        private final WeakReference<EpubPageView> mFragmentView;

        MyHandler(EpubPageView view) {
            this.mFragmentView = new WeakReference<EpubPageView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            EpubPageView service = mFragmentView.get();
            if (service != null) {
                super.handleMessage(msg);
                try {
                    switch (msg.what) {
                        case MSG_VIDEO_INIT:
                            service.initSurfaceView();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initSurfaceView() {
        if (mVideoService != null && getContext() != null) {
            mVideoService.initSurfaceView(getContext());
        }
    }

    public boolean changeVideoOrientation() {
        boolean isChange = false;
        if (mVideoService != null) {
            isChange = mVideoService.changeVideoOrientation();
        }
        return isChange;
    }

    public boolean isVideoShow() {
        return mVideoService != null && mVideoView != null
                && mVideoService.isShow();
    }
    public boolean isVideoLandscape() {
        return mVideoService != null && mVideoView != null
                && mVideoService.isVideoLandscape();
    }
    public void resetVedioViewWithOutOrientation() {
        removeVedioView();
        destroyVedioServiceWithOutOrientation();
    }
	
	public void showInteractiveBlockIconView() {
		if (mInteractiveIconImageView != null) {
			for (int i = 0; i < mInteractiveIconImageView.length; i++) {
				mInteractiveIconImageView[i].setVisibility(View.VISIBLE);
			}
		}
	}

}
