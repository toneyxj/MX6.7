package com.dangdang.reader.db;


public class BuyBookDBColumn {

	public static final String ID = "_id";
	public static final String BOOK_ID = "book_id";
	public static final String BOOK_NAME = "book_name";
	public static final String AUTHOR = "author"; // 在 3.8 版本中，去掉 ，因为
													// mBookJson中有该值,该字段暂时为空
	
	public static final String AUTHORITY_TYPE = "publish_date"; 	// 1全本购买，2章节购买
	public static final String BOOK_SIZE = "book_size";
	public static final String COVER_URL = "cover_url"; // 在 3.8 版本中，去掉 ，因为
														// mBookJson中有该值
	public static final String BOOK_JSON = "book_json";
	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String TYPE_ID = "type_id"; // 类别id
	public static final String RELATION_TYPE = "relation_type";// 区分购买的全本、借阅、免费的类型
	public static final String IS_HIDE = "is_down"; // 是否隐藏，0：显示，1：隐藏
	public static final String DESC = "desc"; // 书籍简介
	public static final String BOOK_NAME_PINYIN = "book_name_pinyin";
	public static final String LAST_TIME = "last_time";
	public static final String ExpColumn1 = "expcolumn1";
	public static final String ExpColumn2 = "expcolumn2"; // 统计阅读时间
	public static final String ExpColumn3 = "expcolumn3"; // 保存书结构(章节列表)
//
	public static final String BUY_TABLE = "buy_book";
	
	
	
	public static final String CATEGORY_TABLE = "cloud_category";
	public static final String TYPE_TABLE = "cloud_type";
	
	public static final String CATEGORY = "category";
	public static final String CREATE_TIME = "create_time";
	public static final String BOOK_NUM = "book_num";
//	
//	
//	private long mId; 				// 数据库自增id
//	private String mBookId; 		// 书籍mediaId
//	private String mBookName; 		// 书籍名称
//	private String mAuthor; 		// 作者名称
//	private String mPublishDate; 	// 出版日期
//	private long mBooksize; 		// 书籍大小
//	private String mCoverUrl; 		// 书籍封面图片URL
//	private String mBookJson; 		// 书籍大概信息
//	private String mUserId; 		// 下载该书的用户id
//	private String mUserName; 		// 下载该书的用户名
//	private String mDesc = ""; 		// 简介
//	private Bitmap mCoverBitmap; 	// 封面bitmap
//	private int mIsDown; 			// 已下载标志
//	private int mGroupId; 			// 分组ID
//	private String mCategory;		// 平台所属分组名
//	private int mType;				// 书籍类型，已购、赠书、免费全本、小说 等	
//	private int mSequence;			// 时间排序序号
//	private String mAuthorityId;	// 授权ID，分页时使用
//	private long mTime;				// 获得授权的时间
//	private String pinyin;			// 书名的拼音，做排序用
//
//	// 扩展字段
//	// 3.8版本之前 expColumn1 在书架本地 默认做为阅读时间， 3.8 版本以后，本地为 阅读时间，借阅时间和时长（如果是借阅书籍），
//	// expColumn1 在 本地书架存放阅读时间 ，在未下载 存放 书籍类型（全本，借阅或者是 免费）
//	private String expColumn1;
//	private String expColumn2;
//	private String expColumn3;

	public static String createBuyTable() {
		String sql = "create table IF NOT EXISTS " + BuyBookDBColumn.BUY_TABLE
				+ " (" + BuyBookDBColumn.ID + " integer primary key autoincrement, "
				+ BuyBookDBColumn.BOOK_ID + "  varchar unique not null, "
				+ BuyBookDBColumn.BOOK_NAME + "  varchar, " 
				+ BuyBookDBColumn.AUTHOR + " varchar, " 
				+ BuyBookDBColumn.AUTHORITY_TYPE + " varchar, "
				+ BuyBookDBColumn.BOOK_SIZE + " long default 0, "
				+ BuyBookDBColumn.COVER_URL + "  varchar, " 
				+ BuyBookDBColumn.BOOK_JSON + " varchar, " 
				+ BuyBookDBColumn.USER_ID + " varchar, "
				+ BuyBookDBColumn.USER_NAME + " varchar, "
				+ BuyBookDBColumn.IS_HIDE + " int default 0, "
				+ // 默认为0， 0为显示， 1为隐藏
				BuyBookDBColumn.TYPE_ID + " int default 0, " 
				+ BuyBookDBColumn.BOOK_NAME_PINYIN + " varchar, "
				+ BuyBookDBColumn.LAST_TIME + " long default 0, "
				+ BuyBookDBColumn.RELATION_TYPE + " varchar, "
				+ BuyBookDBColumn.ExpColumn1 + " varchar, " 
				+ BuyBookDBColumn.ExpColumn2 + " varchar, "
				+ BuyBookDBColumn.ExpColumn3 + " varchar);";
		return sql;
	}

	public static String createBuyTableIndex(){
		return "create index if not exists id_index on " + BuyBookDBColumn.BUY_TABLE 
				+ " (" + BuyBookDBColumn.BOOK_ID + ","
				+ BuyBookDBColumn.RELATION_TYPE + ","
				+ BuyBookDBColumn.IS_HIDE + ");";
	}
	
	public static String createCategoryTable(String name) {
		String sql = "create table IF NOT EXISTS " + 
				name + " (" + 
				BuyBookDBColumn.ID + " integer primary key autoincrement, " +
				BuyBookDBColumn.CATEGORY + " varchar, " +
				BuyBookDBColumn.CREATE_TIME + " long default 0, " + 
				BuyBookDBColumn.BOOK_NUM + " int default 0, " + 
				BuyBookDBColumn.ExpColumn1 + " varchar, " + 
				BuyBookDBColumn.ExpColumn2 + " varchar);"; 
		return sql;
	}
	
//	public static String createHideTable() {
//		String sql = "create table IF NOT EXISTS " + BuyBookDBColumn.CATEGORY_TABLE
//				+ " (" + BuyBookDBColumn.ID + " integer primary key autoincrement, "
//				+ BuyBookDBColumn.BOOK_ID + "  varchar unique not null, "
//				+ BuyBookDBColumn.BOOK_NAME + "  varchar, " 
//				+ BuyBookDBColumn.AUTHOR + " varchar, " 
//				+ BuyBookDBColumn.PUBLISH_DATE + " varchar, "
//				+ BuyBookDBColumn.BOOK_SIZE + " long default 0, "
//				+ BuyBookDBColumn.COVER_URL + "  varchar, " 
//				+ BuyBookDBColumn.BOOK_JSON + " varchar, " 
//				+ BuyBookDBColumn.USER_ID + " varchar, "
//				+ BuyBookDBColumn.USER_NAME + " varchar, "
//				+ BuyBookDBColumn.IS_HIDE + " int default 0, "
//				+ // 默认为0， 0为显示， 1为隐藏
//				BuyBookDBColumn.TYPE_ID + " int default 0, " 
//				+ BuyBookDBColumn.AUTHORITY_ID + " varchar, "
//				+ BuyBookDBColumn.BOOK_NAME_PINYIN + " varchar, "
//				+ BuyBookDBColumn.LAST_TIME + " long default 0, "
//				+ BuyBookDBColumn.RELATION_TYPE + " int default 0, "
//				+ BuyBookDBColumn.ExpColumn1 + " varchar, " 
//				+ BuyBookDBColumn.ExpColumn2 + " varchar, "
//				+ BuyBookDBColumn.ExpColumn3 + " varchar);";
//		return sql;
//	}
}
