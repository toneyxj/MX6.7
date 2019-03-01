package com.dangdang.reader.dread.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.dangdang.reader.R;


public class ExitReadDialog extends Dialog {
	private LinearLayout mExitRootLl;
	private Context mContext;

	public ExitReadDialog(Context context) {
		super(context, R.style.dialog_transbg);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		setContentView(R.layout.dialog_exit_read);
		mExitRootLl = (LinearLayout) findViewById(R.id.dialog_exit_read_root_ll);
		setCanceledOnTouchOutside(true);
	}

	public void setListener(View.OnClickListener clickListener) {
		// 触摸外部，消失
		LinearLayout cancelLl = (LinearLayout) findViewById(R.id.dialog_exit_read_cancel_ll);
		cancelLl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		findViewById(R.id.dialog_exit_read_ok_btn).setOnClickListener(clickListener);
		findViewById(R.id.dialog_exit_read_cancel_btn).setOnClickListener(clickListener);
	}

	@Override
	public void dismiss() {
		super.dismiss();
		mExitRootLl.clearAnimation();
	}

	@Override
	public void show() {
		Window window = getWindow();
		window.setWindowAnimations(R.style.style_popup_alpha_anim);
		window.setGravity(Gravity.TOP);
		window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		lp.y = -50;
		super.show();
		mExitRootLl.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_trans_in_b2t_300));
	}
}
