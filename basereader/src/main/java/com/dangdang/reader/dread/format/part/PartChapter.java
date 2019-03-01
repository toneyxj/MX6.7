package com.dangdang.reader.dread.format.part;

import android.text.TextUtils;

import com.dangdang.reader.dread.format.BaseEBookManager;
import com.dangdang.reader.dread.format.Chapter;

/**
 * Created by Yhyu on 2015/5/19.
 */
public class PartChapter extends Chapter {
	protected int id;// 章节id,对于原创书，可能作为书签笔记中的index
	public static final int CHAPTER_FREE_YES = 1;
	public static final int CHAPTER_FREE_NO = 0;
	private static final long serialVersionUID = 1L;

	protected String title; // 章节标题
	protected int wordCnt; // 章节总字数
	protected int index; // 章节序号
	protected int isFree; // 是否免费
	protected int pageCount; // 章节页码
	protected PartBuyInfo partBuyInfo;
    protected int code;

	public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public PartChapter() {
	}

	public PartChapter(String path) {
		super(path);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof PartChapter)) {
			return false;
		}
		if (this.path == null || this.path.trim().length() == 0) {
			return false;
		}
		PartChapter other = (PartChapter) o;
		return this.path.equals(other.path);
	}

	@Override
	public int hashCode() {
		if (TextUtils.isEmpty(path)) {
			return super.hashCode();
		}
		return path.hashCode();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getWordCnt() {
		return wordCnt;
	}

	public void setWordCnt(int wordCnt) {
		this.wordCnt = wordCnt;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIsFree() {
		return isFree;
	}

	public void setIsFree(int isFree) {
		this.isFree = isFree;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public PartBuyInfo getPartBuyInfo() {
		return partBuyInfo;
	}

	public void setPartBuyInfo(PartBuyInfo partBuyInfo) {
		this.partBuyInfo = partBuyInfo;
	}

    @Override
    public String toString() {
        return "PartChapter{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", wordCnt=" + wordCnt +
                ", index=" + index +
                ", isFree=" + isFree +
                ", pageCount=" + pageCount +
                ", partBuyInfo=" + partBuyInfo +
                ", code=" + code +
                '}';
    }
}
