package com.dangdang.reader.dread.holder;

import android.content.Context;

import com.dangdang.reader.dread.service.MarkService;
import com.dangdang.reader.dread.service.NoteService;

public class ServiceManager {

	
	private MarkService markService;
	private NoteService noteService;
	
	public ServiceManager(Context context){
		
		markService = new MarkService(context);
		noteService = new NoteService(context);
	}

	public MarkService getMarkService() {
		return markService;
	}

	public NoteService getNoteService() {
		return noteService;
	}
	
	public void destory(){
		markService.closeDB();
		noteService.closeDB();
	}
	
}
