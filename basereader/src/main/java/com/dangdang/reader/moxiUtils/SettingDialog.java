package com.dangdang.reader.moxiUtils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.font.FontListHandle;
import com.mx.mxbase.base.BaseApplication;
import com.mx.mxbase.utils.StringUtils;

/**
 * 设置框
 * Created by Administrator on 2016/9/22.
 */
public class SettingDialog extends Dialog implements View.OnClickListener {
    private String fontPath = "font/";
//    private String fontPath = "file:///android_asset/font/";
    //字体文件
    private String sdFilePath= StringUtils.getFilePath("font/");
    /**
     * 设置接口
     */
    private SettingInterface anInterface;
    //字体设置
    private LinearLayout font_click;
    private View font_line;
    private LinearLayout font_layout;
    //页面设置
    private LinearLayout page_click;
    private View page_line;
    private LinearLayout page_layout;
    //阅读进度
    private LinearLayout progress_click;
    private View progress_line;
    private LinearLayout progress_layout;

    /**
     * 字体设置
     */
    //标准
    private LinearLayout standard_click;
    private ImageView standard_select;
    //圆体
    private LinearLayout arcfont_click;
    private ImageView arcfont_select;
    //黑体
    private LinearLayout blackfont_click;
    private ImageView blackfont_select;
    //隶书
    private LinearLayout lishu_click;
    private ImageView lishu_select;
    //楷体
    private LinearLayout kaiti_click;
    private ImageView kaiti_select;
    //字体大小
    private RadioGroup radio_font;

    /**
     * 页面设置
     */
    //行间距
    private ImageView line_min;//最小行间距
    private ImageView line_middle;//中间行间距
    private ImageView line_max;//最大行间距
    //页边距
    private ImageView page_min;//最小页边距
    private ImageView page_middle;//中间大小页边距
    private ImageView page_max;//最大页边距
    //屏幕显示方向
    private ImageView screen_ver;//竖屏
    private ImageView screen_hor;//横屏

    /**
     * 阅读进度设置
     */
    //进度百分比
    private LinearLayout progress_click_one;
    private ImageView progress_select_one;
    //阅读页数
    private LinearLayout progress_click_two;
    private ImageView progress_select_two;
    //跳转页数
    private LinearLayout progress_click_three;
    private ImageView progress_select_three;
    //无
    private LinearLayout progress_click_four;
    private ImageView progress_select_four;
    private FontListHandle mFontHandle;

    public SettingDialog(Context context, int theme, SettingInterface anInterface) {
        super(context, theme);
        this.anInterface = anInterface;
        mFontHandle = FontListHandle.getHandle(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_setting);
        /**
         * 界面主设置选项
         */
        //字体设置
        font_click = (LinearLayout) findViewById(R.id.font_click);
        font_line = (View) findViewById(R.id.font_line);
        font_layout = (LinearLayout) findViewById(R.id.font_layout);
        //页面设置
        page_click = (LinearLayout) findViewById(R.id.page_click);
        page_line = (View) findViewById(R.id.page_line);
        page_layout = (LinearLayout) findViewById(R.id.page_layout);
        //阅读进度
        progress_click = (LinearLayout) findViewById(R.id.progress_click);
        progress_line = (View) findViewById(R.id.progress_line);
        progress_layout = (LinearLayout) findViewById(R.id.progress_layout);

        font_click.setOnClickListener(this);
        page_click.setOnClickListener(this);
        progress_click.setOnClickListener(this);
        onClick(font_click);

        initfontSize();
        initPageSetting();
        initProgressSetting();
    }

    @Override
    public void onClick(View v) {
        goneMainView();
        int i = v.getId();
        if (i == R.id.font_click) {//点击设置字体
            font_line.setVisibility(View.VISIBLE);
            font_layout.setVisibility(View.VISIBLE);
        } else if (i == R.id.page_click) {//点击页面设置
            page_line.setVisibility(View.VISIBLE);
            page_layout.setVisibility(View.VISIBLE);
        } else if (i == R.id.progress_click) {//点击阅读进度
            progress_line.setVisibility(View.VISIBLE);
            progress_layout.setVisibility(View.VISIBLE);
        }

    }

    private void goneMainView() {
        font_line.setVisibility(View.INVISIBLE);
        progress_line.setVisibility(View.INVISIBLE);
        page_line.setVisibility(View.INVISIBLE);

        font_layout.setVisibility(View.GONE);
        page_layout.setVisibility(View.GONE);
        progress_layout.setVisibility(View.GONE);
    }

