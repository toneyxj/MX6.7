package com.dangdang.reader.dread.function;

import android.content.Context;
import android.widget.Toast;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.zframework.utils.UiUtil;
import com.dangdang.zframework.view.DDTextView;

public class ShowToastFunction extends MFunctionImpl {
	
	
	public ShowToastFunction(BaseReaderApplicaion app) {
		super(app);
	}

	protected void runFunction(Object... params) {
		
		if(params != null && params.length > 0){

			try {
				Object param =  params[0];
				if (param instanceof String){
					final String msg = (String) param;
					showToast(mApplication.getContext(), msg);
				}else{
					final int resid = (Integer) param;
					showToast(mApplication.getContext(), resid);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

    private Toast mToast;
    private DDTextView mTextView;
    private void showToast(Context context, int resid) {
       showToast(context,context.getString(resid));
    }
    private void showToast(Context context, String msg) {
        if(mToast == null||mTextView==null){
            mToast =  new Toast(context);
            mTextView = new DDTextView(context);
            mTextView.setGravity(17);
            mTextView.setTextColor(context.getResources().getColor(R.color.white));
            try {
                mTextView.setBackgroundResource(R.drawable.toast_frame);
            } catch (Throwable var6) {
                var6.printStackTrace();
            }

            mToast.setView(mTextView);
            mTextView.setText(msg);
        } else {
            mToast.setDuration(Toast.LENGTH_SHORT);
            mTextView.setText(msg);
        }
        mToast.show();
    }
	
}
