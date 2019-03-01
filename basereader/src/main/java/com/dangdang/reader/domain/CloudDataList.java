package com.dangdang.reader.domain;

import java.util.List;

import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.data.BookMark;

public class CloudDataList {

	private long versionTime;
	private List<BookMark> bookMarks;
	private List<BookNote> bookNotes;
	
	public List<BookMark> getBookMarks() {
		return bookMarks;
	}
	public void setBookMarks(List<BookMark> bookMarks) {
		this.bookMarks = bookMarks;
	}
	public List<BookNote> getBookNotes() {
		return bookNotes;
	}
	public void setBookNotes(List<BookNote> bookNotes) {
		this.bookNotes = bookNotes;
	}
	
	public long getVersionTime() {
		return versionTime;
	}
	
	public void setVersionTime(long versionTime) {
		this.versionTime = versionTime;
	}
	
	public boolean isEmpty(){
		return isMarkEmpty() && isNoteEmpty();
	}
	
	public boolean isMarkEmpty(){
		return bookMarks == null || bookMarks.size() == 0;
	}
	
	public boolean isNoteEmpty(){
		return bookNotes == null || bookNotes.size() == 0;
	}
	
	public void clearList(){
		if(bookMarks != null){
			bookMarks.clear();
		}
		if(bookNotes != null){
			bookNotes.clear();
		}
	}
	
}
