package com.dangdang.reader.dread.core.epub;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.ImageView;

import com.dangdang.reader.dread.InteractiveBlockViewActivity;
import com.dangdang.reader.dread.ReadActivity;
import com.dangdang.reader.dread.format.Chapter;

public class InteractiveBlockIconView extends ImageView {

	private Chapter chapter;
	private int pageIndex;
	private int interactiveBlockIndex;
	private Rect rect;
	
	public InteractiveBlockIconView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	
	private void init() {
//		this.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				clearAnimation();
//				setVisibility(View.GONE);
//				invalidate();
//				
//				final Intent intent = new Intent();
//				intent.setClass(getContext(), InteractiveBlockViewActivity.class);
//				intent.putExtra(InteractiveBlockViewActivity.mStrBlockIndex, interactiveBlockIndex);
//				intent.putExtra(InteractiveBlockViewActivity.mStrPageIndex, pageIndex);
//				Bundle bundle = new Bundle();
//				bundle.putSerializable(InteractiveBlockViewActivity.mStrChapter, chapter);
//				intent.putExtras(bundle);
//				((ReadActivity)getContext()).startActivityForResult(intent, InteractiveBlockViewActivity.REQUEST_CODE);
//				((ReadActivity)getContext()).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
//			}
//		});
	}
	
	public void StartInteractiveBlockViewActivity() {
		
		final Intent intent = new Intent();
		intent.setClass(getContext(), InteractiveBlockViewActivity.class);
		intent.putExtra(InteractiveBlockViewActivity.mStrBlockIndex, interactiveBlockIndex);
		intent.putExtra(InteractiveBlockViewActivity.mStrPageIndex, pageIndex);
		Bundle bundle = new Bundle();
		bundle.putSerializable(InteractiveBlockViewActivity.mStrChapter, chapter);
		intent.putExtras(bundle);
		((ReadActivity)getContext()).startActivityForResult(intent, InteractiveBlockViewActivity.REQUEST_CODE);
//		((ReadActivity)getContext()).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
	}

	public Chapter getChapter() {
		return chapter;
	}

	public void setChapter(Chapter chapter) {
		this.chapter = chapter;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getInteractiveBlockIndex() {
		return interactiveBlockIndex;
	}

	public void setInteractiveBlockIndex(int interactiveBlockIndex) {
		this.interactiveBlockIndex = interactiveBlockIndex;
	}

	public Rect getRect() {
		return rect;
	}

	public void setRect(Rect rect) {
		this.rect = rect;
	}

}
