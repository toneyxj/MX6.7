package com.moxi.bookstore.activity.bookManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.bookManager.LocalAllBookLinearAdapter;
import com.moxi.bookstore.adapter.bookManager.LocalAllBookRecyclerAdapter;
import com.moxi.bookstore.asy.ScanReadFile;
import com.moxi.bookstore.db.EbookDB;
import com.moxi.bookstore.db.SacnReadFileUtils;
import com.moxi.bookstore.db.TableConfig;
import com.moxi.bookstore.db.TableOperate;
import com.moxi.bookstore.modle.BookStoreFile;
import com.moxi.bookstore.modle.SearchBookModel;
import com.moxi.bookstore.pop.SelectSearchBooksFilePopwindow;
import com.moxi.bookstore.utils.PrepareCMS;
import com.moxi.bookstore.utils.SearchFileUtils;
import com.moxi.bookstore.utils.StartUtils;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.LocationBookReadProgressUtils;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.interfaces.LocationInfoListener;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.model.LocationBookInfo;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;
import com.mx.mxbase.view.CustomRecyclerView;
import com.mx.mxbase.view.SildeFrameLayout;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;

/**
 * Created by Archer on 16/8/4.
 */
public class MXStacksActivity extends BaseActivity implements View.OnClickListener, SildeFrameLayout.SildeEventListener {

    @Bind(R.id.move_layout)
    SildeFrameLayout move_layout;
    @Bind(R.id.ll_stacks_back)
    LinearLayout llBack;
    @Bind(R.id.recycler_stacks)
    CustomRecyclerView recyclerStacks;
    @Bind(R.id.tv_stacks_page_index)
    TextView tvPageIndex;
    @Bind(R.id.tv_stacks_total_count)
    TextView tvTotalCount;
    @Bind(R.id.et_stacks_search)
    EditText etSearch;

//    @Bind(R.id.ll_book_stacks_tongbu)
//    LinearLayout llTongbu;
//    @Bind(R.id.rl_stacks_class)
//    RelativeLayout rlClass;
//    @Bind(R.id.rl_stacks_sort)
//    RelativeLayout rlSort;
//    @Bind(R.id.tv_stacks_class)
//    TextView tvStacksSearch;
//    @Bind(R.id.tv_stacks_sort)
//    TextView tvStacksSort;
//@Bind(R.id.rl_change_pai_lie)
//RelativeLayout rlPaiLie;

    @Bind(R.id.search)
    ImageView search;
    @Bind(R.id.refuresh)
    ImageView refuresh;
    @Bind(R.id.book_type)
    ImageView book_type;
    @Bind(R.id.sort_type)
    ImageView sort_type;
    @Bind(R.id.file_show_style)
    ImageView imgPaiLie;

    @Bind(R.id.tv_boos_stacks_title)
    TextView tvTitle;
//    @Bind(R.id.img_pai_lie)
//    ImageView imgPaiLie;

    private List<BookStoreFile> listDate = new ArrayList<>();
    private List<BookStoreFile> middleDates = new ArrayList<>();
    private LocalAllBookRecyclerAdapter adapter;
    private LocalAllBookLinearAdapter linearAdapter;
    /**
     * 当前显示页数
     */
    private int index = 0;
    private Toast mytoast;

    /**
     * 每页显示个数
     */
    private final int pageSize = 12;
    private int linePageSize = 18;
    /**
     * 页面总页数
     */
    private int totalPage;
    private boolean isResultOk = false;
    private long searchTime = 0;
    private int sortType = 0; //0为最近 1为名称 2为当当
    private int searchType = 0; //搜索类型 0为全部图书 1为当当图书
    private boolean NEEDRELOAD = false;
    private int pailieStyle = 0;

    private SelectSearchBooksFilePopwindow booksFilePopwindow;
    /**
     * 开启软键盘
     */
    private boolean openSoftKeybord = false;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_stacks;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        mytoast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mytoast.setGravity(Gravity.CENTER, 0, 0);
        //设置点击监听
        llBack.setOnClickListener(this);

        searchType = share.getInt("book_stacks_search");
        sortType = share.getInt("book_stacks_sort");
        pailieStyle = share.getInt("pailieStyle");

