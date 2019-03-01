package com.dangdang.reader.dread.dialog;

import java.util.List;

/**
 * Created by liuboyu on 2014/12/31.
 */
public class BuyMonthlyShowVo {
	private int mainBalance;
	private int subBalance;
	private List<BuyMonthActivityInfo> activityInfos;

	public int getMainBalance() {
		return mainBalance;
	}

	public void setMainBalance(int mainBalance) {
		this.mainBalance = mainBalance;
	}

	public int getSubBalance() {
		return subBalance;
	}

	public void setSubBalance(int subBalance) {
		this.subBalance = subBalance;
	}

	public List<BuyMonthActivityInfo> getActivityInfos() {
		return activityInfos;
	}

	public void setActivityInfos(List<BuyMonthActivityInfo> activityInfos) {
		this.activityInfos = activityInfos;
	}
}
