package com.dangdang.reader.dread.core.base;

import android.app.Activity;

import com.dangdang.execption.FileFormatException;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.IBook;

public interface IReaderApplication {

	
	public void init(Activity context) throws FileFormatException;//TODO ? 
	
	public BaseReadInfo getReadInfo();
	
	public IBook getBook();
	
	public void destroy();
	
	
	public interface IAbortParserListener {
		
		public void alreadyAbort();
		
	}
	
}
