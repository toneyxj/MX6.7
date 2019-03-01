package com.dangdang.reader.db.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dangdang.reader.db.DDStatisticsHelper;
import com.dangdang.zframework.log.LogM;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DDStatisticsService {

    public static String[] READ_BG_TYPE = {"white", "greet", "black", "kraft"};

    // shelf action
    // public static final String ShelfEbookNum = "shelfEbookNum";
    // public static final String ShelfCategoryNum = "shelfCategoryNum";*/
    public static final String ReadingTime = "readingTime";
    public static final String UserTime = "userTime";
    public static final String OpenToRead = "openToRead";
    // public static final String SingleMoveToCateGory = "singleMoveToClass";
    // public static final String BatchMoveToCateGory = "batchMoveToClass";
    /*
     * public static final String LocalSearch = "localSearch"; public static
	 * final String LocalTidyUp = "localTidyUp"; public static final String
	 * localCateGory = "localClass"; public static final String UnDownloadSearch
	 * = "notDownloadSearch"; public static final String UnDownloadCateGory =
	 * "notDownloadClass"; public static final String ADDBOOKTODESKTOP =
	 * "addBookToDesktop";
	 */
    public static final String SHAREFRIENDSBUTTON = "shareFriendsButton";
    public static final String SHAKEINSHELF = "waveInGroupSort";
    public static final String ARRANGESHELFINSHELF = "pushToClean";

    public static final String SEARCHINSHELFMENU = "searchShelfInFloat";
    public static final String ARRANGESHELFINSHELFMENU = "cleanBookInFloat";
    public static final String IMPORTINSHELFFLOAT = "transferBookInFloat";
    public static final String QUICKGROUPINSHELFMENU = "quickGroupInFloat";
    public static final String ORDERBYTIMEINSHELFMENU = "sortByTimeInFloat";
    public static final String ORDERBYGROUPINSHELFMENU = "sortByGroupInFloat";

    public static final String TOBOOKSTOREINSHELFMENU = "lookBookCityInFloatPlus";
    public static final String TOBUYBOOKINSHELFMENU = "addBuyBookInFloatPlus";
    public static final String IMPORTINSHELFMENU = "transferBookInFloatPlus";

    public static final String SEARCHINSEARCHPAGE = "hitSearchButton";
    public static final String RECOMMONDINSEARCHPAGE = "hitPresetKeyWord";

    public static final String STEALBOOKINLIST = "stealBookInList";
    public static final String STEALBOOKINTASHELF = "stealBookInTaShelf";

    public static final String UPLOADBYWIFI = "wifiImportBookCountSize";
    public static final String TXTCOUNT = "txtCount";
    public static final String PDFCOUNT = "pdfCount";
    public static final String EPUBTHIRDCOUNT = "epubThirdCount";
    public static final String TXTSIZE = "txtSize";
    public static final String PDFSIZE = "pdfSize";
    public static final String EPUBTHIRDSIZE = "epubThirdSize";

    // reader Action
    public static final String FIRST_OPEN_READER = "firstOpenReader";
    public static final String UNDOSLIPING = "undoSliping";
    public static final String GESTUREADJUSTBRIGHTNESS = "gestureAdjustBrightness";
    public static final String BRIGHTBUTTONADJUSTBRIGHTNESS = "brightButtonAdjustBrightness";
    public static final String BRIGHT_FOLLOW_SYSTEM = "brightFollowSystem";
    public static final String GESTUREGETDIRECTORY = "gestureGetDirectory";
    public static final String OTHER_SETTING_IN_READ = "otherSettingInRead";
    public static final String MEMUGETDIRECTORY = "memuGetDirectory";
    public static final String COPY_IN_READING = "copyInReading";
    public static final String DO_NOTE_IN_READING = "doNoteInReading";
    public static final String DO_LINE_IN_READING = "doLineInReading";
    public static final String ADD_LABLE_IN_READING = "addLableInReading";
    public static final String DEL_LABLE_IN_READING = "delLableInReading";
    public static final String FOOT_MARK_IN_READING = "footmarkInReading";
    public static final String COMMENT_IN_READING = "commentInReading";
    public static final String SHAREWEIBO = "shareWeibo";
    public static final String NIGHT_MODEL = "nightModel";
    public static final String APPEND_CART = "appendCart";
    public static final String OPEN_TO_READ = "openToRead";

    public static final String LEFTTORIGHTFLIPPAGE = "leftToRightFlipPage";
    public static final String UPTODOWNFLIPPAGE = "upToDownFlipPage";
    public static final String HITFLIPPAGE = "hitFlipPage";
    public static final String NOFLIPPAGE = "noFlipPage";
    public static final String FONTCHANGE = "fontChange";
    public static final String BACKGROUNDMODEL = "backgroudModel";
    public static final String LONGPRESSANDDRAG = "longPressAndDrag";
    public static final String SLIPING = "sliping";

    public static final String HITREADMARK = "hitReadMark";
    public static final String HITCIRCULARBOOKMARK = "hitCircularBookMark";
    public static final String FINISH_READ = "finishRead";

    public static final String DOWNLOAD_FREE_FONTS = "downloadFreeFonts";
    public static final String IS_DOWNLOAD_FREE_FONTS = "isDownloadFreeFonts";
    public static final String IS_DOWNLOAD = "isDownload";
    public static final String HIT_DICTIONARY = "hitDictionary";
    public static final String IS_HIT_DICTIONARY = "isHitDictionary";
    public static final String ADD_NOTE_IN_DICTIONARY = "addNoteInDictionary";
    public static final String RETURN_FROM_DICTIONARY = "returnFromDictionary";
    public static final String HIT_YOUDAO_DICTIONARY = "hitYoudaoDictionary";
    public static final String HIT_BAIDU_DICTIONARY = "hitBaiduDictionary";
    public static final String SEARCH_IN_READER = "searchInReader";
    public static final String SEARCH_BY_BAIDU = "searchByBaidu";
    public static final String SEARCH_BY_BING = "searchByBing";
    public static final String FLIP_PAGE_IN_READER = "flipPageInReader";
    public static final String SURRENT_VALUE = "surrentValue";
    public static final String CLASSICAL = "classical";
    public static final String SINGLE = "single";
    public static final String FULL_SCREEN = "fullScreen";
    public static final String OFF = "off";
    public static final String ON = "on";
    public static final String INDIVIDUAL_DICTIONARY = "individualDictionary";
    public static final String IS_INDIVIDUAL_DICTIONARY = "isIndividualDictionary";

    // pdf reader
    public static final String PDFADDLABLE = "pdfAddLable";
    public static final String FLIPPDFPAGE = "flipPdfPage";
    public static final String PDFCONTENT = "pdfContent";
    public static final String PDFCONTENTTAB = "pdfContentTab";
    public static final String DOUBLECLICKPDF = "doubleClickPdf";
    public static final String CUT_PDF = "cutPdf";
    public static final String PDF_ADD_LABLE = "pdfAddLable";
    public static final String PDF_CONTENT = "pdfContent";
    public static final String READ_PDF_MODEL = "readPdfModel";
    public static final String ACROSS = "across";
    public static final String VERTICAL = "vertical";

    // 云同步action
    public static final String MOBILEFLOWSYNC = "mobileFlowSyn";
    public static final String ISSYNCPROGRESS = "isSynCloudProgress";
    public static final String SYNC = "synchronization";

    // 添加 导入action
    // public static final String IMPORT = "import";
    public static final String QUICKSCAN = "quickScan";
    // public static final String FULLSCAN = "fullScan";
    public static final String LOCALBROWSE = "localBrowse";
    // public static final String TRANSFERBOOK = "transferBook";
    // public static final String INTELLIGENTSEARCH = "intelligentSearch";
    // public static final String UPONELEVEL = "upOneLevel";
    // public static final String ADDBOOKCASE = "addBookcase";

    // 书架 书城 和 设置 action
    public static final String BOOKSHELF = "hitMyBookcase";
    public static final String BOOKSTORE = "hitDangdangBookCity";
    public static final String SETTING = "hitSetup";

    // 个人中心action
    public static final String PERSONAL_CENTER = "personalCenter";
    public static final String PERSONAL_MESSAGE_CENTER = "messageCenter";
    public static final String PERSONAL_HISTORY = "experienceInPersonalCenter";
    public static final String PERSONAL_HISTORY_DATA = "myExperienceData";
    public static final String PERSONAL_NOTE = "noteInPersonalCenter";
    public static final String PERSONAL_NOTE_DETAIL = "detailNoteListing";
    public static final String PERSONAL_NOTE_DETAIL_READ = "readDetailNote";
    public static final String PERSONAL_FAVOR = "collectInPersonalCenter";
    public static final String PERSONAL_FAVOR_ADD = "addCollectInListing";
    public static final String PERSONAL_FAVOR_DEL = "cancelCollectInListing";
    public static final String PERSONAL_BUY = "buyInPersonalCenter";
    public static final String PERSONAL_BORROW = "borrowReadInPersonalCenter";
    public static final String PERSONAL_BOOK_REVIEW = "reamarksInPersonalCenter";
    public static final String PERSONAL_NEARBY = "nearbyInPersonalCenter";
    public static final String PERSONAL_GUESS_U_LIKE = "guessLikeInPersonalCenter";
    public static final String PERSONAL_FEEDBACK = "seeResponse";
    public static final String PERSONAL_FEEDBACK_SUBMIT = "publishResponse";
    public static final String PERSONAL_FEEDBACK_CALL = "customServicePhone";
    public static final String PERSONAL_GIFT = "giftInPersonalCenter";
    public static final String PERSONAL_GIFT_CARD_ACTIVE = "activeGiftCard";
    public static final String PERSONAL_GIFT_ELEC_ACTIVE = "activeGiftCertificate";
    public static final String PERSONAL_CHANGE_HEAD_PORTRAIT = "changeHeadPortrait";
    public static final String PERSONAL_SETTING = "settingInPersonalCenter";
    public static final String PERSONAL_SETTING_MSG_SEND = "messagePushInSetting";
    public static final String PERSONAL_SETTING_CLEAR_CACHE = "clearCacheInSetting";
    public static final String PERSONAL_SETTING_HELP = "helpInSetting";
    public static final String PERSONAL_SETTING_ABOUT = "aboutInSetting";
    public static final String PERSONAL_SETTING_UPDATE = "updateInSetting";
    public static final String PERSONAL_SETTING_RESET_GUIDE = "resetGuide";
    // 摇一摇
    public static final String PERSONAL_SHAKING_YAOYIYAO = "enterYaoYiYao";
    public static final String PERSONAL_SHAKING_LOGIN = "login";
    public static final String PERSONAL_SHAKING_GETBOOKLIST = "getBuyBookList";
    public static final String PERSONAL_SHAKING_GETVALIDCOUPON = "validCoupon";

    // 借阅action
    public static final String BORROWBOOKCLICK = "borrowBookClick";
    public static final String BORROWBOOKREADTIME = "borrowBookReadTime";
    public static final String BUYBORROWBOOKCLICKINSHELF = "readerBorrow";
    public static final String BUYBORROWBOOKCLICKINSEARCH = "readerBorrow";
    public static final String BUYBORROWBOOKCLICKINBORROW = "readerBorrow";
    public static final String BUYBORROWBOOKCLICKINREADING = "buyBorrowBookClickInReading";

    // 夜场action
    public static final String MALENIGHT = "maleNight";
    public static final String FEMALENIGHT = "femaleNight";
    public static final String BOOKLISTNIGHT = "bookList";

    // key
    public static final String EBookNum = "ebookNum";
    public static final String ShareStatus = "shareStatus";
    public static final String OPerateTime = "operateTime";
    public static final String Title = "title";
    public static final String LineationContent = "lineationContent";
    public static final String CategoryNum = "categoryNum";
    public static final String Length = "length";
    public static final String ProductId = "productId";
    public static final String OpenTime = "openTime";
    public static final String StartTime = "startTime";
    public static final String EndTime = "endTime";
    public static final String SEARCHKEY = "key";
    public static final String CategoryName = "categoryName";
    public static final String UserId = "userId";
    public static final String VerticalNum = "verticalNum";
    public static final String CrossNum = "crossNum";
    public static final String Content = "content";
    public static final String Lable = "lable";
    public static final String ReferType = "referType";
    public static final String EBookType = "ebookType";

    public static final String TYPE = "type";
    public static final String ContentType = "contentType";
    public static final String BRIGHTVALUE = "brightValue";
    public static final String FONT_VALUE = "fontValue";
    public static final String BACKGROUND = "backgroud";
    public static final String FONT_NAME = "fontName";
    public static final String FLIP_PAGE_BY_VOICE = "flipPageByVoice";
    public static final String SLAKE_SCREEN = "slakeScreen";
    public static final String FLIP_PAGE = "flipPage";

    public static final String PULL_DOWN_MARK = "pullDownMark";
    public static final String ACTION = "action";
    public static final String UPPER_MENU = "upperMenu";
    public static final String PDF_RESORT = "pdfReSort";

    /**
     * 第三方相关埋点
     */
    public static final String FROMTHIRD_JUMP = "jumpFromSeller";// 购物跳转

    public static final String USETYPE = "useType";
    public static final long Interval = 1 * 60 * 1000;

    public static final String HOME_TO_SHELF = "toShelfFromHome";
    public static final String HOME_TAB = "homeTab";
    public static final String STORE_TAB = "storeTab";
    public static final String SHELF_TAB = "shelfTab";
    public static final String FIND_TAB = "findTab";
    public static final String PERSONAL_TAB = "personalTab";
    public static final String PERSONAL_FOLLOW_BOOK = "personalFollowBook";
    public static final String CLOUD_GROUP = "cloudGroup";
    public static final String CHANNEL_SHARE = "channelShare";
    public static final String BOOKLIST_SHARE = "booklistShare";


    // 添加导入 key
    public static final String ADDNUM = "addnum";

    protected DDStatisticsHelper mDDStatisticsHelper;

    private Object lock = new Object();
    private Context mContext;
    private static DDStatisticsService mService;

    private List<String> mList = Collections
            .synchronizedList(new ArrayList<String>());

    private DDStatisticsService(Context context) {
        mContext = context;
        mDDStatisticsHelper = new DDStatisticsHelper(context);
    }

    public static synchronized DDStatisticsService getDDStatisticsService(Context context) {
        if (mService == null) {
            mService = new DDStatisticsService(context.getApplicationContext());
        }
        return mService;
    }

    /**
     * @param action
     * @param param  样例:action
     * @return
     */
    public boolean addData(String action, String... param) {
        try {
            JSONObject json = getJson(action, param);
            if (json != null) {
                insertData(json.toString());
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private JSONObject getJson(String action, String... param) {
        if (action == null || param == null || param.length == 0) {
            LogM.e("action == null or param == null or param.length == 0");
            return null;
        }
        JSONObject ob = new JSONObject();
        try {
            ob.put("action", action);
            String key = "";
            for (int i = 0; i < param.length; i++) {
                if (i % 2 == 0) {
                    key = param[i];
                    ob.put(param[i], "");
                } else
                    ob.put(key, param[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return ob;
    }

    public void insertData(String data) {
        mList.add(data);
        if (mList.size() > 10) {
            List<String> list = new ArrayList<String>();
            list.addAll(mList);
            mList.clear();
            insertData(list);
        }
    }

    public void pushData() {
        try {
            if (!mList.isEmpty()) {
                List<String> list = new ArrayList<String>();
                list.addAll(mList);
                mList.clear();
                insertData(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写数据库
     */
    protected void insertData(List<String> list) {
        synchronized (lock) {
            SQLiteDatabase db = mDDStatisticsHelper.getWritableDatabase();
            for (String str : list) {
                try {
                    String insert = "INSERT INTO "
                            + DDStatisticsHelper.DB_TABLE + " ("
                            + DDStatisticsHelper.KEY_UPLOAD + ", "
                            + DDStatisticsHelper.KEY_DATA + ") values (?,?)";

                    Object[] args = new Object[]{String.valueOf(0), str};
                    db.execSQL(insert, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            closeSqliteDb(db);
        }
    }

    public JSONArray getDataAndUpdateStatus(int status) {
        synchronized (lock) {
            JSONArray array = new JSONArray();
            SQLiteDatabase sqLiteDatabase = null;
            Cursor cursor = null;
            try {
                sqLiteDatabase = mDDStatisticsHelper.getWritableDatabase();
                String sql = "SELECT * FROM " + DDStatisticsHelper.DB_TABLE;
                cursor = sqLiteDatabase.rawQuery(sql, null);

                JSONObject ob = null;
                while (cursor.moveToNext()) {
                    String str = cursor.getString(cursor
                            .getColumnIndex(DDStatisticsHelper.KEY_DATA));
                    ob = new JSONObject(str);
                    array.put(ob);
                }
                updateStatus(sqLiteDatabase, status);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeCursor(cursor);
                closeSqliteDb(sqLiteDatabase);
            }
            return array;
        }
    }

    public void deleteUploaddata() {
        synchronized (lock) {
            SQLiteDatabase db = mDDStatisticsHelper.getWritableDatabase();
            try {
                String delete = " DELETE FROM " + DDStatisticsHelper.DB_TABLE
                        + " where " + DDStatisticsHelper.KEY_UPLOAD + " = ? ";
                db.execSQL(delete, new String[]{String.valueOf(1)});

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSqliteDb(db);
            }
        }
    }

    private void updateStatus(SQLiteDatabase db, int psatus) {
        String satus = "0";
        String motify = "1";
        if (psatus == 1) {
            satus = "1";
            motify = "0";
        }
        String sql = " update " + DDStatisticsHelper.DB_TABLE + " set "
                + DDStatisticsHelper.KEY_UPLOAD + " = ? where "
                + DDStatisticsHelper.KEY_UPLOAD + " = ? ";
        db.execSQL(sql,
                new String[]{String.valueOf(satus), String.valueOf(motify)});
    }

    /**
     * 开始上传psatus = 1 上传失败psatus = 0
     *
     * @param psatus
     */
    public void updateStatus(int psatus) {
        synchronized (lock) {
            SQLiteDatabase db = mDDStatisticsHelper.getWritableDatabase();
            try {
                updateStatus(db, psatus);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSqliteDb(db);
            }
        }
    }

    public void closeCursor(Cursor cursor) {
        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeSqliteDb(SQLiteDatabase db) {
        if (db != null) {
            try {
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    public void closeDbHelper() {
//        mDDStatisticsHelper.close();
//    }
//
//    public DDStatisticsHelper getmDDStatisticsHelper() {
//        return mDDStatisticsHelper;
//    }
//
//    public void setStatisticsDataToSp(DDStatisticsData data) {
//        Editor editor = new ConfigManager(mContext).getEditor();
//        if (data != null) {
//            editor.putString("ddsd", "ddsd");
//            editor.putString("ddsd_type", data.getType());
//            editor.putInt("ddsd_contentType", data.getContentType());
//            editor.putString("ddsd_from", data.getFrom());
//            editor.putString("ddsd_productId", data.getProductId());
//            editor.putString("ddsd_bookName", data.getBookName());
//            editor.putString("ddsd_note", data.getNote());
//            editor.putString("ddsd_lineationContent",
//                    data.getLineationContent());
//            editor.putString("ddsd_shareStatus", data.getShareStatus());
//            editor.putLong("ddsd_operateTime", data.getOperateTime());
//        } else {
//            editor.putString("ddsd", "");
//        }
//        editor.commit();
//    }

//    public DDStatisticsData getStatisticsDataFromSp() {
//        SharedPreferences sp = ConfigManager.getPreferences(mContext);
//        if (!TextUtils.isEmpty(sp.getString("ddsd", ""))) {
//            DDStatisticsData data = new DDStatisticsData(sp.getInt(
//                    "ddsd_contentType", 0));
//            data.setType(sp.getString("ddsd_type", ""));
//            data.setFrom(sp.getString("ddsd_from", ""));
//            data.setProductId(sp.getString("ddsd_productId", ""));
//            data.setBookName(sp.getString("ddsd_bookName", ""));
//            data.setNote(sp.getString("ddsd_note", ""));
//            data.setLineationContent(sp.getString("ddsd_lineationContent", ""));
//            data.setShareStatus(sp.getString("ddsd_shareStatus",
//                    DDStatisticsData.SHARE_STATUS_FAIL));
//            data.setOperateTime(sp.getLong("ddsd_operateTime", 0));
//            return data;
//        }
//        return null;
//    }

    /**
     * 将需要分享统计埋点数据插入数据库 如果分享统计埋点数据不为空则立即上传到服务器，提交失败则插入数据库，下次启动客户端会再上传，直到上传成功
     */
//    public void insertStatisticsData(boolean isShareSuccess) {
//        DDStatisticsData ddsd = getStatisticsDataFromSp();
//        if (ddsd != null) {
//            if (isShareSuccess) {
//                ddsd.setShareStatus(DDStatisticsData.SHARE_STATUS_SUCCESS);
//            } else {
//                ddsd.setShareStatus(DDStatisticsData.SHARE_STATUS_FAIL);
//            }
//
//            if (DDShareData.SHARE_TYPE_BOOK ==
//                    ddsd.getContentType()) {
//                JSONObject json = getJson(DDStatisticsService.SHAREWEIBO,
//                        DDStatisticsService.OPerateTime, ddsd.getOperateTime()
//                                + "", DDStatisticsService.TYPE, ddsd.getType(),
//                        DDStatisticsService.ShareStatus, ddsd.getShareStatus(),
//                        DDStatisticsService.ContentType, ddsd.getContentType() + "",
//                        DDStatisticsService.ProductId, ddsd.getProductId(),
//                        DDStatisticsService.Title, ddsd.getBookName());
//                JSONArray array = new JSONArray();
//                array.put(json);
//                submitStatistics(array);
//            } else if (DDShareData.SHARE_TYPE_LINE == ddsd
//                    .getContentType()) {
//                JSONObject json = getJson(DDStatisticsService.SHAREWEIBO,
//                        DDStatisticsService.OPerateTime, ddsd.getOperateTime()
//                                + "", DDStatisticsService.TYPE, ddsd.getType(),
//                        DDStatisticsService.ShareStatus, ddsd.getShareStatus(),
//                        DDStatisticsService.ContentType, ddsd.getContentType() + "",
//                        DDStatisticsService.ProductId, ddsd.getProductId(),
//                        DDStatisticsService.Title, ddsd.getBookName(),
//                        DDStatisticsService.LineationContent,
//                        ddsd.getLineationContent());
//                JSONArray array = new JSONArray();
//                array.put(json);
//                submitStatistics(array);
//            } else {
//                JSONObject json = getJson(DDStatisticsService.SHAREWEIBO,
//                        DDStatisticsService.OPerateTime, ddsd.getOperateTime()
//                                + "", DDStatisticsService.TYPE, ddsd.getType(),
//                        DDStatisticsService.ShareStatus, ddsd.getShareStatus(),
//                        DDStatisticsService.ContentType, ddsd.getContentType() + "");
//                JSONArray array = new JSONArray();
//                array.put(json);
//                submitStatistics(array);
//            }
//        }
//    }

//    private void submitStatistics(JSONArray array) {
//        /*SubmitStatistics submitStatistics = new SubmitStatistics(this);
//        submitStatistics.sendArray(array, false);*/
//    }
}
