package com.moxi.biji;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.evernote.client.android.login.EvernoteLoginFragment;
import com.evernote.client.android.type.NoteRef;
import com.moxi.biji.intf.BackImp;
import com.moxi.biji.intf.NoteUtilsImp;
import com.moxi.biji.intf.SucessImp;
import com.moxi.biji.mdoel.BiJiModel;
import com.moxi.biji.utils.StringUtils;
import com.moxi.biji.yingxiangbiji.YingXiangUtils;

import java.io.Serializable;
import java.util.List;

public class BijiActivity extends FragmentActivity implements EvernoteLoginFragment.ResultCallback, BackImp, View.OnClickListener {
    public static boolean isStart = false;

    /**
     * @param context 上下文
     */
    public static void startBJ(Context context, BiJiModel model) {
        if (isStart) return;
        isStart = true;
        Intent intent = new Intent(context, BijiActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", model);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private BiJiModel model;
    //笔记处理工具类
    private NoteUtilsImp noteUtils;
    private LinearLayout dialog_layout;
    private EditText input_new_txt;
    private Button quit;
    private Button fugai;
    private Button chongmingming;
    private List<NoteRef> deleteRefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ying_xiang);
        dialog_layout = (LinearLayout) findViewById(R.id.dialog_layout);
        input_new_txt = (EditText) findViewById(R.id.input_new_txt);
        quit = (Button) findViewById(R.id.quit);
        fugai = (Button) findViewById(R.id.fugai);
        chongmingming = (Button) findViewById(R.id.chongmingming);


        ViewGroup.LayoutParams params = dialog_layout.getLayoutParams();
        params.width = (int) (getScreenW() * 0.8);
        dialog_layout.setLayoutParams(params);

        dialog_layout.setVisibility(View.GONE);

        quit.setOnClickListener(this);
        fugai.setOnClickListener(this);
        chongmingming.setOnClickListener(this);

        try {
            if (savedInstanceState == null) {
                savedInstanceState = getIntent().getExtras();
            }
            Serializable sb = savedInstanceState.getSerializable("model");
            if (sb == null) {
                model = BiJiModel.builder().setTitle(savedInstanceState.getString("title"))
                        .setContent(savedInstanceState.getString("content"))
                        .setNoteBook(savedInstanceState.getString("noteBook"))
                        .setSdkType(savedInstanceState.getInt("sdkType"))
                        .setShareType(savedInstanceState.getInt("shareType"));
            } else {
                model = (BiJiModel) sb;
            }
            if (null == model.getTitle()) {
                finish();
                return;
            }
            startShare();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    /**
     * 获取屏幕宽度
     */
    private int getScreenW() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        int w = dm.widthPixels;
        return w;

    }

    private void startShare() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (model.getSdkType()) {
                    case 1://印象笔记
                        noteUtils = new YingXiangUtils();
                        if (noteUtils.isLogin(BijiActivity.this)) {
                            noteUtils.sendNote(model, BijiActivity.this);
                        }
                        break;
                    case 2://有道云笔记

                        break;
                    default:
                        break;
                }
            }
        }).start();

    }

    /**
     * 印象笔记
     */
    private void startYingxiang() {
        switch (model.getShareType()) {
            case 1://文本
                break;
            case 2://图片
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable("model", model);
    }

    @Override
    public void onLoginFinished(boolean successful) {
        if (successful) {
            //登录成功
            startShare();
        } else {
            finish();
        }
    }


    @Override
    public void start() {
    }

    @Override
    public void result() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing())return;
                Toast.makeText(BijiActivity.this, "分享成功", Toast.LENGTH_LONG).show();
                BijiActivity.this.finish();
            }
        });
    }

    @Override
    public void removeRepeat(final List<NoteRef> refs, final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing())return;
                deleteRefs = refs;
                input_new_txt.setText(title);
                dialog_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void error(final Exception e) {
        e.printStackTrace();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing())return;
                Toast.makeText(BijiActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                BijiActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isStart = false;
        deleteRefs = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quit:
                finish();
                break;
            case R.id.chongmingming:
                String value = input_new_txt.getText().toString().trim();
                if (null == value || value.isEmpty()) return;

                if (value.equals(model.getTitle())) {
                    Toast.makeText(BijiActivity.this, "请重新输入标题名称", Toast.LENGTH_SHORT).show();
                    return;
                }

                model.setTitle(value);
                startShare();
                dialog_layout.setVisibility(View.GONE);
                break;
            case R.id.fugai:
                dialog_layout.setVisibility(View.GONE);
                //删除以前的文件
                if (deleteRefs != null)
                    noteUtils.deleteNote(deleteRefs, new SucessImp() {
                        @Override
                        public void onSucess() {
                            if (isFinishing())return;
                            startShare();
                        }

                        @Override
                        public void onFail() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isFinishing())return;
                                    Toast.makeText(BijiActivity.this, "笔记覆盖失败", Toast.LENGTH_SHORT).show();
                                    dialog_layout.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                break;
        }
    }

    /**
     * 点击其它地方关闭软键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (StringUtils.isShouldHideInput(v, ev)) {
                StringUtils.closeIMM(this, v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}

