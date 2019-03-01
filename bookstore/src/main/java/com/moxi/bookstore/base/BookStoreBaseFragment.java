package com.moxi.bookstore.base;

import com.moxi.bookstore.R;
import com.moxi.bookstore.request.HttpGetRequest;
import com.moxi.bookstore.request.HttpPostRequest;
import com.moxi.bookstore.request.RequestCallBack;
import com.moxi.bookstore.request.ReuestKeyValues;
import com.mx.mxbase.base.baseFragment;
import com.mx.mxbase.dialog.HitnDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/12.
 */
public abstract class BookStoreBaseFragment extends baseFragment implements RequestCallBack {
    /**
     * 转载请求交互界面
     */
    public ArrayList<String> addRequest = new ArrayList<String>();
    /**
     * 提示dialog
     */
    public HitnDialog dialog;
    public boolean isfinish=false;
    /**
     * get请求方式
     *
     * @param valuePairs
     * @param code
     * @param Url
     * @param show
     */
    public void getData(
            List<ReuestKeyValues> valuePairs, String code, String Url, boolean show, String hitn) {
        hitn=(hitn==null||hitn.equals(""))?getString(R.string.data_loding):hitn;
        if (valuePairs==null)valuePairs=new ArrayList<>();
        if (show) {
//            if (showFail) {
//                showLayoutDialog(hitn);
//            } else {
            showDialog(hitn);
//            }
        }
        new HttpGetRequest(getContext(), this, valuePairs, code, Url, false,show).execute();
    }

    public void hideDialog() {
        if (addRequest.size() > 0) {
            addRequest.remove(0);
        }
        if (dialog != null && dialog.isShowing() && addRequest.size() == 0) {
            dialog.dismiss();
            dialog = null;
        }
    }
    /**
     * 显示dialog
     */
    public void showDialog(String content) {
        if (dialog == null) {
            dialog = dialogShowOrHide(true,content);
        } else {
            dialog.show();
        }
        addRequest.add("1");
    }
    /**
     * post请求方式
     *
     * @param valuePairs
     * @param code
     * @param Url
     * @param show
     */
    public void PostData(
            List<ReuestKeyValues> valuePairs, String code, String Url, boolean show, String hitn) {
        hitn=(hitn==null||hitn.equals(""))?getString(R.string.data_loding):hitn;
        if (valuePairs==null)valuePairs=new ArrayList<>();
        if (show) {
//            if (showFail) {
//                showLayoutDialog(hitn);
//            } else {
            showDialog(hitn);
//            }
        }
        new HttpPostRequest(getContext(), this, valuePairs, code, Url, false,show).execute();
    }

    @Override
    public void onSuccess(String result, String code) {
        hideDialog();
        if (!isfinish)
            Success(result, code);
    }

    @Override
    public void onFail(String code, boolean showFail, int failCode, String msg,String result) {
        if (isfinish)return;
        hideDialog();
        fail(code);
    }

    public void Success(String result, String code) {

    }


    public void fail(String code) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isfinish=true;
    }
}
