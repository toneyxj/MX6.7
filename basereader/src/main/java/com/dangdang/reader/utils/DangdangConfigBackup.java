/**
 * 
 */
package com.dangdang.reader.utils;


/**
 * API环境配置，修改此文件时，需要同步修改DangdangConfigBackupTpl.java.
 */
public class DangdangConfigBackup {
	public static String MODULE_TEST_ENVIRONMENT = "test227";
	public static String MODULE_TEST2_ENVIRONMENT = "test131";
	public static String MODULE_TEST3_ENVIRONMENT = "test174";
	public static String MODULE_TEST4_ENVIRONMENT = "test111";
	public static String MODULE_DEVELOP_ENVIRONMENT = "development";
	public static String MODULE_ONLINE_ENVIRONMENT = "online";
	public static String MODULE_STAG_ENVIRONMENT = "staging";
	public static String MODULE_STAG2_ENVIRONMENT = "staging2";

	public static String[] ENVARRAY = { MODULE_TEST_ENVIRONMENT,
			MODULE_TEST2_ENVIRONMENT, MODULE_TEST3_ENVIRONMENT,
			MODULE_TEST4_ENVIRONMENT, MODULE_DEVELOP_ENVIRONMENT,
			MODULE_ONLINE_ENVIRONMENT, MODULE_STAG_ENVIRONMENT,
			MODULE_STAG2_ENVIRONMENT };

	// *%*(%*&%*&$&*$&%=========注意下面这行代码,千万不要修改提交！！！===========*&^*$%&^$&^#&%*&%&*^$&^%#&%&$^$
	public static String mEnvironment = MODULE_TEST4_ENVIRONMENT;

	public static String DDREADER_ONLINE_LINK = "http://e.dangdang.com/block_mobileClient_download.htm";
//	public static String DDREADER_SHARE_LINK = "http://e.dangdang.com/webReader/min/index.html?pid=";
	public static String DDREADER_SHARE_LINK="http://e.dangdang.com/media/h5/bookStore/product.html?productId=";
	
	public static String DDREADER_H5_AUTH_FAIL_LINK = "http://e.dangdang.com/m/baocuo/wrong_2.html";
	public static String DDREADER_H5_AUTH_APP_KEY = "4005";
	public static String DDREADER_H5_AUTH_MD5 = "d3VsaXVwaW5nNTE2MXpoYW5nbWluamll";

	public static int ALIX_PAYMODE = 1018;
	public static int TENCENT_PAYMODE = 1016;

	// 支付宝当当URL是否线上环境(false:线上环境；true:测试环境)
	public static boolean mIsTestEnv = isTestEnv();

	public static boolean mIsUseBaiduPush = false;

	public static boolean isTestEnv() {
		return mEnvironment.equals(MODULE_TEST_ENVIRONMENT)
				|| mEnvironment.equals(MODULE_TEST2_ENVIRONMENT)
				|| mEnvironment.equals(MODULE_TEST3_ENVIRONMENT) || mEnvironment.equals(MODULE_TEST4_ENVIRONMENT);
	}

	public static String getAppHost() {
		if (mEnvironment.equals(MODULE_TEST_ENVIRONMENT)) {
			return "http://10.255.223.227:8090"; // 测试环境
		} else if (mEnvironment.equals(MODULE_TEST2_ENVIRONMENT)) {
			return "http://10.255.223.131";// "http://192.168.132.109:8080/eapi20150424";//测试环境
		} else if (mEnvironment.equals(MODULE_TEST3_ENVIRONMENT)) {
			return "http://10.255.223.155";
		} else if (mEnvironment.equals(MODULE_TEST4_ENVIRONMENT)) {
			return "http://10.255.223.111:8081";
		} else if (mEnvironment.equals(MODULE_DEVELOP_ENVIRONMENT)) {
			return "http://10.255.223.195:8080"; // 开发环境
		} else if (mEnvironment.equals(MODULE_STAG_ENVIRONMENT)) {
			return "http://172.16.248.28"; // staging环境
		} else if (mEnvironment.equals(MODULE_STAG2_ENVIRONMENT)) {
			return "http://172.16.249.31"; // staging2环境
		} else {
			return "http://e.dangdang.com"; // 正式环境
		}
	}

