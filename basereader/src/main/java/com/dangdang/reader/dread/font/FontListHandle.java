package com.dangdang.reader.dread.font;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.dangdang.reader.dread.data.Font;
import com.dangdang.reader.dread.data.FontDomain;
import com.dangdang.reader.dread.font.DownloadDb.DType;
import com.dangdang.reader.utils.AccountManager;
import com.dangdang.reader.utils.DangdangConfig;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.network.download.DownloadConstant.Status;
import com.dangdang.zframework.utils.FileUtil;
import com.dangdang.zframework.utils.MD5Util;
import com.dangdang.zframework.utils.StringUtil;
import com.dangdang.zframework.utils.ZipExecutor;
import com.mx.mxbase.constant.APPLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FontListHandle {

    private Context mContext;
    private SharedPreferences mSpfs;
    private AccountManager mAccountManager;

    public final static String FREE_FONT_USERNAME = "undefine";
    public final static String KEY_CURRENT_FONT_FLAG = "key_current_font_flag";
    public final static String KEY_CURRENT_FONT_PATH = "key_current_font_path";
    public final static String KEY_CURRENT_FONT_NAME = "key_current_font_name";
    public final static String KEY_CURRENT_FONT_VERSION = "key_current_font_version";//字体版本号，默认版本为1，此次升级为2（5.2.1）
    public final static int CURRENT_FONT_VERSION = 2;

    public final static String KEY_FREEFONT_DOWNFINISH = "key_freefont_downloadfinish";

    private final static String ENTTF_CHARSET = "DD_CHARSET_ANSI";
    public final static String DEFAULT_PRODUCTID = "-1";

    private final LogM logger = LogM.getLog(getClass());

    private FontListHandle(Context cx) {
        this.mContext = cx.getApplicationContext();
        this.mAccountManager = new AccountManager(mContext);
        this.mSpfs = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static FontListHandle getHandle(Context c) {
        return new FontListHandle(c);
    }

    public void addUnZip(String sourceFile, String indentityId) {
        final String destDir = getUnZipDestDir(sourceFile, indentityId);
        final String ttfpath = getTTfDestFile(sourceFile, indentityId);
        //mZipE.addUnZip(sourceFile, destDir, ttfpath);

        ZipExecutor.UnZipOperator unZipTask = new ZipExecutor.UnZipOperator(sourceFile, destDir, ttfpath);
        unZipTask.run();
    }

    public static List<FontDomain> removeRepeat(List<FontDomain> source, List<FontDomain> repeats) {
        if (source != null && repeats != null) {
            FontDomain sFontDomain = null;
            String productId;
            for (int i = 0, len = repeats.size(); i < len; i++) {
                productId = repeats.get(i).productId;

                for (int j = 0, lenj = source.size(); j < lenj; j++) {
                    sFontDomain = source.get(j);
                    if (productId.equals(sFontDomain.productId)) {
                        source.remove(sFontDomain);
                        break;
                    }
                }
            }
        }
        return source;
    }

    public List<FontDomain> conver(List<FontDownload> tmpDownloadFinishs, DownloadDb downService) {
        List<FontDownload> sds = tmpDownloadFinishs;
        List<FontDomain> fonts = new ArrayList<FontDomain>();
        FontDomain font = null;
        for (FontDownload sd : sds) {
            font = new FontDomain();
            font.progress = getFontDownloadSize(sd.indentityId);//sd.progress;
            font.totalSize = sd.totalSize;
            font.status = Status.convert(sd.status);
            font.fontZipPath = sd.saveDir;
            font.fontFtfPath = getTTfDestFile(font.fontZipPath, sd.indentityId);

            String ttfPath = getTTfDestFile(font.fontZipPath, sd.indentityId);
            if (!ttfFileExists(ttfPath)) {
                downService.updateStatusById(sd.indentityId, Status.UNSTART.getStatus());
                continue;
            }
            if (DType.FONT_FREE.name().equals(sd.type)) {
                font.freeBook = true;
                setFreeData(font, sd.bookData);
                if (getDefaultFontName().equals(font.getProductname())) {
                    continue;
                }
            } else if (DType.FONT_CHARGE.name().equals(sd.type)) {
                setChargeData(font, sd.bookData);
            }
            fonts.add(font);
        }

        return fonts;
    }

    private void setChargeData(FontDomain fontItem, String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject joItem = new JSONObject(json);
                fontItem.setDownloadURL(joItem.optString("downloadURL"));
                fontItem.setImageURL(converCoverSize(joItem.optString("cover", "")));
                int fsize = joItem.optInt("translator", 0);
                fontItem.setFontSize(FileUtil.formatFileSize(fsize));
                fontItem.setSalePrice(FileUtil.converYuan((float) joItem.optDouble("price", 0.0)));
                fontItem.productId = joItem.optString("productId");
                fontItem.setProductname(joItem.optString("bookName"));
                fontItem.jsonStr = joItem.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String converCoverSize(String cover) {
        if (!DangdangConfig.isDevelopEnv()) {
            return cover;
        }
        try {
            int index = cover.lastIndexOf("_");
            String s1 = cover.substring(0, index);
            String s2 = cover.substring(index, cover.length());
            System.out.println(s2);
            s2 = s2.replaceFirst("l", "o");
            return s1 + s2;
        } catch (Exception e) {
            e.printStackTrace();
            return cover;
        }
    }

    private void setFreeData(FontDomain fontItem, String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject joItem = new JSONObject(json);
                fontItem.setDownloadURL(joItem.optString("downloadURL"));
                fontItem.setImageURL(joItem.optString("imageURL"));
                fontItem.setFontSize(joItem.optString("fontSize", "0"));
                fontItem.setProductname(joItem.optString("productname"));
                fontItem.productId = MD5Util.getMD5Str(fontItem.getDownloadURL());
                fontItem.jsonStr = json;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public long getFontDownloadSize(String indentityId) {
        File fontFile = getFontSaveFile(indentityId);
        long size = 0;
        try {
            size = fontFile.length();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public String getPresetDefaultFontName() {
        int version = getFontVersion();
        String name = "系统字体";
//        if (version == 1) {
//            name = mContext.getString(R.string.read_fangzheng_lantinghei_font);
//        } else if (version == 2) {
//            name = mContext.getString(R.string.read_font_version2_name);
//        }
        return name;
    }

    /**
     * 字体解压保存目录
     *
     * @param sourceFile
     * @param indentityId
     * @return
     */
    public String getUnZipDestDir(String sourceFile, String indentityId) {
        String productDir = getFontProductDir(indentityId);

        int lastIdx = sourceFile.lastIndexOf(File.separator) + 1;
        int endIdx = sourceFile.lastIndexOf(".");
        String name = sourceFile.substring(lastIdx, endIdx);
        return productDir + File.separator + name;
    }

    public String getTTfDestFile(String sourceFile, String indentityId) {
        String zipDir = getUnZipDestDir(sourceFile, indentityId);
        return zipDir + File.separator + indentityId + ".ttf";
    }

    /**
     * 兼容老版路径：dangdang改为ddReader
     * @param sourceFile
     * @param indentityId
     * @return
     */
    /*public String getTTfDestFileOld(String sourceFile, String indentityId){

		int lastIdx = sourceFile.lastIndexOf(File.separator) + 1;
		int endIdx = sourceFile.lastIndexOf(".");
		String name = sourceFile.substring(lastIdx, endIdx);
		
		String zipDir = sourceFile.substring(0, lastIdx) + name;
		return zipDir + File.separator + indentityId + ".ttf";
	}*/

    /**
     * 字体zip保存的目标文件
     *
     * @param indentityId
     * @return
     */
    public File getFontSaveFile(String indentityId) {
        String productDir = getFontProductDir(indentityId);
        return DangdangFileManager.getFontDownloadSaveFile(productDir, indentityId);
    }

    /**
     * 单个字体的保存目录
     *
     * @param productId
     * @return
     */
    public String getFontProductDir(String productId) {
        String productDir = null;
        String username = mAccountManager.getDownloadUsername();
        //TODO 根据 productId 判断是否是免费字体，免费字体放在  FREE_FONT_USERNAME 下
        /*try {
            Integer.valueOf(productId);
		} catch (Exception e) {
//			e.printStackTrace();
			username = FREE_FONT_USERNAME;
		}*/
        if (!StringUtil.isNumeric(productId)) {
            username = FREE_FONT_USERNAME;
        }
        printLog("[  getFontProductDir  username=" + username + "]");

        productDir = DangdangFileManager.getFontProductDir(productId, username);
        return productDir;
    }

    public void setFreeFontDownFinish() {
        Editor editor = mSpfs.edit();
        editor.putBoolean(KEY_FREEFONT_DOWNFINISH, true);
        editor.commit();
    }

    public boolean isFreeFontDownFinish() {
        return mSpfs.getBoolean(KEY_FREEFONT_DOWNFINISH, false);
    }


    /**
     * 该字体是否是已经设置的默认字体
     *
     * @param productId
     * @return
     */
    public boolean isDefaultFont(String productId) {
        String fontFlag = getSharedPsByStr(KEY_CURRENT_FONT_FLAG);
        if (productId != null
                && productId.equals(fontFlag)) {
            return true;
        }
        return false;
    }

    public String getDefaultFontFlag() {
        String fontflag = getSharedPsByStr(KEY_CURRENT_FONT_FLAG);
        if (StringUtil.isEmpty(fontflag)) {
            return DEFAULT_PRODUCTID;
        }
        return fontflag;
    }

    /**
     * 保存默认字体的标识ID
     *
     * @param indentityId
     */
    public void setDefaultFont(String indentityId) {
        Editor editor = mSpfs.edit();
        editor.putString(KEY_CURRENT_FONT_FLAG, indentityId);
        editor.commit();
    }

    public void setDefaultFontPath(String path) {
        Editor editor = mSpfs.edit();
        editor.putString(KEY_CURRENT_FONT_PATH, path);
        editor.commit();
    }

    public void setDefaultFontName(String name) {
        Editor editor = mSpfs.edit();
        editor.putString(KEY_CURRENT_FONT_NAME, name);
        editor.commit();
    }

    public int getFontVersion() {
        return mSpfs.getInt(KEY_CURRENT_FONT_VERSION, 1);
    }

    public void setFontVersion(int version) {
        Editor editor = mSpfs.edit();
        editor.putInt(KEY_CURRENT_FONT_VERSION, version < 1 ? 1 : version);
        editor.commit();
    }

    /**
     * 获取默认字体ttf文件的绝对路径
     *
     * @return
     */
    public String getDefaultFontPath() {
        String path=getSharedPsByStr(KEY_CURRENT_FONT_PATH);
        if (path!=null||!path.equals("")) {
            File file = new File(path);
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                setDefaultFontName("标准");
                setDefaultFontPath("");
                path="";
            }
        }
        APPLog.e("getDefaultFontPath",path);
        return path;
    }

    public String getDefaultFontName() {
        String defaultName = getSharedPsByStr(KEY_CURRENT_FONT_NAME);
        if (StringUtil.isEmpty(defaultName)) {
            defaultName = FontListHandle.getHandle(mContext).getPresetDefaultFontName();
        }
        return defaultName;
    }

    private String getSharedPsByStr(String key) {
        if (mSpfs.contains(key)) {
            return mSpfs.getString(key, "");
        }
        return "";
    }

    public String getUserName() {
        String un = mAccountManager.getDownloadUsername();
        return TextUtils.isEmpty(un) ? FREE_FONT_USERNAME : un;
    }

    public boolean isFreeFontById(String pid) {
        return !StringUtil.isNumeric(pid);
    }

    public boolean ttfFileExists(String ttfPath) {
        return !TextUtils.isEmpty(ttfPath) && new File(ttfPath).exists();
    }

    private void printLog(String log) {
        logger.d(false, log);
    }

    public List<Font> getFontFileList() {
        DownloadDb mDownService = new DownloadDb(mContext.getApplicationContext());
        final String pubUser = FontListHandle.FREE_FONT_USERNAME;
        final String user = getUserName();
        final String status = Status.FINISH.getStatus();
        DType type = DType.FONT_CHARGE;
        List<FontDownload> downloadList = null;
        List<Font> fontList = new ArrayList<Font>();
        try {
            //字体购买
            downloadList = mDownService.getDownloadList(pubUser, user, status, type);
            for (FontDownload download : downloadList) {
                Font font = new Font();
                String ttfPath = getTTfDestFile(download.saveDir, download.indentityId);
                font.setFontPath(ttfPath);
                if (ttfPath.equals(getDefaultFontPath())) {
                    font.setDefault(true);
                }
                fontList.add(font);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        try {
            //免费字体
            type = DType.FONT_FREE;
            downloadList = mDownService.getDownloadList(pubUser, user, status, type);
            for (FontDownload download : downloadList) {
                Font font = new Font();
                String ttfPath = getTTfDestFile(download.saveDir, download.indentityId);
                font.setFontPath(ttfPath);
                if (ttfPath.equals(getDefaultFontPath())) {
                    font.setDefault(true);
                }
                fontList.add(font);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return fontList;
    }

    public List<FontDomain> getDownloadFontList() {
        List<FontDomain> ret = null;
        try {
            DownloadDb mDownService = new DownloadDb(mContext.getApplicationContext());
            final String pubUser = FontListHandle.FREE_FONT_USERNAME;
            final String user = getUserName();
            final String status = Status.FINISH.getStatus();
            DType type = DType.FONT_CHARGE;

            List<FontDownload> downloadList = mDownService.getDownloadList(pubUser, user, status, type);
            ret = conver(downloadList, mDownService);

            type = DType.FONT_FREE;
            downloadList = mDownService.getDownloadList(pubUser, user, status, type);
            List<FontDomain> temp = conver(downloadList, mDownService);
            ret.addAll(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public List<FontDomain> getDownloadFontList(DType type) {
        List<FontDomain> ret = null;
        try {
            DownloadDb mDownService = new DownloadDb(mContext.getApplicationContext());
            final String pubUser = FontListHandle.FREE_FONT_USERNAME;
            final String user = getUserName();
            final String status = Status.FINISH.getStatus();
            List<FontDownload> downloadList = mDownService.getDownloadList(pubUser, user, status, type);
            ret = conver(downloadList, mDownService);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
