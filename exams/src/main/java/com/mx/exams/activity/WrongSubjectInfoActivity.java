package com.mx.exams.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mx.exams.R;
import com.mx.exams.adapter.SubjectWrongInfoGridAdapter;
import com.mx.exams.model.WrongExamsModel;

import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by zhengdelong on 16/9/22.
 */

public class WrongSubjectInfoActivity extends Activity {

    private LinearLayout ll_base_back;
    private TextView tv_base_back;
    private TextView tv_base_mid_title;

    private TextView title_info;


    private String subjectId;
    private String subjectName;
    private int wrongCount;

    @Bind(R.id.info_list)
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_wrong_subject_info);
        initView();
        getIntentData();
        initData();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        subjectId = intent.getStringExtra("subjectId");
        subjectName = intent.getStringExtra("subjectName");
        wrongCount = intent.getIntExtra("wrongCount", -1);
    }

    private void initData() {
        String title = "科目:" + subjectName + "     " + "共" + wrongCount + "套题";
        title_info.setText(title);
        List<WrongExamsModel> listWrong = new ArrayList<>();
        String sql = "select * from DBExamsModel where subjectid=" + subjectId;
        Cursor cursor = null;
        try {
            cursor = Connector.getDatabase().rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                listWrong.clear();
                do {
                    WrongExamsModel wem = new WrongExamsModel();
                    wem.setSubjectName(cursor.getString(cursor.getColumnIndex("subjectname")));
                    wem.setSubjectId(cursor.getString(cursor.getColumnIndex("subjectid")));
                    listWrong.add(wem);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (listWrong.size() > 0) {
            SubjectWrongInfoGridAdapter adapter = new SubjectWrongInfoGridAdapter(this,listWrong);
            listView.setAdapter(adapter);
        }
    }

    private void initView() {
        title_info = (TextView) findViewById(R.id.title_info);
        tv_base_mid_title = (TextView) findViewById(R.id.tv_base_mid_title);
        tv_base_mid_title.setVisibility(View.GONE);
        tv_base_back = (TextView) findViewById(R.id.tv_base_back);
        tv_base_back.setText("错题库");
        ll_base_back = (LinearLayout) findViewById(R.id.ll_base_back);
        ll_base_back.setVisibility(View.VISIBLE);
        ll_base_back.setOnClickListener(backClick);
    }
    View.OnClickListener backClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            WrongSubjectInfoActivity.this.finish();
        }
    };
}