	// 充值用
	public static String getAppRechargeHost() {
		if (mEnvironment.equals(MODULE_TEST_ENVIRONMENT)) {
			return "http://10.255.223.149"; // 测试环境
		} else if (mEnvironment.equals(MODULE_TEST2_ENVIRONMENT)) {
			return "http://10.255.223.149";// //测试环境
		} else if (mEnvironment.equals(MODULE_TEST3_ENVIRONMENT)) {
			return "http://10.255.223.149";
		} else if (mEnvironment.equals(MODULE_STAG_ENVIRONMENT)) {
			return "http://172.16.248.121:8082"; // staging环境
		} else if (mEnvironment.equals(MODULE_STAG2_ENVIRONMENT)) {
			return "http://172.16.248.121:8082"; // staging2环境
		} else {
			return "http://e.dangdang.com"; // 正式环境
		}
	}
	
	/**
	 * 获取翻篇儿应用的域名
	 * @return
	 */
	public static String getLightReaderHost(){
		if(isTestEnv()){
			return "http://10.255.223.153:8989";
		} else{
			return "http://e.dangdang.com";
		}
	}

	public static String getBookStoreRecommand() {
		if (mIsTestEnv) {
			return "http://10.255.223.131/daily/client40/android/recommend.html"
					+ getRemainders();
		}
		return "http://e.dangdang.com/block_android38_homePage.htm"
				+ getRemainders();
	}

	public static String getH5AuthHost() {
		if (mIsTestEnv) {
			return "http://wxh5test.dangdang.com/h5login/auth.php";
		}
		return "http://m.dangdang.com/h5login/auth.php";
	}

	public static String getH5ProductHost() {
		if (mIsTestEnv) {
			return "http://wxh5test.dangdang.com/h5product/product.php";
		}
		return "http://m.dangdang.com/h5product/product.php";
	}

	public static String getRecommandCodeUrl() {
		if (mIsTestEnv) {
			return "http://10.255.223.131/block_androidClient_recommend.htm?";
		}
		return "http://e.dangdang.com/block_androidClient_recommend.htm?";
	}

	public static String getBookStoreFree() {
		if (mIsTestEnv) {
			return "http://10.255.223.131/daily/client40/android/specialoffer.html"
					+ getRemainders();
		}

		return "http://e.dangdang.com/block_android38_freeColumn.htm"
				+ getRemainders();
	}

	public static String getBookStoreRank() {
		if (mIsTestEnv) {
			return "http://10.255.223.131/daily/client40/android/ranklist.html";
		}

		return "http://e.dangdang.com/block_android38_ranking.htm";
	}

	public static String getHotSearchWords() {
		if (mIsTestEnv) {
			return "http://10.255.223.131/daily/client40/android/hotWords.html"
					+ getRemainders();
		}
		return "http://e.dangdang.com/android_hotWords/index.htm"
				+ getRemainders();
	}

	public static String getCategoryUrl() {
		if (mIsTestEnv) {
			return "http://10.255.223.131/daily/client40/android/classification.html"
					+ getRemainders();
		}
		return "http://e.dangdang.com/android_classification/index.htm"
				+ getRemainders();
	}

	// 获取指定区间的随机数
	public static String getRemainders() {
		return "?refresh=1";//DROSUtility.getRandomInt(1, 10000);
	}

	public static String getEBookHomeAd() {
		if (mIsTestEnv) {
			return "623";
		}
		return "1441";
	}

	public static String getEBookRecommend() {
		if (mIsTestEnv) {
			return "624";
		}
		return "1442";
	}

	public static String getQmonetDangdangMebcode() {
		if (mIsTestEnv) {
			return "10011548156";
		}
		return "10021604640";
	}

	/*
	 * public static String getUpdateVesion(){ if(mIsTestEnv){ //升级接口测试环境 return
	 * "http://10.255.223.134:8083/emobile/mobile/api2.do"; } return
	 * "http://e.dangdang.com/emobile/mobile/api2.do"; }
	 */

	public static String getRecommondCode() {
		return "ad40_teVeryCheap";
	}

	public static String getFreeCode() {
		return "ad40_teLimitedFree";
	}

	public static String getBorrowCode() {
		return "android_borrowRead";
	}

