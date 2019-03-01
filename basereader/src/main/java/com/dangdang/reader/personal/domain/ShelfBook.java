package com.dangdang.reader.personal.domain;

import com.dangdang.reader.db.ShelfBookDBColumn;
import com.dangdang.zframework.network.download.DownloadConstant.Status;


public class ShelfBook extends ShelfBaseBook {
	
	/**
	 * static
	 */
	private static final long serialVersionUID = 1L;

	private int mGroupId; 							// 分组id，0：未分组
	private String mBookDir;       					// 书籍在手机上的保存路径
	private byte[] mBookKey;       					// 书籍key值，按章或者全本下载的时候需要
	private int mBookFinish;       					// 下载是否完成，0未完成， 1完成
	private BookType mBookType;        				// 书籍类型，0：未完结；1：已完结
	private long mLastTime;        					// 最后操作时间（下载开始时间 和 阅读时间）
	private String mReadProgress;					// 阅读进度
	private boolean isFollow;						// 是否追更，用于追更业务
	private boolean isPreload;						// 是否自动购买
	private int localLastIndexOrder;				// 本地最新章节序号，用于追更业务
	private int serverLastIndexOrder;				// 服务端最新章节序号，用于追更业务
	private MonthlyPaymentType mMonthlyPaymentType;	// 包月类型
	private long mDeadline;							// 包月截止时间毫秒值
	private Status mDownloadStatus;					// 下载状态
	private boolean isSelect;						// 是否选中，编辑书架中的书时有效
	private boolean isDown;							// 是否下架
	private boolean isImport;						// 是否本地导入
	
	private TryOrFull mTryOrFull;        			// 试读或者全本, 0 内置试读 ，1 试读， 2购买 全本, 3 内置全本， 4 借阅全本
	private int mOverDue;							// 是否借阅过期
	private long mBorrowStartTime;					// 借阅开始时间
	private long mBorrowTotalTime;					// 借阅持续时间
	private boolean mCanBorrow = true;				// 是否可以续借
	private boolean mIsOthers;						// 是否偷来的
	private int mStealPercent = 100;				// 偷来的比例
	private String mTotalTime;						// 阅读总时长
	private boolean isUpdate;						// try or full 发生了变化
	
	private int progress = -1;						// 下载进度	
	private String mAuthType;						// 1全本，2章节	
	public int isValid;

	private boolean channelHall;                        //bao yue guan

	/**
	 * 书籍类型，0：未完结；1：已完结;Integer.MAX：出版物
	 * @author xiaruri
	 *
	 */
	public static enum BookType {
		BOOK_TYPE_IS_FULL_NO(0), 	// 连载，未完结
		BOOK_TYPE_IS_FULL_YES(1), 	// 已完结
		BOOK_TYPE_NOT_NOVEL(Integer.MAX_VALUE);	//非原创书
		
		private int mCode;
		
		private BookType(int code){
			mCode = code;
		}
		
		public int getValue(){
			return mCode;
		}
		
		public static BookType valueOf(int value) {   
	        switch (value) {
	        case 0:
	            return BOOK_TYPE_IS_FULL_NO;
	        case 1:
	        	return BOOK_TYPE_IS_FULL_YES;
	        case Integer.MAX_VALUE:
	        	return BOOK_TYPE_NOT_NOVEL;
	        default:
	        	return BOOK_TYPE_NOT_NOVEL;
	        }
	    }
	}
	
	public static enum TryOrFull {
		INNER_TRY,
		TRY,
		FULL,
		INNER_FULL,
		BORROW_FULL,
		MONTH_FULL,
		GIFT_FULL;
		
		public static TryOrFull valueOf(int value) {   
	        switch (value) {
	        case 0:
	        	return INNER_TRY;
	        case 1:
	        	return TRY;
	        case 2:
	        	return FULL;
	        case 3:
	        	return INNER_FULL;
	        case 4:
	        	return BORROW_FULL;
	        case 5:
	        	return MONTH_FULL;
	        case 6:
	        	return GIFT_FULL;
	        default:
	        	return FULL;
	        }
	    }
	}
	
