package com.dangdang.reader.personal.domain;

import java.io.Serializable;

public class GroupType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int mId;                //数据库自增id  //id = 0 ，说明当前处于 未分类 状态
	private String mName;           //分类名称
	private long mCreateTime;       //类别创建时间
	
	//扩展字段
	private String expColumn1;
	private String expColumn2;
	
	public class TypeColumn {
		public static final String ID = "_id";
		public static final String NAME = "name";
		public static final String CREATE_TIME = "create_time";
		
		public static final String ExpColumn1 = "expcolumn1";
		public static final String ExpColumn2 = "expcolumn2";
		
		public static final String SHELF_TYPE_TABLE = "shelfbook_group";
		public static final String UNDOWN_TYPE_TABLE = "undownbook_type";
	}
	
	public static String createBookTypeTable(String tableName) {
		String sql = "create table IF NOT EXISTS " + 
				tableName + " (" + 
				TypeColumn.ID + " integer primary key autoincrement, " +
				TypeColumn.NAME + " varchar, " +
				TypeColumn.CREATE_TIME + " long default 0, " + 
				TypeColumn.ExpColumn1 + " varchar, " + 
				TypeColumn.ExpColumn2 + " varchar);"; 
		return sql;
	}
	
	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public long getCreateTime() {
		return mCreateTime;
	}

	public void setCreateTime(long mCreateTime) {
		this.mCreateTime = mCreateTime;
	}

	public String getExpColumn1() {
		return expColumn1;
	}

	public void setExpColumn1(String expColumn1) {
		this.expColumn1 = expColumn1;
	}

	public String getExpColumn2() {
		return expColumn2;
	}

	public void setExpColumn2(String expColumn2) {
		this.expColumn2 = expColumn2;
	}
}