	public static String getActivityCode() {
		return "android_getactivity";
	}

	public static String getPersonalActivityCode() {
		return "android_personactivity";
	}

	public static String getChargeFontCode() {
		if (mIsTestEnv) {
			return "freefont";
		}
		return "AndroidV4_font";
	}

	public static String getFreeFontCode() {
		if (mIsTestEnv) {
			return "freefont";
		}
		return "AndroidV4_freefont";
	}

	public static String getChargeFontClumnId() {
		if (mIsTestEnv) {
			return "638";
		}
		return "1482";
	}

	public static String getFreeBookCode() {
		if (mIsTestEnv) {
			return "getfreeEbook3";// "getFreeEbook";
		}
		return "Andriod_limited_ebookfree";
	}

	public static String getEbookAttentionCode() {
		if (mIsTestEnv) {
			return "unique_sales";
		}
		return "unique_sales";
	}

	public static String getEbookSellCode() {
		if (mIsTestEnv) {
			return "saleRank";
		}
		return "saleRank";
	}

	public static String getEbookFreeTopCode() {
		if (mIsTestEnv) {
			return "Free_list";
		}
		return "Android_Free_list";
	}

	public static String getEbookNewBookTopCode() {
		if (mIsTestEnv) {
			return "newbook_list";
		}
		return "newbook_list";
	}

	public static String getFreeBookClumnId() {
		if (mIsTestEnv) {
			return "1544";
		}
		return "684";
	}

	public static String getDictBlockCode() {
		if (mIsTestEnv) {
			return "readerdict";
		}
		return "readerdict";
	}

	public static final int PageSize = 10;
	public static final int FifthPageSize=50;
	
	/**
	 * 过滤网页参数值
	 */
	public static final String TEXTFILED_TYPE = "txt";

	public static final String DEVICE_TYPE = "Android";
	public static final String HTTP_PARAM_SOFTWAE_VERSION = "&versionNo=";
	public static final String HTTP_PARAM_DEVICE_TYPE = "&deviceType=";
	public static final String HTTP_PARAM_DEVICE_ID = "&deviceNo=";
	public static final String HTTP_PARAM_CHANNEL_CODE = "&channelCode=";
	public static final String UPDATEVERSION_TYPE_SOFTWARE = "1";
	public static final String UPDATEVERSION_DEVICETYPE_ANDROID = "Android";
	// 1、兼容，提示升级; 0、不兼容，强制升级
	public static final int UPDATEVERSION_ISNOTFORCE_UPDATE = 1;
	public static final int UPDATEVERION_ISFORCE_UPDATE = 0;

	public static String SERVER_API_URL = DangdangConfigBackup.getAppHost()
			+ "/media/api2.go?";
	public static String SERVER_API_URL2 = DangdangConfigBackup.getAppHost()
			+ "/mobile/api2.do?";// 去掉问号，用户解绑和注销用
	public static String SERVER_API_URL3 = DangdangConfigBackup.getAppHost()
			+ "/media/api.go?";// 百度推送接口
	public static String SERVER_API_URL4 = DangdangConfigBackup.getAppRechargeHost()
			+ "/media/api.go?";// 充值接口
	public static String SERVER_API_URL5 = DangdangConfigBackup.getAppHost()
			+ "/media/api2.go?";
	public static String SERVER_API_URL6 = DangdangConfigBackup.getLightReaderHost()
			+ "/media/api2.go?";// 翻片儿应用接口

	public static final String VERSION_UPDATE_URL = ""; // 版本更新链接地址

	public static final String JSON_KEY_BOOK_OBJECT = "book_object";
	public static final String JSON_KEY_PBOOK_HONOR = "score";

