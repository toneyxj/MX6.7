package com.moxi.bookstore.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.interfacess.BalenceCheckListener;
import com.mx.mxbase.constant.APPLog;

/**
 * Created by Administrator on 2016/11/16.
 */

public class BalenceCheckDialog extends Dialog implements View.OnClickListener {
    static BalenceCheckDialog mIntence;
    static View v;
    public final static int BALENCE_VIEW=0x01;
    public final static int SCANER_VIEW=0x02;
    ImageView closer;
    TextView mybalence,paybalence,gold,silver;
    Button btn1,btn2,btn3;
    LinearLayout balencePay_ll,scanerPay_ll;
    Context context;
    boolean isCharge;//外部创建时定义充值标记
    BalenceCheckListener listener;
    boolean charge =false;//根据账户余额判断购买或充值
    private BalenceCheckDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context=context;
    }

    public static BalenceCheckDialog creat(Context ctx,boolean isCharge){

        if (null==mIntence)
            mIntence=new BalenceCheckDialog(ctx, R.style.MyDialog);
        v=View.inflate(ctx,R.layout.dialog_balencecheck,null);
        mIntence.setContentView(v);
        mIntence.setCanceledOnTouchOutside(false);
        mIntence.setCancelable(false);
        mIntence.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = mIntence.getWindow().getAttributes();
        //lp.height=320;
        mIntence.getWindow().setAttributes(lp);
        mIntence.isCharge=isCharge;
        return mIntence;
    }

    public void setListener(BalenceCheckListener listener){
        this.listener=listener;
    }

    public void showBalence(long goldeb,long silverb,String payable){
        if (mIntence==null){
            creat(context,false);
        }else if (!this.isShowing()) {
            show();
            long totalbalenc=goldeb+silverb;
            mybalence.setText("账户铃铛: " + totalbalenc);
            String hitn=isCharge?"充值铃铛":"所需铃铛";
            paybalence.setText(hitn+": " + payable);
            gold.setText("金铃铛：" + goldeb);
            silver.setText("银铃铛：" + silverb);
            if (totalbalenc<Long.valueOf(payable)){
                charge=true;
                btn1.setText("充值");
            }
        }
    }

    public void hideBalence(){
        if (null!=mIntence&&mIntence.isShowing()){
            APPLog.e("balencedialog dismiss");
            mIntence.dismiss();
            APPLog.e("bd isshowing:"+mIntence.isShowing());
        }
        mIntence=null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        closer=(ImageView) v.findViewById(R.id.close_iv);
        mybalence=(TextView) v.findViewById(R.id.mybalence_tv);
        paybalence=(TextView) v.findViewById(R.id.paybalence_tv);
        gold=(TextView)v.findViewById(R.id.gold_tv);
        silver=(TextView)v.findViewById(R.id.silver_tv);
        btn1=(Button) v.findViewById(R.id.paywithbalence);
        btn2=(Button) v.findViewById(R.id.paywithwx);
        btn3=(Button) v.findViewById(R.id.paywithaliy);
        balencePay_ll=(LinearLayout) v.findViewById(R.id.balence_pay_ll);
        scanerPay_ll=(LinearLayout) v.findViewById(R.id.scaner_pay_ll);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        closer.setOnClickListener(this);
        if (isCharge){
            scanerPay_ll.setVisibility(View.VISIBLE);
            //balencePay_ll.setVisibility(View.GONE);
            //scanerPay_ll.setVisibility(View.VISIBLE);
            btn1.setVisibility(View.INVISIBLE);
        }else {
            scanerPay_ll.setVisibility(View.GONE);
            //scanerPay_ll.setVisibility(View.GONE);
            //balencePay_ll.setVisibility(View.VISIBLE);
            btn1.setVisibility(View.VISIBLE);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.paywithbalence:

                 if (charge){
                    listener.goCharge();
                 }else
                    listener.payWithBalence();
                 break;
             case R.id.paywithwx:
                 listener.payWithWx();
                 break;
             case R.id.close_iv:
                 listener.finshPay();
                 break;
             case R.id.paywithaliy:
                 listener.payWithAliy();
                 break;
         }
        hideBalence();
    }


}
