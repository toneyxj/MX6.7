package com.dangdang.reader.dread.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Build;

import com.dangdang.reader.DDApplication;
import com.dangdang.reader.R;
import com.dangdang.reader.dread.core.base.IReaderController.DAnimType;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.data.Font;
import com.dangdang.reader.dread.font.FontListHandle;
import com.dangdang.reader.dread.holder.GlobalResource;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.reader.utils.Utils;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;
import com.dangdang.zframework.utils.StringUtil;
import com.mx.mxbase.constant.APPLog;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 保存和获取阅读配置信息 当前页数，字体，字号，样式（css），背景，动画类型，护眼时间，音量翻页开关等等
 *
 * @author
 */
public class ReadConfig {
    /**
     * 全屏显示
     */
    public static boolean AllScreen=false;
    private static ReadConfig mInstance = null;

    public static final String Key_LineWordNum = "k_linewordnum";
    // public static final String Key_ReadPageIndex = "k_readpageindex";
    public static final String Key_Night = "k_night";
    public static final String Key_Light_System = "k_light_system";

    public static final String READER_LIGHT_CONTROL = "reader_light_control";
    public static final String READER_LIGHT_TIME = "reader_light_time";
    public static final String READER_GUIDE_NUM_FILE = "reader_guide_num_file";
    public static final String READER_GUIDE_NUM_VARIABLE = "reader_guide_num_variable";

    public static final String READER_MARK_GUIDE_FILE = "reader_mark_guide_file";
    public static final String READER_MARK_GUIDE_VARIABLE = "reader_mark_guide_variable";

    public static final String READER_FIRST_MARK_FILE = "reader_first_mark_file";
    public static final String READER_FIRST_MARK_VARIABLE = "reader_first_mark_variable";

    public static final String READER_DOUBLE_FINGER_GUIDE_FILE = "reader_double_finger_guide_file";
    public static final String READER_DOUBLE_FINGER_GUIDE_VARIABLE = "reader_double_finger_guide_variable";

    public static final String READER_DOUBLE_FINGER_LIGHT_GUIDE_FILE = "reader_double_finger_light_guide_file";
    public static final String READER_DOUBLE_FINGER_LIGHT_GUIDE_VARIABLE = "reader_double_finger_light_guide_variable";

    public static final String READER_DICT_XDB = "reader_dict_xdb";
    public static final String READER_DICT_RULES = "reader_dict_rules";
    public static final String READER_PREREAD_PATH = "reader_preread_path";
    public static final String READER_IS_OPPEN_ERROR = "reader_is_open_error";
    public static final String READER_IS_NORMAL_EXIT = "reader_is_normal_exit";
    public static final String READER_NOT_NORMAL_EXIT_BOOK_ID = "reader_not_normal_book_id";
    public static final String READER_OFFPRINT_PROGRESS = "reader_offprint_progress";

    public static final String OFFPRINT_PATH = "offprint_path";
    public static final String PreSet_OffPrint_ProductId = "1960071903";

    public static final boolean bPart = true;

    public static final String bookName = "";
    /**
     * 剪裁对称类型：没有对称
     */
    public static final int PDF_NON_SYMMETRY = 0;
    /**
     * 剪裁对称类型：奇数页(左上角)
     */
    public static final int PDF_ODD_SYMMETRY = 1;
    /**
     * 剪裁对称类型：偶数页(右上角)
     */
    public static final int PDF_EVEN_SYMMETRY = 2;

    public final static String SystemFont = "/system/fonts/DroidSansFallback.ttf";
    public final static String SystemFont0 = "/system/fonts/OnyxCustomFont-Regular.ttf";
    public final static String SystemFont2 = "/system/fonts/NotoSansHans-Regular.otf";
    public final static String SystemFont3 = "/system/fonts/NotoSansCJKsc-Regular.otf";
    public final static String SystemFontEn = "/system/fonts/DroidSans.ttf";
    public final static String SystemFontPath = "/system/fonts";

    public static final String PDF_FONT_PATH = "/system/fonts";

    // 屏幕亮度
    public static final String PREF_KEY_FIRST = "pref_first";
    public static final String PREF_KEY_LIGHT = "pref_light";
    public static final String PREF_KEY_LIGHT_NIGHT = "pref_light_night";

    public static final float ZERO_LIGHT = 0.0f;
    public static final float MIN_LIGHT = 0.05f;
    public static final float DEFAULT_LIGHT = 0.5f;
    public static final float DEFAULT_NIGHT_LIGHT = 0.1f;
    public static final float MAX_LIGHT = 1.0f;

    public static final int READER_LIGHT_INTERVAL2 = 2 * 60 * 1000;
    public static final int READER_LIGHT_INTERVAL5 = 5 * 60 * 1000;
    public static final int READER_LIGHT_INTERVAL10 = 10 * 60 * 1000;
    public static final int READER_LIGHT_INTERVAL_FOREVER = -1;

    public static String[] ANIMATION_SHOW = new String[3];
    public static final String[] ANIMATION_TYPE = new String[]{"shift",
            "slide", "none"};

    /**
     * 4.0 reader background color
     */
    public static final String KEY_READER_BG_DAY = "k_reader_bg_day";
    public static final String KEY_READER_BG_NIGHT = "k_reader_bg_night";

    /**
     * 图片背景和对应色值
     */
    public static final int COLOR_GRAY_TEXTURE = 0xe4e5e5;
    public static final String GRAY_TEXTURE_FILE_NAME = "gray_texture_bg.png";

    public static final int COLOR_BROWN_TEXTURE = 0xc7bba9;
    public static final int COLOR_PARCHMENT = 0xc7bbaa;
    // public static final String BROWN_TEXTURE_FILE_NAME =
    // "brown_texture_bg.png";
    public static boolean isFirstLongClick=true;
    /**
     * 阅读内容背景 白天
     */
    public static final Integer[] READER_BG_COLOR_DAY = new Integer[]{
            0xFFFFFFFF, 0xFFE9E3DA, 0xFFD1DCD2, 0xFFD7E0E8, 0xFFEECDCD,
            COLOR_GRAY_TEXTURE, COLOR_BROWN_TEXTURE, COLOR_PARCHMENT};
    /**
     * 阅读内容背景 夜晚
     */
    public static final Integer[] READER_BG_COLOR_NIGHT = new Integer[]{
            0xFF000000, 0xFF000000, 0xFF2F4F4A, 0xFF394456};