	/**
	 * 包月类型
	 * @author xiaruri
	 *
	 */
	public static enum MonthlyPaymentType {
		DEFAULT_VALUE,			// 没有包月
		ALL; 					// 全场包月
		
		public static MonthlyPaymentType valueOf(int value) {    
	        switch (value) {
	        case 1:
	            return ALL;
	        default:
	        	return DEFAULT_VALUE;
	        }
	    }
	}
	
	public static String createShelfTable(){
		String sql = "create table IF NOT EXISTS " + 
				ShelfBookDBColumn.SHELF_TABLE_NAME + " (" + 
				ShelfBookDBColumn.ID + " integer primary key autoincrement, " +
				ShelfBookDBColumn.BOOK_ID + "  varchar unique not null, " +
				ShelfBookDBColumn.BOOK_NAME + "  varchar, " +
				ShelfBookDBColumn.AUTHOR + " varchar, " + 
				ShelfBookDBColumn.BOOK_JSON + " varchar, " + 
				ShelfBookDBColumn.BOOK_DIR + " varchar, " +
				ShelfBookDBColumn.BOOK_KEY + " blob, " +
				ShelfBookDBColumn.BOOK_FINISH + " int default 0, " + 
				ShelfBookDBColumn.BOOK_TYPE + " int default 0, " +
				ShelfBookDBColumn.TRY_OR_FULL + " int default 0, " +
				ShelfBookDBColumn.READ_PROGRESS + " varchar, " + 
				ShelfBookDBColumn.LAST_TIME + " long default 0, " + 
				ShelfBookDBColumn.USER_ID + " varchar, " + 
				ShelfBookDBColumn.USER_NAME + " varchar, " + 
				ShelfBookDBColumn.GROUP_ID + " int default 0, " + 
				ShelfBookDBColumn.LOCAL_GROUP_ID + " int default 0, " + 
				ShelfBookDBColumn.IS_FOLLOW + " int default 0, " + 
				ShelfBookDBColumn.MONTHLY_PAYMENT_TYPE + "  int default 0, " + 
				ShelfBookDBColumn.MONTHLY_END_TIME + " long default 0, " +
				ShelfBookDBColumn.LOCAL_IMPORT + " int default 0, " +
				ShelfBookDBColumn.OVER_DUE + " int default 0, " +
				ShelfBookDBColumn.BOOK_STRUCT + " varchar, " +
				ShelfBookDBColumn.TOTAL_TIME + " varchar, " +
				ShelfBookDBColumn.ExpColumn1 + " varchar, " + 
				ShelfBookDBColumn.ExpColumn2 + " varchar, " + 
				ShelfBookDBColumn.ExpColumn3 + " varchar);";
		return sql;
	}
	
	public static String createShelfTableIndex(){
		return "create index if not exists book_id_index on " + ShelfBookDBColumn.SHELF_TABLE_NAME + " (" + ShelfBookDBColumn.BOOK_ID + ");";
	}
	
	public String getAuthorityType() {
		return mAuthType;
	}

	public void setAuthorityType(String type) {
		this.mAuthType = type;
	}
	
	public void setPreload(boolean bo){
		isPreload = bo;
	}
	
	public boolean isPreload(){
		return isPreload;
	}
	
	public void setUpdate(boolean bo){
		isUpdate = bo;
	}
	
	public boolean isUpdate(){
		return isUpdate;
	}
	
	public void setTotalTime(String str){
		this.mTotalTime = str;
	}
	
	public String getTotalTime(){
		return mTotalTime;
	}
	
	public void setStealPercent(int per){
		this.mStealPercent = per;
	}
	
	public int getStealPercent(){
		return this.mStealPercent;
	}
	
	public void setIsOthers(boolean bo) {
		this.mIsOthers = bo;
	}

