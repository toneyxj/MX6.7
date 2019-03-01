package com.dangdang.reader.domain;

public class ShelfDownload {

	public int id;
	public String indentityId;
	public String url;
	public String saveDir;
	
	/**
	 * 下载时，不更新sqlite 暂不用
	 */
	public long progress;
	public long totalSize;
	public String status;
	public long timeStamp;
	public String bookData;
	public String userName;
	public String type;
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("{indentityId=");
		sb.append(indentityId);
		sb.append(",status=");
		sb.append(status);
		
		return sb.toString();
	}
	
	public final static String TABLE_NAME = "downloads";
	
	public final static String ID = "_id";
	public final static String INDENTITY_ID = "indentity_id";
	public final static String URL = "url";
	public final static String SAVE_DIR = "save_dir";
	public final static String PROGRESS = "progress";
	public final static String TOTALSIZE = "totalsize";
	public final static String STATUS = "status";
	public final static String TIME = "time";
	public final static String EXTEN = "exten";
	public final static String USER = "username";
	public final static String TYPE = "type";
	
	public static String CREATE_SQL = "";
	
	static{
		StringBuilder mSql = new StringBuilder();
		mSql.append("CREATE TABLE ");
		mSql.append(TABLE_NAME);
		mSql.append("(");
		mSql.append(ID);
		mSql.append(" integer primary key autoincrement, ");
		mSql.append(INDENTITY_ID);
		mSql.append(" varchar(1000),");
		mSql.append(URL);
		mSql.append(" varchar(2000),");
		mSql.append(SAVE_DIR);
		mSql.append(" varchar(2000),");
		mSql.append(PROGRESS);
		mSql.append(" integer(32),");
		mSql.append(TOTALSIZE);
		mSql.append(" integer(32),");
		mSql.append(TIME);
		mSql.append(" TIMESTAMP(6),");
		mSql.append(STATUS);
		mSql.append(" varchar(50),");
		mSql.append(EXTEN);
		mSql.append(" varchar(5000),");
		mSql.append(USER);
		mSql.append(" varchar(500),");
		mSql.append(TYPE);
		mSql.append(" varchar(500)");
		mSql.append(")");
		
		CREATE_SQL = mSql.toString();
	}
	
}
