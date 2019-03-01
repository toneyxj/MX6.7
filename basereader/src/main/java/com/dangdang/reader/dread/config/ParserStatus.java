package com.dangdang.reader.dread.config;

public class ParserStatus {
	
	public final static int UNZIP_ERROR = -101;
	public final static int FILE_ERROR = -111;
	public final static int EPUB_ERROR = -112;
	public final static int TRAINING_OVER = - 113;
	public final static int SUCCESS = 100;
	
	/**  
	 * -1 代码成功 -2 html解析失败 -3 解密失败 -4文件不存在 -5 非法文件(文件类型不对..)
	 * C = Composing
	 */
	public final static int C_SUCCESS = -1;
	public final static int C_HTML_ERROR = -2;
	public final static int C_DECTYPT_ERROR = -3;
	public final static int C_FILENOEXIST_ERROR = -4;
	public final static int C_INVALID_FILE = -5;
	public final static int C_INIT_FAILED = -6;
	
	public static boolean isComposingSuccess(int status){
		return status == C_SUCCESS || status == C_INVALID_FILE;
	}
	
	public static boolean isSuccess(int status){
		return status == C_SUCCESS;
	}
	
}