package com.dangdang.reader.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.core.epub.IGlobalWindow;

public abstract class BottomDialog {

	private static final int GRAVITY_DEFAULT = Gravity.BOTTOM;
	private static final int WIDTH_DEFAULT = ViewGroup.LayoutParams.MATCH_PARENT;


	private int mStyle;
	protected Dialog mDialog;
	protected IGlobalWindow.IOnDisMissCallBack mOnDismissCallBack;

	public BottomDialog(){
	}

	public BottomDialog(Context context, View view) {
		init(context, view, GRAVITY_DEFAULT, WIDTH_DEFAULT);
		setListener(view);
	}

	public BottomDialog(Context context, View view, int gravity, int width) {
		init(context, view, gravity, width);
		setListener(view);
	}
	
	protected abstract void setListener(View view);

	protected void init(Context context, View view){
		init(context, view, GRAVITY_DEFAULT, WIDTH_DEFAULT);
	}
	
	protected void init(Context context, View view, int gravity, int width) {
		if (context == null || view == null) {
			return;
		}
		setAnimation();
		// 创建dialog弹窗
		mDialog = new Dialog(context, R.style.transparentFrameWindowStyle);
		mDialog.setContentView(view, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		Window window = mDialog.getWindow();

		// 设置显示动画
		window.setWindowAnimations(mStyle);
		window.setGravity(gravity);
		WindowManager.LayoutParams wl = window.getAttributes();

		wl.x = 0;
		wl.y = 0;
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = width;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

		// 设置显示位置
		mDialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		mDialog.setCanceledOnTouchOutside(true);

		mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(mOnDismissCallBack!=null){
					mOnDismissCallBack.onDismissCallBack();
				}
				destroy();
			}
		});
		
		setListener(view);
	}

	public void show() {
		if (mDialog != null) {
			mDialog.show();
		}
	}

	public void dismiss() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	public boolean isShow() {
		if (mDialog != null) {
			return mDialog.isShowing();
		}
		return false;
	}

	public void setOnDismissListener(IGlobalWindow.IOnDisMissCallBack callBack){
		mOnDismissCallBack = callBack;
	}

	private void setAnimation(){
		if(getStyle() > 0){
			mStyle = getStyle();
		}else if(getStyle() == 0){
			mStyle = 0;
		}else{
			mStyle = R.style.popwindow_anim_style;
		}
	}

	/**
	 *
	 * @return 默认小于0是默认动画，大于0是自定义弹窗动画，等于0是不要动画
	 */
	protected int getStyle(){
		return -1;
	}

	protected void  destroy(){

	}

}