    /**
     * 阅读内目录及阅读结束页背景 白天
     */
    public static final Integer[] OTHER_BG_COLOR_DAY = new Integer[]{
            0xFFFFFFFF, 0xFFF7E3CE, 0xFFC5DFCE, 0xFFcedfef, 0xFFffdfff,
            0xFFF7F7F7, 0xFFF4EFDF, COLOR_PARCHMENT};

    /**
     * 阅读内目录及阅读结束页背景 夜晚
     */
    public static final Integer[] OTHER_BG_COLOR_NIGHT = new Integer[]{
            0xFF292829, 0xFF000000, 0xFF294d4a, 0xFF394552};

    private int mReaderBgColorDay;
    private int mReaderBgColorNight;

    public static final String KEY_READER_NOTE_DRAWLINE_COLOR = "k_reader_note_drawline_color";
    //0xffe5609c
    public static final Integer[] NOTE_DRAWLINE_COLOR = new Integer[]{
            0xff000000 /*RED*/, 0xff000000 /*YELLOW*/, 0xff000000 /*GREEN*/, 0xff000000 /*BLUE*/, 0xff000000 /*PINK*/};

    private int mReaderNoteDrawLineColor;

    public static final String KEY_READER_VOL_FILP = "k_reader_vol_flip";
    public static final String KEY_READER_FONT_HINT = "k_reader_font_hint";
    public static final String KEY_READER_FONT_DOWNLOAD = "k_reader_font_download";

    public static final String KEY_READER_ISFULL = "k_reader_isfull";

    public static final String KEY_READER_PAGETURN_MODE = "k_reader_pageturn_mode";

    public static final String KEY_READER_PADDING_LEFTANDRIGHT = "k_reader_paddingleft_right";

    public static final String KEY_READER_LINESPACING = "k_reader_linespacing";

    public static final String KEY_READER_PARAGRAPHSPACING = "k_reader_paragraphspacing";

    public static final String KEY_READER_MINLINEWORD = "K_reader_minlineword";

    public static final String KEY_READER_MAXLINEWORD = "k_reader_maxlineword";
    public static final String READER_PDF_FIRST = "reader_pdf_firstread";

    public static final String KEY_READER_CHINESE_CONVERT = "k_reader_chinese_convert";
    /**
     * 7寸以下
     */
    private static final int MinLineWordNum_Default = 8;
    private static final int MaxLineWordNum_Default = 24;
    private static final int DefaultLineWordNum_Default = 14;

    /**
     * >= 7寸
     */
    private static final int MinLineWordNumPad_Default = 18;
    private static final int MaxLineWordNumPad_Default = 34;
    private static final int DefaultLineWordNumPad_Default = 26;

    public static final int LineWordOneStepNum = 2;

    /**
     * 双指调节亮度默认距离
     */
    public static final int MaxTwoPrtLightDistance = 50;

    /**
     * 响应双指操作默认距离
     */
    public static final int MaxTwoPrtDistance = 250;

    /**
     * 行间距调整默认步长
     */
    public static final float LINESPACING_STEP_DEFAULT = 0.1f;

    /**
     * 页边距(左右)调整默认步长
     */
    public static final float PADDING_LEFT_RIGHT_STEP_DEFAULT = 10;

    public static final float MIN_PADDING_LEFT_RIGHT = (int) (1.5 * PADDING_LEFT_RIGHT_STEP_DEFAULT);

    /**
     * 默认行间距
     */
    public static final float MIN_LINESPACING = 0f;

    public static final float MAX_LINESPACING = 5f;
    public static final float LINESPACING_DEFAULT_S = 0.7F;
    public static final float LINESPACING_DEFAULT_M = 1.0F;
    public static final float LINESPACING_DEFAULT_L = 1.2F;
    public static final float LINESPACING_DEFAULT_X = 1.5F;
    /**
     * 默认段间距
     */
    public static final float PARAGRAPHSPACING_DEFAULT = 1.0F;// ParagraphSpacing

    public static final float MIN_PARAGRAPHSPACING = PARAGRAPHSPACING_DEFAULT - 0.2f;

    private Context mContext;
    private SharedPreferences mShaprdPre;
    // private int mScreenWidth;
    // private int mScreenHeight;
    private float mDensity = 1f;
    // private float fontSize;
    private float mPaddingLeftOrRight = 0;
    private boolean mNightMode = false;

    private DAnimType mDAnimType = DAnimType.None;
    private int mLightInterval = READER_LIGHT_INTERVAL2;

    private String mSystemFont;

    private ReadConfig() {

    }

    public static synchronized ReadConfig getConfig() {

        if (mInstance == null) {
            mInstance = new ReadConfig();
        }
        return mInstance;
    }

    /**
     * pdf奇偶对称（当前为奇数）
     *
     * @param symmetryType
     * @return
     */
    public static boolean isOddSymmetry(int symmetryType) {
        return symmetryType == PDF_ODD_SYMMETRY;
    }

    /**
     * pdf奇对称（当前为偶数）
     *
     * @param symmetryType
     * @return
     */
    public static boolean isEvenSymmetry(int symmetryType) {
        return symmetryType == PDF_EVEN_SYMMETRY;
    }

    public static boolean isSymmetry(int symmetryType) {
        return isOddSymmetry(symmetryType) || isEvenSymmetry(symmetryType);
    }

    // @SuppressWarnings("static-access")
    public void initContext(Context context) {
        context = context.getApplicationContext();
        this.mContext = context;
        // initValue();
    }
    private int scroolIndex=0;

    public boolean getScroolIndex() {
        if (scroolIndex>=8){
            scroolIndex=0;
            return true;
        }
        return false;
    }

    public void setAddScroolIndex() {
        this.scroolIndex++;
    }

