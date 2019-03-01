package com.dangdang.reader.dread.data;

import com.dangdang.zframework.network.download.DownloadConstant;

/**
 * 字体实例
 *
 * @author Yhyu
 * @date 2014-12-11 下午2:11:30
 */
public class FontDomain {
	public String productId;
	private String downloadURL;
	private String fontSize;
	private String imageURL;
	private float originalPrice;
	private String productname = "";
	private float salePrice;
	private String fontPath;
	public DownloadConstant.Status status = DownloadConstant.Status.UNSTART;
	public long totalSize;
	public long progress;
	public String jsonStr;

	private boolean isBought = false;
	public String fontZipPath;
	public String fontFtfPath;
	public boolean freeBook;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductname() {
		return productname;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

	public String getFontPath() {
		return fontPath;
	}

	public void setFontPath(String fontPath) {
		this.fontPath = fontPath;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	public float getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(float originalPrice) {
		this.originalPrice = originalPrice;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}

	public boolean isBought() {
		return isBought;
	}

	public void setBought(boolean isBought) {
		this.isBought = isBought;
	}

	public float getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(float salePrice) {
		this.salePrice = salePrice;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FontDomain font = (FontDomain) o;

		if (isBought != font.isBought) return false;
		if (Float.compare(font.originalPrice, originalPrice) != 0) return false;
		if (Float.compare(font.salePrice, salePrice) != 0) return false;
		if (downloadURL != null ? !downloadURL.equals(font.downloadURL) : font.downloadURL != null)
			return false;
		if (fontPath != null ? !fontPath.equals(font.fontPath) : font.fontPath != null)
			return false;
		if (fontSize != null ? !fontSize.equals(font.fontSize) : font.fontSize != null)
			return false;
		if (imageURL != null ? !imageURL.equals(font.imageURL) : font.imageURL != null)
			return false;
		if (productId != null ? !productId.equals(font.productId) : font.productId != null)
			return false;
		if (productname != null ? !productname.equals(font.productname) : font.productname != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = productId != null ? productId.hashCode() : 0;
		result = 31 * result + (downloadURL != null ? downloadURL.hashCode() : 0);
		result = 31 * result + (fontSize != null ? fontSize.hashCode() : 0);
		result = 31 * result + (imageURL != null ? imageURL.hashCode() : 0);
		result = 31 * result + (originalPrice != +0.0f ? Float.floatToIntBits(originalPrice) : 0);
		result = 31 * result + (productname != null ? productname.hashCode() : 0);
		result = 31 * result + (salePrice != +0.0f ? Float.floatToIntBits(salePrice) : 0);
		result = 31 * result + (fontPath != null ? fontPath.hashCode() : 0);
		result = 31 * result + (isBought ? 1 : 0);
		return result;
	}

	public int percent() {
		int percent = 0;
		if (totalSize > 0) {
			float mPrgs = (progress * 100f / totalSize);
			percent = (int) Math.rint(mPrgs);
		}
		return percent;
	}

	@Override
	public String toString() {
		return "FontDomain{" +
				"productId='" + productId + '\'' +
				", downloadURL='" + downloadURL + '\'' +
				", fontSize='" + fontSize + '\'' +
				", imageURL='" + imageURL + '\'' +
				", originalPrice=" + originalPrice +
				", productname='" + productname + '\'' +
				", salePrice=" + salePrice +
				", fontPath='" + fontPath + '\'' +
				", status=" + status +
				", totalSize=" + totalSize +
				", progress=" + progress +
				", jsonStr='" + jsonStr + '\'' +
				", isBought=" + isBought +
				", fontZipPath='" + fontZipPath + '\'' +
				", fontFtfPath='" + fontFtfPath + '\'' +
				", freeBook=" + freeBook +
				'}';
	}
}
