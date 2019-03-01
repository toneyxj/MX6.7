package com.moxi.bookstore.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.interfacess.MyClick;

/**
 * Created by Administrator on 2016/10/14.
 */
public class CompletePayDialog extends Dialog implements View.OnClickListener {
    Context ctx;
    TextView money;
    Button done,cancel;
    String price;
    MyClick listener;
    ImageView payico;
    public static final int ALIY=0;
    public static final int WX=1;
    int ico;
    public CompletePayDialog(Context context, MyClick listener,String price,int ico) {
        super(context);
        this.ctx=context;
        this.listener=listener;
        this.price=price;
        this.ico=ico;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pay_complete);
        payico= (ImageView) findViewById(R.id.iv);
        money=(TextView)findViewById(R.id.payment);
        done=(Button)findViewById(R.id.done);
        cancel=(Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        done.setOnClickListener(this);
        money.setText("¥ "+price);
        if (ico==WX)
            payico.setImageResource(R.mipmap.weixin_ico);
        else
            payico.setImageResource(R.mipmap.aliy_ico);
    }

    @Override
    public void onClick(View v) {
        listener.done(MyClick.COMPLETE);
    }


}