    public void initValue() {
        // DRUiUtility ui = DRUiUtility.getUiUtilityInstance();
        // int width = ui.getScreenWith();
        // int height = ui.getScreenHeight();

        // this.mScreenWidth = width;
        // this.mScreenHeight = height;
        this.mDensity = DRUiUtility.getDensity();
        reSetPaddingLeft();

        initAnimation();
        initLightInterval();
        initNightMode();

        mShaprdPre = getSharedPre();
        mReaderBgColorDay = mShaprdPre.getInt(KEY_READER_BG_DAY,
                READER_BG_COLOR_DAY[0]);
        mReaderBgColorNight = mShaprdPre.getInt(KEY_READER_BG_NIGHT,
                READER_BG_COLOR_NIGHT[0]);

        mReaderNoteDrawLineColor = mShaprdPre.getInt(KEY_READER_NOTE_DRAWLINE_COLOR,
                BookNote.NOTE_DRAWLINE_COLOR_RED);
        mLightInterval = getLightInterval();
    }

    public int getTwoPtrLightDistance() {
        return (int) (mDensity * MaxTwoPrtLightDistance);
    }

    public int getTwoPtrDistance() {
        return (int) (mDensity * MaxTwoPrtDistance);
    }

    private void reSetPaddingLeft() {
        mPaddingLeftOrRight = getSharedPre()
                .getFloat(KEY_READER_PADDING_LEFTANDRIGHT,
                        getDefaultPaddingLeftOrRight());
    }

    public int getDefaultPaddingLeftOrRight() {
        return (int) (22 * mDensity);
    }

    public float getPaddingLeftOrRightFromIndex(int index) {
        return 22 * (index + 1);
    }

    public int getPaddingLeftOrRightToIndex() {
        return (int) ((mPaddingLeftOrRight / 22) - 1);
    }

    private void initAnimation() {
        ANIMATION_SHOW[0] = mContext.getResources().getString(
                R.string.animation_shift);
        ANIMATION_SHOW[1] = mContext.getResources().getString(
                R.string.animation_slide);
        ANIMATION_SHOW[2] = mContext.getResources().getString(
                R.string.animation_none);

        mDAnimType = initAnimType();
    }

    /**
     * 获得文字大小级数
     *
     * @return
     */
    public int getFontIndex() {
        int lineWordNum = getLineWordNum(false);
        return ((MaxLineWordNumPad_Default - lineWordNum) / LineWordOneStepNum);
    }

    public int getFontNumber(int index) {
        return MaxLineWordNumPad_Default - (index * LineWordOneStepNum);
    }

    public int getLineSpacingIndex() {
        float spacing = getLineSpacing();
        int index = 0;
        if (spacing == LINESPACING_DEFAULT_S) {
            index = 0;
        } else if (spacing == LINESPACING_DEFAULT_M) {
            index = 1;
        } else if (spacing == LINESPACING_DEFAULT_L) {
            index = 2;
        }
        return index;
    }

    /**
     * 获得进度显示方式 0，进度百分比，1阅读页数，2跳转页数，3无
     *
     * @return
     */
    public int getProgress() {
        SharedPreferences pre = mContext.getSharedPreferences(
                "xj_progress", Context.MODE_PRIVATE);
        int type = pre.getInt("progress", 0);
        return type;
    }

    /**
     * 获得屏幕的横竖屏
     *
     * @return 0竖屏，1横屏
     */
    public int getScreen() {
        SharedPreferences pre = mContext.getSharedPreferences(
                "xj_screen", Context.MODE_PRIVATE);
        int type = pre.getInt("screen", 0);
        return type;
    }

    /**
     * 设置进度显示方式
     *
     * @param index 0:进度百分比，1：阅读页数，2：跳转页数，3无
     */
    public void setProgress(int index) {
        SharedPreferences pre = mContext.getSharedPreferences("xj_progress",
                Context.MODE_PRIVATE);
        Editor edit = pre.edit();
        edit.putInt("progress", index);
        edit.commit();

        mDAnimType = initAnimType();
    }

    /**
     * 设置屏幕的横竖屏
     *
     * @param index
     */
    public void setScreen(int index) {
        SharedPreferences pre = mContext.getSharedPreferences("xj_screen",
                Context.MODE_PRIVATE);
        Editor edit = pre.edit();
        edit.putInt("screen", index);
        edit.commit();
    }

