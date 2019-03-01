package com.dangdang.reader.personal.domain;

import com.dangdang.reader.common.domain.BaseBook;

public class ShelfBaseBook extends BaseBook {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected long mId; 					// 数据库自增id
	protected long mBooksize; 				// 书籍大小
	protected String mBookJson; 			// 书籍信息
	protected String mUserId; 				// 下载该书的用户id，默认为：dangdang_default_user
	protected String mUserName; 			// 下载该书的用户名，默认为：dangdang_default_user
	protected GroupType mGroupType; 		// 分组类别，0：未分组	
	protected String mExpColumn1;			// 扩展字段1
	protected String mExpColumn2;			// 扩展字段2
	protected String mExpColumn3;			// 扩展字段3
	protected int isFull;//是否已完结,0：未完结，1：已完结
	
	/**
	 * 云书架已购需要
	 */
	protected String mBuyType;				// 购买、赠书、免费全本、小说
	protected String mAuthorityId;
	protected int isHide;
	
	public String getExpColumn1(){
		return this.mExpColumn1;
	}
	
	public int getIsFull() {
		return isFull;
	}

	public void setIsFull(int isFull) {
		this.isFull = isFull;
	}

	public void setExpColumn1(String str){
		this.mExpColumn1 = str;
	}
	
	public String getExpColumn2(){
		return this.mExpColumn2;
	}
	
	public void setExpColumn2(String str){
		this.mExpColumn2 = str;
	}
	
	public String getExpColumn3(){
		return this.mExpColumn1;
	}
	
	public void setExpColumn3(String str){
		this.mExpColumn3 = str;
	}
	
	public long getBookSize(){
		return this.mBooksize;
	}
	
	public void setBookSize(long size){
		this.mBooksize = size;
	}
	
	public long getId(){
		return mId;
	}

	public String getUserId(){
		if(mUserId == null)
			return "";
		return this.mUserId;
	}
	
	public String getUserName(){
		if(mUserName == null)
			return "";
		return this.mUserName;
	}
	
	public void setId(long id){
		mId = id;
	}
	
	public String getBookJson(){
		return this.mBookJson;
	}
	
	public void setBookJson(String json){
		mBookJson = json;
	}

	public GroupType getGroupType(){
		return this.mGroupType;
	}

	public void setGroupType(GroupType type){
		mGroupType = type;
	}
	
	public void setUserId(String id){
		if(id == null)
			id = "";
		mUserId = id;
	}
	
	public void setUserName(String name){
		if(name == null)
			name = "";
		mUserName = name;
	}

	/**
	 * 给云书架使用的字段
		if("1001".equals(str))
			str = "已购";
		else if("1002".equals(str))
			str = "免费";
		else if("1004".equals(str))
			str = "赠书";
		else if("2000".equals(str))
			str = "试读";
	 * @return
	 */
	public String getBuyType(){
		return mBuyType;
	}
	
	public void setBuyType(String str){
		mBuyType = str;
	}
	
	public String getAuthorityId(){
		return mAuthorityId;
	}
	
	public void setAuthorityId(String id){
		mAuthorityId = id;
	}
	
	public int isHide(){
		return isHide;
	}
	
	public void setHide(int i){
		isHide = i;
	}
}
