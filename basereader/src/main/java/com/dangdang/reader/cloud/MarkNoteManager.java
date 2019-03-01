package com.dangdang.reader.cloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.dangdang.reader.domain.CloudDataList;
import com.dangdang.reader.dread.data.BookMark;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.data.MarkKey;
import com.dangdang.reader.dread.data.NoteKey;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.BaseBookManager;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.holder.ServiceManager;
import com.dangdang.reader.dread.service.MarkService;
import com.dangdang.reader.dread.service.NoteService;

/**
 * 逻辑更改：
 * 更改书签、笔记列表  获取数据逻辑：不要每次都从数据库获取
 */
/**
 * 插入1000条数据：1-2s 问题：合并过程中 退出阅读 然后再马上打开阅读 是否同步过的状态(什么时候改)
 */
public class MarkNoteManager {

	// private static MarkNoteManager mInstance = null;

	/**
	 * 所有的：合并过程中不包括(changeBookMarks和changeBookNotes)
	 */
	private Map<MarkKey, BookMark> mBookMarks = new Hashtable<MarkKey, BookMark>();
	private Map<NoteKey, BookNote> mBookNotes = new Hashtable<NoteKey, BookNote>();

	/**
	 * 合并书签|笔记时 再操作的书签和笔记...
	 */
	private List<BookMark> mChangeBookMarks;
	private List<BookNote> mChangeBookNotes;

	/**
	 * 是否正在合并
	 */
	private boolean mMerge = false;
	/**
	 * 此书是否支持云同步
	 */
	private boolean mBookSupportCloudSync = false;

	private MarkService mMarkService;
	private NoteService mNoteService;

	/**
	 * 
	 * @param serviceManager
	 */
	public MarkNoteManager(ServiceManager serviceManager) {
		mMarkService = serviceManager.getMarkService();
		mNoteService = serviceManager.getNoteService();
	}

	/**
	 * TODO 考虑更改？
	 * 
	 * @param serviceManager
	 * @return
	 */
	/*
	 * public synchronized static MarkNoteManager getInstance(ServiceManager
	 * serviceManager){ if(mInstance == null){ mInstance = new
	 * MarkNoteManager(serviceManager); } return mInstance; }
	 */

	/**
	 * 合并书签、笔记到缓存和数据库
	 * 
	 * @param dataList
	 */
	public synchronized void mergeMarkAndNote(CloudDataList dataList) {

		if (dataList.isEmpty()) {
			return;
		}

		setMerge();
		if (!dataList.isMarkEmpty()) {
			List<BookMark> markMergeResult = mergeMarks(dataList.getBookMarks());
			processMergeMarkResult(markMergeResult);
		}
		if (!dataList.isNoteEmpty()) {
			List<BookNote> noteMergeResult = mergeNotes(dataList.getBookNotes());
			processMergeNoteResult(noteMergeResult);
		}
		resetMerge();
	}

	/**
	 * 合并书签到缓存和数据库
	 * 
	 * @param markMergeResult
	 */
	private void processMergeMarkResult(List<BookMark> markMergeResult) {

		if (markMergeResult != null && markMergeResult.size() > 0) {
			MarkService markService = mMarkService;
			markService.performMergeMarkResult(markMergeResult);
		}
	}

	/**
	 * 合并笔记到缓存和数据库
	 * 
	 * @param noteMergeResult
	 */
	private void processMergeNoteResult(List<BookNote> noteMergeResult) {

		if (noteMergeResult != null && noteMergeResult.size() > 0) {
			NoteService noteService = mNoteService;
			noteService.performMergeNoteResult(noteMergeResult, mBookNotes);
		}
	}

