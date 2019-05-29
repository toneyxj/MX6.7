package com.dangdang.reader.moxiUtils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.font.FontListHandle;
import com.dangdang.reader.moxiUtils.RunableUtils.FindTTFFilesRunable;
import com.dangdang.reader.moxiUtils.adapter.FontItemAdapter;
import com.dangdang.reader.view.DDProgressView;
import com.mx.mxbase.view.LinerlayoutInter;
import com.mx.mxbase.view.NoGridView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.dangdang.reader.R.id.progress_show_layout;

/**
 * Created by Administrator on 2016/10/14.
 */
public class SettingNewDialog extends Dialog implements View.OnClickListener, LinerlayoutInter.LinerLayoutInter {
    private String fontPath = "font/";
    //    private String fontPath = "file:///android_asset/font/";

    /**
     * 设置接口
     */
    private SettingInterface anInterface;
    //字体设置
    private TextView show_directory;
    private TextView show_font;
    private TextView show_page;
    private TextView show_progress;
    private TextView show_setting;
    private LinearLayout main_setting_layout;
    private TextView show_title;
    private View onclick_dismiss;
    private ImageView setting_select_index;
    private TextView title_name;
    private TextView select_title;
    private ImageButton page_index;
    private ImageButton yu_yin;
    private TextView read_progress_txt;
    private TextView read_page_txt;
    private TextView read_tiaozhaung_txt;

    private LinearLayout font_layout;
    private LinearLayout page_layout;
    private LinearLayout progress_layout;

    /**
     * 字体设置
     */
    private GridView grid_fonts_layout;
    private LinerlayoutInter font_slide;
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

    private String mulu;
    private int totalPage;
    private int currentPage;
    private Handler handler=new Handler();

    private DDProgressView progress_view;
    private TextView progress_hitn;
    private TextView chapter_last;
    private TextView chapter_next;

    public SettingNewDialog(Context context, int theme, String mulu, int totalPage, int currentPage, SettingInterface anInterface) {
        super(context, theme);
        this.anInterface = anInterface;
        this.totalPage = totalPage;
        this.currentPage = currentPage;
        mFontHandle = FontListHandle.getHandle(context);
        this.mulu = mulu;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_new_setting);
        /**
         * 界面主设置选项
         */
        //字体设置
        main_setting_layout = (LinearLayout) findViewById(R.id.main_setting_layout);

        show_directory = (TextView) findViewById(R.id.show_directory);
        show_font = (TextView) findViewById(R.id.show_font);
        show_page = (TextView) findViewById(R.id.show_page);
        yu_yin = (ImageButton) findViewById(R.id.yu_yin);
        show_progress = (TextView) findViewById(R.id.show_progress);
        show_setting = (TextView) findViewById(R.id.show_setting);
        show_title = (TextView) findViewById(R.id.show_title);
        onclick_dismiss = (View) findViewById(R.id.onclick_dismiss);
        setting_select_index = (ImageView) findViewById(R.id.setting_select_index);
        title_name = (TextView) findViewById(R.id.title_name);
        select_title = (TextView) findViewById(R.id.select_title);
        page_index = (ImageButton) findViewById(R.id.page_index);

        read_progress_txt = (TextView) findViewById(R.id.read_progress_txt);
        read_page_txt = (TextView) findViewById(R.id.read_page_txt);
        read_tiaozhaung_txt = (TextView) findViewById(R.id.read_tiaozhaung_txt);

        font_layout = (LinearLayout) findViewById(R.id.font_layout);
        page_layout = (LinearLayout) findViewById(R.id.page_layout);
        progress_layout = (LinearLayout) findViewById(R.id.progress_layout);
        (findViewById(progress_show_layout)).setOnClickListener(this);

        progress_view = (DDProgressView) findViewById(R.id.progress_view);
        chapter_next = (TextView) findViewById(R.id.chapter_next);
        chapter_last = (TextView) findViewById(R.id.chapter_last);
        progress_hitn = (TextView) findViewById(R.id.progress_hitn);

