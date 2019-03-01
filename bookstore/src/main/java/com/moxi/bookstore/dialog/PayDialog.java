package com.moxi.bookstore.dialog;

import android.app.Dialog;
import android.content.Context;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.interfacess.MyClick;

/**
 * Created by Administrator on 2016/10/14.
 */
public class PayDialog extends Dialog implements View.OnClickListener {
    Context ctx;
    TextView money;
    Button done;
    MyClick listener;
    public PayDialog(Context context,MyClick listener) {
        super(context);
        this.ctx=context;
        this.listener=listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_payment);
        money=(TextView)findViewById(R.id.payment);
        done=(Button)findViewById(R.id.done);
        done.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        listener.done(MyClick.PAYMETHOD);
    }

    public void setMoney(String str){
        money.setText(str);
    }
}
