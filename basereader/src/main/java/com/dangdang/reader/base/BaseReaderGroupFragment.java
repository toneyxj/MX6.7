package com.dangdang.reader.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dangdang.reader.R;
import com.dangdang.zframework.BaseFragment;

import java.util.List;

/**
 * 自定义GroupFragment，内置了ViewPager
 *
 * @author xiaruri
 */
public class BaseReaderGroupFragment extends BaseReaderFragment {

    /**
     * view
     */
    private GroupFragmentPageAdapter mAdapter;
    private ViewPager mPager;
    private OnGroupFragmentPageChangeListener myPageChangeListener;
    private int mDefaultIndex=0;
    /**
     * data
     */
    private List<BaseFragment> mFragmentList;
    private boolean isExternal = false;

    public BaseReaderGroupFragment() {
    }

    public View onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_fragment_group, null);

        mPager = (ViewPager) view.findViewById(R.id.view_pager);
        mAdapter = new GroupFragmentPageAdapter(this.getChildFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(mPageChangeListener);
        if (mDefaultIndex<mAdapter.getCount())
        mPager.setCurrentItem(mDefaultIndex);
        return view;
    }

    @Override
    public void onReady() {
    }

    @Override
    public void onDestroyImpl() {
        if (mFragmentList != null) {
            mFragmentList.clear();
            mFragmentList = null;
        }
    }

    /**
     * 设置Fragment列表
     *
     * @param fragmentList 不能为空，否则会抛异常
     */
    public void setFragmentList(List<BaseFragment> fragmentList) {
        if (fragmentList == null) {
            throw new NullPointerException(" fList is null");
        }
        mFragmentList = fragmentList;
    }

    public ViewPager getPager() {
        return mPager;
    }

    public ViewPager getViewPager() {
        return mPager;
    }

    /**
     * 返回ViewPager当前显示的页码，如果ViewPager为空则返回-1
     *
     * @return
     */
    public int getCurrentItem() {
        if (mPager == null) {
            return -1;
        }
        return mPager.getCurrentItem();
    }

    /**
     * 获取当前显示的Fragment，如果ViewPager为空则返回null
     *
     * @return
     */
    public Fragment getCurrentFragment() {
        int index = getCurrentItem();
        if (index == -1)
            return null;
        return mFragmentList.get(index);
    }

    final OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {

        private final float MIN_POSITION_OFFSET = 0.1f;
        private int currentSelectPosition = 0;
        private int state = ViewPager.SCROLL_STATE_IDLE;
        private boolean isMinPositionOffset = false;

        @Override
        public void onPageSelected(int position) {
            currentSelectPosition = position;
            notifyScrollPrepare(position);
            if (myPageChangeListener != null) {
                myPageChangeListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                if (position >= currentSelectPosition) {
                    if (positionOffset >= MIN_POSITION_OFFSET) {
                        isMinPositionOffset = true;
                    }
                } else {
                    if ((1 - positionOffset) >= MIN_POSITION_OFFSET) {
                        isMinPositionOffset = true;
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            this.state = state;
            if (state == ViewPager.SCROLL_STATE_IDLE
                    && (isMinPositionOffset || isExternal)) {
                isMinPositionOffset = false;
                isExternal = false;
                notifyScrollEnd(currentSelectPosition);
            }
        }
    };

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 设置viewpager的页码，从0开始
     *
     * @param page
     */
    public void setSelection(int page) {
        if (mPager == null) {
            return;
        }
        if (page < 0 || page > mFragmentList.size()) {
            throw new IllegalArgumentException(
                    "[  index < 0 || index > mModuleList.size() ]");
        }
        isExternal = true;
        int current = mPager.getCurrentItem();
        if (page == current)
            return;
        boolean isScroll = Math.abs(current - page) == 1;//如果当前和目标查一个位置，则滑动切换，否则直接切换
        mPager.setCurrentItem(page, isScroll);
    }

    public void setDefaultIndex(int mCurrentPage) {
        mDefaultIndex=mCurrentPage;
    }

    public abstract static class FragmentPagerAdapter extends PagerAdapter {

        private final FragmentManager mFragmentManager;
        private FragmentTransaction mCurTransaction = null;
        private Fragment mCurrentPrimaryItem = null;

        public FragmentPagerAdapter(FragmentManager fm) {
            mFragmentManager = fm;
        }

        public abstract Fragment getItem(int position);

        @Override
        public void startUpdate(ViewGroup container) {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            final long itemId = getItemId(position);

            String name = makeFragmentName(container.getId(), itemId);
            Fragment fragment = mFragmentManager.findFragmentByTag(name);
            if (fragment != null) {
                mCurTransaction.attach(fragment);
            } else {
                fragment = getItem(position);
                mCurTransaction.add(container.getId(), fragment,
                        makeFragmentName(container.getId(), itemId));
            }
            if (fragment != mCurrentPrimaryItem) {
                fragment.setMenuVisibility(false);
                fragment.setUserVisibleHint(false);
            }

            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            Fragment fragment = (Fragment) object;
            if (fragment != mCurrentPrimaryItem) {
                if (mCurrentPrimaryItem != null) {
                    mCurrentPrimaryItem.setMenuVisibility(false);
                    mCurrentPrimaryItem.setUserVisibleHint(false);
                }
                if (fragment != null) {
                    fragment.setMenuVisibility(true);
                    fragment.setUserVisibleHint(true);
                }
                mCurrentPrimaryItem = fragment;
            }
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            if (mCurTransaction != null) {
                try {
                    mCurTransaction.commitAllowingStateLoss();
                    mCurTransaction = null;
                    mFragmentManager.executePendingTransactions();
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return ((Fragment) object).getView() == view;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        public long getItemId(int position) {
            return position;
        }

        private static String makeFragmentName(int viewId, long id) {
            return "android:switcher:" + viewId + ":" + id;
        }
    }

    public class GroupFragmentPageAdapter extends FragmentPagerAdapter {
        public GroupFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            if (mFragmentList == null)
                return 0;
            else
                return mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position < mFragmentList.size()) {
                fragment = mFragmentList.get(position);
            } else {
                fragment = mFragmentList.get(0);
            }

            return fragment;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
        }

    }

    private void notifyScrollEnd(int position) {
        if (mFragmentList.get(position) instanceof BaseReaderHtmlFragment) {
            OnScrollListener scrollListener = (BaseReaderFragment) mFragmentList
                    .get(position);
            if (scrollListener != null) {
                scrollListener.onScrollEnd();
            }
        }
    }

    private void notifyScrollPrepare(int position) {
        if (mFragmentList.get(position) instanceof BaseReaderHtmlFragment) {
            OnScrollListener scrollListener = (BaseReaderFragment) mFragmentList
                    .get(position);
            if (scrollListener != null) {
                scrollListener.onScrollPrepare();
            }
        }
    }

    public void setPageChangeListener(OnGroupFragmentPageChangeListener l) {
        myPageChangeListener = l;
    }

    public interface OnGroupFragmentPageChangeListener {
        public void onPageSelected(int position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
