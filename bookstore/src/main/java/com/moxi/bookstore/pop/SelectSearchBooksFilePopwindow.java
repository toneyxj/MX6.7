package com.moxi.bookstore.pop;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.FileAdapter;
import com.moxi.bookstore.db.TableConfig;
import com.moxi.bookstore.modle.SearchBookModel;
import com.moxi.bookstore.utils.PathUtils;
import com.moxi.bookstore.utils.SearchFileUtils;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.view.NoGridView;
import com.mx.mxbase.view.SildeFrameLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选择设置扫描文件
 * Created by xj on 2017/7/28.
 */

public class SelectSearchBooksFilePopwindow extends PopupWindow implements View.OnClickListener, SildeFrameLayout.SildeEventListener, FileAdapter.SettingSearchBookListener {
    Context mContext;
    private LayoutInflater mInflater;
    private View mContentView;
    private SelectBookListener listener;
    private int memoryStyle = -1;
    private List<String> roots = PathUtils.getExtSDCardPathList();
    /**
     * 已经添加的目录集合
     */
    private Map<String, SearchBookModel> addedFiles = new HashMap<>();
    private List<SearchBookModel> listData = new ArrayList<>();
    private List<SearchBookModel> sonList = new ArrayList<>();
    /**
     * 当前文件路径
     */
    private String currentPath="";
    private String[] lastPath=new String[2];
    /**
     * 当前显示页数
     */
    private int CurrentIndex = 0;
    /**
     * 总页数
     */
    private int totalIndex = 1;
    private int pageSize = 18;

