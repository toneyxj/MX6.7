package com.dangdang.reader.store.domain;

import java.util.List;

/**
 * 书城电子书数据结构
 * Created by xiaruri on 2015/5/25.
 */
public class StoreEBook extends StoreBaseBook{

    private String cpShortName;                 // 版权所有
    private String speaker;                     // 该书的演讲者，听书专用字段
    private int shelfStatus;                    // 上下架状态；0：下架；1：上架
    private int isFull;                         // 是否已完结
    private int isSupportFullBuy;               // 是否支持全本购买
    private int isChapterAuthority;             // 是否购买章节
    private int isWholeAuthority;               // 是否购买全本
    private int chapterCnt;                     // 章节数
    private int lastIndexOrder;                 // 最新章节序号
    private String lastChapterName;             // 最新章节名称
    private int freeFileSize;                   // 试读本大小
    private int fileSize;                       // 全本大小
    private int isSupportDevice;                // 该电子书的文件格式是否支持当前客户端，0：不支持；1：支持
    private int isChannelMonth;                 // 是否有频道包月权限
    private int priceUnit;                      // 字数价格，多少铃铛/千字，原创小说特有字段
    private int price;                          // 销售价，单位：铃铛
    private int canBorrow;                      // 能否借阅
    private long borrowDuration;                // 可借阅时间毫秒值
    private long borrowEndTime;                 // 借阅截止时间毫秒值
    private long renewEndTime;                  // 续借截止时间毫秒值
    private long renewStartTime;                // 续借开始时间毫秒值
    private long renewDurationTime;             // 续借剩余时间毫秒值
    private float paperMediaPrice;              // 纸书价格，单位：分
    private float originalPrice;                // 纸书原价，单位：元
    private float lowestPrice;                  // 纸书最低价格，单位：元
    private String paperMediaProductId;         // 纸书id
    private int freeBook;                       // 是否能免费获取，0：否，1：是
    private boolean isSelect;			        // 是否被选中
    private boolean isEditSelect;		        // 是否在编辑状态下选中
    private String relationType;                // 关系类型(1001购买  1002免费获取  1004领取的赠书    9999赠出的书)
    private int promotionId;					// 活动id，3：免费
    private String cId = "";		            // 该书所在的频道id
    private boolean isFontBuy;                  // 是否是字体购买
    private String editorRecommend;             // 编辑推荐语


    //5.6版本添加支持包月字段
    private  String channelId;
    private  String channelName;
    private long download_time;//下载时间
    private long monthlyEndTime;
    private long creationDate;
    private boolean isBuyAuth;//true:已经购买,false:未购买
    private String id;//用来分页使用，但是目前不使用分页
    private boolean channelHall = true; // is book in vip channel
    private int authStatus;//1 包月中，2已经购买，0，还了

    //5.7版本添加 促销信息（限时抢）
    private float salePrice;
    private List<Promotion> promotionList;

    public float getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(float salePrice) {
        this.salePrice = salePrice;
    }

    public List<Promotion> getPromotionList() {
        return promotionList;
    }

    public void setPromotionList(List<Promotion> promotionList) {
        this.promotionList = promotionList;
    }

    public int getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(int authStatus) {
        this.authStatus = authStatus;
    }
    public boolean isBuyAuth() {
        return isBuyAuth;
    }