    public DAnimType initAnimType() {
        try {
            SharedPreferences pre = mContext.getSharedPreferences(
                    "animation_type", Context.MODE_PRIVATE);
            String type = pre.getString("type", ANIMATION_TYPE[2]);
            if (type.equals(ANIMATION_TYPE[0])) {
                return DAnimType.Shift;
            } else if (type.equals(ANIMATION_TYPE[1])) {
                return DAnimType.Slide;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DAnimType.None;
    }

    public DAnimType getAnimationTypeNew() {
        return mDAnimType;
    }

    public void setAnimationType(String str) {
        SharedPreferences pre = mContext.getSharedPreferences("animation_type",
                Context.MODE_PRIVATE);
        Editor edit = pre.edit();
        edit.putString("type", str);
        edit.commit();

        mDAnimType = initAnimType();
    }

    public int getReaderBgColorDay() {
        return mReaderBgColorDay;
    }

    public void setReaderBgColorDay(int color) {
        mReaderBgColorDay = color;
        SharedPreferences pre = getSharedPre();
        Editor edit = pre.edit();
        edit.putInt(KEY_READER_BG_DAY, color);
        edit.commit();
    }

    public int getReaderBgColorNight() {
        return mReaderBgColorNight;
    }

    public void setReaderBgColorNight(int color) {
        mReaderBgColorNight = color;
        SharedPreferences pre = getSharedPre();
        Editor edit = pre.edit();
        edit.putInt(KEY_READER_BG_NIGHT, color);
        edit.commit();
    }

    public int getReaderBgColor() {
        APPLog.e("ReaderBgColor",String.valueOf(mReaderBgColorDay));
        if (isNightMode()) {
            return mReaderBgColorNight;
        } else {
            return mReaderBgColorDay;
        }
    }

    public int getReaderForeColor() {
        if (isNightMode()) {
            return 0xD1D1D1D1 - mReaderBgColorNight;
        } else {
            return 0xFFFFFFFF - mReaderBgColorDay;
        }
    }

    public boolean isDefaultBg(int bgColor) {
        return READER_BG_COLOR_DAY[0] == bgColor;
    }

    public boolean isImgBg() {
        if (isNightMode()) {
            return false;
        } else {
            return mReaderBgColorDay == COLOR_BROWN_TEXTURE
                    || mReaderBgColorDay == COLOR_GRAY_TEXTURE
                    || mReaderBgColorDay == COLOR_PARCHMENT;
        }
    }

    public Bitmap getImgBg() {
        Bitmap imgBp = null;
        if (mReaderBgColorDay == COLOR_BROWN_TEXTURE) {
            imgBp = GlobalResource.getBrownTextureBitmap();
        } else if (mReaderBgColorDay == COLOR_GRAY_TEXTURE) {
            imgBp = GlobalResource.getGrayTextureBitmap();
        } else if (mReaderBgColorDay == COLOR_PARCHMENT) {
            imgBp = GlobalResource.getmParchmentBitmap();
        }
        return imgBp;
    }

    /**
     * 阅读View背景
     *
     * @return
     */
    public int getReaderOtherBgColor() {
        int otherBg = OTHER_BG_COLOR_DAY[0];
        if (isNightMode()) {
            try {
                int index = getIndexOfArray(READER_BG_COLOR_NIGHT,
                        mReaderBgColorNight);
                otherBg = OTHER_BG_COLOR_NIGHT[index];
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                int index = getIndexOfArray(READER_BG_COLOR_DAY,
                        mReaderBgColorDay);
                otherBg = OTHER_BG_COLOR_DAY[index];
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        APPLog.e("getReaderOtherBgColor",otherBg);
        return otherBg;
    }

    public int getNoteDrawLineColor() {
        return mReaderNoteDrawLineColor;
    }

    public void setNoteDrawlineColor(int color) {
        mReaderNoteDrawLineColor = color;
        SharedPreferences pre = getSharedPre();
        Editor edit = pre.edit();
        edit.putInt(KEY_READER_NOTE_DRAWLINE_COLOR, color);
        edit.commit();
    }

    private int getIndexOfArray(Integer[] arrs, int element) {
        int index = 0;
        for (int i = 0; i < arrs.length; i++) {
            if (arrs[i] == element) {
                index = i;
                break;
            }
        }
        return index;
    }

	/*
     * public ParserParam getParserParam(){
	 * 
	 * reSetMarginLeft();
	 * 
	 * final int width = getReadWidth(); final int height = getReadHeight();
	 * 
	 * ParserParam pParam = new ParserParam();
	 * 
	 * pParam.fontSize = getFontSize(); pParam.marginLeft = getMarginLeft();
	 * pParam.marginTop = getMarginTop(); pParam.marginBottom =
	 * getMarginButtom(); pParam.lineWordNum = getLineWordNum(); pParam.mWidth =
	 * width; pParam.mHeight = height;
	 * 
	 * //pParam.fontPath = getFontPath();
	 * 
	 * return pParam; }
	 */

    public ComposingFactor getComposingFactor(Context context) {
        ComposingFactor factor = new ComposingFactor();
        getComposingFactorInner(factor, context);
        return factor;
    }

    protected void getComposingFactorInner(ComposingFactor factor, Context context) {
        reSetPaddingLeft();
        float fontSize = getFontSize();// 只有先计算fontsize才有paddingLeft和right

        factor.setWidth(getReadWidth());
        factor.setHeight(getReadHeight());
        factor.setPaddingLeft(getPaddingLeft());
        factor.setPaddingTop(getPaddingTop(context));
        factor.setPaddintRight(getPaddingLeft());// left==right
        factor.setPaddingBottom(getPaddingButtom());
        factor.setLineSpacing(getLineSpacing());
        factor.setParagraphSpacing(getParagraphSpacing());
        factor.setFontSize(fontSize);
        factor.setLineWord(getLineWordNum());
        factor.setFirstLineIndent(getFirstLineIndent());
        factor.setFont(getFontPath());
    }

    /**
     * @return PagePadding
     */
    public PagePadding getPaddingRect(Context context) {

        initPadding();

        float paddingLeft = getPaddingLeft();
        float paddingRight = paddingLeft;
        float paddingTop = getPaddingTop(context);
        float paddingBottom = getPaddingButtom();

        PagePadding padding = new PagePadding();
        padding.setPaddingLeft(paddingLeft);
        padding.setPaddingTop(paddingTop);
        padding.setPaddingRight(paddingRight);
        padding.setPaddingBottom(paddingBottom);

        return padding;
    }

    public PagePadding getDefaultPaddingRect(Context context) {

        PagePadding padding = new PagePadding();

        float paddingLeft = getDefaultPaddingLeftOrRight();
        float paddingRight = paddingLeft;

        padding.setPaddingLeft(paddingLeft);
        padding.setPaddingTop(getPaddingTop(context));
        padding.setPaddingRight(paddingRight);
        padding.setPaddingBottom(getPaddingButtom());

        return padding;
    }

    private void initPadding() {
        getFontSize();
    }

    public boolean isScreenNonChange = true;

    /**
     * 可排版区域-宽
     *
     * @return
     */
    public int getReadWidth() {
        int readWidth;
        if (getScreen() == 1) {//横屏
            readWidth = DRUiUtility.getScreenHeight() + Utils.dip2px(mContext, 45);
            if (readWidth<1200)readWidth=DRUiUtility.getScreenWith();
        } else {
            readWidth = DRUiUtility.getScreenWith();
        }
        APPLog.e("drawSizereadWidth", readWidth);
        return readWidth;
    }

    /**
     * 可排版区域-高
     *
     * @return
     */
    public int getReadHeight() {
        int readHeight = 0;
        if (getScreen() == 1) {//横屏
            readHeight = DRUiUtility.getScreenWith() - Utils.dip2px(mContext, 45);
            if (readHeight>1200)readHeight=DRUiUtility.getScreenHeight();
        } else {
            readHeight = DRUiUtility.getScreenHeight();
        }
//        if (!isFullScreen()) {
//            readHeight = readHeight - DRUiUtility.getStatusHeight(mContext);
////            if (getProgress() != 3) {
////                readHeight += Utils.dip2px(mContext, 50);
////            } else {
////                readHeight += Utils.dip2px(mContext, 65);
////            }
//            if (hasSmartBar()) {
//                readHeight = readHeight - Utils.dip2px(mContext, 48);
//            }
//        }
        readHeight+=DRUiUtility.getStatusHeight(mContext);
        if (getProgress() != 3) {
            readHeight -= Utils.dip2px(mContext, 40);
        }
//        if (AllScreen){
//            readHeight+=DRUiUtility.getStatusHeight(mContext);
//        }
        APPLog.e("drawSizereadHeight", readHeight);
        return readHeight;
    }

    private boolean hasSmartBar() {
        try {
            // 新型号可用反射调用Build.hasSmartBar()
            Method method = Class.forName("android.os.Build").getMethod(
                    "hasSmartBar");
            return ((Boolean) method.invoke(null)).booleanValue();
        } catch (Exception e) {
        }
        // 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
        if (Build.DEVICE.equals("mx2")) {
            return true;
        } else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
            return false;
        }
        return false;
    }

    /**
     * TODO 只有先计算fontsize才有paddingLeft和right
     *
     * @return
     */
    public float getFontSize() {
        final int mScreenWidth = getReadWidth();
        // TODO
        float fsize = 16;
        float mLeft = getPaddingLeft();
        float pageWidth = mScreenWidth - 2 * mLeft;
        int lineWordNum = getLineWordNum();

        int m = (int) (pageWidth % lineWordNum);
        if (m == 0) {
            fsize = pageWidth / lineWordNum;
        } else {
            if (m <= (mLeft / 2)) {
                fsize = (pageWidth - m) / lineWordNum;
                mLeft = m / 2f + mLeft;
            } else {
                int needValue = lineWordNum - m;

                fsize = (pageWidth + needValue) / lineWordNum;
                mLeft = mLeft - needValue / 2f;
            }
            setPaddingLeftOrRight(mLeft);
        }
        return fsize;
    }

    public float onlyFontSize() {
        final int mScreenWidth = getReadWidth();
        // TODO
        float fsize = 16;
        float mLeft = getPaddingLeft();
        float pageWidth = mScreenWidth - 2 * mLeft;
        int lineWordNum = getLineWordNum();

        int m = (int) (pageWidth % lineWordNum);
        if (m == 0) {
            fsize = pageWidth / lineWordNum;
        } else {
            if (m <= (mLeft / 2)) {
                fsize = (pageWidth - m) / lineWordNum;
                mLeft = m / 2f + mLeft;
            } else {
                int needValue = lineWordNum - m;
                fsize = (pageWidth + needValue) / lineWordNum;
                mLeft = mLeft - needValue / 2f;
            }
        }
        return fsize;
    }

    /**
     * right = left
     *
     * @return
     */
    public float getPaddingLeft() {

        return mPaddingLeftOrRight;
    }

    /**
     * bottom = top
     *
     * @return
     */
    public float getPaddingTop(Context context) {

//		DRUiUtility.getUiUtilityInstance();
//		float top = DRUiUtility.getStatusHeight(context);
//		top = (int) (top * 1.5);
        int top = (int) (mDensity * 46.5);
        return top;
    }

    public float getPaddingButtom() {
		/*
		 * float buttom = DRUiUtility.getUiUtilityInstance().getStatusHeight();
		 * buttom = (int) (0.5 * buttom);
		 */
        int buttom = (int) (mDensity * 46.5);
        return buttom;
    }

    public float getLineSpacing() {
        SharedPreferences spf = getSharedPre();
        return spf.getFloat(KEY_READER_LINESPACING, LINESPACING_DEFAULT_M);
    }

    public int getLineSpacingToPx(float lineSpacing) {
        return (int) (getFontSize() * (lineSpacing - 0.3));
    }

    public void saveLineSpacing(float lineSpacing) {
        SharedPreferences spf = getSharedPre();
        Editor editor = spf.edit();
        editor.putFloat(KEY_READER_LINESPACING, lineSpacing);
        editor.commit();
    }

    public float getParagraphSpacing() {
        SharedPreferences spf = getSharedPre();
        return spf.getFloat(KEY_READER_PARAGRAPHSPACING,
                PARAGRAPHSPACING_DEFAULT);
    }

    public void saveParagraphSpacing(float paragraphSpacing) {
        SharedPreferences spf = getSharedPre();
        Editor editor = spf.edit();
        editor.putFloat(KEY_READER_PARAGRAPHSPACING, paragraphSpacing);
        editor.commit();
    }

    public void savePaddingLeftOrRight(float leftOrRight) {
        SharedPreferences spf = getSharedPre();
        Editor editor = spf.edit();
        editor.putFloat(KEY_READER_PADDING_LEFTANDRIGHT, leftOrRight);
        editor.commit();
        setPaddingLeftOrRight(leftOrRight);
    }

    public float getFirstLineIndent() {
        return 2;
    }
    public int getLineWordNum(boolean is){

        int lineWordNum = getDefaultLineWord();
        SharedPreferences pre = getSharedPre();
        lineWordNum = pre.getInt(Key_LineWordNum, lineWordNum);
        if (getScreen() == 1&&is) {
            //横屏
            float max = getReadWidth() / (getReadHeight() * 1f);
            lineWordNum = (int) (lineWordNum * max);
        }
        return lineWordNum;
    }

    public int getLineWordNum() {
        return getLineWordNum(true);
    }

    public float getLineSpacingStep() {
        return LINESPACING_STEP_DEFAULT;
    }

    public int getPaddingLeftOrRightStep() {
        return (int) (mDensity * PADDING_LEFT_RIGHT_STEP_DEFAULT);
    }

    public float getMinLineSpacing() {
        return MIN_LINESPACING;
    }

    public float getMaxLineSpacing() {
        return MAX_LINESPACING;
    }

    public float getMinParagraphSpacing() {
        return MIN_PARAGRAPHSPACING;
    }

    public float getMinPaddingLeftOrRight() {
        return (mDensity * MIN_PADDING_LEFT_RIGHT);
    }

    public float getMaxPaddingLeftOrRight() {
        return getReadWidth() / 2 - 6 * getMinPaddingLeftOrRight();
    }

    public String getFontPath() {

        // String path = FileUtil.getFontPath();
        FontListHandle fontHandle = FontListHandle.getHandle(mContext);
        String fontPath = fontHandle.getDefaultFontPath();

        if (!new File(fontPath).exists()) {
            fontPath = SystemFont0;
        }
        if (!new File(fontPath).exists()) {
            fontPath = SystemFont;
        }
        if (!new File(fontPath).exists()) {
            fontPath = SystemFont2;
        }
        if (!new File(fontPath).exists()) {
            fontPath = SystemFont3;
        }
        if (!new File(fontPath).exists()) {
            if (mSystemFont == null)
                findSystemFont();
            fontPath = mSystemFont;
        }
//		String name = fontHandle.getDefaultFontName();
//		if (name.equals("圆体")||name.equals("黑体")||name.equals("仿宋")||name.equals("楷体")) {
//
//		} else {//默认
//			if (mSystemFont == null)
//				findSystemFont();
//			fontPath = mSystemFont;
//		}
        APPLog.e("getFontPath",fontPath);
        return fontPath;//
    }

    public void findSystemFont() {
        File fontDir = new File(SystemFontPath);
        File[] files = fontDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return "ttf".equalsIgnoreCase(StringUtil.getExpName(filename))
                        || "otf".equalsIgnoreCase(StringUtil.getExpName(filename));
            }
        });

        long size = 0;
        File findFile = null;
        if (files != null) {
            for (File fontFile : files) {
                if (size < fontFile.length()) {
                    size = fontFile.length();
                    findFile = fontFile;
                }
            }
        }

        if (findFile != null) {
            mSystemFont = findFile.getAbsolutePath();
        }
    }

