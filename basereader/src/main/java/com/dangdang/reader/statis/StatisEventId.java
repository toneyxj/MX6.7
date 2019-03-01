package com.dangdang.reader.statis;

/**
 * @author luxu
 * 埋点事件
 */
public enum StatisEventId {

	
	/*public final static int E_STARTAPP = 20001;//启动应用
	public final static int E_PLACEORDER = 20002; //下订单事件
	public final static int E_DOWNEBOOK = 20003;// 书籍下载事件
	public final static int E_VISITPAGE = 20004;// 页面访问事件
	public final static int E_CLICK_BUYBUTTON = 20005;// 点击购买按钮
*/
	
	E_STARTAPP(20001, "startReaderApp"),
	E_VISITPAGE(20004, "visitPage"),
	E_CLICK_BUYBUTTON(20005, "clickBuyButton");
	
	private int eventId;
	private String eventName;
	
	private StatisEventId(int eId, String eName){
		eventId = eId;
		eventName = eName;
	}

	public int getEventId() {
		return eventId;
	}

	public String getEventName() {
		return eventName;
	}
	
	
}
