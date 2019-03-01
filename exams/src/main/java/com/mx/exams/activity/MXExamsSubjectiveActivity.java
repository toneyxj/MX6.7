package com.mx.exams.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mx.exams.R;
import com.mx.exams.cache.ACache;
import com.mx.exams.view.PaintInvalidateRectView;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.http.MXHttpHelper;
import com.mx.mxbase.model.BaseModel;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.Bind;

/**
 * 主观题显示UI视图(试卷的)
 * Created by Archer on 16/9/20.
 */
public class MXExamsSubjectiveActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_back)
    TextView tvBaseBack;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.img_home_work_left)
    ImageView imgLeft;
    @Bind(R.id.tv_base_right)
    TextView tvBaseRight;
    @Bind(R.id.ll_base_right)
    LinearLayout llBaseRight;
    @Bind(R.id.img_home_work_right)
    ImageView imgRight;
    @Bind(R.id.rl_shot_screen)
    RelativeLayout rlShotScreen;
    @Bind(R.id.img_home_work_done)
    ImageView imgDone;
    @Bind(R.id.pirv_home_work_achace)
    PaintInvalidateRectView paintInvalidateRectView;
    @Bind(R.id.selet_paint)
    RadioGroup selet_paint;
    @Bind(R.id.rubber_draft)
    ImageView rubber_draft;
    @Bind(R.id.ll_submit_view)
    LinearLayout llSubmitView;
    @Bind(R.id.draft_show)
    TextView tvSubmit;
    @Bind(R.id.img_zhan_wei)
    ImageView imgZw;
    @Bind(R.id.tv_home_work_page_count)
    TextView tvPageCount;

    private int page = 0;
    private int count = 0;
    private String fileUrl = "";
    private String fileId = "";

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.arg1 == 103) {
            if (msg.what == Integer.parseInt(Constant.SUCCESS)) {
                Toastor.showToast(this, "提交成功");
            } else {
                Toastor.showToast(this, "提交失败，请重试！");
            }
        }
    }

    @Override
    protected int getMainContentViewId() {
        //获取课后作业数据
        return R.layout.mx_activity_subjective;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {

        String examsTitle = this.getIntent().getStringExtra("exams_title");
        fileUrl = this.getIntent().getStringExtra("file_url");
        fileId = examsTitle + this.getIntent().getStringExtra("file_id");

        //初始化view
        llBack.setVisibility(View.VISIBLE);
        tvBaseBack.setText("好题天天练");
        tvMidTitle.setText(examsTitle);
        rlShotScreen.setDrawingCacheEnabled(true);

        //设置点击事件监听
        llBack.setOnClickListener(this);
        imgLeft.setOnClickListener(this);
        imgRight.setOnClickListener(this);
        llBaseRight.setOnClickListener(this);
        selet_paint.setOnCheckedChangeListener(this);
        rubber_draft.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);

        parseHomeWork(fileUrl, page);
    }

    /**
     * 解析数据
     *
     * @param fileUrl
     */
    private void parseHomeWork(String fileUrl, int page) {
        File file = new File(fileUrl);
        if (file.exists() && file.isDirectory()) {
            count = file.listFiles().length;
            File[] subFile = file.listFiles();
            Arrays.sort(subFile);
            Bitmap bitmap = BitmapFactory.decodeFile(subFile[page].getAbsolutePath());
            imgDone.setVisibility(View.VISIBLE);
            imgDone.setImageBitmap(bitmap);
            tvPageCount.setText((page + 1) + "/" + count);
            //设置bitmap给paintInvalidate
            String aaaa = fileId + "_" + page;
            if (ACache.get(this).getAsBitmap(aaaa) != null) {
                paintInvalidateRectView.initBitmap(ACache.get(this).getAsBitmap(aaaa),"");
            } else {
                paintInvalidateRectView.initBitmap(null,"");
            }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.setResult(-1);
            this.finish();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            //上一页
            saveBitMap();
            if (page > 0) {
                page--;
                parseHomeWork(fileUrl, page);
            }
            return true;
        } else if ( keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            //下一页
            saveBitMap();
            if (page < count - 1) {
                page++;
                parseHomeWork(fileUrl, page);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                this.setResult(-1);
                this.finish();
                break;
            case R.id.img_home_work_left:
                saveBitMap();
                if (page > 0) {
                    page--;
                    parseHomeWork(fileUrl, page);
                }
                break;
            case R.id.img_home_work_right:
                saveBitMap();
                if (page < count - 1) {
                    page++;
                    parseHomeWork(fileUrl, page);
                }
                break;
            case R.id.ll_base_right:
                break;
            case R.id.draft_show:
                new AlertDialog(MXExamsSubjectiveActivity.this).builder().setTitle("提示").setMsg("提交后将无法修改，确认提交?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        submitHomeWork();
                    }
                }).setPositiveButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
                break;
            case R.id.rubber_draft:
                paintInvalidateRectView.setPaint(4);
                break;
            default:
                break;
        }
    }

    /**
     * 保存缓存图片方便上传图片的时候读取
     */
    private void saveBitMap() {
        String strPath = "/mxAcache/" + fileId + page + ".png";
//        Bitmap bitmap1 = rlShotScreen.getDrawingCache();
        Bitmap bitmap1 = loadBitmapFromView(rlShotScreen);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardDir = Environment.getExternalStorageDirectory();
            FileOutputStream fos = null;
            try {
                File fileBitmap = new File(sdCardDir, strPath);
                if (!fileBitmap.getParentFile().exists()) {
                    fileBitmap.getParentFile().mkdirs();
                }
                fos = new FileOutputStream(fileBitmap);
                //当指定压缩格式为PNG时保存下来的图片显示正常
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String daaa = fileId + "_" + page;
        ACache.get(this).put(daaa, bitmap1);
        rlShotScreen.setDrawingCacheEnabled(true);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if (checkedId == R.id.bottom_1) {// 笔触最小
            paintInvalidateRectView.setPaint(0);
        } else if (checkedId == R.id.bottom_2) {//笔触中等
            paintInvalidateRectView.setPaint(1);
        } else if (checkedId == R.id.bottom_3) {// 笔触最大
            paintInvalidateRectView.setPaint(2);
        }
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
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(screenshot);
        c.translate(-v.getScrollX(), -v.getScrollY());
        v.draw(c);
        return screenshot;
    }

    //提交课后作业
    private void submitHomeWork() {
        saveBitMap();
        HashMap<String, String> submit = new HashMap<>();
        submit.put("copId", this.getIntent().getStringExtra("file_id"));
        submit.put("appSession", MXUamManager.queryUser(this));
        MXHttpHelper.getInstance(this).postStringBack(103, Constant.SUBMIT_EXAMS_RESULT, submit, getHandler(), BaseModel.class);
    }
}
