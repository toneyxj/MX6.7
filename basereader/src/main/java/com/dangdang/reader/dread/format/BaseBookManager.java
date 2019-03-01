package com.dangdang.reader.dread.format;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;

import com.dangdang.reader.dread.config.PageType;
import com.dangdang.reader.dread.config.ParserStatus;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion.IBookParserListener;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion.ParserProgress;
import com.dangdang.reader.dread.core.base.IReaderApplication.IAbortParserListener;
import com.dangdang.reader.dread.core.epub.DrawWrapper;
import com.dangdang.reader.dread.data.Font;
import com.dangdang.reader.dread.data.GallaryData;
import com.dangdang.reader.dread.data.OneSearch;
import com.dangdang.reader.dread.data.ParagraphText;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.epub.EpubBookCache;
import com.dangdang.reader.dread.format.epub.IEpubBookManager;
import com.dangdang.reader.dread.format.part.PartChapter;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.jni.BaseJniWarp.EPoint;
import com.dangdang.reader.dread.jni.BaseJniWarp.ERect;
import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;
import com.dangdang.reader.dread.jni.ChaterInfoHandler;
import com.dangdang.reader.dread.jni.DrawInteractiveBlockHandler;
import com.dangdang.reader.dread.jni.InteractiveBlockHandler.InteractiveBlock;
import com.dangdang.reader.dread.jni.ParagraphTextHandler;
import com.dangdang.reader.dread.jni.WrapClass;
import com.dangdang.reader.dread.task.BaseTask.BaseTaskImpl;
import com.dangdang.reader.dread.task.BaseTaskManager;
import com.dangdang.reader.dread.task.ITaskCallback;
import com.dangdang.reader.dread.task.TaskThreadPoolExecutor;
import com.dangdang.reader.dread.util.BookCacheHandle;
import com.dangdang.reader.dread.util.BookStructConvert;
import com.dangdang.reader.dread.util.BookStructConvert.ComposingSeriBook;
import com.dangdang.reader.dread.util.BookStructConvert.SeriBook;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class BaseBookManager implements IEpubBookManager,
		ITaskCallback, IMsgTransfer {

	private Book mBook;
	protected ReadInfo mReadInfo;
	protected String mBookFile;
	protected String mBookDir;

	protected boolean mStructFinish = true;
	private boolean mBookComposingDone = false;
	private boolean mComposingOneDone = true;
	private boolean mPlanAbortComposing = false;
	private boolean mPlanAbortSearch = false;
	private boolean mUseCache = false;

	protected int mStatus = ParserStatus.C_SUCCESS;
	protected int mRequestChapterIndex = -1;
	protected Chapter mRequestChapter;

	protected List<IBookParserListener> mComposingListeners;
	protected IAbortParserListener mAbortListener;
	// private ExecutorService mExetService;
	private TaskThreadPoolExecutor mExetService;

	protected WrapClass mWrapClass;
	private BaseJniWarp mBaseJni;
	private TaskManager mTaskManager;
	private EpubBookCache mBookCache;
	private SearchManager mSearchManager;

	private SyncHandler mSyncHandler = new SyncHandler();

	/**
	 * 内核版本
	 */
	private int mKernelVersion;
	/**
	 * 影响排版的版本
	 */
	private int mKernelCompsVersion;
	
	protected Context mContext;

	private boolean mAlreadyDestroy = false;

	public boolean isAlreadyDestroy() {
		return mAlreadyDestroy;
	}

	public void setAlreadyDestroy(boolean mAlreadyDestroy) {
		this.mAlreadyDestroy = mAlreadyDestroy;
	}

	public BaseBookManager(Context context, Book book) {
		mBook = book;
		mContext = context.getApplicationContext();
		mComposingListeners = new Vector<IBookParserListener>();
		// mExetService = Executors.newSingleThreadExecutor();
		mExetService = TaskThreadPoolExecutor.getDefault();
		mExetService
				.setRejectedExecutionHandler(new RejectedExecutionHandler() {
					@Override
					public void rejectedExecution(Runnable r,
							ThreadPoolExecutor executor) {
						LogM.e(getClass().getSimpleName(),
								" bm rejectedExecution ");
					}
				});

		mWrapClass = new WrapClass(ReadConfig.getConfig(), mContext);
		// initParseEngine();

		mKernelVersion = BaseJniWarp.getKernelVersion();
		mKernelCompsVersion = BaseJniWarp.getCompVersion();

		mTaskManager = new TaskManager();
		mTaskManager.setTaskCallback(this);
		mTaskManager.startTask();

		mBookCache = createBookCache();
	}

	protected EpubBookCache createBookCache() {
		return new EpubBookCache();
	}

	protected void initParseEngine() {
		BaseJniWarp.initParseEngine(mWrapClass);
	}

	protected void initComposingStyle() {
		setFonts(ReadConfig.getConfig().getFontFileList());
		setDefaultFont();
		updateBackground(mWrapClass.getBgColor(), mWrapClass.getForeColor());
		BaseJniWarp.setBig5Encoding(ReadConfig.getConfig().getChineseConvert());
	}

	public void initReadPlanInfo(ReadInfo readInfo) {

	}

	protected abstract void startRead(final ReadInfo readInfo);

	// public abstract void reStartRead(final ReadInfo readInfo);

	// public abstract int getPageIndexInBook(final Chapter chapter, final int
	// elementIndex); //TODO ?

	protected abstract Book buildBookStruct(String bookFile, int bookType,
			boolean hasNotChapter) throws FileNotFoundException;

	protected abstract void loadChapterList(List<Chapter> chapters,
			int chapterSize, boolean isReLoad, boolean isFirst);

	/**
	 * @param chapter
	 * @param pageIndexInChapter
	 * @param bitmap
	 * @param sync
	 *            是否与其它方法同步
	 * @return
	 */
	protected abstract int drawPage(Chapter chapter, int pageIndexInChapter,
			int pageSeqNum, Bitmap bitmap, boolean sync);

	protected abstract int getChapterStructInner(Chapter chapter);

	protected abstract ChaterInfoHandler getChapterInfo(Chapter chapter);

	protected abstract IndexRange getPageStartAndEndIndexInner(Chapter chapter,
			int pageIndexInChapter);

	protected abstract int getNativePageCount(Chapter chapter);

	/**
	 * 请求此次终止排版()
	 */
	protected abstract void cancelParse();

	protected abstract List<OneSearch> search(Chapter chapter, String word);
	
	public int UpdateElementIndex(String strChapter, String oldModVersion, String newModVersion, int nIndex) {
		return mBaseJni.UpdateElementIndex(strChapter, oldModVersion, newModVersion, nIndex);
	}

	public abstract void DrawInteractiveBlock(Chapter chapter, int pageIndexInChapter, int nBlockIndex, int nWidth, 
			int nHeight, DrawInteractiveBlockHandler handler);
	
	@Override
	public void startRead(BaseReadInfo readInfo) {
		initParseEngine();
		startRead((ReadInfo) readInfo);
	}

	@Override
	public void drawPage(BaseDrawPageParam param) {
		DrawPageAsycCommand asycParam = (DrawPageAsycCommand) param;
		drawPage((DrawPageAsycCommand) param);
		printLog(" drawPage Asyc " + ", " + asycParam.getChapter() + ", "
				+ asycParam.getPageIndexInChapter() + ", " + asycParam.isLast());
	}

	public BaseDrawPageResult drawPageSync(BaseDrawPageParam param) {

		DrawPageSyncCommand syncParam = (DrawPageSyncCommand) param;
		Chapter chapter = syncParam.getChapter();
		int pageIndexInChapter = syncParam.getPageIndexInChapter();
		int pageSeqNum = syncParam.getPageSequenceNum();
		int width = syncParam.getPageSize().getPageWidth();
		int height = syncParam.getPageSize().getPageHeight();
		return drawPageInner(chapter, pageIndexInChapter, pageSeqNum, width,
				height, false);
	}

	protected DrawPageResult drawPageInner(Chapter chapter,
			int pageIndexInChapter, int pageSeqNum, int width, int height,
			boolean isSync) {
		printLog("drawpagexyz synchnzed  drawPageInner start pi=" + chapter
				+ "," + pageIndexInChapter + ",isSync=" + isSync);
		DrawPageResult dpResult;
		DrawWrapper drawWrapper = new DrawWrapper();
		final Bitmap bitmap = drawWrapper.getBackgroundBitmap(width, height);
		int iPageType = drawPage(chapter, pageIndexInChapter, pageSeqNum,
				bitmap, isSync);
		HashSet<PageType> pageType = PageType.convert(iPageType);

		/*
		 * final File file = new File(DROSUtility.getImageCache(),
		 * chapter.hashCode() + "-" + pageIndexInChapter + ".jpg");
		 * CacheManager.getInstance().saveFile(bitmap, file);
		 */
		IndexRange pageRange = getPageStartAndEndIndex(chapter,
				pageIndexInChapter);

		dpResult = new DrawPageResult();
		// dpResult.setStatus(0);
		dpResult.setPageType(pageType);
		dpResult.setBitmap(bitmap);
		dpResult.setPageRange(pageRange);
		printLog("drawpagexyz synchnzed drawPageInner end pi=" + chapter + ","
				+ pageIndexInChapter);

		return dpResult;
	}

	public void asynGoto(GotoPageCommand command) {

		if (isPlanAbortComposing()) {
			printLog(" asynGoto isPlanAbortComposing=true ");
			return;
		}
		ToPageTask task = newToPageTask(command);
		task.setMsgTransfer(this);
		mTaskManager.putTaskToFirst(task);
	}

	protected ToPageTask newToPageTask(GotoPageCommand command) {
		ToPageTask task = new ToPageTask(command, this);
		return task;
	}

	protected void drawPage(DrawPageAsycCommand command) {

		if (isPlanAbortComposing()) {
			printLog(" drawPage isPlanAbortComposing=true ");
			return;
		}
		DrawPageTask task = new DrawPageTask(command, this);
		task.setMsgTransfer(this);
		mTaskManager.checkQueueSize();
		mTaskManager.putTaskToFirst(task);
	}

	// a
	@Override
	public void search(String word, SearchListener l) {
		if (isPlanAbortComposing()) {
			printLog(" drawPage isPlanAbortComposing=true ");
			return;
		}
		if (mSearchManager == null) {
			mSearchManager = new SearchManager();
			mSearchManager.startTask();
		}
		SearchCommand sehCommand = new SearchCommand(word, l);
		SearchTask task = new SearchTask(sehCommand, this, getBook()
				.getChapterList());
		// task.setMsgTransfer(this);
		// mTaskManager.putTaskToFirst(task);//TODO mTaskManager.putTask(task);
		// ?
		mSearchManager.putTaskToFirst(task);
	}

	public void reStartRead(BaseReadInfo readInfo) {

		LogM.w(getClass().getSimpleName(), "wyz [ reStartRead() ]");

		mReadInfo = (ReadInfo) readInfo;
		reSet(true);
		doStart(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				final boolean isFirst = preStartLoad();
				startLoadChapters(mBook.getChapterList(), true, isFirst);
			}
		});
	}

	public int getPageIndexInBook(Chapter chapter, int elementIndex) {

		int indexInBook = 0;
		if (chapter == null) {
			return indexInBook;
		}

		int beforeIndexInBook = mBook.getPageIndexInBookAtBeforeHtml(chapter);
		int indexInHtml = getPageIndexInChapter(chapter, elementIndex);
		indexInBook = beforeIndexInBook + indexInHtml;
		// printLog(" getPageIndexInBookAtBeforeHtml beforeIndexInBook = " +
		// beforeIndexInBook + ", indexInHtml = " + indexInHtml);

		return indexInBook;
	}

	public int getPageIndexInBook(int chapterIndex, int elementIndex) {

		final Chapter chapter = mBook.getChapter(chapterIndex);

		return getPageIndexInBook(chapter, elementIndex);
	}

	protected void doStart(Runnable task) {
		mExetService.clearQueue();
		mExetService.submit(task);
	}

	protected void initNative() {
		initComposingStyle();
	}

	private String getExpName(String path) {
		return StringUtil.getExpName(path);
	}

	protected void startParser() {
		printLog(" startParser()  mBookFile = " + mBookFile);

		boolean hasSucc = false;
		List<Chapter> chapterList = null;
		try {
			onVersion(getKernelVersion(), getKernelCompsVersion());

			mStructFinish = false;
			final boolean theSameFile = convertBookStruct();
			chapterList = mReadInfo.getChapterList();

			final boolean hasNotChapter = isListEmpty(chapterList);
			String tmpBookFile = mBookFile;
			if (!new File(mBookFile).exists()) {// 兼容老版本
				tmpBookFile = mBookDir;
			}
			final Book book = buildBookStruct(tmpBookFile,
					mReadInfo.getEBookType(), hasNotChapter);
			chapterList = book.getChapterList();
			book.setTheSameFile(theSameFile);

			onStructFinish(book);// TODO is callback ?

			hasSucc = true;

		} catch (Exception e) {
			e.printStackTrace();
			hasSucc = false;
		} finally {
			mStructFinish = true;
		}
		if (isPlanAbortComposing()) {
			printLogE(" isPlanAbortComposing=true ");
			return;
		}

		if (isComposingError(mStatus)) {
			onComposingError(mStatus);
		} else if (isListEmpty(chapterList)) {
			hasSucc = false;
		}

		if (hasSucc) {
			try {
				final boolean isFirst = preStartLoad();
				startLoadChapters(chapterList, false, isFirst);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				onComposingError(ParserStatus.FILE_ERROR);
			}
		} else {
			onComposingError(ParserStatus.FILE_ERROR);
		}
	}

	private boolean convertBookStruct() {

		boolean sameFile = true;
		if (DDFile.isTxt(mBookFile)) {
			SeriBook seriBook = BookStructConvert.dataToBookStruct(mReadInfo
					.getBookStructDatas());
			final long seriFileSize = seriBook == null ? 0 : seriBook
					.getFileSize();
			sameFile = mReadInfo.isTheSameFile(mBookFile, seriFileSize);
			if (sameFile) {
				if (seriBook != null) {
					mReadInfo.setChapterList(seriBook.getChapters());
					// setFileSize(seriBook.getFileSize());
				}
			}
			if (seriBook == null) {
				sameFile = true;
			}
		}

		return sameFile;
	}

	/**
	 * @param list
	 * @return true 代表为null或者size为0
	 */
	protected boolean isListEmpty(List<?> list) {
		return list == null || list.size() == 0;
	}

	protected void startLoadChapters(List<Chapter> chapters, boolean isReLoad,
			boolean isFirst) {

		// Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		printLog(" startLoadChapters start ");
		mUseCache = handleBookCache();
		printLog(" startLoadChapters userCache is=" + mUseCache);
		final int chapterSize = chapters.size();

		resetBookComposingDone();
		if (mUseCache) {
			loadChapterList(chapters, chapterSize, isReLoad, isFirst);
			clacPageSize(chapters);
			loadChapterListBefore(chapterSize);
			loadChapterListAfter(chapters, mUseCache, chapterSize);
		} else {
			loadChapterListBefore(chapterSize);
			loadChapterList(chapters, chapterSize, isReLoad, isFirst);
			loadChapterListAfter(chapters, mUseCache, chapterSize);
		}
		setBookComposingDone();
		checkIsNotifyLoadData();
		printLog(" startLoadChapters end");

	}

	protected boolean handleBookCache() {
		boolean isCache = false;
		String bookId = mReadInfo.getDefaultPid();
		int isFull = mReadInfo.isBoughtToInt();
		ComposingSeriBook composingBook = (ComposingSeriBook) BookCacheHandle
				.deSeriBookCache(mContext, bookId, isFull);
		if (composingBook != null&&composingBook.getPageInfoCache()!=null&&composingBook.getPageCount()>0) {
			isCache = true;
			mBookCache.setPageInfoCache(composingBook.getPageInfoCache());
			mBook.setPageCount(composingBook.getPageCount());
		}
		return isCache;
	}

	protected void loadChapterListBefore(int chapterSize) {
		// resetBookComposingDone();
		if (!isPlanAbortComposing()) {
			try {
				resetComposingOneDone();
				Chapter chapter = mRequestChapter;
				getChapterPageCount(chapter);
				// saveChapterCache(chapter);
				onComposingStart(chapter, mBook);
				// onFirstCallBeingParse(chapterSize);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				setComposingOneDone();
			}
		}
	}

	protected void loadChapterListAfter(List<Chapter> chapters,
			boolean userCache, final int chapterCount) {
		synchronized (mAbortLock) {
			LogM.d(getClass().getSimpleName(),
					"wyz loadChapterList==end mPlanAbortComposing = "
							+ isPlanAbortComposing() + ", isTasking="
							+ isTasking());
			if (isPlanAbortComposing()) {
				if (!isTasking()) {
					onAbortComposint();
				}
			} else {
				int pageCount = 0;
				if (chapterCount > 0) {
					pageCount = clacPageSize(chapters);// chapters.get(chapterCount
														// - 1).endPageNum;
				}
				setPageCount(pageCount);
				// setBookComposingDone();
				// checkIsNotifyLoadData();
				onComposingFinish(pageCount, mBook, mBookCache);
			}
		}
	}

	public Book getBook() {
		return mBook;
	}

	@Override
	public int getPageCount() {
		return mBook.getPageCount();
	}

	private void setPageCount(int pageCount) {
		mBook.setPageCount(pageCount);
		mBookCache.setPageCount(pageCount);
	}

	public void clearPrev() {
		try {
			clearData();
			reSet(false);
			mBook.clearAll();
			// mComposingListeners.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unComposingListeners() {
		if (mComposingListeners != null) {
			mComposingListeners.clear();
		}
	}

	public void reSet(boolean resetNative) {
		try {
			printLog(" reSet ");
			mBookCache.reset();
			mBook.reSet();
			mTaskManager.clearTask();
			// mBookReaderMap.clear();
			// clearBookData();
			// BookReader.mSymbolSize.clear();

			ReadConfig config = ReadConfig.getConfig();
			// init(config);
			mWrapClass.init(config, mContext);
			if (resetNative) {
				resetData();
			}

			initComposingStyle();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void resetData() {
		printLog("in Drwrap resetData() ");
		mBaseJni.resetData();
		printLog("out Drwrap resetData() ");
	}

	protected void clearData() {
		mBaseJni.clearData();
	}

	public void destroy() {
		try {
			// printLog(" native destroy() start ");
			setPlanAbortComposing();
			mTaskManager.stopTask();
			if (mSearchManager != null) {
				mSearchManager.stopTask();
			}
			mExetService.shutdownNow();
			clearData();
			mComposingListeners.clear();
			// clearBookData();
			mBook.clearAll();
			mBookCache.reset();
			setAlreadyDestroy(true);
			// printLog(" native destroy() end ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void setBookCacheNotAvailable() {
		getBookCache().setAvailable(false);
	}

	protected int saveChapterCache(Chapter chapter) {
		ChaterInfoHandler ciHandle = getChapterInfo(chapter);
		mBookCache.setPageInfo(chapter, ciHandle);
		return ciHandle.getPageInfos().size();
	}

	public int getChapterPageCount(Chapter chapter) {
		return getChapterPageCount(chapter, false);
	}

	@Override
	public int getChapterPageCount(Chapter chapter, boolean onlyCache) {
		int pageCount = 0;
		if (hasCache(chapter)) {
			pageCount = mBookCache.getPageCount(chapter);
		} else if (!onlyCache) {
			 printLog(" synchnzed getChapterPageCountInner start " +
			 chapter.hashCode());
			pageCount = getChapterStructInner(chapter);
			if (pageCount <= 0) {
				printLogE(" synchnzed getChapterPageCountInner end "
						+ pageCount);
			}
			// printLog(" synchnzed getChapterPageCountInner end " +
			// chapter.hashCode());
			// mBookCache.setPageCount(chapter, pageCount);
		} else {
			printLog(" synchnzed getChapterPageCountInner onlyCache "
					+ onlyCache);
		}
		return pageCount;
	}

	@Override
	public void preComposingChapter(Chapter chapter) {
		if (isCacheChapter(chapter)) {
			return;
		}
		// getNativePageCount(chapter);
		printLog(" preComposingChapter " + chapter);
		PreCompCommand preCompsCmd = new PreCompCommand();
		preCompsCmd.setChapter(chapter);
		PreCompsTask preCompsTask = new PreCompsTask(preCompsCmd, this);
		preCompsTask.setMsgTransfer(this);
		mTaskManager.checkQueueSize();
		mTaskManager.putTaskToFirst(preCompsTask);
	}

	@Override
	public int composingChapterAndGetPageIndex(final Chapter chapter, final int elementIndex){
		BaseJniWarp.EPageIndex pageIndex = new BaseJniWarp.EPageIndex();
		pageIndex.filePath = chapter.getPath();
		pageIndex.pageIndexInChapter = 0;
		return mBaseJni.layoutAndGetPageByIndex(pageIndex, elementIndex) + 1;
	}

	public IndexRange getPageStartAndEndIndex(Chapter chapter,
			int pageIndexInChapter) {
		if (chapter == null) {
			LogM.e(getClass().getSimpleName(),
					" getPageSEIndex chapter == null");
			return null;
		}
		IndexRange indexPage = null;
		PageInfo pageInfo = mBookCache.getPageInfo(chapter, pageIndexInChapter);
		// LogM.i(getClass().getSimpleName(), " getPageSEIndex 1 " + pageInfo +
		// "," + chapter);
		if (pageInfo != null) {
			indexPage = new IndexRange();
			indexPage.setStartIndex(new ElementIndex(pageInfo.getStartIndex()));
			indexPage.setEndIndex(new ElementIndex(pageInfo.getEndIndex()));
		} else {
			indexPage = getPageStartAndEndIndexInner(chapter,
					pageIndexInChapter);
		}
		// LogM.i(getClass().getSimpleName(), " getPageSEIndex 2 " + indexPage);
		return indexPage;
	}

	@Override
	public IndexRange getChapterStartAndEndIndex(Chapter chapter) {
		return mBookCache.getChapterStartAndEndIndex(chapter);
	}

	@Override
	public void updateBackground(int bgColor, int foreColor) {
		mBaseJni.setBkForeColor(bgColor, foreColor);
	}

	protected void setDefaultFont() {
		mBaseJni.addBasicFont("", ReadConfig.SystemFontEn, "DD_CHARSET_ANSI");
		mBaseJni.setCurDefaultFont(mWrapClass.getDefaultFontName(),
				mWrapClass.getDefaultFontPath(), "");
        mBaseJni.addGlobalFont("", ReadConfig.SystemFont, "");
	}

	public void setFonts(List<Font> fonts) {
		if (fonts == null) {
			LogM.e(getClass().getSimpleName(), " setFonts == null ");
			return;
		}
		for (Font f : fonts) {
			String fontPath = f.getFontPath();
			if (new File(fontPath).exists()) {
				String fontName = f.getFontName();
				String charset = f.getCharset();
				if (f.isDefault()) {
					mBaseJni.setCurDefaultFont(fontName, fontPath, charset);
				} else {
					mBaseJni.addBasicFont(fontName, fontPath, charset);
				}
			}
		}
	}

	@Override
	public ParagraphText getParagraphText(Chapter chapter, int elementIndex,
			boolean first, int maxLen) {

		ParagraphTextHandler handler = new ParagraphTextHandler();
		getParagraphTextInner(chapter, elementIndex, first, maxLen, handler);
		ParagraphText paragText = handler.getParagraphText();
		if (paragText.isIllegality()) {
			printLogE(" getParagraphText isIllegality " + paragText + ","
					+ chapter + ", " + elementIndex + "," + first
					+ ", iscache=" + isCacheChapter(chapter));
		}
		return paragText;
	}

	protected abstract void getParagraphTextInner(Chapter chapter,
			int elementIndex, boolean first, int maxLen,
			ParagraphTextHandler handler);

	public void lockMsg() {
		mSyncHandler.lockComposing();
	}

	public void unLockMsg() {
		mSyncHandler.unLockComposing();
	}

	public void notifyMsg() {
		mSyncHandler.notifyComposing();
	}

	public void requestLoadChapter() throws InterruptedException {

		mSyncHandler.requestComposingWait();
		mSyncHandler.setAsycLoadChapterData();

		LogM.d(getClass().getSimpleName(),
				"lux2 [ start ] LoadDataCondition.await() ");
		mSyncHandler.loadDataConditionAwait();// mLoadDataCondition.await();
		LogM.d(getClass().getSimpleName(),
				"lux2 [ end ] LoadDataCondition.await() ");
	}

	protected void notifyLoadData() {
		mSyncHandler.notifyLoadData();
	}

	protected void processComposingWait() throws InterruptedException {
		mSyncHandler.processComposingWait();
	}

	protected void checkIsNotifyLoadData() {
		try {
			lockMsg();
			notifyLoadData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			unLockMsg();
		}
	}

	public void registerComposingListener(IBookParserListener l) {
		if (l == null) {
			throw new NullPointerException(" BookComposingListener == null ");
		}
		if (!mComposingListeners.contains(l)) {
			mComposingListeners.add(l);
		}
	}

	protected boolean preStartLoad() throws IndexOutOfBoundsException {
//修改了charindex
		int chapterIndex = mReadInfo.getChapterIndex();
		if ( mBook.getChapterList().size()<=chapterIndex){
			mReadInfo.setChapterIndex(0, 4);
			chapterIndex=0;
		}
		boolean isFirst = chapterIndex <= 0;
		if (chapterIndex < 0) {
			chapterIndex = 0;
		}
		Chapter chapter = null;
		try {
			chapter = mBook.getChapterList().get(chapterIndex);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			throw e;
		}
		requestChapterData(chapter, chapterIndex);

		return isFirst;
	}

	protected int clacPageSize(List<Chapter> chapters) {
		int size = 0;
		Chapter prev = null;
		Chapter chapter = null;
		for (int i = 0, len = chapters.size(); i < len; i++) {
			chapter = chapters.get(i);
			int htmlPageTotal = chapter.getPageTotal();

			int prevEndPageInBook = (prev == null) ? 0 : prev
					.getEndIndexInBook();// endIndexInBook;
			int startIndexInBook = getStartIndexInBook(prevEndPageInBook,
					htmlPageTotal);
			int endIndexInBook = getEndIndexInBook(startIndexInBook,
					htmlPageTotal);
			/*
			 * printLog(" clacPageSize html = " + html + ", htmlPageTotal = " +
			 * htmlPageTotal + ", startIndexInBook = " + startIndexInBook +
			 * ", endIndexInBook = " + endIndexInBook);
			 */

			chapter.setStartIndexInBook(startIndexInBook);// html.startIndexInBook
															// =
															// startIndexInBook;
			chapter.setEndIndexInBook(endIndexInBook);// html.endIndexInBook =
														// endIndexInBook;
			prev = chapter;

			size += htmlPageTotal;
		}
		printLog(" clacPageSize finish size = " + size);
		return size;
	}

	protected int getStartIndexInBook(int prevEndPageInBook, int htmlPageTotal) {
		return prevEndPageInBook + 1;
	}

	protected int getEndIndexInBook(int startIndexInBook, int htmlPageTotal) {
		return startIndexInBook + (htmlPageTotal > 1 ? htmlPageTotal : 1) - 1;
	}

	protected void requestChapterData(final Chapter chapter,
			final int chapterIndex) {
		// synchronized (mRequestLuck) {
		// List<Chapter> chapterList = mOneBook.getChapterList();
		mRequestChapterIndex = chapterIndex;// chapterList.indexOf(chapter);
		mRequestChapter = chapter;
		// }
		LogM.d(getClass().getSimpleName(),
				"lux requestChapterData mRequestChapterIndex = "
						+ mRequestChapterIndex);
	}

	/*
	 * protected void onFirstCallBeingParse(int chapterCount) {
	 * 
	 * printLog("wyz onFirstCallBeingParse " + mRequestChapter); Chapter chapter
	 * = mRequestChapter; BookReader bookReader = loadNewChapter(chapter);
	 * callBeingParse(chapterCount, 1, chapter, true, bookReader);
	 * 
	 * }
	 */

	protected void callBeingParse(int chapterCount, int being, Chapter chapter,
			boolean requestChapter) {
		ParserProgress progress = new ParserProgress();
		progress.chapterIndex = mBook.chapterIndexInBook(chapter);
		progress.beingchapterCount = being + 1;
		progress.chapterCount = chapterCount;

		onBeingComposing(progress);
	}

	protected void onVersion(int kernelVersion, int kernelComposingVersion) {
		for (IBookParserListener l : mComposingListeners) {
			l.onVersion(kernelVersion, kernelComposingVersion);
		}
	}

	protected void onStructFinish(final Book book) {
		for (IBookParserListener l : mComposingListeners) {
			l.onStructFinish(book);
		}
	}

	protected void onComposingStart(Chapter pre, Book book) {
		for (IBookParserListener l : mComposingListeners) {
			l.onStart(book);
		}
	}

	protected void onBeingComposing(ParserProgress progress) {
		for (IBookParserListener l : mComposingListeners) {
			l.onBeingComposing(progress);
		}
	}

	protected void onComposingFinish(int pageTotal, Book book,
			IBookCache bookCache) {
		for (IBookParserListener l : mComposingListeners) {
			l.onFinish(pageTotal, book, bookCache);
		}
	}

	protected void onComposingError(int error) {
		for (IBookParserListener l : mComposingListeners) {
			l.onStatus(error, "");
		}
	}

	protected void setComposingStatus(final int status) {
		mStatus = status;
	}

	public boolean isBookComposingDone() {
		return mBookComposingDone;
	}

	protected void setBookComposingDone() {
		mBookComposingDone = true;
	}

	protected void resetBookComposingDone() {
		mBookComposingDone = false;
	}

	protected boolean isComposingOneDone() {
		return mComposingOneDone;
	}

	protected void setComposingOneDone() {
		mComposingOneDone = true;
	}

	protected void resetComposingOneDone() {
		mComposingOneDone = false;
	}

	public boolean isCanExit() {
		printLog(" isCanExit mComposingOneDone = " + isComposingOneDone()
				+ ", mStructFinish = " + mStructFinish + ", isTasking = "
				+ isTasking());
		return isComposingOneDone() && mStructFinish;
	}

	protected boolean isComposingError(int status) {
		return status != ParserStatus.C_SUCCESS;
	}

	public void setPlanAbortComposing() {
		mPlanAbortComposing = true;
	}

	protected void reSetPlanAbortComposing() {
		mPlanAbortComposing = false;
	}

	protected boolean isPlanAbortComposing() {
		return mPlanAbortComposing;
	}

	protected boolean isTasking() {
		return mTaskManager.isTasking();
	}

	public int getStatus() {
		return mStatus;
	}

	private final Object mAbortLock = new Object();

	public void requestAbortComposing(IAbortParserListener l) {
		printLog(" requestAbortComposing in ");
		synchronized (mAbortLock) {
			mAbortListener = l;
			printLog("wyz requestAbortComposing 2 composingdone="
					+ isBookComposingDone());
			if (isBookComposingDone()) {
				onAbortComposint();
			} else {
				setPlanAbortComposing();
				// cancelParse();
			}
		}
		printLog("wyz requestAbortComposing out ");
	}

	protected void onAbortComposint() {
		printLog(" onAbortComposint in " + mAbortListener);
		// synchronized (mAbortLock) {
		if (mAbortListener != null) {
			mAbortListener.alreadyAbort();
			reSetAbortListener();
		}
		mTaskManager.clearTask();
		reSetPlanAbortComposing();
		// }
		printLog(" onAbortComposint out ");

	}

	protected void reSetAbortListener() {
		mAbortListener = null;
	}

	@Override
	public void abortSearch() {
		setAbortSearch();
	}

	private void setAbortSearch() {
		printLogE(" set abort search ");
		mPlanAbortSearch = true;
	}

	private void resetAbortSearch() {
		mPlanAbortSearch = false;
	}

	private boolean isAbortSearch() {
		return mPlanAbortSearch;
	}

	protected boolean isUseCache() {
		return mUseCache;
	}

	protected void setBaseJni(BaseJniWarp baseJni) {
		this.mBaseJni = baseJni;
	}

	@Override
	public void onTask(BaseTaskKey taskKey, BaseTaskResult result) {
		synchronized (mAbortLock) {
			if (isPlanAbortComposing()) {
				printLog(" onTask isPlanAbortComposing=true ");
				onAbortComposint();
				return;
			}
		}

		IAsynListener l = taskKey.getAsynListener();
		if (l != null) {
			l.onAsyn(taskKey, result);
		}
	}

	protected EPoint convertPaint(Point p) {
		return NativeStructConvert.convertPoint(p);
	}

	protected Rect[] convertRect(ERect... rs) {
		return NativeStructConvert.convertRects(rs);
	}

	protected EpubBookCache getBookCache() {
		return mBookCache;
	}

	public boolean hasCache(Chapter chapter) {
		return getBookCache().hasCache(chapter);
	}

	protected int getPrevKernelVersion() {
		return mReadInfo.getKernelVersion();
	}

	protected int getPrevKernelComsVersion() {
		return mReadInfo.getKernelComsVersion();
	}

	protected int getKernelVersion() {
		return mKernelVersion;
	}

	protected void setKernelVersion(int kernelVersion) {
		this.mKernelVersion = kernelVersion;
	}

	protected int getKernelCompsVersion() {
		return mKernelCompsVersion;
	}

	protected void setKernelCompsVersion(int kernelCompsVersion) {
		this.mKernelCompsVersion = kernelCompsVersion;
	}

	protected TaskManager getTaskManager() {
		return mTaskManager;
	}

	protected String convertToSimplified(String text) {
		if (!TextUtils.isEmpty(text)
				&& ReadConfig.getConfig().getChineseConvert()&&mReadInfo.isSupportConvert()) {
			text = BaseJniWarp.ConvertToGBorBig5(text, 1);
		}
		return text;
	}

	protected void printLog(String msg) {
		LogM.i(getClass().getSimpleName(), msg);
	}

	protected void printLogE(String msg) {
		LogM.e(getClass().getSimpleName(), msg);
	}

	public static class TaskManager extends BaseTaskManager {

		private ITaskCallback taskCallback;

		public TaskManager() {
			super();
		}

		public void setTaskCallback(ITaskCallback taskCallback) {
			this.taskCallback = taskCallback;
		}

		@Override
		public void run() {
			while (isRun()) {
				try {
					printLog(" luxu 1 TaskManager. taskQueue.size() = "
							+ getTaskSize());
					BaseTaskImpl<?> task = (BaseTaskImpl<?>) taskQueue.take();
					if (!isRun()) {
						printLog(" luxu 1 TaskManager isRun=false ");
						return;
					}
					setTasking(task);

					Future<?> result = extService.submit(task);
					BaseTaskResult taskResult = (BaseTaskResult) result.get();
					BaseTaskKey taskKey = task.getTaskKey();
					taskCallback.onTask(taskKey, taskResult);

					printLog(" luxu 2 TaskManager. onTask = " + task
							+ ",Queue.size=" + getTaskSize() + "]");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					resetTasking();
				}
			}
			taskQueue.clear();
			taskQueue = null;
			printLog(" luxu 1 TaskManager isRun=false ");
		}
	}

	public static class SearchManager extends BaseTaskManager {

		protected final static int MAX_SEARCH_TASKSIZE = 1;

		@Override
		public void run() {

			while (isRun()) {
				try {
					printLog(" luxu 1 SearchM. taskQueue.size() = "
							+ taskQueue.size());
					BaseTaskImpl<?> task = (BaseTaskImpl<?>) taskQueue.take();
					if (!isRun()) {
						printLog(" luxu 1 SearchM isRun=false ");
						return;
					}
					setTasking(task);
					extService.submit(task);

					printLog(" luxu 2 SearchM. onTask ]");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					resetTasking();
				}
			}
		}

		@Override
		protected int getMaxTaskSize() {
			return MAX_SEARCH_TASKSIZE;
		}

	}

	public static abstract class CoreBaseTaskImpl<T> extends BaseTaskImpl<T> {

		private IMsgTransfer msgTransfer;

		@Override
		final public T processTask() throws Exception {

			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			T t = null;
			try {
				msgTransfer.lockMsg();

				t = processTaskImpl();

				msgTransfer.notifyMsg();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				msgTransfer.unLockMsg();
			}
			return t;
		}

		public abstract T processTaskImpl() throws Exception;

		public IMsgTransfer getMsgTransfer() {
			return msgTransfer;
		}

		public void setMsgTransfer(IMsgTransfer msgTransfer) {
			this.msgTransfer = msgTransfer;
		}

	}

	public static class ToPageTask extends CoreBaseTaskImpl<GotoPageResult> {

		protected GotoPageCommand command;
		protected GotoPageResult result;
		protected IEpubBookManager bookManager;

		public ToPageTask(GotoPageCommand command, IEpubBookManager bookManager) {
			super();
			this.command = command;
			this.bookManager = bookManager;
		}

		@Override
		public GotoPageResult processTaskImpl() throws Exception {

			Chapter chapter = command.getChapter();
			int pageIndexInChapter = 1;
			int tPageIndex = pageIndexInChapter;
			int pageCount = -1;
			if (!command.isCacheChapter()) {
				pageCount = bookManager.getChapterPageCount(chapter, false);
			}
			if (command.isAnchor()) {
				pageIndexInChapter = bookManager.getPageIndexInHtmlByAnchor(
						chapter, command.getAnchor());
				tPageIndex = pageIndexInChapter;
			} else if (command.isLastPage()) {
				if (pageCount == -1) {
					pageIndexInChapter = bookManager.getChapterPageCount(
							chapter, false);
				}
				tPageIndex = pageIndexInChapter - 1;// 国为把阅读完成页当成书的一页处理的，所以实际应该减1
			} else {
				pageIndexInChapter = bookManager.getPageIndexInChapter(chapter,
						command.getElementIndex());
				tPageIndex = pageIndexInChapter;
			}
			LogM.i(getClass().getSimpleName(),
					" processTaskImpl " + chapter + ", type="
							+ command.getType() + ", "
							+ command.getElementIndex());

			IndexRange pageRange = bookManager.getPageStartAndEndIndex(chapter,
					tPageIndex);
			result = new GotoPageResult();
			result.setChapter(chapter);
			result.setPageIndexInChapter(pageIndexInChapter);
			result.setPageRange(pageRange);

			return result;
		}

		@Override
		public BaseTaskKey getTaskKey() {
			return command;
		}

		@Override
		public BaseTaskResult getTaskResult() {
			return result;
		}

	}

	public static class PreCompsTask extends CoreBaseTaskImpl<Object> {

		private PreCompCommand command;
		private PreCompResult result;
		private BaseBookManager bookManager;

		public PreCompsTask(PreCompCommand command, BaseBookManager bookManager) {
			super();
			this.command = command;
			this.bookManager = bookManager;
		}

		@Override
		public Object processTaskImpl() throws Exception {

			Chapter chapter = command.getChapter();
			int pageCount = bookManager.getNativePageCount(chapter);
			result = new PreCompResult();
			result.setPageCount(pageCount);

			return result;
		}

		@Override
		public BaseTaskKey getTaskKey() {
			return command;
		}

		@Override
		public BaseTaskResult getTaskResult() {
			return result;
		}
	}

	public static class DrawPageTask extends CoreBaseTaskImpl<DrawPageResult> {

		private DrawPageAsycCommand command;
		private DrawPageResult result;
		private BaseBookManager bookManager;

		public DrawPageTask(DrawPageAsycCommand command,
				BaseBookManager bookManager) {
			super();
			this.command = command;
			this.bookManager = bookManager;
		}

		@Override
		public DrawPageResult processTaskImpl() throws Exception {

			if (!bookManager.isBookComposingDone()) {
				bookManager.requestLoadChapter();
			}
			result = processTaskInner();

			return result;
		}

		private DrawPageResult processTaskInner() {
			DrawPageResult dpResult;
			Chapter chapter = command.getChapter();
			int pageIndexInChapter = command.getPageIndexInChapter();
			if (!command.isCache()) {
				int pCount = bookManager.getChapterPageCount(chapter);
				// int s = bookManager.saveChapterCache(chapter);
				if (command.isLast()) {
					pageIndexInChapter = pCount;
				}
				printLog("  processTask() PageCount = " + pCount);
			}
			int pageSeqNum = command.getPageSequenceNum();
			int width = command.getPageSize().getPageWidth();
			int height = command.getPageSize().getPageHeight();

			dpResult = bookManager.drawPageInner(chapter, pageIndexInChapter,
					pageSeqNum, width, height, true);

			return dpResult;
		}

		public DrawPageAsycCommand getCommand() {
			return command;
		}

		@Override
		public BaseTaskKey getTaskKey() {
			return command;
		}

		@Override
		public BaseTaskResult getTaskResult() {
			return result;
		}

	}

	public static class SearchTask extends BaseTaskImpl<SearchResult> {// extends
																		// CoreBaseTaskImpl<SearchResult>

		private SearchCommand command;
		private SearchResult result;
		private BaseBookManager bookManager;
		private List<Chapter> chapterList;

		public SearchTask(SearchCommand command, BaseBookManager bookManager,
				List<Chapter> chapterList) {
			super();
			this.command = command;
			this.bookManager = bookManager;
			this.chapterList = chapterList;
		}

		@Override
		public SearchResult processTask() throws Exception {
			return processTaskImpl();
		}

		public SearchResult processTaskImpl() throws Exception {

			result = new SearchResult();

			final SearchListener searchListener = command.getListener();
			final List<Chapter> chapters = chapterList;
			if (chapters == null || chapters.size() == 0) {
				printLogE(" bm search chapterList is empty ");
				return result;
			}
			searchListener.onStart();
			printLog(" search onStart ");
			final String word = command.getWord();
			boolean isBreak = false;
			for (int i = 0, len = chapters.size(); i < len; i++) {
				if (isAbortSearch()) {
					resetAbortSearch();
					printLogE(" bm search abort search ");
					isBreak = true;
					break;
				}
                Chapter chapter = chapters.get(i);

                if (chapter instanceof PartChapter){
                    PartChapter p = (PartChapter) chapter;
                    if (p.getPageCount()>0){
                        List<OneSearch> searchRs = bookManager.search(chapter,
                                word);
                        if (isAbortSearch()) {
                            resetAbortSearch();
                            printLogE(" bm search abort search ");
                            isBreak = true;
                            break;
                        }
                        searchListener.onSearch(searchRs);
                    }
                }else{
                    List<OneSearch> searchRs = bookManager.search(chapter,
                            word);
                    if (isAbortSearch()) {
                        resetAbortSearch();
                        printLogE(" bm search abort search ");
                        isBreak = true;
                        break;
                    }
                    searchListener.onSearch(searchRs);
                }

			}
			if (!isBreak) {
				searchListener.onEnd();
				printLog(" search onEnd ");
			}
			return result;
		}

		private boolean isAbortSearch() {
			return bookManager.isAbortSearch();
		}

		private void resetAbortSearch() {
			bookManager.resetAbortSearch();
		}

		@Override
		public BaseTaskKey getTaskKey() {
			return command;
		}

		@Override
		public BaseTaskResult getTaskResult() {
			return result;
		}

	}

	public static class DrawPageSyncCommand extends BaseDrawPageParam {

		private Chapter chapter;
		private int pageIndexInChapter;
		private int pageSequenceNum = 0;
		private PageSize pageSize;

		public Chapter getChapter() {
			return chapter;
		}

		public void setChapter(Chapter chapter) {
			this.chapter = chapter;
		}

		public int getPageIndexInChapter() {
			return pageIndexInChapter;
		}

		public void setPageIndexInChapter(int pageIndexInChapter) {
			this.pageIndexInChapter = pageIndexInChapter;
		}

		public int getPageSequenceNum() {
			return pageSequenceNum;
		}

		public void setPageSequenceNum(int pageSequenceNum) {
			this.pageSequenceNum = pageSequenceNum;
		}

		public PageSize getPageSize() {
			return pageSize;
		}

		public void setPageSize(PageSize pageSize) {
			this.pageSize = pageSize;
		}

		@Override
		public String toString() {
			return chapter + "-" + pageIndexInChapter;
		}

	}

	public static class DrawPageAsycCommand extends DrawPageSyncCommand {

		/**
		 * 获取Chapter的最后一页，就得先调排版，再drawPage
		 */
		private boolean isLast = false;
		/**
		 * false:先调排版，再drawPage
		 */
		private boolean isCache = true;

		private int bgType = -1;

		public boolean isLast() {
			return isLast;
		}

		public void setLast(boolean isLast) {
			this.isLast = isLast;
		}

		public boolean isCache() {
			return isCache;
		}

		public void setCache(boolean isCache) {
			this.isCache = isCache;
		}

		public int getBgType() {
			return bgType;
		}

		public void setBgType(int bgType) {
			this.bgType = bgType;
		}

	}

	public static class GotoPageCommand extends BaseTaskKey {

		public final static int TYPE_ELEMENTI = 1;
		public final static int TYPE_ANCHOR = 2;
		public final static int TYPE_LASTPAGE = 3;

		private int type = TYPE_ELEMENTI;
		private Chapter chapter;
		private String anchor;
		private int elementIndex;
		private boolean isCacheChapter = false;

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public Chapter getChapter() {
			return chapter;
		}

		public void setChapter(Chapter chapter) {
			this.chapter = chapter;
		}

		public String getAnchor() {
			return anchor;
		}

		public void setAnchor(String anchor) {
			this.anchor = anchor;
		}

		public int getElementIndex() {
			return elementIndex;
		}

		public void setElementIndex(int elementIndex) {
			this.elementIndex = elementIndex;
		}

		public boolean isAnchor() {
			return type == TYPE_ANCHOR;
		}

		public boolean isLastPage() {
			return type == TYPE_LASTPAGE;
		}

		public boolean isCacheChapter() {
			return isCacheChapter;
		}

		public void setCacheChapter(boolean isCacheChapter) {
			this.isCacheChapter = isCacheChapter;
		}
        //原创新增
        private boolean isBuy;// 是否购买此章
        private boolean isGotoLast;// 是否调转最后一页

        public boolean isGotoLast() {
            return isGotoLast;
        }

        public void setGotoLast(boolean isGotoLast) {
            this.isGotoLast = isGotoLast;
        }

        public boolean isBuy() {
            return isBuy;
        }

        public void setBuy(boolean isBuy) {
            this.isBuy = isBuy;
        }

	}

	public static class SearchCommand extends BaseTaskKey {

		private String word;
		private SearchListener listener;

		public SearchCommand(String word, SearchListener listener) {
			super();
			this.word = word;
			this.listener = listener;
		}

		public String getWord() {
			return word;
		}

		public SearchListener getListener() {
			return listener;
		}

	}

	public static class PreCompCommand extends BaseTaskKey {

		private Chapter chapter;

		public Chapter getChapter() {
			return chapter;
		}

		public void setChapter(Chapter chapter) {
			this.chapter = chapter;
		}

	}

	public static class DrawPageResult extends BaseDrawPageResult {

		/**
		 * drawPage状态，成功，失败...
		 */
		private int status;
		/**
		 * 页面类型
		 */
		private HashSet<PageType> pageType = new HashSet<PageType>();
		private Bitmap bitmap;
		private IndexRange pageRange;
		private GallaryData[] gallarys;
		private Rect videoRect;
		private List<InteractiveBlock> listInteractiveBlocks;

		public DrawPageResult() {
			pageType.add(PageType.Common);
		}
		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public Bitmap getBitmap() {
			return bitmap;
		}

		public void setBitmap(Bitmap bitmap) {
			this.bitmap = bitmap;
		}

		public HashSet<PageType> getPageType() {
			return pageType;
		}

		public void setPageType(HashSet<PageType> pageType) {
			this.pageType = pageType;
		}

		public IndexRange getPageRange() {
			return pageRange;
		}

		public void setPageRange(IndexRange pageRange) {
			this.pageRange = pageRange;
		}

		public GallaryData[] getGallarys() {
			return gallarys;
		}

		public void setGallarys(GallaryData[] gallarys) {
			this.gallarys = gallarys;
		}

		public void setVideoRect(Rect rect) {
			this.videoRect = rect;
		}

		public Rect getVideoRect() {
			return videoRect;
		}

		public List<InteractiveBlock> getListInteractiveBlocks() {
			return listInteractiveBlocks;
		}

		public void setListInteractiveBlocks(List<InteractiveBlock> listInteractiveBlocks) {
			this.listInteractiveBlocks = listInteractiveBlocks;
		}
	}

	public static class GotoPageResult extends BaseTaskResult {

		private Chapter chapter;
		private int pageIndexInChapter;
		private IndexRange pageRange;

		public Chapter getChapter() {
			return chapter;
		}

		public void setChapter(Chapter chapter) {
			this.chapter = chapter;
		}

		public int getPageIndexInChapter() {
			return pageIndexInChapter;
		}

		public void setPageIndexInChapter(int pageIndexInChapter) {
			this.pageIndexInChapter = pageIndexInChapter;
		}

		public IndexRange getPageRange() {
			return pageRange;
		}

		public void setPageRange(IndexRange pageRange) {
			this.pageRange = pageRange;
		}

	}

	public static class SearchResult extends BaseTaskResult {

		private List<OneSearch> searchDatas;

		public List<OneSearch> getSearchDatas() {
			return searchDatas;
		}

		public void setSearchDatas(List<OneSearch> searchDatas) {
			this.searchDatas = searchDatas;
		}

	}

	public static class PreCompResult extends BaseTaskResult {

		private int pageCount;

		public int getPageCount() {
			return pageCount;
		}

		public void setPageCount(int pageCount) {
			this.pageCount = pageCount;
		}

	}

	public interface IAsynListener {

		public void onAsyn(BaseTaskKey command, BaseTaskResult result);

	}

	public static class PageSize {

		private int pageWidth;
		private int pageHeight;

		public PageSize(int pageWidth, int pageHeight) {
			super();
			this.pageWidth = pageWidth;
			this.pageHeight = pageHeight;
		}

		public int getPageWidth() {
			return pageWidth;
		}

		public void setPageWidth(int pageWidth) {
			this.pageWidth = pageWidth;
		}

		public int getPageHeight() {
			return pageHeight;
		}

		public void setPageHeight(int pageHeight) {
			this.pageHeight = pageHeight;
		}

	}

	public static class SyncHandler {

		private final Lock mComposingLock = new ReentrantLock(true);
		private final Condition mComposingCodition = mComposingLock
				.newCondition();
		private final Condition mLoadDataCondition = mComposingLock
				.newCondition();
		private final AtomicBoolean hasComposingWait = new AtomicBoolean(false);
		private final AtomicBoolean hasAsycLoadChapterData = new AtomicBoolean(
				false);

		public void lockComposing() {
			/**
			 * 此日志影响cpu对线程的调度
			 */
			// LogM.i(getClass().getSimpleName(), " in lockComposing() ");
			printLog(" in lockComposing() ");
			mComposingLock.lock();
			// LogM.i(getClass().getSimpleName(), " out lockComposing() ");
			printLog(" out lockComposing() ");
		}

		public void unLockComposing() {
			mComposingLock.unlock();
		}

		public void notifyComposing() {
			mComposingCodition.signal();
			reSetComposingWait();
		}

		// ------------------------------

		public void processComposingWait() throws InterruptedException {
			if (hasComposingWait.get()) {
				reSetComposingWait();
				LogM.d(getClass().getSimpleName(),
						"lux2 in ComposingCodition.await() ");
				mComposingCodition.await();
				LogM.d(getClass().getSimpleName(),
						"lux2 out ComposingCodition.await() ");
			}
		}

		public void notifyLoadData() {
			if (hasAsycLoadChapterData.get()) {
				reSetAsycLoadChapterData();
				mLoadDataCondition.signal();
				LogM.d(getClass().getSimpleName(),
						"lux2 LoadDataCondition.signal() ");
			}
		}

		public void loadDataConditionAwait() throws InterruptedException {
			mLoadDataCondition.await();
		}

		public void requestComposingWait() {
			hasComposingWait.set(true);
		}

		public void reSetComposingWait() {
			hasComposingWait.set(false);
		}

		public void reSetAsycLoadChapterData() {
			hasAsycLoadChapterData.set(false);
		}

		public void setAsycLoadChapterData() {
			hasAsycLoadChapterData.set(true);
		}

		protected void printLog(String msg) {
			// LogM.i(getClass().getSimpleName()
		}
	}

}
