package com.moxi.biji;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.evernote.client.android.login.EvernoteLoginFragment;
import com.moxi.biji.intf.BackImp;
import com.moxi.biji.intf.NoteUtilsImp;
import com.moxi.biji.mdoel.BiJiModel;
import com.moxi.biji.yingxiangbiji.YingXiangUtils;

import java.io.Serializable;

public class BijiActivity extends FragmentActivity implements EvernoteLoginFragment.ResultCallback, BackImp {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ying_xiang);
        try {
            if (savedInstanceState == null) {
                savedInstanceState = getIntent().getExtras();
            }
            Serializable sb=savedInstanceState.getSerializable("model");
            if (sb==null){
                model=BiJiModel.builder().setTitle(savedInstanceState.getString("title"))
                        .setContent(savedInstanceState.getString("content"))
                        .setNoteBook(savedInstanceState.getString("noteBook"))
                        .setSdkType(savedInstanceState.getInt("sdkType"))
                        .setShareType(savedInstanceState.getInt("shareType"));
            }else {
                model = (BiJiModel)sb;
            }
            if (null==model.getTitle()){finish(); return;}
            startShare();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void startShare() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (model.getSdkType()) {
                    case 1://印象笔记
                        noteUtils = new YingXiangUtils();
                        if (noteUtils.isLogin(BijiActivity.this)) {
                            startYingxiang();
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
                noteUtils.sendText(model, this);
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
                Toast.makeText(BijiActivity.this, "同步成功", Toast.LENGTH_LONG).show();
                BijiActivity.this.finish();
            }
        });
    }

    @Override
    public void error(final Exception e) {
        e.printStackTrace();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BijiActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                BijiActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isStart = false;
    }
}

