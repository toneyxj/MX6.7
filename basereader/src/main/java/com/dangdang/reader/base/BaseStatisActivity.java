package com.dangdang.reader.base;

import com.dangdang.reader.statis.StatisEventId;

public abstract class BaseStatisActivity extends BaseReaderActivity {

	@Override
	protected boolean isStatisDDClick() {
		return true;
	}

	@Override
	public void onStart() {
		super.onStart();
		printLog(" onStart ");

		if (isStatisDDClick()) {
			startStatis();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (isStatisDDClick()) {
			stopStatis();
		}
	}

	protected void startStatis() {
		if (mDDClickHandle == null) {
			printLogE(" act start ClickHandle is null ");
			return;
		}
		mDDClickHandle.start();
	}

	protected void stopStatis() {
		if (mDDClickHandle == null) {
			printLogE(" act stop ClickHandle is null ");
			return;
		}
		String pageInfo = "";
		mDDClickHandle.stop(this, pageInfo);
	}

	/**
	 * 增加一个ddclick埋点统计
	 * 
	 * @param eventId
	 *            事件id, 如打开应用，点击购买等
	 * @param pageInfo
	 *            页标识等 k=v的格式；记录当前页类型和id，例如单品页：pid=xxx；
	 * @param linkUrl
	 *            去向页面标识；格式是cms字典格式：type://key=value；
	 * @param expandField
	 *            如记录楼层-坑位信息和其他扩展字段；key=value的格式，不同k=v之间用#分隔；
	 */
	public void addStatis(StatisEventId eventId, String pageInfo,
			String linkUrl, String expandField) {
		if (mDDClickHandle == null) {
			printLogE(" act addStatis ClickHandle is null ");
			return;
		}
		mDDClickHandle.addStatis(this, eventId, pageInfo, linkUrl, expandField);
	}

}
