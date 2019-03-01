package com.moxi.haierc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.haierc.R;
import com.mx.mxbase.utils.GlideUtils;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.StartActivityUtils;
import com.mx.mxbase.utils.StringUtils;

public class ScreenShotPicActivity extends Activity implements View.OnClickListener{


    private ImageView show_view;
    private TextView delete;
    private TextView pizhu;

    private String backImgPath;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot_pic);
        if (savedInstanceState!=null){
            finish();
        }

        backImgPath=getIntent().getExtras().getString("backImgPath");
        title=getIntent().getExtras().getString("title");

        if (StringUtils.isNull(backImgPath)||StringUtils.isNull(title)) {
            Log.i("ScreenShotPicActivity","backImgPath/title is null");
            return;
        }
        show_view=(ImageView)findViewById(R.id.show_view);
        delete=(TextView)findViewById(R.id.delete);
        pizhu=(TextView)findViewById(R.id.pizhu);

        delete.setOnClickListener(this);
        pizhu.setOnClickListener(this);
        show_view.setOnClickListener(this);

        GlideUtils.getInstance().locatonPic(this,show_view,backImgPath);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("backImgPath",backImgPath);
        outState.putString("title",title);
    }
    private long clickTime=0;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.show_view:
                if (System.currentTimeMillis()-clickTime<500){
                    this.finish();
                    return;
                }
                clickTime=System.currentTimeMillis();
                break;
            case R.id.delete://
                StringUtils.deleteFile(backImgPath);
                this.finish();
                break;
            case R.id.pizhu://跳往批注
                StartActivityUtils.startPicPostil(this,backImgPath,title);
                this.finish();
                break;
            default:
                break;
        }
    }
}