	/**
	 * 合并再操作的书签和笔记...数据
	 */
	public synchronized void mergeChangeMarkAndNote() {
		if (!isLocalChangeMarksEmpty()) {
			/**
			 * 本地变化的 可以当做云的处理
			 */
			final List<BookMark> localChangeMarks = mChangeBookMarks;
			List<BookMark> changeMergeResult = mergeMarks(localChangeMarks);
			processMergeMarkResult(changeMergeResult);

			clearChangeMarks();
		}
		if (!isLocalChangeNotesEmpty()) {
			/**
			 * 本地变化的 当做云的处理
			 */
			final List<BookNote> localChangeNotes = mChangeBookNotes;
			List<BookNote> changeNotesMergeResult = mergeNotes(localChangeNotes);
			processMergeNoteResult(changeNotesMergeResult);

			clearChangeNotes();
		}
	}

	/**
	 * 合并书签 合并结果：返回需要本地更改的书签列表 并把差异同步到缓存中
	 * 
	 * @param cloudMarks
	 * @return
	 */
	private List<BookMark> mergeMarks(final List<BookMark> cloudMarks) {

		final Map<MarkKey, BookMark> localMarks = mBookMarks;
		final List<BookMark> markMergeResult = new ArrayList<BookMark>();

		BookMark cloudMark = null;
		BookMark localMark = null;
		MarkKey markKey = null;
		for (int i = 0, len = cloudMarks.size(); i < len; i++) {

			cloudMark = cloudMarks.get(i);
			markKey = getMarkKey(cloudMark);
			localMark = localMarks.get(markKey);
			/**
			 * 比较差异书签
			 */
			BookMark differenceMark = compareMark(cloudMark, localMark);

			if (differenceMark != null) {
				markMergeResult.add(differenceMark);

				// TODO 同步到缓存中 可能报ConcurrentModificationException
				if (isDeleteStatus(differenceMark.getStatus())) {
					localMarks.remove(markKey);
				} else {
					localMarks.put(markKey, differenceMark);
				}
			}

		}
		return markMergeResult;
	}

	/**
	 * 如果本地没有的 或者 本地的操作时间比云端旧的 视为差异数据 在操作数据库时 根据status对数据库 insert、update、delete
	 * 
	 * @param cloudMark
	 * @param localMark
	 * @return
	 */
	private BookMark compareMark(BookMark cloudMark, BookMark localMark) {
		BookMark differenceMark = null;
		if (localMark == null) {// TODO 如果本地没有，且云端书签cloudMark为删除状态，还有必要作为差异
								// 再删吗？除非缓存与数据库不同步
			differenceMark = cloudMark;
			// 如果本地没有，且云端是更新状态 那么对本地的状态应该是增加，在插入数据库时 根据status对数据库
			// insert、update、delete
			if (isUpdateStatus(differenceMark.getStatus())) {
				differenceMark.setStatus(String.valueOf(Status.COLUMN_NEW));
			}
		} else {
			/**
			 * 本地存的毫秒，服务端是秒 所以需要转换时间
			 */
			long cloudOperateTime = cloudMark.getMarkTime() * 1000;
			long localOperateTime = localMark.getMarkTime();
			// String cloudStatus = cloudMark.getExpColumn1();
			// String localStatus = localMark.getExpColumn1();
			if (localOperateTime < cloudOperateTime) {
				differenceMark = cloudMark;

				// 如果都有，并且云端是新增的状态 ，那么对本地的状态应该是修改
				if (isNewStatus(cloudMark.getStatus())) {
					differenceMark.setStatus(String
							.valueOf(Status.COLUMN_UPDATE));
				}

			} else {
			}
		}
		if (differenceMark != null) {
			/**
			 * 置为已同步状态 本地存的毫秒，服务端是秒 所以需要转换时间
			 */
			differenceMark.setCloudStatus(String.valueOf(Status.CLOUD_YES));
			differenceMark.setIsBought(ReadInfo.BSTATUS_FULL);

			long markTime = differenceMark.getMarkTime() * 1000;
			long modifyTime = 0;
			try {
				modifyTime = Long.valueOf(differenceMark.getModifyTime()) * 1000;
			} catch (Exception e) {
				e.printStackTrace();
			}
			differenceMark.setMarkTime(markTime);
			differenceMark.setModifyTime(String.valueOf(modifyTime));
		}
		return differenceMark;
	}

