package com.dangdang.reader.dread.dialog;

/**
 * Created by liuboyu on 2014/12/31.
 */
public class BuyMonthActivityInfo {
	private String activityId;
	private int monthlyBuyDays;
	private int monthlyPaymentOriginalPrice;
	private int monthlyPaymentPrice;
	private int monthlyPaymentDiscount;
	private String activityName;

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public int getMonthlyBuyDays() {
		return monthlyBuyDays;
	}

	public void setMonthlyBuyDays(int monthlyBuyDays) {
		this.monthlyBuyDays = monthlyBuyDays;
	}

	public int getMonthlyPaymentOriginalPrice() {
		return monthlyPaymentOriginalPrice;
	}

	public void setMonthlyPaymentOriginalPrice(int monthlyPaymentOriginalPrice) {
		this.monthlyPaymentOriginalPrice = monthlyPaymentOriginalPrice;
	}

	public int getMonthlyPaymentPrice() {
		return monthlyPaymentPrice;
	}

	public void setMonthlyPaymentPrice(int monthlyPaymentPrice) {
		this.monthlyPaymentPrice = monthlyPaymentPrice;
	}

	public int getMonthlyPaymentDiscount() {
		return monthlyPaymentDiscount;
	}

	public void setMonthlyPaymentDiscount(int monthlyPaymentDiscount) {
		this.monthlyPaymentDiscount = monthlyPaymentDiscount;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
}
