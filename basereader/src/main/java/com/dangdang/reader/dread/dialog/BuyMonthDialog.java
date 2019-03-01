package com.dangdang.reader.dread.dialog;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.format.part.PartBuyInfo;
import com.dangdang.reader.view.IDialog;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

public class BuyMonthDialog extends IDialog implements IBuyDialog {
	private TextView mNameTv;
	private TextView mOriginalPrice;
	private TextView mDiscountPrice;
	private TextView mBalanceTv;
	private TextView mBuyTv;
	private TextView mNotEnough;

	private LinearLayout mSelect1;
	private LinearLayout mSelect2;
	private LinearLayout mSelect3;
	private TextView mSelectText1;
	private TextView mSelectText2;
	private TextView mSelectText3;
	private TextView mSelectDiscount1;
	private TextView mSelectDiscount2;
	private TextView mSelectDiscount3;

	private BuyMonthlyShowVo mBuyInfo;
	private Handler mHandler;
	private String mSelectedActivityId;
	private boolean mEnough;

	private int mFrom;

	public BuyMonthDialog(Context context) {
		super(context, R.style.Dialog_NoBackground);
	}

	@Override
	public void onCreateD() {
		setContentView(R.layout.dialog_buy_month);
		initViews();
		mHandler = new MyHandler(this);
//		GetBuyMonthlyShowVoRequest request = new GetBuyMonthlyShowVoRequest(mHandler);
//		DDOriginalApp.getApplication().getRequestQueueManager().sendRequest(request, "");
	}

