package com.moxi.systemapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.moxi.systemapp.R;

public class UpdataHwtActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updata_hwt);

        findViewById(R.id.updata).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                RootCmd.startUpF();
            }
        });
    }
}
