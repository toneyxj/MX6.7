package com.dangdang.reader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.dangdang.zframework.log.LogM;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.util.UUID;


public class ConfigManager {

    public final static String KEY_EYE_CARE_TIME = "rest_period_per_mins";
    public final static String KEY_HAS_SENT_CHANNEL_ID = "has_sent_channel";

    private static final String DANG_READER_PREF = "dang_reader_config";
    public static final String DANG_BOOK_STORE = "dang_book_store_config";
    public static final String DANG_PERSONAL = "dang_personal_config";
    //购物车
    public static final String KEY_INIT_PERMANENTID = "init_permanentId";
    public static final String KEY_INIT_CARTID = "init_cartId";
    public static final String KEY_PROMPT_NUM = "prompt_num";//购物车数量提示
    public static final String KEY_IS_REGISTER = "is_sina_register";
    public static final String KEY_IS_REGISTER_TENCENT = "is_tencent_register";
    public static final String KEY_INIT_DATA_IN_SDCARD = "init_data_in_sdcard";
    public static final String KEY_CURRENT_DATA_IN_SDCARD = "current_data_in_sdcard";
    public static final String KEY_CANCELED_MOVE_DATA_TO_SDCARD = "not_move_data_to_sdcard";
    public static final String KEY_IS_CLOSE_CHANGE_BG = "close_change_bg";
    public static final String KEY_IS_READ_VERTICAL_BG = "read_vertical_bg";
    public static final String KEY_CONTENT_CSS_VEVSION = "reader_epub_css_version_new";
    public static final String KEY_ERROR_LOG_LINE = "error_log_line";

    public static final int LIGHT_SCALE_FACTOR = 100;

    public static final String KEY_MSG_PREQUERY_DATE = "msg_prequery_longdate";
    public static final String KEY_UPGRADE_FLAG = "key_upgrade_flag";
    //兰亭黑默认字体
    public static final String KEY_DEFAULT_FONT_EXIST = "default_font_exist";

    //消息推送开关
    public static final String MESSAGE_SEND_STATE_FILE = "message_send_state_file";
    public static final String MESSAGE_SEND_STATE = "message_send_state";

    /**
     * 同步设置显示new图片
     *
     * @return
     */
    public static final String KEY_SYNC_NEWTIP = "key_sync_newtip";

    private Context context;
    private SharedPreferences pref = null;
    private static final String PRIVATE_KEY_URL = "priave";
    private static final String PUBLIC_KEY_URL = "public";
    //private String mPublicKey = null;
    //private String mPrivateKey = null;

    //第一次进入 3.0版本
    public static final String FIRST_UPGRADE_FILE = "first_upgrade_file";
    public static final String FIRST_UPGRADE = "first_upgrade";

    //第一次 进入 3.2 版本
    public static final String UPGRADE_TO_3_2 = "upgrade_to_3_2";
    public static final String UPGRADE_TO_3_2_VARIABLE = "upgrade_to_3_2_variable";

    public static final String FIRST_TO_3_3_GUIDE = "first_3_3_guide_file";
    public static final String FIRST_TO_3_3_GUIDE_VARIABLE = "first_3_3_guide_variable";

    //用户获取启动页面配置
    public static final String STARTPAGE_IMG_VERSION = "startpage_img_version";
    public static final String STARTPAGE_IMG_FILE = "startpage_img_file";

    //pdf 资源文件存放preference
    public static final String PDF_RESOURCE_FILE = "pdf_resource_file";
    public static final String PDF_RESOURCE_URL = "pdf_resource_url";

    //修改 3.5.0 版本之前 笔记 没有添加 试读或者全本标记的问题
    public static final String NOTE_WITHOUT_FULL_FLAG = "note_without_full_flag";
    public static final String NOTE_WITHOUT_FULL_FLAG_VAR = "note_without_full_flag_var";
    public static final String IS_CLEAR_CACHE = "is_clear_cache_4.1.0";

