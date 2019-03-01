package com.dangdang.reader.dread.core.epub;

import android.app.Activity;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.dangdang.execption.FileFormatException;
import com.dangdang.reader.cloud.MarkNoteManager;
import com.dangdang.reader.dread.cache.PageBitmapCache;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.core.base.BaseReaderWidget;
import com.dangdang.reader.dread.core.base.IReaderController;
import com.dangdang.reader.dread.core.part.PartControllerWrapperImpl;
import com.dangdang.reader.dread.core.part.PartPageAdapter;
import com.dangdang.reader.dread.core.part.PartReaderController;
import com.dangdang.reader.dread.dialog.BuyDialogManager;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.DDFile;
import com.dangdang.reader.dread.format.DDFile.FileType;
import com.dangdang.reader.dread.format.IBook;
import com.dangdang.reader.dread.format.IBookCache;
import com.dangdang.reader.dread.format.IBookManager;
import com.dangdang.reader.dread.format.ManagerFactory;
import com.dangdang.reader.dread.function.AddBookmarkFunction;
import com.dangdang.reader.dread.function.DelBookmarkFunction;
import com.dangdang.reader.dread.function.FunctionCode;
import com.dangdang.reader.dread.function.GotoPageByChapterFunction;
import com.dangdang.reader.dread.function.GotoPageIndexFunction;
import com.dangdang.reader.dread.function.NextPageFunction;
import com.dangdang.reader.dread.function.OperationMenuFunction;
import com.dangdang.reader.dread.function.PrevPageFunction;
import com.dangdang.reader.dread.function.ReComposingFunction;
import com.dangdang.reader.dread.function.ShowToastFunction;
import com.dangdang.reader.dread.function.ToBrowserFunction;
import com.dangdang.reader.dread.function.ToReadEndFunction;
import com.dangdang.reader.dread.holder.GlobalResource;
import com.dangdang.reader.dread.holder.PromptResource;
import com.dangdang.reader.dread.holder.SearchDataHolder;
import com.dangdang.reader.dread.holder.ServiceManager;

public class ReaderAppImpl extends BaseReaderApplicaion {


    private static ReaderAppImpl mApp = null;

    private Activity mContext;
    private BaseReadInfo mReadInfo;

    private DDFile mDDFile;
    private IBook mOneBook;

    private IBookManager mBookManager;
    private BaseReaderWidget mReaderWidget;
    private IReaderController mReaderController;
    private GlobalWindow mGlobalWindow;

    private ServiceManager mServiceManager;
    private MarkNoteManager mMarkNoteManager;

    //protected int mPageSize;
    protected boolean mBookComposingDone = false;


    private ReaderAppImpl() {
    }

    public synchronized static ReaderAppImpl getApp() {
        if (mApp == null) {
            mApp = new ReaderAppImpl();
        }
        return mApp;
    }

    @Override
    public void init(Activity context) throws FileFormatException {
        create(context);
    }

    public void prepareInit(BaseReadInfo readInfo, DDFile ddFile) {
        mReadInfo = readInfo;
        mDDFile = ddFile;//new DDFile(mReadInfo.getBookFile());
    }

    private void create(Activity context) throws FileFormatException {
        mContext = context;

        mOneBook = ManagerFactory.createBook(mDDFile);
        mBookManager = ManagerFactory.create(context, mDDFile, mOneBook);

        PromptResource.getInstance().initResource(context);//TODO ?
        if (mServiceManager == null)
            mServiceManager = new ServiceManager(context);
        if (mMarkNoteManager == null)
            mMarkNoteManager = new MarkNoteManager(mServiceManager);

        //if(isEpub() || isTxt()){//TODO txt?
        EpubReaderWidget readerWidget;
        if (mReaderWidget == null) {
            readerWidget = new EpubReaderWidget(context);
        } else {
            readerWidget = (EpubReaderWidget) mReaderWidget;
        }
        EpubReaderController readerController = null;//new EpubReaderController();
        ControllerWrapperImpl cWrapper = null;
        if (isPart()) {
            //TODO 拆分新增
            readerController = new PartReaderController(mContext);
            cWrapper = new PartControllerWrapperImpl(this);
            BuyDialogManager.getInstance().init(mContext);
        } else {
            readerController = new EpubReaderController(mContext);
            cWrapper = new ControllerWrapperImpl(this);
        }
        //TODO 拆分新增
        EpubPageAdapter adapter = null;
        if (isPart()) {
            adapter = new PartPageAdapter();
        } else {
            adapter = new EpubPageAdapter();
        }

        mGlobalWindow = new GlobalWindow(context, readerWidget);
        mGlobalWindow.initIsPdf(isPdf());

        readerWidget.setAdapter(adapter);
        readerWidget.setController(readerController);
        readerController.setReaderApp(this);
        readerController.setReaderWidget(readerWidget);
        readerController.setControllerWrapper(cWrapper);
        readerController.setGlobalWindow(mGlobalWindow);

        adapter.setController(readerController);
        adapter.setDateBridge(cWrapper);
        adapter.setReaderApp(this);

        mReaderWidget = readerWidget;
        mReaderController = readerController;
        /*} else {
            //Pdf...
			
		}*/

        mBookManager.registerComposingListener(mBookParserListener);
        registerFunction();

    }

