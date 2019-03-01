/**
 *
 */
package com.dangdang.reader.utils;

import com.dangdang.reader.DDApplication;
import com.mx.mxbase.constant.APPLog;

/**
 * API环境配置，修改此文件时，需要同步修改DangdangConfigTpl.java
 */
public class DangdangConfig {

    public static String MODULE_DEVELOP_ENVIRONMENT = "develop";
    public static String MODULE_TEST_ENVIRONMENT = "test";
    public static String MODULE_STAG_ENVIRONMENT = "staging";
    public static String MODULE_ONLINE_ENVIRONMENT = "online";

    public static String mEnvironment = MODULE_ONLINE_ENVIRONMENT;
    public static boolean mLogON = APPLog.is;

    private static final  String DEVELOP_URL="http://10.255.223.117";// 开发环境
    private static final  String TEST_URL="http://10.255.223.212";// 测试环境
    private static final  String STAG_URL="http://10.5.39.27";// staging环境
    private static final  String ONLINE_URL="http://e.dangdang.com";// 正式环境

    /**
     * !!!!!!!!!!!!!!!!推送后台使用，区分不同APP ，
     * 星空Android版 -----------------9
     * 当当读书5.0Android版本---------11
     */
//    public static int BAIDU_PUSH_APPID=9;
    /**
     * media api 接口地址
     */
    public static String SERVER_MEDIA_API_URL = DangdangConfig.getAppHost() + "/media/api.go?";
    /**
     * media api2 接口地址
     */
    public static String SERVER_MEDIA_API2_URL = DangdangConfig.getAppHost() + "/media/api2.go?";
    /**
     * mediaapi 接口地址
     */
    public static String SERVER_EAPI_URL = DangdangConfig.getAppHost()
            + "/mobile/api2.do?";
    /**
     * mobile aapi2 接口地址
     */
    public static String SERVER_MOBILE_API2_URL = DangdangConfig.getAppHost() + "/mobile/api2.do?";

    /**
     * 笔记，书签，进度的Host
     */
    public static String SERVER_MOBILE_BOOK_CLOUD_API2_URL = DangdangConfig.getAppBookCloudHost() + "/mobile/api2.do?";

    /**
     * !!!!!!!!!!!!!!!!推送后台使用，区分不同APP ，
     * 星空----------------------9
     * 当当读书5.0---------------11
     */
    public static int  getBaiduPushAppId(){
        if(ParamsType.isReader())
            return 11;
        if(ParamsType.isXingkong())
            return 9;
        return 0;

    }
    /**
     * 当前环境是否为开发，
     *
     * @return
     */
    public static boolean isDevelopEnv() {
        String environment;
        if (isDevelopForConfig())
            environment = new ConfigManager(DDApplication.getApplication())
                    .getEnvironment();
        else
            environment = mEnvironment;

        return MODULE_DEVELOP_ENVIRONMENT.equals(environment);
    }

    /**
     * 当前环境是否为线上，
     *
     * @return
     */
    public static boolean isOnLineOrStaggingEnv() {
        String environment;
        if (isDevelopForConfig())
            environment = new ConfigManager(DDApplication.getApplication())
                    .getEnvironment();
        else
            environment = mEnvironment;
        return MODULE_STAG_ENVIRONMENT.equals(environment)||MODULE_ONLINE_ENVIRONMENT.equals(environment);
    }
    /**
     * 当前环境是否为线上，
     *
     * @return
     */
    public static boolean isOnLineEnv() {
        String environment;
        if (isDevelopForConfig())
            environment = new ConfigManager(DDApplication.getApplication())
                    .getEnvironment();
        else
            environment = mEnvironment;
        return MODULE_ONLINE_ENVIRONMENT.equals(environment);
    }
    /**
     * 初始配置是否为测试环境，若为测试环境，则可以切换3种环境
     *
     * @return
     */
    public static boolean isDevelopForConfig() {
        return MODULE_DEVELOP_ENVIRONMENT.equals(mEnvironment);
    }

    public static String getAppHost() {
        String environment;
        if (isDevelopForConfig()) {
            environment = new ConfigManager(DDApplication.getApplication())
                    .getEnvironment();
        } else {
            environment = mEnvironment;
        }
        if (environment.equals(MODULE_DEVELOP_ENVIRONMENT)) {
            return DEVELOP_URL; // 开发环境
        } else if (environment.equals(MODULE_TEST_ENVIRONMENT)) {
            return TEST_URL; // 测试环境
        } else if (environment.equals(MODULE_STAG_ENVIRONMENT)) {
            return STAG_URL; // staging环境
        } else {
            return ONLINE_URL; // 正式环境
        }
    }
    
    public static String getAppH5Host(){
    	String environment;
        if (isDevelopForConfig()) {
            environment = new ConfigManager(DDApplication.getApplication())
                    .getEnvironment();
        } else {
            environment = mEnvironment;
        }
        if (environment.equals(MODULE_DEVELOP_ENVIRONMENT)||environment.equals(MODULE_TEST_ENVIRONMENT)) {
            return DEVELOP_URL; // 开发环境
        }else{
            return ONLINE_URL; // 正式环境
        }
    }

    /**
     * 笔记，书签，进度的Host
     * @return
     */
	public static String getAppBookCloudHost(){
        String environment;
        if (isDevelopForConfig()) {
            environment = new ConfigManager(DDApplication.getApplication())
                    .getEnvironment();
        } else {
            environment = mEnvironment;
        }
        if (environment.equals(MODULE_DEVELOP_ENVIRONMENT)) {
            return "http://10.255.223.117"; // 开发环境
        } else if (environment.equals(MODULE_TEST_ENVIRONMENT)) {
            return TEST_URL+":8090"; // 测试环境
        } else if (environment.equals(MODULE_STAG_ENVIRONMENT)) {
            return STAG_URL; // staging环境
        } else {
            return ONLINE_URL; // 正式环境
        }
    }

