package com.dangdang.reader.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.dangdang.reader.R;
import com.dangdang.zframework.utils.UiUtil;

/**
 * 非WiFi下载确认框
 * Created by xrr on 2015/7/2.
 */
public class MobilNetDownloadPromptDialog extends BottomDialog {

    private Context mContext;
    private View mRootView;

    public MobilNetDownloadPromptDialog(Context context) {
        mContext = context;
        int width = UiUtil.dip2px(context, 254);
        mRootView = LayoutInflater.from(context).inflate(R.layout.mobile_net_download_prompt_dialog, null);
        super.init(context, mRootView, Gravity.CENTER, width);
    }

    public void setOnLeftClickListener(View.OnClickListener onClickListener){
        mRootView.findViewById(R.id.left_tv).setOnClickListener(onClickListener);
    }

    public void setOnRightClickListener(View.OnClickListener onClickListener){
        mRootView.findViewById(R.id.right_tv).setOnClickListener(onClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.left_tv) {
                mDialog.dismiss();

            }
        }
    };

    @Override
    protected void setListener(View view) {
    }

}
