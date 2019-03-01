package com.moxi.filemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.moxi.filemanager.adapter.FileAdapter;
import com.moxi.filemanager.fragment.FileFragment;
import com.moxi.filemanager.model.FileModel;
import com.moxi.filemanager.utils.AllImageFile;
import com.moxi.filemanager.utils.PDFCreateRunalbe;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.view.SildeFrameLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;

import static org.litepal.LitePalApplication.getContext;

/**
 * 图片导出为pdf文件编辑界面
 */
public class PhotoExportPDFActivity extends BaseActivity implements View.OnClickListener, SildeFrameLayout.SildeEventListener {

    public static void startPhotoExportPDF(Context context, String floder,int requestCode) {
        Intent intent = new Intent(context, PhotoExportPDFActivity.class);
        intent.putExtra("floder", floder);
        if (context instanceof Activity){
           ((Activity) context).startActivityForResult(intent,requestCode);
        }else {
            context.startActivity(intent);
        }

    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_photo_export_pdf;
    }

    @Bind(R.id.back_button)
    TextView back_button;

    @Bind(R.id.figer_layout)
    SildeFrameLayout figer_layout;

    @Bind(R.id.show_layout)
    GridView show_layout;

    @Bind(R.id.export_path)
    TextView export_path;
    @Bind(R.id.select_style)
    Button select_style;
    @Bind(R.id.export_pdf)
    Button export_pdf;
    @Bind(R.id.loading)
    TextView loading;
    @Bind(R.id.show_index)
    TextView show_index;

    private String floder;
    /**
     * 当前显示页数
     */
    private int CurrentIndex = 0;
    /**
     * 总页数
     */
    private int totalIndex = 1;
    private int pageSize = 25;
    private FileAdapter adapter;
    private List<FileModel> sonList = new ArrayList<>();
    private List<FileModel> listData = new ArrayList<>();
    private PDFCreateRunalbe pdfRunable;