	/**
	 * 合并笔记 合并结果：返回需要本地更改的笔记列表 并把差异同步到缓存中
	 * 
	 * @param cloudNotes
	 * @return
	 */
	private List<BookNote> mergeNotes(final List<BookNote> cloudNotes) {

		final Map<NoteKey, BookNote> localNotes = mBookNotes;
		final List<BookNote> mergeNoteResult = new ArrayList<BookNote>();

		BookNote cloudNote = null;
		BookNote localNote = null;
		NoteKey noteKey = null;
		for (int i = 0, len = cloudNotes.size(); i < len; i++) {

			cloudNote = cloudNotes.get(i);
			noteKey = getNoteKey(cloudNote);
			localNote = localNotes.get(noteKey);
			/**
			 * 比较差异笔记
			 */
			BookNote differenceNote = compareNote(cloudNote, localNote);
			if (differenceNote != null) {
				mergeNoteResult.add(differenceNote);

				// TODO 同步到缓存中 可能报ConcurrentModificationException
				if (isDeleteStatus(differenceNote.getStatus())) {
					localNotes.remove(noteKey);
				} else {
					localNotes.put(noteKey, differenceNote);
				}
			}
		}

		return mergeNoteResult;
	}

	/**
	 * 如果本地没有的 或者 本地的操作时间比云端旧的 视为差异数据 在插入数据库时 根据status对数据库 insert、update、delete
	 * 本地存的毫秒，服务端是秒 所以需要转换时间
	 * 
	 * @param cloudNote
	 * @param localNote
	 * @return
	 */
	private BookNote compareNote(BookNote cloudNote, BookNote localNote) {
		BookNote differenceNote = null;
		if (localNote == null) {
			differenceNote = cloudNote;
			// //如果本地没有，且云端是更新状态 那么对本地的状态应该是增加
			if (isUpdateStatus(differenceNote.getStatus())) {
				differenceNote.setStatus(String.valueOf(Status.COLUMN_NEW));
			}
		} else {
			/**
			 * 本地存的毫秒，服务端是秒 所以需要转换时间
			 */
			long cloudOperateTime = cloudNote.getNoteTime() * 1000;
			long localOperateTime = localNote.getNoteTime();
			if (localOperateTime < cloudOperateTime) {
				differenceNote = cloudNote;

				// 如果都有，并且云端是新增的状态 ，那么对本地的状态应该是修改
				if (isNewStatus(cloudNote.getStatus())) {
					differenceNote.setStatus(String
							.valueOf(Status.COLUMN_UPDATE));
				}
				differenceNote.setId(localNote.getId());// 设置主键id，点击笔记小按钮时有用
			} else {
				// ?
			}
		}
		if (differenceNote != null) {
			differenceNote.setCloudStatus(String.valueOf(Status.CLOUD_YES));
			differenceNote.setIsBought(ReadInfo.BSTATUS_FULL);

			long noteTime = differenceNote.getNoteTime() * 1000;
			long modifyTime = 0;
			try {
				modifyTime = Long.valueOf(differenceNote.getModifyTime()) * 1000;
			} catch (Exception e) {
				e.printStackTrace();
			}
			differenceNote.setNoteTime(noteTime);
			differenceNote.setModifyTime(String.valueOf(modifyTime));
		}
		return differenceNote;
	}

	private MarkKey getMarkKey(BookMark bookMark) {

		final String productId = bookMark.pId;
		final String bookModVersion = bookMark.bookModVersion;
		final int chapterIndex = bookMark.chapterIndex;
		final int elementIndex = bookMark.elementIndex;

		return new MarkKey(productId, bookModVersion, chapterIndex, elementIndex);
	}

	private NoteKey getNoteKey(BookNote bookNote) {

		final String productId = bookNote.getBookId();
		final String modVersion = bookNote.getBookModVersion();
		final int chapterIndex = bookNote.getChapterIndex();
		final int startElementIndex = bookNote.getNoteStart();
		final int endElementIndex = bookNote.getNoteEnd();

		return getNoteKey(productId, modVersion, chapterIndex, startElementIndex,
				endElementIndex);
	}

