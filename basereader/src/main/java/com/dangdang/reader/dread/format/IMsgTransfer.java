package com.dangdang.reader.dread.format;

public interface IMsgTransfer {

	
	public void lockMsg();
	
	public void notifyMsg();
	
	public void unLockMsg();
	
}
