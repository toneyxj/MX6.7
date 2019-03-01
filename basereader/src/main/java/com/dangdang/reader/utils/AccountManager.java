package com.dangdang.reader.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.dangdang.zframework.utils.StringUtil;


public class AccountManager {

    private final String KEY_USERNAME = "username";
    private final String KEY_THREENAME = "threesname";
    private final String KEY_PHONE = "phone";
    private final String KEY_USEID = "userid";
    private final String KEY_DISPLAY_ID = "display_id";
    private final String KEY_TOKEN = "token";
    private final String KEY_TOKEN_BIRTHDAY = "token_birthday";
    private final long TOKEN_VALID_PERIOD = 24 * 60 * 60 * 1000;
    private final String KEY_LOGINED_FLAG = "logined";
    private final String KEY_ISTHIRD = "is_third";
    private final String KEY_HEAD_PORTRAIT = "head_portrait";
    private final static String KEY_CART_ID = "cart_id";
    private final static String KEY_NIGHT_SHOW_CHOOSE_TYPE = "night_show_choose_type";
    private final static String KEY_NIGHT_SHOW_FIRST_TIME = "KEY_NIGHT_SHOW_FIRST_TIME";
    private final static String KEY_NIGHT_ENTER_SHOW_OTHER_MODULE = "KEY_NIGHT_ENTER_SHOW_OTHER_MODULE";
    private final static String KEY_BAIDU_PUSH_BIND_SUCCESS_STATE = "KEY_BAIDU_PUSH_BIND_SUCCESS_STATE";
    private final static String KEY_SHOPPING_CART_REFRESH = "shopping_cart_refresh";
    private final static String KEY_CURRENT_ACTIVITY_ID = "current_activity_id";
    private final static String KEY_CART_LIST_COUNT = "cart_list_count";
    private final static String KEY_FAVOR_PRODUCT_ID = "KEY_FAVOR_PRODUCT_ID";
    private final static String KEY_PAY_METHOD = "KEY_PAY_METHOD_INT";
    private final static String KEY_HISTORY_BEAT_OTHERS = "KEY_HISTORY_BEAT_OTHERS";
    private final static String KEY_BAIDU_PUSH_SERVICE_STOP = "key_baidu_push_service_stop";
    private final static String KEY_GUIDE_IMAGE_END_TIME = "key_guide_image_end_time";
    private final static String KEY_CURRENT_BAIDU_PUSH_MESSAGE = "key_current_baidu_push_message";
    private final static String KEY_VIRTUAL_GOLD_SHOW = "key_virtual_gold_show";
    private final static String KEY_VIRTUAL_SILVER_SHOW = "key_virtual_silver_show";
    private final static String KEY_SHOW_MARKET_COMMENT = "key_show_market_comment";
    private final static String KEY_VERSION_NAME = "key_version_name";
    private final static String KEY_APPLICATION_BOOT_NUM = "key_application_boot_num";
    private final static String KEY_LOADING_IMAGE_INFO ="key_loading_image_info";
    private Context mContext;
    private ConfigManager mConfig;
    public static final String QQ_THIRD_ID = "6";
    public static final String NETEASE_THIRD_ID = "9";
    public static final String ALIXWAP_THIRD_ID = "7";
    public static final String SINA_THIRD_ID = "5";

    public static final String BAIDU_PUSH_APP_KEY = "aFGxyEhvCRHEX5pCjwuL1F4g";
    public static final String CHANNELMSG_SWITCH = "channelmsg_switch";
    public static final String USR_DIR = "usr_dir";

    /**
     * @param context
     */
    public AccountManager(Context context) {
        this(context, new ConfigManager(context.getApplicationContext()));
    }

    /**
     * @param context
     * @param config
     */
    public AccountManager(Context context, ConfigManager config) {
        super();
        this.mContext = context.getApplicationContext();
        this.mConfig = config;
    }

    public boolean checkTokenValid() {
        boolean isLogin = mConfig.getPreferences().getBoolean(KEY_LOGINED_FLAG,
                false);
        if (isLogin) {
            return (!TextUtils.isEmpty(getToken()));
        } else {
            return false;
        }
    }