	private NoteKey getNoteKey(final String productId, final String modVersion, final int chapterIndex,
			final int startElementIndex, final int endElementIndex) {

		return new NoteKey(productId, modVersion, chapterIndex, startElementIndex,
				endElementIndex);
	}

	private boolean isNewStatus(String status) {
		return Status.isNew(Integer.valueOf(status));
	}

	private boolean isUpdateStatus(String status) {
		return Status.isUpdate(Integer.valueOf(status));
	}

	private boolean isDeleteStatus(String status) {
		return Status.isDelete(Integer.valueOf(status));
	}

	public boolean isLocalMarksEmpty() {
		return mBookMarks == null || mBookMarks.size() == 0;
	}

	public boolean isLocalNotesEmpty() {
		return mBookNotes == null || mBookNotes.size() == 0;
	}

	public boolean isLocalChangeMarksEmpty() {
		return mChangeBookMarks == null || mChangeBookMarks.size() == 0;
	}

	public boolean isLocalChangeNotesEmpty() {
		return mChangeBookNotes == null || mChangeBookNotes.size() == 0;
	}

	/**
	 * 是否正在合并书签、笔记...
	 * 
	 * @return
	 */
	public boolean isMerge() {
		return mMerge;
	}

	private void setMerge() {
		this.mMerge = true;
	}

	private void resetMerge() {
		mMerge = false;
	}

	public void setBookSupportCloudSync(boolean isBookSupportCloudSync) {
		this.mBookSupportCloudSync = isBookSupportCloudSync;
	}

	/**
	 * 此书是否支持云同步
	 * 
	 * @return
	 */
	public boolean isBookSupportCloudSync() {
		return mBookSupportCloudSync;
	}

	public void setBookMarks(Map<MarkKey, BookMark> bookMarks) {
		if (bookMarks != null) {
			this.mBookMarks = bookMarks;
		}
	}

	public void setBookNotes(Map<NoteKey, BookNote> bookNotes) {
		if (bookNotes != null) {
			this.mBookNotes = bookNotes;
		}
	}

	private void putBookNoteToCache(BookNote note) {
		NoteKey key = getNoteKey(note);
		mBookNotes.put(key, note);
	}

	private void removeBookNoteToCache(BookNote note) {
		NoteKey key = getNoteKey(note);
		mBookNotes.remove(key);
	}

	private void putBookMarkToCache(BookMark mark) {
		MarkKey key = getMarkKey(mark);
		mBookMarks.put(key, mark);
	}

	private void removeBookMarkToCache(BookMark mark) {
		MarkKey key = getMarkKey(mark);
		mBookMarks.remove(key);
	}

	private void putBookMarkToChangeList(BookMark mark) {
		if (mChangeBookMarks == null) {
			mChangeBookMarks = new ArrayList<BookMark>();
		}
		mChangeBookMarks.remove(mark);// TODO modify ?
		mChangeBookMarks.add(mark);
	}

	private void putBookNoteToChangeList(BookNote note) {
		if (mChangeBookNotes == null) {
			mChangeBookNotes = new ArrayList<BookNote>();
		}
		mChangeBookNotes.remove(note);// TODO modify ?
		mChangeBookNotes.add(note);
	}

	private void clearChangeMarks() {
		if (!isLocalChangeMarksEmpty()) {
			mChangeBookMarks.clear();
		}
	}

	private void clearChangeNotes() {
		if (!isLocalChangeNotesEmpty()) {
			mChangeBookNotes.clear();
		}
	}

	private void clearLocalMarks() {
		if (!isLocalMarksEmpty()) {
			mBookMarks.clear();
		}
	}

	private void clearLocalNotes() {
		if (!isLocalNotesEmpty()) {
			mBookNotes.clear();
		}
	}