    private int itemWidth;
    private int itemHeight;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            floder = savedInstanceState.getString("floder", "");
        } else {
            floder = getIntent().getStringExtra("floder");
        }
        if (StringUtils.isNull(floder)) {
            ToastUtils.getInstance().showToastShort("文件夹路径不能为空");
            finish();
            return;
        }
        File flo = new File(floder);
        if (!flo.exists()) {
            ToastUtils.getInstance().showToastShort("文件夹路径不存在");
            finish();
            return;
        }
        if (!flo.isDirectory()) {
            ToastUtils.getInstance().showToastShort("需要文件夹哟！！");
            finish();
            return;
        }
        if (!flo.canRead()) {
            ToastUtils.getInstance().showToastShort("文件夹路径不可读");
            finish();
            return;
        }

        back_button.setOnClickListener(this);
        select_style.setOnClickListener(this);
        export_pdf.setOnClickListener(this);
        figer_layout.setListener(this);

        select_style.setText("全选");

        export_path.setText("导出路径：" + flo.getParent());

        select_style.setVisibility(View.INVISIBLE);
        export_pdf.setVisibility(View.INVISIBLE);

        new AllImageFile(getContext(), floder, false, new AllImageFile.ImageFileListener() {
            @Override
            public void getFileSucess(List<FileModel> results) {
                if (PhotoExportPDFActivity.this.isFinishing()) return;
                listData.addAll(results);
                nameSort();
            }
        }).execute();

        show_layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sonList.get(position).changeSelect();
                adapter.updateSelect(position, show_layout);
                setAllSelect();

            }
        });

    }

    public void setAllSelect() {
        boolean is = true;
        for (int i = 0; i < listData.size(); i++) {
            if (!listData.get(i).isSelect) {
                is = false;
                break;
            }
        }
        select_style.setText(is ? "取消全选" : "全选");
    }
    private void setHitn(boolean isshow,String hitn){
        if (isshow) {
            loading.setVisibility(View.VISIBLE);
            loading.setText(hitn);
        } else {
            loading.setVisibility(View.GONE);
        }
    }

    /**
     * 分配下面数据以刷新
     */
    public void initSonData() {
        if (isFinishing()) return;
        setHitn(listData.size() == 0,"文件夹下暂无图片");
        //计算页数
        totalIndex = listData.size() / pageSize;
        totalIndex += listData.size() % pageSize == 0 ? 0 : 1;

        //计算当前页数
        if (CurrentIndex > totalIndex - 1) {
            CurrentIndex = totalIndex - 1;
        }
        if (CurrentIndex < 0) CurrentIndex = 0;
        if (totalIndex == 0) totalIndex = 1;

        if (listData.size() == 0) {
            adapterItems(listData);
        } else if (totalIndex - 1 == CurrentIndex) {
            adapterItems(listData.subList(CurrentIndex * pageSize, listData.size()));
        } else {
            adapterItems(listData.subList(CurrentIndex * pageSize, (CurrentIndex + 1) * pageSize));
        }
        show_index.setText(String.valueOf(CurrentIndex + 1) + "/" + totalIndex);
    }

    private void adapterItems(List<FileModel> listModels) {
        if (listModels == null) return;
        sonList.clear();
        sonList.addAll(listModels);
        if (adapter == null) {
            adapter = new FileAdapter(this, sonList, itemWidth, itemHeight, true, 0);
            show_layout.setAdapter(adapter);
        } else {
            if (sonList.get(0).getFileType() != 4) {
                adapter = null;
                adapter = new FileAdapter(this, sonList, itemWidth, itemHeight, true, 0);
                show_layout.setAdapter(adapter);
                return;
            }
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == 1) {//排序完成
            itemWidth = figer_layout.getWidth() / 5;
            itemHeight = figer_layout.getHeight() / 5;

            if (itemWidth <= 0 || itemHeight <= 0) {
                getHandler().sendEmptyMessageDelayed(1, 100);
                return;
            }
            select_style.setVisibility(View.VISIBLE);
            export_pdf.setVisibility(View.VISIBLE);
            //显示内容
            initSonData();
        }
    }

    /**
     * 按名称排序
     */
    private void nameSort() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Collections.sort(listData, new FileFragment.NameComparator());
                getHandler().sendEmptyMessage(1);
            }
        }).start();

    }

    public void moveLeft() {
        if (CurrentIndex > 0 && (CurrentIndex <= totalIndex - 1)) {
            CurrentIndex--;
            initSonData();
        }
    }

    public void moveRight() {
        if (CurrentIndex >= totalIndex - 1) {
            return;
        } else {
            CurrentIndex++;
            initSonData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                onBackPressed();
                break;
            case R.id.select_style://全选与否
                if (pdfRunable!=null&&pdfRunable.isExport())return;
                if (listData==null||listData.size()==0)return;
                if (select_style.getText().toString().equals("全选")) {
                    select_style.setText("取消全选");
                    for (FileModel model : listData) {
                        model.isSelect = true;
                    }
                } else {
                    select_style.setText("全选");
                    for (FileModel model : listData) {
                        model.isSelect = false;
                    }
                }
                adapter.notifyDataSetChanged();

                break;
            case R.id.export_pdf://导出pdf文件按钮
                if (pdfRunable!=null&&pdfRunable.isExport())return;
                if (listData==null||listData.size()==0)return;
                List<String> exports = new ArrayList<>();
                for (int i = 0; i < listData.size(); i++) {
                    if (listData.get(i).isSelect){
                        exports.add(listData.get(i).getFilePath());
                    }
                }
                if (exports.size()==0){
                    ToastUtils.getInstance().showToastShort("请选择导出图片");
                    return;
                }
                File f=new File(floder);
                acquireWakeLock();
                pdfRunable=new PDFCreateRunalbe(exports, getPdfName(f, 0), new PDFCreateRunalbe.PDFCreateListener() {
                    @Override
                    public void onFinish() {
                        releaseWakeLock();
                        setResult(RESULT_OK);
                        ToastUtils.getInstance().showToastShort("导出PDF文件成功");
                        setHitn(false,"");
                        onBackPressed();
                    }

                    @Override
                    public void onFail(String msg) {
                        releaseWakeLock();
                        setResult(RESULT_OK);
                        ToastUtils.getInstance().showToastShort("导出PDF文件失败");
                        setHitn(false,"");
                    }

                    @Override
                    public void onProgressHitn(String hitn) {
                        setHitn(true,hitn);
                    }
                });
                new Thread(pdfRunable).start();
                break;
            default:
                break;
        }
    }
    private String getPdfName(File f,int index){
        String path=f.getParent();
        if (index==0){
            path+=File.separatorChar+f.getName()+".pdf";
        }else {
            path+=File.separatorChar+f.getName()+index+".pdf";
        }
        if (new File(path).exists()){
            return getPdfName(f,index+1);
        }
        APPLog.e("getPdfName",path);
        return path;
    }


    @Override
    public void onBackPressed() {
        if (pdfRunable!=null&&pdfRunable.isExport()){
            insureDialog("导出提示", "导出pdf中，退出可能导致pdf文件生成不完整",
                    "退出","取消", "文件导出", new InsureOrQuitListener() {
                        @Override
                        public void isInsure(Object code, boolean is) {
                            if (is){
                                if (pdfRunable!=null){
                                    pdfRunable.setFinish(true);
                                }
                                setResult(RESULT_OK);
                                PhotoExportPDFActivity.this.finish();
                            }
                        }
                    });
        }else {
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
        releaseWakeLock();
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString("floder", floder);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (pdfRunable!=null){
            pdfRunable.setFinish(true);
        }
        releaseWakeLock();
    }

    PowerManager.WakeLock wakeLock = null;

    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock() {
        if (null == wakeLock || !wakeLock.isHeld()) {
            APPLog.e("开启WakeLock");
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, getClass()
                    .getCanonicalName());
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            //上一页
            moveLeft();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            //下一页
            moveRight();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onSildeEventLeft() {
        moveLeft();
    }

    @Override
    public void onSildeEventRight() {
        moveRight();
    }
}
