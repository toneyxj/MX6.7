package com.moxi.bookstore.activity.bookManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.bookManager.BookListTopAdapter;
import com.moxi.bookstore.modle.bookManager.BookTypesModel;
import com.moxi.bookstore.modle.bookManager.NetBookDetailsModel;
import com.moxi.bookstore.modle.bookManager.NetBookList;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.http.MXHttpHelper;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.GlideUtils;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.CustomRecyclerView;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

/**
 * 网上书城列表
 * Created by Archer on 16/8/3.
 */
public class MXNetBookListActivity extends BaseActivity implements GestureDetector.OnGestureListener,
        View.OnClickListener {

    @Bind(R.id.recycler_net_book_list_top)
    RecyclerView recyclerTop;
    @Bind(R.id.custom_recycler_view_net_book_list_content)
    CustomRecyclerView recyclerContent;
    @Bind(R.id.ll_net_book_list_back)
    LinearLayout llBack;
    @Bind(R.id.img_net_book_list_left)
    ImageView imgPageLeft;
    @Bind(R.id.img_net_book_list_right)
    ImageView imgPageRight;
    @Bind(R.id.tv_net_book_list_page_count)
    TextView tvPageCount;

    private BookTypesModel bookTypes;
    private BookListTopAdapter topAdapter;
    private BookListContentAdapter contentAdapter;
    private List<BookTypesModel.BookType> result;
    private List<NetBookList.BookList.Book> listBook;
    private MXHttpHelper httpHelper;
    private NetBookList netBookList;
    private GestureDetector gestureDetector = null;

    //请求列表参数
    private String rows = "12";//每页显示条数
    private int page = 1;//页码
    private String bookTypeId = "";//书籍类别id，不传为查询所有类别
    private String name = "";//书籍名称，用于模糊查找

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.arg1 == 1001) {
            if (msg.what == 0) {
                netBookList = (NetBookList) msg.obj;
                if (netBookList == null) {
                    return;
                }
                listBook = netBookList.getResult().getList();
                contentAdapter = new BookListContentAdapter(this, listBook);
                recyclerContent.setAdapter(contentAdapter);
                settingView();
                contentAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        httpHelper.postStringBack(1002, Constant.GET_BOOK_DETAILS + netBookList.
                                        getResult().getList().get(position).getId(),
                                null, getHandler(), NetBookDetailsModel.class);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });
            } else {
                Toastor.showToast(MXNetBookListActivity.this, msg.obj.toString());
            }
        } else if (msg.arg1 == 1002) {
            if (msg.what == 0) {
                NetBookDetailsModel netBookDetailsModel = (NetBookDetailsModel) msg.obj;
                Intent details = new Intent(MXNetBookListActivity.this, MXBookDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("net_book_details", netBookDetailsModel);
                details.putExtras(bundle);
                startActivity(details);
            } else {
                Toastor.showToast(MXNetBookListActivity.this, msg.obj.toString());
            }
        }
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_net_book_list;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        httpHelper = MXHttpHelper.getInstance(this);
        llBack.setOnClickListener(this);
        //获取上一个界面传递过来的数据
        Intent preIntent = this.getIntent();
        bookTypes = (BookTypesModel) preIntent.getSerializableExtra("net_book_type");
        int bookTypeIndex = preIntent.getIntExtra("net_book_type_id", -1);
        //content recycler初始化
        gestureDetector = new GestureDetector(this, this);
        recyclerContent.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerContent.setGestureDetector(gestureDetector);
        if (bookTypes == null) {
            return;
        }
        recyclerTop.setLayoutManager(new GridLayoutManager(this, 6));
        try {
            result = bookTypes.getResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        topAdapter = new BookListTopAdapter(this, result);
        recyclerTop.setAdapter(topAdapter);
        topAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                for (int i = 0; i < result.size(); i++) {
                    if (position == i) {
                        result.get(i).setIndex(true);
                    } else {
                        result.get(i).setIndex(false);
                    }
                }
                topAdapter.notifyDataSetChanged();
                page = 1;
                try {
                    bookTypeId = bookTypes.getResult().get(position).getId();
                    if (bookTypeId == null) {
                        bookTypeId = "";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getBookList(rows, page, bookTypeId, name);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        //获取书籍数据
        if (bookTypeIndex != -1) {
            bookTypeId = bookTypes.getResult().get(bookTypeIndex).getId();
            if (bookTypeId == null) {
                bookTypeId = "";
            }
            bookTypes.getResult().get(bookTypeIndex).setIndex(true);
        } else {
            bookTypes.getResult().get(0).setIndex(true);
        }
        getBookList(rows, page, bookTypeId, name);
    }

    /**
     * 从服务器获取书籍列表
     *
     * @param rows       每页显示条数
     * @param page       页码
     * @param bookTypeId 书籍类型
     * @param name       书籍名称，用于模糊查询
     */
    private void getBookList(String rows, int page, String bookTypeId, String name) {
        HashMap<String, String> getBooklist = new HashMap<>();
        getBooklist.put("rows", rows);
        getBooklist.put("page", page + "");
        getBooklist.put("bookTypeId", bookTypeId);
        getBooklist.put("name", name);
        httpHelper.postStringBack(1001, Constant.GET_BOOK_LIST, getBooklist, getHandler(), NetBookList.class);
    }

    private void settingView() {
        imgPageLeft.setOnClickListener(this);
        imgPageRight.setOnClickListener(this);
        try {
            tvPageCount.setText(page + "/" + netBookList.getResult().getPageCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        float minMove = 60;        //最小滑动距离
        float minVelocity = 0;      //最小滑动速度
        float beginX = motionEvent.getX();
        float endX = motionEvent1.getX();
        float beginY = motionEvent.getY();
        float endY = motionEvent1.getY();

        if (beginX - endX > minMove && Math.abs(v) > minVelocity) {   //左滑
            if (netBookList != null) {
                if (page < Integer.parseInt(netBookList.getResult().getPageCount())) {
                    page++;
                    getBookList(rows, page, bookTypeId, name);
                }
            }
        } else if (endX - beginX > minMove && Math.abs(v) > minVelocity) {   //右滑
            if (page > 1) {
                page--;
                getBookList(rows, page, bookTypeId, name);
            }
        } else if (beginY - endY > minMove && Math.abs(v1) > minVelocity) {   //上滑
        } else if (endY - beginY > minMove && Math.abs(v1) > minVelocity) {   //下滑
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_net_book_list_back:
                this.finish();
                break;
            case R.id.img_net_book_list_left:
                if (page > 1) {
                    page--;
                    getBookList(rows, page, bookTypeId, name);
                }
                break;
            case R.id.img_net_book_list_right:
                try {
                    if (page < Integer.parseInt(netBookList.getResult().getPageCount())) {
                        page++;
                        getBookList(rows, page, bookTypeId, name);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public class BookListContentAdapter extends RecyclerView.Adapter {

        private Context context;
        private List<NetBookList.BookList.Book> listBook;
        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public BookListContentAdapter(Context context, List<NetBookList.BookList.Book> listBook) {
            this.context = context;
            this.listBook = listBook;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_net_book_list_content_item, parent, false);
            return new ContentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            ((ContentViewHolder) holder).tvBookName.setText(listBook.get(position).getName());
            ((ContentViewHolder) holder).tvBookAuthor.setText(listBook.get(position).getDesc());
            GlideUtils.getInstance().loadImage(context,((ContentViewHolder) holder).imgBookPic,Constant.HTTP_HOST + listBook.get(position).getCoverImage());
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return listBook != null ? listBook.size() : 0;
        }

        class ContentViewHolder extends RecyclerView.ViewHolder {
            TextView tvBookName, tvBookAuthor;
            ImageView imgBookPic;

            public ContentViewHolder(View itemView) {
                super(itemView);
                tvBookAuthor = (TextView) itemView.findViewById(R.id.tv_net_book_list_content_author);
                tvBookName = (TextView) itemView.findViewById(R.id.tv_net_book_list_content_name);
                imgBookPic = (ImageView) itemView.findViewById(R.id.img_net_book_list_content);
            }
        }
    }
}
