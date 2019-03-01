package com.dangdang.reader.dread;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


import com.dangdang.reader.R;
import com.dangdang.reader.dread.core.epub.InteractiveBlockImageView;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.format.BaseBookManager;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.jni.DrawInteractiveBlockHandler;
import com.dangdang.zframework.BaseActivity;


public class InteractiveBlockViewActivity extends BaseActivity {

	public static final String mStrBlockIndex = "blockIndex";
	public static final String mStrPageIndex = "pageIndex";
	public static final String mStrChapter = "chapter";
	public static final int REQUEST_CODE = 100;
	
	private ReaderAppImpl mReaderApps;

	@Override
	protected void onCreateImpl(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.read_interactiveblock_activity);
		
		Intent intent = this.getIntent();
		int nBlockIndex = intent.getIntExtra(mStrBlockIndex, 0);
		int nPageIndex = intent.getIntExtra(mStrPageIndex, 0);
		Chapter chapter = (Chapter)intent.getSerializableExtra(mStrChapter);
		DrawInteractiveBlockHandler handler = new DrawInteractiveBlockHandler();
		int nWidth = 0;
		int nHeight = 0;
		
		mReaderApps = ReaderAppImpl.getApp();
		BaseBookManager bookManager = (BaseBookManager)mReaderApps.getBookManager();
		bookManager.DrawInteractiveBlock(chapter, nPageIndex, nBlockIndex, nWidth, nHeight, handler);
		
//		GestrueControlGalleryView imageViewBmp = (GestrueControlGalleryView)findViewById(R.id.interactiveBlockImageView);
//		InteractiveBlockViewAdapter adapter = new InteractiveBlockViewAdapter(this);
//		adapter.setBmp(handler.getBitmap());
//		imageViewBmp.setAdapter(adapter);
//		imageViewBmp.setGallerySize(handler.getRenderWidth(), handler.getRenderHeight());
//		GallaryData gallaryData = new GallaryData();
//		gallaryData.setImageRect(new Rect(0, 0, handler.getRenderWidth(), handler.getRenderHeight()));
//		imageViewBmp.setGalleryData(gallaryData);
		InteractiveBlockImageView imageViewBmp = (InteractiveBlockImageView)findViewById(R.id.interactiveBlockImageView);
		imageViewBmp.Init();

		imageViewBmp.setImageBitmap(handler.getBitmap());
		imageViewBmp.setImageWidth(handler.getRenderWidth());
		imageViewBmp.setImageHeight(handler.getRenderHeight());
		imageViewBmp.invalidate();
		imageViewBmp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
//				overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			}
		});
		
		setResult(REQUEST_CODE);
//		ScrollView vScrollView = new ScrollView(this);
//		HorizontalScrollView hScrollView = new HorizontalScrollView(this);
//		
//		ImageView imageView = new ImageView(this);
//		imageView.setImageBitmap(handler.getBitmap());
//		LayoutParams params = new LayoutParams(handler.getRenderWidth(), handler.getRenderHeight());
//		imageView.setLayoutParams(params);
//		imageView.setBackgroundColor(0x00ffffff);
//		imageView.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				finish();
//				overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
//			}
//		});
//		
//		LayoutParams paramsHScroll = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//		hScrollView.setLayoutParams(paramsHScroll);
//		hScrollView.addView(imageView);
//		hScrollView.setBackgroundColor(0x00ffffff);
//		
//		LayoutParams paramsVScroll = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//		vScrollView.setLayoutParams(paramsVScroll);
//		vScrollView.addView(hScrollView);
//		vScrollView.setBackgroundColor(0xf0ffffff);
		
//		setContentView(vScrollView);
	}

	@Override
	protected void onDestroyImpl() {
		// TODO Auto-generated method stub
		
	}
}
