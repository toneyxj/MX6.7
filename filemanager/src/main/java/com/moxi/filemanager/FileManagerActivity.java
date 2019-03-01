package com.moxi.filemanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.filemanager.configer.ConfigerUtils;
import com.moxi.filemanager.fragment.FileFragment;
import com.moxi.filemanager.interfaces.FileInterface;
import com.moxi.filemanager.model.ClipboardType;
import com.moxi.filemanager.model.FileModel;
import com.moxi.filemanager.utils.ClipboardUtils;
import com.moxi.filemanager.utils.DeleteFile;
import com.moxi.filemanager.utils.FileCopy;
import com.moxi.filemanager.utils.MoveFile;
import com.moxi.filemanager.utils.PathUtils;
import com.moxi.filemanager.utils.RefureshPathUtils;
import com.moxi.remotefilemanager.filedata.RemoteFileService;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.dialog.InputDialog;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.utils.DeleteDDReaderBook;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 文件管理器进入activity
 */
public class FileManagerActivity extends BaseFileManagerActivity implements View.OnClickListener, FileInterface {
    @Bind(R.id.show_title)
    TextView show_title;
    @Bind(R.id.manager)
    TextView manager;

    @Bind(R.id.search)
    ImageButton search;
    @Bind(R.id.file_show_style)
    ImageButton file_show_style;
    @Bind(R.id.sort)
    ImageButton sort;

    @Bind(R.id.internal_memory)
    TextView internal_memory;
    @Bind(R.id.exteral_memory)
    TextView exteral_memory;
    @Bind(R.id.all_file_layout)
    RelativeLayout all_file_layout;
    @Bind(R.id.all_file_show)
    TextView all_file_show;
    @Bind(R.id.document_layout)
    RelativeLayout document_layout;
    @Bind(R.id.document_show)
    TextView document_show;
    @Bind(R.id.picture_layout)
    RelativeLayout picture_layout;
    @Bind(R.id.picture_show)
    TextView picture_show;
    @Bind(R.id.other_layout)
    RelativeLayout other_layout;
    @Bind(R.id.other_show)
    TextView other_show;
    @Bind(R.id.sort_layout)
    LinearLayout sort_layout;

    @Bind(R.id.double_select)
    TextView double_select;

    @Bind(R.id.new_floder)
    TextView new_floder;

    @Bind(R.id.quit_move_and_detele)
    TextView quit_move_and_detele;

    //粘贴数据
    @Bind(R.id.paste_layout)
    LinearLayout paste_layout;
    @Bind(R.id.paste)
    Button paste;
    @Bind(R.id.cancel_paste)
    Button cancel_paste;
    //标题显示栏
    @Bind(R.id.clipboard_status)
    TextView clipboard_status;
    @Bind(R.id.wifi_net_files)
    Button wifi_net_files;

    @Bind(R.id.delete)
    Button delete;
    @Bind(R.id.copy)
    Button copy;
    @Bind(R.id.move)
    Button move;
    @Bind(R.id.fragment_file_manager)
    FrameLayout fragment_file_manager;
    @Bind(R.id.last_page)
    ImageButton last_page;
    @Bind(R.id.show_index)
    TextView show_index;
    @Bind(R.id.next_page)
    ImageButton next_page;
    /**
     * 存储类型，1外部存储，0内部存储
     */
    private int memoryStyle = 0;
    /**
     * 查看类型，0全部,1文档，2图片，3其他
     */
    private int checkStyle = 0;
    FileFragment[] fragments = new FileFragment[8];
    List<FileFragment> sonFragments = new ArrayList<>();
    private int itemWidth = 0;
    private int itemHeight;
    private int currentFragment = 0;
    /**
     * 无线传书数据列表
     */
    private List<String> flushList = new ArrayList<>();

    private String saveSonPath = "";
    /**
     * 选择文件
     */
    private boolean pick_attachment = false;
    /**
     * 选择文件类型,默认是0：代表任意文件类型
     * 1：选择图片
     */
    public int pickAttachStyle = 0;

    BroadcastReceiver fileChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.moxi.fileChange.ACTION")) {
                String path = intent.getStringExtra("filePath");
                File file = new File(path);
                if (!flushList.contains(file.getParent())) {
                    flushList.add(file.getParent());
                }
            }
        }
    };
