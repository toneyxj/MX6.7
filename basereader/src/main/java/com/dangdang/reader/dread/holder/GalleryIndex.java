package com.dangdang.reader.dread.holder;

/**
 * @author luxu
 */
public class GalleryIndex {

	private int gallery;
	private int frameIndex;

	public int getGallery() {
		return gallery;
	}

	public void setGallery(int gallery) {
		this.gallery = gallery;
	}

	public int getFrameIndex() {
		return frameIndex;
	}

	public void setFrameIndex(int frameIndex) {
		this.frameIndex = frameIndex;
	}

	@Override
	public String toString() {
		return "[gallery=" + gallery + ",frameIndex=" + frameIndex + "]";
	}
	
}