	public boolean getIsOthers() {
		return this.mIsOthers;
	}
	
	public long getBorrowStartTime() {
		return mBorrowStartTime;
	}

	public void setBorrowStartTime(long mBorrowStartTime) {
		this.mBorrowStartTime = mBorrowStartTime;
	}

	public long getBorrowTotalTime() {
		return mBorrowTotalTime;
	}

	public void setBorrowTotalTime(long mBorrowTotalTime) {
		this.mBorrowTotalTime = mBorrowTotalTime;
	}

	public void setCanBorrow(boolean bo){
		mCanBorrow = bo;
	}
	
	public boolean canBorrow(){
		return mCanBorrow;
	}
	
	public int getOverDue() {
		return mOverDue;
	}

	public void setOverDue(int mOverDue) {
		this.mOverDue = mOverDue;
	}
	
	public void setTryOrFull(TryOrFull type){
		this.mTryOrFull = type;
	}
	
	public TryOrFull getTryOrFull(){
		return mTryOrFull;
	}
	
	public void setImport(boolean bo){
		this.isImport = bo;
	}
	
	public boolean isImport(){
		return this.isImport;
	}
	
	public int getGroupId(){
		return this.mGroupId;
	}
	
	public void setGroupId(int id){
		this.mGroupId = id;
	}
	
	public String getBookDir(){
		return this.mBookDir;
	}
	
	public byte[] getBookKey(){
		return this.mBookKey;
	}
	
	public int getBookFinish(){
		return this.mBookFinish;
	}
	
	public BookType getBookType(){
		return this.mBookType;
	}
	
	public String getReadProgress(){
		return this.mReadProgress;
	}
	
	public long getLastTime(){
		return this.mLastTime;
	}
	
	public void setBookDir(String str){
		mBookDir = str;
	}
	
	public void setBookKey(byte[] data){
		mBookKey = data;
	}
	
	public void setBookFinish(int i){
		mBookFinish = i;
	}

	public void setBookType(BookType value){
		mBookType = value;
	}
	
	public void setReadProgress(String str){
		mReadProgress = str;
	}
	
	public void setLastTime(long time){
		mLastTime = time;
	}
	
	public boolean isSelect(){
		return this.isSelect;
	}
	
	public void setSelect(boolean bo){
		this.isSelect = bo;
	}
	
	public Status getDownloadStatus(){
		return mDownloadStatus;
	}
	
	public void setDownloadStatus(Status s){
		mDownloadStatus = s;
	}
	
	public boolean isFollow(){
		return this.isFollow;
	}
	
	public void setFollow(boolean bo){
		isFollow = bo;
	}

	public int getLocalLastIndexOrder() {
		return localLastIndexOrder;
	}

	public void setLocalLastIndexOrder(int localLastIndexOrder) {
		this.localLastIndexOrder = localLastIndexOrder;
	}

	public int getServerLastIndexOrder() {
		return serverLastIndexOrder;
	}

	public void setServerLastIndexOrder(int serverLastIndexOrder) {
		this.serverLastIndexOrder = serverLastIndexOrder;
	}

    public void setMonthlyPaymentType(MonthlyPaymentType type){
		this.mMonthlyPaymentType = type;
	}

	public MonthlyPaymentType getMonthlyPaymentType(){
		return this.mMonthlyPaymentType;
	}
	
	public void setDeadline(long time){
		this.mDeadline = time;
	}
	
	public long getDeadline(){
		return this.mDeadline;
	}

	public boolean isDown() {
		return isDown;
	}

	public void setDown(boolean isDown) {
		this.isDown = isDown;
	}	
	
	public int getProgress(){
		return progress;
	}
	
	public void setProgress(int i){
		progress = i;
	}

	public boolean isChannelHall() {
		return channelHall;
	}

	public void setChannelHall(boolean channelHall) {
		this.channelHall = channelHall;
	}
}
