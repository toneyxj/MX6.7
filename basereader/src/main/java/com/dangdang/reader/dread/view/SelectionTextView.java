package com.dangdang.reader.dread.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.dangdang.reader.R;
import com.dangdang.zframework.view.DDTextView;

public class SelectionTextView extends LinearLayout {

	
	private onPositionListener mPositionListener;
	
	public SelectionTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SelectionTextView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		
		setOrientation(HORIZONTAL);
		
//		initTexts();
	}

	public void initTexts(int[] texts) {
		
		LayoutInflater flater = LayoutInflater.from(getContext());
		
		for(int i = 0, len = texts.length; i < len; i++){
			DDTextView textView = (DDTextView)flater.inflate(R.layout.read_selectiontext, null);
			textView.setText(texts[i]);
			textView.setTag(i);
			textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
			textView.setOnClickListener(mClickListener);
			addView(textView);
		}
//		setSelection(0);
	}
	
	final OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final int index = (Integer) v.getTag();
			setSelection(index);
			if(mPositionListener != null){
				mPositionListener.onPosition(index);
			}
		}
	};

	public void setSelection(int index){
		
		final int ct = getChildCount();
		for(int i = 0; i < ct; i++){
			View view = getChildAt(i);
			if(i == index){
				view.setBackgroundResource(R.drawable.read_menu_selectiontext_white);
			} else {
				view.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
			}
			view.setSelected(i == index);
		}
		
	}
	
	public void setOnPositionListener(onPositionListener l){
		mPositionListener = l;
	}
	

	public interface onPositionListener {
		
		public void onPosition(int position);
		
	}
	
}
