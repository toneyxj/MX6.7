package com.moxi.bookstore.request.json;

/**
 * 接口文件，保存所有的接口
 * 
 * @author 夏君
 * 
 */
public class Connector {
	// 初始化类实列
	private static Connector instatnce = null;

	/**
	 * 获得软键盘弹出类实列
	 * 
	 * @return 返回初始化实列
	 */
	public static Connector getInstance() {
		if (instatnce == null) {
			synchronized (Connector.class) {
				if (instatnce == null) {
					instatnce = new Connector();
				}
			}
		}
		return instatnce;
	}

	/**
	 * url根路径
	 */
	public final String url="http://e.dangdang.com/media/api2.go";
	/**
	 *租约书库分类接口code
	 */
	public final String mediaCategory="mediaCategory";
	/**
	 *租阅书城接口
	 */
	public final String mediaCategoryLeaf="mediaCategoryLeaf";
	/**
	 *购买VIP获取vip产品列表
	 */
	public final String channel="channel";
	/**
	 *获取个人包月vip信息
	 */
	public final String getMonthlyChannelList="getMonthlyChannelList";
	/**
	 *获取个人包月vip信息
	 */
	public final String getMonthlyChannelListNotify="getMonthlyChannelListNotify";
	/**
	 *购买租阅权限
	 */
	public final String buyMonthlyAuthority="buyMonthlyAuthority";
	/**
	 *个人购买书籍总数
	 */
	public final String myProperty="myProperty";
	/**
	 * 租阅信息展示
	 */
	public final String block="block";
	//http://e.dangdang.com/media/api2.go?action=block&code=getVipPrivilege
}
