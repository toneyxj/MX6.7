package com.mx.user.activity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.Toastor;
import com.mx.user.R;
import com.mx.user.adapter.VerificationAdapter;
import com.mx.user.adapter.VerificationContentAdapter;
import com.mx.user.model.VerificationModel;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by King on 2017/11/29.
 */

public class TestVerificationActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.mx_verification_code)
    GridView gridView;
    @Bind(R.id.img_verification_code)
    ImageView imageView;
    @Bind(R.id.fl_retry_verification)
    FrameLayout frameLayout;
    @Bind(R.id.gv_verification_content)
    GridView gridViewContent;
    private VerificationAdapter verificationAdapter;
    private List<String> listVerification = new ArrayList<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        frameLayout.setOnClickListener(this);
        verificationAdapter = new VerificationAdapter(this, listVerification, gridView);
        gridView.setAdapter(verificationAdapter);
        setVerificationContentAdapter();
    }

    private void setVerificationContentAdapter() {
        listVerification.clear();
        verificationAdapter.notifyDataSetChanged();
        OkHttpUtils.post().url(Constant.verificationUrl).addParams("imei", "JS004J00T004BH1E0057").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("onError", e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    VerificationModel verificationModel = GsonTools.getPerson(response, VerificationModel.class);
                    byte[] bytes = Base64.decode(verificationModel.getResult().getImg(), Base64.DEFAULT);
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    String temp = verificationModel.getResult().getCodes();
                    String[] tempStr = temp.split(",");
                    final List<String> listStr = new ArrayList<>(Arrays.asList(tempStr));
                    listStr.add(4, "删除");
                    listStr.add(9, "清除");
                    listStr.add(14, "确定");
                    gridViewContent.setAdapter(new VerificationContentAdapter(TestVerificationActivity.this, listStr));
                    gridViewContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (position % 5 == 4) {
                                switch (position) {
                                    case 4:
                                        if (listVerification.size() > 0) {
                                            listVerification.remove(listVerification.size() - 1);
                                            verificationAdapter.notifyDataSetChanged();
                                        }
                                        break;
                                    case 9:
                                        listVerification.clear();
                                        verificationAdapter.notifyDataSetChanged();
                                        break;
                                    case 14:
                                        if (listVerification.size() == 4) {

                                        } else {
                                            Toastor.showToast(TestVerificationActivity.this, "请输入完整的验证码");
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                if (listVerification.size() < 4) {
                                    listVerification.add(listStr.get(position));
                                    verificationAdapter.notifyDataSetChanged();
                                    if (listVerification.size() == 4) {
                                        Toastor.showToast(TestVerificationActivity.this, "提交验证码");
                                    }
                                }
                            }
                        }
                    });
                }catch (Exception e){

                }

            }
        });
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
    protected int getMainContentViewId() {
        return R.layout.mx_popwindow_veritry_code;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_retry_verification:
                setVerificationContentAdapter();
                break;
            default:
                break;
        }
    }
}