	private void initViews() {
		mNameTv = (TextView) findViewById(R.id.dialog_buy_month_name_tv);
		mOriginalPrice = (TextView) findViewById(R.id.dialog_buy_month_original_price);
		mOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		mDiscountPrice = (TextView) findViewById(R.id.dialog_buy_month_discount_price);
		mBalanceTv = (TextView) findViewById(R.id.dialog_buy_month_balance_tv);
		mBuyTv = (TextView) findViewById(R.id.dialog_buy_month_buy_month);
		mBuyTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBuy();
			}
		});
		mNotEnough = (TextView) findViewById(R.id.dialog_buy_month_not_enough);
		mSelect1 = (LinearLayout) findViewById(R.id.dialog_buy_month_select1);
		mSelect2 = (LinearLayout) findViewById(R.id.dialog_buy_month_select2);
		mSelect3 = (LinearLayout) findViewById(R.id.dialog_buy_month_select3);
		mSelectDiscount1 = (TextView) findViewById(R.id.dialog_buy_month_select1_discount);
		mSelectDiscount2 = (TextView) findViewById(R.id.dialog_buy_month_select2_discount);
		mSelectDiscount3 = (TextView) findViewById(R.id.dialog_buy_month_select3_discount);
		mSelect1.setSelected(true);
		View.OnClickListener mSelectListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clearSelected();
				BuyMonthActivityInfo activityInfo = null;
				int i = v.getId();
				if (i == R.id.dialog_buy_month_select1) {
					mSelect1.setSelected(true);
					activityInfo = mBuyInfo.getActivityInfos().get(0);

				} else if (i == R.id.dialog_buy_month_select2) {
					mSelect2.setSelected(true);
					activityInfo = mBuyInfo.getActivityInfos().get(1);

				} else if (i == R.id.dialog_buy_month_select3) {
					mSelect3.setSelected(true);
					activityInfo = mBuyInfo.getActivityInfos().get(2);

				} else {
				}
				if (activityInfo != null) {
					setNameAndPrice(activityInfo);
					mSelectedActivityId = activityInfo.getActivityId();
				}
			}
		};
		mSelect1.setOnClickListener(mSelectListener);
		mSelect2.setOnClickListener(mSelectListener);
		mSelect3.setOnClickListener(mSelectListener);
		mSelectText1 = (TextView) findViewById(R.id.dialog_buy_month_select1_text);
		mSelectText2 = (TextView) findViewById(R.id.dialog_buy_month_select2_text);
		mSelectText3 = (TextView) findViewById(R.id.dialog_buy_month_select3_text);
		findViewById(R.id.dialog_buy_month_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mFrom == BuyDialogManager.DIALOG_BUY_CHAPTER)
					BuyDialogManager.getInstance().getBuyChapterDialog().show();
				else if (mFrom == BuyDialogManager.DIALOG_BUY_FULL)
					BuyDialogManager.getInstance().getBuyFullDialog().show();
				dismiss();
			}
		});
		findViewById(R.id.dialog_buy_month_close).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendCancelBroadcast();
				dismiss();
			}
		});
	}

	private void sendCancelBroadcast() {
//		Intent intent = new Intent(Constants.BROADCAST_BUY_DIALOG_CANCEL);
//		mContext.sendBroadcast(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK: {
			if (mFrom == BuyDialogManager.DIALOG_BUY_CHAPTER)
				BuyDialogManager.getInstance().getBuyChapterDialog().show();
			else if (mFrom == BuyDialogManager.DIALOG_BUY_FULL)
				BuyDialogManager.getInstance().getBuyFullDialog().show();
			else
				sendCancelBroadcast();
			dismiss();
			return true;
		}
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void clearSelected() {
		mSelect1.setSelected(false);
		mSelect2.setSelected(false);
		mSelect3.setSelected(false);
	}

	@Override
	public void setBuyInfo(PartBuyInfo info, boolean isPre) {

	}

	@Override
	public void setBalanceInfo(int main, int sub,int type) {
		if (mBuyInfo == null)
			return;
		if (main != -1) {
			 if(type==0){
				mBuyInfo.setMainBalance(main+mBuyInfo.getMainBalance());
				mBuyInfo.setSubBalance(sub+mBuyInfo.getSubBalance());
			}else{
				mBuyInfo.setMainBalance(main);
				mBuyInfo.setSubBalance(sub);
			}
		} else {
			mBuyInfo.setSubBalance(mBuyInfo.getSubBalance() + sub);
		}

		StringBuilder balance = new StringBuilder();
		balance.append(mContext.getText(R.string.gold));
		balance.append(mBuyInfo.getMainBalance());
		balance.append(" | ");
		balance.append(mContext.getText(R.string.gold));
		balance.append(mBuyInfo.getSubBalance());
		mBalanceTv.setText(balance.toString());

		setBuyInfo();
	}

	private static class MyHandler extends Handler {
		private final WeakReference<BuyMonthDialog> mFragmentView;

		MyHandler(BuyMonthDialog view) {
			this.mFragmentView = new WeakReference<BuyMonthDialog>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			BuyMonthDialog service = mFragmentView.get();
			if (service != null) {
				super.handleMessage(msg);
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class InfoHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
//			ResultExpCode expCode = null;
//			switch (msg.what) {
//			case RequestMsgWhatConstants.MSG_WHAT_GET_BUY_MONTHLY_SHOW_SUCCESS:
//				BuyMonthlyShowVo info = (BuyMonthlyShowVo) msg.obj;
//				if (info != null) {
//					mBuyInfo = info;
//					setBalanceInfo(info.getMainBalance(), info.getSubBalance());
//				}
//				break;
//			case RequestMsgWhatConstants.MSG_WHAT_GET_BUY_MONTHLY_SHOW_FAIL:
//				if (msg.arg1 == StatusCode.NEED_LOGIN) {
//					Intent intent = new Intent(mContext, LoginActivity.class);
//					mContext.startActivity(intent);
//					return;
//				}
//				if (msg.obj instanceof ResultExpCode)
//					expCode = (ResultExpCode) msg.obj;
//				if (expCode == null || StringUtil.isEmpty(expCode.getResultErrorMessage()))
//					showToast(R.string.get_buy_month_message_fail);
//				else
//					showToast(expCode.getResultErrorMessage());
//				dismiss();
//				break;
//			case RequestMsgWhatConstants.MSG_WHAT_BUY_MEDIA_SUCCESS:
//				showToast(R.string.monthly_buy_success);
//				dismiss();
//				break;
//			case RequestMsgWhatConstants.MSG_WHAT_BUY_MEDIA_FAIL:
//				if (msg.obj instanceof ResultExpCode)
//					expCode = (ResultExpCode) msg.obj;
//				if (expCode == null || StringUtil.isEmpty(expCode.getResultErrorMessage()))
//					showToast(R.string.monthly_buy_fail);
//				else
//					showToast(expCode.getResultErrorMessage());
//				dismiss();
//				break;
//			default:
//				break;
//			}
		}
	}

	private void setBuyInfo() {
		BuyMonthActivityInfo activityInfo = mBuyInfo.getActivityInfos().get(0);
		mSelect1.setTag(activityInfo);
		mSelectText1.setText(String.valueOf(activityInfo.getMonthlyBuyDays() / 30));
		mNameTv.setText(activityInfo.getActivityName());
		setDiscount(mSelectDiscount1, activityInfo.getMonthlyPaymentPrice(), activityInfo.getMonthlyPaymentDiscount());

		setNameAndPrice(activityInfo);
		mSelectedActivityId = activityInfo.getActivityId();

		activityInfo = mBuyInfo.getActivityInfos().get(1);
		mSelect2.setTag(activityInfo);
		mSelectText2.setText(String.valueOf(activityInfo.getMonthlyBuyDays() / 30));
		setDiscount(mSelectDiscount2, activityInfo.getMonthlyPaymentPrice(), activityInfo.getMonthlyPaymentDiscount());
		activityInfo = mBuyInfo.getActivityInfos().get(2);
		mSelect3.setTag(activityInfo);
		mSelectText3.setText(String.valueOf(activityInfo.getMonthlyBuyDays() / 30));
		setDiscount(mSelectDiscount3, activityInfo.getMonthlyPaymentPrice(), activityInfo.getMonthlyPaymentDiscount());
	}

	private void setDiscount(TextView textView, int price, int discount) {
		if (discount == 10) {
			String priceStr = String.valueOf(price) + mContext.getString(R.string.gold);
			textView.setText(priceStr);
		} else {
			String priceStr;
			if (price > 10000) {
				float temp = price / 10000f;
				DecimalFormat df = new DecimalFormat("#0.0");
				priceStr = df.format(temp) + mContext.getString(R.string.gold) + " ";
			} else {
				priceStr = String.valueOf(price) + mContext.getString(R.string.gold) + " ";
			}
			String discountStr = String.valueOf(discount) + mContext.getString(R.string.gold);
			SpannableStringBuilder style = new SpannableStringBuilder(priceStr + discountStr);
			style.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.yellow_ff9939)), priceStr.length(),
					priceStr.length() + discountStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			style.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)), priceStr.length(), priceStr.length()
					+ discountStr.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			textView.setText(style);
		}
	}

	private void setNameAndPrice(BuyMonthActivityInfo activityInfo) {
		mNameTv.setText(activityInfo.getActivityName());
		if (activityInfo.getMonthlyPaymentOriginalPrice() != activityInfo.getMonthlyPaymentPrice()) {
			String priceStr = String.valueOf(activityInfo.getMonthlyPaymentOriginalPrice());
			mOriginalPrice.setVisibility(View.VISIBLE);
			mOriginalPrice.setText(priceStr);
		} else {
			mOriginalPrice.setVisibility(View.GONE);
		}
		mDiscountPrice.setText(activityInfo.getMonthlyPaymentPrice() + mContext.getString(R.string.silver));
		if (mBuyInfo.getMainBalance() < activityInfo.getMonthlyPaymentPrice()) {
			mNotEnough.setVisibility(View.VISIBLE);
			mNotEnough.setText(mContext.getString(R.string.buy_dialog_buy_month_not_enough)
					+ (activityInfo.getMonthlyPaymentPrice() - mBuyInfo.getMainBalance()) + mContext.getString(R.string.silver));
			mBuyTv.setText(R.string.personal_exchange);
			mEnough = false;
		} else {
			mNotEnough.setVisibility(View.GONE);
			mBuyTv.setText(R.string.buy_dialog_buy_month);
			mEnough = true;
		}
	}

	protected void onBuy() {
//		if (mEnough) {
//			BuyMediaRequest request = new BuyMediaRequest(mSelectedActivityId, mHandler);
//			DDOriginalApp.getApplication().getRequestQueueManager().sendRequest(request, "");
//		} else {
//			Intent intent = new Intent(mContext, PersonalRechargeActivity.class);
//			mContext.startActivity(intent);
//		}
	}

	public void setFrom(int from) {
		mFrom = from;
	}
}