    public List<Font> getFontFileList() {
        FontListHandle handle = FontListHandle.getHandle(mContext);
        return handle.getFontFileList();
    }

    public String getFontName() {
        FontListHandle fontHandle = FontListHandle.getHandle(mContext);
        return fontHandle.getDefaultFontName();
    }

    public String getCssPath() {
        // String path =
        // DangdangFileManager.getPreSetCss();//FileUtil.getCssPath();
        String path = ((DDApplication) mContext).getEpubCss();
        return path;
    }

    private void setPaddingLeftOrRight(float mLeft) {

        this.mPaddingLeftOrRight = mLeft;
        // savePaddingLeftOrRight(mLeft);
    }

    public void saveLineWordNum(int lineWordNum) {

        SharedPreferences pre = getSharedPre();
        Editor edit = pre.edit();
        edit.putInt(Key_LineWordNum, lineWordNum);
        edit.commit();

    }

    public void setNightMode(boolean isNight) {
        SharedPreferences pre = getSharedPre();
        Editor edit = pre.edit();
        edit.putBoolean(Key_Night, isNight);
        edit.commit();

        mNightMode = isNight;
    }

    /**
     * 是否夜间模式
     *
     * @return
     */
    public boolean isNightMode() {
        return mNightMode;
    }

    private boolean initNightMode() {
        SharedPreferences pre = getSharedPre();
        mNightMode = pre.getBoolean(Key_Night, false);
        return mNightMode;
    }

