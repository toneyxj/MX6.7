package com.moxi.nexams.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.moxi.nexams.R;
import com.moxi.nexams.adapter.PaperIndexAdapter;
import com.moxi.nexams.cache.ACache;
import com.moxi.nexams.db.newdb.NewExamsSqliteUtils;
import com.moxi.nexams.model.HistoryModel;
import com.moxi.nexams.model.papermodel.PaperModelDesc;
import com.moxi.nexams.utils.TitleUtils;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by Archer on 2017/1/9.
 */
public class MXPaperIndexActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.list_view_paper_index)
    ListView lvPaperIndex;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    private List<PaperModelDesc> listPaperType = new ArrayList<>();
    private PaperIndexAdapter adapter;
    private String paperTitle;
    private int paperDb;
    private boolean isHistory = false;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_paper_index;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化view
     */
    private void init() {

        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        paperTitle = this.getIntent().getStringExtra("paper_title");
        paperDb = this.getIntent().getIntExtra("paper_index", -1);
        tvMidTitle.setText(paperTitle);


        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/nexams/dbdata/";
        String fileName = paperDb + ".db";
        NewExamsSqliteUtils newdb = new NewExamsSqliteUtils(this, filePath, fileName);
        listPaperType = newdb.getAllTypePaper();
        if (listPaperType.size() > 0) {
            adapter = new PaperIndexAdapter(this, listPaperType);
            lvPaperIndex.setAdapter(adapter);
            lvPaperIndex.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    TitleUtils.moveToActivity(MXPaperIndexActivity.this, isHistory, listPaperType.get(position).getPpsMainTitle(),
                            paperDb, listPaperType.get(position).getPpsId(), paperTitle, position, listPaperType);
                }
            });
        }
        getHistory(MXUamManager.queryUser(this));
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                this.finish();
                break;
            default:
                break;
        }
    }

    /**
     * 获取历史纪录
     *
     * @param appSession
     */
    private void getHistory(String appSession) {
        dialogShowOrHide(true, "数据加载中...");
        HashMap<String, String> param = new HashMap<>();
        param.put("rows", "2000");
        param.put("subId", "");
        param.put("appSession", appSession);
        OkHttpUtils.post().url(Constant.HISTORYURL).params(param).build().connTimeOut(10000).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dialogShowOrHide(false, "数据加载中...");
                String tempHistory = ACache.get(MXPaperIndexActivity.this).getAsString(Constant.HISTORYURL);
                if (tempHistory != null) {
                    parseHistoryData(tempHistory, paperDb);
                } else {
                    parseHistoryData("", paperDb);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                dialogShowOrHide(false, "数据加载中...");
                ACache.get(MXPaperIndexActivity.this).put(Constant.HISTORYURL, response);
                parseHistoryData(response, paperDb);
            }
        });
    }

    private boolean parseHistoryData(String data, int cobId) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            int code = jsonObject.optInt("code", -1);
            if (code == 0) {
                JSONObject jsonObject1 = jsonObject.optJSONObject("result");
                JSONArray jsonArray = jsonObject1.optJSONArray("list");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject liObj = jsonArray.getJSONObject(i);
                    if (cobId == liObj.optInt("cobId")) {
                        isHistory = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isHistory = false;
        }
        return isHistory;
    }
}
