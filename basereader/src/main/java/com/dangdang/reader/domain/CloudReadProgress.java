package com.dangdang.reader.domain;

public class CloudReadProgress {

	private String productId;
	private int chapterIndex;
	private int elementIndex;
	private long clientOperateTime;
	private long startTime;
	private long endTime;
	
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public int getChapterIndex() {
		return chapterIndex;
	}
	public void setChapterIndex(int chapterIndex) {
		this.chapterIndex = chapterIndex;
	}
	public int getElementIndex() {
		return elementIndex;
	}
	public void setElementIndex(int elementIndex) {
		this.elementIndex = elementIndex;
	}
	public long getClientOperateTime() {
		return clientOperateTime;
	}
	public void setClientOperateTime(long clientOperateTime) {
		this.clientOperateTime = clientOperateTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}
