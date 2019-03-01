package com.dangdang.reader.dread.jni;

import android.text.TextUtils;

import com.dangdang.reader.dread.data.ParagraphText;
import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;
import com.dangdang.zframework.log.LogM;

/**
 * @author luxu
 */
public class ParagraphTextHandler {

	private String mText = "";
	private int mStartEmtIndex;
	private int mEndEmtIndex;
	
	
	public void onParagraphText(String text, int startEmtIndex, int endEmtIndex){
		mText = text;
		mStartEmtIndex = startEmtIndex;
		mEndEmtIndex = endEmtIndex;
		
		if(isIllegality()){
			StringBuffer sb = new StringBuffer("[");
			sb.append(text);
			sb.append("][");
			sb.append(mStartEmtIndex + "-" + mEndEmtIndex);
			sb.append("]");
			
			pringLog(" onParagraphText: " + sb);
		}
	}
	
	private boolean isIllegality(){
		return TextUtils.isEmpty(mText) 
				|| mStartEmtIndex < 0 || mEndEmtIndex < 0 
				|| (mStartEmtIndex == 0 && mEndEmtIndex == 0);
	}

	public ParagraphText getParagraphText(){
		
		ParagraphText paragText = new ParagraphText();
		paragText.setText(mText);
		paragText.setStartEmtIndex(new ElementIndex(mStartEmtIndex));
		paragText.setEndEmtIndex(new ElementIndex(mEndEmtIndex));
		
		return paragText;
	}
	
	protected void pringLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
	protected void pringLogE(String log){
		LogM.e(getClass().getSimpleName(), log);
	}
	
}