	public static final String JSON_KEY_BOOK_HONOR = "honor";
	public static final String JSON_KEY_BOOK_NAME = "bookName";
	public static final String JSON_KEY_BOOK_PRODUCTID = "productId";
	public static final String JSON_KEY_BOOK_COVER_URL = "cover";
	public static final String JSON_KEY_BOOK_REFER_TYPE = "referType";
	public static final String JSON_KEY_BOOK_REFER_PARAMS = "referParams";
	public static final String JSON_KEY_BOOK_AUTHOR = "author";
	public static final String JSON_KEY_BOOK_CATEGORY_TYPE = "mainCategory";
	public static final String JSON_KEY_BOOK_PRICE = "price";
	public static final String JSON_KEY_BOOK_SALE_PRICE = "salePrice";
	public static final String JSON_KEY_PAPER_BOOK_PRICE = "paperBookPrice";
	public static final String JSON_KEY_BOOK_PUBLISHER = "publisher";
	public static final String JSON_KEY_BOOK_PUBLISHDATE = "publishDate";
	public static final String JSON_KEY_BOOK_DESC = "desc";
	public static final String JSON_KEY_BOOK_ISBUY = "isBuy";
	public static final String JSON_KEY_BOOK_TYPE_KEY = "permissionAndFileTypeKey";
	public static final String JSON_KEY_BOOK_TYPE_MESSAGE = "permissionAndFileTypeMsg";
	public static final String JSON_KEY_BOOK_CURRENT_DATE = "currentDate";
	public static final String JSON_KEY_BOOK_FREEEPUBSIZE = "freeEpubSize";
	public static final String JSON_KEY_BOOK_FULLEPUBSIZE = "fullEpubSize";
	public static final String JSON_KEY_BOOK_PAPER_PRICE = "paperBookPrice";
	public static final String JSON_KEY_BOOK_ISBN = "isbn";
	public static final String JSON_KEY_BOOK_FREEBOOK = "freeBook";
	public static final String JSON_KEY_BOOK_BORROWBOOK = "isBorrow";
	// 促销
	public static final String JSON_KEY_BOOK_FIRST_MODEL = "firstPromoModel";
	public static final String JSON_KEY_BOOK_END_DATE = "end_date";
	public static final String JSON_KEY_BOOK_PROMO_PIC = "promoPicUrl";

	public static final String JSON_KEY_COMMENT_DESC = "commentDesc";
	public static final String JSON_KEY_COMMENT_TITLE = "commentTitle";
	public static final String JSON_KEY_COMMENT_STARS = "commentStars";
	public static final String JSON_KEY_COMMENT_TIME = "commentTime";
	public static final String JSON_KEY_COMMENT_AUTHOR = "commentAuthor";

	public static final String JSON_KEY_BOOK_VERSION_DATA = "data";
	public static final String JSON_KEY_BOOK_VERSION_NAME = "name";
	public static final String JSON_KEY_BOOK_VERSION_NUM = "version";
	public static final String JSON_KEY_BOOK_VERSION_URL = "url";
	public static final String JSON_KEY_BOOK_VERSION_DESC = "desc";

	public static final String DEFAULT_HONOR = "4"; // 默认星级
	// 购物车
	public static final String JSON_KEY_BOOK_CRATID = "cartId";

	/**
	 * 由应用进入订单页
	 */
	public static final String INTENT_KEY_DDREADER_INDENT = "to_indent";
	/**
	 * 应用内为：self_to_indent;如果无值，则认为是快钱返回页面;giftcard_to_indent:礼品卡支付返回订单页;
	 * 支付宝返回订单页
	 */
	public static final String INTENT_KEY_DDREADER_INDENT_VALUE = "self_to_indent";
	public static final String GIFTCARD_TO_INDENT = "giftcard_to_indent";
	public static final String ZHIFUBAO_TO_INDENT = "zhifubao_to_indent";
	public static final String TENPAY_TO_INDENT = "tenpay_to_indent";
	public static final String WXPAY_TO_INDENT = "wxpay_to_indent";
	/**
	 * 订单来源
	 */
	public static final String FROM_URL = "103";
	/**
	 * 新书权限开通定时间隔
	 */
	public static final int DEFAULT_NEWBOOK_OPEN_ACESS_TIME = 1;

	public static final String BOOKNAME_SIGN = "(电子书)";

	/**
	 * 引导使用存储key
	 */
	public static final String PREFERENCE_KEY_FIRST_USEGUIDE = "first_useguide";
	public static final String BOOK_GUIDE_PREFERENCE = "p_book_guide";

	/**
	 * 礼品卡相关定义
	 */
	// 由个人页点击进入礼品卡
	public static final int PERSON_TO_GIFTCARD = 0;
	// 由订单页进入礼品卡
	public static final int INDENT_TO_GIFTCARD = 1;
	public static final int GIFTCARD_ACTIVITYID = 1;
	// 由订单页进入礼品券
	public static final int INDENT_TO_GIFTCOUPON = 2;

