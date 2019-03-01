package com.dangdang.reader.dread.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dangdang.reader.dread.fragment.BaseReadFragment;
import com.dangdang.reader.dread.fragment.DmnDirFragment;
import com.dangdang.reader.dread.fragment.DmnMarkFragment;
import com.dangdang.reader.dread.fragment.DmnNoteFragment;
import com.dangdang.zframework.log.LogM;

public class DmnFragmentPagerAdapter extends FragmentPagerAdapter {

	private DmnDirFragment mDirFragment;
	private DmnMarkFragment mMarkFragment;
	private DmnNoteFragment mNoteFragment;
	private String mBookName;
	private int mCount = 2;//只保留了目录

	public DmnFragmentPagerAdapter(FragmentManager fm, String bookName) {
		super(fm);
		mBookName = bookName;
	}

	@Override
	public BaseReadFragment getItem(int position) {
		BaseReadFragment ret = mDirFragment;
		switch (position) {
		case 0:
			if (mDirFragment == null) {
				mDirFragment = new DmnDirFragment();
				mDirFragment.setBookName(mBookName);
			}
			mDirFragment.setBookName();
			ret = mDirFragment;
			break;
		case 1:
//			if (mMarkFragment == null) {
//				mMarkFragment = new DmnMarkFragment();
//				/*
//				 * mMarkFragment.SetOnResultListener(new OnResultListener() {
//				 *
//				 * @Override public void OnBackResult(boolean isSucess) { if
//				 * (isSucess && !mClickEntryMark) { mCurrentModule = NOTE;
//				 * setSelection(); loadChildModule(mCurrentModule); } } });
//				 */
//			}
//			ret = mMarkFragment;
//			break;
//		case 2:
			if (mNoteFragment == null) {
				mNoteFragment = new DmnNoteFragment();
			}
			ret = mNoteFragment;
			break;
		default:
			break;
		}
		return ret;
	}

	@Override
	public int getCount() {
		return mCount;
	}

	public void setCount(int c) {
		mCount = c;
	}

	public void reLoad(int index) {
		if (index < getCount()) {
			BaseReadFragment frag = getItem(index);
			if (frag != null) {
				frag.reload();
			}
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	public DmnNoteFragment getNoteFragment() {
		return mNoteFragment;
	}

	public DmnDirFragment getDirFragment() {
		return mDirFragment;
	}

	public void setAllNeedReload() {
		int m = getCount();
		for (int i = 0; i < m; ++i) {
			getItem(i).setNeedReload(true);
		}
	}

	public void reloadAll() {
		int m = getCount();
		for (int i = 0; i < m; ++i) {
			BaseReadFragment readFrgt = getItem(i);
			if (readFrgt.getBaseReadActivity() == null) {
				LogM.e(getClass().getSimpleName(), " dmn activity is null ");
				continue;
			}
			readFrgt.reload();
		}
	}

	public String getBookNoteExportContent() {
		if (mNoteFragment != null)
			return mNoteFragment.getBookNoteExportContent();
		return null;
	}

	public int getBookNoteCount() {
		if (mNoteFragment != null)
			return  mNoteFragment.getBookNoteCount();
		return 0;
	}
}
