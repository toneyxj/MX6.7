package com.dangdang.reader.dread.format.part;

/**
 * Created by liuboyu on 2014/12/3.
 */
public class StatusCode {

	public static final int SUCCESS = 0;
	public static final int DOWNLOAD_ERROR = 1;
	public static final int NETWORK_EXCEPTION = 10001;
	public static final int ILEGAL_PARAMS = 10002;
	public static final int NEED_LOGIN = 10003;
	/**购买*/
	public static final int PERMISSION_DENINED = 10004;
	/**强制下载*/
	public static final int MEDIA_FORCE_UNSHELVE = 10009;
	/**
	 * 木有章节
	 */
	public static final int NO_BOOK_OR_CHAPTER = 12002;
	public static final int ENCRYPT_ERROR = 12003;

	public static final int MONEY_NOT_ENOUGH = 18001;
}
