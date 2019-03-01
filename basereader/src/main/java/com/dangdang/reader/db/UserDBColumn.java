package com.dangdang.reader.db;


public class UserDBColumn {
	
	public final static String TABLE_NAME = "user";
	public final static String ID = "_id";
	public final static String USER_ID = "user_id";
	public final static String USER_NAME = "username";
	public final static String NICK_NAME = "nickname";
	public final static String NICK_NAME_ALL = "nickname_all";
	public final static String THIRD = "third";
	public final static String TOKEN = "token";	
	public final static String SEX = "sex";
	public final static String HEADIMG = "head_img";
	public final static String LAST_TIME = "last_time";
	public final static String LOGIN_TYPE = "login_type";
	
	public final static String EMAIL = "email";
	public final static String PHONE = "phone";
	public final static String REGIST = "regist";
	public final static String VIP = "vip";
	public final static String CREATEBAR = "create_bar";
	public final static String REWARDHEAD = "reward_head";
	public final static String REWARDINTRODUCT = "reward_introduct";
	public final static String REWARDNICKNAME = "reward_nick_name";
	public final static String GOLD = "gold";
	public final static String SILVER = "silver";
	public final static String FRIEND = "friend";
	public final static String INFO = "info";
	public final static String LEVEL = "level";

	public final static String ExpColumn1 = "exp_column1";			// 5.2.0版本用于保存channelOwner字段
	public final static String ExpColumn2 = "exp_column2";			// json, 保存barOwnerLevel
	public final static String ExpColumn3 = "exp_column3";			//荣誉头衔 2016-05-04 wangzhiwei
			
	public static String createUserTable(){
		String sql = "create table IF NOT EXISTS " + 
				UserDBColumn.TABLE_NAME + " (" + 
				UserDBColumn.ID + " integer primary key autoincrement, " +
				UserDBColumn.USER_ID + "  varchar unique not null, " +
				UserDBColumn.USER_NAME + "  varchar, " +
				UserDBColumn.NICK_NAME + "  varchar, " +
				UserDBColumn.NICK_NAME_ALL + "  varchar, " +
				UserDBColumn.THIRD + "  varchar, " +
				UserDBColumn.LAST_TIME + " long default 0, " + 
				UserDBColumn.TOKEN + " varchar, " + 
				UserDBColumn.HEADIMG + " varchar, " + 
				UserDBColumn.SEX + " int default -1, " + 
				UserDBColumn.LOGIN_TYPE + " int default 0, " + 
				
				UserDBColumn.EMAIL + " varchar, " +
				UserDBColumn.PHONE + " varchar, " +
				UserDBColumn.REGIST + " long default 0, " +
				UserDBColumn.VIP + " int default 0, " +
				UserDBColumn.CREATEBAR + " int default 0, " +
				UserDBColumn.REWARDHEAD + " int default 0, " +
				UserDBColumn.REWARDINTRODUCT + " int default 0, " +
				UserDBColumn.REWARDNICKNAME + " int default 0, " +
				UserDBColumn.GOLD + " long default 0, " +
				UserDBColumn.SILVER + " long default 0, " +
				UserDBColumn.FRIEND + " long default 0, " +
				UserDBColumn.INFO + " varchar, " +
				UserDBColumn.LEVEL + " int default 0, " +
				UserDBColumn.ExpColumn1 + " varchar, " +
				UserDBColumn.ExpColumn2 + " varchar, " + 
				UserDBColumn.ExpColumn3 + " varchar);";
		return sql;
	}
}
