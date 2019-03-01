package com.moxi.writeNote.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.handwritinglibs.WriteNoFigerSaveView;
import com.moxi.handwritinglibs.utils.TimerUtils;
import com.moxi.writeNote.R;
import com.moxi.writeNote.WriteBaseActivity;
import com.moxi.writeNote.dialog.CustomPenActivity;
import com.moxi.writeNote.utils.BrushSettingUtils;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.interfaces.Sucess;
import com.mx.mxbase.utils.LocationPhotoLoder;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.SystemUtils;
import com.mx.mxbase.utils.WindowsUtils;
import com.mx.mxbase.view.WriteDrawLayout;
import com.onyx.android.sdk.device.Device;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.Bind;

/**
 * 图片批注处理
 */
public class PicPostilActivity extends WriteBaseActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {


    /**
     * 启动图片描图
     *
     * @param context     上下文
     * @param backImgPath 背景图路径
     * @param title       标题
     */
    public static void startPicPostil(Context context, String backImgPath, String title,boolean titleShow) {
        Intent intent = new Intent(context, PicPostilActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("backImgPath", backImgPath);
        bundle.putString("title", title);
        bundle.putBoolean("titleShow", titleShow);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Bind(R.id.quit_pic_postil)
    TextView quit_pic_postil;
    @Bind(R.id.title_postil)
    TextView title_postil;

    @Bind(R.id.write_back)
    ImageView write_back;
    //绘制控件区域
    @Bind(R.id.show_view_layout)
    RelativeLayout show_view_layout;
    @Bind(R.id.write_view)
    WriteNoFigerSaveView write_view;

    //绘图切换view
    //橡皮擦
    @Bind(R.id.rubber)
    WriteDrawLayout rubber;
    //铅笔
    @Bind(R.id.pen)
    WriteDrawLayout pen;
    //底部操作按钮
    @Bind(R.id.pen_group)
    RadioGroup pen_group;
    /**
     * 背景图片地址
     */
    private String backImgPath;
    private String title;
    private boolean statusShow;
    private boolean isClicksetingPen = true;
    /**
     * activity执行了onstop
     */
    private boolean isStop = false;
    private long clickTime = 0;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_pic_postil;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras();
        }
        if (savedInstanceState == null) finish();
        backImgPath = savedInstanceState.getString("backImgPath");
        title = savedInstanceState.getString("title");
        statusShow = savedInstanceState.getBoolean("titleShow",false);

        if (StringUtils.isNull(backImgPath)) finish();

        LocationPhotoLoder.getInstance().loadImage(backImgPath, new Sucess() {
            @Override
            public void setSucess(Bitmap bitmap, boolean isS) {
                if (isfinish) return;
                //获得处理后的背景图片
                int w=bitmap.getWidth();
                int h=bitmap.getHeight();
                if (h<w){
//                    Bitmap newb = Bitmap.createBitmap(h, w, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
                    Matrix m = new Matrix();
                    m.postScale(1, -1);   //镜像垂直翻转
                    m.postScale(-1, 1);   //镜像水平翻转
                    m.postRotate(-90);  //旋转-90度
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, m, true);
                    LocationPhotoLoder.getInstance().addBitmapToLruCache(backImgPath,bitmap);
                }
                write_back.setImageBitmap(bitmap);
            }
        }, WindowsUtils.WritedrawWidth, WindowsUtils.WritedrawHeight,false);

        title_postil.setText(title);

