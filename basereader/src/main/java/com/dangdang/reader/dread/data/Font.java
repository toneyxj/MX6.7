package com.dangdang.reader.dread.data;

public class Font {

	private boolean isDefault;
	private String fontName;
	private String fontPath;
	private String charset;
	
	public boolean isDefault() {
		return isDefault;
	}
	
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	public String getFontName() {
		if(fontName == null){
			fontName = "";
		}
		return fontName;
	}
	
	public void setFontName(String fontName) {
		this.fontName = fontName;
	}
	
	public String getFontPath() {
		return fontPath;
	}
	
	public void setFontPath(String fontPath) {
		this.fontPath = fontPath;
	}
	
	public String getCharset() {
		if(charset == null){
			charset = "";
		}
		return charset;
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
}