//    private HomeKeyEventBrodcast homeKeyEventBrodcast = new HomeKeyEventBrodcast();

    //    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
    public void initfragemnt() {
//        if (hasFocus && itemWidth == 0) {
        APPLog.e("saveSonPath:" + saveSonPath);
        //设置宽高
        int w = (int) (MyApplication.ScreenWidth*0.7);
        int h = 1180;
//        int w = fragment_file_manager.getMeasuredWidth();
//        int h = fragment_file_manager.getMeasuredHeight();
        itemWidth = w / 4;
        itemHeight = h / 5;
        if (isRefuresh) {
            switch (memoryStyle) {
                case 0:
                    setTextAllImage(internal_memory, true, R.mipmap.save_manager_select, R.mipmap.save_manager_non_seelct, 1);
                    setTextAllImage(exteral_memory, false, R.mipmap.save_manager_select, R.mipmap.save_manager_non_seelct, 1);
                    break;
                case 1:
                    setTextAllImage(internal_memory, false, R.mipmap.save_manager_select, R.mipmap.save_manager_non_seelct, 1);
                    setTextAllImage(exteral_memory, true, R.mipmap.save_manager_select, R.mipmap.save_manager_non_seelct, 1);
                    break;
                default:
                    break;
            }

            if (saveSonPath.equals("") || checkStyle != 0) {
                addFragment(currentFragment);
            } else {
                File file = new File(saveSonPath);
                addFragment(saveSonPath, file.getName());
            }
            if (checkStyle != 0) {
                checkStyleIndex(0, false);
            }
            checkStyleIndex(checkStyle, true);
        } else {
            addFragment(0);
//            }
        }
    }

    /**
     * 初始化碎片
     */
    private void addFragment(int type) {
        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();

        hideFragment(mFragmentTransaction);
        hideListFragment(mFragmentTransaction);
        if (null == fragments[type]) {
            FileFragment fileFragment = FileFragment.newInstance(type, itemWidth, itemHeight, 20, showSelect, pick_attachment,pickAttachStyle);
            mFragmentTransaction.add(R.id.fragment_file_manager, fileFragment, "fragment" + type);
            fragments[type] = fileFragment;
            mFragmentTransaction.commitAllowingStateLoss();
        } else {
            if (type == 0) {
                ShowMainFragment(mFragmentTransaction, type);
            } else {
                mFragmentTransaction.show(fragments[type]);
                mFragmentTransaction.commitAllowingStateLoss();
                fragments[type].initFragmentTitle();
            }
        }
        currentFragment = type;


    }

    /**
     * 隐藏fragment
     *
     * @param mFragmentTransaction
     */
    private void hideFragment(FragmentTransaction mFragmentTransaction) {
        for (int i = 0; i < fragments.length; i++) {
            if (null != fragments[i] && !fragments[i].isHidden()) {
                mFragmentTransaction.hide(fragments[i]);
            }
        }
    }

    /**
     * 隐藏fragment
     *
     * @param mFragmentTransaction
     */
    private void hideListFragment(FragmentTransaction mFragmentTransaction) {
        for (int i = 0; i < sonFragments.size(); i++) {
            if (!sonFragments.get(i).isHidden()) {
                mFragmentTransaction.hide(sonFragments.get(i));
            }
        }
    }

    /**
     * 初始化碎片
     *
     * @param titleName 标题名
     * @param filePath  文件夹路径
     */
    private void addFragment(String filePath, String titleName) {
        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        //隐藏头8个碎片
        hideFragment(mFragmentTransaction);
        //隐藏以前的fragment
        hideListFragment(mFragmentTransaction);
        FileFragment writeFragment = FileFragment.newInstance(filePath, titleName, itemWidth, itemHeight, 20, showSelect, pick_attachment,pickAttachStyle);
        mFragmentTransaction.add(R.id.fragment_file_manager, writeFragment, filePath);
        sonFragments.add(writeFragment);

        mFragmentTransaction.commitAllowingStateLoss();
    }

    private void ShowMainFragment(FragmentTransaction mFragmentTransaction, int type) {
        if (sonFragments.size() == 0) {
            mFragmentTransaction.show(fragments[type]);
            mFragmentTransaction.commitAllowingStateLoss();
            fragments[type].initFragmentTitle();
        } else {
            mFragmentTransaction.show(sonFragments.get(sonFragments.size() - 1));
            mFragmentTransaction.commitAllowingStateLoss();
            sonFragments.get(sonFragments.size() - 1).initFragmentTitle();
        }
    }

    private void clearSonFragment() {
        if (sonFragments.size() == 0) return;

        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < sonFragments.size(); i++) {
            mFragmentTransaction.remove(sonFragments.get(i));
        }
        ShowMainFragment(mFragmentTransaction, currentFragment);
