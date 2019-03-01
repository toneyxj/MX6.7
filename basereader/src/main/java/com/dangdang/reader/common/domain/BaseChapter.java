package com.dangdang.reader.common.domain;

import java.io.Serializable;

/**
 * 章节基础数据结构 Created by liuboyu on 2014/12/13.
 */
public class BaseChapter implements Serializable, Titleable {
	public static final int CHAPTER_FREE_YES = 1;
	public static final int CHAPTER_FREE_NO = 0;
	private static final long serialVersionUID = 1L;

	protected String id; // 章节id
	protected String title; // 章节标题
	protected int wordCnt; // 章节总字数
	protected int index; // 章节序号
	protected String chapterPath; // 章节本地存储路径
	protected int isFree; // 是否免费
	protected int pageCount; // 章节页码

	public int getIsFree() {
		return isFree;
	}

	public void setIsFree(int isFree) {
		this.isFree = isFree;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String chapterName) {
		this.title = chapterName;
	}

	public String getChapterPath() {
		return chapterPath;
	}

	public void setChapterPath(String chapterPath) {
		this.chapterPath = chapterPath;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int indexOrder) {
		this.index = indexOrder;
	}

	public String getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id + "";
	}

	public int getWordCnt() {
		return wordCnt;
	}

	public void setWordCnt(int wordCnt) {
		this.wordCnt = wordCnt;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chapterPath == null) ? 0 : chapterPath.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + index;
		result = prime * result + pageCount;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + wordCnt;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseChapter other = (BaseChapter) obj;
		if (chapterPath == null) {
			if (other.chapterPath != null)
				return false;
		} else if (!chapterPath.equals(other.chapterPath))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (index != other.index)
			return false;
		if (isFree != other.isFree)
			return false;
		if (pageCount != other.pageCount)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (wordCnt != other.wordCnt)
			return false;
		return true;
	}

}
