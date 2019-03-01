package com.dangdang.reader.cloud;

/**
 * 云同步状态相关类
 */
public class Status {
	
	/**
	 * 书签、笔记状态:新增 
	 */
	public final static int COLUMN_NEW = 1;
	/**
	 * 书签、笔记状态: 修改 
	 */
	public final static int COLUMN_UPDATE = 2;
	/**
	 * 书签、笔记状态 :删除 
	 */
	public final static int COLUMN_DELETE = 3;
	
	/**
	 * 同步状态cloudStatus(已同步)
	 */
	public final static int CLOUD_YES = 1;
	/**
	 * 同步状态cloudStatus(未同步)
	 */
	public final static int CLOUD_NO = -1;
	
	
	public static boolean isNew(int status){
		return status == COLUMN_NEW;
	}
	
	public static boolean isUpdate(int status){
		return status == COLUMN_UPDATE;
	}
	
	public static boolean isDelete(int status){
		return status == COLUMN_DELETE;
	}
	
	public static boolean isCloudSync(int cloudStatus){
		return cloudStatus == CLOUD_YES;
	}
	
}