//        mFragmentTransaction.commitAllowingStateLoss();
        sonFragments.clear();
    }
    private void clearTfFragment(){
        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (int i = 4; i < fragments.length; i++) {
            if (fragments[i]!=null) {
                mFragmentTransaction.remove(fragments[i]);
            }
            fragments[i]=null;
        }
    }

    private void dbackFragment(String path) {
        try {
            FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragmentTransaction.remove(sonFragments.get(sonFragments.size() - 1));
            sonFragments.remove(sonFragments.size() - 1);
            File file = new File(path);
            String parentPath = file.getParent();
            if (FileUtils.getInstance().getExtSDCardPathList().contains(parentPath)) {
                if (sonFragments.size() == 0) {
                    mFragmentTransaction.commitAllowingStateLoss();
                    addFragment(currentFragment);
                    return;
                }
            } else {
                if (sonFragments.size() == 0) {
                    addFragment(parentPath, new File(parentPath).getName());
                    return;
                }
            }
            mFragmentTransaction.show(sonFragments.get(sonFragments.size() - 1));
            mFragmentTransaction.commitAllowingStateLoss();
            sonFragments.get(sonFragments.size() - 1).initFragmentTitle();
        }catch (Exception e){
            this.finish();
        }

    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_file_manager;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("memoryStyle", memoryStyle);
        outState.putInt("checkStyle", checkStyle);
        outState.putInt("currentFragment", currentFragment);
        outState.putBoolean("pick_attachment", pick_attachment);
        outState.putInt("pickAttachStyle", pickAttachStyle);
        outState.putString("saveSonPath", (sonFragments.size() == 0) ? "" : sonFragments.get(sonFragments.size() - 1).getFilePath());
    }

    /**
     * 对以前加载的fragment进行清除
     */
    public void destoryReload() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("fragment" + i);
            if (fragment != null) {
                ft.remove(fragment);
            }
        }
        List<String> files = getCanDestory(saveSonPath);
        for (int i = 0; i < files.size(); i++) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(files.get(i));
            if (fragment != null) {
                ft.remove(fragment);
            }
        }
        ft.commitAllowingStateLoss();
    }

    private List<String> getCanDestory(String path) {
        List<String> files = new ArrayList<>();
        if (path == null || path.equals("") || FileUtils.getInstance().getExtSDCardPathList().contains(path)) {
//        if (path == null || path.equals("") || path.equals("/mnt/sdcard") || path.equals("/mnt/extsd")) {
            return files;
        } else {
            File file = new File(path);
            files.add(path);
            files.addAll(getCanDestory(file.getParent()));
            return files;
        }
    }

    private boolean isRefuresh = false;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        isRefuresh = savedInstanceState != null;
        if (isRefuresh) {
            memoryStyle = savedInstanceState.getInt("memoryStyle");
            checkStyle = savedInstanceState.getInt("checkStyle");
            currentFragment = savedInstanceState.getInt("currentFragment");
            saveSonPath = savedInstanceState.getString("saveSonPath");
            pick_attachment = savedInstanceState.getBoolean("pick_attachment", false);
            pickAttachStyle = savedInstanceState.getInt("pickAttachStyle", 0);
            destoryReload();
        } else {
            pick_attachment = getIntent().getBooleanExtra("pick_attachment", false);
            pickAttachStyle = getIntent().getIntExtra("pickAttachStyle", 0);
        }
