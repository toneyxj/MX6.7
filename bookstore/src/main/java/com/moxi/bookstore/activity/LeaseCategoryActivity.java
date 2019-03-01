package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.CategoryMainAdapter;
import com.moxi.bookstore.adapter.CategorySonAdapter;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.modle.mediaCategory.CatetoryList;
import com.moxi.bookstore.modle.mediaCategory.ZYCategotyModel;
import com.moxi.bookstore.request.ReuestKeyValues;
import com.moxi.bookstore.request.json.Connector;
import com.moxi.bookstore.request.json.JsonAnalysis;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.view.NoGridView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class LeaseCategoryActivity extends BookStoreBaseActivity implements View.OnClickListener {
    /**
     * 启动筛选租阅书籍
     *
     * @param activity
     * @param categotyModel
     */
    public static void startLeaseCategory(Activity activity, ZYCategotyModel categotyModel) {
        Intent intent = new Intent(activity, LeaseCategoryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("mdoel", categotyModel);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, 10);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_lease_category;
    }

    private ZYCategotyModel categotyModel;

    @Bind(R.id.back_rl)
    RelativeLayout back_rl;
    @Bind(R.id.gride_layout)
    LinearLayout gride_layout;
    @Bind(R.id.main_gride)
    NoGridView main_gride;
    @Bind(R.id.son_gride)
    GridView son_gride;

    //无数据布局
    @Bind(R.id.error_body)
    RelativeLayout error_body;
    @Bind(R.id.reflash_tv)
    TextView reflash_tv;

    @Bind(R.id.reset)
    TextView reset;
    @Bind(R.id.insure)
    TextView insure;

    private List<ZYCategotyModel> categotyModels = new ArrayList<>();
    private List<CatetoryList> categorysonList = new ArrayList<>();
    private CategoryMainAdapter mainAdapter;
    private CategorySonAdapter sonAdapter;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Bundle bundle = savedInstanceState;
        if (bundle == null) {
            bundle = getIntent().getExtras();
        }
        categotyModel = (ZYCategotyModel) bundle.getSerializable("mdoel");


        back_rl.setOnClickListener(this);
        reflash_tv.setOnClickListener(this);

        reset.setOnClickListener(this);
        insure.setOnClickListener(this);

        main_gride.setOnItemClickListener(mainListener);
        son_gride.setOnItemClickListener(sonListener);

        getData();
    }

    private void getData() {
        gride_layout.setVisibility(View.GONE);
        error_body.setVisibility(View.GONE);
        String code = Connector.getInstance().mediaCategory;
        String result = "";
        try {
            result = FileUtils.getInstance().readFile(FileUtils.getInstance().getCacheMksPath()+code);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result == null || result.equals("")) {
            List<ReuestKeyValues> valuePairs = new ArrayList<>();
            valuePairs.add(new ReuestKeyValues("action", "mediaCategory"));
            valuePairs.add(new ReuestKeyValues("channelType", "DZS"));
            valuePairs.add(new ReuestKeyValues("start", "0"));
            valuePairs.add(new ReuestKeyValues("end", "19"));
            valuePairs.add(new ReuestKeyValues("level", "5"));
            getData(valuePairs, code, Connector.getInstance().url, true, "");
        } else {
            Success(result,code);
        }
    }

    @Override
    public void Success(String result, String code) {
        if (code.equals(Connector.getInstance().mediaCategory)) {
            gride_layout.setVisibility(View.VISIBLE);
            error_body.setVisibility(View.GONE);
            jsonData(result);

            try {
                FileUtils.getInstance().writeFile(FileUtils.getInstance().getCacheMksPath()+code, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void jsonData(String result) {
        categotyModels.addAll(JsonAnalysis.getInstance().getZYCategotyModels(result));
        initMainAdater();
    }

    private void initMainAdater() {
        if (mainAdapter == null) {
            mainAdapter = new CategoryMainAdapter(this, categotyModels, getCode(true), selectCategoryListener);
            main_gride.setAdapter(mainAdapter);
        } else {
            mainAdapter.notifyDataSetChanged();
        }
    }

    private void initSonAdapter(List<CatetoryList> list) {
        categorysonList.clear();
        categorysonList.addAll(list);
        if (sonAdapter == null) {
            sonAdapter = new CategorySonAdapter(this, categorysonList, getCode(false));
            son_gride.setAdapter(sonAdapter);
        } else {
            sonAdapter.notifyDataSetChanged();
        }
    }

    private String getCode(boolean isMain) {
        if (categotyModel != null) {
            if (isMain) {
                return categotyModel.code;
            } else {
                if (categotyModel.catetoryList != null && categotyModel.catetoryList.size() > 0) {
                    return categotyModel.catetoryList.get(0).code;
                }
            }
        }
        return "";
    }

    @Override
    public void fail(String code) {
        if (code.equals(Connector.getInstance().mediaCategory)) {
            gride_layout.setVisibility(View.GONE);
            error_body.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_rl:
                onBackPressed();
                break;
            case R.id.reflash_tv:
                getData();
                break;
            case R.id.reset:
                categotyModel = null;
                back();
                break;
            case R.id.insure:
                back();
                break;
            default:
                break;
        }
    }

    private void back() {
        Intent intent = new Intent();
        intent.putExtra("model", categotyModel);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    CategoryMainAdapter.SelectCategoryListener selectCategoryListener = new CategoryMainAdapter.SelectCategoryListener() {
        @Override
        public void onSelectCategory(List<CatetoryList> catetoryList) {
            initSonAdapter(catetoryList);
        }
    };
    AdapterView.OnItemClickListener mainListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mainAdapter.setCode(categotyModels.get(position).code);
            categotyModel = mainAdapter.getCategory();
            if (sonAdapter != null)
                sonAdapter.code = "";
        }
    };
    AdapterView.OnItemClickListener sonListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            sonAdapter.setCode(categorysonList.get(position).code);
            categotyModel.catetoryList = new ArrayList<>();
            categotyModel.catetoryList.add(categorysonList.get(position));
        }
    };

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
        outState.putSerializable("mdoel", categotyModel);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

}
