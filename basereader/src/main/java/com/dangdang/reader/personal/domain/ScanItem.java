package com.dangdang.reader.personal.domain;

import java.io.File;

public class ScanItem {
	
	public File file;
	public boolean select;
	
	public ScanItem(File file, boolean bo){
		this.file = file;
		select = bo;
	}
}