//        registerReceiver(homeKeyEventBrodcast, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        new DeleteDDReaderBook(this, false, null).execute();
        show_title.setOnClickListener(this);
        manager.setOnClickListener(this);
        search.setOnClickListener(this);
        file_show_style.setOnClickListener(this);
        sort.setOnClickListener(this);
        internal_memory.setOnClickListener(this);
        exteral_memory.setOnClickListener(this);

        all_file_layout.setOnClickListener(this);
        document_layout.setOnClickListener(this);
        picture_layout.setOnClickListener(this);
        other_layout.setOnClickListener(this);

        delete.setOnClickListener(this);
        move.setOnClickListener(this);
        copy.setOnClickListener(this);
        quit_move_and_detele.setOnClickListener(this);

        double_select.setOnClickListener(this);
        new_floder.setOnClickListener(this);

        new_floder.setOnClickListener(this);

        last_page.setOnClickListener(this);
        next_page.setOnClickListener(this);
        wifi_net_files.setOnClickListener(this);

        //复制粘贴
        paste.setOnClickListener(this);
        cancel_paste.setOnClickListener(this);

        setFileShowStyle();

        registerReceiver(fileChangeReceiver, new IntentFilter("com.moxi.fileChange.ACTION"));

        initfragemnt();

        if (pick_attachment) {
            //隐藏布局
            file_show_style.setVisibility(View.INVISIBLE);
            sort.setVisibility(View.INVISIBLE);
            new_floder.setVisibility(View.INVISIBLE);
            (findViewById(R.id.hide_manager_layout)).setVisibility(View.INVISIBLE);
            wifi_net_files.setVisibility(View.INVISIBLE);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addDataScheme("file");
        this.registerReceiver(usbReceiver, filter);
    }
    BroadcastReceiver usbReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)){
                //插入TF卡
                APPLog.e("filemanager","插入TF卡");
                clearTfFragment();
                if (memoryStyle==1){
                    addFragment(memoryStyle * middleFragment());
                }
            }else if (intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)){
                //拔出TF卡
                APPLog.e("filemanager","拔出TF卡");
                if (memoryStyle==1){
                    changeMemoryStyle(0);
                }
                clearTfFragment();
            }
        }
    };

    private void setFileShowStyle() {
        int fileShowStyle = MyApplication.preferences.getInt("fileShowStyle", 0);
        file_show_style.setImageResource(fileShowStyle == 0 ? R.mipmap.file_show_style_one : R.mipmap.file_show_style_two);
    }

    @Override
    public void onClick(View v) {
        if (sort_layout.getVisibility() == View.VISIBLE) {
            if (v == internal_memory || v == exteral_memory || v == show_title || v == manager || v == sort || v == new_floder) {
                showDeleteAndMove(0);
                return;
            }
        }
        switch (v.getId()) {
            case R.id.show_title://返回
                onBackPressed();
                break;
            case R.id.search://搜索
                SearchActivity.startSearchActivity(this, pick_attachment, pickAttachStyle, 12);
                break;
//            case R.id.manager://管理
//                showManagerPopupWindow(manager);
//                break;
            case R.id.file_show_style://切换风格

                int fileShowStyle = MyApplication.preferences.getInt("fileShowStyle", 0) == 0 ? 1 : 0;
                SharedPreferences.Editor editor = MyApplication.editor;
                editor.putInt("fileShowStyle", fileShowStyle);
                editor.commit();

                setFileShowStyle();

                for (FileFragment fileFragment : sonFragments) {
                    fileFragment.setShowStyle();
                }
                for (FileFragment fileFragment : fragments) {
                    if (fileFragment != null)
                        fileFragment.setShowStyle();
                }
                break;
            case R.id.double_select://批量管理
                if (ClipboardUtils.getInstance().isHaveEvent) {
                    showToast("文件" + ClipboardUtils.getInstance().getTypeString() + "中无法进行批量管理");
                } else {
                    selectMore();
                }
                break;
            case R.id.new_floder://新建文件夹

                newFloder();
                break;
            case R.id.sort://排序
                showSortPopupWindow(manager);
                break;
            case R.id.internal_memory://内部存储
                changeMemoryStyle(0);
                break;
            case R.id.exteral_memory://外部存储
                List<String> listSdcard = PathUtils.getExtSDCardPathList(this);

                if (listSdcard.size() > 1) {
                    changeMemoryStyle(1);
                } else {
                    MyApplication.Toast("无外部存储卡");
                }
                break;
            case R.id.all_file_layout://全部
                changeCheckStyle(memoryStyle * middleFragment());
                break;
            case R.id.document_layout://文档
                changeCheckStyle(memoryStyle * middleFragment() + 1);
                break;
            case R.id.picture_layout://图片
                changeCheckStyle(memoryStyle * middleFragment() + 2);
                break;
            case R.id.other_layout://其他
                changeCheckStyle(memoryStyle * middleFragment() + 3);
                break;
            case R.id.delete://删除
                deleteFilesFile();
                break;
            case R.id.move://移动
                MoveFiles();
                showDeleteAndMove(0);
                break;
            case R.id.copy://复制
                copyFiles();
                showDeleteAndMove(0);
                break;
            case R.id.quit_move_and_detele://取消
                APPLog.e("点击取消了");
                getFtagemt().clearSeelcts();
                showDeleteAndMove(0);
                break;
            case R.id.last_page://上一页
                getFtagemt().moveLeft();
                break;
            case R.id.next_page://下一页
                getFtagemt().moveRight();
                break;
            case R.id.cancel_paste://取消剪切板
                APPLog.e("点击取消了");
                ClipboardUtils.getInstance().ClearClipboard(0);
                setTitleShow();
                break;
            case R.id.paste://点击粘贴
                //获得当前文件文件夹路径
                int type = getFtagemt().getType();
                if (type == 1 || type == 2 || type == 3) {
                    MyApplication.Toast("请选择具体粘贴文件夹");
                    return;
                }
//                String filepath = getFtagemt().getFilePath();
                if (ClipboardUtils.getInstance().getType() == ClipboardType.COPY) {
                    copyFile();
                } else {
                    cutFile();
                }
                break;
            case R.id.wifi_net_files://无线传输
                if (RemoteFileService.getInstance().isConnectedInWifi(this)) {
                    flushList.clear();
                    boolean is = RemoteFileService.getInstance().startRemoteFileService(this);
                    if (!is) {
                        ToastUtils.getInstance().showToastShort("无线传书服务开启失败！！");
                        return;
                    }
                    RemoteFileService.getInstance().setPower(this);
                    String hint = "服务已开启，请于电脑端打开浏览器输入连接地址:\n";
                    String ipAddress = RemoteFileService.getInstance().getIpAccess(this);
                    String showHitn = hint + ipAddress + "\n使用过程请勿退出！";
                    insureDialog("无线传书", showHitn,
                            "断开连接", "wifi连接", new InsureOrQuitListener() {
                                @Override
                                public void isInsure(Object code, boolean is) {
                                    RemoteFileService.getInstance().stopAndroidWebServer();
                                    setingWifiClose(false);
                                    for (String path : flushList) {
                                        refureshMoveToFragement(path);
                                    }
                                    flushList.clear();
                                }
                            });
                    setAlertContenttyle(StringUtils.getStyle(FileManagerActivity.this, showHitn, ipAddress, R.style.black_size_22));

                    setingWifiClose(true);
                } else {
                    ToastUtils.getInstance().showToastShort("请检查WIFI网络连接");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 移动选中文件
     */
    private void MoveFiles() {
        List<File> files = new ArrayList<>();
        files.addAll(getFtagemt().getFileSelete());

        if (files.size() == 0) return;

        moveFile(files, true);
    }

    private void moveFile(File file) {
        List<File> files = new ArrayList<>();
        files.add(file);
        moveFile(files, false);
    }

    /**
     * 移动文件实现类
     *
     * @param files 当前文件
     * @param is    是否是单个文件操作，单个文件操作为false，多个文件操作为true
     */
    private void moveFile(final List<File> files, final boolean is) {
        ClipboardUtils.getInstance().init(ClipboardType.CUT, files);
        setTitleShow();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        APPLog.e("点击 x="+ev.getRawX()+"  点击y="+ev.getRawY());
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 复制选中文件
     */
    private void copyFiles() {
        List<File> files = new ArrayList<>();
        files.addAll(getFtagemt().getFileSelete());

        if (files.size() == 0) return;

        copyFile(files, true);
    }

    /**
     * 复制文件
     *
     * @param file
     */
    private void copyFile(File file) {
        List<File> files = new ArrayList<>();
        files.add(file);
        copyFile(files, false);
    }

    /**
     * 移动文件实现类
     *
     * @param files 当前文件
     * @param is    是否是单个文件操作，单个文件操作为false，多个文件操作为true
     */
    private void copyFile(final List<File> files, final boolean is) {
        ClipboardUtils.getInstance().init(ClipboardType.COPY, files);
        setTitleShow();
    }

    /**
     * 获得文件类型
     *
     * @return 0文件夹，1图片，2文件
     */
    public int getFileType(File file) {
        if (file.isDirectory()) return 0;
        String name = file.getAbsolutePath();
        if (name.contains(".")) {
            String prefix = name.substring(name.lastIndexOf(".") + 1);
            prefix = prefix.toLowerCase();
            if (prefix.equals("jpg") || prefix.equals("png") || prefix.equals("jpeg")) {
                return 1;
            } else if (prefix.equals("txt") || prefix.equals("pdf") || prefix.equals("epub") || prefix.equals("mobi")) {
                return 2;
            } else {
                return 4;
            }
        } else {
            return 4;
        }
    }

    /**
     * 获得当前主文件目录
     *
     * @return
     */
    public String getMainPath() {
        if (memoryStyle == 0) {
            return StringUtils.getSDCardPath();
        } else {
            return PathUtils.getExtSDCardPathList(this).get(1);
        }
    }

    /**
     * 如果存在的话，刷新移动取得fragment
     *
     * @param floder 需要刷新的文件夹路径
     */
    public void refureshMoveToFragement(String floder) {
//        String titleStr = show_title.getText().toString();
//        for (int i = 0; i < sonFragments.size(); i++) {
//            String path = sonFragments.get(i).getFilePath();
//            if (path.equals(floder)) {
//                sonFragments.get(i).getallFile();
//                return;
//            }
//        }
        RefureshPathUtils.getInstance().addRefureshPath(floder);
        if ((getMainPath()).equals(floder)) {
            fragments[middleFragment() * memoryStyle].getallFile();
        }
//        show_title.setText(titleStr);
    }

    /**
     * 获得所有文件
     */
    public void deleteFilesFile() {
        final List<File> files =
                getFtagemt().getFileSelete();

        if (files.size() == 0) return;

        insureDialog("请确认删除选中的文件", "", new InsureOrQuitListener() {
            @Override
            public void isInsure(Object code, boolean is) {
                if (is) {
                    deleteFile(files);
                    getFtagemt().deleteData();
                    showDeleteAndMove(1);
                    if (getFtagemt().getType() != 1 || getFtagemt().getType() != 2) {
                        for (File filess : files) {
                            if (getFileType(filess) == 1) {
                                FileFragment.isrefureshPhoto = true;
                            } else if (getFileType(filess) == 2) {
                                FileFragment.isrefureshFile = true;
                            }
                        }
                    }
//                    getFtagemt().getallFile();
                }
            }
        });


    }

    private void deleteFile(File file) {
        List<File> files = new ArrayList<>();
        files.add(file);
        deleteFile(files);
    }

    private void deleteFile(final List<File> files) {
        dialogShowOrHide(true, "删除中...");
        new DeleteFile(this, files, new DeleteFile.DeleteFileListener() {
            @Override
            public void DeleteSucess(boolean results) {
                //删除完成刷新界面
                dialogShowOrHide(false, "");
                if (getFtagemt().getType() == 1 || getFtagemt().getType() == 2) {
                    List<String> filePaths = new ArrayList<String>();
                    for (int i = 0; i < files.size(); i++) {
//                        String path = files.get(i).getAbsolutePath();
                        String path = files.get(i).getParent();
//                        path = path.substring(0, path.lastIndexOf("/"));
                        if (!filePaths.contains(path)) {
                            refureshMoveToFragement(path);
                            filePaths.add(path);
                        }
                    }
                }
                sendDelte(files);
            }
        }).execute();
    }

    private void sendDelte(List<File> files) {
        APPLog.e("阅读器删除-sendDelte", files.toString());
        String[] strs = new String[files.size()];
        for (int i = 0; i < files.size(); i++) {
            strs[i] = files.get(i).getAbsolutePath();
        }
        ContentResolver contentResolver = getContentResolver();
        Uri selecturi = Uri.parse("content://com.moxi.bookstore.provider.RecentlyProvider/Recently");
        contentResolver.delete(selecturi, "", strs);
    }

    /**
     * 是否开启选择模式
     */
    private boolean showSelect = false;

    /**
     * 显示删除和移动
     */
    private void showDeleteAndMove(int classType) {
        if (sort_layout.getVisibility() == View.VISIBLE) {
            showSelect = false;
            sort_layout.setVisibility(View.INVISIBLE);
        } else if (!ClipboardUtils.getInstance().isHaveEvent) {
            showSelect = true;
            sort_layout.setVisibility(View.VISIBLE);
        }

        slectMoveInit(classType);
    }

    public void slectMoveInit(int classType) {
        if (currentFragment < middleFragment()) {
            for (int i = 1; i < middleFragment(); i++) {
                if (null != fragments[i]) {
                    fragments[i].setIsSelect(showSelect, classType);
                }
            }
        } else if (currentFragment >= middleFragment()) {
            for (int i = middleFragment(); i < fragments.length; i++) {
                if (null != fragments[i]) {
                    fragments[i].setIsSelect(showSelect, classType);
                }
            }
        }

        if (sonFragments.size() != 0) {
            sonFragments.get(sonFragments.size() - 1).setIsSelect(showSelect, classType);
        } else if (currentFragment < middleFragment()) {
            fragments[0].setIsSelect(showSelect, classType);
        } else if (currentFragment >= middleFragment()) {
            fragments[middleFragment()].setIsSelect(showSelect, classType);
        }
    }

    private int middleFragment() {
        return (fragments.length / 2);
    }

    private void changeMemoryStyle(int style) {
        if (style == memoryStyle) return;
        //清空子页面
        clearSonFragment();

        memoryStyle = style;
        switch (memoryStyle) {
            case 0:
                setTextAllImage(internal_memory, true, R.mipmap.save_manager_select, R.mipmap.save_manager_non_seelct, 0);
                setTextAllImage(exteral_memory, false, R.mipmap.save_manager_select, R.mipmap.save_manager_non_seelct, 0);
                break;
            case 1:
                setTextAllImage(internal_memory, false, R.mipmap.save_manager_select, R.mipmap.save_manager_non_seelct, 0);
                setTextAllImage(exteral_memory, true, R.mipmap.save_manager_select, R.mipmap.save_manager_non_seelct, 0);
                break;
            default:
                break;
        }
        changeCheckStyle(memoryStyle * middleFragment());

    }

    private void changeCheckStyle(int style) {
        if (style == checkStyle) return;

        addFragment(style);
        checkStyleIndex(checkStyle, false);
        checkStyle = style;
        checkStyleIndex(checkStyle, true);
    }

    public void checkStyleIndex(int style, boolean is) {
        RelativeLayout layout = null;
        TextView textView = null;
        int falseimage = 0;
        int trueiamge = 0;
        style = style >= middleFragment() ? style - middleFragment() : style;
        switch (style) {
            case 0:
                layout = all_file_layout;
                textView = all_file_show;
                falseimage = R.mipmap.file_manager_all_style;
                trueiamge = R.mipmap.all_file1;
                break;
            case 1:
                layout = document_layout;
                textView = document_show;
                falseimage = R.mipmap.doucument_file;
                trueiamge = R.mipmap.document_file1;
                break;
            case 2:
                layout = picture_layout;
                textView = picture_show;
                falseimage = R.mipmap.picture;
                trueiamge = R.mipmap.picture1;
                break;
            case 3:
                layout = other_layout;
                textView = other_show;
                falseimage = R.mipmap.other;
                trueiamge = R.mipmap.other1;
                break;
            default:
                break;
        }
        changeCheck(layout, textView, is, trueiamge, falseimage);
    }

    private void changeCheck(RelativeLayout layout, TextView textView, boolean is, int falseiamge, int trueImage) {
        if (is) {
            layout.setBackgroundResource(R.drawable.back_black);
            textView.setTextColor(getResources().getColor(R.color.colorWihte));
        } else {
            layout.setBackgroundResource(R.drawable.back_white);
            textView.setTextColor(getResources().getColor(R.color.colorBlack));
        }
        setTextAllImage(textView, is, trueImage, falseiamge, 0);
    }

    @Override

    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
//        unregisterReceiver(homeKeyEventBrodcast);
    }


    /**
     * 左边设置图片
     *
     * @param view       设置的文本
     * @param is         显示样式
     * @param trueImage  显示true的image
     * @param falseImage 显示为false的image
     * @param position   图片放置方位 0代表左，1代表上，2代表右，其他代表下
     */
    private void setTextAllImage(TextView view, boolean is, int trueImage, int falseImage, int position) {
        Drawable drawable;
        if (is) {// 是否显示列表
            drawable = getResources().getDrawable(trueImage);
        } else {
            drawable = getResources().getDrawable(falseImage);
        }
        // / 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        if (position == 0) {
            view.setCompoundDrawables(drawable, null, null, null);
        } else if (position == 1) {
            view.setCompoundDrawables(null, drawable, null, null);
        } else if (position == 2) {
            view.setCompoundDrawables(null, null, drawable, null);
        } else {
            view.setCompoundDrawables(null, null, null, drawable);
        }
    }

    @Override
    public void newFloder() {
        if (currentFragment != 0 && currentFragment != middleFragment()) {
            FileApplication.Toast("文档、图片、其它里面无法新建文件夹");
            return;
        }
        newFiloderDialog();
    }

    /**
     * 新建文件夹
     */
    private void newFiloderDialog() {

        InputDialog.getdialog(this, getString(R.string.new_floder), "请输入文件名", new InputDialog.InputListener() {
                    @Override
                    public void quit() {
                    }

                    @Override
                    public void insure(String name) {
                        if (ConfigerUtils.isFail(name)) return;
                        getFtagemt().insuret(name);
                    }
                }
        );

    }

    private FileFragment getFtagemt() {
        FileFragment fileFragment;
        if (sonFragments.size() != 0) {
            fileFragment = sonFragments.get(sonFragments.size() - 1);
            if (fileFragment.isHidden()) {
                fileFragment = fragments[currentFragment];
            }
        } else {
            fileFragment = fragments[currentFragment];
        }
        return fileFragment;
    }

    @Override
    public void selectMore() {
        //显示可以多选
        showDeleteAndMove(0);
    }

    /**
     * 更新排序方式
     */
    @Override
    public void updataSort() {
        super.updataSort();
        if (currentFragment == 0) {
            if (sonFragments.size() != 0) {
                sonFragments.get(sonFragments.size() - 1).sortList(sortStyle, false);
            } else {
                fragments[currentFragment].sortList(sortStyle, false);
            }
        } else {
            fragments[currentFragment].sortList(sortStyle, false);
        }
    }

    @Override
    public void showIndex(String show, int Type) {
        show_index.setText(show);
        sortStyle = Type;
    }

    @Override
    public void setTitle(String title) {
        show_title.setText(title);
    }

    @Override
    public void clickFile(FileModel model) {
        addFragment(model.getFilePath(), model.getFileName());
    }

    @Override
    public void fileCopy(File file) {
//        ClipboardUtils.getInstance().init(ClipboardType.COPY, file);
//        setTitleShow();
        copyFile(file);
    }

    @Override
    public void fileDelete(File file) {
//        String path = file.getAbsolutePath();
//        path = path.substring(0, path.lastIndexOf("/"));
        String path = file.getParent();
        refureshMoveToFragement(path);
        List<File> files = new ArrayList<>();
        files.add(file);
        sendDelte(files);
    }

    @Override
    public void fileMove(File file) {
        moveFile(file);
    }

    @Override
    public void fileRename(File file) {
        //刷新父类文件
        if (getFtagemt().getType() == 1 || getFtagemt().getType() == 2) {
            refureshMoveToFragement(file.getParent());
        } else {
            if (getFileType(file) == 1) {
                FileFragment.isrefureshPhoto = true;
            } else if (getFileType(file) == 2) {
                FileFragment.isrefureshFile = true;
            }
        }
    }

    @Override
    public void judgeRefureshFileOrImage(File file) {
        if (getFileType(file) == 1) {
            FileFragment.isrefureshPhoto = true;
        } else if (getFileType(file) == 2) {
            FileFragment.isrefureshFile = true;
        }
        List<File> files = new ArrayList<>();
        files.add(file);
        sendDelte(files);
    }

    @Override
    public void emailBack(String url) {
        Uri urll = Uri.fromFile(new File(url));
        Intent result = new Intent(null, urll);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onBackPressed() {
        String path = getFtagemt().getFilePath();
//        if (sonFragments.size() == 0 || show_title.getText().toString().equals(getString(R.string.souye))) {
        if (FileUtils.getInstance().getExtSDCardPathList().contains(path)||sonFragments.size()==0) {
            this.finish();
        } else {
            dbackFragment(path);
        }
    }
    /**
     * 设置显示标题栏
     */
    private void setTitleShow() {
        clipboard_status.setText(ClipboardUtils.getInstance().getTypeString());
        if (ClipboardUtils.getInstance().isHaveEvent) {
            sort_layout.setVisibility(View.INVISIBLE);
            paste_layout.setVisibility(View.VISIBLE);
            showSelect = false;
        } else {
            paste_layout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            //上一页
            getFtagemt().moveLeft();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            //下一页
            getFtagemt().moveRight();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 复制文件
     */
    private void copyFile() {
        final String floder = getFtagemt().getFilePath();
        dialogShowOrHide(true, "复制中...");
        new FileCopy(FileManagerActivity.this, ClipboardUtils.getInstance().eventFiles, floder, new FileCopy.CopyListener() {
            @Override
            public void CopyListener(boolean results) {//移动成功返回
                dialogShowOrHide(false, "");
                if (results) {
                    //刷新当前页面
//                    List<String> paths = ClipboardUtils.getInstance().getDifferentPath(floder);
//                    for (String path : paths) {
//                        refureshMoveToFragement(path);
//                    }
                    getFtagemt().getallFile();

                    for (File filess : ClipboardUtils.getInstance().eventFiles) {
                        if (getFileType(filess) == 1) {
                            FileFragment.isrefureshPhoto = true;
                        } else if (getFileType(filess) == 2) {
                            FileFragment.isrefureshFile = true;
                        }
                    }
                } else {
                    showToast("复制失败！！");
                }
                ClipboardUtils.getInstance().ClearClipboard(1);
                setTitleShow();
            }
        }).execute();

    }

    /**
     * 剪切文件
     */
    private void cutFile() {
        final String floder = getFtagemt().getFilePath();
        dialogShowOrHide(true, "移动中...");
        new MoveFile(FileManagerActivity.this, ClipboardUtils.getInstance().eventFiles, floder, new MoveFile.MoveListener() {
            @Override
            public void moveSucess(boolean results, String log) {//移动成功返回
                dialogShowOrHide(false, "");
                if (results) {
                    //刷新当前页面
                    getFtagemt().getallFile();
                    List<String> paths = ClipboardUtils.getInstance().getDifferentPath(floder);
                    for (String path : paths) {
                        refureshMoveToFragement(path);
                    }
                    for (File filess : ClipboardUtils.getInstance().eventFiles) {
                        if (getFileType(filess) == 1) {
                            FileFragment.isrefureshPhoto = true;
                        } else if (getFileType(filess) == 2) {
                            FileFragment.isrefureshFile = true;
                        }
                    }
                } else {
                    ToastUtils.getInstance().showToastShort(log);
                }
                ClipboardUtils.getInstance().ClearClipboard(2);
                setTitleShow();
            }
        }).execute();

    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
//        ClipboardUtils.getInstance().ClearClipboard(3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClipboardUtils.getInstance().ClearClipboard(4);
        RefureshPathUtils.getInstance().clearRefuresh();
        unregisterReceiver(fileChangeReceiver);
        unregisterReceiver(usbReceiver);
        flushList.clear();
        setingWifiClose(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        RemoteFileService.getInstance().stopAndroidWebServer();
        RemoteFileService.getInstance().closePower();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHandler().sendEmptyMessageDelayed(1000, 500);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == 1000) {
//            EpdController.invalidate(getWindow().getDecorView(), EpdController.UpdateMode.GC);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        //TRIM_MEMORY_COMPLETE：内存不足，并且该进程在后台进程列表最后一个，马上就要被清理
        //TRIM_MEMORY_MODERATE：内存不足，并且该进程在后台进程列表的中部。
        //TRIM_MEMORY_BACKGROUND：内存不足，并且该进程是后台进程。

        //需要处理不必要的ui引用
        //TRIM_MEMORY_UI_HIDDEN：内存不足，并且该进程的UI已经不可见了。

        //TRIM_MEMORY_RUNNING_CRITICAL：内存不足(后台进程不足3个)，并且该进程优先级比较高，需要清理内存
        //TRIM_MEMORY_RUNNING_LOW：内存不足(后台进程不足5个)，并且该进程优先级比较高，需要清理内存
        // TRIM_MEMORY_RUNNING_MODERATE：内存不足(后台进程超过5个)，并且该进程优先级比较高，需要清理内存
    }

    private int value = 0;

    public void setingWifiClose(boolean is) {
        if (!is && value == 0) return;
        if (is) {
            value = Settings.System.getInt(getContentResolver(), "close_wifi_delay", value);
            APPLog.e("setingWifiClose-value", value);
            Settings.System.putInt(getContentResolver(), "close_wifi_delay", -1);
            Intent intent = new Intent("update_close_wifi_delay");
            sendBroadcast(intent);
        } else {
            Settings.System.putInt(getContentResolver(), "close_wifi_delay", value);
            Intent intent = new Intent("update_close_wifi_delay");
            sendBroadcast(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK) {
            int style = data.getIntExtra("style", -1);
            if (style < 0) return;
            if (style == 3) {//正常返回带删除路径
                ArrayList<String> list = data.getStringArrayListExtra("paths");
                for (int i = 0; i < list.size(); i++) {
                    judgeRefureshFileOrImage(new File(list.get(i)));
                    refureshMoveToFragement(new File(list.get(i)).getParent());

                    FileFragment fileFragment = getFtagemt();
                    if (RefureshPathUtils.getInstance().judgeRefureshPath(fileFragment.getFilePath())) {
                        fileFragment.getallFile();
                    }
                }
                return;
            }
            String path = data.getStringExtra("path");
            switch (style) {
                case 0://邮件返回
                    emailBack(path);
                    break;
                case 1:
                    fileCopy(new File(path));
                    break;
                case 2:
                    fileMove(new File(path));
                    break;
                default:
                    break;
            }
        }else if (requestCode==10&&resultCode==RESULT_OK){
            //刷新最上面的fragment
            //刷新当前页面
            getFtagemt().getallFile();
        }
    }
}
