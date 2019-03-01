package com.dangdang.reader.cloud;

public class CloudConstant {
	
	/**
	 * 云同步错误码：
		30001: "设备同步图书进度异常"
		30002: 设备同步图书书签，笔记异常
		30003: 设备同步版本时间过低
		云同步（错误码：30001-31000）
		markInfo:书签页文字内容
		callOutInfo：笔记画线内容
		noteInfo：笔记内容
	 */
	public final static int ERRORCODE_30001 = 30001;
	public final static int ERRORCODE_30002 = 30002;
	public final static int ERRORCODE_30003 = 30003;
	
	
	public final static String JSONK_MARK_CUSTID = "custId";
	public final static String JSONK_MARK_PRODUCTID = "productId";
	public final static String JSONK_MARK_CHAPTERINDEX = "chaptersIndex";
	public final static String JSONK_MARK_ELEMENTINDEX = "characterIndex";
	/**
	 * 服务端要求时间为秒
	 */
	public final static String JSONK_MARK_OPERATETIME = "clientOperateTime";
	/**
	 * 服务端要求时间为秒
	 */
	public final static String JSONK_MARK_MODIFYTIME = "modifyTime";
	public final static String JSONK_MARK_STATUS = "status";
	public final static String JSONK_MARK_MARKINFO = "markInfo";
	public final static String JSONK_MARK_BOOKMODVERSION = "bookmodversion";
	
	
	public final static String JSONK_NOTE_CUSTID = "custId";
	public final static String JSONK_NOTE_PRODUCTID = "productId";
	public final static String JSONK_NOTE_CHAPTERINDEX = "chaptersIndex";
	public final static String JSONK_NOTE_START_ELEMENTINDEX = "characterStartIndex";
	public final static String JSONK_NOTE_END_ELEMENTINDEX = "characterEndIndex";
	public final static String JSONK_NOTE_STATUS = "status";
	public final static String JSONK_NOTE_BOOKMODVERSION = "bookmodversion";
	public final static String JSONK_NOTE_DRAWLINECOLOR = "drawLineColor";
	/**
	 * 笔记内容（用户录入的）
	 */
	public final static String JSONK_NOTE_NOTEINFO = "noteInfo";
	/**
	 * 画线内容
	 */
	public final static String JSONK_NOTE_CALLOUTINFO = "callOutInfo";
	/**
	 * 服务端要求时间为秒
	 */
	public final static String JSONK_NOTE_OPERATETIME = "clientOperateTime";
	/**
	 * 服务端要求时间为秒
	 */
	public final static String JSONK_NOTE_MODIFYTIME = "modifyTime";
	
	
}
