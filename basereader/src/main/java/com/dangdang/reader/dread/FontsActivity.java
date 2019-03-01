package com.dangdang.reader.dread;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.base.BaseReaderActivity;
import com.dangdang.reader.dread.adapter.FontPagerAdapter;
import com.dangdang.reader.dread.fragment.MyFontsFragment;
import com.dangdang.zframework.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class FontsActivity extends BaseReaderActivity {

    public final static int PAGE_MY_FONT = 0;
    public final static int PAGE_CHARGE_FONT = 1;
    private int mCurrentPage = PAGE_MY_FONT;

    private MyFontsFragment myFontsFragment;
    protected ViewGroup mRootView;
    private ViewPager mViewPager;

    @Override
    protected void onCreateImpl(Bundle savedInstanceState) {
        setContentView(R.layout.activity_fonts);
        initView();
        initFragmentGroup();
    }

    private void initView() {
        mRootView = (ViewGroup) getWindow().getDecorView();
        (findViewById(R.id.common_back)).setOnClickListener(mClickListener);
        ((TextView) findViewById(R.id.common_title)).setText("更多字体");
    }

    private OnClickListener mClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.common_back) {
                onBackPressed();

            } else {
            }
        }
    };


    public void hideGifLoadingByUi() {
        super.hideGifLoadingByUi(mRootView);
    }

    private void initFragmentGroup() {
        mViewPager = (ViewPager) findViewById(R.id.activity_fonts_content_fl);
        myFontsFragment = new MyFontsFragment();
        List<BaseFragment> mList = new ArrayList<BaseFragment>();
        mList.add(myFontsFragment);
        FontPagerAdapter mAdapter = new FontPagerAdapter(getSupportFragmentManager(), mList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(PAGE_MY_FONT);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mCurrentPage = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            default:
                break;
        }
    }

    @Override
    protected void onDestroyImpl() {
        myFontsFragment = null;
    }

    public void showGifLoadingByUi() {
        showGifLoadingByUi(mRootView, 0);
    }

    @Override
    protected void onStatisticsResume() {
        // 添加友盟统计
//        UmengStatistics.onResume(this);
    }

    @Override
    protected void onStatisticsPause() {
        // 添加友盟统计
//        UmengStatistics.onPause(this);
    }

    @Override
    public boolean isTransparentSystemBar() {
        return false;
    }
}
