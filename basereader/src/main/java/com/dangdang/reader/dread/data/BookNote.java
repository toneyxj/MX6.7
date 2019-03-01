package com.dangdang.reader.dread.data;

import java.io.Serializable;

public class BookNote implements Cloneable, Serializable {

	public static final int NOTE_DRAWLINE_COLOR_RED = 0;
	public static final int NOTE_DRAWLINE_COLOR_YELLOW = 1;
	public static final int NOTE_DRAWLINE_COLOR_GREEN = 2;
	public static final int NOTE_DRAWLINE_COLOR_BLUE = 3;
	public static final int NOTE_DRAWLINE_COLOR_PINK = 4;

	public int id;
	public String bookId;
	public String bookPath;

	public String chapterName;
	//public String chapterPath;//html path
	public int chapterIndex;

	public int noteStart; // by chapter or by html
	public int noteEnd; // by chapter or by html

	/**
	 * clientOperateTime
	 * 单位：秒
	 */
	public long noteTime;
	public String noteText;
	public String sourceText;
	
	public int isBought;
	
	/**
	 * 扩展字段
	 */
	/**
	 * 状态Status(1新增，2修改，3删除)
	 * 对老数据兼容处理：默认为新增
	 */
	public String status;//
	/**
	 * 是否同步
	 * 对老数据兼容处理：默认为未同步
	 */
	public String cloudStatus;//
	/**
	 * modifyTime
	 * 单位秒
	 */
	public String modifyTime;
	
	
	/**
	 * bookModVersion
	 * 书籍修改版本号
	 */
	public String bookModVersion;

	public int drawLineColor = NOTE_DRAWLINE_COLOR_RED;
	
	//-------非数据库对应字段-------------
	
	public boolean isChapterHead;
	
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

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getBookPath() {
		return bookPath;
	}

	public void setBookPath(String bookPath) {
		this.bookPath = bookPath;
	}

	public String getChapterName() {
		return chapterName;
	}

	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}

	public int getChapterIndex() {
		return chapterIndex;
	}

	public void setChapterIndex(int chapterIndex) {
		this.chapterIndex = chapterIndex;
	}

	public int getNoteStart() {
		return noteStart;
	}

	public void setNoteStart(int noteStart) {
		this.noteStart = noteStart;
	}

	public int getNoteEnd() {
		return noteEnd;
	}

	public void setNoteEnd(int noteEnd) {
		this.noteEnd = noteEnd;
	}

	/**
	 * clientOperateTime
	 */
	public long getNoteTime() {
		return noteTime;
	}

	/**
	 * clientOperateTime
	 */
	public void setNoteTime(long noteTime) {
		this.noteTime = noteTime;
	}

	public String getNoteText() {
		return noteText;
	}

	public void setNoteText(String noteText) {
		this.noteText = noteText;
	}

	public String getSourceText() {
		return sourceText;
	}

	public void setSourceText(String sourceText) {
		this.sourceText = sourceText;
	}

	public int getIsBought() {
		return isBought;
	}

	public void setIsBought(int isBought) {
		this.isBought = isBought;
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

	public int getDrawLineColor() {
		return drawLineColor;
	}

	public void setDrawLineColor(int drawLineColor) {
		this.drawLineColor = drawLineColor;
	}

	public boolean isChapterHead() {
		return isChapterHead;
	}

	public void setChapterHead(boolean isChapterHead) {
		this.isChapterHead = isChapterHead;
	}




	public final static String CreateNoteSql = "CREATE TABLE IF NOT EXISTS " 
			+ NoteColumn.TableName + "(" 
			+ NoteColumn.Id + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "  
			+ NoteColumn.BookId + " VARCHAR, " 
			+ NoteColumn.BookPath + " VARCHAR, " 
			+ NoteColumn.ChapterName + 	" VARCHAR, " 
			+ NoteColumn.ChapterIndex + " INT DEFAULT 0,"
			+ NoteColumn.SourceText + " VARCHAR, "
			+ NoteColumn.NoteStart + " INT DEFAULT 0, " 
			+ NoteColumn.NoteEnd + " INT DEFAULT 0, " 
			+ NoteColumn.NoteText + " VARCHAR, " 
			+ NoteColumn.NoteTime + " LONG DEFAULT 0," 
			+ NoteColumn.IsBought +" INT DEFAULT 0," 
			+ NoteColumn.ExpColumn1 + " VARCHAR, " 
			+ NoteColumn.ExpColumn2 + " VARCHAR, " 
			+ NoteColumn.ExpColumn3 + " VARCHAR, "
			+ NoteColumn.ExpColumn4 + " VARCHAR, " 
			+ NoteColumn.ExpColumn5 + " VARCHAR, " 
			+ NoteColumn.ExpColumn6 + " VARCHAR, "
			+ NoteColumn.ModVersion + " VARCHAR);";
	
	public class NoteColumn{
		public final static String TableName = "notetable";
		
		public final static String Id = "_id";
		public final static String BookId = "bookid";
		public final static String BookPath = "bookpath";
		
		public final static String ChapterName = "chaptername";
		public final static String ChapterIndex = "chapterindex";
		
		public final static String SourceText = "sourcetext";
		public final static String NoteStart = "startindex";
		public final static String NoteEnd = "endindex";
		public final static String NoteText = "notetext";
		public final static String NoteTime = "notetime";
		
		public final static String IsBought = "isbought";
		
		/**
		 * 状态Status(1新增，2修改，3删除)
		 */
		public final static String ExpColumn1 = "expcolumn1";
		/**
		 * 是否同步
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
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		BookNote note = (BookNote)o;
		return (id == note.id) 
				&& (chapterIndex == note.chapterIndex) 
				&& (noteStart == note.noteStart) 
				&& (noteEnd == note.noteEnd);
	}
	
	@Override
	public int hashCode() {
		return id * 3 + chapterIndex * 5 + noteStart * 7 + noteEnd * 11;
	}

}
