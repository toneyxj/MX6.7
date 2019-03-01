package com.dangdang.reader.domain;

import com.dangdang.zframework.network.download.DownloadConstant.Status;

public class ProductDomain {

	public final static int TYPE_DEFAULT = 0;
	public final static int TYPE_FREE = 1;
	public final static int TYPE_CHARGE = 2;

	public final static String DEFAULT_PRODUCTID = "-1";
	public final static String BLUE_BLACK_PRODUCTID = "defaultBluefont";
	// public final static String BLUE_BLACK_NAME = "方正兰亭黑";
	/**
	 * FontDomain.TYPE_DEFAULT FontDomain.TYPE_FREE FontDomain.TYPE_CHARGE
	 */
	public int type;
	public String productId;
	public String productName;
	public String productNameUrl;// 字体
	public String coverUrl;
	public String imgUrl;
	public String fontDownUrl;
	public String fileSize;
	public String author;
	public String desc;
	public float price;
	public float salePrice;
	public String firstPromoModel = "";
	public String editorRecommend = "";
	public boolean freeBook = false;
	public boolean isBorrow = false;
	public long totalSize;
	public long progress;
	public Status status = Status.UNSTART;
	public String fontZipPath;
	public String fontFtfPath;
	public String saleQutity;
	public String paperBookPrice;
	public String jsonStr;
	public String riseNum;
	public String commentQutity;
	public boolean isEbook = true;

	public int percent() {
		int percent = 0;
		if (totalSize > 0) {
			float mPrgs = (progress * 100f / totalSize);
			percent = (int) Math.rint(mPrgs);
		}
		return percent;
	}

//	public static class ProductDataHolder {
//		public List<ProductDomain> data;
//		public ResultExpCode expCode;
//		public int totalNum = 0;
//		public String name;
//		public boolean hasPaper;
//		public String columnCode;
//		public int productType;
//
//	}
}
