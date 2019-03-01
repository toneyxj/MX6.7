package com.dangdang.reader.dread.jni;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.dangdang.reader.dread.format.PageInfo;
import com.dangdang.zframework.log.LogM;

public class ChaterInfoHandler implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<PageInfo> pageInfos = new ArrayList<PageInfo>();
	private List<ALabelInfo> labelInfos = new ArrayList<ALabelInfo>(1);
	private int weightedTextCount = 0;
	
	public void setPageRange(int start, int end){
	
		PageInfo pInfo = new PageInfo();
		pInfo.setStartIndex(start);
		pInfo.setEndIndex(end);
		
		//printLog(" setPageRange " + pInfo);
		
		pageInfos.add(pInfo);
	}

	public List<PageInfo> getPageInfos(){
		return pageInfos;
	}
	
	public List<ALabelInfo> getLabelInfos() {
		return labelInfos;
	}

	public int getWeightedTextCount() {
		return weightedTextCount;
	}

	public void setWeightedTextCount(int weightedTextCount) {
		this.weightedTextCount = weightedTextCount;
	}

	/**
	 * @param pageIndexInChapter 从1开始，对应到实际List需要减1
	 * @return
	 */
	public PageInfo getPageInfo(int pageIndexInChapter){
		
		int position = pageIndexInChapter - 1;
		PageInfo pInfo = null;
		try {
			if(position < pageInfos.size()){
				pInfo = pageInfos.get(position);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pInfo;
	}
	
	public void setAnchorInfo(int start, int end, int pageIndex, String anchor){
		
		ALabelInfo aInfo = new ALabelInfo();
		//aInfo.setStartIndex(start);
		//aInfo.setEndIndex(end);
		aInfo.setPageIndex(pageIndex);
		aInfo.setAnchor(anchor);
		
		labelInfos.add(aInfo);
		
		//printLog(" setAnchorInfo " + start + "-" + end + "," + pageIndex + "," + anchor);
	}
	
	public int getPageIndexByAnchor(String anchor){
		int page = 0;
		for(int i = 0,len = labelInfos.size(); i < len; i++){
			ALabelInfo aInfo = labelInfos.get(i);
			if(aInfo.isSame(anchor)){
				page = aInfo.getPageIndex();
				break;
			}
		}
		return page;
	}
	
	public int getPageIndexByElementIndex(int elementIndex){
		int page = 0;
		for(int i = 0, len = pageInfos.size(); i < len; i++){
			PageInfo pInfo = pageInfos.get(i);
			if(pInfo.hasContain(elementIndex)){
				page = i;
				break;
			}
		}
		return page;
	}
	
	public int getFirstElementIndex(){
		int firstIndex = 0;
		if(pageInfos.size() > 0){
			try {
				firstIndex = pageInfos.get(0).getStartIndex();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return firstIndex;
	}
	
	public int getLastElementIndex(){
		int lastIndex = 0;
		int size = pageInfos.size();
		if(size > 0){
			try {
				lastIndex = pageInfos.get(size - 1).getEndIndex();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return lastIndex;
	}
	
	public void reset(){
		pageInfos.clear();
		labelInfos.clear();
	}
	
	protected void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
}
