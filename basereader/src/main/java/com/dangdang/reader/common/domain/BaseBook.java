package com.dangdang.reader.common.domain;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiaruri
 */
public class BaseBook implements Serializable {

	private static final long serialVersionUID = 1L;

	private int mediaType;                  // 书籍类型，1：原创电子书；2：出版物电子书；3：纸书
	private String productId;						// 纸书id
	private String saleId;                                        // 电子书saleId
	protected String mediaId;                                    // mediaId
	protected String title;                                        // 名称
	private String authorPenname;                                // 作者
	private String authorName;                                // 作者
	private String coverPic;                                    // 封面
	private String descs;                                        // 简介
	private String categorys = "无";                                // 分类
	private String categoryIds;                                 // 分类ID
	private String publisher;                                    // 出版社
	private String publishDate;                                    // 出版时间
	private String boughtId;				//电子书已购订单ID

	public String getBoughtId(){
		return boughtId;
	}
	
	public void setBoughtId(String id){
		boughtId = id;
	}
	
	public int getMediaType() {
		return mediaType;
	}

	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
	}


	public String getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(String categoryIds) {
		this.categoryIds = categoryIds;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getSaleId() {
		return saleId;
	}

	public void setSaleId(String saleId) {
		this.saleId = saleId;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthorPenname() {
		return authorPenname;
	}

	public void setAuthorPenname(String author) {
		this.authorPenname = author;
	}

	public String getCoverPic() {
		return coverPic;
	}

	public void setCoverPic(String coverPic) {
		this.coverPic = coverPic;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public String getCategorys() {
		// 多个分类用","分隔
        if (TextUtils.isEmpty(categorys))
            return categorys;
		if (categorys.contains(",")) {
			return categorys.split(",")[0];
		}
		return categorys;
	}

	public void setCategorys(String categorys) {
		this.categorys = categorys;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	@Override
	public String toString() {
		return "BaseBook [saleId=" + saleId + ", mediaId=" + mediaId + ", productId=" + productId
				+ ", title=" + title + ", authorPenname=" + authorPenname
				+ ", coverPic=" + coverPic + ", descs=" + descs
				+ ", categorys=" + categorys + ", categoryIds=" + categoryIds
				+ ", publisher=" + publisher + ", publishDate=" + publishDate
				+ "]";
	}

	public List<? extends BaseChapter> getChapters() {
		return null;
	}

	public BaseChapter getChapterByIndex(int pageIndex) {
		return null;
	}

	public int chapterIndexInBook(final BaseChapter chapter) {
		int index = 0;
		try {
			//EpubChapter html = new EpubChapter(htmlPath);
			index = getChapters().indexOf(chapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return index;
	}

	protected boolean mTheSameFile = true;
	private byte[] bookStructDatas = null;

	public boolean isTheSameFile() {
		return mTheSameFile;
	}

	public void setTheSameFile(boolean theSameFile) {
		this.mTheSameFile = theSameFile;
	}

	public byte[] getBookStructDatas() {
		return bookStructDatas;
	}

	public void setBookStructDatas(byte[] bookStructDatas) {
		this.bookStructDatas = bookStructDatas;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BaseBook baseBook = (BaseBook) o;

		return !(mediaId != null ? !mediaId.equals(baseBook.mediaId) : baseBook.mediaId != null);

	}

	@Override
	public int hashCode() {
		return mediaId != null ? mediaId.hashCode() : 0;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
}
