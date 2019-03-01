package com.dangdang.reader.dread.config;

public class PagePadding {
	
	
	private float paddingLeft;
	private float paddingTop;
	private float paddingRight;
	private float paddingBottom;
	
	public float getPaddingLeft() {
		return paddingLeft;
	}
	public void setPaddingLeft(float paddingLeft) {
		this.paddingLeft = paddingLeft;
	}
	public float getPaddingTop() {
		return paddingTop;
	}
	public void setPaddingTop(float paddingTop) {
		this.paddingTop = paddingTop;
	}
	public float getPaddingRight() {
		return paddingRight;
	}
	public void setPaddingRight(float paddingRight) {
		this.paddingRight = paddingRight;
	}
	public float getPaddingBottom() {
		return paddingBottom;
	}
	public void setPaddingBottom(float paddingBottom) {
		this.paddingBottom = paddingBottom;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof PagePadding)){
			return false;
		}
		PagePadding other = (PagePadding) o;
		return paddingTop == other.getPaddingTop() 
				&& paddingRight == other.getPaddingRight() 
				&& paddingBottom == other.getPaddingBottom() 
				&& paddingLeft == other.getPaddingLeft();
	}
	
}