    public static final String SIGN_IN_DATE_MILLIS = "sign_in_date_millis";
    public static final String PERSONAL_SHOW_RECHARGE = "show_recharge";
    public static final String PERSONAL_SHOW_WALLET = "show_wallet";
    /**
     * 环境
     */
    public static final String ENVIRONMENT = "environment";

    /**
     * 选择退出应用标志
     */
    public static final String KEY_EXITAPP_FLAG = "key_exitapp_flag";

    private static final String KEY_EBOOK_SHOPPING_CART_ID = "key_ebook_shopping_cart_id";
    private static final String KEY_PAPER_BOOK_SHOPPING_CART_ID = "key_paper_book_shopping_cart_id";
    private static final String KEY_PAPER_BOOK_SHOPPING_CART_ID_IS_TEMP = "key_paper_book_shopping_cart_id_is_temp";
    private static final String KEY_SHOPPING_CART_EBOOK_COUNT = "key_shopping_cart_ebook_count";
    private static final String KEY_SHOPPING_CART_PAPERBOOK_COUNT = "key_shopping_cart_paperbook_count";
    private static final String KEY_SHOPPING_CART_EBOOK_IDS = "key_shopping_cart_ebook_ids";

    private static final String KEY_LAST_SHARE_TO_BAR_ID = "key_last_share_to_bar_id";
    private static final String KEY_LAST_SHARE_TO_BAR_NAME = "key_last_share_to_bar_name";
    private static final String KEY_LAST_SHARE_TO_BAR_IMG_URL = "key_last_share_to_bar_img_url";
    private static final String KEY_LAST_SHARE_TO_BAR_DESC = "key_last_share_to_bar_desc";

    public ConfigManager(Context context) {
        super();
        this.context = context;
        pref = context.getSharedPreferences(DANG_READER_PREF,
                Context.MODE_PRIVATE);
    }

    /**
     * @param context
     * @param configName
     * @function 指定存储文件
     */
    public ConfigManager(Context context, String configName) {
        super();
        this.context = context;
        pref = context.getSharedPreferences(configName,
                Context.MODE_PRIVATE);
    }

    public SharedPreferences getPreferences() {
        return pref;
    }

    public SharedPreferences.Editor getEditor() {
        return pref.edit();
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(DANG_READER_PREF,
                Context.MODE_PRIVATE);
    }

    /**
     * 获取设备id策略： 1，优先IMEI串号； 2，MAC地址串； 3，ANDROID_ID串号(获取 Android 设备的唯一标识码 )；
     *
     * @return
     */
    public String getDeviceId() {
        String deviceId = pref.getString("device_id", null);
        if (TextUtils.isEmpty(deviceId) || deviceId.equalsIgnoreCase("null")) {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId();
            // LogM.d("ConfigManager", "getDeviceId:" + deviceId);
            long deviceIdInt = 0;
            try {
                deviceIdInt = Long.parseLong(deviceId);
            } catch (Exception e) {
                e.printStackTrace();
                deviceIdInt = 0;
            }
            // if (deviceId == null || "0".equalsIgnoreCase(deviceId)) {
            if (deviceIdInt == 0) {
                deviceId = getDeviceMacAddress();
                if (TextUtils.isEmpty(deviceId)
                        || deviceId.equalsIgnoreCase("null")
                        || "00:00:00:00:00:00".equals(deviceId)) {
                    deviceId = getDeviceAndroidId();
                    // 如果都没有取到，添加随机生成唯一码
                    if (TextUtils.isEmpty(deviceId)
                            || deviceId.equalsIgnoreCase("null")) {
                        deviceId = "dandangreader"
                                + UUID.randomUUID().toString();
                    }
                }
                LogM.d("ConfigManager", "getDeviceMacAddress:" + deviceId);
            }
            SharedPreferences.Editor editor = getEditor();
            editor.putString("device_id", deviceId);
            editor.commit();
        }
        return deviceId;
    }

