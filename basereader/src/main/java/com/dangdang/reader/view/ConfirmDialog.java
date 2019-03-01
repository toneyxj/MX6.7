package com.dangdang.reader.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.personal.domain.GroupItem;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.zframework.utils.DeviceUtil;

public class ConfirmDialog {
	
	private OnClickListener mListener;
	private Dialog dialog;
	private CheckBox mCheck;
	private GroupItem mGroupItem;
	private ShelfBook mBook;
	private boolean isFile;
	
	public ConfirmDialog(){
	}
	
	private void getDialog(Activity activity){
		dialog = new Dialog(activity, R.style.deleteDialog);
		dialog.setContentView(R.layout.delete_confirm_dialog);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  
		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();      
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();   
		lp.width = (int)(DeviceUtil.getInstance(activity).getDisplayWidth() * 0.8);
		lp.y = -50; // 新位置Y坐标        
		dialogWindow.setAttributes(lp);
	}
	
	public void setOnClickListener(OnClickListener l){
		mListener = l;
	}
	
	public void showConfirmDialog(boolean isFile, GroupItem item, ShelfBook book, Activity activity){
		this.isFile = isFile;
		if(dialog == null || dialog.getOwnerActivity() == null || dialog.getOwnerActivity().isFinishing())
			getDialog(activity);
		mCheck = (CheckBox)dialog.findViewById(R.id.check);
		if(!isFile){
			mGroupItem = item;
			((TextView)dialog.findViewById(R.id.tip)).setText(R.string.delete_group_tip);
			mCheck.setText(R.string.delete_group);
		}else{
			mBook = book;
			mCheck.setVisibility(View.GONE);
		}
		dialog.findViewById(R.id.delete_left_btn).setOnClickListener(mListener);
		dialog.findViewById(R.id.delete_right_btn).setOnClickListener(mListener);
		dialog.show();		
	}
	
	public void dismiss(){
		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
			mListener = null;
			dialog = null;
		}
	}
	
	public boolean deleteFile(){
		return mCheck.isChecked();
	}
	
	public GroupItem getGroupItem(){
		return mGroupItem;
	}
	
	public ShelfBook getBook(){
		return mBook;
	}
	
	public boolean isFile(){
		return isFile;
	}
}