    /**
     * 翻页
     *
     * @param is true下一页 false 上一页
     */
    public void pageTurning(boolean is) {
        if (is) {
            mReaderWidget.onTouchEvent(getevent(100f, 0, MotionEvent.ACTION_DOWN));
            mReaderWidget.onTouchEvent(getevent(150f, 0, MotionEvent.ACTION_MOVE));
            mReaderWidget.onTouchEvent(getevent(200f, 0, MotionEvent.ACTION_UP));
        } else {
            mReaderWidget.onTouchEvent(getevent(200f, 0, MotionEvent.ACTION_DOWN));
            mReaderWidget.onTouchEvent(getevent(150f, 0, MotionEvent.ACTION_MOVE));
            mReaderWidget.onTouchEvent(getevent(100f, 0, MotionEvent.ACTION_UP));
        }
    }

    private MotionEvent getevent(float x, float y, int action) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 10;
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                action,
                x,
                y,
                metaState
        );
        return motionEvent;
    }

    private void registerFunction() {

        addFunction(FunctionCode.FCODE_TURNPAGE_BACK, new PrevPageFunction(this));
        addFunction(FunctionCode.FCODE_TURNPAGE_FORWARD, new NextPageFunction(this));
        addFunction(FunctionCode.FCODE_SHOWTOAST, new ShowToastFunction(this));
        addFunction(FunctionCode.FCODE_OPERATIONMENU, new OperationMenuFunction(this));
        addFunction(FunctionCode.FCODE_RECOMPOSING, new ReComposingFunction(this));
        addFunction(FunctionCode.FCODE_GOTO_PAGEINDEX, new GotoPageIndexFunction(this));
        addFunction(FunctionCode.FCODE_GOTO_PAGECHAPTER, new GotoPageByChapterFunction(this));
        addFunction(FunctionCode.FCODE_ADD_MARK, new AddBookmarkFunction(this));
        addFunction(FunctionCode.FCODE_REMOVE_MARK, new DelBookmarkFunction(this));
        addFunction(FunctionCode.FCODE_TO_READEND, new ToReadEndFunction(this));
        addFunction(FunctionCode.FCODE_TOBROWSER, new ToBrowserFunction(this));

    }

    final IBookParserListener mBookParserListener = new IBookParserListener() {
        @Override
        public void onStructFinish(IBook book) {
        }

        @Override
        public void onStart(IBook book) {
            //mPageSize = 0;
            mBookComposingDone = false;
        }

        @Override
        public void onBeingComposing(ParserProgress progress) {
        }

        @Override
        public void onFinish(int pageTotal, IBook book, IBookCache bookCache) {
            //mPageSize = pageTotal;
            mBookComposingDone = true;
        }

        @Override
        public void onStatus(int status, String errorMsg) {
        }

        @Override
        public void onVersion(int kernelVersion, int kernelCompsVersion) {
        }

    };

    @Override
    public BaseReaderWidget getReaderWidget() {
        return mReaderWidget;
    }

    @Override
    public IReaderController getReaderController() {
        return mReaderController;
    }

    @Override
    public IBookManager getBookManager() {
        return mBookManager;
    }

    public ServiceManager getServiceManager() {
        return mServiceManager;
    }

    public MarkNoteManager getMarkNoteManager() {
        return mMarkNoteManager;
    }

    public FileType getFileType() {
        if (mDDFile == null)
            return null;
        return mDDFile.getFileType();
    }

    @Override
    public int getCurrentPageIndex() {
        return mReaderController.getCurrentPageIndexInBook();
    }

    public int getPageSize() {
        int pageCount = 0;
        if (mOneBook != null) {
            pageCount = mOneBook.getPageCount();
        }
        return pageCount;
    }

    public boolean isBookComposingDone() {
        return mBookComposingDone;
    }

    public BaseReadInfo getReadInfo() {
        return mReadInfo;
    }

    public IBook getBook() {
        return mOneBook;
    }

    public GlobalWindow getGlobalWindow() {
        return mGlobalWindow;
    }

	
	/*@Override
    public void updateReadProgress(IReadProgress readProgress) {
		
		if(readProgress instanceof EpubProgress){
			EpubProgress pgs = (EpubProgress) readProgress;
			pgs.getChapter();
			mReadInfo.c
			
		} else if(readProgress instanceof PdfProgress){
			
		}
		
	}*/

    @Override
    public void destroy() {
        mReaderWidget.clear();
        mReaderController.reset();
        //这里改变过本来没有影藏
//        mBookManager.destroy();
        NoteHolder.getHolder().clear();
        SearchDataHolder.getHolder().clear();
        GlobalResource.clear();
//        //release, liuzhuo
        PageBitmapCache.getInstance().release();
        if (mGlobalWindow != null)
            mGlobalWindow.stopAudio();
        mGlobalWindow = null;

        mServiceManager = null;
        mMarkNoteManager = null;

        mApp = null;
    }

    public void clear() {
        //TODO 会报好多空针，因为onCreate和OnDestory不成对出现
        BuyDialogManager.getInstance().clear();
        mReaderWidget = null;
        mReaderController = null;
        mBookManager = null;
        mGlobalWindow = null;
        mContext = null;
    }

    @Override
    public void startRead(BaseReadInfo readInfo) {
        mBookManager.startRead(readInfo);
    }

    @Override
    public void reStartRead(BaseReadInfo readInfo) {
        mBookManager.reStartRead(readInfo);
    }

    @Override
    public boolean isCanExit() {
        return mBookManager.isCanExit();
    }

    @Override
    public Activity getContext() {
        return mContext;
    }

}
