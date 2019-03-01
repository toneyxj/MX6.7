package com.dangdang.reader.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dangdang.reader.R;

/**
 * Created by liuzhuo on 2015-12-4.
 */
public class ErrView {

    private Context mContext;
    private int imgId, txtId, btnId, leftBtnId, rightBtnId;

    private View mView;
    private ImageView prompt_icon_iv;
    private TextView prompt_msg_tv;
    private Button prompt_btn, left_btn, right_btn;

    public ErrView(Context context){
        mContext = context;
    }

    public View getView(){
        return mView;
    }

    /**
     * 初始化服务器出错的提示view
     *
     * @param propmtImageResid
     *            提示图片资源id <=0代表不显示
     * @param propmtTextResid
     *            提示文字资源id <=0代表不显示
     * @param propmtButtonResid
     *            提示按钮文字资源id <=0代表不显示
     */
    public void initPromptView(int propmtImageResid, int propmtTextResid,
                                int propmtButtonResid, View.OnClickListener listener,
                                View.OnClickListener mOnClickListener) {
        if(mContext == null){
            return;
        }

        initData(propmtImageResid, propmtTextResid, propmtButtonResid, 0, 0);

        initView(listener, mOnClickListener);
    }

    /**
     * 刷新服务器出错的提示view
     */
    public void refresh(int imgId, int txtId, int btnId, View.OnClickListener listener,
                        View.OnClickListener mOnClickListener){
        if(this.imgId == imgId && this.txtId == txtId && this.btnId == btnId)
            return;

        initData(imgId, txtId, btnId, 0, 0);

        initView(listener, mOnClickListener);
    }

    public void initNoDataPromptView(int propmtImageResid, int propmtTextResid,int propmtButtonResid,
                               int leftButtonResid, int rightButtonResid, View.OnClickListener listener,
                               View.OnClickListener mOnClickListener) {
        if(mContext == null){
            return;
        }

        initData(propmtImageResid, propmtTextResid, propmtButtonResid, leftButtonResid, rightButtonResid);

        initView(listener, mOnClickListener);
    }
    public void initNoDataPromptView(int propmtImageResid, int propmtTextResid,
                                     int leftButtonResid, int rightButtonResid, View.OnClickListener listener,
                                     View.OnClickListener mOnClickListener) {
        if(mContext == null){
            return;
        }

        initData(propmtImageResid, propmtTextResid, 0, leftButtonResid, rightButtonResid);

        initView(listener, mOnClickListener);
    }
    public void refreshNoDataPromptView(int propmtImageResid, int propmtTextResid,
                                        int propmtButtonResid, int leftButtonResid, int rightButtonResid, View.OnClickListener listener,
                               View.OnClickListener mOnClickListener) {
        if(this.imgId == propmtImageResid && this.txtId == propmtTextResid && this.leftBtnId == leftButtonResid && this.rightBtnId == rightButtonResid)
            return;

        initData(propmtImageResid, propmtTextResid, propmtButtonResid, leftButtonResid, rightButtonResid);

        initView(listener, mOnClickListener);
    }


    public void refreshNoDataPromptView(int propmtImageResid, int propmtTextResid,
                                        int leftButtonResid, int rightButtonResid, View.OnClickListener listener,
                                        View.OnClickListener mOnClickListener) {
        if(this.imgId == propmtImageResid && this.txtId == propmtTextResid && this.leftBtnId == leftButtonResid && this.rightBtnId == rightButtonResid)
            return;

        initData(propmtImageResid, propmtTextResid, 0, leftButtonResid, rightButtonResid);

        initView(listener, mOnClickListener);
    }
    private void initData(int propmtImageResid, int propmtTextResid,
                          int propmtButtonResid, int leftButtonResid, int rightButtonResid){
        imgId = propmtImageResid;
        txtId = propmtTextResid;
        btnId = propmtButtonResid;
        leftBtnId = leftButtonResid;
        rightBtnId = rightButtonResid;
    }

    private void initView(View.OnClickListener listener,
                          View.OnClickListener mOnClickListener){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.common_prompt_page, null);
        mView.setClickable(true);

        prompt_icon_iv = (ImageView) mView.findViewById(R.id.prompt_icon_iv);
        prompt_msg_tv = (TextView) mView.findViewById(R.id.prompt_msg_tv);
        prompt_btn = (Button) mView.findViewById(R.id.prompt_btn);
        left_btn = (Button) mView.findViewById(R.id.left_btn);
        right_btn = (Button) mView.findViewById(R.id.right_btn);

        // 提示图片
        if (imgId > 0) {
            prompt_icon_iv.setVisibility(View.VISIBLE);
            prompt_icon_iv.setImageResource(imgId);
        } else {
            prompt_icon_iv.setVisibility(View.GONE);
        }

        // 提示信息
        if (txtId > 0) {
            prompt_msg_tv.setVisibility(View.VISIBLE);
            prompt_msg_tv.setText(txtId);
        } else {
            prompt_msg_tv.setVisibility(View.GONE);
        }

        // 服务器出错时的刷新按钮
        if (btnId > 0) {
            prompt_btn.setVisibility(View.VISIBLE);
            prompt_btn.setText(btnId);
            if (listener == null)
                prompt_btn.setOnClickListener(mOnClickListener);
            else
                prompt_btn.setOnClickListener(listener);
        } else {
            prompt_btn.setVisibility(View.GONE);
        }

        // 数据被删除或者下架时左边的按钮
        if (leftBtnId > 0) {
            left_btn.setVisibility(View.VISIBLE);
            left_btn.setText(leftBtnId);
            if (listener == null)
                left_btn.setOnClickListener(mOnClickListener);
            else
                left_btn.setOnClickListener(listener);
        } else {
            left_btn.setVisibility(View.GONE);
        }

        // 数据被删除或者下架时右边的按钮
        if (rightBtnId > 0) {
            right_btn.setVisibility(View.VISIBLE);
            right_btn.setText(rightBtnId);
            if (listener == null)
                right_btn.setOnClickListener(mOnClickListener);
            else
                right_btn.setOnClickListener(listener);
        } else {
            right_btn.setVisibility(View.GONE);
        }
    }

}