    /**
     * 获取配置文件中版本号versionName android:versionName="1.0.6"
     */
    public String getVersionName() {
        String versionName = null;
        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            versionName = packInfo.versionName;
            LogM.d("versionName=" + versionName);
        } catch (Exception e) {
            LogM.d("Failed to get versionName: " + e.getMessage());
        }
        return versionName;
    }

    /**
     * 是否是V4.0版本
     */
    public boolean IsNewVersion() {
        if (("4.0.0").equals(getVersionName())) {
            return true;
        }
        return false;
    }

    public String getPackageName() {
        return context.getPackageName();
    }

    /**
     * 获取配置文件中渠道号
     *
     * @return
     */
    public String getChannelId() {
        String channelId = null;
        int nchannelId = 0;
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            nchannelId = bundle.getInt("UMENG_CHANNEL");
            channelId = String.valueOf(nchannelId);
            LogM.d("channelId=" + channelId);
        } catch (NameNotFoundException e) {
            LogM.d("Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            LogM.d("Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return channelId;
    }

    /*
     * 服务器版本号
     */
    public String getServerVesion() {
        String vesion = null;
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            vesion = bundle.getString("SERVER_VERSION");
        } catch (NameNotFoundException e) {
            LogM.d("Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            LogM.d("Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return vesion;
    }

    /*
     * 活动id
     */
    public String getActivityId() {
        String activityId = null;
        int id = 0;
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            id = bundle.getInt("ACTIVITY_ID");
            activityId = String.valueOf(id);
        } catch (NameNotFoundException e) {
            LogM.d("Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            LogM.d("Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return activityId;
    }

    /**
     * 获取应用名
     */
    public String getAppName() {
        String appName = null;
        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            appName = packInfo.applicationInfo.toString();
            LogM.d("appName=" + appName);
        } catch (Exception e) {
            LogM.d("Failed to get versionName: " + e.getMessage());
        }
        return appName;
    }

    /**
     * 获取mac地址作为认证
     *
     * @return
     */
    public String getDeviceMacAddress() {

        String macAddr = "";
        try {
            WifiManager wifi = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            macAddr = info.getMacAddress();
        } catch (Exception e) {
            macAddr = null;
            e.printStackTrace();
        }

        return macAddr;
    }

    /**
     * 获取设备的唯一标识码
     */
    public String getDeviceAndroidId() {
        String android_id = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
        return android_id;
    }

    /**
     * 获取设备cpu序列号
     *
     * @return CPU序列号(16位) 未使用过 读取失败为"0000000000000000"
     */
    public static String getCPUSerial() {
        String str = "", strCPU = "", cpuAddress = "0000000000000000";
        try {
            // 读取CPU信息
            // Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            Process pp = Runtime.getRuntime().exec("system/proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            // 查找CPU序列号
            for (int i = 1; i < 500; i++) {
                str = input.readLine();
                if (str != null) {
                    // 查找到序列号所在行
                    // if (str.indexOf("Serial") > -1) {
                    // 提取序列号
                    strCPU = str.substring(str.indexOf(":") + 1, str.length());
                    // 去空格
                    // cpuAddress = strCPU.trim();
                    cpuAddress += str.trim();
                    // break;
                    // }
                } else {
                    // 文件结尾
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return cpuAddress;
    }


    /**
     * 手机型号
     */
    public String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * OS version
     */
    public String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public String getPublicKeyPath() {

        return context.getFilesDir().toString() + "/" + PUBLIC_KEY_URL;
        //return DangdangFileManager.getPreSetOffPrintRsaPublic();
    }

    public String getPrivateKeyPath() {

        return context.getFilesDir().toString() + "/" + PRIVATE_KEY_URL;
        //return DangdangFileManager.getPreSetOffPrintRsaPrivate();
    }

    public byte[] getPublicKeyPathByte() {

        try {
            return getPublicKeyPath().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] getPrivateKeyPathByte() {

        try {
            return getPrivateKeyPath().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @return
     * @throws Exception
     * @throws FileNotFoundException
     */
    /**
     * @return
     * @throws Exception
     * @throws FileNotFoundException
     */
    /*public String getPublicKey() throws Exception {

		if (mPublicKey != null) {
			return mPublicKey;
		}
		File file = new File(context.getFilesDir() + "/" + PUBLIC_KEY_URL);
		if (file.exists()) {
			try {
				FileInputStream inStream = new FileInputStream(
						context.getFilesDir() + "/" + PUBLIC_KEY_URL);
				byte[] datas = DRCompress.decrypt(StreamUtils
						.getBytesFromStream(inStream));
				mPublicKey = new String(datas, "utf-8");
				return mPublicKey;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			Map<RsaKey, String> keyPair = RsaUtils.generateKeyPair();

			// 如果有113之前的密钥对，直接使用，不生成新密钥
			mPublicKey = pref.getString("public_key", null);
			mPrivateKey = pref.getString("private_key", null);
			if (mPublicKey == null) {
				mPublicKey = keyPair.get(RsaUtils.RsaKey.publicKey);
				mPrivateKey = keyPair.get(RsaUtils.RsaKey.privateKey);
			} else {
				SharedPreferences.Editor editor = getEditor();
				editor.putString("public_key", null);
				editor.putString("private_key", null);
				editor.commit();
			}

			byte[] reponsebody = DRCompress.encrypt(mPublicKey.getBytes());
			FileOutputStream outStream = context.openFileOutput(PUBLIC_KEY_URL,
					context.MODE_PRIVATE);
			outStream.write(reponsebody);

			byte[] privatebody = DRCompress.encrypt(mPrivateKey.getBytes());
			FileOutputStream privateoutStream = context.openFileOutput(
					PRIVATE_KEY_URL, context.MODE_PRIVATE);
			privateoutStream.write(privatebody);

		}
		return mPublicKey;
	}*/
	/*public String getPublicKey() throws Exception {
		if (mPublicKey != null) {
			return mPublicKey;
		}
		File file = new File(context.getFilesDir() + "/" + PUBLIC_KEY_URL);
		if (file.exists()) {
			try {
				FileInputStream inStream = new FileInputStream(
						context.getFilesDir() + "/" + PUBLIC_KEY_URL);
				byte[] datas = DRKeyEncry.decrypt(StreamUtils
						.getBytesFromStream(inStream));
				mPublicKey = new String(datas, "utf-8");
				return mPublicKey;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} 
		else{
			Map<RsaKey, String> keyPair = RsaUtils.generateKeyPair();
			mPublicKey = keyPair.get(RsaUtils.RsaKey.publicKey);
			mPrivateKey = keyPair.get(RsaUtils.RsaKey.privateKey);
	
			byte[] reponsebody = DRKeyEncry.encrypt(mPublicKey.getBytes());		
			FileOutputStream outStream = context.openFileOutput(PUBLIC_KEY_URL,context.MODE_PRIVATE);
			outStream.write(reponsebody);	
	
			byte[] privatebody = DRKeyEncry.encrypt(mPrivateKey.getBytes());
			FileOutputStream privateoutStream = context.openFileOutput(PRIVATE_KEY_URL,context.MODE_PRIVATE);
			privateoutStream.write(privatebody);			
		}
		return mPublicKey;
	}*/

	/*public String getPrivateKey() throws Exception {
		if (mPrivateKey != null) {
			return mPrivateKey;
		}
		File file = new File(context.getFilesDir() + "/" + PRIVATE_KEY_URL);
		if (file.exists() && mPrivateKey == null) {
			try {
				FileInputStream inStream = new FileInputStream(
						context.getFilesDir() + "/" + PRIVATE_KEY_URL);
				byte[] datas = DRCompress.decrypt(StreamUtils
						.getBytesFromStream(inStream));
				mPrivateKey = new String(datas, "utf-8");
				return mPrivateKey;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			Map<RsaKey, String> keyPair = RsaUtils.generateKeyPair();

			// 如果有113之前的密钥对，直接使用，不生成新密钥
			mPublicKey = pref.getString("public_key", null);
			mPrivateKey = pref.getString("private_key", null);
			if (mPublicKey == null) {
				mPublicKey = keyPair.get(RsaUtils.RsaKey.publicKey);
				mPrivateKey = keyPair.get(RsaUtils.RsaKey.privateKey);
			} else {
				SharedPreferences.Editor editor = getEditor();
				editor.putString("public_key", null);
				editor.putString("private_key", null);
				editor.commit();
			}

			byte[] reponsebody = DRCompress.encrypt(mPublicKey.getBytes());
			FileOutputStream outStream = context.openFileOutput(PUBLIC_KEY_URL,
					context.MODE_PRIVATE);
			outStream.write(reponsebody);

			byte[] privatebody = DRCompress.encrypt(mPrivateKey.getBytes());
			FileOutputStream privateoutStream = context.openFileOutput(
					PRIVATE_KEY_URL, context.MODE_PRIVATE);
			privateoutStream.write(privatebody);
		}
		return mPrivateKey;
	}*/
	
	/*public String getPrivateKey() throws Exception {
		if (mPrivateKey != null) {
			return mPrivateKey;
		}
		File file = new File(context.getFilesDir() + "/" + PRIVATE_KEY_URL);
		if (file.exists() && mPrivateKey == null) {
			try {
				FileInputStream inStream = new FileInputStream(
						context.getFilesDir() + "/" + PRIVATE_KEY_URL);
				byte[] datas = DRKeyEncry.decrypt(StreamUtils
						.getBytesFromStream(inStream));
				mPrivateKey = new String(datas, "utf-8");
				return mPrivateKey;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			Map<RsaKey, String> keyPair = RsaUtils.generateKeyPair();
			mPublicKey = keyPair.get(RsaUtils.RsaKey.publicKey);
			mPrivateKey = keyPair.get(RsaUtils.RsaKey.privateKey);
	
			byte[] reponsebody = DRKeyEncry.encrypt(mPublicKey.getBytes());		
			FileOutputStream outStream=context.openFileOutput(PUBLIC_KEY_URL,context.MODE_PRIVATE);
			outStream.write(reponsebody);	

			byte[] privatebody = DRKeyEncry.encrypt(mPrivateKey.getBytes());
			FileOutputStream privateoutStream = context.openFileOutput(PRIVATE_KEY_URL,context.MODE_PRIVATE);
			privateoutStream.write(privatebody);	
		}
		return mPrivateKey;
	}*/
    public boolean isInitDataInSdcard() {
        return pref.getBoolean(KEY_INIT_DATA_IN_SDCARD, false);
    }

    public boolean isCurrentDataInSdcard() {
        return pref.getBoolean(KEY_CURRENT_DATA_IN_SDCARD, false);
    }

    public boolean isCancelledMoveDataToSdcard() {
        return pref.getBoolean(KEY_CANCELED_MOVE_DATA_TO_SDCARD, false);
    }

    public long getMsgPreQueryDate() {
        return pref.getLong(KEY_MSG_PREQUERY_DATE, 0);
    }

    public void setInitInSdcard(boolean useSd) {
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putBoolean(ConfigManager.KEY_INIT_DATA_IN_SDCARD, useSd);
        prefEditor.commit();
    }

    public void setCurrentDataInSdcard(boolean usingSdcard) {
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putBoolean(ConfigManager.KEY_CURRENT_DATA_IN_SDCARD,
                usingSdcard);
        prefEditor.commit();
    }

    public void setCancelledMoveDataToSdcard(boolean canceled) {
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putBoolean(ConfigManager.KEY_CANCELED_MOVE_DATA_TO_SDCARD,
                canceled);
        prefEditor.commit();
    }

    public void setMsgPreQueryDate(long preDate) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(KEY_MSG_PREQUERY_DATE, preDate);
        editor.commit();
    }

    public void setUpgradeFlag(boolean hasUpgrade) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_UPGRADE_FLAG, hasUpgrade);
        editor.commit();
    }

    public boolean getUpgradeFlag() {
        return pref.getBoolean(KEY_UPGRADE_FLAG, false);
    }

    public boolean isFirstUpgrade() {
        SharedPreferences pre = context.getSharedPreferences(FIRST_UPGRADE_FILE, Context.MODE_PRIVATE);
        return pre.getBoolean(FIRST_UPGRADE, true);
    }

    public void setUpgradeState() {
        SharedPreferences pre = context.getSharedPreferences(FIRST_UPGRADE_FILE, Context.MODE_PRIVATE);
        Editor ep = pre.edit();
        ep.putBoolean(FIRST_UPGRADE, false);
        ep.commit();
    }

    //返回true，说明 消息推送处于 打开状态，返回 false，说明处于 关闭状态
    public boolean getMessageSendState() {
        SharedPreferences pre = context.getSharedPreferences(MESSAGE_SEND_STATE_FILE, Context.MODE_PRIVATE);
        return pre.getBoolean(MESSAGE_SEND_STATE, true);
    }

    public void setMessageSendState(boolean bo) {
        SharedPreferences pre = context.getSharedPreferences(MESSAGE_SEND_STATE_FILE, Context.MODE_PRIVATE);
        Editor ep = pre.edit();
        ep.putBoolean(MESSAGE_SEND_STATE, bo);
        ep.commit();
    }

    /**
     * 同步设置是否显示new图片
     *
     * @return
     */
    public boolean isShowSyncNewTip() {
        SharedPreferences pre = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return pre.getBoolean(KEY_SYNC_NEWTIP, true);
    }

    public void setSyncNewTip(boolean bo) {
        SharedPreferences pre = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        Editor ep = pre.edit();
        ep.putBoolean(KEY_SYNC_NEWTIP, bo);
        ep.commit();
    }

    public boolean isFirstAccess3_2Application() {
        SharedPreferences pre = context.getSharedPreferences(UPGRADE_TO_3_2, Context.MODE_PRIVATE);
        return pre.getBoolean(UPGRADE_TO_3_2_VARIABLE, true);
    }

    public void update3_2ApplicationStatus(boolean bo) {
        SharedPreferences pre = context.getSharedPreferences(UPGRADE_TO_3_2, Context.MODE_PRIVATE);
        Editor ep = pre.edit();
        ep.putBoolean(UPGRADE_TO_3_2_VARIABLE, bo);
        ep.commit();
    }

    public boolean isFirstAccess3_2ApplicationGuide() {
        SharedPreferences pre = context.getSharedPreferences(FIRST_TO_3_3_GUIDE, Context.MODE_PRIVATE);
        return pre.getBoolean(FIRST_TO_3_3_GUIDE_VARIABLE, true);
    }

    public void update3_2ApplicationGuideStatus(boolean bo) {
        SharedPreferences pre = context.getSharedPreferences(FIRST_TO_3_3_GUIDE, Context.MODE_PRIVATE);
        Editor ep = pre.edit();
        ep.putBoolean(FIRST_TO_3_3_GUIDE_VARIABLE, bo);
        ep.commit();
    }

    public int getStartPageImgVersion() {
        SharedPreferences pre = context.getSharedPreferences(STARTPAGE_IMG_FILE, Context.MODE_PRIVATE);
        return pre.getInt(STARTPAGE_IMG_VERSION, 1);
    }

    public void updateStartPageImgVersion(int version) {
        SharedPreferences pre = context.getSharedPreferences(STARTPAGE_IMG_FILE, Context.MODE_PRIVATE);
        Editor ep = pre.edit();
        ep.putInt(STARTPAGE_IMG_VERSION, version);
        ep.commit();
    }

    public String getPdfResourceUrl() {
        SharedPreferences pre = context.getSharedPreferences(PDF_RESOURCE_FILE, Context.MODE_PRIVATE);
        return pre.getString(PDF_RESOURCE_URL, "");
    }

    public void setPdfResourceUrl(String url) {
        SharedPreferences pre = context.getSharedPreferences(PDF_RESOURCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString(PDF_RESOURCE_URL, url);
        editor.commit();
    }

    public boolean isCopyPdfRes() {
        SharedPreferences pre = context.getSharedPreferences(PDF_RESOURCE_FILE, Context.MODE_PRIVATE);
        return pre.contains(PDF_RESOURCE_URL);
    }

    public boolean getNoteFullFlag() {
        SharedPreferences pre = context.getSharedPreferences(NOTE_WITHOUT_FULL_FLAG, Context.MODE_PRIVATE);
        return pre.getBoolean(NOTE_WITHOUT_FULL_FLAG_VAR, true);
    }

    public void setNoteFullFlag(boolean flag) {
        SharedPreferences pre = context.getSharedPreferences(NOTE_WITHOUT_FULL_FLAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean(NOTE_WITHOUT_FULL_FLAG_VAR, flag);
        editor.commit();
    }

    public void setIsHasClearCache(boolean isClear) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_CLEAR_CACHE, isClear);
        editor.commit();
    }

    public boolean isHasClearCache() {
        return pref.getBoolean(IS_CLEAR_CACHE, false);
    }

    /**
     * 当前版本是否首次启动(根据versionCode组成key判断)
     *
     * @return
     */
    public boolean isCurrVersionFirstStart() {
        String key = getVersionFirstStartKey();
        return pref.getBoolean(key, true);
    }

    /**
     * 保存当前版本首次启动flag值
     *
     * @param flag
     */
    public void saveCurrVersionFirstStartFlag(boolean flag) {
        SharedPreferences.Editor editor = pref.edit();
        String key = getVersionFirstStartKey();
        editor.putBoolean(key, flag);
        editor.commit();
    }

    private String getVersionFirstStartKey() {
        int versionCode = -1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        String key = "versionCode:" + versionCode;
        return key;
    }

    public void saveExitAppFlag(boolean flag) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_EXITAPP_FLAG, flag);
        editor.commit();
    }

    public void delExitAppFlag() {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(KEY_EXITAPP_FLAG);
        editor.commit();
    }

    public boolean hasExitAppFlag() {
        return pref.getBoolean(KEY_EXITAPP_FLAG, false);
    }







    /**
     * 当前环境
     *
     * @param environment
     */
    public void setEnvironment(String environment) {
        if (TextUtils.isEmpty(environment)) {
            return;
        }
        Editor mEditor = pref.edit();
        mEditor.putString(ENVIRONMENT, environment);
        mEditor.commit();
    }

    /**
     * 当前环境
     */
    public String getEnvironment() {
        return pref.getString(ENVIRONMENT, DangdangConfig.mEnvironment);
    }



    /**
     * 保存推荐频道id列表
     *
     * @param ids
     */
    public void setChannelIds(String ids) {
        if (ids == null) {
            ids = "";
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ids", ids);
        editor.commit();
    }

    public String getChannelIds() {
        return pref.getString("ids", "");
    }

    /**
     * 保存兴趣标签id列表
     *
     * @param ids
     */
    public void setTagIds(String ids) {
        if (ids == null) {
            ids = "";
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("tagids", ids);
        editor.commit();
    }

    public String getTagIds() {
        return pref.getString("tagids", "");
    }


}