    public void setIsBuyAuth(boolean isBuyAuth) {
        this.isBuyAuth = isBuyAuth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public long getDownload_time() {
        return download_time;
    }

    public void setDownload_time(long download_time) {
        this.download_time = download_time;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getMonthlyEndTime() {
        return monthlyEndTime;
    }

    public void setMonthlyEndTime(long monthlyEndTime) {
        this.monthlyEndTime = monthlyEndTime;
    }

    /**
     * relationType 1001购买  1002免费获取  1004领取的赠书    9999赠出的书
     * 是否是别人总是的全本
     * @return
     */
    public boolean isGiftFull(){
        return "1004".equals(relationType);
    }

    /**
     * 该书是否能加入购物车
     * @return
     */
    public boolean isCanAddShoppingCart(){
        if(getMediaType() == 1){
            return false;
        }
        if(getIsWholeAuthority() == 1){
            return false;
        }
        if(getFreeBook() == 1){
            return false;
        }
        if(getIsChannelMonth() == 1){
            return false;
        }
        if(getIsSupportDevice() != 1){
            return false;
        }

        return true;
    }

    public String getCpShortName() {
        return cpShortName;
    }

    public void setCpShortName(String cpShortName) {
        this.cpShortName = cpShortName;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public int getShelfStatus() {
        return shelfStatus;
    }

    public void setShelfStatus(int shelfStatus) {
        this.shelfStatus = shelfStatus;
    }

    public int getIsFull() {
        return isFull;
    }

    public void setIsFull(int isFull) {
        this.isFull = isFull;
    }

    public int getIsSupportFullBuy() {
        return isSupportFullBuy;
    }

    public void setIsSupportFullBuy(int isSupportFullBuy) {
        this.isSupportFullBuy = isSupportFullBuy;
    }

    public int getIsChapterAuthority() {
        return isChapterAuthority;
    }

    public void setIsChapterAuthority(int isChapterAuthority) {
        this.isChapterAuthority = isChapterAuthority;
    }

    public int getIsWholeAuthority() {
        return isWholeAuthority;
    }

    public void setIsWholeAuthority(int isWholeAuthority) {
        this.isWholeAuthority = isWholeAuthority;
    }

    public int getChapterCnt() {
        return chapterCnt;
    }

    public void setChapterCnt(int chapterCnt) {
        this.chapterCnt = chapterCnt;
    }

    public int getLastIndexOrder() {
        return lastIndexOrder;
    }

    public String getLastChapterName() {
        return lastChapterName;
    }

    public void setLastChapterName(String lastChapterName) {
        this.lastChapterName = lastChapterName;
    }

    public void setLastIndexOrder(int lastIndexOrder) {
        this.lastIndexOrder = lastIndexOrder;
    }

    public int getFreeFileSize() {
        return freeFileSize;
    }

    public void setFreeFileSize(int freeFileSize) {
        this.freeFileSize = freeFileSize;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fullFileSize) {
        this.fileSize = fullFileSize;
    }

    public int getIsSupportDevice() {
        return isSupportDevice;
    }

    public void setIsSupportDevice(int isSupportDevice) {
        this.isSupportDevice = isSupportDevice;
    }

    public int getIsChannelMonth() {
        return isChannelMonth;
    }

    public void setIsChannelMonth(int isChannelMonth) {
        this.isChannelMonth = isChannelMonth;
    }

    public int getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(int priceUnit) {
        this.priceUnit = priceUnit;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCanBorrow() {
        return canBorrow;
    }

    public void setCanBorrow(int canBorrow) {
        this.canBorrow = canBorrow;
    }

    public long getBorrowDuration() {
        return borrowDuration;
    }

    public void setBorrowDuration(long borrowDuration) {
        this.borrowDuration = borrowDuration;
    }

    public long getBorrowEndTime() {
        return borrowEndTime;
    }

    public void setBorrowEndTime(long borrowEndTime) {
        this.borrowEndTime = borrowEndTime;
    }

    public long getRenewEndTime() {
        return renewEndTime;
    }

    public void setRenewEndTime(long renewEndTime) {
        this.renewEndTime = renewEndTime;
    }

    public long getRenewStartTime() {
        return renewStartTime;
    }

    public void setRenewStartTime(long renewStartTime) {
        this.renewStartTime = renewStartTime;
    }

    public long getRenewDurationTime() {
        return renewDurationTime;
    }

    public void setRenewDurationTime(long renewDurationTime) {
        this.renewDurationTime = renewDurationTime;
    }

    public float getPaperMediaPrice() {
        return paperMediaPrice;
    }

    public void setPaperMediaPrice(float paperMediaPrice) {
        this.paperMediaPrice = paperMediaPrice;
    }

    public float getLowestPrice() {
        if(lowestPrice <= 0){
            return originalPrice;
        }
        return lowestPrice;
    }

    public void setLowestPrice(float lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public float getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(float originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getPaperMediaProductId() {
        return paperMediaProductId;
    }

    public void setPaperMediaProductId(String paperMediaProductId) {
        this.paperMediaProductId = paperMediaProductId;
    }

    public int getFreeBook() {
        return freeBook;
    }

    public void setFreeBook(int freeBook) {
        this.freeBook = freeBook;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public boolean isEditSelect() {
        return isEditSelect;
    }

    public void setIsEditSelect(boolean isEditSelect) {
        this.isEditSelect = isEditSelect;
    }

    public int getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public boolean isFontBuy() {
        return isFontBuy;
    }

    public void setIsFontBuy(boolean isFontBuy) {
        this.isFontBuy = isFontBuy;
    }

    public String getEditorRecommend() {
        return editorRecommend;
    }

    public void setEditorRecommend(String editorRecommend) {
        this.editorRecommend = editorRecommend;
    }

    public boolean isChannelHall() {
        return channelHall;
    }

    public void setChannelHall(boolean channelHall) {
        this.channelHall = channelHall;
    }
}
