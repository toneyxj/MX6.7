package com.dangdang.reader.dread.util;

public class HeaderHolder {

	
	public UpgradeHeader upgradeHeader;
	public ErrorHeader errorHeader;
	
	
	public static class UpgradeHeader {
		
		/**
		 * 不兼容,即强制升级
		 */
		public final static int Compatible_True = 0;
		
		/**
		 * 有新升级
		 */
		public final static int New_True = 1;
		
		public int isNew = 0;
		public int isCompatible = 1;
		public String name;
		public String desc;
		public String apkUrl;
		public String destVersion;
		public long fileSize;
		
		
		/**
		 * 是否强制升级
		 * @return
		 */
		public boolean isCompatible(){
			return isCompatible == Compatible_True;
		}
		
	}
	
	public static class ErrorHeader {
		
		public int errorCode;
		public String errorMessage;
		
	}
	
}


