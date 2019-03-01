package com.dangdang.reader.dread.data;


public class NoteKey {
	
	private String productId;
	private String bookModVersion;
	private int chapterIndex;
	private int startElementIndex;
	private int endElementIndex;
	
	public NoteKey(){
	}
	
	public NoteKey(String productId, String bookModVersion, int chapterIndex, int startElementIndex,
			int endElementIndex) {
		super();
		this.productId = productId;
		this.bookModVersion = bookModVersion;
		this.chapterIndex = chapterIndex;
		this.startElementIndex = startElementIndex;
		this.endElementIndex = endElementIndex;
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

	public int getStartElementIndex() {
		return startElementIndex;
	}

	public void setStartElementIndex(int startElementIndex) {
		this.startElementIndex = startElementIndex;
	}

	public int getEndElementIndex() {
		return endElementIndex;
	}

	public void setEndElementIndex(int endElementIndex) {
		this.endElementIndex = endElementIndex;
	}

	public static NoteKey convert(BookNote bookNote){
		
		final String productId = bookNote.getBookId();
		final String bookModVersion = bookNote.getBookModVersion();
		final int chapterIndex = bookNote.getChapterIndex();
		final int startElementIndex = bookNote.getNoteStart();
		final int endElementIndex = bookNote.getNoteEnd();
		
		return new NoteKey(productId, bookModVersion, chapterIndex, startElementIndex, endElementIndex);
	}
	
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof NoteKey)){
			return false;
		}
		NoteKey other = (NoteKey) o;
		return other.productId.equals(productId) 
				&& (other.bookModVersion == null || other.bookModVersion.equals(bookModVersion))
				&& other.chapterIndex == chapterIndex 
				&& other.startElementIndex == startElementIndex 
				&& other.endElementIndex == endElementIndex;
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
		sb.append(startElementIndex);
		sb.append("-");
		sb.append(endElementIndex);
		sb.append("]");
		
		return sb.toString().hashCode();
	}
	
}