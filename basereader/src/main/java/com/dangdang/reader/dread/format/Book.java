package com.dangdang.reader.dread.format;

import com.dangdang.reader.dread.util.DreaderConstants;
import com.dangdang.zframework.log.LogM;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Book implements IBook {

	/**
	 * 试读最后一页标识
	 */
	public final static String PAYTIP = "paytip";
	
	protected long mfileSize = 0;
	protected boolean mTheSameFile = true;
	protected String mVersion;
	protected String mModVersion = DreaderConstants.BOOK_MODIFY_VERSION;
	
	protected String mBookPath;
	protected String mBookName;
	protected String mDecipherKey;// ?
	/**
	 * 本书排版后总页数
	 */
	protected int mPageCount;
	
	/**
	 * 每个Chapter(章节) startPage - endPage
	 */
	protected Map<String, Chapter> mChapterMap;
	/**
	 * 解析container.xml中配置的所有文件
	protected Map<String, ResourceFile> mFileMap;
	*/
	
	/**
	 *所有章节Chapter
	 */
	protected List<Chapter> mChapterList;
	/**
	 * 目录列表
	 */
	protected List<BaseNavPoint> mNavPointList;
	
	
	public Book(){
		mChapterMap = new HashMap<String, Chapter>();
		//mFileMap = new HashMap<String, ResourceFile>();
	}
	
	public void setPageCount(int pageCount) {
		this.mPageCount = pageCount;
	}
	
	@Override
	public int getPageCount() {
		return mPageCount;
	}

	public abstract BaseNavPoint getNavPoint(Chapter chapter);
	
	/*public BaseNavPoint getNavPoint(int chapterIndex){
		Chapter chapter = getChapter(chapterIndex);
		return getNavPoint(chapter);
	}*/
	
	
	public BaseNavPoint getNavPoint(Chapter chapter, int pageIndexInChapter){
		
		BaseNavPoint navPoint = getNavPoint(chapter);
		if(navPoint != null){
			BaseNavPoint subPoint = findSubNavPoint(pageIndexInChapter, navPoint);
			if(subPoint != null){
				navPoint = subPoint;
			}
		}
		return navPoint;
	}
	
	public String getChapterName(int chapterIndex){
		String chapterName = "";
		Chapter chapter = getChapter(chapterIndex);
		if(chapter != null){
			chapterName = getChapterName(chapter);
		}
		return chapterName;
	}
	
	public String getChapterName(Chapter chapter){
		String chapterName = "";
		if(chapter != null){
			BaseNavPoint baseNp = getNavPoint(chapter);
			if(baseNp != null){
				chapterName = baseNp.lableText;
			}
		}
		return chapterName;
	}
	
	public BaseNavPoint getNavPoint(int pageIndexInBook){
		
		BaseNavPoint np = null;
		Chapter html = getChapterByPageIndex(pageIndexInBook);
		if(html != null){
			np = getNavPoint(html);
			if(np != null){
				BaseNavPoint childNp = findSubNavPoint(pageIndexInBook, np, html);
				if(childNp != null){
					np = childNp;
				}
			}
		}
		if(np == null){
			LogM.e(getClass().getSimpleName(), " getNavPoint == null index=" + pageIndexInBook);
		}
		return np;
	}

	protected BaseNavPoint findSubNavPoint(int pageIndexInBook, BaseNavPoint parent, Chapter html) {
		List<BaseNavPoint> subPs = parent.getSubNavPs();
		if(subPs == null || subPs.size() == 0){
			return null;
		}
		BaseNavPoint subPoint = null;
		int startIndex = html.getStartIndexInBook();
		for(int i = 0,len = subPs.size(); i < len; i++){
			
			BaseNavPoint tmpSubPoint = subPs.get(i);
			int tmpEndIndexInBook = startIndex + tmpSubPoint.getPageIndex();
			int nexti = i + 1;
			if(nexti < len){
				tmpEndIndexInBook = startIndex + (subPs.get(nexti).getPageIndex() - 1);
			} 
			
			if(pageIndexInBook > startIndex && pageIndexInBook < tmpEndIndexInBook){
				subPoint = tmpSubPoint;
				break;
			}
		}
		return subPoint;
	}
	
	protected BaseNavPoint findSubNavPoint(int pageIndexInChapter, BaseNavPoint parent){
		if(parent == null){
			return null;
		}
		List<BaseNavPoint> subPs = parent.getSubNavPs();
		if(subPs == null || subPs.size() == 0){
			return null;
		}
		BaseNavPoint subPoint = null;
		for(int i = 0,len = subPs.size(); i < len; i++){
			
			BaseNavPoint tmpSubPoint = subPs.get(i);
			int tmpEndIndexInBook = tmpSubPoint.getPageIndex();
			int nexti = i + 1;
			if(nexti < len){
				tmpEndIndexInBook = subPs.get(nexti).getPageIndex();
			} 
			if(pageIndexInChapter > 1 && pageIndexInChapter <= tmpEndIndexInBook){
				subPoint = tmpSubPoint;
				break;
			}
		}
		return subPoint;
	}
	
	public void addPageRange(String chapterPath, Chapter chapter){
		
		mChapterMap.put(chapterPath, chapter);
		
	}
	
	public Chapter getPageRange(String chapterPath){
		
		Chapter chapter = null;
		if(mChapterMap.containsKey(chapterPath)){
			chapter = mChapterMap.get(chapterPath);
		}
		
		return chapter;
	}
	
	/*public String getPathByPageIndex(int pageIndex){
		
		String htmlPath = "";
		Html html = getHtmlByPageIndex(pageIndex);
		if(html != null){
			htmlPath = html.path;
		}
		
		return htmlPath;
	}*/
	
	public Chapter getChapterByPageIndex(int pageIndex){
		
		Chapter chapter = null;
		/*Set<Entry<String, Html>> entrySet = mHtmlMap.entrySet();
		for(Entry<String, Html> entry : entrySet){
			Html range = entry.getValue();
			if(isHtmlContainPageIndex(range, pageIndex)){
				html = range;
				break;
			}
		}*/
		//防止 opf文件中 itemref有重复项 
		for(Chapter ch : mChapterList){
			if(isChapterContainPageIndex(ch, pageIndex)){
				chapter = ch;
				break;
			}
		}
		return chapter;
	}
	
	public boolean isChapterContainPageIndex(Chapter chapter, int pageIndex){
		if(chapter == null){
			return false;
		}
		//return pageIndex >= html.startIndexInBook && pageIndex <= html.endIndexInBook;
		return pageIndex >= chapter.getStartIndexInBook() && pageIndex <= chapter.getEndIndexInBook();
		
	}
	
	public int chapterIndexInBook(final Chapter chapter){
		int index = 0;
		try {
			//EpubChapter html = new EpubChapter(htmlPath);
			index = mChapterList.indexOf(chapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return index;
	}
	
	public boolean isFirstChapter(final Chapter chapter){
		
		List<Chapter> chList = getChapterList();
		if(chList == null){
			return false;
		}
		if(chapter != null && chList !=null && chList.size() > 0){
			if(chapter.equals(chList.get(0))){
				return true;
			}
		}
		return false;
	}
	
	public boolean isLastChapter(final Chapter chapter){
		
		List<Chapter> chList = getChapterList();
		if(chList == null){
			return false;
		}
		final int chSize = chList.size();
		if(chapter != null && chList !=null && chSize > 0){
			if(chapter.equals(chList.get(chSize - 1))){
				return true;
			}
		}
		return false;
		
	}
	
	public String getDecipherKey() {
		return mDecipherKey;
	}

	public void setDecipherKey(String decipherKey) {
		this.mDecipherKey = decipherKey;
	}

	public String getBookPath() {
		return mBookPath;
	}
	public void setBookPath(String path){
		mBookPath=path;
	}
	public String getBookName() {
		return mBookName;
	}

	/*public Map<String, ResourceFile> getFileMap() {
		return mFileMap;
	}

	public void setFileMap(Map<String, ResourceFile> fileMap) {
		this.mFileMap = fileMap;
	}*/

	public List<BaseNavPoint> getNavPointList() {
		return mNavPointList;
	}

	public List<BaseNavPoint> getAllNavPointList() {
		return mNavPointList;
	}

	public void setNavPointList(List<BaseNavPoint> navPointList) {
		this.mNavPointList = navPointList;
	}
	
	public int getPageIndexInBookAtBeforeHtml(final Chapter chapter){
		
		//printLog(" getPageIndexInBookAtBeforeHtml " + htmlPath);
		int indexInBook = 0;
		if(chapter == null){
			return indexInBook;
		}
		final Chapter tChapter = chapter;//getHtmlByPath(chapter.getPath());
		final List<Chapter> chapters = getChapterList();
		if(chapters == null){
			return indexInBook;
		}
		int prevHtmlIndex = chapters.indexOf(tChapter) - 1;
		if(prevHtmlIndex >= 0){
			//indexInBook = chapters.get(prevHtmlIndex).endIndexInBook;
			indexInBook = chapters.get(prevHtmlIndex).getEndIndexInBook();
		}
		
		return indexInBook;
	}


	/*public Map<String, Html> getHtmlMap() {
		return htmlMap;
	}*/
	
	public Chapter getChapterByPath(String chapterPath){
		
		return mChapterMap.get(chapterPath);
	}
	
	public Chapter getChapter(int position){
		Chapter chapter = null;
		try {
			chapter = null;
			if(mChapterList != null){
				position = position < 0 ? 0 : position;
				chapter = mChapterList.get(position);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chapter;
	}
	
	public boolean hasExistsChapter(Chapter chapter){
		int index = 0;
		try {
			index = mChapterList.indexOf(chapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return index != -1;
	}
	
	/**
	 * 书最后一章
	 * @return
	 */
	public Chapter getLastChapter(){
		int lastPosition = getChapterSize() - 1;
		return getChapter(lastPosition);
	}
	
	public List<Chapter> getChapterList() {
		return mChapterList;
	}

	public void setChapterList(List<Chapter> chapterList) {
		this.mChapterList = chapterList;
	}

	public int getChapterSize(){
		int size = 0;
		if(mChapterList != null){
			size = mChapterList.size();
		}
		return size;
	}
	
	public String getVersion() {
		return mVersion;
	}

	public void setVersion(String version) {
		this.mVersion = version;
	}

	public String getModVersion() {
		return mModVersion;
	}

	public void setModVersion(String version) {
		this.mModVersion = version;
	}
	
	public long getFileSize() {
		return mfileSize;
	}
	
	public void setFileSize(long fileSize) {
		this.mfileSize = fileSize;
	}
	
	public boolean isTheSameFile() {
		return mTheSameFile;
	}

	public void setTheSameFile(boolean theSameFile) {
		this.mTheSameFile = theSameFile;
	}

	public boolean hasChapterList(){
		return mChapterList != null && mChapterList.size() > 0;
	}

	public void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}

	public void reSet(){
		mChapterMap.clear();
		mPageCount = 0;
	}
	
	public void clearAll(){
		mChapterMap.clear();
		//mFileMap.clear();
		/*if(mChapterList != null){
			mChapterList.clear();
		}*/
		if(mNavPointList != null){
			mNavPointList.clear();
		}
	}

	/*public static class Html {
		
		public int startPageNum;//in html
		public int endPageNum;//in html 
		
		public int startIndexInBook;
		public int endIndexInBook;
		
		public String path;
		
		public Html(){
			
		}
		
		public Html(String path){
			this.path = path;
		}
		
		public int getPageTotal(){
			return endPageNum;
		}
		
		public boolean isContainPageIndex(int pageIndexInHtml){
			
			if(startPageNum == 0 || endPageNum == 0){
				return false;
			}
			final int pageTotal = getPageTotal();
			return pageIndexInHtml <= pageTotal;
			
		}
		
		public void reSet(){
			startPageNum = 0;
			endPageNum = 0;
		}
		
		@Override
		public String toString() {
			
			return "Html[ " + path + " - (" + startPageNum + " - " + endPageNum +") (" + startIndexInBook + " - " + endIndexInBook +") ]";
		}
		
		@Override
		public boolean equals(Object o) {
			if(o == null){
				return false;
			}
			if(this.path == null && this.path.trim().length() == 0){
				return false;
			}
			Html other = (Html) o;
			return this.path.equals(other.path);
		}
		
	}*/
	
	public static class ResourceFile {
		
		public String dir;
		public boolean isEncrtyped;
		
		public ResourceFile(String dir, boolean isEncrtyped) {
			super();
			this.dir = dir;
			this.isEncrtyped = isEncrtyped;
		}

		public ResourceFile() {
			super();
		}
	}
	
	
	public static class BaseNavPoint implements Serializable{
		
		public String lableText;//chapter name
		public String fullSrc;
		
		/**
		 * 目录下：子章节
		 */
		public List<BaseNavPoint> subNavPs;
		
		/**
		 * 如果是子章节，那么此为它的父章节
		 */
		public BaseNavPoint parentNav;
		
		/**
		 * 相对于这一章
		 */
		private int pageIndex;
		
		public BaseNavPoint(){
			
		}

		public String getLableText() {
			return lableText;
		}

		public void setLableText(String lableText) {
			this.lableText = lableText;
		}

		public String getFullSrc() {
			return fullSrc;
		}

		public void setFullSrc(String fullSrc) {
			this.fullSrc = fullSrc;
		}

		public List<BaseNavPoint> getSubNavPs() {
			return subNavPs;
		}

		public void setSubNavPs(List<BaseNavPoint> subNavPs) {
			this.subNavPs = subNavPs;
		}

		public BaseNavPoint getParentNav() {
			return parentNav;
		}

		public void setParentNav(BaseNavPoint parentNav) {
			this.parentNav = parentNav;
		}

		public int getPageIndex() {
			return pageIndex;
		}

		public void setPageIndex(int pageIndex) {
			this.pageIndex = pageIndex;
		}
	}
	
}