    public void markLogout() {
        SharedPreferences.Editor editor = mConfig.getEditor();
        editor.remove(KEY_LOGINED_FLAG);
        editor.commit();

        sendLogoutBraodcast();
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return mConfig.getPreferences().getString(KEY_USERNAME, null);
    }

    public String getDownloadUsername(){
    	return DangdangFileManager.USER_UNDEFINE;
    	
//    	String tmp = getUsername();
//    	if(tmp == null)
//    		return DangdangFileManager.USER_UNDEFINE;
//    	if(DownloadBookHandle.isValidDownloadUsername(tmp))
//    		return tmp;
//    	else{
//    		tmp = getLoginID();
//    		if(TextUtils.isEmpty(tmp))
//    			return DangdangFileManager.USER_UNDEFINE;
//    		else{
//    			int RADIX = 10 + 26; // 10 digits + 26 letters
//    			byte[] md5 = getMD5(tmp.getBytes());
//    			BigInteger bi = new BigInteger(md5).abs();
//    			return bi.toString(RADIX);
//    		}
//    	}
    }
    
//	private byte[] getMD5(byte[] data) {
//		byte[] hash = null;
//		try {
//			MessageDigest digest = MessageDigest.getInstance("MD5");
//			digest.update(data);
//			hash = digest.digest();
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
//		return hash;
//	}
//	
    public void setThreeUsername(String threename) {
        SharedPreferences.Editor editor = mConfig.getEditor();
        editor.putString(KEY_THREENAME, threename);
        editor.commit();
    }

    public String getThreeUsername() {
        return mConfig.getPreferences().getString(KEY_THREENAME, null);
    }

    public String getUserId() {
        return mConfig.getPreferences().getString(KEY_USEID, null);
    }

    public void setDisplayId(String displayId) {
        SharedPreferences.Editor editor = mConfig.getEditor();
        editor.putString(KEY_DISPLAY_ID, displayId);
        editor.commit();
    }

    public String getDisplayId() {
        return mConfig.getPreferences().getString(KEY_DISPLAY_ID, "");
    }

    /**
     * @return the isThirdLogin
     */
    public boolean getIsThirdLogin() {
        return mConfig.getPreferences().getBoolean(KEY_ISTHIRD, false);
    }

    /**
     * @return the token
     */
    public String getToken() {
        String token = mConfig.getPreferences().getString(KEY_TOKEN, "");
        return token;
    }

    /**
     * 为校验token是否过期，保存token生成时间。目前服务端设置为1天过期
     *
     * @return the tokenBirthday
     */
    public long getTokenBirthday() {
        return mConfig.getPreferences().getLong(KEY_TOKEN_BIRTHDAY, -1);
    }

    public void setHeadPortrait(String headPortrait) {
        SharedPreferences.Editor editor = mConfig.getEditor();
        editor.putString(KEY_HEAD_PORTRAIT, headPortrait);
        editor.commit();
    }

    public String getHeadPortrait() {
        return mConfig.getPreferences().getString(KEY_HEAD_PORTRAIT, null);
    }

    public String getPhone() {
        return mConfig.getPreferences().getString(KEY_PHONE, "");
    }

    /**
     * @return the config
     */
    public ConfigManager getConfig() {
        return mConfig;
    }

//    public void saveLoginID(DangUserInfo info) {
//        SharedPreferences.Editor editor = mConfig.getEditor();
//        editor.putString("login_id", info.id);
//        editor.putString("login_token", info.token);
//        editor.putInt("login_type", info.loginType.ordinal());
//        editor.commit();
//
//        saveLoginResult(info);
//    }

    public String getLoginToken() {
        return mConfig.getPreferences().getString("login_token", "");
    }

    public boolean isLogin() {
        return StringUtil.isEmpty(getLoginToken()) == true ? false : true;
    }

    public String getLoginID() {
        return mConfig.getPreferences().getString("login_id", "");
    }

//    public LoginType getLoginType() {
//        int value = mConfig.getPreferences().getInt("login_type", -1);
//        if (value == -1)
//            return null;
//        return LoginType.valueOf(value);
//    }

    public boolean isOrderByTime() {
        return mConfig.getPreferences().getBoolean("orderByTime", true);
    }