        page_index.setOnClickListener(this);
        yu_yin.setOnClickListener(this);

        main_setting_layout.setOnClickListener(this);
        show_title.setOnClickListener(this);
        onclick_dismiss.setOnClickListener(this);

        show_directory.setOnClickListener(this);
        chapter_next.setOnClickListener(this);
        chapter_last.setOnClickListener(this);

        show_font.setOnClickListener(this);
        show_page.setOnClickListener(this);
        show_progress.setOnClickListener(this);
        show_setting.setOnClickListener(this);



        title_name.setText(mulu);
        initfontSize();
        initPageSetting();
        initProgressSetting();
        progress_view.initView(progressListener,totalPage, currentPage);
        initSetProgress(totalPage, currentPage);

        ReadConfig readConfig = ReadConfig.getConfig();
        sourcePosition = readConfig.getProgress();

        if (anInterface==null) {//弹出语音窗口
            (findViewById(R.id.progress_show_layout)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.line_bottom)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.bottom_layout)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.title_layout)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.title_line)).setVisibility(View.INVISIBLE);
        }

    }
    private DDProgressView.ProgressListener progressListener=new DDProgressView.ProgressListener() {
        @Override
        public void onProgress(int size,boolean isUp) {
            if (size<=0)size=1;
            progress_hitn.setText(size+"/"+totalPage);
            if (isUp){
                anInterface.onScrollToPage(size);
                SettingNewDialog.this.currentPage=size;
                initView();
            }
        }
    };

    public void initSetProgress(int totalPage, int currentPage) {
        if (totalPage > 0) {
            this.totalPage = totalPage - 1;
            this.currentPage = currentPage;
             (findViewById(progress_show_layout)).setVisibility(View.VISIBLE);
            progress_view.setMaxNumber(this.totalPage);
            progress_view.setCurNumber(currentPage);
        }else {
            (findViewById(progress_show_layout)).setVisibility(View.GONE);
        }
       initView();
    }
    private void initView(){
        setTitleProgress(-1);
        read_progress_txt.setText(setTitleProgress(0));
        read_page_txt.setText(setTitleProgress(1));
        read_tiaozhaung_txt.setText(setTitleProgress(2));
    }

    /**
     *
     * @param is 是否正在播放
     */
    public void setYuYinStatus(boolean is){
//        yu_yin.setText(is?"停止":"播放");
    }
    private long Ctime=0;
    @Override
    public void onClick(View v) {
        long tt=System.currentTimeMillis();
        if (Math.abs(tt-Ctime)<500){
            return;
        }
        Ctime=tt;
        int i = v.getId();
        if (i == R.id.page_index) {//点击查看目录 showDirMarkNote(DirectoryMarkNoteActivity.MARK);
            if (anInterface != null) {
                anInterface.shareBiJi();
                this.dismiss();
            }
        } else if (i == R.id.yu_yin) {
            if (anInterface != null) {
                anInterface.startYuyin();
                this.dismiss();
            }
        } else if (i == R.id.show_directory) {//点击查看目录 showDirMarkNote(DirectoryMarkNoteActivity.MARK);
            if (anInterface != null) {
                anInterface.startMuen();
                this.dismiss();
            }
        } else if (i == R.id.show_font) {//点击设置字体
            goneMainView(0);
            setShowText();
//            if (adapter==null&&ttfModels.size()>0){
//                initSonData();
//            }
        } else if (i == R.id.show_page) {//点击页面设置
            goneMainView(1);
        } else if (i == R.id.show_progress) {//点击阅读进度
            goneMainView(2);
        } else if (i == R.id.show_setting) {//点击其他设置
            anInterface.onScreenShort();
            this.dismiss();
        } else if (i == R.id.onclick_dismiss) {//点击返回
            this.dismiss();
        } else if (i == R.id.show_title) {
            anInterface.endReader();
            this.dismiss();
        }else if (i == R.id.chapter_next) {
            anInterface.onChapterJump(false);
        }else if (i == R.id.chapter_last) {
            anInterface.onChapterJump(true);
        }

    }

    private int sourcePosition = 0;

    private String setTitleProgress(int index) {
        String value = "";
        int position = index;
        if (index == -1) {
            ReadConfig readConfig = ReadConfig.getConfig();
            position = readConfig.getProgress();
        }
        switch (position) {
            case 0:
                float progrssF = currentPage * 100f / totalPage;
                DecimalFormat format = new DecimalFormat("0.00");

                value = format.format(progrssF) + "%";
                if (index != -1) {
                    value += "\t当前进度";
                }
                break;
            case 1:
                value = currentPage + "页\t全书" + totalPage + "页";
                break;
            case 2:
                value = currentPage + "页\t剩余" + String.valueOf(totalPage - currentPage) + "页";
                break;
            default:
                break;
        }
//        if (index == -1) {
//            page_index.setText(value);
//        }
        return value;
    }

    private void goneMainView(int index) {
        if (main_setting_layout.getVisibility() == View.INVISIBLE)
            main_setting_layout.setVisibility(View.VISIBLE);
        if (setting_select_index.getVisibility() == View.INVISIBLE)
            setting_select_index.setVisibility(View.VISIBLE);
        font_layout.setVisibility(View.GONE);
        page_layout.setVisibility(View.GONE);
        progress_layout.setVisibility(View.GONE);

        TextView indexView = null;
        switch (index) {
            case 0:
                indexView = show_font;
                font_layout.setVisibility(View.VISIBLE);
                break;
            case 1:
                indexView = show_page;
                page_layout.setVisibility(View.VISIBLE);
                break;
            case 2:
                indexView = show_progress;
                progress_layout.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        if (indexView != null) {
            select_title.setText(indexView.getText().toString());

            int[] location = new int[2];
            indexView.getLocationOnScreen(location);
            int x = location[0];
            int imageWidth = setting_select_index.getMeasuredWidth();
            int widgetWidth = indexView.getMeasuredWidth();
            int startX = x + widgetWidth / 2 - imageWidth / 2;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) setting_select_index.getLayoutParams();
            params.setMargins(startX, 0, 0, 0);
            setting_select_index.setLayoutParams(params);
        }
    }

    private List<TTFModel> ttfModels = new ArrayList<>();
    private List<TTFModel> sonTTFs = new ArrayList<>();
    private FontItemAdapter adapter;
    private int currentIndex = 0;
    private int totalIndex;
    private int pageSize = 6;

    private void initfontSize() {
        /**
         * 字体选择
         */
        grid_fonts_layout = (NoGridView) findViewById(R.id.grid_fonts_layout);
        font_slide = (LinerlayoutInter) findViewById(R.id.font_slide);
        grid_fonts_layout.setOnItemClickListener(fontItemClick);
        radio_font = (RadioGroup) findViewById(R.id.radio_font);


        getTTFFromFonts();

        font_slide.setLayoutInter(this);
        radio_font.setOnCheckedChangeListener(fontGroup);

        final ReadConfig readConfig = ReadConfig.getConfig();
        setFontSize(readConfig.getFontIndex());
    }

    /**
     * 分配下面数据以刷新
     */
    public void initSonData() {
        //计算页数
        totalIndex = ttfModels.size() / pageSize;
        totalIndex += ttfModels.size() % pageSize == 0 ? 0 : 1;

        //计算当前页数
        if (currentIndex > totalIndex - 1) {
            currentIndex = totalIndex - 1;
        }
        if (currentIndex < 0) currentIndex = 0;
        if (totalIndex == 0) totalIndex = 1;

        if (ttfModels.size() == 0) {
            adapterItems(ttfModels);
        } else if (totalIndex - 1 == currentIndex) {
            adapterItems(ttfModels.subList(currentIndex * pageSize, ttfModels.size()));
        } else {
            adapterItems(ttfModels.subList(currentIndex * pageSize, (currentIndex + 1) * pageSize));
        }
        setShowText();
    }

    /**
     * 设置显示页面的文字
     */
    private void setShowText() {
        if (font_layout.getVisibility() == View.VISIBLE) {
            select_title.setText("字体选项\t( " + String.valueOf(currentIndex + 1) + "/" + totalIndex + " )");
        }
    }

    private void adapterItems(List<TTFModel> listModels) {
        if (listModels == null) return;
        sonTTFs.clear();
        sonTTFs.addAll(listModels);
        if (adapter == null) {
            adapter = new FontItemAdapter(getContext(), sonTTFs, mFontHandle.getDefaultFontName());
            grid_fonts_layout.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    AdapterView.OnItemClickListener fontItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectIndex(sonTTFs.get(position));
        }
    };


    private void selectIndex(TTFModel model) {
        if (adapter.selectedFonts.equals(model.ttfName)) return;

        if (model.isAssets) {
            new FileCopy(getContext(), model.filePath, model.getFontsSdCardPath(), model.ttfName, new FileCopy.CopyListener() {
                @Override
                public void CopyListener(boolean results, String path, String name) {
                    if (results) {
                        mFontHandle.setDefaultFontPath(path);
                        mFontHandle.setDefaultFontName(name);
                        anInterface.settingFont(0);
                        if (adapter != null) {
                            adapter.updateSelect(name, grid_fonts_layout);
                        }
                    } else {
                        Toast.makeText(getContext(), "字体准备失败", Toast.LENGTH_SHORT).show();
                    }

                }
            }).execute();
        } else {
            mFontHandle.setDefaultFontPath(model.filePath);
            mFontHandle.setDefaultFontName(model.ttfName);
            anInterface.settingFont(0);
            if (adapter != null) {
                adapter.updateSelect(model.ttfName, grid_fonts_layout);
            }
        }

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
            this.dismiss();
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
//            anInterface.settingProgress(index);
            setTitleProgress(-1);
        }
        isRefureshProgress = true;
        ProgressIndex = index;
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
        final ReadConfig config = ReadConfig.getConfig();
        if (sourcePosition != config.getProgress()) {
            anInterface.settingProgress(sourcePosition);
        }
    }

    /**
     * 全屏显示的dialog
     *
     * @param context
     * @param anInterface
     */
    public static SettingNewDialog getdialog(Context context, String mulu, int totalPage, int currentPage, SettingInterface anInterface) {
        SettingNewDialog dialog = new SettingNewDialog(context, R.style.AlertDialogStyle, mulu, totalPage, currentPage, anInterface);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        window.setAttributes(lp);
        dialog.show();
        return dialog;
    }

    /**
     * 获得TTF文件列表
     *
     * @return
     */
    private void getTTFFromFonts() {
        new Thread(new FindTTFFilesRunable(fontPath, new FindTTFFilesRunable.FindTTFFilesListener() {
            @Override
            public void onTTFFiles(List<TTFModel> ttfModel) {
                if (isDismiss) return;
                ttfModels.addAll(ttfModel);
//                if (font_layout.getVisibility() == View.VISIBLE) {
//                    initSonData();
//                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initSonData();
                    }
                },300);
            }
        })).start();
    }

    @Override
    public void moveLeft() {
        if (currentIndex > 0 && (currentIndex <= totalIndex - 1)) {
            currentIndex--;
            initSonData();
        }
    }

    @Override
    public void moveRight() {
        if (currentIndex >= totalIndex - 1) {
            return;
        } else {
            currentIndex++;
            initSonData();
        }
    }

    /**
     * 字体切换页面翻页
     *
     * @param event 翻页事件
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && font_layout.getVisibility() == View.VISIBLE) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_PAGE_UP:
                    moveLeft();
                    return true;
                case KeyEvent.KEYCODE_PAGE_DOWN:
                    moveRight();
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);

    }


    private boolean isDismiss = false;

    @Override
    public void dismiss() {
        isDismiss = true;
        super.dismiss();
    }
}
