package com.dangdang.reader.service;

import java.io.File;

import android.os.Environment;

import com.dangdang.zframework.network.download.Download;
import com.dangdang.zframework.network.download.DownloadManagerFactory.DownloadModule;

public class ApkDownload extends Download {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long size = 0;
	private String url = "http://192.168.1.105/apk/DDHearMe.apk";
	private String local = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.apk";
	private DownloadModule mModule;
	private boolean isShow = false;
	
	public ApkDownload(DownloadModule module){
		mModule = module;
	}

	public void setShow(boolean bo){
		isShow = bo;
	}
	
	public boolean isShow(){
		return isShow;
	}
	
	public void setFileSize(long size){
		this.size = size;
	}
	
	public void setUrl(String s){
		url = s;
	}
	
	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public long getStartPosition() {
		File f = new File(local);
		if(f.exists() && f.isFile())
			return f.length();
		
		return 0;
	}

	@Override
	public long getTotalSize() {
		return size;
	}

	public void setLocalFile(String s){
		local = s;
	}
	
	@Override
	public File getLoaclFile() {
		return new File(local);
	}

	@Override
	public DownloadModule getDownloadModule() {
		// TODO Auto-generated method stub
		return mModule;
	}
	
	@Override
	public boolean addPublicParams() {
		return false;
	}
}
