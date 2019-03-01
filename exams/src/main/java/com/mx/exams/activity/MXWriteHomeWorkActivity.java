package com.mx.exams.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mx.exams.R;
import com.mx.exams.cache.ACache;
import com.mx.exams.db.SQLBookUtil;
import com.mx.exams.db.SQLUtil;
import com.mx.exams.model.ExamsDetailsModel;
import com.mx.exams.model.OptionModel;
import com.mx.exams.model.SyncExamsModel;
import com.mx.exams.utils.LocationPhotoInstance;
import com.mx.exams.utils.MxgsaTagHandler;
import com.mx.exams.view.PaintInvalidateRectView;
import com.mx.exams.view.SlideLinerlayout;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.http.MXHttpHelper;
import com.mx.mxbase.model.BaseModel;
import com.mx.mxbase.utils.Base64Utils;
import com.mx.mxbase.utils.DensityUtil;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by Archer on 16/10/13.
 */
public class MXWriteHomeWorkActivity extends BaseActivity implements View.OnClickListener {

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

    private ExamsDetailsModel edm;
    private int page = 0;
    private SyncExamsModel sem;
    private String bookId;
    private OptionModel optionModel;
    private String response;//同步练习json数据
    private String historyJson = "";

    private String cacheImgPath = FileUtils.getInstance().getDataFilePath();
    private String saveSync = "saveSync";