    public void setSystemLight(boolean isSystemLight) {
        SharedPreferences pre = getSharedPre();
        Editor edit = pre.edit();
        edit.putBoolean(Key_Light_System, isSystemLight);
        edit.commit();
    }

    public boolean isSystemLight() {
//        SharedPreferences pre = getSharedPre();
//        return pre.getBoolean(Key_Light_System, true);
        return true;
    }

    public int getLightInterval() {
        int lightInter = mLightInterval;
        if (lightInter <= 0) {
            lightInter = initLightInterval();
        }
        return lightInter;
    }

    private int initLightInterval() {
        SharedPreferences pre = mContext.getSharedPreferences(
                READER_LIGHT_CONTROL, Context.MODE_PRIVATE);
        mLightInterval = pre.getInt(READER_LIGHT_TIME, READER_LIGHT_INTERVAL2);
        return mLightInterval;
    }

    public void setLightInterval(int interval) {
        SharedPreferences pre = mContext.getSharedPreferences(
                READER_LIGHT_CONTROL, Context.MODE_PRIVATE);
        Editor edit = pre.edit();
        edit.putInt(READER_LIGHT_TIME, interval);
        edit.commit();
    }

    public int getReaderGuideNum() {
        SharedPreferences pre = mContext.getSharedPreferences(
                READER_GUIDE_NUM_FILE, Context.MODE_PRIVATE);
        return pre.getInt(READER_GUIDE_NUM_VARIABLE, 0);
    }

    public void setReaderGuideNum(int num) {
        SharedPreferences pre = mContext.getSharedPreferences(
                READER_GUIDE_NUM_FILE, Context.MODE_PRIVATE);
        Editor edit = pre.edit();
        edit.putInt(READER_GUIDE_NUM_VARIABLE, num);
        edit.commit();
    }

    public boolean getMarkGuideShowStatus() {
        SharedPreferences pre = mContext.getSharedPreferences(
                READER_MARK_GUIDE_FILE, Context.MODE_PRIVATE);
        return pre.getBoolean(READER_MARK_GUIDE_VARIABLE, true);
    }

    public void setMarkGuideShowStatus(boolean bo) {
        SharedPreferences pre = mContext.getSharedPreferences(
                READER_MARK_GUIDE_FILE, Context.MODE_PRIVATE);
        Editor edit = pre.edit();
        edit.putBoolean(READER_MARK_GUIDE_VARIABLE, bo);
        edit.commit();
    }

    public boolean getDoubleFingerGuideShowStatus() {
        SharedPreferences pre = mContext.getSharedPreferences(
                READER_DOUBLE_FINGER_GUIDE_FILE, Context.MODE_PRIVATE);
        return pre.getBoolean(READER_DOUBLE_FINGER_GUIDE_VARIABLE, true);
    }

    public void setDoubleFingerGuideShowStatus(boolean bo) {
        SharedPreferences pre = mContext.getSharedPreferences(
                READER_DOUBLE_FINGER_GUIDE_FILE, Context.MODE_PRIVATE);
        Editor edit = pre.edit();
        edit.putBoolean(READER_DOUBLE_FINGER_GUIDE_VARIABLE, bo);
        edit.commit();
    }

    public boolean getFirstMarkFlag() {
        SharedPreferences pre = mContext.getSharedPreferences(
                READER_FIRST_MARK_FILE, Context.MODE_PRIVATE);
        return pre.getBoolean(READER_FIRST_MARK_VARIABLE, true);
    }

