package com.dangdang.reader.cloud;

import java.util.List;

import org.json.JSONArray;

import com.dangdang.reader.dread.data.BookMark;

public class MarkData {

	private JSONArray marksJson;
	private List<BookMark> marks;
	
	public JSONArray getMarksJson() {
		return marksJson;
	}
	public void setMarksJson(JSONArray marksJson) {
		this.marksJson = marksJson;
	}
	public List<BookMark> getMarks() {
		return marks;
	}
	public void setMarks(List<BookMark> marks) {
		this.marks = marks;
	}
	
	public String getMarksString(){
		String str = "";
		if(!isJsonEmpty()){
			str = marksJson.toString();
		}
		return str;
	}
	
	public boolean isJsonEmpty(){
		return marksJson == null || marksJson.length() == 0;
	}
	
	public boolean isListEmpty(){
		return marks == null || marks.size() == 0;
	}
	
}