    private boolean IsfontRefuresh = true;

    private void initfontSize() {
        //标准
        standard_click = (LinearLayout) findViewById(R.id.standard_click);
        standard_select = (ImageView) findViewById(R.id.standard_select);
        //圆体
        arcfont_click = (LinearLayout) findViewById(R.id.arcfont_click);
        arcfont_select = (ImageView) findViewById(R.id.arcfont_select);
        //黑体
        blackfont_click = (LinearLayout) findViewById(R.id.blackfont_click);
        blackfont_select = (ImageView) findViewById(R.id.blackfont_select);
        //隶书/仿宋
        lishu_click = (LinearLayout) findViewById(R.id.lishu_click);
        lishu_select = (ImageView) findViewById(R.id.lishu_select);
        //楷体
        kaiti_click = (LinearLayout) findViewById(R.id.kaiti_click);
        kaiti_select = (ImageView) findViewById(R.id.kaiti_select);
        //字体大小
        radio_font = (RadioGroup) findViewById(R.id.radio_font);

        standard_click.setOnClickListener(fontClick);
        arcfont_click.setOnClickListener(fontClick);
        blackfont_click.setOnClickListener(fontClick);
        lishu_click.setOnClickListener(fontClick);
        kaiti_click.setOnClickListener(fontClick);

        selectIndex(getFontIndex());

        radio_font.setOnCheckedChangeListener(fontGroup);

        final ReadConfig readConfig = ReadConfig.getConfig();
        setFontSize(readConfig.getFontIndex());
    }