	// 支付标志(快钱)
	public static final int PAYMENT_QMONEY = 0;
	public static final int PAYMENT_GIFTCARD = 1;
	public static final int PAYMENT_ZHIFUBAO = 2;

	// 微博相关
	public static final String SINA_WEIBO = "sina_weibo";
	public static final String TENCENT_WEIBO = "tencent_weibo";
	public static final String SWITCH_STATE = "switch_state";
	public static final String WEIBO_TOKEN = "weibo_token";
	public static final String WEIBO_TOKEN_EXPIRES = "weibo_token_expires";
	public static final String WEIBO_ID = "weibo_id";
	public static final String WEIBO_NICK_NAME = "weibo_nick_name";
	// 新浪微博 accesstoken，accesssecret 和 回调url
	public static final String SINA_CONSUMER_KEY = "1288497763";// 开发者的appkey
	public static final String SINA_CONSUMER_SECRET = "47bece26261d76cdc62efba082bdbf1f";// 开发者的secret
	public static final String SINA_REDIRECT_URL = "http://e.dangdang.com/auth";

	// 腾讯微博 accesstoken,accesssecret 和 回调 url
	public static final String TENCENT_APP_ID = "100273905";
	public static final String TENCENT_REDIRECT_URL = "http://e.dangdang.com";
	public static final String TENCENT_CONSUMER_KEY = "801227893";
	public static String TENCENT_CONSUMER_SECRET = "d918915182768fbad8b5dc873065cee5";

	public static final String FONT_CARTID = "font_cartId";
	public static final String ONE_KEY_CARTID = "one_key_cartId";
	public static final String FROM_MODULE = "from_submodle";
	public static final String BROUGHT_BOOK_INFO = "brought_book_info";
	public static final String VIRTUAL_PRODUCT_ID = "virtual_product_id";
	public static final String IS_PAY_ALL = "is_pay_all";
	public static final String ONLY_DEPOSIT = "only_deposit";
	public static final String PAY_ORDERID = "pay_ordeId";
	public static final String PAY_PRICE = "pay_Price";
	public static final String PAY_TYPE = "pay_type";
	public static final String BIND_PERMISSION_V2_KEY = "key";
	public static final String VIRTUAL_COIN_KEY = "virtual_coin_key";
	public static final String SYSTEM_DATE = "system_date";

	public static final int MODULE_SUB_ID_EBOOK_ONE_KEY_BROUGHT_PAY = 0x03;// 一键支付
	public static final int MODULE_SUB_ID_EBOOK_FONT_BROUGHT_PAY = 0x04;// 字体一键支付
	public static final int MODULE_SUB_ID_SHELF_EBOOK_BROUGHT_PAY = 0x05;// 书架图书一键支付

	public static final String BUY_READER_TRY_READ = "readerTryRead";// 表示reader试读
	public static final String BUY_READER_BORROW = "readerBorrow";// 表示reader借阅书籍
	public static final String BUY_SHELF_STEAL = "shelfSteal";// 表示书架偷来的书籍(属于reader)
	public static final String BUY_BOOK_CITY = "bookCity";// 表示书城购买
	public static final String BUY_SHELF_BORROW = "shelfBorrow";// 书架借阅购买

	public static final int TYPE_TEN_PAY = 0x06;// 快捷支付
	public static final int TYPE_ALIX_PAY = 0x07;// 支付宝支付
	public static final int TYPE_NET_ALIX_PAY = 0x08;// 支付宝wap支付
	public static final int TYPE_MINITEN_PAY = 0x09;// 微信支付
	public static final int TYPE_VIRTUAL_COIN_PAY = 0x010;// 虚拟币支付

	public static final String SALE_PLAT = "110001";
	public static final String BOOK_DETAIL_FROM_MODLE = "detailFromModle";// 从哪里进入书城
	// 从哪里进入书城
	public static final String BOOK_DETAIL_FROM_EXTERNAL = "external";// 书城外部
	public static final String BOOK_DETAIL_FROM_INSIDE = "inside";// 书城内部
}
