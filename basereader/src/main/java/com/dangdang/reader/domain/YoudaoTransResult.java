package com.dangdang.reader.domain;

import java.util.ArrayList;

import android.text.TextUtils;

/**
 * Created by liuboyu on 2014/10/22.
 */
public class YoudaoTransResult {
	private String query;
	private String phonetic;

	private ArrayList<String> translations = new ArrayList<String>();
	private ArrayList<String> basicExplains = new ArrayList<String>();
	private ArrayList<String> webExplains = new ArrayList<String>();

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public ArrayList<String> getTranslations() {
		return translations;
	}

	public void addTranslation(String translation) {
		this.translations.add(translation);
	}

	public ArrayList<String> getBasicExplains() {
		return basicExplains;
	}

	public void addBasicExplains(String explain) {
		this.basicExplains.add(explain);
	}

	public ArrayList<String> getWebExplains() {
		return webExplains;
	}

	public void addWebExplains(String explain) {
		this.webExplains.add(explain);
	}

	public String getPhonetic() {
		return phonetic;
	}

	public void setPhonetic(String phonetic) {
		this.phonetic = phonetic;
	}

	public String getViewString() {
		if(TextUtils.isEmpty(phonetic) && basicExplains.size() == 0){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(phonetic).append("]\n");
		int size = basicExplains.size();
		for (int i = 0; i < size; i++) {
			sb.append("(").append(i + 1).append(")");
			sb.append(basicExplains.get(i));
			if(i != size - 1){
				sb.append("；");
				sb.append("\n");
			} else {
				sb.append("。");
			}
		}
		return sb.toString();
	}
}