    /**
     * 网络请求相关配置
     */
    public static final int PageSize = 10;

    /**
     * 充值相关的平台类型,<p>注意单词拼写</p>
     *  //fromPaltform	String	否	ds_android(当当读书安卓平台)，ds_ios(当当读书ios平台)，yc_android(当读小说安卓平台)，yc_ios(当读小说ios平台),若参数为空则默认查询当读小说安卓平台数据
     */
    public static final String fromPaltform_client = "ds_android";

    /**
     * 支付相关配置
     */
    public static final int MODULE_SUB_ID_EBOOK_ONE_KEY_BROUGHT_PAY = 0x03;// 一键支付
    public static final String BUY_READER_TRY_READ = "readerTryRead";// 表示reader试读
    public static final String BUY_READER_BORROW = "readerBorrow";// 表示reader借阅书籍
    public static final String BUY_SHELF_STEAL = "shelfSteal";// 表示书架偷来的书籍(属于reader)
    public static final String BUY_BOOK_CITY = "bookCity";// 表示书城购买
    public static final String BUY_SHELF_BORROW = "shelfBorrow";// 书架借阅购买

    /**
     * 电子书支付方式相关配置
     */
    public static final int EBOOK_BUY_PAYMENT_KEY_ALI = 1004;                    // 电子书支付宝支付key
    public static final int EBOOK_BUY_PAYMENT_KEY_WEIXIN = 1005;                // 电子书微信支付key
    /**
     * 纸书支付方式相关配置
     */
    public static final int PAPER_BOOK_BUY_PAYMENT_KEY_ALI = 1004;                // 纸书支付宝支付key
    public static final int PAPER_BOOK_BUY_PAYMENT_KEY_WEIXIN = 1005;            // 纸书微信支付key
    /**
     * 铃铛充值方式相关配置
     */
    public static final int SMALL_BELL_RECHARGE_PAYMENT_KEY_WEIXIN = 1017;        // 铃铛充值微信支付key
    public static final int SMALL_BELL_RECHARGE_PAYMENT_KEY_ALI = 1018;            // 铃铛充值支付宝支付key
    public static final int SMALL_BELL_RECHARGE_PAYMENT_KEY_SMS = 1019;            // 铃铛充值短信支付key

    public static final int TYPE_TEN_PAY = 0x06;            // 快捷支付
    public static final int TYPE_ALIX_PAY = 0x07;            // 支付宝支付
    public static final int TYPE_NET_ALIX_PAY = 0x08;        // 支付宝wap支付
    public static final int TYPE_MINITEN_PAY = 0x09;        // 微信支付
    public static final int TYPE_VIRTUAL_COIN_PAY = 0x010;    // 虚拟币支付
    public static final int TYPE_GIFT_CARD_PAY = 0x011;        // 礼品卡支付
    public static final int TYPE_COUPON_PAY = 0x012;        // 礼券支付
    public static final String JSON_KEY_BOOK_REFER_TYPE = "referType";

    /**
     * 参数中涉及的相关类型
     */
    public static class ParamsType{
        public static String mPagekageName;
        public static void  initPackageName(String packageName ){
            mPagekageName=packageName;
        }
        public static String getDeviceType(){
            if (isReader()){
                return "Android";
            }
            if (isXingkong()){
                //TODO
                return "Android";
            }
            return "";
        }

        /**
         * 平台来源
         * @return
         */
        public static String getPlatformSource(){
            if (isReader()){
                return "DDDS-P";
            }
            if (isXingkong()){
                //TODO
                return "DDDS-P";	//"XK-P"，星皓接口不支持;
            }
            return "";
        }

        /**
         * 楼上订单，103 110
         * @return
         */
        public static String getFromPlatform(){
            if (isReader()){
                return "103";
            }
            if (isXingkong()){
                //TODO
                return "110";
            }
            return "";
        }
        /**
         * 充值购买相关接口,<p><b>区分设备</b</p>，ds_android(当当读书安卓平台)，ds_ios(当当读书ios平台)，
         * yc_android(当读小说安卓平台)，yc_ios(当读小说ios平台),若参数为空则默认查询当读小说安卓平台数据
         * @return
         */
        public static String getFromPaltform(){
            if (isReader()){
                return "ds_android";
            }
            if (isXingkong()){
                //TODO
                return "ds_android";	//"xk_android";
            }
            return "";
        }
        /**
         * 充值购买相关接口,<p><b>不区分设备</b</p>，ds 当当读书i，
         * yc当读小说 若参数为空则默认查询当读小说安卓平台数据
         * @return
         */
        public static String getFromPaltformNoDevice(){
            if (isReader()){
                return "ds";
            }
            if (isXingkong()){
                //TODO
                return "ds";
            }
            return "";
        }
        public static boolean isReader(){
            return "com.dangdang.reader".equals(mPagekageName);
        }
        public static boolean isXingkong(){
            return "com.dangdang.xingkong".equals(mPagekageName);
        }
    }

    /**
     *栏目URL
     */
    public static String BOOK_SHELF_RECOMMAND_COLUMN_HOST=DangdangConfig.getAppH5Host() +"/media/h5/ddreader50/list-page.html?type=listType&code=";

}
