package com.moxi.bookstore.dialog;

import android.app.Dialog;
import android.content.Context;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.interfacess.MyClick;

/**
 * Created by Administrator on 2016/10/14.
 */
public class PayMethodsDialog extends Dialog implements View.OnClickListener {
    Context ctx;
    CheckBox weixin_cb;
    Button done;
    MyClick listener;
    public PayMethodsDialog(Context context, MyClick listener) {
        super(context);
        this.ctx=context;
        this.listener=listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_payment);
        weixin_cb=(CheckBox) findViewById(R.id.weixin_cb);
        weixin_cb.setChecked(true);
        done=(Button)findViewById(R.id.done);
        done.setOnClickListener(this);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        listener.done(MyClick.PAYMETHOD);
    }

    public boolean getSelected(){
        if (weixin_cb.isChecked())
            return true;
        else

            return false;
    }


}