        initView();
    }

    private void initView() {
        pen.setallValue(R.mipmap.pencil, false);
        rubber.setallValue(R.mipmap.rubber, false);
        quit_pic_postil.setOnClickListener(this);

        pen_group.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.pen6)).setOnClickListener(this);

        pen.setOnClickListener(penClick);
        rubber.setOnClickListener(penClick);

        pen.changeStatus(true);
    }

    View.OnClickListener penClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isRubber = false;
            switch (v.getId()) {
                case R.id.pen:
                    isRubber = false;
                    break;
                case R.id.rubber:
                    isRubber = true;
                    break;
                default:
                    break;
            }
            if (write_view.isNibWipe() == isRubber) return;
            pen.changeStatus(!isRubber);
            rubber.changeStatus(isRubber);
            write_view.setNibWipe(isRubber);
            setingPenIndex();
        }
    };

    /**
     * 设置笔记大小
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        write_view.setCanDraw(false, 1);
        int position = -1;
        switch (checkedId) {
            case R.id.pen0:
                position = 0;
                break;
            case R.id.pen1:
                position = 1;
                break;
            case R.id.pen2:
                position = 2;
                break;
            case R.id.pen3:
                position = 3;
                break;
            case R.id.pen4:
                position = 4;
                break;
            case R.id.pen5:
                position = 5;
                break;
            case R.id.pen6:

                break;
            default:
                break;
        }
        if (position != -1) {
            if (write_view.isNibWipe()) {
                int size = BrushSettingUtils.getInstance(PicPostilActivity.this).getRubberIndexSize(position);
                write_view.setClearLineWidth(size);
            } else {
                int size = BrushSettingUtils.getInstance(PicPostilActivity.this).getDrawLineIndexSize(position);
                write_view.setDrawLineWidth(size);
            }
        }
        write_view.setCanDraw(true, 2);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        Device.currentDevice.hideSystemStatusBar(this);
        acquireWakeLock();
        setingPenIndex();
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (windowFocus)
                    write_view.setCanDraw(true, 22);
            }
        }, 500);
        isStop = false;
        initTimerUtils();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        isStop = true;
        if (write_view != null)
            write_view.setCanDraw(false, 21);
        releaseWakeLock();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        show_view_layout.destroyDrawingCache();
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString("backImgPath", backImgPath);
        outState.putString("title", title);
        outState.putBoolean("titleShow", statusShow);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (!StringUtils.isNull(backImgPath))
            LocationPhotoLoder.getInstance().clearCatch(backImgPath);

        releaseWakeLock();
    }

    @Override
    public void onClick(View v) {
        if (write_view==null||write_view.getTimerUtils()==null)return;
        write_view.getTimerUtils().resetStartTime();
        if (Math.abs(System.currentTimeMillis() - clickTime) < 1000) {
            return;
        }
        clickTime = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.quit_pic_postil://返回
                write_view.setCanDraw(false,6);
                insureDialog("标注保存", "请选择保存方式", "覆盖", "新建", "", new InsureOrQuitListener() {
                    @Override
                    public void isInsure(Object code, boolean is) {
                        saveFile(true,is);
                    }
                });
                break;
            case R.id.pen6:
                //跳转到设置界面
                if (isClicksetingPen) {
                    write_view.setCanDraw(false,6);
                    CustomPenActivity.startCustomPen(this, !write_view.isNibWipe());
                }
                break;
            default:
                break;
        }
    }
private String savePath;
    /**
     * 保存文件
     * @param finish 保存后是否退出
     * @param isReplace 是否替换以前文件
     */
    private void saveFile(final boolean finish, boolean isReplace){
         savePath=backImgPath;
        if (!isReplace) {
            File file = new File(backImgPath);
            savePath=file.getParent()+"/"+System.currentTimeMillis()+".png";
        }
        show_view_layout.setDrawingCacheEnabled(true);
        show_view_layout.buildDrawingCache();
       final Bitmap b = show_view_layout.getDrawingCache();
        dialogShowOrHide(true,"保存中");
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(savePath);
                    if (null != fos) {
                        b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                    }
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (isfinish)return;

                            show_view_layout.destroyDrawingCache();
                            dialogShowOrHide(false,"");
                            File file = new File(savePath);
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file));
                            PicPostilActivity.this.sendBroadcast(intent);
                            if (finish){
                                backActivity();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        write_view.setCanDraw(false,6);
        insureDialog("退出提示", "请确认是否保存", "保存", "丢弃", "", new InsureOrQuitListener() {
            @Override
            public void isInsure(Object code, boolean is) {
                if (is){
                    onClick(quit_pic_postil);
                }else {
                    backActivity();
                }
            }
        });

    }

    private void backActivity() {
        write_view.setleaveScribbleMode(false, 0);
        if (statusShow){
            Device.currentDevice.showSystemStatusBar(this);
            isfinish=true;
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            },100);
        }else {
            finish();
        }
    }
    private void setingPenIndex() {
        isClicksetingPen = false;
        if (write_view.isNibWipe()) {
            ((RadioButton) (pen_group.getChildAt(BrushSettingUtils.getInstance(PicPostilActivity.this).pitchOnRubberIndex()))).performClick();
        } else {
            ((RadioButton) (pen_group.getChildAt(BrushSettingUtils.getInstance(PicPostilActivity.this).pitchdrawLineIndex()))).performClick();
        }
        write_view.setClearLineWidth(BrushSettingUtils.getInstance(this).getRubberSize());
        write_view.setDrawLineWidth(BrushSettingUtils.getInstance(this).getDrawLineSize());
        isClicksetingPen = true;
    }

    private void initTimerUtils() {
        long timeouts = Settings.System.getLong(getContentResolver(), "screen_off_timeout",
                3 * 60 * 1000 /* default, 3 mininutes */);
        int timeout = 0;//系统休眠时间 秒
        if (timeouts > Integer.MAX_VALUE) {
            timeout = Integer.MAX_VALUE;
        } else {
            timeout = (int) (timeouts / 1000);
        }
        APPLog.e("系统休眠时间", timeout);
        write_view.setTimerUtils(TimerUtils.TimerE.UP, 0, timeout, new TimerUtils.TimeListener() {
            @Override
            public void cuttentTime(int time) {
            }

            @Override
            public void TimeEnd() {
                if (!isStop) {
                    //结束操作
                    SystemUtils.putOutScreen(PicPostilActivity.this);
                    releaseWakeLock();
                }
            }
        });
    }

    private boolean windowFocus = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isfinish)return;
        windowFocus = hasFocus;
        if (hasFocus) {
            getHandler().removeCallbacksAndMessages(null);
            Device.currentDevice.hideSystemStatusBar(this);
            write_view.setCanDraw(true, 23);
        } else {
            write_view.setCanDraw(false, 24);
        }
    }

    PowerManager.WakeLock wakeLock = null;

    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock() {
        if (null == wakeLock || !wakeLock.isHeld()) {
            APPLog.e("开启WakeLock");
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "PostLocationService");
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    //释放设备电源锁
    private void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            APPLog.e("释放WakeLock");
            wakeLock.release();
            wakeLock = null;
        }
    }
}
