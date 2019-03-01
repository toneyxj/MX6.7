package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.moxi.bookstore.BookstoreApplication;
import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.CartAdapter;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.Cart;
import com.moxi.bookstore.bean.DeleteShoppingCart;
import com.moxi.bookstore.bean.MakeOrderData;
import com.moxi.bookstore.http.HttpManager;
import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.deal.CartListDeal;
import com.moxi.bookstore.http.deal.DoStoreDeal;
import com.moxi.bookstore.http.deal.MakeOrderDeal;
import com.moxi.bookstore.http.deal.ParamsMap;
import com.moxi.bookstore.http.entity.BaseDeal;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.http.subscribers.ProgressSubscriber;
import com.moxi.bookstore.interfacess.ClickPosition;
import com.moxi.bookstore.interfacess.OnFlingListener;
import com.moxi.bookstore.modle.getEbookOrderFlowV2_Data;
import com.moxi.bookstore.utils.ToolUtils;
import com.moxi.bookstore.view.HSlidableListView;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;

public class CartActivity extends BookStoreBaseActivity implements OnFlingListener, ClickPosition {

    @Bind(R.id.booklist_lv)
    HSlidableListView bookslv;
    @Bind(R.id.title)
    TextView catetory_title;
    @Bind(R.id.price)
    TextView totalPrice;
    @Bind(R.id.edit_end_tv)
    TextView edit_end_tv;
    @Bind(R.id.checkBox)
    CheckBox checkBox;
    @Bind(R.id.sumbit_tv)
    TextView sumbit_tv;
    CartAdapter booksadapter;
    List<Cart.ProductsBean> data, pageData;
    String type, group, chanelname;
    String cartId, token, deviceNo;
    int currentPage, pageCunt, resultCount;
    String productIds, selectTitles;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_cart;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Intent intent = getIntent();
        type = intent.getStringExtra("chaneltype");
        APPLog.e("channelType" + type);
        group = intent.getStringExtra("group");
        chanelname = intent.getStringExtra("chaneltitle");
        deviceNo = BookstoreApplication.getDeviceNO();
        pageData = new ArrayList<>();
        initView(null);
    }

    private void initView(List<Cart.ProductsBean> data) {
        currentPage = 1;
        if (data == null) {

            bookslv.setDivider(null);
            booksadapter = new CartAdapter(this, this);
            bookslv.setAdapter(booksadapter);
            bookslv.setOnFlingListener(this);

            findViewById(R.id.addfavor_tv).setVisibility(View.GONE);
            findViewById(R.id.del_tv).setVisibility(View.GONE);
            findViewById(R.id.edit_ll).setVisibility(View.VISIBLE);

            findViewById(R.id.edit_end_tv).setVisibility(View.GONE);
            findViewById(R.id.edit_tv).setVisibility(View.VISIBLE);

        } else if (data != null) {

            resultCount = data.size();
            getpageCunt();
            booksadapter.setData(data);
            catetory_title.setText("购物车（ " + data.size() + " ）" + "共" + currentPage + "/" + pageCunt + " 页");
        }

    }

    private void getSelectPrice() {
        if (getSelectData()) {
            makeOrder(true);
        }
//        Double priceDouble = 0.0;
//        for (Cart.ProductsBean bean : data) {
//            if (bean.isChecked()) {
//                Double price = Double.parseDouble(bean.getPrice());
//                priceDouble += price;
//            }
//        }
//       totalPrice.setText("￥：" + ToolUtils.getIntence().formatPrice(priceDouble/100));
    }

    public void editCart(View v) {
        switch (v.getId()) {
            case R.id.edit_tv:
                findViewById(R.id.addfavor_tv).setVisibility(View.VISIBLE);
                findViewById(R.id.del_tv).setVisibility(View.VISIBLE);
                findViewById(R.id.edit_ll).setVisibility(View.INVISIBLE);
                findViewById(R.id.edit_end_tv).setVisibility(View.VISIBLE);
                edit_end_tv.setText("编辑");
                findViewById(R.id.edit_tv).setVisibility(View.GONE);
                break;
            case R.id.edit_end_tv:
                findViewById(R.id.addfavor_tv).setVisibility(View.GONE);
                findViewById(R.id.del_tv).setVisibility(View.GONE);
                findViewById(R.id.edit_ll).setVisibility(View.VISIBLE);
                findViewById(R.id.edit_end_tv).setVisibility(View.GONE);
                findViewById(R.id.edit_tv).setVisibility(View.VISIBLE);
                break;
        }

    }

    private void getToken() {
        if (ToolUtils.getIntence().hasLogin(this)) {
            if (token==null||!ToolUtils.getIntence().getToken(this).getToken().equals(token)) {
                token = ToolUtils.getIntence().getToken(this).getToken();
                getCartId();
            }
        }

    }

    private void getCartId() {
        HashMap<String, Object> pramers = new ParamsMap(this);
        //pramers.put("action", "listShoppingCart");
        pramers.put("deviceType", "Android");
        pramers.put("token", token);
        CartListDeal IdDeal = new CartListDeal(new ProgressSubscriber(CartIdListener, this), pramers);
        HttpManager manager = HttpManager.getInstance();
        manager.doHttpDeal(IdDeal);
        showDialog("加载中...");
    }

    private HttpOnNextListener CartIdListener = new HttpOnNextListener<Cart>() {
        @Override
        public void onNext(Cart cart) {
            if (isfinish) return;
            hideDialog();
            cartId = cart.getCartId();
            APPLog.e("getCartId",cartId);
            getSaleData();
        }

        @Override
        public void onError() {
            if (isfinish) return;
            hideDialog();
            APPLog.e("getCartId failed");
        }
    };

    public void delFromCart(View v) {

        List<Cart.ProductsBean> checkBeens = booksadapter.getChickItems();
        String productIds = "";
        for (Cart.ProductsBean checkBeen : data) {
            if (checkBeen.isChecked())
                productIds += "," + checkBeen.getMediaId();
        }
        if (TextUtils.isEmpty(productIds)) {
            ToastUtil("未添加书籍");
            totalPrice.setText("￥：0.00");
            return;
        }
        productIds = productIds.substring(1);

        final HashMap<String, Object> params = new ParamsMap(this);
        //params.put("action", "deleteShoppingCart");
        //params.put("cartId", "1609271123042346");
        APPLog.e("cartId:" + cartId);
        APPLog.e("productIds:" + productIds);
        APPLog.e("token:" + token);
        params.put("cartId", cartId);
        params.put("productIds", productIds);
        params.put("token", token);
        HttpManager manager = HttpManager.getInstance();
        manager.doHttpDeal(new BaseDeal() {
            @Override
            public Observable getObservable(HttpService methods) {
                return methods.deleteShoppingCart(params);
            }

            @Override
            public Subscriber getSubscirber() {
                return new ProgressSubscriber(
                        new HttpOnNextListener<DeleteShoppingCart>() {
                            @Override
                            public void onError() {
                                hideDialog();
                            }

                            @Override
                            public void onNext(DeleteShoppingCart o) {
                                hideDialog();
                                APPLog.e("deletcartlist result:" + o.getResult());
                                List<Cart.ProductsBean> checkBeens = booksadapter.getChickItems();
                                List<Cart.ProductsBean> newBeans = new ArrayList<Cart.ProductsBean>();

                                for (Cart.ProductsBean bean : data) {
                                   /* boolean isDel = false;
                                    for (Cart.ProductsBean delBean : checkBeens) {
                                        if (bean.getMediaId() == delBean.getMediaId()) {
                                            isDel = true;
                                            break;
                                        }
                                    }
                                    if (!isDel) {
                                        newBeans.add(bean);
                                    }*/
                                    if (!bean.isChecked())
                                        newBeans.add(bean);
                                }
                                data = newBeans;
                                initView(data);
                                totalPrice.setText("￥：0.00");
                                checkBox.setChecked(false);
                                edit_end_tv.setText("完成");
                            }
                        }, CartActivity.this);
            }

        });
        showDialog("加载中...");
    }

    public void allCheck(View v) {//全选
        if (isfinish || data == null) return;
        CheckBox checkBox = (CheckBox) v;
        for (Cart.ProductsBean bean : data) {
            bean.setChecked(checkBox.isChecked());
        }
        booksadapter.setData(data);
        currentPage = 1;
        catetory_title.setText("购物车（ " + data.size() + " ）" + "共" + currentPage + "/" + pageCunt + " 页");
        getSelectPrice();
    }

    private boolean isAllCheck() {
        for (Cart.ProductsBean bean : data) {
            if (!bean.isChecked()) {
                checkBox.setChecked(false);
                return false;
            }
        }
        return true;
    }

    /**
     * 提交订单
     *
     * @param v
     */
    public void sumbit(View v) {
        if (getSelectData()) {
            sumbit_tv.setClickable(false);
            makeOrder(false);
        }
    }

    private boolean getSelectData() {
        List<Cart.ProductsBean> beens = new ArrayList<>();
        for (Cart.ProductsBean bean : data) {
            if (bean!=null&&bean.isChecked())
                beens.add(bean);
        }
        if (null == beens || 0 == beens.size()) {
            ToastUtil("未添加书籍!");
            totalPrice.setText("￥：0.00");
            return false;
        } else {
            StringBuilder sbName = new StringBuilder();
            StringBuilder sb = new StringBuilder();
            StringBuilder arraysb = new StringBuilder();
//            sb.append("\"");
            arraysb.append("[");
            for (int i = 0; i < beens.size(); i++) {
                Cart.ProductsBean bean = beens.get(i);
                String item = "{\"productId\":\"" + bean.getMediaId() + "\",\"saleId\":\"" + bean.getSaleId() + "\",\"cId\":\"\"}";
                arraysb.append(item);
                if (i < beens.size() - 1)
                    arraysb.append(",");
                if (i > 0) {
                    sb.append("," + bean.getSaleId());
                    sbName.append("+" + bean.getTitle());
                } else {
                    sb.append(bean.getSaleId());
                    sbName.append(bean.getTitle());
                }
            }
//            sb.append("\"");
            arraysb.append("]");
            productArray = arraysb.toString();
            productIds = sb.toString();
            selectTitles = sbName.toString();
            return true;
        }
    }

    String productArray;

    @Override
    public void onActivityStarted(Activity activity) {
        if (null != cartId && null != token) {
            // getSaleData();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        getToken();
        getSaleData();
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        setResult(MyActivity.CARTL_RES);
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    /**
     * 请求购物车列表
     */
    private void getSaleData() {
        final HashMap<String, Object> params = new ParamsMap(this);
        //params.put("action", "listShoppingCart");
        if (StringUtils.isNull(cartId))return;
        params.put("cartId", cartId);
        params.put("token", token);
        HttpManager manager = HttpManager.getInstance();
        manager.doHttpDeal(new BaseDeal() {
            @Override
            public Observable getObservable(HttpService methods) {
                return methods.listShoppingCart(params);
            }

            @Override
            public Subscriber getSubscirber() {
                return new ProgressSubscriber(
                        new HttpOnNextListener<Cart>() {
                            @Override
                            public void onError() {
                                if (isfinish) return;
                                hideDialog();
                            }

                            @Override
                            public void onNext(Cart o) {
                                if (isfinish) return;
                                hideDialog();
                                data = o.getProducts();
                                initView(data);
                            }
                        }, CartActivity.this);
            }

        });
        showDialog("加载购物车...");
    }

    public void addFavor(View v) {
        if (getSelectData()) {
            if (ToolUtils.getIntence().hasLogin(this)) {
                DoStoreDeal storeDeal = new DoStoreDeal(new ProgressSubscriber(addFavorListener, CartActivity.this),
                        productIds, token);
                HttpManager manager = HttpManager.getInstance();
                manager.doHttpDeal(storeDeal);
                APPLog.e("收藏ids:" + productIds);
                showDialog("加载中...");
            }
        }
    }

    private HttpOnNextListener addFavorListener = new HttpOnNextListener() {
        @Override
        public void onNext(Object o) {
            if (isfinish) return;
            hideDialog();
            ToastUtil("收藏成功");
            edit_end_tv.setText("完成");
        }

        @Override
        public void onError() {
            if (isfinish) return;
            hideDialog();
        }
    };

    public void goBack(View v) {
        finish();
    }

    public void searchBook(View v) {
        startActivity(new Intent(CartActivity.this, SearchBookActivity.class));

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onLeftFling() {

        APPLog.e("go next page" + currentPage);

        if (currentPage < pageCunt) {
            currentPage++;
            if (pageData.size() != 0) {
                pageData.clear();
            }
            for (int i = (currentPage - 1) * pageCount; i < (currentPage * pageCount > resultCount ?
                    resultCount : currentPage * pageCount); i++) {
                APPLog.e("i" + currentPage);
                pageData.add(data.get(i));
            }
            APPLog.e(resultCount + "pagedata.size:" + pageData.size());
            booksadapter.setData(pageData);
            catetory_title.setText("购物车（ " + data.size() + " ）" + "共" + currentPage + "/" + pageCunt + " 页");
        } else
            ToastUtil("最后一页");

    }

    @Override
    public void onRightFling() {
        APPLog.e("go last page" + currentPage);

        if (currentPage > 1) {
            currentPage--;
            if (pageData.size() != 0) {
                pageData.clear();
            }
            for (int i = (currentPage - 1) * pageCount; i < currentPage * pageCount; i++) {
                pageData.add(data.get(i));
            }
            booksadapter.setData(pageData);
            catetory_title.setText("购物车（ " + data.size() + " ）" + "共" + currentPage + "/" + pageCunt + " 页");
        } else
            ToastUtil("首页");

    }

    /**
     * 每页显示书籍数量
     */
    private int pageCount=4;
    public void getpageCunt() {
        if (0 == data.size()) {
            pageCunt = 1;
        } else {
            int n = data.size() % pageCount;
            if (n == 0)
                pageCunt = data.size() / pageCount;
            else
                pageCunt = data.size() / pageCount + 1;
            APPLog.e("pagecunt:" + pageCunt);
        }
    }

    @Override
    public void click(int position) {
        getSelectPrice();
        if (isAllCheck())
            checkBox.setChecked(true);
    }

    private boolean isMathPrice = false;

    private void makeOrder(boolean isMathPrice) {
        if (ToolUtils.getIntence().showBindingDDUser(this)) {
            this.isMathPrice = isMathPrice;
            MakeOrderDeal makeOrderDeal = new MakeOrderDeal(new ProgressSubscriber(orderListener, this),
                    productIds, token, deviceNo, false);
            HttpManager manager = HttpManager.getInstance();
            manager.doHttpDeal(makeOrderDeal);
            if (!isMathPrice) {
                showDialog(isMathPrice ? "" : "创建订单");
            }
        }
    }

    String orderId, total, key, payable;
    private HttpOnNextListener orderListener = new HttpOnNextListener<MakeOrderData>() {
        @Override
        public void onNext(MakeOrderData data) {
            if (isfinish)return;
            sumbit_tv.setClickable(true);
            hideDialog();
            getEbookOrderFlowV2_Data result = data.getResult().getEbookOrderFlowV2.data;
//            OrderResult result = data.getResult().getSubmitOrder().getData().getResult();
            if (null != result) {
//                orderId = result.getOrder_id();
//                total = result.getTotal();
//                payable = result.getPayable();//铃铛数
                int totalPrice = result.totalPrice;
                int payableprice = result.payable;
                total = String.valueOf(totalPrice / 100.0);
                payable = String.valueOf(payableprice / 100.0);
                key = result.key;
                APPLog.e("makeOrder success: " + orderId);
                if (visibale) {
                    if (isMathPrice) {
                        String money="总计￥：" + ToolUtils.getIntence().formatPrice(payableprice / 100.0);
                        if (totalPrice > payableprice) {
                            money+="    已优惠￥："+ToolUtils.getIntence().formatPrice((totalPrice-payableprice)/ 100.0);
                        }
                        CartActivity.this.totalPrice.setText(money);
                    } else {
                        goPay();
                        finish();
                    }
                }
            } else
                showToast("结算失败,请重试");
        }

        @Override
        public void onError() {
            if (isfinish)return;
            sumbit_tv.setClickable(true);
            hideDialog();
        }
    };

    private void goPay() {
//        Intent intent=new Intent(this,PayActivity.class);
//        intent.putExtra("orderId",orderId);
//        intent.putExtra("total",total);
//        intent.putExtra("payable",payable);
//        intent.putExtra("key",key);
//        intent.putExtra("token",token);
//        intent.putExtra("CartId",cartId);
//        intent.putExtra("productIds",productIds);
//        intent.putExtra("bookName",selectTitles);
//        intent.putExtra("productArray",productArray);
//        intent.putExtra("deviceNo",deviceNo);
//        startActivity(intent);
        PayActivity.startPayActivity(this, "", productIds, payable, total, key, token, productArray, deviceNo, selectTitles, cartId, 0);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {//上一页
            onRightFling();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {//下一页
            onLeftFling();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

}
