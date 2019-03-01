package com.dangdang.reader.dread.holder;

import android.content.Context;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ParserStatus;

public class PromptResource {

	private static PromptResource mInstance = null;
	
	private String parserHtmlFailed;
	private String deCryptFailed;
	private String fileNoExist;
	private String loadingPrompt;
	private String fullBookLastTip;
	private String tryBookLastTip;
	private String trainingLastTip;

	public final static int FirstPagePrompt = R.string.reader_firstpage;;
	public final static int LastPagePrompt = R.string.reader_lastpage;;
	public final static int ParserHtmlFailed = R.string.parser_failed;
	public final static int DeCryptFailed = R.string.decrypt_failed;
	public final static int FileNoExist = R.string.file_not_exist;
	public final static int LoadingPrompt = R.string.read_loading;//
	
	public final static int FullBookLastTip = R.string.reader_fullbook_ttsfinish_tip;
	public final static int TryBookLastTip = R.string.reader_trybook_ttsfinish_tip;
	public final static int TrainingBookLastTip = R.string.reader_training_ttsfinish_tip;

	private PromptResource(){
	}
	
	public synchronized static PromptResource getInstance(){
		if(mInstance == null){
			mInstance = new PromptResource();
		}
		return mInstance;
	}
	
	public void initResource(Context context){
		
		parserHtmlFailed = context.getString(ParserHtmlFailed);
		deCryptFailed = context.getString(DeCryptFailed);
		fileNoExist = context.getString(FileNoExist);
		loadingPrompt = context.getString(LoadingPrompt);
		fullBookLastTip = context.getString(FullBookLastTip);
		tryBookLastTip = context.getString(TryBookLastTip);
		trainingLastTip = context.getString(TrainingBookLastTip);
	}
	
	public String getLoadingPrompt(){
		return loadingPrompt;
	}
	
	public String getFullBookLastTip() {
		return fullBookLastTip;
	}

	public String getTryBookLastTip() {
		return tryBookLastTip;
	}

	public String getTrainingLastTip() {
		return trainingLastTip;
	}

	public String switchPrompt(int status){
		
		String p = parserHtmlFailed;
		switch (status) {
		case ParserStatus.C_HTML_ERROR:
			p = parserHtmlFailed;
			break;

		case ParserStatus.C_DECTYPT_ERROR:
			p = deCryptFailed;
			break;
		case ParserStatus.C_FILENOEXIST_ERROR:
			p = fileNoExist;
			break;
		}
		
		return p;
	}

	
}