    View.OnClickListener fontClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.standard_click) {
                selectIndex(0);
            } else if (i == R.id.arcfont_click) {
                selectIndex(1);
            } else if (i == R.id.blackfont_click) {
                selectIndex(2);
            } else if (i == R.id.lishu_click) {
                selectIndex(3);
            } else if (i == R.id.kaiti_click) {
                selectIndex(4);
            }
        }
    };
    private int currentFontIndex=-1;

    private void selectIndex(final int index) {
        if (index==currentFontIndex)return;

        nonSelect();
        String path = null;
        String fontName = "";
        String toSdPath=null;
        switch (index) {
            case 0:
                standard_select.setImageResource(R.drawable.select_have);
                fontName = "系统字体";
                break;
            case 1:
                path = fontPath + "zhun_yuan.TTF";
                toSdPath=sdFilePath+"zhun_yuan.TTF";
                fontName = "圆体";
                arcfont_select.setImageResource(R.drawable.select_have);
                break;
            case 2:
                path = fontPath + "hei_ti.TTF";
                toSdPath=sdFilePath+"hei_ti.TTF";
                fontName = "黑体";
                blackfont_select.setImageResource(R.drawable.select_have);
                break;
            case 3:
                path = fontPath + "fang_son.TTF";
                toSdPath=sdFilePath+"fang_son.TTF";
                fontName = "仿宋";
                lishu_select.setImageResource(R.drawable.select_have);
                break;
            case 4:
                path = fontPath + "kai_ti.TTF";
                toSdPath=sdFilePath+"kai_ti.TTF";
                fontName = "楷体";
                kaiti_select.setImageResource(R.drawable.select_have);
                break;
            default:
                break;
        }

        if (!IsfontRefuresh) {
//            APPLog.e("字体路径"+path);
            new FileCopy(getContext(), path, toSdPath, fontName, new FileCopy.CopyListener() {
                @Override
                public void CopyListener(boolean results, String path, String name) {
                    if (results){
                        mFontHandle.setDefaultFontPath(path);
                        mFontHandle.setDefaultFontName(name);
                        anInterface.settingFont(index);
                    }else {
                        Toast.makeText(getContext(),"字体准备失败",Toast.LENGTH_SHORT).show();
                    }

                }
            }).execute();
        }
        currentFontIndex=index;
        IsfontRefuresh = false;
    }

    private int getFontIndex() {
        int index = 0;
        String name = mFontHandle.getDefaultFontName();
        if (name.equals("圆体")) {
            index = 1;
        } else if (name.equals("黑体")) {
            index = 2;
        } else if (name.equals("仿宋")) {
            index = 3;
        } else if (name.equals("楷体")) {
            index = 4;
        } else {
            index = 0;
        }
        return index;
    }

    private void nonSelect() {
        standard_select.setImageResource(R.drawable.select_non);
        arcfont_select.setImageResource(R.drawable.select_non);
        blackfont_select.setImageResource(R.drawable.select_non);
        lishu_select.setImageResource(R.drawable.select_non);
        kaiti_select.setImageResource(R.drawable.select_non);
    }

    /**
     * 设置字体大小
     */
    RadioGroup.OnCheckedChangeListener fontGroup = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int index = 0;
            if (checkedId == R.id.bottom_1) {
                index = 0;
            } else if (checkedId == R.id.bottom_2) {
                index = 1;
            } else if (checkedId == R.id.bottom_3) {
                index = 2;
            } else if (checkedId == R.id.bottom_4) {
                index = 3;
            } else if (checkedId == R.id.bottom_5) {
                index = 4;
            } else if (checkedId == R.id.bottom_6) {
                index = 5;
            } else if (checkedId == R.id.bottom_7) {
                index = 6;
            } else if (checkedId == R.id.bottom_8) {
                index = 7;
            } else if (checkedId == R.id.bottom_9) {
                index = 8;
            }
            if (isRefuresh) {
                final ReadConfig readConfig = ReadConfig.getConfig();
                anInterface.settingSize(readConfig.getFontNumber(index));
            } else {
                isRefuresh = true;
            }
        }
    };
    private boolean isRefuresh = true;

    /**
     * 设置字体大小
     *
     * @param index
     */
    private void setFontSize(int index) {
        isRefuresh = false;
        ((RadioButton) radio_font.getChildAt(index)).setChecked(true);
    }


    /**
     * 页面设置项
     */
    private void initPageSetting() {
        //行间距
        line_min = (ImageView) findViewById(R.id.line_min);//最小行间距
        line_middle = (ImageView) findViewById(R.id.line_middle);//中间行间距
        line_max = (ImageView) findViewById(R.id.line_max);//最大行间距
        //页边距
        page_min = (ImageView) findViewById(R.id.page_min);//最小页边距
        page_middle = (ImageView) findViewById(R.id.page_middle);//中间大小页边距
        page_max = (ImageView) findViewById(R.id.page_max);//最大页边距
        //屏幕显示方向
        screen_ver = (ImageView) findViewById(R.id.screen_ver);//竖屏
        screen_hor = (ImageView) findViewById(R.id.screen_hor);//横屏

        line_min.setOnClickListener(pageClick);
        line_middle.setOnClickListener(pageClick);
        line_max.setOnClickListener(pageClick);
        page_min.setOnClickListener(pageClick);
        page_middle.setOnClickListener(pageClick);
        page_max.setOnClickListener(pageClick);
        screen_ver.setOnClickListener(pageClick);
        screen_hor.setOnClickListener(pageClick);

        final ReadConfig readConfig = ReadConfig.getConfig();
        //初始化设置
        setLineIndex(readConfig.getLineSpacingIndex());
        setPageIndex(readConfig.getPaddingLeftOrRightToIndex());
        setScreenIndex(readConfig.getScreen());
    }

    private boolean isRefureshLine = false;
    private boolean isRefureshPage = false;
    private boolean isRefureshscreen = false;
    private int line = -1;
    private int page = -1;
    private int screen = -1;
    View.OnClickListener pageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.line_min) {
                setLineIndex(0);
            } else if (i == R.id.line_middle) {
                setLineIndex(1);
            } else if (i == R.id.line_max) {
                setLineIndex(2);
            } else if (i == R.id.page_min) {
                setPageIndex(0);
            } else if (i == R.id.page_middle) {
                setPageIndex(1);
            } else if (i == R.id.page_max) {
                setPageIndex(2);
            } else if (i == R.id.screen_ver) {
                setScreenIndex(0);
            } else if (i == R.id.screen_hor) {
                setScreenIndex(1);
            } else {
            }
        }
    };

    private void setLineIndex(int index) {
        if (index == line) return;
        line_min.setBackgroundResource(R.color.transparent);
        line_max.setBackgroundResource(R.color.transparent);
        line_middle.setBackgroundResource(R.color.transparent);
        switch (index) {
            case 0:
                line_min.setBackgroundResource(R.drawable.di_white_bian_font4);
                break;
            case 1:
                line_middle.setBackgroundResource(R.drawable.di_white_bian_font4);
                break;
            case 2:
                line_max.setBackgroundResource(R.drawable.di_white_bian_font4);
                break;
            default:
                break;
        }
        if (isRefureshLine) {
            anInterface.settingLine(index);
        }
        line = index;
        isRefureshLine = true;
    }

    private void setPageIndex(int index) {
        if (page == index) return;
        page_min.setBackgroundResource(R.color.transparent);
        page_middle.setBackgroundResource(R.color.transparent);
        page_max.setBackgroundResource(R.color.transparent);
        switch (index) {
            case 0:
                page_min.setBackgroundResource(R.drawable.di_white_bian_font4);
                break;
            case 1:
                page_middle.setBackgroundResource(R.drawable.di_white_bian_font4);
                break;
            case 2:
                page_max.setBackgroundResource(R.drawable.di_white_bian_font4);
                break;
            default:
                break;
        }
        if (isRefureshPage) {
            final ReadConfig config = ReadConfig.getConfig();
            config.savePaddingLeftOrRight(config.getPaddingLeftOrRightFromIndex(index));
            anInterface.settingPage(index);
        }
        page = index;
        isRefureshPage = true;
    }

    private void setScreenIndex(int index) {
        if (screen == index) return;
        screen_ver.setBackgroundResource(R.color.transparent);
        screen_hor.setBackgroundResource(R.color.transparent);
        switch (index) {
            case 0:
                screen_ver.setBackgroundResource(R.drawable.di_white_bian_font4);
                break;
            case 1:
                screen_hor.setBackgroundResource(R.drawable.di_white_bian_font4);
                break;
            default:
                break;
        }
        if (isRefureshscreen) {
            final ReadConfig config = ReadConfig.getConfig();
            config.setScreen(index);
            config.isScreenNonChange = false;
            anInterface.settingScreen(index);
//            new AlertDialog(getContext()).builder().setTitle("提示").setCancelable(true).setMsg("改变屏幕横竖屏需要退出才能生效哟！").show();
        }
        screen = index;
        isRefureshscreen = true;
    }

    private void initProgressSetting() {
        //进度百分比
        progress_click_one = (LinearLayout) findViewById(R.id.progress_click_one);
        progress_select_one = (ImageView) findViewById(R.id.progress_select_one);
        //阅读页数
        progress_click_two = (LinearLayout) findViewById(R.id.progress_click_two);
        progress_select_two = (ImageView) findViewById(R.id.progress_select_two);
        //跳转页数
        progress_click_three = (LinearLayout) findViewById(R.id.progress_click_three);
        progress_select_three = (ImageView) findViewById(R.id.progress_select_three);
        //无
        progress_click_four = (LinearLayout) findViewById(R.id.progress_click_four);
        progress_select_four = (ImageView) findViewById(R.id.progress_select_four);

        progress_click_one.setOnClickListener(progressClick);
        progress_click_two.setOnClickListener(progressClick);
        progress_click_three.setOnClickListener(progressClick);
        progress_click_four.setOnClickListener(progressClick);


        setProgress(ReadConfig.getConfig().getProgress());
    }

    private int ProgressIndex = -1;
    private boolean isRefureshProgress = false;

    View.OnClickListener progressClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.progress_click_one) {
                setProgress(0);
            } else if (i == R.id.progress_click_two) {
                setProgress(1);
            } else if (i == R.id.progress_click_three) {
                setProgress(2);
            } else if (i == R.id.progress_click_four) {
                setProgress(3);
            }
        }
    };

    private void setProgress(int index) {
        if (ProgressIndex == index) return;
        progress_select_one.setImageResource(R.drawable.select_non);
        progress_select_two.setImageResource(R.drawable.select_non);
        progress_select_three.setImageResource(R.drawable.select_non);
        progress_select_four.setImageResource(R.drawable.select_non);
        switch (index) {
            case 0:
                progress_select_one.setImageResource(R.drawable.select_have);
                break;
            case 1:
                progress_select_two.setImageResource(R.drawable.select_have);
                break;
            case 2:
                progress_select_three.setImageResource(R.drawable.select_have);
                break;
            case 3:
                progress_select_four.setImageResource(R.drawable.select_have);
                break;
            default:
                break;
        }
        if (isRefureshProgress) {
            final ReadConfig config = ReadConfig.getConfig();
            config.setProgress(index);
            anInterface.settingProgress(index);
        }
        isRefureshProgress = true;
        ProgressIndex = index;
    }

    public static void getdialog(Context context, SettingInterface anInterface) {
        SettingDialog dialog = new SettingDialog(context, R.style.AlertDialogStyle, anInterface);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getDecorView().setPadding(BaseApplication.ScreenWidth / 6, 0, BaseApplication.ScreenWidth / 6, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        window.setAttributes(lp);
        dialog.show();
    }

}