    public void setFirstMarkFlag(boolean bo) {
        SharedPreferences pre = mContext.getSharedPreferences(
                READER_FIRST_MARK_FILE, Context.MODE_PRIVATE);
        Editor edit = pre.edit();
        edit.putBoolean(READER_FIRST_MARK_VARIABLE, bo);
        edit.commit();
    }

    public boolean getStatusOfDoubleFingerLight() {
        SharedPreferences pre = mContext.getSharedPreferences(
                READER_DOUBLE_FINGER_LIGHT_GUIDE_FILE, Context.MODE_PRIVATE);
        return pre.getBoolean(READER_DOUBLE_FINGER_LIGHT_GUIDE_VARIABLE, true);
    }

    public void setStatusOfDoubleFingerLight(boolean bo) {
        SharedPreferences pre = mContext.getSharedPreferences(
                READER_DOUBLE_FINGER_LIGHT_GUIDE_FILE, Context.MODE_PRIVATE);
        Editor edit = pre.edit();
        edit.putBoolean(READER_DOUBLE_FINGER_LIGHT_GUIDE_VARIABLE, bo);
        edit.commit();
    }

    public void saveLight(float light) {
        SharedPreferences pre = getSharedPre();
        SharedPreferences.Editor editor = pre.edit();
        editor.putFloat(ReadConfig.PREF_KEY_LIGHT, light);
        editor.commit();
    }

    public float getLight() {
        final int screenBright = DRUiUtility.getScreenBrightness(mContext);
        SharedPreferences sp = getSharedPre();
        float light = sp.getFloat(ReadConfig.PREF_KEY_LIGHT,
                ReadConfig.ZERO_LIGHT);
        if (light == ReadConfig.ZERO_LIGHT) {
            light = screenBright * 1f / 255;
        }

        if (light <= ReadConfig.MIN_LIGHT) {
            light = ReadConfig.MIN_LIGHT;
        } else if (light >= ReadConfig.MAX_LIGHT) {
            light = ReadConfig.MAX_LIGHT;
        }
        return light;
    }

    public void saveNightLight(float light) {
        SharedPreferences pre = getSharedPre();
        SharedPreferences.Editor editor = pre.edit();
        editor.putFloat(PREF_KEY_LIGHT_NIGHT, light);
        editor.commit();
    }

    public float getNightLight() {

        SharedPreferences sp = getSharedPre();
        float light = sp.getFloat(PREF_KEY_LIGHT_NIGHT, DEFAULT_NIGHT_LIGHT);

        return light;
    }

    public float getRealLight() {
        if (isNightMode()) {
            return getNightLight();
        }
        return getLight();
    }

    // 正常非夜间模式亮度
    public float getCommonLight() {

        return getLight();
    }

    public boolean hasRealLight() {
        if (hasLight() || hasNightLight()) {
            return true;
        }
        return false;
    }

    // 是否有正常非夜间模式亮度值
    public boolean hasCommonLight() {
        if (hasLight()) {
            return true;
        }
        return false;
    }

    public boolean hasLight() {
        SharedPreferences pre = getSharedPre();
        return pre.contains(ReadConfig.PREF_KEY_LIGHT);
    }

    public boolean hasNightLight() {
        SharedPreferences pre = getSharedPre();
        return pre.contains(ReadConfig.PREF_KEY_LIGHT_NIGHT);
    }

    public boolean hasFirstReadPdf() {
        SharedPreferences pre = getSharedPre();
        return !pre.contains(READER_PDF_FIRST);
    }

    public void setFirstReadPdfFlag() {
        SharedPreferences pre = getSharedPre();
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean(READER_PDF_FIRST, true);
        editor.commit();
    }

    public int getMinLineWord() {
        int minLineWord = getDefaultMinLineWord();
        minLineWord = getSharedPre()
                .getInt(KEY_READER_MINLINEWORD, minLineWord);
        return minLineWord;
    }

    public void saveMinLineWord(int lineWord) {
        Editor editor = getSharedPre().edit();
        editor.putInt(KEY_READER_MINLINEWORD, lineWord);
        editor.commit();
    }

    public int getMaxLineWord() {
        int maxLineWord = getDefaultMaxLineWord();
        maxLineWord = getSharedPre()
                .getInt(KEY_READER_MAXLINEWORD, maxLineWord);
        return maxLineWord;
    }

    public void saveMaxLineWord(int lineWord) {
        Editor editor = getSharedPre().edit();
        editor.putInt(KEY_READER_MAXLINEWORD, lineWord);
        editor.commit();
    }

    public int getDefaultMinLineWord() {
        return DRUiUtility.getPadScreenIsLarge() ? MinLineWordNumPad_Default
                : MinLineWordNum_Default;
    }

    public int getDefaultMaxLineWord() {
        return DRUiUtility.getPadScreenIsLarge() ? MaxLineWordNumPad_Default
                : MaxLineWordNum_Default;
    }

    public int getDefaultLineWord() {
        return DRUiUtility.getPadScreenIsLarge() ? DefaultLineWordNumPad_Default
                : DefaultLineWordNum_Default;
    }

    public boolean isPadScreenIsLarge() {
        return DRUiUtility.getPadScreenIsLarge();
    }

	/*
	 * public boolean isFirstChangeLight(boolean defaultV){ SharedPreferences sp
	 * =mContext.getSharedPreferences(mContext.getPackageName(),
	 * Context.MODE_PRIVATE); return sp.getBoolean(ReadConfig.PREF_KEY_FIRST,
	 * defaultV); }
	 * 
	 * public void saveFirstChangeLight(boolean value){ SharedPreferences sp
	 * =mContext.getSharedPreferences(mContext.getPackageName(),
	 * Context.MODE_PRIVATE); SharedPreferences.Editor editor = sp.edit();
	 * editor.putBoolean(ReadConfig.PREF_KEY_FIRST, value); editor.commit(); }
	 */

    public boolean isVolKeyFlip() {
        try {
            SharedPreferences sp = getSharedPre();
            return sp.getBoolean(KEY_READER_VOL_FILP, false);
        } catch (Exception e) {
            LogM.e(e.toString());
        }
        return false;
    }

