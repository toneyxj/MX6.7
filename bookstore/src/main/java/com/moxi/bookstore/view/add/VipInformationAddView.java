package com.moxi.bookstore.view.add;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.modle.mediaCategory.MonthlyChannel;

/**
 * Created by xj on 2017/11/14.
 */

public class VipInformationAddView  extends LinearLayout {
    public VipInformationAddView(Context context, ClickVipListener listener) {
        super(context);
        init(context);
        this.listener=listener;
    }
    private TextView hitn_text;
    private TextView get_vip;
    private ClickVipListener listener;
    private MonthlyChannel channel;
    private boolean request;


    private void init(Context context) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.addview_vip_information,
                this);
        hitn_text = (TextView) view.findViewById(R.id.hitn_text);
        get_vip = (TextView) view.findViewById(R.id.get_vip);
        get_vip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickVip();
            }
        });
    }

    public void setInit(MonthlyChannel model,boolean request) {
        this.request=request;
        this.channel=model;

        get_vip.setVisibility((model!=null&&model.isBoughtMonthly())?INVISIBLE:VISIBLE);
        hitn_text.setText((model!=null&&model.isBoughtMonthly())?"您的vip租阅到期时间为："+model.getBoughtMonthlyEndTime():"开通租阅权限，免费读好书");
    }

    public MonthlyChannel getChannel() {
        return channel;
    }

    /**
     * 是否请求完成
     * @return
     */
    public boolean isRequest() {
        return request;
    }

    public interface ClickVipListener{
        void onClickVip();
    }
}