    public void setOrderType(boolean bo) {
        SharedPreferences.Editor editor = mConfig.getEditor();
        editor.putBoolean("orderByTime", bo);
        editor.commit();
    }

//    private void saveLoginResult(DangUserInfo info) {
//
//        SharedPreferences.Editor editor = mConfig.getEditor();
//        editor.putString(KEY_USERNAME, info.nameAll);
////		editor.putString(KEY_THREENAME, threename);
//        editor.putString(KEY_USEID, info.id);
//        editor.putString(KEY_TOKEN, info.token);
//        if (info.loginType == LoginType.DD)
//            editor.putBoolean(KEY_ISTHIRD, false);
//        else
//            editor.putBoolean(KEY_ISTHIRD, true);
//        editor.putLong(KEY_TOKEN_BIRTHDAY, System.currentTimeMillis());
//        editor.putBoolean(KEY_LOGINED_FLAG, true);
//        editor.putString(KEY_HEAD_PORTRAIT, info.head);
//        editor.putString(KEY_PHONE, info.telephone);
//        editor.commit();
//        // 保存登录用户信息
//        // saveLoginData();
//        sendGetUnreadMsgCountBroadcast();
//    }

    private void sendGetUnreadMsgCountBroadcast() {
        Intent intent = new Intent(Constant.ACTION_GET_UNREAD_MSG_COUNT);
        mContext.sendBroadcast(intent);
    }

    private void sendLogoutBraodcast() {
        Intent intent = new Intent(Constant.ACTION_LOGOUT_SUCCESS);
        mContext.sendBroadcast(intent);
    }

    public void removeLoginResult() {
        // 更新当前用户的信息
        // saveRemoveLoginData();

        SharedPreferences.Editor editor = mConfig.getEditor();
        editor.remove(ConfigManager.KEY_INIT_CARTID);
        editor.remove(ConfigManager.KEY_PROMPT_NUM);
        editor.remove(KEY_THREENAME);
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_TOKEN_BIRTHDAY);
        editor.remove(KEY_PHONE);
        editor.remove(KEY_DISPLAY_ID);
        editor.remove("login_id");
        editor.remove("login_token");
        editor.remove("login_type");
        editor.remove("dd_last_bought");
        editor.remove("dd_last_bought_id");
        editor.remove("get_all_buy");