        if (pailieStyle == 0) {
            imgPaiLie.setImageResource(R.mipmap.file_show_style_one);
        } else {
            imgPaiLie.setImageResource(R.mipmap.file_show_style_two);
        }

        move_layout.setListener(this);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Math.abs(searchTime - System.currentTimeMillis()) > 500) {
                    searchTime = System.currentTimeMillis();
                    addData(0);
                }
            }
        });
        addData(0);

//        rlClass.setOnClickListener(this);
//        rlSort.setOnClickListener(this);
//        llTongbu.setOnClickListener(this);
//        rlPaiLie.setOnClickListener(this);

        search.setOnClickListener(this);
        refuresh.setOnClickListener(this);
        book_type.setOnClickListener(this);
        sort_type.setOnClickListener(this);
        imgPaiLie.setOnClickListener(this);

        refuresh.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPop();
                return true;
            }
        });

        LocationBookReadProgressUtils.getInstance(this).setListeners(infoListener);
    }

    LocationInfoListener infoListener = new LocationInfoListener() {
        @Override
        public void onBackInfo(LocationBookInfo info) {
            if (adapter != null && recyclerStacks != null) {
                adapter.updateSelect(info);
            }
            if (linearAdapter != null && recyclerStacks != null) {
                linearAdapter.updateSelect(info);
            }
        }
    };

    private void showPop() {
        if (popWindowsShow(true)) return;
        //长按呼出设置界面
        booksFilePopwindow = new SelectSearchBooksFilePopwindow(MXStacksActivity.this, new SelectSearchBooksFilePopwindow.SelectBookListener() {
            @Override
            public void onInsureSelct(final Map<String, SearchBookModel> addedFiles) {
                dialogShowOrHide(true, "请稍候...");
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SearchFileUtils.getInstance().saveFiles(addedFiles);
                        SacnReadFileUtils.getInstance(MXStacksActivity.this).clearDb();
                        SacnReadFileUtils.getInstance(MXStacksActivity.this).SearchBooks(MXStacksActivity.this, scanReadListner);
                    }
                }, 1500);
            }
        });
        booksFilePopwindow.showAsDropDown(refuresh);
    }

    public boolean popWindowsShow(boolean isDismiss) {
        if (booksFilePopwindow != null && booksFilePopwindow.isShowing()) {
            if (isDismiss)
                booksFilePopwindow.dismiss();
            return true;
        }
        return false;
    }

    private void addData(int index) {
        changeSearchAndSort();
        String search = etSearch.getText().toString().trim();
        this.index = index;
        listDate.clear();

        if (search == null || search.equals("")) {
            if (searchType == 0) { //全部图书
                if (sortType == 0) {//按照最近阅读排序
                    listDate.addAll(SacnReadFileUtils.getInstance(this).getBookStoreSize(0));
                } else {//按照名称排序
                    listDate.addAll(SacnReadFileUtils.getInstance(this).getBookStoreSizeOrderByPinyin(0));
                }
//                if (listDate.size()==0){
//                    refureshData();
//                }
            } else {//当当图书
                if (sortType == 0) {//按照最近阅读排序
                    listDate.addAll(SacnReadFileUtils.getInstance(this).searchDDBookStore(0));
                } else {//按照名称排序
                    listDate.addAll(SacnReadFileUtils.getInstance(this).searchDDBookStoreByFullPinYin(0));
                }
            }

        } else {
            if (searchType == 0) {//搜索全部图书
                listDate.addAll(SacnReadFileUtils.getInstance(this).getSearchBookStoreSize(search, 0));
            } else {//搜索当当图书
                listDate.addAll(TableOperate.getInstance().queryByDDTableName(search, TableConfig.TABLE_NAME));
            }
        }
        APPLog.e(listDate.toString());

        if (sortType != 0) {
            dialogShowOrHide(true, "");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //排序
                    nameSort();
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (isfinish) return;
                            changePage();
                            dialogShowOrHide(false, "");
                        }
                    });
                }
            }).start();
        } else {
            changePage();
        }

    }

    private ScanReadFile.ScanReadListner scanReadListner = new ScanReadFile.ScanReadListner() {
        @Override
        public void onScanReadEnd() {
            if (isfinish)
                return;
            Thread delThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    List<BookStoreFile> list = SacnReadFileUtils.getInstance(MXStacksActivity.this).getBookStoreSize(0);
                    //错误数据筛查删除
                    int index=0;
                    for (BookStoreFile bookStoreFile : list) {
                        APPLog.e("bookStoreFile-index="+index,bookStoreFile.toString());
                        index++;
                        if (bookStoreFile.NoFile() || bookStoreFile.getFile().getName().startsWith(".")) {
                            SacnReadFileUtils.getInstance(MXStacksActivity.this).deleteFile(bookStoreFile.filePath);
                        }
                        if (bookStoreFile.pathMd5 == null || bookStoreFile.pathMd5.equals("")) {
                            DataSupport.delete(BookStoreFile.class, bookStoreFile.id);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isfinish) return;
                            dialogShowOrHide(false, "请稍候...");
                            addData(0);
                        }
                    });
                }
            };
            delThread.start();
        }

        @Override
        public void onScanReadFile(BookStoreFile file) {
        }
    };

    private void changeSearchAndSort() {
        if (searchType == 0) {
//            tvStacksSearch.setText("全部图书");
            tvTitle.setText("全部图书");
//            if (sortType == 0) {
//                tvStacksSort.setText("最近");
//            } else {
//                tvStacksSort.setText("名称");
//            }
        } else {
            tvTitle.setText("当当书库");
//            tvStacksSearch.setText("当当书库");
//            if (sortType == 0) {
//                tvStacksSort.setText("最近");
//            } else {
//                tvStacksSort.setText("名称");
//            }
        }
    }

    /**
     * 修改页面
     */
    private void changePage() {
        APPLog.e("changePage-修改页面");
        Glide.clear(recyclerStacks);
        int size = listDate.size();
        LocationBookReadProgressUtils.getInstance(this).ClearData();
        if (pailieStyle == 0) {
            resetAdapter(size);
        } else {
            setLinearAdapter(size);
        }
    }

    private void setLinearAdapter(int size) {
        totalPage = (size / linePageSize) + ((size % linePageSize == 0) ? 0 : 1);
        if (totalPage <= index) {
            index = totalPage - 1;
        }
        recyclerStacks.setLayoutManager(new GridLayoutManager(this, 2));
        middleDates.clear();
        if (totalPage > 0 && index >= 0) {
            if ((totalPage - 1) > index) {
                middleDates.addAll(listDate.subList(linePageSize * index, linePageSize * (index + 1)));
            } else {
                middleDates.addAll(listDate.subList(linePageSize * index, listDate.size()));
            }
        }
        if (linearAdapter == null) {
            linearAdapter = new LocalAllBookLinearAdapter(middleDates, this, recyclerStacks);
            recyclerStacks.setAdapter(linearAdapter);
            linearAdapter.setOnItemClickLIstener(onItemClickListener);
        } else {
            linearAdapter.notifyDataSetChanged();
        }
        adapter = null;
        tvPageIndex.setText((index + 1) + "/" + totalPage);
        tvTotalCount.setText("总计：" + listDate.size());
    }

    /**
     * 初始化adapter
     */
    private void resetAdapter(int size) {
        totalPage = (size / pageSize) + ((size % pageSize == 0) ? 0 : 1);
        if (totalPage <= index) {
            index = totalPage - 1;
        }
        //修改当前页面的索引值
        recyclerStacks.setLayoutManager(new GridLayoutManager(this, 4));
        middleDates.clear();
        if (totalPage > 0 && index >= 0) {
            if ((totalPage - 1) > index) {
                middleDates.addAll(listDate.subList(pageSize * index, pageSize * (index + 1)));
            } else {
                middleDates.addAll(listDate.subList(pageSize * index, listDate.size()));
            }
        }
        if (adapter == null) {
            adapter = new LocalAllBookRecyclerAdapter(middleDates, this, recyclerStacks);
            recyclerStacks.setAdapter(adapter);
            adapter.setOnItemClickLIstener(onItemClickListener);
        } else {
            adapter.notifyDataSetChanged();
        }
        linearAdapter = null;

        tvPageIndex.setText((index + 1) + "/" + totalPage);
        tvTotalCount.setText("总计：" + listDate.size());
    }

    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            if (popWindowsShow(true)) return;
            String path = middleDates.get(position).filePath;
            File openFile = new File(path);
            if (!openFile.exists() || openFile.length() == 0||!openFile.canRead()) {
                ToastUtils.getInstance().showToastShort("该图书不可用！！");
                SacnReadFileUtils.getInstance(MXStacksActivity.this).deleteFile(path);
                addData(index);
                return;
            }
            EbookDB book = TableOperate.getInstance().queryByPath(TableConfig.TABLE_NAME, path);
            if (book != null) {
                //开始跳转当当阅读器
                StartUtils.OpenDDRead(MXStacksActivity.this, book);
//                share.setCache("Recently", path);
            } else {
                share.setCache("Recently", path);
                APPLog.e("middleDates.get(position).filePath", middleDates.get(position).filePath);
                new PrepareCMS(MXStacksActivity.this).insertCMS(middleDates.get(position).filePath);
                FileUtils.getInstance().openFile(MXStacksActivity.this, middleDates.get(position).getFile());
//                new StartFile(MXStacksActivity.this, path);
                SacnReadFileUtils.getInstance(MXStacksActivity.this).updateIndex(middleDates.get(position));
            }
            isResultOk = true;
        }

        @Override
        public void onItemLongClick(View view, final int position) {
            if (popWindowsShow(true)) return;
            final File f = middleDates.get(position).getFile();
            new AlertDialog(MXStacksActivity.this).builder().setTitle("提示").
                    setMsg("确认移除" + f.getName() + "?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (StringUtils.deleteFile(f)) {
                        SacnReadFileUtils.getInstance(MXStacksActivity.this).deleteFile(f.getAbsolutePath());
                        listDate.remove(pageSize * index + position);
                        changePage();
                        Toastor.showToast(MXStacksActivity.this, "删除成功!");
                        isResultOk = true;
                    } else {
                        Toastor.showToast(MXStacksActivity.this, "删除失败!");
                    }
                }
            }).setPositiveButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            }).show();
        }
    };

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
//        DeviceInfo.currentDevice.showSystemStatusBar(this);
        if (NEEDRELOAD) {
            addData(index);
            NEEDRELOAD = false;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        NEEDRELOAD = true;
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        SacnReadFileUtils.getInstance(this).removeListener(scanReadListner);
    }
