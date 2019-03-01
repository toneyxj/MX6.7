package com.dangdang.reader.cloud;

import java.util.List;

import org.json.JSONArray;

import com.dangdang.reader.dread.data.BookNote;

public class NoteData {
	
	private JSONArray notesJson;
	private List<BookNote> notes;
	
	public JSONArray getNotesJson() {
		return notesJson;
	}
	
	public void setNotesJson(JSONArray notesJson) {
		this.notesJson = notesJson;
	}
	
	public String getNotesString(){
		String str = "";
		if(!isJsonEmpty()){
			str = notesJson.toString();
		}
		return str;
	}
	
	public List<BookNote> getNotes() {
		return notes;
	}
	
	public void setNotes(List<BookNote> notes) {
		this.notes = notes;
	}

	public boolean isJsonEmpty(){
		return notesJson == null || notesJson.length() == 0;
	}
	
	public boolean isListEmpty(){
		return notes == null || notes.size() == 0;
	}
	
}