	/**
	 * 与合并书签、笔记...同步
	 */
	public synchronized void clear() {

		clearLocalMarks();
		clearLocalNotes();

		clearChangeMarks();
		clearChangeNotes();
	}

	/**
	 * 对一条书签进行增删改(先操作缓存，再操作数据库)
	 * 
	 * @param mark
	 * @param type
	 */
	public void operationBookMark(BookMark mark, OperateType type) {
		if (isMerge()) {
			/*
			 * if(type == OperateType.NEW){ } else if(type ==
			 * OperateType.DELETE){ }
			 */
			putBookMarkToChangeList(mark);
		} else {
			if (type == OperateType.NEW) {
				if (checkMarkExist(mark)) {
					updateMarkStatus(mark);
				} else {
					mMarkService.saveMark(mark);
				}
				putBookMarkToCache(mark);
			} else if (type == OperateType.DELETE) {
				String pid = mark.getpId();
				int isBought = mark.getIsBought();
				int chapterIndex = mark.getChapterIndex();
				int startIndex = mark.getElementIndex();
				int endIndex = mark.getElementIndex();
				if (isBookSupportCloudSync()) {
					/*
					 * int status = Status.COLUMN_DELETE; long operateTime =
					 * mark.getMarkTime(); mMarkService.updateMarkStatus(pid,
					 * isBought, chapterIndex, startIndex, endIndex, status,
					 * operateTime);
					 */
					updateMarkStatus(mark);
					putBookMarkToCache(mark);
				} else {
					mMarkService.deleteBookMark(pid, isBought, chapterIndex,
							startIndex, endIndex);
					removeBookMarkToCache(mark);
				}
			}
		}
	}

	private void updateMarkStatus(BookMark mark) {
		String pid = mark.getpId();
		int isBought = mark.getIsBought();
		int chapterIndex = mark.getChapterIndex();
		int startIndex = mark.getElementIndex();
		int endIndex = mark.getElementIndex();
		int status = Integer.valueOf(mark.getStatus());
		long operateTime = mark.getMarkTime();
		String modVersion = mark.getBookModVersion();
		mMarkService.updateMarkStatus(pid, modVersion, isBought, chapterIndex, startIndex,
				endIndex, status, operateTime);
	}

	/**
	 * 删除这个范围内的书签 startElementIndex - endElementIndex
	 * 支持云同步的书、修改状态为Delete,否则直接删除(删缓存和数据库)
	 * 
	 * @param pid
	 * @param isBought
	 * @param chapterIndex
	 * @param startElementIndex
	 * @param endElementIndex
	 * @param operateTime
	 */
	public void deleteBookMark(String pid, String modVersion, int isBought, int chapterIndex,
			int startElementIndex, int endElementIndex, long operateTime) {

		if (isBookSupportCloudSync()) {
			Collection<BookMark> values = mBookMarks.values();
			int status = Status.COLUMN_DELETE;
			for (BookMark mark : values) {
				int eIndex = mark.getElementIndex();
				if (mark.getChapterIndex() == chapterIndex
						&& eIndex >= startElementIndex
						&& eIndex <= endElementIndex) {
					mark.setStatus(String.valueOf(status));
					mark.setMarkTime(operateTime);
					mark.setModifyTime(String.valueOf(operateTime));
					mark.setCloudStatus(String.valueOf(Status.CLOUD_NO));

					// mBookMarks.put(getMarkKey(mark), mark);
				}
			}
			mMarkService.updateMarkStatus(pid, modVersion, isBought, chapterIndex,
					startElementIndex, endElementIndex, status, operateTime);
		} else {
			Iterator<MarkKey> iters = mBookMarks.keySet().iterator();
			while (iters.hasNext()) {
				BookMark mark = mBookMarks.get(iters.next());
				int eIndex = mark.getElementIndex();
				if (mark.getChapterIndex() == chapterIndex
						&& eIndex >= startElementIndex
						&& eIndex <= endElementIndex) {
					iters.remove();
				}
			}

			/*
			 * for(BookMark mark : values){ int eIndex = mark.getElementIndex();
			 * if(mark.getChapterIndex() == chapterIndex && eIndex >=
			 * startElementIndex && eIndex <= endElementIndex){
			 * mBookMarks.remove(getMarkKey(mark)); } }
			 */
			mMarkService.deleteBookMark(pid, isBought, chapterIndex,
					startElementIndex, endElementIndex);
		}

	}

