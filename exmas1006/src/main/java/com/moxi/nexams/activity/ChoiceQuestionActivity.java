package com.moxi.nexams.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.nexams.R;
import com.moxi.nexams.adapter.AllOptionAdapter;
import com.moxi.nexams.adapter.ChoiceOptionAdapter;
import com.moxi.nexams.cache.ACache;
import com.moxi.nexams.db.newdb.NewExamsSqliteUtils;
import com.moxi.nexams.model.papermodel.DetailsTestModel;
import com.moxi.nexams.model.papermodel.LitterSelectModel;
import com.moxi.nexams.model.papermodel.PaperDetailsModel;
import com.moxi.nexams.utils.FullyLinearLayoutManager;
import com.moxi.nexams.utils.MxgsaTagHandler;
import com.moxi.nexams.view.CustomScrollView;
import com.moxi.nexams.view.PaintInvalidateRectView;
import com.moxi.nexams.view.WarpLayoutOnClickListener;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.Base64Utils;
import com.mx.mxbase.utils.DividerItemDecoration;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.Toastor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;

/**
 * Created by Archer on 2017/1/9.
 */
public class ChoiceQuestionActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.recycler_option)
    RecyclerView recyclerOption;
    @Bind(R.id.tv_chose_exams_title_view)
    TextView tvTitleView;
    @Bind(R.id.ll_page_left)
    LinearLayout llPageLeft;
    @Bind(R.id.ll_page_right)
    LinearLayout llPageRight;
    @Bind(R.id.tv_page_count)
    TextView tvPageCount;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.tv_base_right)
    TextView tvRight;
    @Bind(R.id.ll_base_right)
    LinearLayout llRight;
    @Bind(R.id.customScrollview)
    CustomScrollView customScrollView;
    @Bind(R.id.paint_invalidate_rectview)
    PaintInvalidateRectView paintInvalidateRectView;

    private List<PaperDetailsModel> listDetails = new ArrayList<>();
    private int page, ppsId;
    private String paperTitle;
    private ACache aCache;
    private String filePath, fileName;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_choice_question;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    private void init() {
        aCache = ACache.get(this);
        int paperDb = getIntent().getIntExtra("paper_index", -1);
        ppsId = getIntent().getIntExtra("pps_id", -1);
        paperTitle = this.getIntent().getStringExtra("paper_title");
        page = 0;
        tvMidTitle.setText(paperTitle);
        llBack.setVisibility(View.VISIBLE);
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/nexams/dbdata/";
        fileName = paperDb + ".db";
        NewExamsSqliteUtils newdb = new NewExamsSqliteUtils(this, filePath, fileName);
        listDetails = newdb.getPaperDetailsByPpsId(ppsId);
        recyclerOption.setLayoutManager(new FullyLinearLayoutManager(this));
        recyclerOption.setNestedScrollingEnabled(false);
        if (listDetails.size() > 0) {
            resolveResult(page);
        }
        llPageLeft.setOnClickListener(this);
        llPageRight.setOnClickListener(this);
        llBack.setOnClickListener(this);
        llRight.setOnClickListener(this);
    }

    /**
     * 解析获取到的结果
     *
     * @param page 页码
     */
    private void resolveResult(final int page) {
        final PaperDetailsModel pdm = listDetails.get(page);
        NewExamsSqliteUtils child = new NewExamsSqliteUtils(this, filePath, fileName);
        List<PaperDetailsModel> listPaper = child.getPaperChildrenByParentId(pdm.getPsjId() + "");
        final List<LitterSelectModel> listOptions = new ArrayList<>();
        if (listPaper.size() > 0) {
            listOptions.clear();
            for (PaperDetailsModel tem : listPaper) {
                LitterSelectModel lsm = new LitterSelectModel();
                List<DetailsTestModel> listChoice = GsonTools.getPersons(tem.getPsjOption(), DetailsTestModel.class);
                lsm.setListOption(listChoice);
                lsm.setPdm(tem);
                listOptions.add(lsm);
            }
            final AllOptionAdapter allOptionAdapter = new AllOptionAdapter(this, listOptions);
            DividerItemDecoration dividerVERTICAL = new DividerItemDecoration(DividerItemDecoration.VERTICAL);
            dividerVERTICAL.setSize(1);
            dividerVERTICAL.setColor(Color.BLACK);
            recyclerOption.addItemDecoration(dividerVERTICAL);
            recyclerOption.setVisibility(View.VISIBLE);
            recyclerOption.setAdapter(allOptionAdapter);
            paintInvalidateRectView.setVisibility(View.GONE);
            allOptionAdapter.setOnClickListener(new WarpLayoutOnClickListener() {
                @Override
                public void onClickListener(View view, int parentPosition, int position, String[] results) {
                    aCache.put(listDetails.get(page).getPsjId() + "psj_id_" + parentPosition, position + "");
                    aCache.put(listDetails.get(page).getPsjId() + "_results", results);
                    allOptionAdapter.setData(listOptions);
                }
            });
        } else {
            List<DetailsTestModel> listChoice = GsonTools.getPersons(pdm.getPsjOption(), DetailsTestModel.class);
            ChoiceOptionAdapter adapter = new ChoiceOptionAdapter(this, pdm.getPsjId(), listChoice, ppsId);
            if (listChoice.size() > 0) {
                recyclerOption.setAdapter(adapter);
                recyclerOption.setVisibility(View.VISIBLE);
                paintInvalidateRectView.setVisibility(View.GONE);
            } else {
                recyclerOption.setVisibility(View.GONE);
                paintInvalidateRectView.setVisibility(View.VISIBLE);
            }
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    aCache.put(ppsId + "psj_id_" + pdm.getPsjId(), position + "");
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
        }
        setTestTitle((page + 1) + "、" + pdm.getPsjTitle(), tvTitleView);
        tvPageCount.setText((page + 1) + "/" + listDetails.size());
        if (page == listDetails.size() - 1) {
            tvRight.setText("提交");
            llRight.setVisibility(View.VISIBLE);
        } else {
            llRight.setVisibility(View.GONE);
        }
        customScrollView.scrollTo(0, 0);
        customScrollView.setSlideListener(new CustomScrollView.SlideListener() {
            @Override
            public void moveDirection(boolean left, boolean up, boolean right, boolean down) {
                if (left) {
                    Toastor.showToast(ChoiceQuestionActivity.this, "左边");
                } else if (right) {
                    Toastor.showToast(ChoiceQuestionActivity.this, "右边");
                }
            }

            @Override
            public void toBootom() {

            }

            @Override
            public void toTop() {

            }
        });
        customScrollView.setScrollHigh(0);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            int temp = customScrollView.getChildAt(0).getHeight();
            if (temp < 1000) {
                temp = 1000;
            }
            paintInvalidateRectView.getLayoutParams().height = temp;
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

    /**
     * @param title
     * @param view
     */
    private void setTestTitle(String title, TextView view) {
        Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"](\\s*)(data:image/)\\S+(base64,)([^'\"]+)['\"][^>]*>");
        Matcher m = p.matcher(title);
        while (m.find()) {
            String str = m.group(4);
            title = title.replace(m.group(), "#@M#@X@" + str + "#@M#@X@");
        }
        String titleResult = title;
        if (titleResult.indexOf("#@M#@X@") > 0) {
            String[] s = titleResult.split("#@M#@X@");
            view.setText("");
            for (int j = 0; j < s.length; j++) {
                if (j % 2 == 0) {
                    view.append(Html.fromHtml(s[j]));
                } else {
                    Bitmap bitmap = Base64Utils.base64ToBitmap(s[j]);
                    ImageSpan imgSpan = new ImageSpan(this, bitmap);
                    SpannableString spanString = new SpannableString("icon");
                    spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    view.append(spanString);
                }
            }
        } else {
            view.setText(Html.fromHtml(title, null, new MxgsaTagHandler(this)));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                this.finish();
                break;
            case R.id.ll_page_left:
                if (page > 0) {
                    page--;
                    resolveResult(page);
                }
                break;
            case R.id.ll_page_right:
                if (page < listDetails.size() - 1) {
                    page++;
                    resolveResult(page);
                }
                break;
            case R.id.ll_base_right:
                Toastor.showToast(this, "提交");
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_UP:
                if (page > 0) {
                    page--;
                    resolveResult(page);
                }
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN:
                if (page < listDetails.size() - 1) {
                    page++;
                    resolveResult(page);
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                this.finish();
                return true;
        }
        return true;
    }
}
