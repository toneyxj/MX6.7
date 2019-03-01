package com.dangdang.reader.dread.config;

/**
 * @author luxu
 */
public class ComposingFactor implements IFactor {

	/**
	 * 排版区域
	 */
	private int width;
	private int height;
	
	/**
	 * 页边距
	 */
	private float paddingLeft;
	private float paddingTop;
	private float paddintRight;
	private float paddingBottom;
	
	/**
	 * 行间距
	 */
	private float lineSpacing;
	/**
	 * 段间距
	 */
	private float paragraphSpacing;
	/**
	 * 首行缩进
	 */
	private float firstLineIndent;
	
	/**
	 * 字体大小 即每行多少字
	 */
	private float fontSize;//== fontSize
	
	private int lineWord;
	
	/**
	 * 字体
	 */
	private String font;
	
	
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public float getPaddingLeft() {
		return paddingLeft;
	}

	public void setPaddingLeft(float paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	public float getPaddingTop() {
		return paddingTop;
	}

	public void setPaddingTop(float paddingTop) {
		this.paddingTop = paddingTop;
	}

	public float getPaddintRight() {
		return paddintRight;
	}

	public void setPaddintRight(float paddintRight) {
		this.paddintRight = paddintRight;
	}

	public float getPaddingBottom() {
		return paddingBottom;
	}

	public void setPaddingBottom(float paddingBottom) {
		this.paddingBottom = paddingBottom;
	}

	public float getLineSpacing() {
		return lineSpacing;
	}

	public void setLineSpacing(float lineSpacing) {
		this.lineSpacing = lineSpacing;
	}

	public float getParagraphSpacing() {
		return paragraphSpacing;
	}

	public void setParagraphSpacing(float paragraphSpacing) {
		this.paragraphSpacing = paragraphSpacing;
	}

	public float getFirstLineIndent() {
		return firstLineIndent;
	}

	public void setFirstLineIndent(float firstLineIndent) {
		this.firstLineIndent = firstLineIndent;
	}

	public float getFontSize() {
		return fontSize;
	}

	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}
	
	public int getLineWord() {
		return lineWord;
	}

	public void setLineWord(int lineWord) {
		this.lineWord = lineWord;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String uniqueId(){
		return String.valueOf(toString().hashCode());
	}
	
	@Override
	public String toString() {
		StringBuffer sbs = new StringBuffer();
		sbs.append("area[" + getWidth() + "-" + getHeight()+"],");
		sbs.append("padLTRB[" + getPaddingLeft() + "," + getPaddingTop() + "," + getPaddintRight() + "," + getPaddingBottom() +"],");
		sbs.append("lineSp[" + getLineSpacing() + "],");
		sbs.append("paragSp[" + getParagraphSpacing() + "],");
		sbs.append("fLIndent[" + getFirstLineIndent() + "],");
		sbs.append("fontsize[" + getFontSize() + "],");
		sbs.append("font[" + getFont() + "]");
		return sbs.toString();
	}
	
}