	/**
	 * 对笔记增、删、改(先操作缓存，再操作数据库) insert时返回主键id，其它返回-1;
	 * 
	 * @param note
	 * @param type
	 * @return
	 */
	public long operationBookNote(BookNote note, OperateType type) {
		long id = -1;
		if (isMerge()) {
			putBookNoteToChangeList(note);
		} else {
			if (type == OperateType.NEW) {
				BookNote cacheNote = checkNoteExist(note);
				if (cacheNote != null) {
					mNoteService.updateNote(note);
					note.setId(cacheNote.getId());
				} else {
					id = mNoteService.saveNote(note);
					note.setId((int) id);
				}
				putBookNoteToCache(note);
			} else if (type == OperateType.UPDATE) {
				BookNote cacheNote = checkNoteExist(note);
				if (cacheNote != null && cacheNote.getId() > 0) {
					note.setId(cacheNote.getId());
				}
				mNoteService.updateNote(note);
				putBookNoteToCache(note);
			} else if (type == OperateType.DELETE) {
				if (isBookSupportCloudSync()) {
					mNoteService.updateNote(note);
					putBookNoteToCache(note);
				} else {
					mNoteService.deleteBookNoteById(note.getId());
					removeBookNoteToCache(note);
				}
			}
		}
		return id;
	}