    private String answer_key = "";
    private String midStr = "";
    private int cchId;
    private boolean NEEDREFUSE = false;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        dialogShowOrHide(false, "");
        if (msg.arg1 == 102) {
            if (msg.what == Integer.parseInt(Constant.SUCCESS)) {
                Toastor.showToast(this, "提交成功");
                Intent intent = new Intent(this, MXErrorExamsActivity.class);
                intent.putExtra("cob_exams_title", midStr);
                intent.putExtra("cob_zj_id", cchId);
                startActivity(intent);
            } else {
                Toastor.showToast(this, "提交失败，请检查网络！");
            }
        }
    }

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
        //获取上个界面传递过来的数据
        sem = (SyncExamsModel) this.getIntent().getSerializableExtra("sem_info");
        optionModel = (OptionModel) this.getIntent().getSerializableExtra("option_info");
        bookId = this.getIntent().getStringExtra("book_id");
        if (sem == null || optionModel == null) {
            midStr = this.getIntent().getStringExtra("mid_title");
            cchId = this.getIntent().getIntExtra("cch_id", -1);
        } else {
            cchId = optionModel.getId();
            midStr = sem.getCob_pub_name() + sem.getCob_sub_name() + sem.getCob_sec_name() + optionModel.getOptionName();
        }
        tvMidTitle.setText(midStr);
        answer_key = midStr + cchId;

        llRight.setVisibility(View.VISIBLE);
        llBack.setVisibility(View.VISIBLE);
        tvBack.setText("返回");

        llBack.setOnClickListener(this);
        llRight.setOnClickListener(this);
        imgPageLeft.setOnClickListener(this);
        imgPageRight.setOnClickListener(this);
        radioBtn1.setOnClickListener(this);
        radioBtn2.setOnClickListener(this);
        radioBtn3.setOnClickListener(this);
        radioBtn4.setOnClickListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bookId == null) {
                            bookId = SQLUtil.getInstance(MXWriteHomeWorkActivity.this).getBookIdByCchId(cchId + "");
                        }
                        response = SQLBookUtil.getInstance(getApplicationContext()).getExamsDetails(bookId, cchId + "");
                        if (response.length() > 50) {
                            imgPageLeft.setVisibility(View.VISIBLE);
                            imgPageRight.setVisibility(View.VISIBLE);
                            tvPage.setVisibility(View.VISIBLE);
                            llRight.setVisibility(View.VISIBLE);
                            getHistory(MXUamManager.queryUser(MXWriteHomeWorkActivity.this));
                        } else {
                            imgPageLeft.setVisibility(View.GONE);
                            imgPageRight.setVisibility(View.GONE);
                            tvPage.setVisibility(View.GONE);
                            llRight.setVisibility(View.GONE);
                            Toastor.showToast(MXWriteHomeWorkActivity.this, "此章节题目为空，请重新选择");
                        }
                    }
                });
            }
        }).start();
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
                String temp = ACache.get(MXWriteHomeWorkActivity.this).getAsString(Constant.HISTORYURL);
                if (temp != null) {
                    historyJson = temp;
                    imgPageLeft.setVisibility(View.VISIBLE);
                    imgPageRight.setVisibility(View.VISIBLE);
                    tvPage.setVisibility(View.VISIBLE);
                    llRight.setVisibility(View.VISIBLE);
                    parseExamsDetails(page, temp);
                } else {
                    historyJson = "";
                    parseExamsDetails(page);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                dialogShowOrHide(false, "数据加载中...");
                //保存接口数据1分钟
                ACache.get(MXWriteHomeWorkActivity.this).put(Constant.HISTORYURL, response);
                historyJson = response;
                parseExamsDetails(page, response);
            }
        });
    }

    /**
     * 解析历史纪录并判断当前同步练习是否提交过
     *
     * @param response 历史记录model
     * @param cchid    章节id
     * @return true 为提交过 false为未做过
     */
    private boolean parseHistoryData(String response, int cchid) {
        boolean temp = false;
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.optInt("code", -1);
            if (code == 0) {
                JSONObject jsonObject1 = jsonObject.optJSONObject("result");
                JSONArray jsonArray = jsonObject1.optJSONArray("list");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject liObj = jsonArray.getJSONObject(i);
                    if (liObj.optInt("cchId") == cchid) {
                        //TODO
                        String coeIds = liObj.optString("coeIds");
                        Log.e("coeIds", "coeIds" + coeIds);
                        temp = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * 解析同步练习试题
     *
     * @param page         当前页码
     * @param examsDetails 试题json数据
     */
    private void parseExamsDetails(int page, String examsDetails) {
        edm = GsonTools.getPerson(response, ExamsDetailsModel.class);
        if (parseHistoryData(examsDetails, cchId)) {
            //提交过的
            tvRight.setText("查看结果");
            disableRadioGroup(radioGroup, false);
            tvChoseAnalysis.setVisibility(View.VISIBLE);
            tvAnalysis.setVisibility(View.VISIBLE);
        } else {
            //未提交过的
            tvRight.setText("提交");
            disableRadioGroup(radioGroup, true);
            tvChoseAnalysis.setVisibility(View.GONE);
            tvAnalysis.setVisibility(View.GONE);
        }
        if (edm.getResult().get(page).getType() == 6 || edm.getResult().get(page).getType() == 18) {
            //TODO 选择题
            slideChoseLayout.setVisibility(View.VISIBLE);
            slideLinerLayout.setVisibility(View.GONE);
            setTitle((page + 1) + "、" + edm.getResult().get(page).getTitle(), tvChoseTitle);
            setTitle("正确答案为：" + edm.getResult().get(page).getAnswer() + "\t\t解析：" + edm.getResult().get(page).getAnalysis(), tvChoseAnalysis);
            resetRadioGroup(page);
        } else {
            slideChoseLayout.setVisibility(View.GONE);
            slideLinerLayout.setVisibility(View.VISIBLE);
            setTitle((page + 1) + "、" + edm.getResult().get(page).getTitle(), tvExamsTitle);
            setTitle("\n\n解析：" + edm.getResult().get(page).getAnalysis(), tvAnalysis);
            int height = measureView(tvExamsTitle) + measureView(tvAnalysis);
            RelativeLayout.LayoutParams da = (RelativeLayout.LayoutParams) paintInvalidateRectView.getLayoutParams();
            if (height < DensityUtil.getScreenH(this)) {
                height = DensityUtil.getScreenH(this);
            }
            da.height = height;
            paintInvalidateRectView.setLayoutParams(da);
        }
        String bitUrl = cacheImgPath + saveSync + "/" + cchId + "/" + edm.getResult().get(page).getId() + ".png";
        LocationPhotoInstance.getInstance().loadImage(bitUrl.replace(" ", ""), new LocationPhotoInstance.LoadPhotoListener() {
            @Override
            public void onLoadSucess(Bitmap bitmap, String path) {
                paintInvalidateRectView.initBitmap(bitmap, path);
            }
        });

        slideLinerLayout.moveToTop();
        slideChoseLayout.moveToTop();
        tvPage.setText((page + 1) + "/" + edm.getResult().size());//
    }

    /**
     * 解析同步练习试题
     *
     * @param page 当前页码
     */
    private void parseExamsDetails(int page) {
        edm = GsonTools.getPerson(response, ExamsDetailsModel.class);
        //未提交过的
        tvRight.setText("提交");
        disableRadioGroup(radioGroup, true);
        tvChoseAnalysis.setVisibility(View.GONE);
        tvAnalysis.setVisibility(View.GONE);
        if (edm.getResult().get(page).getType() == 6 || edm.getResult().get(page).getType() == 18) {
            //TODO 选择题
            slideChoseLayout.setVisibility(View.VISIBLE);
            slideLinerLayout.setVisibility(View.GONE);
            setTitle((page + 1) + "、" + edm.getResult().get(page).getTitle(), tvChoseTitle);
            setTitle("正确答案为：" + edm.getResult().get(page).getAnswer() + "\t\t解析：" + edm.getResult().get(page).getAnalysis(), tvChoseAnalysis);
            resetRadioGroup(page);
        } else {
            slideChoseLayout.setVisibility(View.GONE);
            slideLinerLayout.setVisibility(View.VISIBLE);
            setTitle((page + 1) + "、" + edm.getResult().get(page).getTitle(), tvExamsTitle);
            setTitle("\n\n解析：" + edm.getResult().get(page).getAnalysis(), tvAnalysis);
            int height = measureView(tvExamsTitle) + measureView(tvAnalysis);
            RelativeLayout.LayoutParams da = (RelativeLayout.LayoutParams) paintInvalidateRectView.getLayoutParams();
            if (height < DensityUtil.getScreenH(this)) {
                height = DensityUtil.getScreenH(this);
            }
            da.height = height;
            paintInvalidateRectView.setLayoutParams(da);
        }
        String bitUrl = cacheImgPath + saveSync + "/" + cchId + "/" + edm.getResult().get(page).getId() + ".png";
        LocationPhotoInstance.getInstance().loadImage(bitUrl.replace(" ", ""), new LocationPhotoInstance.LoadPhotoListener() {
            @Override
            public void onLoadSucess(Bitmap bitmap, String path) {
                paintInvalidateRectView.initBitmap(bitmap, path);
            }
        });

        slideLinerLayout.moveToTop();
        slideChoseLayout.moveToTop();
        tvPage.setText((page + 1) + "/" + edm.getResult().size());//edm.getResult().size()
    }

    /**
     * 重新设置radioGroup
     *
     * @param page 题号
     */
    private void resetRadioGroup(int page) {
        String aa = ACache.get(this).getAsString("cchId" + cchId + page);
        if (TextUtils.isEmpty(aa)) {
            radioGroup.clearCheck();
        } else {
            switch (aa) {
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
                default:
                    break;
            }
        }
    }

    /**
     * 让radiogroup失去点击效果
     *
     * @param testRadioGroup 目标radiogroup
     * @param able           是否允许点击
     */
    public void disableRadioGroup(RadioGroup testRadioGroup, boolean able) {
        for (int i = 0; i < testRadioGroup.getChildCount(); i++) {
            testRadioGroup.getChildAt(i).setEnabled(able);
            testRadioGroup.getChildAt(i).setClickable(able);
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
            MXErrorExamsActivity.page = -1;
        }
        if (NEEDREFUSE) {
            if (response != null) {
                getHistory(MXUamManager.queryUser(MXWriteHomeWorkActivity.this));
            }
            NEEDREFUSE = false;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        try {
            if (edm.getResult().get(page).getType() != 6 && edm.getResult().get(page).getType() != 18) {
                Bitmap bitmap = paintInvalidateRectView.getBitmap();
                if (bitmap != null) {
                    saveBitMap(cacheImgPath + saveSync + "/" + cchId + "/" + edm.getResult().get(page).getId() + ".png", bitmap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        try {
            paintInvalidateRectView.initBitmap(null, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        NEEDREFUSE = true;
        dialogShowOrHide(false, "");
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        NEEDREFUSE = false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_UP:
                try {
                    if (edm.getResult().get(page).getType() != 6 && edm.getResult().get(page).getType() != 18) {
                        Bitmap bitmap = paintInvalidateRectView.getBitmap();
                        if (bitmap != null) {
                            saveBitMap(cacheImgPath + saveSync + "/" + cchId + "/" + edm.getResult().get(page).getId() + ".png", bitmap);
                        }
                    }
                    if (page > 0) {
                        page--;
                        if (historyJson.equals("")) {
                            parseExamsDetails(page);
                        } else {
                            parseExamsDetails(page, historyJson);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN:
                try {
                    if (edm.getResult().get(page).getType() != 6 && edm.getResult().get(page).getType() != 18) {
                        Bitmap bitmap = paintInvalidateRectView.getBitmap();
                        if (bitmap != null) {
                            saveBitMap(cacheImgPath + saveSync + "/" + cchId + "/" + edm.getResult().get(page).getId() + ".png", bitmap);
                        }
                    }
                    if (page < edm.getResult().size() - 1) {
                        page++;
                        if (historyJson.equals("")) {
                            parseExamsDetails(page);
                        } else {
                            parseExamsDetails(page, historyJson);
                        }
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
                    if (edm.getResult().get(page).getType() != 6 && edm.getResult().get(page).getType() != 18) {
                        Bitmap bitmap = paintInvalidateRectView.getBitmap();
                        if (bitmap != null) {
                            saveBitMap(cacheImgPath + saveSync + "/" + cchId + "/" + edm.getResult().get(page).getId() + ".png", bitmap);
                        }
                    }
                    if (page > 0) {
                        page--;
                        if (historyJson.equals("")) {
                            parseExamsDetails(page);
                        } else {
                            parseExamsDetails(page, historyJson);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.img_home_work_right:
                try {
                    if (edm.getResult().get(page).getType() != 6 && edm.getResult().get(page).getType() != 18) {
                        Bitmap bitmap = paintInvalidateRectView.getBitmap();
                        if (bitmap != null) {
                            saveBitMap(cacheImgPath + saveSync + "/" + cchId + "/" + edm.getResult().get(page).getId() + ".png", bitmap);
                        }
                    }
                    if (page < edm.getResult().size() - 1) {
                        page++;
                        if (historyJson.equals("")) {
                            parseExamsDetails(page);
                        } else {
                            parseExamsDetails(page, historyJson);
                        }
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
                            submitTongbu();
                        }
                    }).setPositiveButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(this, MXErrorExamsActivity.class);
                    intent.putExtra("cob_zj_id", cchId);
                    intent.putExtra("cob_exams_title", midStr);
                    startActivity(intent);
                    this.finish();
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
     * 提交同步练习
     */
    private void submitTongbu() {
        HashMap<String, String> sub = new HashMap<>();
        sub.put("semId", sem.getCos_sem_id());
        sub.put("cchId", cchId + "");
        String cobID = SQLUtil.getInstance(this).getBookIdByCchId(cchId + "");
        sub.put("cobId", cobID);
        sub.put("coeIds", "");
        sub.put("appSession", MXUamManager.queryUser(this));
        MXHttpHelper.getInstance(this).postStringBack(102, Constant.SUBMIT_TB_RESULT, sub, getHandler(), BaseModel.class);
    }

    /**
     * 更新缓存结果
     *
     * @param page
     * @param d
     */
    private void updateAnswer(int page, String d) {
        ACache.get(this).put("cchId" + cchId + page, d);
    }

    /**
     * 替代getDrawingCache方法
     *
     * @param v
     * @return
     */
    private Bitmap loadBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getMeasuredHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(screenshot);
        c.translate(-v.getScrollX(), -v.getScrollY());
        v.draw(c);
        return screenshot;
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
