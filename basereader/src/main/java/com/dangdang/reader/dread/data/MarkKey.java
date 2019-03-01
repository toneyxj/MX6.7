package com.dangdang.reader.dread.data;


public class MarkKey {
	
	private String productId;
	private String bookModVersion;
	private int chapterIndex;
	private int elementIndex;
	
	public MarkKey(){
		
	}
	
	public MarkKey(String productId, String bookModVersion, int chapterIndex, int elementIndex) {
		super();
		this.productId = productId;
		this.bookModVersion = bookModVersion;
		this.chapterIndex = chapterIndex;
		this.elementIndex = elementIndex;
	}
	
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getBookModVersion() {
		return bookModVersion;
	}

	public void setBookModVersion(String bookModVersion) {
		this.bookModVersion = bookModVersion;
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

	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof MarkKey)){
			return false;
		}
		MarkKey other = (MarkKey) o;
		return other.productId.equals(productId)
				&& (other.bookModVersion == null || other.bookModVersion.equals(bookModVersion))
				&& other.chapterIndex == chapterIndex
				&& other.elementIndex == elementIndex;
	}
	
	@Override
	public int hashCode() {
		StringBuffer sb = new StringBuffer();
		sb.append(productId);
		sb.append("-");
		sb.append(bookModVersion);
		sb.append("-");
		sb.append(chapterIndex);
		sb.append("[");
		sb.append(elementIndex);
		sb.append("]");
		return sb.toString().hashCode();
	}
}