        editor.commit();
    }

    /**
     * 保存购物车id
     *
     * @param cardId
     */
    public void setCartId(String cardId) {
        Editor editor = mConfig.getEditor();
        editor.putString(KEY_CART_ID, cardId);
        editor.commit();
    }

    /**
     * 获取购物车id
     *
     * @return
     */
    public String getCartId() {
        return mConfig.getPreferences().getString(KEY_CART_ID, "");
    }

    /**
     * 是否有升级版本
     * @param bo
     */
    public void setNewFlag(boolean bo){
    	Editor editor = mConfig.getEditor();
        editor.putBoolean("new_flag", bo);
        editor.commit();
    }
    
    public boolean getNewFlag(){
    	return mConfig.getPreferences().getBoolean("new_flag", false);
    }
    
    /**
     * 保存购物车列表数
     *
     * @param count
     */
    public void setCartListCount(int count) {
        Editor editor = mConfig.getEditor();
        editor.putInt(KEY_CART_LIST_COUNT, count);
        editor.commit();
    }

    /**
     * 获取购物列表数
     *
     * @return
     */
    public int getCartListCount() {
        return mConfig.getPreferences().getInt(KEY_CART_LIST_COUNT, 0);
    }

    /**
     * 设置收藏列表id
     *
     * @param favorProductId 多个productId之间用分号(;)分隔
     */
    public void setFavorProductId(String favorProductId) {
        Editor editor = mConfig.getEditor();
        editor.putString(KEY_FAVOR_PRODUCT_ID, favorProductId);
        editor.commit();
    }

    /**
     * 获取收藏favorProductId
     *
     * @return favorProductId字符串，多个productId之间用分号(;)分隔
     */
    public String getFavorProductId() {
        return mConfig.getPreferences().getString(KEY_FAVOR_PRODUCT_ID, "");
    }

    /**
     * 设置当前活动id
     *
     * @param activityId
     */
    public void setCurrentActivityId(String activityId) {
        Editor editor = mConfig.getEditor();
        editor.putString(KEY_CURRENT_ACTIVITY_ID, activityId);
        editor.commit();
    }

    /**
     * 获取活动activityId
     *
     * @return activityId
     */
    public String getCurrentActivityId() {
        return mConfig.getPreferences().getString(KEY_CURRENT_ACTIVITY_ID, "");
    }

    /**
     * 设置当前启动图endTime
     *
     * @param endTime
     */
    public void setGuideImageEndTime(String endTime) {
        Editor editor = mConfig.getEditor();
        editor.putString(KEY_GUIDE_IMAGE_END_TIME, endTime);
        editor.commit();
    }

    /**
     * 获取启动图endTime
     */
    public String getGuideImageEndTime() {
        return mConfig.getPreferences().getString(KEY_GUIDE_IMAGE_END_TIME, "");
    }

    /**
     * 设置当前版本启动次数
     *
     * @param endTime
     */
    public void setAppBootNum(String endTime) {
        Editor editor = mConfig.getEditor();
        editor.putString(KEY_APPLICATION_BOOT_NUM, endTime);
        editor.commit();
    }

    /**
     * 获取当前版本启动次数
     */
    public String getAppBootNum() {
        return mConfig.getPreferences().getString(KEY_APPLICATION_BOOT_NUM, "0");
    }

    /**
     * 设置市场评论时间
     *
     * @param endTime
     */
    public void setMarketCommentTime(String endTime) {
        Editor editor = mConfig.getEditor();
        editor.putString(KEY_SHOW_MARKET_COMMENT, endTime);
        editor.commit();
    }

    /**
     * 获取市场品论时间
     */
    public String getMarketCommentTime() {
        return mConfig.getPreferences().getString(KEY_SHOW_MARKET_COMMENT, "");
    }

    /**
     * 设置应用版本
     *
     * @param version
     */
    public void setVersionName(String version) {
        Editor editor = mConfig.getEditor();
        editor.putString(KEY_VERSION_NAME, version);
        editor.commit();
    }

    /**
     * 获取应用版本
     */
    public String getVersionName() {
        return mConfig.getPreferences().getString(KEY_VERSION_NAME, "");
    }

    /**
     * 设置当前baidu push消息
     *
     * @param message
     */
    public void setCurrentPushMessage(String message) {
        Editor editor = mConfig.getEditor();
        editor.putString(KEY_CURRENT_BAIDU_PUSH_MESSAGE, message);
        editor.commit();
    }

    /**
     * 获取当前baidu push消息
     */
    public String getCurrentPushMessage() {
        return mConfig.getPreferences().getString(KEY_CURRENT_BAIDU_PUSH_MESSAGE, "");
    }

    /**
     * 获取上次支付的方式
     *
     * @return
     */
    public int getPayMethod() {
        return mConfig.getPreferences().getInt(KEY_PAY_METHOD, 0);
    }

    /**
     * 保存上次支付方式
     *
     * @param payMethod
     */
    public void setPayMethod(int payMethod) {
        Editor editor = mConfig.getEditor();
        editor.putInt(KEY_PAY_METHOD, payMethod);
        editor.commit();
    }

    /**
     * 获取夜场类型 1 男 2 女
     *
     * @return
     */
    public int getNightShowChooseType() {
        return mConfig.getPreferences().getInt(KEY_NIGHT_SHOW_CHOOSE_TYPE, 1);
    }

    /**
     * 保存夜场类型
     *
     * @param chooseType
     */
    public void setNightShowChooseType(int chooseType) {
        Editor editor = mConfig.getEditor();
        editor.putInt(KEY_NIGHT_SHOW_CHOOSE_TYPE, chooseType);
        editor.commit();
    }

    /**
     * 是否第一次进入夜场
     *
     * @return
     */
    public boolean getIsNightFirstTime() {
        return mConfig.getPreferences().getBoolean(KEY_NIGHT_SHOW_FIRST_TIME,
                true);
    }

    public void setNightFirstTime(boolean bFirstTime) {
        Editor editor = mConfig.getEditor();
        editor.putBoolean(KEY_NIGHT_SHOW_FIRST_TIME, bFirstTime);
        editor.commit();
    }

    /**
     * 百度推送是否关闭
     *
     * @return
     */
    public boolean getIsPushStop() {
        return mConfig.getPreferences().getBoolean(KEY_BAIDU_PUSH_SERVICE_STOP,
                false);
    }

    /**
     * @param bFirstTime
     * @function 设置关闭或打开百度推送
     */
    public void setPushStop(boolean bFirstTime) {
        Editor editor = mConfig.getEditor();
        editor.putBoolean(KEY_BAIDU_PUSH_SERVICE_STOP, bFirstTime);
        editor.commit();
    }

    // 是否在其他tab显示夜场入口
    public boolean getIsNightEnterOtherModuleShow() {
        return mConfig.getPreferences().getBoolean(
                KEY_NIGHT_ENTER_SHOW_OTHER_MODULE, true);
    }

    public void setNightEnterOtherModuleShow(boolean bshow) {
        Editor editor = mConfig.getEditor();
        editor.putBoolean(KEY_NIGHT_ENTER_SHOW_OTHER_MODULE, bshow);
        editor.commit();
    }


    /**
     * 保存是购物车列表是否刷新
     *
     * @param isRrefresh
     */
    public void setShoppingCartListRefresh(boolean isRrefresh) {
        Editor editor = mConfig.getEditor();
        editor.putBoolean(KEY_SHOPPING_CART_REFRESH, isRrefresh);
        editor.commit();
    }

    /**
     * 获取列表是否刷新
     *
     * @return
     */
    public boolean getShoppingCartListRefresh() {
        return mConfig.getPreferences().getBoolean(KEY_SHOPPING_CART_REFRESH,
                true);
    }

    /**
     * 设置收藏列表id
     *
     * @param historyBeatOthers 多个productId之间用分号(;)分隔
     */
    public void setHistoryBeatOthers(String historyBeatOthers) {
        Editor editor = mConfig.getEditor();
        editor.putString(KEY_HISTORY_BEAT_OTHERS, historyBeatOthers);
        editor.commit();
    }

    public void setGoldBell(String goldBell) {
        Editor editor = mConfig.getEditor();
        editor.putString(KEY_VIRTUAL_GOLD_SHOW, goldBell);
        editor.commit();
    }

    public String getGoldBell() {
        return mConfig.getPreferences().getString(KEY_VIRTUAL_GOLD_SHOW, "--");
    }

    public void setSilverBell(String silverBell) {
        Editor editor = mConfig.getEditor();
        editor.putString(KEY_VIRTUAL_SILVER_SHOW, silverBell);
        editor.commit();
    }

    public String getSilverBell() {
        return mConfig.getPreferences().getString(KEY_VIRTUAL_SILVER_SHOW, "--");
    }

    /**
     * 获取收藏favorProductId
     *
     * @return favorProductId字符串，多个productId之间用分号(;)分隔
     */
    public String getHistoryBeatOthers() {
        return mConfig.getPreferences().getString(KEY_HISTORY_BEAT_OTHERS, "");
    }

    /**
     * 更新收藏列表id
     *
     * @param favorProductId 某本书的productId
     * @param isAdd          是否是添加，true表示添加收藏成功后更新；false表示取消收藏成功后更新
     */
    public void updateFavorProductId(String favorProductId, boolean isAdd) {
        String saveFavProductId = getFavorProductId();
        if (!TextUtils.isEmpty(favorProductId)) {
            if (isAdd) {
                saveFavProductId = saveFavProductId + ";" + favorProductId;
                setFavorProductId(saveFavProductId);
            } else {
                if (!TextUtils.isEmpty(saveFavProductId)) {
                    if (saveFavProductId.contains(favorProductId + ";")) {
                        saveFavProductId = saveFavProductId.replace(
                                favorProductId + ";", "");
                    } else if (saveFavProductId.contains(favorProductId)) {
                        saveFavProductId = saveFavProductId.replace(
                                favorProductId, "");
                    }
                    setFavorProductId(saveFavProductId);
                }
            }
        }
    }
    /**
     * 设置启动图信息
     *
     * @param info
     *
     */
    public void setLoadingImageInfo(String info) {
        Editor editor = mConfig.getEditor();
        editor.putString(KEY_LOADING_IMAGE_INFO, info);
        editor.commit();
    }

    /**
     * 获取启动图信息
     *
     * @return info
     */
    public String getLoadingImageInfo() {
        return mConfig.getPreferences().getString(KEY_LOADING_IMAGE_INFO, "");
    }
    /**
     * 设置启动图信息
     *
     * @param imid
     *
     */
    public void setImUserId(String imid) {
        Editor editor = mConfig.getEditor();
        editor.putString("dd_imuser_id", imid);
        editor.commit();
    }
    /**
     *
     *
     */
    public String getImUserId() {
        return mConfig.getPreferences().getString("dd_imuser_id", "undefine");

    }

    /**
     * 获取启动图信息
     *
     * @return info
     */
    public void setImUserPwd(String pwd) {
        Editor editor = mConfig.getEditor();
        editor.putString("dd_imuser_pwd", pwd);
        editor.commit();
    }
    /**
     * 获取启动图信息
     *
     * @return info
     */
    public String getImUserPwd() {
        return mConfig.getPreferences().getString("dd_imuser_pwd", "");
    }
        
    /**
     * 清除已购列表信息
     */
    public void removeBuyInfo() {
        SharedPreferences.Editor editor = mConfig.getEditor();
        editor.remove("dd_last_bought");
        editor.remove("dd_last_bought_id");
        editor.remove("get_all_buy");

        editor.commit();
    }

    public void setUpdateSaleId(boolean bo){
    	Editor editor = mConfig.getEditor();
        editor.putBoolean("dd_update_bought_id", bo);
        editor.commit();
    }
    
    public boolean isUpdateSaleId(){
    	return mConfig.getPreferences().getBoolean("dd_update_bought_id", false);
    }
    
    /**
     * 设置最后取已购的时间
     * @param time
     */
    public void setBoughtLastTime(long time){
    	Editor editor = mConfig.getEditor();
        editor.putLong("dd_last_bought", time);
        editor.commit();
    }
    
    /**
     * 取最后已购时间
     * @return
     */
    public long getBoughtLastTime(){
    	return mConfig.getPreferences().getLong("dd_last_bought", 0);
    }

    /**
     * 设置取到全部已购
     */
    public void setGetAllBuy(){
    	Editor editor = mConfig.getEditor();
    	editor.putBoolean("get_all_buy", true);
    	editor.commit();
    }
    
    /**
     * 是否取到全部已购
     * @return
     */
    public boolean isGetAllBuy(){
    	return mConfig.getPreferences().getBoolean("get_all_buy", false);
    }
    
    /**
     * 设置最后取已购的ID
     * @param id
     */
    public void setBoughtLastAuthorityId(String id){
    	if(TextUtils.isEmpty(id))
    		return;
    	Editor editor = mConfig.getEditor();
        editor.putString("dd_last_bought_id", id);
        editor.commit();
    }
    
    /**
     * 取最后已购ID
     * @return
     */
    public String getBoughtLastAuthorityId(){
    	return mConfig.getPreferences().getString("dd_last_bought_id", "");
    }
    
    public void updateOldOrderByTime(){
    	SharedPreferences pre = mContext.getSharedPreferences(
				"last_refresh_time_file", Context.MODE_PRIVATE);
		int type = pre.getInt("last_order_type", 1);
		setOrderType(type == 1);
    }

    /**
     * 取关闭或打开频道信息推送
     * @return
     */
    public boolean getChannelMsgSwitch(){
        SharedPreferences pre = mConfig.getPreferences();
        return pre.getBoolean(CHANNELMSG_SWITCH, false);
    }

    /**
     * 设置关闭或打开频道信息推送
     * @param tSwitch
     */
    public void setChannelMsgSwitch(boolean tSwitch){
        SharedPreferences.Editor editor = mConfig.getEditor();
        editor.putBoolean(CHANNELMSG_SWITCH, tSwitch);
        editor.commit();
    }

    public String getUsrDir(){
        SharedPreferences pre = mConfig.getPreferences();
        return pre.getString(USR_DIR, "");
    }

    public void setUsrDir(String dir){
        if(TextUtils.isEmpty(dir))
            return;
        SharedPreferences.Editor editor = mConfig.getEditor();
        editor.putString(USR_DIR, dir);
        editor.commit();
    }
}
