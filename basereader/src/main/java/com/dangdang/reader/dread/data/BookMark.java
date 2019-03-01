package com.dangdang.reader.dread.data;



public class BookMark implements Cloneable {
	
	public int id;
	public String pId;
	public int isBought;
	
	public String bookPath = "";
	public int chapterIndex;//html path index
	public int elementIndex; // by chapter or by html
	
	public String chapterName;
	/**
	 * clientOperateTime
	 * 单位：秒
	 */
	public long markTime;//
	public String markText;
	
	/**
	 * 扩展字段
	 */
	/**
	 * 状态Status(1新增，2修改，3删除)
	 * 对老数据兼容处理：默认为新增
	 */
	public String status;//
	/**
	 *  是否同步cloudStatus(1：已同步, -1：未同步)
	 *  对老数据兼容处理：默认为未同步
	 */
	public String cloudStatus;
	/**
	 * modifyTime
	 * 单位：秒
	 */
	public String modifyTime;
	
	public boolean isChapterHead;//非数据库字段
	
	
	/**
	 * bookModVersion
	 * 书籍修改版本号
	 */
	public String bookModVersion;
	
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public int getIsBought() {
		return isBought;
	}

	public void setIsBought(int isBought) {
		this.isBought = isBought;
	}

	public int getChapterIndex() {
		return chapterIndex;
	}

	public void setChapterIndex(int chapterIndex) {
		this.chapterIndex = chapterIndex;
	}

	public int getElementIndex() {
		return elementIndex;
	}

	public void setElementIndex(int elementIndex) {
		this.elementIndex = elementIndex;
	}

	public String getChapterName() {
		return chapterName;
	}

	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}

	/**
	 * clientOperateTime
	 */
	public long getMarkTime() {
		return markTime;
	}

	/**
	 * clientOperateTime
	 */
	public void setMarkTime(long markTime) {
		this.markTime = markTime;
	}

	public String getMarkText() {
		return markText;
	}

	public void setMarkText(String markText) {
		this.markText = markText;
	}

	/**
	 * 状态Status(1新增，2修改，3删除)
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 状态Status(1新增，2修改，3删除)
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 *  是否同步cloudStatus
	 */
	public String getCloudStatus() {
		return cloudStatus;
	}

	/**
	 *  是否同步cloudStatus
	 */
	public void setCloudStatus(String cloudStatus) {
		this.cloudStatus = cloudStatus;
	}

	/**
	 * modifyTime
	 */
	public String getModifyTime() {
		return modifyTime;
	}

	/**
	 * modifyTime
	 */
	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	public String getBookPath() {
		return bookPath;
	}

	public void setBookPath(String bookPath) {
		this.bookPath = bookPath;
	}

	public boolean isChapterHead() {
		return isChapterHead;
	}

	public void setChapterHead(boolean isChapterHead) {
		this.isChapterHead = isChapterHead;
	}


	/**
	 * bookModVersion
	 */
	public String getBookModVersion() {
		return bookModVersion;
	}

	/**
	 * bookModVersion
	 */
	public void setBookModVersion(String bookModVersion) {
		this.bookModVersion = bookModVersion;
	}



	public class Column {
		
		public final static String TableName = "newmarks";
		public final static String Id = "_id";
		public final static String Pid = "pid";
		public final static String IsBought = "isbought";
		public final static String BookPath = "bookpath";
		public final static String ChapterIndex = "chapterindex";//TODO chapterIndex
		public final static String ElementIndex = "elementindex";
		public final static String ChapterName = "chaptername";
		/**
		 * clientOperateTime
		 */
		public final static String MarkTime = "marktime";
		public final static String MarkText = "marktext";
		/**
		 * 状态Status(新增，修改，删除)
		 */
		public final static String ExpColumn1 = "expcolumn1";
		/**
		 *  是否同步cloudStatus(已同步, 未同步)
		 */
		public final static String ExpColumn2 = "expcolumn2";
		/**
		 * modifyTime
		 */
		public final static String ExpColumn3 = "expcolumn3";
		
		/**
		 * 修改版本号
		 */
		public final static String ModVersion = "modversion";

		/**
		 * 扩展
		 */
		public final static String ExpColumn4 = "expcolumn4";
		public final static String ExpColumn5 = "expcolumn5";
		public final static String ExpColumn6 = "expcolumn6";
	}
	
	
	
	public final static String CreateSql = "CREATE TABLE IF NOT EXISTS " + Column.TableName  
			+ "('_id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "  
			+ Column.Pid + " VARCHAR, " 
			+ Column.IsBought +" INT DEFAULT 0," 
			+ Column.BookPath + " VARCHAR, " 
			+ Column.ChapterIndex + " INT DEFAULT 0, " 
			+ Column.ElementIndex + " INT DEFAULT 0, " 
			+ Column.ChapterName + 	" VARCHAR, " 
			+ Column.MarkTime + " INT DEFAULT 0," 
			+ Column.MarkText + " VARCHAR, " 
			+ Column.ExpColumn1 + " VARCHAR, " 
			+ Column.ExpColumn2 + " VARCHAR, " 
			+ Column.ExpColumn3 + " VARCHAR, "
			+ Column.ExpColumn4 + " VARCHAR, " 
			+ Column.ExpColumn5 + " VARCHAR, " 
			+ Column.ExpColumn6 + " VARCHAR, "
			+ Column.ModVersion + " VARCHAR);";

}