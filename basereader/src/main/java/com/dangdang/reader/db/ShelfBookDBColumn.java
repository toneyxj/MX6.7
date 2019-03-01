package com.dangdang.reader.db;


public class ShelfBookDBColumn{

	public static final String SHELF_DB_TABLE_NAME = "shelfbook";
	public static final String SHELF_TABLE_NAME = "shelfbook_5";
	public static final String ID = "_id";
	public static final String BOOK_ID = "book_id";								// 书籍id，原创书城的书则为mediaId；第三方导入的书则为根据书名生成的hash值
	public static final String BOOK_NAME = "book_name";							// 书名
	public static final String AUTHOR = "author";								// 作者
	public static final String BOOK_TYPE = "book_type";							// 书籍类型，0：未完结；1：已完结
	public static final String TRY_OR_FULL = "try_or_full";						// 书籍类型
	public static final String BOOK_JSON = "book_json";							// 书籍信息json字符串
	public static final String LAST_TIME = "last_time";							// 加入书架时间
	public static final String BOOK_DIR = "book_dir";							// 书籍在手机上的保存路径
	public static final String GROUP_ID = "group_id"; 							// 分组id（平台分组ID）
	public static final String LOCAL_GROUP_ID = "local_group_id";				// 本地分组id
	public static final String BOOK_KEY = "book_key";							// 书籍key值，按章或者全本下载的时候需要
	public static final String BOOK_FINISH = "book_finish";						// 下载是否完成，0未完成， 1完成
	public static final String READ_PROGRESS = "read_progress";					// 阅读记录
	public static final String IS_FOLLOW = "is_follow";							// 是否追更，0：未追更；1：追更
	public static final String MONTHLY_PAYMENT_TYPE = "monthly_payment_type";	// 包月类型，0：未包月；1：全场包月
	public static final String MONTHLY_END_TIME = "monthly_end_Time";			// 包月截止时间毫秒值
	public static final String USER_ID = "user_id";								// 下载该书的用户id，默认为：dangdang_default_user 
	public static final String USER_NAME = "user_name";							// 下载该书的用户id，默认为：dangdang_default_user
	public static final String LOCAL_IMPORT = "local_import";					// 是否是本地导入书
	public static final String BOOK_STRUCT = "book_struct";						// 章节结构
	public static final String OVER_DUE = "overdue"; 							// 借阅过期标志
	public static final String TOTAL_TIME = "total_time";						// 阅读总时长
	public static final String ExpColumn1 = "expcolumn1";						// 扩展字段1    5.3版本用来标识是否同步过，批量同步  1已同步
	public static final String ExpColumn2 = "expcolumn2"; 						// 扩展字段2
	public static final String ExpColumn3 = "expcolumn3"; 						// 扩展字段3
	
}