private void refureshData(){
    dialogShowOrHide(true, "请稍候...");
    getHandler().postDelayed(new Runnable() {
        @Override
        public void run() {
            SacnReadFileUtils.getInstance(MXStacksActivity.this).SearchBooks(MXStacksActivity.this, scanReadListner);
        }
    }, 500);
}
    @Override
    public void onClick(View view) {
        if (popWindowsShow(true)) return;
        switch (view.getId()) {
            case R.id.ll_stacks_back:
                onBackPressed();
                break;
            case R.id.search:
                etSearch.setVisibility(etSearch.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                break;
            case R.id.refuresh://ll_book_stacks_tongbu
                if (SacnReadFileUtils.getInstance(this).getFirstSetting()) {
                    SacnReadFileUtils.getInstance(this).SetFirstSetting();
                    String hitn = "刷新按钮功能提示";
                    String content = "点击刷新按钮：进行本地书籍刷新\n" +
                            "长按刷新按钮：进入设置刷新的文件夹\n" +
                            "（默认刷新Books文件夹）";
                    insureDialog(hitn, content, "前往设置", "刷新", "setting", new InsureOrQuitListener() {
                        @Override
                        public void isInsure(Object code, boolean is) {
                            if (is) {
                                showPop();
                            } else {
                                refureshData();
                            }
                        }
                    });
                } else {
                    dialogShowOrHide(true, "请稍候...");
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SacnReadFileUtils.getInstance(MXStacksActivity.this).SearchBooks(MXStacksActivity.this, scanReadListner);
                        }
                    }, 1500);
                }
                break;
            case R.id.book_type://rl_stacks_class
                showClassPop();
                break;
            case R.id.sort_type://rl_stacks_sort
                showSortPop();
                break;
            //全部书籍
            case R.id.tv_book_store_all_book:
                if (popupClass != null) {
                    if (!popupClass.isShowing()) {
                    }
                    popupClass.dismiss();
                    popupClass = null;
                }
                if (searchType == 0) return;
                switchPopClass(0);
                break;
            //当当书籍
            case R.id.tv_book_store_dangdang_book:
                if (popupClass != null) {
                    if (!popupClass.isShowing()) {
                    }
                    popupClass.dismiss();
                    popupClass = null;
                }
                if (searchType == 1) return;
                switchPopClass(1);
                break;
            //最近
            case R.id.tv_book_sort_recent:
                if (popupSort != null) {
                    if (!popupSort.isShowing()) {
                    }
                    popupSort.dismiss();
                    popupSort = null;
                }
                if (sortType == 0) return;
                switchPopSort(0);
                break;
            //名称
            case R.id.tv_book_sort_name:
                if (popupSort != null) {
                    if (!popupSort.isShowing()) {
                    }
                    popupSort.dismiss();
                    popupSort = null;
                }
                if (sortType == 1) return;
                switchPopSort(1);
                break;
            case R.id.file_show_style://rl_change_pai_lie
                index = 0;
                if (pailieStyle == 1) {
                    imgPaiLie.setImageResource(R.mipmap.file_show_style_one);
                    pailieStyle = 0;
                } else {
                    imgPaiLie.setImageResource(R.mipmap.file_show_style_two);
                    pailieStyle = 1;
                }
                share.setCache("pailieStyle", pailieStyle);
                changePage();
                break;
            default:
                break;
        }
    }

    /**
     * 分类switch
     *
     * @param sortStyle
     */
    private void switchPopSort(int sortStyle) {
        sortType = sortStyle;
        etSearch.setText("");
        share.setCache("book_stacks_sort", sortType);
        addData(0);
    }

    /**
     * 分类switch
     *
     * @param sType
     */
    private void switchPopClass(int sType) {
        searchType = sType;
        share.setCache("book_stacks_search", searchType);
        etSearch.setText("");
        addData(0);
    }

    PopupWindow popupClass, popupSort;

    private void showClassPop() {
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.popwindow_class, null);
        popupClass = new PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        contentView.findViewById(R.id.tv_book_store_all_book).setOnClickListener(this);
        contentView.findViewById(R.id.tv_book_store_dangdang_book).setOnClickListener(this);
        ImageView imgAllBook = (ImageView) contentView.findViewById(R.id.img_book_search_all_book);
        ImageView imgDDBook = (ImageView) contentView.findViewById(R.id.img_book_search_dangdang_book);
        if (searchType == 0) {
//            tvStacksSearch.setText("全部图书");
            tvTitle.setText("全部图书");
            imgAllBook.setVisibility(View.VISIBLE);
            imgDDBook.setVisibility(View.INVISIBLE);
        } else {
//            tvStacksSearch.setText("当当书库");
            tvTitle.setText("当当书库");
            imgAllBook.setVisibility(View.INVISIBLE);
            imgDDBook.setVisibility(View.VISIBLE);
        }

        popupClass.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    if (popupClass != null) {
                        if (!popupClass.isShowing()) {
                        }
                        popupClass.dismiss();
                        popupClass = null;
                    }
                    return true;
                }
                return false;
            }
        });

        popupClass.setBackgroundDrawable(new BitmapDrawable());
        popupClass.setOutsideTouchable(false);
        popupClass.showAsDropDown(book_type, -10, 0);
    }

    private void showSortPop() {
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.popwindow_sort, null);
        popupSort = new PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        contentView.findViewById(R.id.tv_book_sort_recent).setOnClickListener(this);
        contentView.findViewById(R.id.tv_book_sort_name).setOnClickListener(this);

        ImageView imgSortRecent = (ImageView) contentView.findViewById(R.id.img_book_sort_by_recent);
        ImageView imgSortName = (ImageView) contentView.findViewById(R.id.img_book_sort_by_name);
        if (sortType == 0) {
//            tvStacksSort.setText("最近");
            imgSortRecent.setVisibility(View.VISIBLE);
            imgSortName.setVisibility(View.INVISIBLE);
        } else {
//            tvStacksSort.setText("名称");
            imgSortRecent.setVisibility(View.INVISIBLE);
            imgSortName.setVisibility(View.VISIBLE);
        }

        popupSort.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    if (popupSort != null) {
                        if (!popupSort.isShowing()) {
                        }
                        popupSort.dismiss();
                        popupSort = null;
                    }
                    return true;
                }
                return false;
            }
        });

        popupSort.setBackgroundDrawable(new BitmapDrawable());
        popupSort.setOutsideTouchable(false);
        popupSort.showAsDropDown(sort_type, -10, 0);
    }

    @Override
    public void onBackPressed() {
        if (popWindowsShow(false)) {
            booksFilePopwindow.onBack();
        } else {
            if (isResultOk) {
                Intent intent = getIntent();
//            intent.putStringArrayListExtra("paths", listPaths);
                setResult(RESULT_OK, intent);
            }
            MXStacksActivity.this.finish();
        }
    }


    /**
     * 点击其它地方关闭软键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (StringUtils.isShouldHideInput(v, ev)) {
                setOpenSoftKeybord(false);
                StringUtils.closeIMM(this, v.getWindowToken());
                v.clearFocus();
                return true;
            } else {
                setOpenSoftKeybord(true);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setOpenSoftKeybord(boolean openSoftKeybord) {
        if (openSoftKeybord == this.openSoftKeybord) return;
        this.openSoftKeybord = openSoftKeybord;
        if (!openSoftKeybord)
            etSearch.setVisibility(View.INVISIBLE);
    }

    private void moveLeft() {
        if (index > 0) {
            index--;
            changePage();
        }
    }

    private void moveRight() {
        if (index < totalPage - 1) {
            index++;
            changePage();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            //上一页
            if (popWindowsShow(false)) {
                booksFilePopwindow.onSildeEventLeft();
            } else {
                moveLeft();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            //下一页
            if (popWindowsShow(false)) {
                booksFilePopwindow.onSildeEventRight();
            } else {
                moveRight();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private boolean isfirst = true;

    @Override
    protected void onResume() {
        super.onResume();
//        DeviceInfo.currentDevice.showSystemStatusBar(MXStacksActivity.this);
        dismissFilePop();
        getHandler().sendEmptyMessageDelayed(1000, 500);
//        LocationBookReadProgressUtils.getInstance(this).onReadResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        LocationBookReadProgressUtils.getInstance(this).onReadPasue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissFilePop();
        LocationBookReadProgressUtils.getInstance(this).onDestory();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == 1000 && !isfirst) {
            isfirst = false;
//            EpdController.invalidate(getWindow().getDecorView(), EpdController.UpdateMode.GC);
        }
    }

    private void dismissFilePop() {
        if (booksFilePopwindow != null && booksFilePopwindow.isShowing()) {
            booksFilePopwindow.dismiss();
        }
    }


    /**
     * 按名称排序
     */
    private void nameSort() {
//        Collections.sort(listDate, new NameComparator());
        if (listDate == null || listDate.size() == 0) return;
        Collections.sort(listDate, new StartNameComparator());
    }

    /**
     * 初次顺序排序
     */
    private static class NameComparator implements Comparator<BookStoreFile> {
        @Override
        public int compare(BookStoreFile lhs, BookStoreFile rhs) {
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

    /**
     * 开始的特殊字符处理
     */
    private static class StartNameComparator implements Comparator<BookStoreFile> {
        @Override
        public int compare(BookStoreFile lhs, BookStoreFile rhs) {
            if (lhs == null && rhs == null) return 0;
            if (lhs == null || rhs == null) return lhs == null ? 1 : -1;

            String name1 = lhs.getFullPinyin();
            String name2 = rhs.getFullPinyin();
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

    @Override
    public void onSildeEventLeft() {
        moveLeft();
    }

    @Override
    public void onSildeEventRight() {
        moveRight();
    }
}
