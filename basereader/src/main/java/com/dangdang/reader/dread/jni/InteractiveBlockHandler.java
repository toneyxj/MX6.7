package com.dangdang.reader.dread.jni;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;

public class InteractiveBlockHandler {

	int mCount;
	

	enum InteractiveBlockType {
		type_code,
		type_table,
	}
	
	public int getInteractiveBlockCount() {
		return mCount;
	}

	public void setInteractiveBlockCount(int mCount) {
		this.mCount = mCount;
	}

	public class InteractiveBlock {
		private Rect mIconRect;
		
		private int mIndex;
		private InteractiveBlockType mType;
		
		public Rect getIconRect() {
			return mIconRect;
		}
		public void setIconRect(Rect rectIcon) {
			this.mIconRect = rectIcon;
		}
		
		public int getIndex() {
			return mIndex;
		}
		public void setIndex(int mIndex) {
			this.mIndex = mIndex;
		}
		public InteractiveBlockType getType() {
			return mType;
		}
		public void setType(InteractiveBlockType mType) {
			this.mType = mType;
		}
		
	}
	
	public void setInteractiveBlockInfo(int left, int top, int right, int bottom, int nType, int nIndex) {
		InteractiveBlock interactiveBlock = new InteractiveBlock();
		Rect rectIcon = new Rect();
		rectIcon.set(left, top, right, bottom);
		if (nType == 1)
			interactiveBlock.setType(InteractiveBlockType.type_table);
		else if (nType == 2)
			interactiveBlock.setType(InteractiveBlockType.type_code);
		interactiveBlock.setIndex(nIndex);
		interactiveBlock.setIconRect(rectIcon);
		mInteractiveBlockList.add(interactiveBlock);
	}
	
	List<InteractiveBlock> mInteractiveBlockList = new ArrayList<InteractiveBlock>();
	
	public List<InteractiveBlock> getInteractiveBlockList() {
		return mInteractiveBlockList;
	}
}