    public void setVolKeyFlip(boolean value) {
        SharedPreferences sp = getSharedPre();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY_READER_VOL_FILP, value);
        editor.commit();
    }

    public void setFullScreen(boolean value) {
        SharedPreferences sp = getSharedPre();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY_READER_ISFULL, value);
        editor.commit();
    }

    public boolean isFullScreen() {
        return getSharedPre().getBoolean(KEY_READER_ISFULL, true);
    }

    public void setPageTurnMode(int mode) {
        SharedPreferences sp = getSharedPre();
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_READER_PAGETURN_MODE, mode);
        editor.commit();
    }

    public int getPageTurnMode() {
        SharedPreferences sp = getSharedPre();
        return sp.getInt(KEY_READER_PAGETURN_MODE, PageTurnMode.MODE_DEFAULT);
    }

    public boolean isShowFontHint() {
        SharedPreferences sp = getSharedPre();
        return sp.getBoolean(KEY_READER_FONT_HINT, true);
    }

    public void setShowFontHint(boolean value) {
        SharedPreferences sp = getSharedPre();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY_READER_FONT_HINT, value);
        editor.commit();
    }

    public boolean isFontAutoDownload() {
        SharedPreferences sp = getSharedPre();
        return sp.getBoolean(KEY_READER_FONT_DOWNLOAD, false);
    }

    public void setFontAutoDownload(boolean value) {
        SharedPreferences sp = getSharedPre();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY_READER_FONT_DOWNLOAD, value);
        editor.commit();
    }

    public void setChineseConvert(Context context, boolean isConvert) {
        SharedPreferences sp = getSharedPre(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY_READER_CHINESE_CONVERT, isConvert);
        editor.commit();
    }

    public boolean getChineseConvert() {
        SharedPreferences sp = getSharedPre();
        return sp.getBoolean(KEY_READER_CHINESE_CONVERT, false);
    }

    public void setDictPath(Context context, String xdbPath, String rulesPath) {
        SharedPreferences sp = getSharedPre(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(READER_DICT_XDB, xdbPath);
        editor.putString(READER_DICT_RULES, rulesPath);
        editor.commit();
    }

    public String getDictXdbPath() {
        SharedPreferences sp = getSharedPre();
        return sp.getString(READER_DICT_XDB, DangdangFileManager.getPreSetDictXdb());
    }

    public String getDictRulesPath() {
        SharedPreferences sp = getSharedPre();
        return sp.getString(READER_DICT_RULES, DangdangFileManager.getPreSetDictRule());
    }

    public String getPreReadPath() {
        SharedPreferences sp = getSharedPre();
        return sp.getString(READER_PREREAD_PATH, "");
    }

    public void setPreReadPath(Context context, String preReadPath) {
        SharedPreferences sp = getSharedPre(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(READER_PREREAD_PATH, preReadPath);
        editor.commit();
    }

    public String getReadProgress() {
        SharedPreferences sp = getSharedPre();
        return sp.getString(READER_OFFPRINT_PROGRESS, "");
    }

    public void setReadProgress(Context context, String readProgress) {
        SharedPreferences sp = getSharedPre(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(READER_OFFPRINT_PROGRESS, readProgress);
        editor.commit();
    }

    public String getOffPrintPath() {
        SharedPreferences sp = getSharedPre();
        return sp.getString(OFFPRINT_PATH, "");
    }

    public void setOffPrintPath(Context context, String offPrintPath) {
        SharedPreferences sp = getSharedPre(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(OFFPRINT_PATH, offPrintPath);
        editor.commit();
    }

    private SharedPreferences getSharedPre() {
        if (mShaprdPre == null) {
            mShaprdPre = getSharedPre(mContext);
        }
        return mShaprdPre;
    }

    private SharedPreferences getSharedPre(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        return sp;
    }

    public static class PageTurnMode {

        /**
         * 经典模式(默认)
         */
        public final static int MODE_DEFAULT = 1;
        /**
         * 单手模式
         */
        public final static int MODE_SINGLEHAND = 2;

        public static boolean isSingleHanded(int mode) {
            return MODE_SINGLEHAND == mode;
        }

    }

    private static final String AUTO_OPEN_SWITCH = "auto_open_switch";

    /**
     * 自动打开最近阅读书开关，默认false
     *
     * @return
     */
    public static boolean getAutoOpenSwitch(Context context) {
        if (context == null) {
            return false;
        }
        SharedPreferences pre = getPreferences(context);
        return pre.getBoolean(AUTO_OPEN_SWITCH, false);
    }

    public static void saveAutoOpenSwitch(boolean tSwitch, Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(AUTO_OPEN_SWITCH, tSwitch);
        editor.commit();
    }

    private static SharedPreferences getPreferences(Context context) {
        final String name = context.getPackageName();
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    /**
     * 保存打开阅读是否报错
     *
     * @param isReadOpenError
     */
    public void setIsReadOpenError(boolean isReadOpenError) {
        SharedPreferences pre = getSharedPre();
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean(READER_IS_OPPEN_ERROR, isReadOpenError);
        editor.commit();
    }

    /**
     * 打开阅读是否报错
     *
     * @return
     */
    public boolean isReadOpenError() {
        return getSharedPre().getBoolean(READER_IS_OPPEN_ERROR, false);
    }

    /**
     * 保存阅读是否是正常退出
     *
     * @param isReadNormalExit
     */
    public void setIsReadNormalExit(boolean isReadNormalExit) {
        SharedPreferences pre = getSharedPre();
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean(READER_IS_NORMAL_EXIT, isReadNormalExit);
        editor.commit();
    }

    /**
     * 阅读是否是正常退出
     *
     * @return
     */
    public boolean isReadNormalExit() {
        return getSharedPre().getBoolean(READER_IS_NORMAL_EXIT, true);
    }

    /**
     * 保存阅读是否是正常退出
     *
     * @param bookId
     */
    public void setReadNotNormalExitBookId(String bookId) {
        SharedPreferences pre = getSharedPre();
        SharedPreferences.Editor editor = pre.edit();
        editor.putString(READER_NOT_NORMAL_EXIT_BOOK_ID, bookId);
        editor.commit();
    }

    /**
     * 阅读是否是正常退出
     *
     * @return
     */
    public String getReadNotNormalExitBookId() {
        return getSharedPre().getString(READER_NOT_NORMAL_EXIT_BOOK_ID, "");
    }

}
