package com.dangdang.reader.dread.format;

import java.io.Serializable;

public  class Chapter implements Serializable{

	
	private static final long serialVersionUID = 1L;
	protected int startPageNum;//in html
	protected int endPageNum;//in html 
	
	protected int startIndexInBook;//pageindex in book
	protected int endIndexInBook; //pageindex in book
	
	protected int indexInBook;
	
	protected String path;

	public Chapter(){
		
	}
	
	public Chapter(String path) {
		this.path = path;
	}

	public int getStartPageNum() {
		return startPageNum;
	}

	public void setStartPageNum(int startPageNum) {
		this.startPageNum = startPageNum;
	}

	public int getEndPageNum() {
		return endPageNum;
	}

	public void setEndPageNum(int endPageNum) {
		this.endPageNum = endPageNum;
	}

	public int getStartIndexInBook() {
		return startIndexInBook;
	}

	public void setStartIndexInBook(int startIndexInBook) {
		this.startIndexInBook = startIndexInBook;
	}

	public int getEndIndexInBook() {
		return endIndexInBook;
	}

	public void setEndIndexInBook(int endIndexInBook) {
		this.endIndexInBook = endIndexInBook;
	}

	public int getIndexInBook() {
		return indexInBook;
	}

	public void setIndexInBook(int indexInBook) {
		this.indexInBook = indexInBook;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public int getPageTotal(){
		return endPageNum;
	}
	
	public String getTagPath(){
		return path;
	}
	
	public void reSet(){
		startPageNum = 0;
		endPageNum = 0;
	}

}
