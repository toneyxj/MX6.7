package com.dangdang.reader.dread.media;

import java.io.File;

public class FileEntry {
	
	public File f;
	public String mimeType;
	
	private FileType type = FileType.Local;
	private String innerPath;
	private int bookType;
	
	public FileType getType() {
		return type;
	}

	public void setType(FileType type) {
		this.type = type;
	}

	public String getInnerPath() {
		return innerPath;
	}

	public void setInnerPath(String innerPath) {
		this.innerPath = innerPath;
	}

	public int getBookType() {
		return bookType;
	}

	public void setBookType(int bookType) {
		this.bookType = bookType;
	}

	public static enum FileType {
		
		FileInner, Local, Http
		
	}
	
}