    public  SelectSearchBooksFilePopwindow(Context context, SelectBookListener listener) {
        super(context);
        this.mContext = context;
        this.listener = listener;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = mInflater.inflate(R.layout.popwin_select_bookfile, null);
        setContentView(mContentView);
        setWidth(MyApplication.ScreenWidth);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable());
        initView();
    }

    private TextView internal_memory;
    private TextView exteral_memory;
    private TextView quit;
    private TextView insure;
    private TextView last_directory;
    private TextView last_page;
    private TextView next_page;
    private TextView all_files;
    private TextView show_index;
    private SildeFrameLayout silde_layout;
    private NoGridView show_files;
    private FileAdapter adapter;

    private void initView() {
        addedFiles=SearchFileUtils.getInstance().getFiles();
        internal_memory = (TextView) mContentView.findViewById(R.id.internal_memory);
        exteral_memory = (TextView) mContentView.findViewById(R.id.exteral_memory);
        quit = (TextView) mContentView.findViewById(R.id.quit);
        insure = (TextView) mContentView.findViewById(R.id.insure);
        last_directory = (TextView) mContentView.findViewById(R.id.last_directory);
        last_page = (TextView) mContentView.findViewById(R.id.last_page);
        next_page = (TextView) mContentView.findViewById(R.id.next_page);
        all_files = (TextView) mContentView.findViewById(R.id.all_files);
        show_index = (TextView) mContentView.findViewById(R.id.show_index);
        silde_layout = (SildeFrameLayout) mContentView.findViewById(R.id.silde_layout);
        show_files = (NoGridView) mContentView.findViewById(R.id.show_files);

        internal_memory.setOnClickListener(this);
        exteral_memory.setOnClickListener(this);
        quit.setOnClickListener(this);
        insure.setOnClickListener(this);

        last_directory.setOnClickListener(this);
        last_page.setOnClickListener(this);
        next_page.setOnClickListener(this);
        all_files.setOnClickListener(this);

        silde_layout.setListener(this);

        onClick(internal_memory);
    }

    public void onBack(){
        if (roots.contains(currentPath)){
            this.dismiss();
        }else {
            String txt=all_files.getText().toString();
            File f=new File(currentPath);
            String path=f.getParent();
            if (txt.equals("取消全选")){
                for (SearchBookModel model : listData) {
                    addedFiles.remove(model.filePath);
                }
                SearchBookModel model=new SearchBookModel().init(currentPath);
                addedFiles.put(model.filePath, model);
            }
            settingCurrentPath(path);
        }
    }

    private void initAdapter() {
        if (adapter == null) {
            adapter = new FileAdapter(mContext, sonList, addedFiles, this);
            show_files.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.internal_memory:
                changeMemoryStyle(0);
                break;
            case R.id.exteral_memory:
                changeMemoryStyle(1);
                break;
            case R.id.quit:
                this.dismiss();
                break;
            case R.id.insure:
                if (addedFiles.size()==0){
                    ToastUtils.getInstance().showToastShort("请选择同步文件夹");
                    return;
                }
                listener.onInsureSelct(addedFiles);
                this.dismiss();
                break;
            case R.id.last_directory:
                if (roots.contains(currentPath)){
                    ToastUtils.getInstance().showToastShort("已经是根目录");
                }else {
                    File f=new File(currentPath);
                    String path=f.getParent();
                    settingCurrentPath(path);
                }
                break;
            case R.id.last_page:
                onSildeEventLeft();
                break;
            case R.id.next_page:
                onSildeEventRight();
                break;
            case R.id.all_files:
                String txt=all_files.getText().toString();
                if (txt.equals("全选")) {
                    for (SearchBookModel model : listData) {
                        addedFiles.put(model.filePath, model);
                    }
                    adapter.updateSelect(show_files);
                    setingAllSelect();
                }else {//取消全选
                    for (SearchBookModel model : listData) {
                        addedFiles.remove(model.filePath);
                    }
                    adapter.updateSelect(show_files);
                    setingAllSelect();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 分配下面数据以刷新
     */
    public void initSonData() {
        sonList.clear();
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
            sonList.addAll(listData);
        } else if (totalIndex - 1 == CurrentIndex) {
            sonList.addAll(listData.subList(CurrentIndex * pageSize, listData.size()));
        } else {
            sonList.addAll(listData.subList(CurrentIndex * pageSize, (CurrentIndex + 1) * pageSize));
        }
        initAdapter();
        show_index.setText(String.valueOf(CurrentIndex+1)+"/"+totalIndex);
    }
    @Override
    public void onSildeEventLeft() {
        if (CurrentIndex > 0 && (CurrentIndex <= totalIndex - 1)) {
            CurrentIndex--;
            initSonData();
        }else {
//            ToastUtils.getInstance().showToastShort("已经是第一页");
        }
    }

    @Override
    public void onSildeEventRight() {
        if (CurrentIndex >= totalIndex - 1) {
//            ToastUtils.getInstance().showToastShort("已经是最后一页");
        } else {
            CurrentIndex++;
            initSonData();
        }
    }

    @Override
    public void onClickItem(SearchBookModel model, int postion) {
        if (model.addType!=0)return;
        if (addedFiles.get(model.filePath)==null){
            settingCurrentPath(model.filePath);
        }else {
            ToastUtils.getInstance().showToastShort("您已勾选");
        }
    }

    @Override
    public void onClickSelect(SearchBookModel model, int postion) {
        SearchBookModel mo=addedFiles.get(model.filePath);
       if (mo==null||mo.addType!=model.addType){
           addedFiles.put(model.filePath,model);
       }else {
           addedFiles.remove(model.filePath);
       }
        setingAllSelect();
        adapter.updateSelect(postion,show_files);
    }

    /**
     * 书籍选择监听按钮
     */
    public interface SelectBookListener {
        void onInsureSelct( Map<String, SearchBookModel> addedFiles);
    }

    private void changeMemoryStyle(int style) {
        File file = new File(roots.get(style));
        if (!file.exists() || file.list() == null) {
            ToastUtils.getInstance().showToastShort("无存储卡，请插入后点击");
            return;
        }
        if (style == memoryStyle) return;
        if (memoryStyle!=-1)
        lastPath[memoryStyle]=currentPath;
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
        if (lastPath[memoryStyle]!=null) {
            settingCurrentPath(lastPath[memoryStyle]);
        }else {
            settingCurrentPath(roots.get(style));
        }
    }

    /**
     * 设置当前显示文件
     * @param path
     */
    private void settingCurrentPath(String path){
        File[] files=new File(path).listFiles();
        if (files==null){
            ToastUtils.getInstance().showToastShort("文件夹不存在");
            return;
        }
        currentPath=path;
        listData.clear();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()&&!files[i].getName().startsWith(".")&&!files[i].getAbsolutePath().equals(TableConfig.E_DOWNLOAD_DIR)) {
                listData.add(new SearchBookModel().init(files[i].getAbsolutePath(), 0));
            }
        }
        Collections.sort(listData, new NameComparator());
        listData.add(0,new SearchBookModel().init(path,1));
        setingAllSelect();
        initSonData();
    }
    private void setingAllSelect(){
        boolean isAll=false;
        for (SearchBookModel model:listData){
            if (addedFiles.get(model.filePath)==null){
                isAll=true;
                break;
            }
        }
        all_files.setText(isAll?"全选":"取消全选");
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
            drawable = mContext.getResources().getDrawable(trueImage);
        } else {
            drawable = mContext.getResources().getDrawable(falseImage);
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
    /**
     * 按名称排序
     */
    private static class NameComparator implements Comparator<SearchBookModel> {
        @Override
        public int compare(SearchBookModel lhs, SearchBookModel rhs) {
            String name1 = lhs.getName().toLowerCase();
            String name2 = rhs.getName().toLowerCase();
            int type = name1.compareTo(name2);
            if (type > 0) {
                return 1;
            } else if (type < 0) {
                return -1;
            }
            return 0;
        }
    }
}
