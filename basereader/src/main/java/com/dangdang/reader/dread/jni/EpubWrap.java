package com.dangdang.reader.dread.jni;

import android.graphics.Bitmap;

public class EpubWrap extends BaseJniWarp {
	public static class EResult {

		public int type; // 点击类型：如图片、锚点、注释
		public String strURL;// 点击图片的地址
		public String strAlt;// 点击图片的alt信息
		public ERect imgRect;// 图片的边框信息
		public int nWidth;// 图片实际宽度
		public int nHeight;// 图片实际高度
		public int nImgBgColor;
		public boolean isHttpUrl;

		public EResult() {

		}

		public EResult(int clickType, String url, String alt, ERect rect,
					   int nRealWidth, int nRealHeight, int nColor, boolean isHttpUrl) {
			this.type = clickType;
			this.strURL = url;
			this.strAlt = alt;
			this.imgRect = rect;
			this.nWidth = nRealWidth;
			this.nHeight = nRealHeight;
			this.nImgBgColor = nColor;
			this.isHttpUrl = isHttpUrl;
		}

		public int getType() {
			return type;
		}

		public String getStrURL() {
			return strURL;
		}

		public String getStrAlt() {
			return strAlt;
		}

		public ERect getImgRect() {
			return imgRect;
		}

		public int getImgRealWidth() {
			return nWidth;
		}

		public int getImgRealHeight() {
			return nHeight;
		}

		public int getImgBgColor() {
			return nImgBgColor;
		}

		// other column ...
		public boolean isHttpUrl() {
			return isHttpUrl;
		}
	}
	public static class EInnerGotoResult extends EResult {
		/*
		 * gotoType
		 * 
		 * typedef enum tagAType { AT_NONE, // 无跳转 AT_HTTP, // 网页 AT_INNER, //
		 * 本章内跳转 AT_OUTER, // 其他章跳转头 AT_OUTER_TAG, // 其他章跳转锚点 AT_MAILTO, // mail
		 * } ATYPE;
		 */
		private int gotoType;// 锚点类型
		private String href;// 锚点跳转地址
		private String anchorID;// 锚点ID
		private int pageIndex; // 锚点对应的页码

		public EInnerGotoResult(int gotoType, String href, String anchorID,
				int nPageIndex) {
			this.gotoType = gotoType;
			this.href = href;
			this.anchorID = anchorID;
			this.pageIndex = nPageIndex;
		}

		public int getGotoType() {
			return gotoType;
		}

		public String getHref() {
			return href;
		}

		public String getAnchorID() {
			return anchorID;
		}

		public int getPageIndex() {
			return pageIndex;
		}

	}

	public static class EChapterElementIndex {
		public int chapterIndex;
		public int elementIndex;
		public EChapterElementIndex() {
			chapterIndex = 0;
			elementIndex = 0;
		}
		public void setChapterIndex(int chapterIndex) {
			this.chapterIndex = chapterIndex;
		}

		public void setElementIndex(int elementIndex) {
			this.elementIndex = elementIndex;
		}
	}

	public EpubWrap() {

	}

	/**
	 * 对于非加密的epub书，封面图通过内核解析opf文件获取，解析完成，返回图片buffer
	 * 
	 * @param filepath
	 * @return
	 */
	public final native int getEpubBookCover(String filepath, int bookType,
			Bitmap bitmap);

	/**
	 * 获取epub的书名
	 * 
	 * @param filepath
	 * @param bookType
	 * @return
	 */
	public final native String getEpubBookCaption(String filepath, int bookType);

	/**
	 * 获取epub的内容类型
	 * 
	 * @param filepath
	 * @return
	 */
	public final native int getEpubBookCategory(String filepath);
	/**
	 * bookType 在BaseJniWrap里
	 */
	public final native int openFile(String bookPath, int bookType,
			BookStructHandler handler);

	/**
	 * 不再使用。留着兼容旧版本
	 */
	public final native int buildBookStruct(String bookPath,
			BookStructHandler handler);

	/**
	 * 先找缓存，没有再新排
	 * 
	 * @param html
	 * @param ALabelID
	 * @return
	 */
	public final native int getPageByALabel(String html, String ALabelID);

	// public final native boolean isComplete(String html); //判断是否排版完成


	/**
	 * 如：点击图片 return: 点击类型和图片路径等(锚点、注释相关数据)
	 */
	public final native EResult clickEvent(EPageIndex pageIndex, EPoint point);


	/**
	 * /** 解压jpeg
	 * 
	 * @param strImgFile
	 * @param bitmap
	 */
	public final native int decodeJpeg(String strImgFile, Bitmap bitmap);

	/**
	 * 对一段文本进行排版、绘制
	 * 
	 * @param pageIndex
	 *            页码
	 * @param handler
	 *            画廊数据信息
	 * @return
	 */
	public final native int getGalleryInfo(EPageIndex pageIndex,
			ImageGalleryHandler handler);

	/**
	 * 获取视频信息
	 * 
	 * @param pageIndex
	 * @param handler
	 * @return
	 */
	public final native int getVideoInfo(EPageIndex pageIndex,
			VideoInfoHandler handler);
	/**
	 * 添加设置灰度，第一个参数传true，第二个传一个灰度值
	 */
	public static native void setTextColorBlack(boolean bUseBlack, int nGray);
	/**
	 * 获取交互代码 交互表格信息
	 * 
	 * @param pageIndex
	 * @param handler
	 * @return
	 */
	public final native int getInteractiveBlocks(EPageIndex pageIndex,
			InteractiveBlockHandler handler);
	
	public final native int drawInteractiveBlock(EPageIndex pageIndex, int nIndex, int nWidth, int nHeight,
			DrawInteractiveBlockHandler handler);
	/**
	 * 请求停止排版
	 * 
	 * @return
	 */
	public final native int cancelParse();

	public final int getParagraphText(EPageIndex pageIndex, int elementIndex,
			boolean isFirst, int maxLen, ParagraphTextHandler handler) {
		final boolean forward = true;
		return getParagraphText(pageIndex, elementIndex, isFirst, forward, maxLen,
				handler);
	}

	public final native boolean decryptMedia(byte[] sources, byte[] dest);
	public final native boolean getEpubBookBig5EncodingSupport();
	public final native boolean getEpubBookTTSSupport();

	public final native boolean saveFileToDisk(String fileinEpub,
			String fileInDisk);
	
	public final native void calcEndElementIndexByReadRate(int startChapterIndex, int startElementIndex, float readRate, int totalCount, float imageWeight
																, EChapterElementIndex chapterElementIndex, int bookType);

	public final native float calcReadRate(int startChapterIndex, int startElementIndex, int endChapterIndex,
										   int endElementIndex, int totalCount, float imageWeight, int bookType);

	public final native int convertIndexToWeightedIndex(String htmlFile, int elementIndex, float imageWeight);

	public final native String getChaptersWeightedCountOffsetJson();
}
