package com.dangdang.reader.dread.core.epub;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.graphics.Rect;

import com.dangdang.zframework.log.LogM;

public class NoteHolder {

	
	private static NoteHolder mInstance = null;
	private Map<PageNoteK, List<NotePicRect>> noteBmpRectMaps = new Hashtable<PageNoteK, List<NotePicRect>>();
	
	public synchronized static NoteHolder getHolder(){
		if(mInstance == null){
			mInstance = new NoteHolder();
		}
		return mInstance;
	}
	
	private NotePicRect createNoteBmpRect(NoteFlag flag, int noteWidth, int noteHeight, float noteBmpX, float noteBmpY) {
		
		Rect noteBmpRect = new Rect();
		noteBmpRect.left = (int) (noteBmpX);// - noteWidth / 2
		noteBmpRect.top = (int) (noteBmpY - noteHeight);
		noteBmpRect.right = (int) (noteBmpX + noteWidth + noteWidth / 2);
		noteBmpRect.bottom = (int) (noteBmpY + noteHeight + noteHeight);
		
		NotePicRect nRect = new NotePicRect();
		nRect.setFlag(flag);
		nRect.setRect(noteBmpRect);
		
		return nRect;
	}
	
	public void addPicRect(int chapterIndex, int pageIndexInChapter, 
			NoteFlag flag, int noteWidth, int noteHeight, float noteBmpX, float noteBmpY){
		
		NotePicRect noteRect = createNoteBmpRect(flag, noteWidth, noteHeight, noteBmpX, noteBmpY);
		addPicRect(chapterIndex, pageIndexInChapter, noteRect);
		
	}
	
	private void addPicRect(int chapterIndex, int pageIndexInChapter, NotePicRect noteRect){
		
		PageNoteK rectKey = newPageNoteKey(chapterIndex, pageIndexInChapter);
		List<NotePicRect> rectList = noteBmpRectMaps.get(rectKey);
		if(rectList == null){
			rectList = new ArrayList<NotePicRect>();
		}
		if(!rectList.contains(noteRect)){
			rectList.add(noteRect);
		}
		noteBmpRectMaps.put(rectKey, rectList);
	}

	private PageNoteK newPageNoteKey(int chapterIndex, int pageIndexInChapter) {
		
		PageNoteK rectKey = new PageNoteK();
		rectKey.setChapterIndex(chapterIndex);
		rectKey.setPageIndexInChapter(pageIndexInChapter);
		
		return rectKey;
	}
	
	public void deleteNoteRect(int chapterIndex, int pageIndexInChapter, NoteFlag flag){
		
		PageNoteK rectKey = newPageNoteKey(chapterIndex, pageIndexInChapter);
		List<NotePicRect> rectList = noteBmpRectMaps.get(rectKey);
		if(rectList == null){
			return;
		}
		int delPosition = -1;
		for(int i = 0, len = rectList.size(); i < len; i++){
			NotePicRect nRect = rectList.get(i);
			if(flag.equals(nRect.getFlag())){
				delPosition = i;
			}
		}
		if(delPosition != -1){
			rectList.remove(delPosition);
		}
		noteBmpRectMaps.put(rectKey, rectList);
	}
	
	public void deleteNoteRect(int chapterIndex, int pageIndexInChapter){
		PageNoteK rectKey = newPageNoteKey(chapterIndex, pageIndexInChapter);
		if(noteBmpRectMaps.containsKey(rectKey)){
			noteBmpRectMaps.remove(rectKey);
		}
	}
	
	
	/**
	 * @param chapterTag
	 * @param pageIndexInChapter
	 * @param x
	 * @param yInPage
	 * @return -1 代表没有点中 笔记小图  否则返回BookNote.id
	 */
	public NotePicRect isClickNoteBitmap(int chapterIndex, int pageIndexInChapter, int x, int yInPage){
		
		NotePicRect noteFlag = null;
		//boolean retb = false;
		PageNoteK rectKey = newPageNoteKey(chapterIndex, pageIndexInChapter);
		
		List<NotePicRect> rectList = noteBmpRectMaps.get(rectKey);
		if(rectList != null){
			for(NotePicRect nrect : rectList){
				if(nrect.rect.contains(x, yInPage)){
					//retb = true;
					noteFlag = nrect;
					break;
				}
			}
		}
		return noteFlag;
	}

	public void resetNoteRectMaps(){
		if(noteBmpRectMaps.size() > 0){
			try {
				Iterator<PageNoteK> iters = noteBmpRectMaps.keySet().iterator();
				while(iters.hasNext()){
					List<NotePicRect> rectList = noteBmpRectMaps.get(iters.next());
					rectList.clear();
					iters.remove();
				}
				noteBmpRectMaps.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void clear(){
		resetNoteRectMaps();
	}
	
	private void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
	
	public static class PageNoteK {
		
		private int chapterIndex = -1;
		private int pageIndexInChapter = -1;
		
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof PageNoteK) || isDefaultValue()){
				return false;
			}
			PageNoteK otherO = (PageNoteK) o;
			return chapterIndex == otherO.chapterIndex 
					&& pageIndexInChapter == otherO.pageIndexInChapter;
		}
		
		@Override
		public int hashCode() {
			if(isDefaultValue()){
				return super.hashCode();
			}
			return toString().hashCode();
		}

		private boolean isDefaultValue() {
			return chapterIndex == -1 || pageIndexInChapter == -1;
		}

		public int getChapterIndex() {
			return chapterIndex;
		}

		public void setChapterIndex(int chapterIndex) {
			this.chapterIndex = chapterIndex;
		}

		public int getPageIndexInChapter() {
			return pageIndexInChapter;
		}

		public void setPageIndexInChapter(int pageIndexInChapter) {
			this.pageIndexInChapter = pageIndexInChapter;
		}
		
		@Override
		public String toString() {
			return (chapterIndex + ":" + pageIndexInChapter);
		}
		
	}
	
	public static class NotePicRect {
		
		private Rect rect;
		private NoteFlag flag;
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof NotePicRect){
				NotePicRect other = (NotePicRect) o;
				return other.getFlag().equals(getFlag());
			}
			return false;
		}

		public Rect getRect() {
			return rect;
		}

		public void setRect(Rect rect) {
			this.rect = rect;
		}

		public NoteFlag getFlag() {
			return flag;
		}

		public void setFlag(NoteFlag flag) {
			this.flag = flag;
		}

	}
	
	public static class NoteFlag {
		
		private int chapterIndex;
		private int startIndex;
		private int endIndex;
		
		public int getChapterIndex() {
			return chapterIndex;
		}
		public void setChapterIndex(int chapterIndex) {
			this.chapterIndex = chapterIndex;
		}
		public int getStartIndex() {
			return startIndex;
		}
		public void setStartIndex(int startIndex) {
			this.startIndex = startIndex;
		}
		public int getEndIndex() {
			return endIndex;
		}
		public void setEndIndex(int endIndex) {
			this.endIndex = endIndex;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof NoteFlag){
				NoteFlag other = (NoteFlag)o;
				return other.getChapterIndex() == getChapterIndex()
						&& other.getStartIndex() == getStartIndex()
						&& other.getEndIndex() == getEndIndex();
			}
			return super.equals(o);
		}
		
	}
	
}
