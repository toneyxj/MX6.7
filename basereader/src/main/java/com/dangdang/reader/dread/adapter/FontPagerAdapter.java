package com.dangdang.reader.dread.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dangdang.zframework.BaseFragment;

import java.util.List;

public class FontPagerAdapter extends FragmentPagerAdapter {

    private int mCount = 1;

    private List<BaseFragment> mList;

    public FontPagerAdapter(FragmentManager fm, List<BaseFragment> list) {
        super(fm);
        this.mList = list;
    }

    @Override
    public BaseFragment getItem(int position) {
        BaseFragment mCurrentFragment = mList.get(position);
        return mCurrentFragment;
    }

    @Override
    public int getCount() {
        return mCount;
    }
}