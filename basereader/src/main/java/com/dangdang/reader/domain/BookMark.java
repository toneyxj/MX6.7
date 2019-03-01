package com.dangdang.reader.domain;

public class BookMark {
	public int id;
	public String bookId;
	public int isBought;
	public String bookdir;
	public String chapterHref;
	public int progressInBook;
	public float progressInChapter;
	public String content;
	public long addTime;
	
	/**
	 * "CREATE TABLE IF NOT EXISTS 'bookmarks' " +
				"('_id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , " +
				"'bookid' VARCHAR, 
				'isBought' INT DEFAULT 0,
				'bookdir' VARCHAR, " +
				"'chapterHref' VARCHAR, 
				'progress_book' INT DEFAULT 0," +
				"'progress_chapter' FLOAT DEFAULT 0, 
				'content' VARCHAR," +
				"'addtime' INT DEFAULT 0);"
	 */
	
	public final static String TableName = "bookmarks";
	
	public final static String ID = "_id";
	public final static String BookId = "bookid";
	public final static String IsBought = "isBought";
	public final static String Bookdir = "bookdir";
	public final static String ChapterHref = "chapterHref";
	public final static String ProgressInBook = "progress_book";
	public final static String ProgressInChapter = "progress_chapter";
	public final static String Content = "content";
	public final static String AddTime = "addtime";
	
}
