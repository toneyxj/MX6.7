package com.dangdang.reader.utils;

import com.dangdang.zframework.log.LogM;

public class InbuildBooks {

	public static final int BOOKS_LENGTH = 4;
	public static final String HELP_ID = "help";
	public static final String PUBLIC_KEY_PREFIX = "has_key";

	
	
	protected void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
}
