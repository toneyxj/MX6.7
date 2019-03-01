package com.moxi.haierexams.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.haierexams.R;
import com.moxi.haierexams.cache.ACache;
import com.moxi.haierexams.db.TestSqlUtil;
import com.moxi.haierexams.model.ExamsDetails;
import com.moxi.haierexams.utils.LocationPhotoInstance;
import com.moxi.haierexams.utils.MxgsaTagHandler;
import com.moxi.haierexams.view.PaintInvalidateRectView;
import com.moxi.haierexams.view.SlideLinerlayout;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.Base64Utils;
import com.mx.mxbase.utils.DensityUtil;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import okhttp3.Call;

/**
 * 试卷展示界面
 * Created by Archer on 16/10/13.
 */
public class ExamsTestActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_exams_title_view)
    TextView tvExamsTitle;
    @Bind(R.id.tv_exams_analysis_view)
    TextView tvAnalysis;
    @Bind(R.id.tv_chose_exams_title_view)
    TextView tvChoseTitle;
    @Bind(R.id.tv_chose_exams_analysis_view)
    TextView tvChoseAnalysis;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_back)
    TextView tvBack;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.slide_liner_layout)
    SlideLinerlayout slideLinerLayout;
    @Bind(R.id.slide_chose_liner_layout)
    SlideLinerlayout slideChoseLayout;
    @Bind(R.id.pirv_home_work_achace)
    PaintInvalidateRectView paintInvalidateRectView;
    @Bind(R.id.img_home_work_left)
    TextView imgPageLeft;
    @Bind(R.id.img_home_work_right)
    TextView imgPageRight;
    @Bind(R.id.tv_home_work_page_count)
    TextView tvPage;
    @Bind(R.id.radio_group_write_home)
    RadioGroup radioGroup;
    @Bind(R.id.radio_answer_1)
    RadioButton radioBtn1;
    @Bind(R.id.radio_answer_2)
    RadioButton radioBtn2;
    @Bind(R.id.radio_answer_3)
    RadioButton radioBtn3;
    @Bind(R.id.radio_answer_4)
    RadioButton radioBtn4;
    @Bind(R.id.ll_base_right)
    LinearLayout llRight;
    @Bind(R.id.tv_base_right)
    TextView tvRight;

    private int page = 0;
    private List<ExamsDetails> examsDetails = new ArrayList<>();
    private String midTitle = "";
    private String papId = "";

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_write_home_work;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * 初始化视图
     */
    private void init() {
        llBack.setVisibility(View.VISIBLE);
        tvBack.setText("返回");
        llRight.setVisibility(View.VISIBLE);

        imgPageLeft.setOnClickListener(this);
        imgPageRight.setOnClickListener(this);
        llBack.setOnClickListener(this);
        radioBtn1.setOnClickListener(this);
        radioBtn2.setOnClickListener(this);
        radioBtn3.setOnClickListener(this);
        radioBtn4.setOnClickListener(this);
        llRight.setOnClickListener(this);

        slideChoseLayout.setVisibility(View.VISIBLE);
        slideLinerLayout.setVisibility(View.GONE);
        papId = this.getIntent().getStringExtra("test_pap_id");
        midTitle = this.getIntent().getStringExtra("test_pap_name");
        tvMidTitle.setText(midTitle);
        examsDetails = TestSqlUtil.getInstance(this).getExamsDetails(papId);
        if (MXErrorExamsActivity.page != -1) {
            page = MXErrorExamsActivity.page;
            MXErrorExamsActivity.page = -1;
        }
        parseExamsDetails(page);
    }

    /**
     * 解析试卷详情
     *
     * @param page 题目索引
     */
    private void parseExamsDetails(int page) {
        String isDone = this.getIntent().getStringExtra("test_pap_cprId");
        int testType = examsDetails.get(page).getType();
        String testTitle = (page + 1) + "、" + examsDetails.get(page).getTitle();
        String testAnalysis = examsDetails.get(page).getAnalysis();
        String testResult = examsDetails.get(page).getAnswer();
        if (testType == 6 || testType == 18) {
            slideChoseLayout.setVisibility(View.VISIBLE);
            slideLinerLayout.setVisibility(View.GONE);
            setTitle(testTitle, tvChoseTitle);
            setTitle("正确答案为：" + testResult + "\t\t解析：" + testAnalysis, tvChoseAnalysis);
            resetRadioGroup(page, ACache.get(this).getAsString("papId" + papId + page));
        } else {
            slideChoseLayout.setVisibility(View.GONE);
            slideLinerLayout.setVisibility(View.VISIBLE);
            setTitle(testTitle, tvExamsTitle);
            setTitle("正确答案为：" + testResult + "\t\t解析：" + testAnalysis, tvAnalysis);
            resetRadioGroup(page, ACache.get(this).getAsString("papId" + papId + page));
            int height = measureView(tvExamsTitle) + measureView(tvAnalysis);
            RelativeLayout.LayoutParams da = (RelativeLayout.LayoutParams) paintInvalidateRectView.getLayoutParams();
            if (height < DensityUtil.getScreenH(this)) {
                height = DensityUtil.getScreenH(this);
            }
            da.height = height;
            paintInvalidateRectView.setLayoutParams(da);
        }
        String bitUrl = cacheImgPath + saveSync + "/" + papId + "/" + examsDetails.get(page).getId() + ".png";
        LocationPhotoInstance.getInstance().loadImage(bitUrl.replace(" ", ""), new LocationPhotoInstance.LoadPhotoListener() {
            @Override
            public void onLoadSucess(Bitmap bitmap, String path) {
                paintInvalidateRectView.initBitmap(bitmap, path);
            }
        });
        slideLinerLayout.moveToTop();
        slideChoseLayout.moveToTop();
        if (isDone.equals("-999")) {
            tvRight.setText("提交");
            tvChoseAnalysis.setVisibility(View.GONE);
            tvAnalysis.setVisibility(View.GONE);
        } else {
            tvRight.setText("查看结果");
            tvChoseAnalysis.setVisibility(View.VISIBLE);
            tvAnalysis.setVisibility(View.VISIBLE);
            disableRadioGroup(radioGroup);
        }
        if (examsDetails.size() == 0) {
            tvPage.setText((page) + "/" + examsDetails.size());
        } else {
            tvPage.setText((page + 1) + "/" + examsDetails.size());
        }
    }

    /**
     * 重新设置radioGroup
     *
     * @param page   题号
     * @param answer 数据对象
     */
    private void resetRadioGroup(int page, String answer) {
        if (answer == null) {
            radioGroup.clearCheck();
        } else {
            switch (answer) {
                case "A":
                    radioBtn1.setChecked(true);
                    break;
                case "B":
                    radioBtn2.setChecked(true);
                    break;
                case "C":
                    radioBtn3.setChecked(true);
                    break;
                case "D":
                    radioBtn4.setChecked(true);
                    break;
            }
        }
    }

    /**
     * 让radiogroup失去点击效果
     *
     * @param testRadioGroup 目标radiogroup
     */
    public void disableRadioGroup(RadioGroup testRadioGroup) {
        for (int i = 0; i < testRadioGroup.getChildCount(); i++) {
            testRadioGroup.getChildAt(i).setEnabled(false);
            testRadioGroup.getChildAt(i).setClickable(false);
        }
    }

    /**
     * @param title
     * @param view
     */
    private void setTitle(String title, TextView view) {
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
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (MXErrorExamsActivity.page != -1) {
            page = MXErrorExamsActivity.page;
            Intent intent = getIntent();
            this.finish();
            startActivity(intent);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    private String cacheImgPath = FileUtils.getInstance().getDataFilePath();
    private String saveSync = "savePap";

    @Override
    public void onActivityStopped(Activity activity) {
        dialogShowOrHide(false, "");
        if (examsDetails.get(page).getType() != 6 && examsDetails.get(page).getType() != 18) {
            Bitmap bitmap = paintInvalidateRectView.getBitmap();
            if (bitmap != null) {
                saveBitMap(cacheImgPath + saveSync + "/" + papId + "/" + examsDetails.get(page).getId() + ".png", bitmap);
            }
        }
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
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_UP:
                try {
                    if (examsDetails.get(page).getType() != 6 && examsDetails.get(page).getType() != 18) {
                        Bitmap bitmap = paintInvalidateRectView.getBitmap();
                        if (bitmap != null) {
                            saveBitMap(cacheImgPath + saveSync + "/" + papId + "/" + examsDetails.get(page).getId() + ".png", bitmap);
                        }
                    }
                    if (page > 0) {
                        page--;
                        parseExamsDetails(page);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN:
                try {
                    if (examsDetails.get(page).getType() != 6 && examsDetails.get(page).getType() != 18) {
                        Bitmap bitmap = paintInvalidateRectView.getBitmap();
                        if (bitmap != null) {
                            saveBitMap(cacheImgPath + saveSync + "/" + papId + "/" + examsDetails.get(page).getId() + ".png", bitmap);
                        }
                    }
                    if (page < examsDetails.size() - 1) {
                        page++;
                        parseExamsDetails(page);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                this.finish();
                return true;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                this.finish();
                break;
            case R.id.img_home_work_left:
                try {
                    if (examsDetails.get(page).getType() != 6 && examsDetails.get(page).getType() != 18) {
                        Bitmap bitmap = paintInvalidateRectView.getBitmap();
                        if (bitmap != null) {
                            saveBitMap(cacheImgPath + saveSync + "/" + papId + "/" + examsDetails.get(page).getId() + ".png", bitmap);
                        }
                    }
                    if (page > 0) {
                        page--;
                        parseExamsDetails(page);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.img_home_work_right:
                try {
                    if (examsDetails.get(page).getType() != 6 && examsDetails.get(page).getType() != 18) {
                        Bitmap bitmap = paintInvalidateRectView.getBitmap();
                        if (bitmap != null) {
                            saveBitMap(cacheImgPath + saveSync + "/" + papId + "/" + examsDetails.get(page).getId() + ".png", bitmap);
                        }
                    }
                    if (page < examsDetails.size() - 1) {
                        page++;
                        parseExamsDetails(page);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            //提交
            case R.id.ll_base_right:
                if (tvRight.getText().toString().equals("提交")) {
                    new AlertDialog(this).builder().setTitle("提示").setMsg("确认提交?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            OkHttpUtils.post().url(Constant.SUBMIT_EXAMS_RESULT).addParams("appSession", MXUamManager.queryUser(ExamsTestActivity.this)).
                                    addParams("copId", papId).build().connTimeOut(10000).execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {

                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(response);
                                        int code = jsonObject.optInt("code", -1);
                                        if (code == 0) {
                                            Intent intent = new Intent();
                                            intent.setClass(ExamsTestActivity.this, MXErrorExamsActivity.class);
                                            intent.putExtra("exams_pap_id", papId);
                                            intent.putExtra("cob_exams_title", midTitle);
                                            startActivity(intent);
                                            ExamsTestActivity.this.finish();
                                            Toastor.showToast(ExamsTestActivity.this, "提交成功");
                                        } else {
                                            Toastor.showToast(ExamsTestActivity.this, "提交失败");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }).setPositiveButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(this, MXErrorExamsActivity.class);
                    intent.putExtra("exams_pap_id", papId);
                    intent.putExtra("cob_exams_title", midTitle);
                    startActivity(intent);
                }
                break;
            case R.id.radio_answer_1:
                updateAnswer(page, "A");
                break;
            case R.id.radio_answer_2:
                updateAnswer(page, "B");
                break;
            case R.id.radio_answer_3:
                updateAnswer(page, "C");
                break;
            case R.id.radio_answer_4:
                updateAnswer(page, "D");
                break;
            default:
                break;
        }
    }

    /**
     * 更新缓存结果
     *
     * @param page
     * @param d
     */
    private void updateAnswer(int page, String d) {
        ACache.get(this).put("papId" + papId + page, d);
    }

    /**
     * 保存缓存图片方便上传图片的时候读取
     */
    private void saveBitMap(String filePath, Bitmap bitmap) {
        LocationPhotoInstance.getInstance().addPhoto(filePath.replace(" ", ""), bitmap);
    }

    /**
     * 计算view的高度
     *
     * @param child
     * @return
     */
    private int measureView(View child) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childMeasureWidth = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        int childMeasureHeight;
        if (lp.height > 0) {
            childMeasureHeight = View.MeasureSpec.makeMeasureSpec(lp.height, View.MeasureSpec.EXACTLY);
        } else {
            childMeasureHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);//未指定
        }
        child.measure(childMeasureWidth, childMeasureHeight);
        int aaa = child.getMeasuredHeight();
        return aaa;
    }
}
