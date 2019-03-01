package com.moxi.filemanager.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.moxi.filemanager.CheckImageActivity;
import com.moxi.filemanager.PhotoExportPDFActivity;
import com.moxi.filemanager.R;
import com.moxi.filemanager.adapter.FileAdapter;
import com.moxi.filemanager.configer.ConfigerUtils;
import com.moxi.filemanager.interfaces.FileInterface;
import com.moxi.filemanager.model.FileModel;
import com.moxi.filemanager.utils.AllDocumentFile;
import com.moxi.filemanager.utils.AllImageFile;
import com.moxi.filemanager.utils.ByPathGetFiles;
import com.moxi.filemanager.utils.ClipboardUtils;
import com.moxi.filemanager.utils.PathUtils;
import com.moxi.filemanager.utils.RefureshPathUtils;
import com.moxi.filemanager.utils.StartFile;
import com.mx.mxbase.base.BaseApplication;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.dialog.InputDialog;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.PakegeString;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.view.AlertDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;

/**
 * 文件数据碎片
 * Created by Administrator on 2016/8/29.
 */
public class FileFragment extends baseFile implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    public static boolean isrefureshFile = false;//刷新文件加
    public static boolean isrefureshPhoto = false;//刷新图片

    @Bind(R.id.file_layout)
    GridView file_layout;

    private FileInterface listener;

    private List<FileModel> listData = new ArrayList<>();

    private FileAdapter adapter;
    private List<FileModel> sonList = new ArrayList<>();
    /**
     * 当前显示页数
     */
    private int CurrentIndex = 0;
    /**
     * 总页数
     */
    private int totalIndex = 1;
    /**
     * 当前是否处于批量管理模式
     */
    private boolean isSelect = false;

    private int itemWidth;
    private int itemHeight;
    private int pageSize = 0;
    private int type = 0;
    private String filePath;//文件夹路径
    private String titleName;//标题名
    public int sortType = 0;//排序方式
    /**
     * 文件展示方式
     */
    public int fileShowStyle;
    private boolean pick_attachment=false;
    private int pickAttachStyle=0;

    public int getType() {
        return type;
    }

    public void setIsSelect(boolean isSelect, int classType) {
        this.isSelect = isSelect;
        if (isSelect && adapter != null) {
            adapter.setShowAelect(isSelect);
            return;
        }
        clearAllSelect(classType);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                dialogShowOrHide(false, "");
            }
        }
    };

    private void hitnDialog() {
        handler.sendEmptyMessageDelayed(1, 1000);
    }

    /**
     * 获得文件夹路径名
     *
     * @return
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * 完整文件管理
     *
     * @param filePath
     * @param titleName
     * @param itemWidth
     * @param itemHeight
     * @param pageSize
     * @return
     */
    public static FileFragment newInstance(
            String filePath, String titleName, int itemWidth, int itemHeight, int pageSize, boolean isSelect,boolean pick_attachment,int pickAttachStyle) {
        FileFragment diFragment = new FileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("itemWidth", itemWidth);
        bundle.putInt("itemHeight", itemHeight);
        bundle.putInt("pageSize", pageSize);
        bundle.putString("filePath", filePath);
        bundle.putString("titleName", titleName);
        bundle.putBoolean("isSelect", isSelect);
        bundle.putBoolean("pick_attachment", pick_attachment);
        bundle.putInt("pickAttachStyle", pickAttachStyle);
        diFragment.setArguments(bundle);
        return diFragment;
    }

    /**
     * 主页文件类型
     *
     * @param type       0：全部，1文档，2图片，3其他
     * @param itemWidth  单个item宽
     * @param itemHeight 单个item高
     * @param pageSize   文件最多显示个数
     * @return
     */
    public static FileFragment newInstance(
            int type, int itemWidth, int itemHeight, int pageSize, boolean isSelect,boolean pick_attachment,int pickAttachStyle) {
        FileFragment diFragment = new FileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("itemWidth", itemWidth);
        bundle.putInt("itemHeight", itemHeight);
        bundle.putInt("pageSize", pageSize);
        bundle.putInt("type", type);
        bundle.putBoolean("isSelect", isSelect);
        bundle.putBoolean("pick_attachment", pick_attachment);
        bundle.putInt("pickAttachStyle", pickAttachStyle);
        diFragment.setArguments(bundle);
        return diFragment;
    }

    @Override
    public void initFragment(View view) {
        init();
        file_layout.setOnItemClickListener(this);
        file_layout.setOnItemLongClickListener(this);
        file_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);//返回手势识别触发的事件
            }
        });
        RefureshPathUtils.getInstance().removeRefureshPath(filePath);
        initFileShowStyle(false);

        getallFile();
    }

    private void init() {
        itemWidth = getArguments().getInt("itemWidth");
        itemHeight = getArguments().getInt("itemHeight");
        pageSize = getArguments().getInt("pageSize");
        type = getArguments().getInt("type", -1);
        isSelect = getArguments().getBoolean("isSelect");
        pick_attachment = getArguments().getBoolean("pick_attachment");
        pickAttachStyle = getArguments().getInt("pickAttachStyle",0);
        APPLog.e("pickAttachStyle",pickAttachStyle);
        APPLog.e("pick_attachment",pick_attachment);

        if (type == -1) {
            filePath = getArguments().getString("filePath");
            titleName = getArguments().getString("titleName");
        } else {
            if (type < 4) {

                filePath = StringUtils.getSDCardPath();
            } else {
                type -= 4;
                filePath = PathUtils.getExtSDCardPathList(getActivity()).get(1);
            }
            if (type >= 0) {
                titleName = getString(R.string.souye);
            }

        }
        if (type >= 0) {
            sortType = MyApplication.preferences.getInt(filePath + type, 0);
        } else {
            sortType = MyApplication.preferences.getInt(filePath, 0);
        }
    }

    /**
     * 获得文件夹下所有文件
     */
    public void getallFile() {
        boolean isshowhitn = listData.size() == 0;
        listData.clear();
        switch (type) {
            case -1://文件夹下所有文件
            case 0://根目录文件
                if (!this.isHidden())
                    dialogShowOrHide(isshowhitn, "");

                new ByPathGetFiles(getContext(), filePath, new ByPathGetFiles.SucessListener() {
                    @Override
                    public void onSucess(List<FileModel> sucess) {
                        if (isFinish) return;
//                        hitnDialog();
                        listData.addAll(sucess);
                        initTitle(true);
                        sortList(0, true);
                        APPLog.e("读取当前文件夹路径=" + filePath);

                    }
                }).execute();
                break;
            case 1://读取文档
                dialogShowOrHide(true, "");
                new AllDocumentFile(getContext(), filePath, new AllDocumentFile.DocumentFileListener() {
                    @Override
                    public void getFileSucess(List<FileModel> results) {
                        if (isFinish) return;
//                        hitnDialog();
                        listData.addAll(results);
                        initTitle(true);
                        sortList(0, true);
                    }
                }).execute();
                break;
            case 2://读取图片
                dialogShowOrHide(true, "");
                new AllImageFile(getContext(), filePath, new AllImageFile.ImageFileListener() {
                    @Override
                    public void getFileSucess(List<FileModel> results) {
                        if (isFinish) return;
//                        hitnDialog();
                        APPLog.e("进入这里面"+results.toString());
//                        results=new ArrayList<FileModel>();
                        listData.addAll(results);
                        initTitle(true);
                        sortList(0, true);
                    }
                }).execute();
                break;
            case 3://读取其他文件
                listener.showIndex(String.valueOf(CurrentIndex + 1) + "/" + totalIndex, sortType);
                listener.setTitle(titleName);
                break;
            default:
                break;
        }
    }

    /**
     * 对数据进行排序
     *
     * @param type 排序类型，0按名称，1按大小，2按类型，3按创建时间
     * @param is   是否需要判断保存
     */
    public void sortList(final int type, final boolean is) {
        //当不是初始化时且当前类型与修改类型相同时退出
        if (!is && sortType == type) {
            return;
        }
        if (listData.size()>50) {
            dialogShowOrHide(true, "");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sortListData(type,is);
                }
            }).start();
        }else {
            sortListData(type,is);
        }
    }
    private void sortListData(final int type, final boolean is){
        try {
            switch (type) {
                case 0://名称排序
                    nameSort();
//                Collections.sort(listData, new NameComparator());
                    break;
                case 1://大小排序
                    Collections.sort(listData, new BosComparator());
                    Collections.sort(listData, new MdirOrFileComparator());
                    break;
                case 2://类型排序
                    Collections.sort(listData, new typeComparator());
                    break;
                case 3://创建时间排序
                    Collections.sort(listData, new TimeComparator());
                    Collections.sort(listData, new MdirOrFileComparator());
                    break;
                default:
                    break;
            }
            sortType = type;
        } catch (Exception e) {

        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!is) {
                    SharedPreferences.Editor editor = MyApplication.editor;
                    if (type >= 0) {
                        editor.putInt(filePath + type, sortType);// 保存主目录文件排序方式
                    } else {
                        editor.putInt(filePath, sortType);//保存子目录文件排序方式
                    }
                    editor.commit();// 提交
                }

                initSonData();
                dialogShowOrHide(false, "");
            }
        });
    }

    /**
     * 设置文字展现方式
     */
    public void setShowStyle() {
        CurrentIndex = 0;
        initFileShowStyle(true);
    }

    public void initFileShowStyle(boolean refuresh) {
        fileShowStyle = MyApplication.preferences.getInt("fileShowStyle", 0);
        APPLog.e("fileShowStyle", fileShowStyle);
        pageSize = ConfigerUtils.getShowFileSize(fileShowStyle);

        switch (fileShowStyle) {
            case 0:
                file_layout.setNumColumns(4);
                break;
            case 1:
                file_layout.setNumColumns(1);
                break;
            default:
                break;
        }
        if (refuresh) {
            adapter = null;
            initSonData();
        }
    }


    /**
     * 分配下面数据以刷新
     */
    public void initSonData() {
        if (isFinish)return;
        //计算页数
        totalIndex = listData.size() / pageSize;
        totalIndex += listData.size() % pageSize == 0 ? 0 : 1;

        //计算当前页数
        if (CurrentIndex > totalIndex - 1) {
            CurrentIndex = totalIndex - 1;
        }
        if (CurrentIndex < 0) CurrentIndex = 0;
        if (totalIndex == 0) totalIndex = 1;

        if (listData.size() == 0) {
            adapterItems(listData);
        } else if (totalIndex - 1 == CurrentIndex) {
            adapterItems(listData.subList(CurrentIndex * pageSize, listData.size()));
        } else {
            adapterItems(listData.subList(CurrentIndex * pageSize, (CurrentIndex + 1) * pageSize));
        }
        setShowText();
    }

    private void adapterItems(List<FileModel> listModels) {
        if (listModels == null) return;
        sonList.clear();
        sonList.addAll(listModels);
        if (adapter == null) {
            adapter = new FileAdapter(getActivity(), sonList, itemWidth, itemHeight, isSelect, fileShowStyle);
            file_layout.setAdapter(adapter);
        } else {
            if ((type == 0 || type == -1) && sonList.size() != 0) {
                if (sonList.get(0).getFileType() != 4) {
                    adapter = null;
                    adapter = new FileAdapter(getActivity(), sonList, itemWidth, itemHeight, isSelect, fileShowStyle);
                    file_layout.setAdapter(adapter);
                    return;
                }
            }
            adapter.notifyDataSetChanged();

        }
    }

    /**
     * 设置显示当前显示页数
     */
    public void setShowText() {
        if (!this.isHidden())
            listener.showIndex(String.valueOf(CurrentIndex + 1) + "/" + totalIndex, sortType);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_file;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!isSelect) {
            //获得文件类型
            final FileModel fileModel = sonList.get(position);
            if (fileModel.file.isDirectory()) {
                //如果是文件夹
                listener.clickFile(fileModel);
            } else {
                if (pick_attachment&&pickAttachStyle==0){
                    //邮件进入
                    listener.emailBack(fileModel.file.getAbsolutePath());
                    return;
                }
                //文件进入
                switch (fileModel.getFileType()) {
                    case 1://图片
                        if (pick_attachment&&pickAttachStyle==1){
                            //选择图片进入
                            listener.emailBack(fileModel.file.getAbsolutePath());
                            return;
                        }
                        if (type == 2) {
                            //读取所有文件
                            String sourecePath = fileModel.file.getAbsolutePath();
                            int index = 0;
                            ArrayList<String> images = new ArrayList<>();
                            int i = 0;
                            for (FileModel model : listData) {
                                String path = model.file.getAbsolutePath();
                                images.add(path);
                                if (path.equals(sourecePath)) {
                                    index = i;
                                }
                                i++;
                            }
                            CheckImageActivity.startCheck(getContext(), images, index);
                        } else {
                            ArrayList<String> images = new ArrayList<>();
                            int index = 0;
                            String imagePath = fileModel.file.getAbsolutePath();
                            int i = 0;
                            for (FileModel model : listData) {
                                if (model.getFileType() == 1) {
                                    images.add(model.file.getAbsolutePath());
                                    if (model.file.getAbsolutePath().equals(imagePath)) index = i;
                                    i++;
                                }
                            }
//                            CheckImageActivity.startCheck(getContext(), imagePath);
                            CheckImageActivity.startCheck(getContext(), images, index);
                        }
                        break;
                    case 2://pdf文件
                    case 3://txt文件
                        if (pick_attachment&&pickAttachStyle==1){
                            return;
                        }
                        try {
                            if (fileModel.file.exists()) {
                                Intent input = new Intent();
                                String openPath = fileModel.file.getAbsolutePath();
                                input.putExtra("file", openPath);
                                ComponentName cnInput = new ComponentName(PakegeString.bookstore, "com.moxi.bookstore.activity.RecentlyActivity");
                                input.setComponent(cnInput);

                                startActivity(input);
                            } else {
                                MyApplication.Toast("阅读文件已异常，请确认文件是否存在！！");
                            }
                        } catch (Exception e) {
                            FileUtils.getInstance().openFile(getContext(), fileModel.file);
//                            new StartFile(getActivity(), fileModel.file.getAbsolutePath());
                        }
                        break;
                    default://未知文件
                        if (pick_attachment&&pickAttachStyle==1){
                            return;
                        }
                        if (fileModel.file.getAbsolutePath().equals(StringUtils.getSDCardPath() + "/update.zip")) {
                            insureDialog("请确认进行系统更新", "update", new InsureOrQuitListener() {
                                @Override
                                public void isInsure(Object code, boolean is) {
                                    if (is) {
                                        Intent input = new Intent();
                                        ComponentName cnInput = new ComponentName("com.moxi.systemapp", "com.moxi.systemapp.activity.DownLoadSystemActivity");
                                        input.setComponent(cnInput);
                                        Bundle bundle=new Bundle();
                                        bundle.putString("url",fileModel.file.getAbsolutePath());
                                        bundle.putString("MD5","");
                                        input.putExtras(bundle);
                                        startActivity(input);
                                    }
                                }
                            });
                        } else {
                            new StartFile(getActivity(), fileModel.file.getAbsolutePath());
                        }
                        break;
                }
            }
        } else {//书写批量管理
            //修改选中状态
            sonList.get(position).changeSelect();
            adapter.updateSelect(position, file_layout);
        }
    }

    private void updateFile(final File f) {
        int dotIndex = f.getName().lastIndexOf(".");
        /* 获取文件的后缀名 */
        String end = f.getName().substring(dotIndex, f.getName().length()).toLowerCase();
        end = end.toLowerCase();
        //进行验证
        if (end.equals(".zip")) {

            try {
                RecoverySystem.verifyPackage(f, new RecoverySystem.ProgressListener() {
                    @Override
                    public void onProgress(int progress) {
                        if (isFinish) return;
                        APPLog.e("progress", String.valueOf(progress));
                        if (progress == 100) {
                            try {
                                RecoverySystem.installPackage(getActivity(), f);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, null);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (pick_attachment)return false;
        //长按修改文件名
        if (!isSelect && !ClipboardUtils.getInstance().isHaveEvent) {
//            inputTitleDialog(position);
            showManagerOneFilePopupWindow(view, position, sonList.get(position).getFileType());

        }
        return true;
    }

    private void inputTitleDialog(final int index) {
        InputDialog.getdialog(getActivity(), getString(R.string.re_name), "请输入新文件名", new InputDialog.InputListener() {
            @Override
            public void quit() {
            }

            @Override
            public void insure(String name) {
                String prefix = sonList.get(index).getSuffix();
                prefix = prefix.equals("") ? "" : ("." + prefix);
                name += prefix;
                if (ConfigerUtils.isFail(name)) return;
                if (judgeExist(name)) {
                    BaseApplication.Toast("该文件名已存在");
                    return;
                }
                String newname = sonList.get(index).file.getParent() + "/" + name;
                sonList.get(index).file.renameTo(new File(newname));
                /**
                 * 重新计算布局
                 */
                getallFile();
                listener.fileRename(new File(newname));
            }
        });
    }

    private boolean judgeExist(String name) {
        for (FileModel model : listData) {
            if (model.getFileName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 插入文件
     *
     * @param name 文件名称
     */
    public void insuret(String name) {
        name = name.trim();
        File file = new File(filePath + "/" + name);
        if (ConfigerUtils.isTostSystemFile(file)) return;
        APPLog.e("新建文件路径=" + (filePath + "/" + name));
        if (file.exists()) {
            if (ByPathGetFiles.E_DOWNLOAD_DIR.toLowerCase().equals(file.getAbsolutePath().toLowerCase())){
                BaseApplication.Toast("有隐藏文件已占用该名");
            }else {
                BaseApplication.Toast("该文件名已存在,文件名不区分大小写哟");
            }
        } else {
            file.mkdir();
            /**
             * 重新计算布局
             */
            getallFile();
        }

    }

    @Override
    public void moveLeft() {
        if (CurrentIndex > 0 && (CurrentIndex <= totalIndex - 1)) {
            CurrentIndex--;
            initSonData();
        }
    }

    @Override
    public void moveRight() {
        if (CurrentIndex >= totalIndex - 1) {
            return;
        } else {
            CurrentIndex++;
            initSonData();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (FileInterface) context;
        } catch (Exception e) {
        }
    }

    /**
     * 单个文件处理
     *
     * @param style    处理方式复制，删除，移动，重命名
     * @param position 处理的问价
     */
    @Override
    public void updataOnFile(int style, final int position) {
        final File file = sonList.get(position).file;
        switch (style) {
            case 0://复制
                listener.fileCopy(file);
                break;
            case 1://删除
                insureDialog("请确认删除文件：" + file.getName(), file, new InsureOrQuitListener() {
                    @Override
                    public void isInsure(Object code, boolean is) {
                        if (is) {
                            StringUtils.deleteFile((File) code);
                            listData.remove(sonList.get(position));
                            initSonData();
//                            getallFile();
                            if (type == 1 || type == 2) {
                                listener.fileDelete(file);
                            } else {
                                listener.judgeRefureshFileOrImage(file);
                            }
                        }
                    }
                });
                break;
            case 2://移动
                clearSeelcts();
                seletes.add(position + pageSize * CurrentIndex);
                listener.fileMove(file);
                break;
            case 3://重命名
                inputTitleDialog(position);
                break;
            case 4://设置
                try {
                    String picPatch = file.getAbsolutePath();
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName("com.moxi.systemapp", "com.moxi.systemapp.activity.PhotoCutActivity");
                    intent.setComponent(componentName);
                    intent.putExtra("filePath", picPatch);
                    startActivity(intent);
                } catch (Exception e) {
                    ToastUtils.getInstance().showToastShort("请升级到最新版本，完成设置！");
                }
                break;
            case 5://详情 ，展示文件全路径，文件名称，文件属性
                int fileType=sonList.get(position).getFileType();
//                String typeStr;
//                switch (fileType){
//                    case 0:
//                        typeStr="文件夹";
//                        break;
//                    case 1:
//                        typeStr="图片";
//                        break;
//                    case 2:
//                        typeStr="文档";
//                        break;
//                    case 3:
//                        typeStr="未知";
//                        break;
//                    default:
//                        typeStr="未知";
//                        break;
//                }
                StringBuilder builder=new StringBuilder();
                builder.append("文件路径:");
                builder.append(file.getAbsolutePath());
                builder.append("\n");

                builder.append("文件名:");
                builder.append(file.getName());
                builder.append("\n");

//                builder.append("文件类型:");
//                builder.append(typeStr);
//                builder.append("\n");
                if (fileType!=0) {
                    long size=0;
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                        size = fis.available();
                        builder.append("文件大小:");
                        builder.append(android.text.format.Formatter.formatFileSize(getContext(),size));
                        builder.append("\n");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                new AlertDialog(getActivity()).builder().setTitle("详情").setCancelable(false).setMsg(builder.toString()).
                        setNegativeButton("确定", null).show();
                break;
            case 6://蓝牙分享
                //调用android分享窗口
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.setPackage("com.android.bluetooth");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));//path为文件的路径
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Intent chooser = Intent.createChooser(intent, "Share file");
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(chooser);
                break;
            case 7://导出pdf
                PhotoExportPDFActivity.startPhotoExportPDF(getActivity(),file.getAbsolutePath(),10);
                break;
            default:
                break;
        }
    }


    /**
     * 按时间排序，最近修改文件排在最前面
     */
    private static class TimeComparator implements Comparator<FileModel> {
        public int compare(FileModel file1, FileModel file2) {
            if (file1.file.lastModified() < file2.file.lastModified()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * 大小排序
     */
    private static class BosComparator implements Comparator<FileModel> {
        @Override
        public int compare(FileModel lhs, FileModel rhs) {
            if (getFileSize(lhs.file) < getFileSize(rhs.file)) {
                return 1;
            } else {
                return -1;
            }
        }

        /**
         * 获得文件夹文件大小
         *
         * @param f
         * @return
         * @throws Exception
         */
        private long getFileSize(File f) {
            long size = 0;
            if (f.isDirectory()) {
                File flist[] = f.listFiles();
                if (flist == null) return 0;
                for (int i = 0; i < flist.length; i++) {
                    if (flist[i].isDirectory()) {
                        size += getFileSize(flist[i]);
                    } else {
                        size += flist[i].length();
                    }
                }
            } else {
                size = f.length();
            }
            return size;
        }
    }

    /**
     * 按文件夹与文件排序
     */
    private static class MdirOrFileComparator implements java.util.Comparator<FileModel> {

        @Override
        public int compare(FileModel lhs, FileModel rhs) {
            if (lhs.file.isFile() && rhs.file.isDirectory()) {
                return 1;
            } else if (lhs.file.isDirectory() && rhs.file.isFile()) {
                return -1;
            }
            return 0;
        }
    }

    /**
     * 按类型排序
     */
    private static class typeComparator implements java.util.Comparator<FileModel> {
        @Override
        public int compare(FileModel lhs, FileModel rhs) {
            if (lhs.file.isFile() && rhs.file.isDirectory()) {
                return 1;
            } else if (lhs.file.isDirectory() && rhs.file.isFile()) {
                return -1;
            } else if (lhs.file.isDirectory() && rhs.file.isDirectory()) {
                String name1 = lhs.getFileName();
                String name2 = rhs.getFileName();
                int type = name1.compareTo(name2);
                if (type > 0) {
                    return 1;
                } else if (type < 0) {
                    return -1;
                }
            } else if (lhs.getSuffix().equals("") && !rhs.getSuffix().equals("")) {
                return 1;
            } else if (!lhs.getSuffix().equals("") && rhs.getSuffix().equals("")) {
                return -1;
            } else if (!lhs.getSuffix().equals("") && !rhs.getSuffix().equals("")) {
                String name1 = lhs.getSuffix();
                String name2 = rhs.getSuffix();
                int type = name1.compareTo(name2);
                if (type > 0) {
                    return 1;
                } else if (type < 0) {
                    return -1;
                }
            }
            return 0;
        }
    }

    /**
     * 按名称排序
     */
    private void nameSort() {
        Collections.sort(listData, new NameComparator());
        Collections.sort(listData, new MdirOrFileComparator());
//        Collections.sort(listData, new StartNameComparator());
    }

    /**
     * 初次顺序排序
     */
    public static class NameComparator implements Comparator<FileModel> {
        @Override
        public int compare(FileModel lhs, FileModel rhs) {
            if (lhs == null && rhs == null) return 0;
            if (lhs == null || rhs == null) return lhs == null ? 1 : -1;

            String name1 = lhs.getFileName();
            String name2 = rhs.getFileName();
            if (name1 == null && name2 == null) {
                return 0;
            } else if (name1 == null && name2 != null) {
                return 1;
            } else if (name1 != null && name2 == null) {
                return -1;
            } else if (name1.equals("") && !name2.equals("")) {
                return 1;
            } else if (!name1.equals("") && name2.equals("")) {
                return -1;
            }
            List<String> list1 = getNumbers(name1);
            List<String> list2 = getNumbers(name2);
            if (list1.size() > 0 && list2.size() > 0) {
                String[] text1 = name1.split("\\d+");
                String[] text2 = name2.split("\\d+");
                if ((text1.length == 0 && text2.length == 0)
                        || (text1.length != 0 && text2.length == 0 && text1[0].equals(""))
                        || (text1.length == 0 && text2.length != 0 && text2[0].equals(""))) {
                    String num1 = list1.get(0);
                    String num2 = list2.get(0);
                    if (num1.length() != num2.length()) {
                        return num1.length() > num2.length() ? 1 : -1;
                    }

                    int type = name1.compareTo(name2);
                    if (type > 0) {
                        return 1;
                    } else if (type < 0) {
                        return -1;
                    }
                }

                int len = text1.length > text2.length ? text2.length : text1.length;

                for (int i = 0; i < len; i++) {
                    if (text1[i].equals(text2[i]) && (i < list1.size() && i < list2.size())) {
                        String num1 = list1.get(i);
                        String num2 = list2.get(i);
                        if (num1.length() != num2.length()) {
                            return num1.length() > num2.length() ? 1 : -1;
                        }
                        int type = name1.compareTo(name2);
                        if (type > 0) {
                            return 1;
                        } else if (type < 0) {
                            return -1;
                        }
                    }
                }
            }
            int type = name1.compareTo(name2);
            if (type > 0) {
                return 1;
            } else if (type < 0) {
                return -1;
            }
            return 0;
        }
    }

    public static List<String> getNumbers(String str) {
        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(str);
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }
//    public static List<String> getSpilstr(String str){
//        String[] spl=str.split("\\d+");
//      List<String> list=new ArrayList<>();
//
//        int index=0;
//        for (int i = 0; i < numbers.size(); i++) {
//           int in = str.indexOf(numbers.get(i), index);
//            if (in>index) {
//                list.add(str.substring(index, in));
//            }
//            index=in+numbers.get(i).length();
//        }
//        APPLog.e(list.toString());
//        return list;
//    }

    /**
     * 开始的特殊字符处理
     */
    private static class StartNameComparator implements Comparator<FileModel> {
        @Override
        public int compare(FileModel lhs, FileModel rhs) {
//            String name1 = lhs.getFileName();
//            String name2 = rhs.getFileName();
            String name1 = lhs.getStartAssic();
            String name2 = rhs.getStartAssic();
            if (name1 == null && name2 == null) {
                return 0;
            } else if (name1 == null && name2 != null) {
                return 1;
            } else if (name1 != null && name2 == null) {
                return -1;
            } else if (name1.equals("") && !name2.equals("")) {
                return 1;
            } else if (!name1.equals("") && name2.equals("")) {
                return -1;
            }

            int type = name1.compareTo(name2);
            if (type > 0) {
                return 1;
            } else if (type < 0) {
                return -1;
            }
            return 0;
        }
    }

    public void initTitle(boolean is) {
        if (!is && type == 1 && isrefureshFile) {
            getallFile();
            isrefureshFile = false;
            return;
        } else if (!is && type == 2 && isrefureshPhoto) {
            getallFile();
            isrefureshPhoto = false;
            return;
        }
        if (!this.isHidden()) {
            listener.setTitle(titleName);
            setShowText();
        }
    }

    public void initFragmentTitle() {
        if (type == 1 && isrefureshFile) {
            getallFile();
            isrefureshFile = false;
            return;
        } else if (type == 2 && isrefureshPhoto) {
            getallFile();
            isrefureshPhoto = false;
            return;
        } else if (RefureshPathUtils.getInstance().judgeRefureshPath(filePath)) {
            getallFile();
        }
        listener.setTitle(titleName);
        listener.showIndex(String.valueOf(CurrentIndex + 1) + "/" + totalIndex, sortType);
        setShowText();
    }

    private List<Integer> seletes = new ArrayList<>();

    /**
     * 清空当前选择项
     */
    public void clearSeelcts() {
        seletes.clear();
    }

    /**
     * 获得本fragment下面所有选中的文件
     *
     * @return
     */
    public List<File> getFileSelete() {
        seletes.clear();
        List<File> files = new ArrayList<>();
        int i = 0;
        for (FileModel model : listData) {
            if (model.isSelect) {
                seletes.add(i);
                files.add(model.file);
            }
            i++;
        }
        return files;
    }

    /**
     * 清空所有的选中 1删除，2移动，3复制，0其他
     */
    public void clearAllSelect(int classType) {
        seletes.clear();
        for (FileModel model : listData) {
            model.isSelect = false;
        }
        if ((type == 0 || type == 3) || classType == 1) {
            if (classType == 1) {
//                if (seletes.size() != 0) {
//                    for (int i = 0; i < seletes.size(); i++) {
//                        int position = seletes.get(i) - i;
//                        listData.remove(position);
//                    }
//                    initSonData();
//                }
            }
        } else if (classType != 0) {
            getallFile();
        }
        isSelect = false;
        if (adapter != null) {
            adapter.setShowAelect(isSelect);
        }
    }

    public void deleteData() {
        if (seletes.size() != 0) {
            for (int i = 0; i < seletes.size(); i++) {
                int position = seletes.get(i) - i;
                listData.remove(position);
            }
            initSonData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
