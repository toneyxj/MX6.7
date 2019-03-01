package com.moxi.bookstore.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.moxi.bookstore.R;
import com.moxi.bookstore.dialog.LoadingProgressDialog;

public class Test extends Activity {
    LoadingProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        pd=LoadingProgressDialog.create(this,"loading",true,null);
        //pd.setMessage("loading...");
    }

    public void showDialog(View v){
        pd.show();
    }
    public void closeDialog(View v){
        pd.dismiss();
    }
}