	/**
	 * 获取提供参数范围内的List<BookNote>
	 * 
	 * @param chapterIndex
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public List<BookNote> getBookNotes(int chapterIndex, int startIndex,
			int endIndex) {

		if (isLocalNotesEmpty()) {
			return null;
		}

		final int start = startIndex;
		final int end = endIndex;
		List<BookNote> tmpNotes = findBookNotes(mBookNotes.values(),
				chapterIndex, start, end);

		if (!isLocalChangeNotesEmpty()) {
			// 从脏数据里找
			List<BookNote> tmpChangeNotes = findBookNotes(mChangeBookNotes,
					chapterIndex, start, end);
			// 合并结果
			if (tmpChangeNotes != null && tmpChangeNotes.size() > 0) {
				for (BookNote cbNote : tmpChangeNotes) {
					if (isDeleteStatus(cbNote.getStatus())) {
						tmpNotes.remove(cbNote);
					} else if (isUpdateStatus(cbNote.getStatus())) {
						int location = tmpNotes.indexOf(cbNote);
						if (location != -1) {
							tmpNotes.remove(location);
						}
						tmpNotes.add(cbNote);
					} else if (isNewStatus(cbNote.getStatus())) {
						if (!tmpNotes.contains(cbNote)) {
							tmpNotes.add(cbNote);
						}
					}
				}
			}
		}

		return tmpNotes;
	}

	private List<BookNote> findBookNotes(Collection<BookNote> notes,
			int chapterIndex, int start, int end) {
		List<BookNote> tmpNotes = new CopyOnWriteArrayList<BookNote>();
		if (notes == null) {
			return tmpNotes;
		}
		for (BookNote note : notes) {
			if (isDeleteStatus(note.getStatus())) {
				continue;
			}
			boolean sameChapter = note.chapterIndex == chapterIndex;
			boolean b1 = note.noteStart <= end;// && note.noteStart >= start;
			boolean b2 = note.noteEnd >= start;// && note.noteEnd <= end;
			if (sameChapter && (b1 && b2)) {
				tmpNotes.add(note);
			}
		}
		return tmpNotes;
	}

	private BookNote checkNoteExist(BookNote note) {

		BookNote cacheNote = null;
		// boolean noteExist = false;
		if (!isLocalNotesEmpty()) {
			cacheNote = mBookNotes.get(getNoteKey(note));
			// noteExist = mBookNotes.containsKey(getNoteKey(note));
		}
		if (cacheNote == null && !isLocalChangeNotesEmpty()) {
			for (BookNote chNote : mChangeBookNotes) {
				if (chNote.equals(note)) {
					cacheNote = chNote;
				}
			}
			// noteExist = mChangeBookNotes.contains(note);
		}

		return cacheNote;
	}

	public BookNote checkNoteExist(final String productId, final String modVersion, final int isBought,
			final int chapterIndex, final int startElementIndex,
			final int endElementIndex) {

		BookNote cacheNote = null;
		// boolean noteExist = false;
		if (!isLocalNotesEmpty()) {
			cacheNote = mBookNotes.get(getNoteKey(productId, modVersion, chapterIndex,
					startElementIndex, endElementIndex));
			// noteExist = mBookNotes.containsKey(getNoteKey(note));
		}
		if (cacheNote == null && !isLocalChangeNotesEmpty()) {
			for (BookNote chNote : mChangeBookNotes) {
				if (chNote.getBookId().equals(productId)
						&& chNote.getBookModVersion().equals(modVersion)
						&& chNote.getIsBought() == isBought
						&& chNote.getChapterIndex() == chapterIndex
						&& chNote.getNoteStart() == startElementIndex
						&& chNote.getNoteEnd() == endElementIndex) {
					cacheNote = chNote;
				}
			}
			// noteExist = mChangeBookNotes.contains(note);
		}
		if (cacheNote != null) {
			if (isDeleteStatus(cacheNote.getStatus())) {
				cacheNote = null;
			}
		}
		return cacheNote;
	}

	/**
	 * (startIndex - endIndex)范围内是否有书签
	 * 
	 * @param pId
	 * @param isBought
	 * @param chapterIndex
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public boolean checkMarkExist(String pId, String modVersion, int isBought, int chapterIndex,
			int startIndex, int endIndex) {

		boolean markExist = false;
		// mMarkService.checkExist(pId, isBought, chapterIndex, startIndex,
		// endIndex);1、2486、2717
		List<BookMark> tmpMarks = findMark(mBookMarks.values(), chapterIndex,
				startIndex, endIndex, modVersion);
		if (!isLocalChangeMarksEmpty()) {
			// 再从脏数据里找
			List<BookMark> tmpChangeMarks = findMark(mChangeBookMarks,
					chapterIndex, startIndex, endIndex, modVersion);
			if (tmpChangeMarks != null && tmpChangeMarks.size() > 0) {// 合并
				for (BookMark tcMark : tmpChangeMarks) {
					if (isDeleteStatus(tcMark.getStatus())) {
						tmpMarks.remove(tcMark);
					}
				}
			}
		}
		markExist = (tmpMarks != null && tmpMarks.size() > 0);

		return markExist;
	}

	/**
	 * 是否存在书签
	 * 
	 * @param mark
	 * @return
	 */
	private boolean checkMarkExist(BookMark mark) {

		boolean markExist = mBookMarks.containsKey(getMarkKey(mark));
		if (!markExist && !isLocalChangeMarksEmpty()) {
			markExist = mChangeBookMarks.contains(mark);
		}

		return markExist;
	}

	private List<BookMark> findMark(Collection<BookMark> marks,
			int chapterIndex, int startIndex, int endIndex, String modVersion) {
		List<BookMark> tmpMarks = new CopyOnWriteArrayList<BookMark>();
		if (marks == null) {
			return tmpMarks;
		}

		for (BookMark mark : marks) {
			if (isDeleteStatus(mark.getStatus())) {
				continue;
			} else {
				if (mark.getChapterIndex() == chapterIndex
						&& mark.getBookModVersion().equals(modVersion)
						&& mark.getElementIndex() >= startIndex
						&& mark.getElementIndex() <= endIndex) {
					tmpMarks.add(mark);
				}
			}
		}
		return tmpMarks;
	}

