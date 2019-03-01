package com.moxi.systemapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.moxi.systemapp.R;
import com.moxi.systemapp.SystemApp;
import com.moxi.systemapp.views.CorpImageView;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.interfaces.Sucess;
import com.mx.mxbase.utils.LocationPhotoLoder;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;

/**
 * 图像裁剪操作界面
 */
public class PhotoCutActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 启动图片截取activity
     *
     * @param activity    文件截取操作器
     * @param filePath    文件路径
     * @param requestCode 请求返回标识
     */
    public static void startACutctivity(Activity activity, String filePath, int requestCode) {
        Intent intent = new Intent(activity, PhotoCutActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("filePath", filePath);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }

    @Bind(R.id.get_image)
    RelativeLayout get_image;
    @Bind(R.id.cut_photo)
    CorpImageView cut_photo;
    @Bind(R.id.cancle)
    Button cancle;
    @Bind(R.id.restore)
    Button restore;
    @Bind(R.id.result)
    Button result;

    @Override
    protected int getMainContentViewId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        return R.layout.activity_photo_cut;
    }

    private String filePath = "";

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            filePath = savedInstanceState.getString("filePath");
        } else {
            filePath = getIntent().getExtras().getString("filePath");
        }
        LocationPhotoLoder.getInstance().loadImage(filePath, new Sucess() {
            @Override
            public void setSucess(Bitmap bitmap, boolean isS) {
                if (isfinish) return;
                //获得处理后的背景图片
                cut_photo.setImageBitmap(bitmap);
            }
        }, (int) (SystemApp.ScreenWidth * 1.3), (int) (SystemApp.ScreenHeight * 1.3), true);


        cancle.setOnClickListener(this);
        restore.setOnClickListener(this);
        result.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancle:
                onBackPressed();
                break;
            case R.id.restore:
                cut_photo.resetImage();
                break;
            case R.id.result:
                if (isFilecopying) return;
                //获取图片保存到sd卡里面
                insureDialog("图片设置", "请选择图片设置目的", "待机画面", "关机画面", 1, new InsureOrQuitListener() {
                    @Override
                    public void isInsure(Object code, boolean is) {
                        if (is) {//待机画面
                            savePic(getBitmap(), StringUtils.getSaveSystemCorrelationPhoto(2));

                        } else {//关机画面
                            savePic(getBitmap(), StringUtils.getSaveSystemCorrelationPhoto(0));
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    private String tagPath;

    // 保存到sdcard
    private void savePic(final Bitmap b, final String strFileName) {
        tagPath = strFileName;
        File file = new File(strFileName);
        isFilecopying = true;
        APPLog.d("file_exists", file.exists());
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(strFileName);
                    if (null != fos) {
                        b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                    }
                    Message message = new Message();
                    message.what = 1;
                    message.obj = strFileName;
                    getHandler().sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                    StringUtils.deleteFile(strFileName);
                    Message message = new Message();
                    message.what = 0;
                    message.obj = strFileName;
                    getHandler().sendMessage(message);
                }
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        StringUtils.recycleBitmap(b);

                    }
                });
            }
        }).start();

    }

    private boolean isFilecopying = false;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        isFilecopying = false;
        switch (msg.what) {
            case 0://失败
                ToastUtils.getInstance().showToastShort("背景设置失败！");
                break;
            case 1://成功
                updateChomd();
                ToastUtils.getInstance().showToastShort("背景设置成功！");
                onBackPressed();
                break;

        }
    }

    private void updateChomd() {
        try {
            String command = "chmod 777 " + tagPath;
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(command);
        } catch (IOException e) {
            APPLog.e("updateChomd", e.getMessage());
        }
    }

    /**
     * 获取Layout截图
     *
     * @return 所需区域的截图
     */
    private Bitmap getBitmap() {
        get_image.setDrawingCacheEnabled(true);
        get_image.buildDrawingCache();  //启用DrawingCache并创建位图
        Bitmap bitmap = Bitmap.createBitmap(get_image.getDrawingCache()); //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        get_image.setDrawingCacheEnabled(false);  //禁用DrawingCahce否则会影响性能
        return bitmap;
    }

    @Override
    public void onBackPressed() {
        if (isFilecopying) {
            ToastUtils.getInstance().showToastShort("文件拷贝中!");
            return;
        } else {
            super.onBackPressed();
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
        outState.putString("filePath", filePath);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
