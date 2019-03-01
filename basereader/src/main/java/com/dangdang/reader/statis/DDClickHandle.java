package com.dangdang.reader.statis;

import android.content.Context;

import com.dangdang.zframework.log.LogM;

/**
 * @author luxu
 */
public class DDClickHandle {

	// private long statisTime = 0;
	private long pageStartTime = 0;
	private Context context;

	/**
	 * ddClick开关
	 */
	public final static boolean ddClickSwitch = true;

	public DDClickHandle(Context context) {
		if (context != null) {
			this.context = context.getApplicationContext();
		}
	}

	protected DDClickStatisService getStatisService() {
		return DDClickStatisService.getStatisService(context);
	}

	public void start() {
		pageStartTime = System.currentTimeMillis();
	}

	public void stop(Object module, String pageInfo) {
		if (!ddClickSwitch) {
			printLog(" ddClickSwitch false ");
			return;
		}
		if (module == null) {
			printLogE(" ddclick stop module == null");
			return;
		}
		if (pageStartTime <= 0) {
			printLogE(" ddclick stop pageStartTime=" + pageStartTime);
			return;
		}
		if (context == null) {
			printLogE(" ddclick stop context == null");
			return;
		}
		try {
			long pageStayTime = System.currentTimeMillis() - pageStartTime;
			DDClickStatisService statisService = getStatisService();
			final int pageId = getPageId(module.getClass());
			final StatisEventId event = StatisEventId.E_VISITPAGE;
			printLog(" onStop statisTime=" + pageStayTime + "," + module + ",["
					+ pageId);
			statisService.addStatis(pageId, event, pageInfo, pageStayTime, "",
					"");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getPageId(Class<?> clazz) {

		int pageId = StatisPageId.SHELF_MAIN;
		// if (clazz == GuideActivity.class) {
		// pageId = StatisPageId.START_PAGE;
		// } else if (clazz == BookShelfFragment.class
		// || clazz == BookShelfMenuActivity.class) {
		// pageId = StatisPageId.SHELF_MAIN;
		// } else if (clazz == BookShelfBoughtFragment.class) {
		// pageId = StatisPageId.SHELF_BOUGHT;
		// } else if (clazz == BookShelfBorrowFragment.class) {
		// pageId = StatisPageId.SHELF_BORROW;
		// } else if (clazz == BookShelfSearchActivity.class) {
		// pageId = StatisPageId.SHELF_SEARCH;
		// } else if (clazz == BookShelfNearbyActivity.class) {
		// pageId = StatisPageId.SHELF_NEARBY;
		// } else if (clazz == BookShelfOtherActivity.class
		// || clazz == BookShelfHisGroupActivity.class) {
		// pageId = StatisPageId.SHELF_TASHELF;
		// } else if (clazz == FileQuickScanActivity.class) {
		// pageId = StatisPageId.SHELF_IMPORT_FAST;
		// } else if (clazz == FileBrowserActivity.class) {
		// pageId = StatisPageId.SHELF_IMPORT_LOCAL;
		// } else if (clazz == WifiActivity.class) {
		// pageId = StatisPageId.SHELF_WIFI_TRANS;
		// } else if (clazz == FileScanResultActivity.class) {
		// pageId = StatisPageId.SHELF_SEARCH_RESULT;
		// } else if (clazz == BookShelfSelectGroupActivity.class) {
		// pageId = StatisPageId.SHELF_SELECTGROUP;
		// } else if (clazz == BookShelfGroupActivity.class) {
		// pageId = StatisPageId.SHELF_UNGROUP;
		// } else if (clazz == BookShelfHideBookActivity.class) {
		// pageId = StatisPageId.SHELF_HIDEBOOK;
		// } else if (clazz == BookShelfStealActivity.class) {
		// pageId = StatisPageId.SHELF_STEAL;
		// }
		//
		// /**
		// * 书架
		// *****************************************
		// * 书城
		// */
		//
		// else if (clazz == BookListFragment.class) {
		// pageId = StatisPageId.STORE_BOOKLIST;
		// } else if (clazz == FragmentGroup.class) {
		// pageId = StatisPageId.STORE_RECOMMAND;
		// } else if (clazz == DissertationFragment.class
		// || clazz == HistoryDissertationFragment.class) {
		// pageId = StatisPageId.STORE_SPECIAL;
		// } else if (clazz == SearchFragment.class) {
		// pageId = StatisPageId.STORE_SEARCH;
		// } else if (clazz == BookDetailFragment.class) {
		// pageId = StatisPageId.STORE_DETAIL;
		// } else if (clazz == BookStoreDetailCatalogFragment.class) {
		// pageId = StatisPageId.STORE_DIRLIST;
		// } else if (clazz == ShoppingCartActivity.class) {
		// pageId = StatisPageId.STORE_SHOPCART;
		// } else if (clazz == BookStoreOneKeyPayActivity.class) {
		// pageId = StatisPageId.STORE_PAYLIST;
		// } else if (clazz == ThreePayIndentActivity.class) {
		// pageId = StatisPageId.STORE_PAYRESULT;
		// } else if (clazz == PromotionalActActivity.class) {
		// pageId = StatisPageId.STORE_PROMOTIONAL;
		// } else if (clazz == NightStoreGuideActivity.class) {
		// pageId = StatisPageId.STORE_NIGHTGUIDE;
		// } else if (clazz == NightStoreActivity.class) {
		// pageId = StatisPageId.STORE_NIGHTLIST;
		// } else if (clazz == BookStoreGetCouponActivity.class) {
		// pageId = StatisPageId.STORE_GETCOUPON;
		// } else if (clazz == BookAuthorRelationFragment.class) {
		// pageId = StatisPageId.STORE_AUTHORRELA;
		// } else if (clazz == BSCategoryFragment.class) {
		// pageId = StatisPageId.STORE_CATEGORY_BOOKLIST;
		// }
		//
		// /**
		// * 书城
		// *****************************************
		// * 书评
		// */
		// else if (clazz == BookReviewSquareActivity.class) {
		// pageId = StatisPageId.REVIEW_HOT;
		// } else if (clazz == BookReviewSearchActivity.class) {
		// pageId = StatisPageId.REVIEW_SEARCH;
		// } else if (clazz == BookReviewPublishActionActivity.class
		// || clazz == BookReviewSelectBookActivity.class) {
		// pageId = StatisPageId.REVIEW_WRITE;
		// } else if (clazz == BookReviewDetailsActivity.class) {
		// pageId = StatisPageId.REVIEW_DETAIL;
		// } else if (clazz == BookReviewOtherPersonalFragment.class) {
		// pageId = StatisPageId.REVIEW_PERAONAL;
		// }
		//
		// /**
		// * 书评
		// *****************************************
		// * 个人
		// */
		//
		// else if (clazz == PersonalMainFragment.class) {
		// pageId = StatisPageId.PERSONAL_MAIN;
		// } else if (clazz == PersonalMsgFragment.class) {
		// pageId = StatisPageId.PERSONAL_MSGLIST;
		// } else if (clazz == PersonalSettingFragment.class) {
		// pageId = StatisPageId.PERSONAL_SET;
		// } else if (clazz == PersonShareActivity.class) {
		// pageId = StatisPageId.PERSONAL_SHAREFRIEND;
		// } else if (clazz == AboutActivity.class) {
		// pageId = StatisPageId.PERSONAL_ABOUT;
		// } else if (clazz == PersonalCouponFragment.class) {
		// pageId = StatisPageId.PERSONAL_GIFTLIST;
		// } else if (clazz == GiftCardActivateActivity.class) {
		// pageId = StatisPageId.PERSONAL_GIFTCARD_ACTIVITE;
		// } else if (clazz == PersonInfoActivity.class
		// || clazz == PersonTakePhotoActivity.class) {
		// pageId = StatisPageId.PERSONAL_INFO;
		// } else if (clazz == PersonCertificationActivity.class) {
		// pageId = StatisPageId.PERSONAL_INFO_AUTH;
		// } else if (clazz == PersonalHistoryFragment.class) {
		// pageId = StatisPageId.PERSONAL_READLOG;
		// } else if (clazz == PersonalBookNoteFragment.class) {
		// pageId = StatisPageId.PERSONAL_NOTELIST;
		// } else if (clazz == PersonalBookNoteDetailFragment.class) {
		// pageId = StatisPageId.PERSONAL_NOTELIST_BOOK;
		// } else if (clazz == PersonalFavorFragment.class) {
		// pageId = StatisPageId.PERSONAL_FAVLIST;
		// } else if (clazz == PersonalShakeActivity.class) {
		// pageId = StatisPageId.PERSONAL_SHAKE;
		// } else if (clazz == SignInActivity.class) {
		// pageId = StatisPageId.PERSONAL_SIGN;
		// } else if (clazz == HelpActivity.class) {
		// pageId = StatisPageId.PERSONAL_HELP;
		// } else if (clazz == Feedbackactivity.class) {
		// pageId = StatisPageId.PERSONAL_FEEDBACK;
		// } else if (clazz == RegisterAndLoginActivity.class) {
		// pageId = StatisPageId.PERSONAL_LOGINREGISTER;
		// } else if (clazz == PersonalHistoryDetailFragment.class) {
		// pageId = StatisPageId.PERSONAL_READLOG_CHART;
		// } else if (clazz == EditUserProfileActivity.class) {
		// pageId = StatisPageId.PERSONAL_EDITINFO;
		// } else if (clazz == EditUserNameActivity.class) {
		// pageId = StatisPageId.PERSONAL_EDITNAME;
		// }
		//
		// /**
		// * 个人
		// *****************************************
		// * 阅读
		// */
		// else if (clazz == ReadMainActivity.class) {
		// pageId = StatisPageId.PERSONAL_ABOUT;
		// } else if (clazz == FootprintsActivity.class) {
		// pageId = StatisPageId.READ_FOOTPRIENT;
		// } else if (clazz == BookNoteActivity.class) {
		// pageId = StatisPageId.READ_EDIT_NOTE;
		// } else if (clazz == MoreReadSettingsActivity.class) {
		// pageId = StatisPageId.READ_SETTING;
		// } else if (clazz == FontListActivity.class) {
		// pageId = StatisPageId.READ_FONTLIST;
		// } else if (clazz == ReadSpacingActivity.class) {
		// pageId = StatisPageId.READ_CUSTOMFORMAT;
		// } else if (clazz == WebBrowserActivity.class) {
		// pageId = StatisPageId.READ_BROWSER;
		// }
		//
		// /**
		// * 阅读
		// *****************************************
		// * 公共
		// */
		// else if (clazz == WebFragment.class || clazz == WebActivity.class) {
		// pageId = StatisPageId.READ_BROWSER;
		// } else if (clazz == UpgradeActivity.class) {
		// pageId = StatisPageId.PUB_UPGRADE;
		// }

		return pageId;
	}

	/**
	 * 增加一个ddclick埋点
	 * 
	 * @param module
	 *            模块名 根据模块名得到pageId
	 * @param eventId
	 *            事件id, 如打开应用，点击购买等
	 * @param pageInfo
	 *            页标识等 k=v的格式；记录当前页类型和id，例如单品页：pid=xxx；
	 * @param linkUrl
	 *            去向页面标识；格式是cms字典格式：type://key=value；
	 * @param expandField
	 *            如记录楼层-坑位信息和其他扩展字段；key=value的格式，不同k=v之间用#分隔；
	 */
	public void addStatis(Object module, StatisEventId eventId,
			String pageInfo, String linkUrl, String expandField) {
		if (!ddClickSwitch) {
			printLog(" ddClickSwitch false ");
			return;
		}
		int pageId = -1;
		if (module != null) {
			pageId = getPageId(module.getClass());
		}
		try {
			addStatisInner(pageId, eventId, pageInfo, 0, linkUrl, expandField);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pushData() {
		getStatisService().pushData();
	}

	public void send2Server() {
		if (!ddClickSwitch) {
			printLog(" ddClickSwitch false ");
			return;
		}
		if (context == null) {
			printLogE(" send2Server context == null ");
			return;
		}
		/*SubmitNewStatis newStatis = new SubmitNewStatis(context);
		newStatis.send();*/
	}

	protected void addStatisInner(int pageId, StatisEventId eventId,
			String pageInfo, long pageStayTime, String linkUrl,
			String expandField) {
		getStatisService().addStatis(pageId, eventId, pageInfo, pageStayTime,
				linkUrl, expandField);
	}

	protected void printLog(String log) {
		LogM.i(getClass().getSimpleName(), log);
	}

	protected void printLogE(String log) {
		LogM.e(getClass().getSimpleName(), log);
	}
}
