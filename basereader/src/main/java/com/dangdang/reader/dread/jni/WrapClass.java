package com.dangdang.reader.dread.jni;


import android.content.Context;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ComposingFactor;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.utils.DangdangFileManager;
import com.mx.mxbase.constant.APPLog;

import java.io.File;
import java.io.InputStream;


public class WrapClass {
	
	//private	Paint mPaint = new Paint();
	private ComposingFactor mComposFactor;
	private String mDefaultFontName;
	private String mDefaultFontPath;
	private String mCssPath;
	private String mXdbPath;
	private String mRulesPath;
	private String mHyphenPath;
	
	private int mBgColor;
	private int mForeColor;
	
	private int mDefaultLineWordNum;
	
	private boolean mIsPadScreenLarge;
	
	public WrapClass(ReadConfig config, Context context){
		init(config, context);
	}	
	
	public void init(ReadConfig config, Context context){
		mComposFactor = config.getComposingFactor(context);
		mDefaultFontPath = config.getFontPath();
		mDefaultFontName = config.getFontName();
		mCssPath = config.getCssPath();
		mXdbPath = config.getDictXdbPath();//preread/dicts/dictgbk.xdb
		mRulesPath = config.getDictRulesPath();//preread/dicts/rules.ini
		copyfileToSdcard(context,mXdbPath, R.raw.dictgbk);
		copyfileToSdcard(context,mRulesPath,R.raw.rules);
		APPLog.e("WrapClass-mXdbPath",mXdbPath);
		APPLog.e("WrapClass-mRulesPath",mRulesPath);
		mHyphenPath = config.getPreReadPath();
		mBgColor = config.getReaderBgColor();
		mForeColor = config.getReaderForeColor();
		mDefaultLineWordNum = config.getDefaultLineWord();
		mIsPadScreenLarge = config.isPadScreenIsLarge();
	}
	private void copyfileToSdcard(Context context,String flie,int raw){
		final File filePath = new File(flie);
		if (filePath.exists())return;
		File part=filePath.getParentFile();
		if (!part.exists()){
			part.mkdirs();
		}
		final InputStream is=context.getResources().openRawResource(raw);
		new Thread(){
			@Override
			public void run() {
//				super.run();
				DangdangFileManager.writeStringToFile(is,filePath);
			}
		}.start();

	}
	
	/*public float getCharWidth(char text,float size){
		char[] tmp = {text};
		mPaint.setTextSize(size);
		return mPaint.measureText(tmp,0,tmp.length);
	}*/
	
	public int getScreenWidth(){
		return mComposFactor.getWidth();
	}
	
	public int getScreenHeight(){
		return mComposFactor.getHeight();
	}
	
	public float getMarginTop(){	
		return mComposFactor.getPaddingTop();		
	}
	
	public float getMarginBottom(){		
		return mComposFactor.getPaddingBottom();
	}
	
	public int getLineWordNum(){	
		return mComposFactor.getLineWord();		
	}
	
	public int getDefaultLineWordNum(){	
		return mDefaultLineWordNum;		
	}

	public float getMarginLeft(){		
		return  mComposFactor.getPaddingLeft();
	}
	
	public String getCssPath() {
		return mCssPath;
	}
	
	public String getDictPath(){
		return mXdbPath;
	}
	
	public String getRulePath(){
		return mRulesPath;
	}
	
	public String getHyphenPath(){
		return mHyphenPath;		
	}
	
	public String getDefaultFontPath(){
		return mDefaultFontPath;
	}
	
	public String getDefaultFontName(){
		return mDefaultFontName;
	}
	
	public int getBgColor() {
		return mBgColor;
	}

	public int getForeColor() {
		return mForeColor;
	}

	// 页面的行间距、段间距的缩放因子
	public float getPageFactor(){
		return mComposFactor.getLineSpacing();
	}
	
	public class FontFamily{
		public String fontName;
		public String fontPath;
		public String charset;
	}

	
	public FontFamily[] getFontFamilyArray(){
		FontFamily [] familyArray = new FontFamily[1];
		for(int i = 0; i < familyArray.length; i++)
		{
			FontFamily family = new FontFamily();
			family.charset = "DD_CHARSET_DEFAULT";
			family.fontName = "bold";
			family.fontPath = mDefaultFontPath;
			familyArray[i] = family;
		}
		return familyArray;
	}

	public boolean isPadScreenIsLarge() {
		return mIsPadScreenLarge;
	}
}