	/*
	 * private boolean findMark(Collection<BookMark> marks, int chapterIndex,
	 * int startIndex, int endIndex) { if(marks == null){ return false; }
	 * boolean markExist = false; for(BookMark mark : marks){
	 * if(isDeleteStatus(mark.getStatus())){ continue; } else {
	 * if(mark.getChapterIndex() == chapterIndex && mark.getElementIndex() >=
	 * startIndex && mark.getElementIndex() <= endIndex){ markExist = true;
	 * break; } } } return markExist; }
	 */

	public enum OperateType {
		NEW, UPDATE, DELETE
	}

	public boolean UpdateMarksAndBookNotesIfModVersionChange(Book book, BaseBookManager bookManager) {
		
		UpdateMarksIfModVersionChange(book, bookManager);
		UpdateBookNotesIfModVersionChange(book, bookManager);
		
		return true;
	}
	
	public boolean UpdateMarksIfModVersionChange(Book book, BaseBookManager bookManager) {
		String modVersion = book.getModVersion();
		Collection<BookMark> marks = mBookMarks.values();
		final List<BookMark> updateMarkResult = new ArrayList<BookMark>();

		for (BookMark mark : marks) {
			if (!mark.getBookModVersion().equals(modVersion)) {
				Chapter chapter = book.getChapter(mark.getChapterIndex());
				if (chapter != null) {
					try {
						BookMark markOld = (BookMark)mark.clone();
						markOld.setStatus(String.valueOf(Status.COLUMN_DELETE));
						markOld.setCloudStatus(String.valueOf(Status.CLOUD_NO));
						updateMarkResult.add(markOld);

						int newIndex = bookManager.UpdateElementIndex(chapter.getPath(), mark.getBookModVersion(), 
								modVersion, mark.getElementIndex());
						if (newIndex < 0)
							newIndex = mark.getElementIndex();
						mark.setElementIndex(newIndex);
						mark.setStatus(String.valueOf(Status.COLUMN_NEW));
						mark.setCloudStatus(String.valueOf(Status.CLOUD_NO));
						mark.setBookModVersion(modVersion);
						updateMarkResult.add(mark);
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		processMergeMarkResult(updateMarkResult);
		
		return true;
	}

	public boolean UpdateBookNotesIfModVersionChange(Book book, BaseBookManager bookManager) {
		String modVersion = book.getModVersion();
		Collection<BookNote> notes = mBookNotes.values();
		final List<BookNote> updateNoteResult = new ArrayList<BookNote>();

		for (BookNote note : notes) {
			if (!note.getBookModVersion().equals(modVersion)) {
				Chapter chapter = book.getChapter(note.getChapterIndex());
				if (chapter != null) {
					try {
						BookNote noteOld = (BookNote)note.clone();
						noteOld.setStatus(String.valueOf(Status.COLUMN_DELETE));
						noteOld.setCloudStatus(String.valueOf(Status.CLOUD_NO));
						updateNoteResult.add(noteOld);
						
						int newStartIndex = bookManager.UpdateElementIndex(chapter.getPath(), note.getBookModVersion(), 
								modVersion, note.getNoteStart());
						if (newStartIndex < 0)
							newStartIndex = note.getNoteStart();
						note.setNoteStart(newStartIndex);
						int newEndIndex = bookManager.UpdateElementIndex(chapter.getPath(), note.getBookModVersion(), 
								modVersion, note.getNoteEnd());
						if (newEndIndex < 0)
							newEndIndex = note.getNoteEnd();
						note.setNoteEnd(newEndIndex);
						note.setStatus(String.valueOf(Status.COLUMN_NEW));
						note.setCloudStatus(String.valueOf(Status.CLOUD_NO));
						note.setBookModVersion(modVersion);
						updateNoteResult.add(note);
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
			}
		}
		
		processMergeNoteResult(updateNoteResult);
		
		return true;
	}
	 

}
