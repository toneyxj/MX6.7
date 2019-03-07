package com.mx.exams.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mx.exams.R;
import com.mx.exams.model.ExamsDetails;
import com.mx.exams.model.ExamsDetailsModel;
import com.mx.exams.utils.MxgsaTagHandler;
import com.mx.exams.view.PaintInvalidateRectView;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.http.MXHttpHelper;
import com.mx.mxbase.interfaces.Sucess;
import com.mx.mxbase.model.BaseModel;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.LocationPhotoLoder;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Archer on 16/9/29.
 */
public class MXSubjectExamsActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.img_home_work_right)
    ImageView imgWorkRight;
    @Bind(R.id.img_home_work_left)
    ImageView imgWorkLeft;
    @Bind(R.id.tv_home_work_subjective_title)
    TextView tvHomeWork;
    @Bind(R.id.tv_home_work_page_count)
    TextView tvWorkCount;
    @Bind(R.id.pirv_home_work_achace)
    PaintInvalidateRectView pirv_home_work_achace;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.ll_base_right)
    LinearLayout llRight;
    @Bind(R.id.tv_base_right)
    TextView tvRight;

    private String examsDetails = "";
    private String cob_tx_id;
    private String cob_zj_id, cos_sem_id;
    private int page = 0;
    private List<ExamsDetails> listExams = new ArrayList<>();

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.arg1 == 102) {
            if (msg.what == Integer.parseInt(Constant.SUCCESS)) {
                Toastor.showToast(this, "提交成功");
            } else {
                Toastor.showToast(this, "提交失败，请重试！");
            }
        }
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_subject_exams;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        examsDetails = this.getIntent().getStringExtra("exams_details");
        tvMidTitle.setText(this.getIntent().getStringExtra("exams_details_title"));
        cob_tx_id = this.getIntent().getStringExtra("cob_tx_id");
        cob_zj_id = this.getIntent().getStringExtra("cos_zj_id");
        cos_sem_id = this.getIntent().getStringExtra("cos_sem_id");
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        llRight.setVisibility(View.VISIBLE);
        llRight.setOnClickListener(this);
        tvRight.setText("提交");
        ExamsDetailsModel edm = GsonTools.getPerson(examsDetails, ExamsDetailsModel.class);
        for (ExamsDetails ed : edm.getResult()) {
            if (cob_tx_id.equals("")) {
                listExams = edm.getResult();
            } else {
                if (ed.getType() == Integer.parseInt(cob_tx_id)) {
                    listExams.add(ed);
                }
            }
        }
        if (listExams.size() > 0) {
            parseExamsDetails(page);
        } else {
            Toastor.showToast(this, "请重新选择没有当前类型题目");
        }

        imgWorkRight.setOnClickListener(this);
        imgWorkLeft.setOnClickListener(this);
    }

    private void parseExamsDetails(int page) {
        tvWorkCount.setText((page + 1) + "/" + (listExams.size()));
        tvHomeWork.setText(Html.fromHtml(listExams.get(page).getTitle(), null, new MxgsaTagHandler(this)));
        setBitmap();
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
        try {
            saveImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            //上一页
            if (page > 0) {
                saveImage();
                page--;
                parseExamsDetails(page);
            }
            return true;
        } else if ( keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            //下一页
            if (page < listExams.size() - 1) {
                saveImage();
                page++;
                parseExamsDetails(page);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                this.finish();
                break;
            case R.id.img_home_work_left:
                if (page > 0) {
                    saveImage();
                    page--;
                    parseExamsDetails(page);
                }
                break;
            case R.id.img_home_work_right:
                if (page < listExams.size() - 1) {
                    saveImage();
                    page++;
                    parseExamsDetails(page);
                }
                break;
            case R.id.ll_base_right:
                saveImage();
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
        sub.put("semId", cos_sem_id);
        sub.put("cchId", cob_zj_id);
        sub.put("appSession", MXUamManager.queryUser(this));
        MXHttpHelper.getInstance(this).postStringBack(102, Constant.SUBMIT_TB_RESULT, sub, getHandler(), BaseModel.class);
    }

    /**
     * 保存图片
     */
    public void saveImage() {
        Bitmap bitmap = pirv_home_work_achace.getBitmap();
        try {
            String path = getsavePath(String.valueOf(page));
            saveImageFile(path, bitmap);
            LocationPhotoLoder.getInstance().clearCatch(path);
        } catch (IOException e) {
            showToast("绘制存储失败");
        }
    }

    private void setBitmap() {
        LocationPhotoLoder.getInstance().loadImage(getsavePath(String.valueOf(page)), new Sucess() {
            @Override
            public void setSucess(Bitmap bitmap, boolean isS) {
                pirv_home_work_achace.initBitmap(bitmap,"");
            }
        });
    }

    /**
     * 将图片持久化到本地文件夹下面
     *
     * @param bitmap 需要持久化的图片
     */
    private void saveImageFile(String path, Bitmap bitmap) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        fileOutputStream.close();
    }

    /**
     * 获得图片路径
     *
     * @param page
     * @return
     */
    private String getsavePath(String page) {
        return getFileMidr() + page + ".png";
    }

    /**
     * 图片文件保存根目录
     *
     * @return 返回文件目录
     */
    private String getFileMidr() {
        String zhangjie = String.valueOf(listExams.get(this.page).getCchId());
        String url = "url";
        String gen = FileUtils.getInstance().getDataFilePath() + "/exams/" + StringUtils.stringToMD5(url) + "/" + zhangjie + "/";
        File file = new File(gen);
        if (!file.exists()) {
            file.mkdirs();
        }
        return gen;
    }
}
