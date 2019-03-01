package com.moxi.bookstore.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/27 0027.
 */
public class Cart {


    /**
     * cartId : 1609271123042346
     * currentDate : 2016-09-28 09:39:31
     * products : [{"authorPenname":"当年明月","cartId":"1609271123042346","category":"bb","categoryIds":"LSPJDW","categorys":"历史普及读物","coverPic":"http://img60.ddimg.cn/digital/product/55/79/1900505579_ii_cover.jpg?version=13ce19c1-5b09-4d24-8ce6-dceb1a5e30b1","creationDate":1474947619000,"deviceTypeCodes":"pconline|pconline|ipad|iphone|android|eink|html5","mediaId":1900505579,"price":"1399","promotionList":[{"code":"10000","columnCode":"1388","endTime":1475247600000,"extendInfo":"1000_300;2000_600;3000_900;4000_1200;5000_1500;6000_1800","iconUrl":"http://img4.ddimg.cn/00035/pic/mej_b.png","isCirculation":1,"name":"电子书满10减3","productIdList":[],"promotionDesc":"满10减3,满20减6,满30减9,满40减12,满50减15,满60减18","salePromotionId":1,"shortName":"满额减","sort":1,"startTime":1474196641000,"status":1}],"qutity":1,"saleId":1900505579,"title":"明朝那些事儿（全七册）"},{"authorPenname":"孙力科","cartId":"1609271123042346","category":"bb","categoryIds":"GLX","categorys":"管理学","coverPic":"http://img62.ddimg.cn/digital/product/31/2/1900353102_ii_cover.jpg?version=748b25aa-6408-40f8-8019-3c5b996f1f17","creationDate":1474964974000,"deviceTypeCodes":"pconline|pconline|ipad|iphone|android|eink|html5","mediaId":1900353102,"price":"80","qutity":1,"saleId":1900353102,"title":"任正非：管理的真相"},{"authorPenname":"白岩松","cartId":"1609271123042346","category":"bb","categoryIds":"SB","categorys":"随笔","coverPic":"http://img62.ddimg.cn/digital/product/11/21/1900351121_ii_cover.jpg?version=3f217b75-a6ed-4cd3-99b5-ba059fcb79f3","creationDate":1474420437000,"deviceTypeCodes":"pconline|pconline|ipad|iphone|android|eink|html5","mediaId":1900351121,"price":"98","qutity":1,"saleId":1900351121,"title":"幸福了吗"},{"authorPenname":"黄磊","cartId":"1609271123042346","category":"bb","categoryIds":"SB","categorys":"随笔","coverPic":"http://img61.ddimg.cn/digital/product/94/26/1900389426_ii_cover.jpg?version=2bffbb8d-f97a-4133-bdee-2f10bfc2e7c8","creationDate":1474420456000,"deviceTypeCodes":"pconline|pconline|ipad|iphone|android|eink|html5","mediaId":1900389426,"price":"10","qutity":1,"saleId":1900389426,"title":"我的肩膀，她们的翅膀"},{"authorPenname":"囧叔","cartId":"1609271123042346","category":"bb","categoryIds":"SB","categorys":"随笔","coverPic":"http://img61.ddimg.cn/digital/product/0/97/1900340097_ii_cover.jpg?version=4646d3cc-7c25-4830-a74b-88ec394769cb","creationDate":1474964394000,"deviceTypeCodes":"pconline|pconline|ipad|iphone|android|eink|html5","mediaId":1900340097,"price":"99","qutity":1,"saleId":1900340097,"title":"我讲个笑话，你可别哭啊"},{"authorPenname":"牧来","cartId":"1609271123042346","category":"bb","categoryIds":"WXJ","categorys":"文学家","coverPic":"http://img61.ddimg.cn/digital/product/73/97/1900497397_ii_cover.jpg?version=27b4af3f-d235-4137-9b70-52571b60eee5","creationDate":1475026057000,"deviceTypeCodes":"pconline|pconline|ipad|iphone|android|eink|html5","mediaId":1900497397,"price":"99","qutity":1,"saleId":1900497397,"title":"张爱玲：最是清醒落寞人"},{"authorPenname":"沧月","cartId":"1609271123042346","category":"bb","categoryIds":"XHJS","categorys":"玄幻/惊悚","coverPic":"http://img62.ddimg.cn/digital/product/38/92/1900573892_ii_cover.jpg?version=b1b94c99-223f-4de2-bffb-f9b7c2d53b7d","creationDate":1474947630000,"deviceTypeCodes":"pconline|ipad|iphone|android|eink|html5","mediaId":1900573892,"price":"1999","qutity":1,"saleId":1900573892,"title":"镜（套装全6册）·沧月纪念珍藏版"}]
     * promotion : [{"code":"10000","columnCode":"1388","endTime":1475247600000,"extendInfo":"1000_300;2000_600;3000_900;4000_1200;5000_1500;6000_1800","iconUrl":"http://img4.ddimg.cn/00035/pic/mej_b.png","isCirculation":1,"name":"电子书满10减3","productIdList":[1900505579],"productIds":"1900505579","promotionDesc":"满10减3,满20减6,满30减9,满40减12,满50减15,满60减18","shortName":"满额减","sort":1,"startTime":1474196641000,"status":1,"tips":[{"discountedPrice":300,"minPrice":1000,"tip":"满10减3"},{"discountedPrice":600,"minPrice":2000,"tip":"满20减6"},{"discountedPrice":900,"minPrice":3000,"tip":"满30减9"},{"discountedPrice":1200,"minPrice":4000,"tip":"满40减12"},{"discountedPrice":1500,"minPrice":5000,"tip":"满50减15"},{"discountedPrice":1800,"minPrice":6000,"tip":"满60减18"}]}]
     * systemDate : 1475026771195
     */

    private String cartId;
    private String currentDate;
    private String systemDate;
    /**
     * authorPenname : 当年明月
     * cartId : 1609271123042346
     * category : bb
     * categoryIds : LSPJDW
     * categorys : 历史普及读物
     * coverPic : http://img60.ddimg.cn/digital/product/55/79/1900505579_ii_cover.jpg?version=13ce19c1-5b09-4d24-8ce6-dceb1a5e30b1
     * creationDate : 1474947619000
     * deviceTypeCodes : pconline|pconline|ipad|iphone|android|eink|html5
     * mediaId : 1900505579
     * price : 1399
     * promotionList : [{"code":"10000","columnCode":"1388","endTime":1475247600000,"extendInfo":"1000_300;2000_600;3000_900;4000_1200;5000_1500;6000_1800","iconUrl":"http://img4.ddimg.cn/00035/pic/mej_b.png","isCirculation":1,"name":"电子书满10减3","productIdList":[],"promotionDesc":"满10减3,满20减6,满30减9,满40减12,满50减15,满60减18","salePromotionId":1,"shortName":"满额减","sort":1,"startTime":1474196641000,"status":1}]
     * qutity : 1
     * saleId : 1900505579
     * title : 明朝那些事儿（全七册）
     */

    private List<ProductsBean> products;
    /**
     * code : 10000
     * columnCode : 1388
     * endTime : 1475247600000
     * extendInfo : 1000_300;2000_600;3000_900;4000_1200;5000_1500;6000_1800
     * iconUrl : http://img4.ddimg.cn/00035/pic/mej_b.png
     * isCirculation : 1
     * name : 电子书满10减3
     * productIdList : [1900505579]
     * productIds : 1900505579
     * promotionDesc : 满10减3,满20减6,满30减9,满40减12,满50减15,满60减18
     * shortName : 满额减
     * sort : 1
     * startTime : 1474196641000
     * status : 1
     * tips : [{"discountedPrice":300,"minPrice":1000,"tip":"满10减3"},{"discountedPrice":600,"minPrice":2000,"tip":"满20减6"},{"discountedPrice":900,"minPrice":3000,"tip":"满30减9"},{"discountedPrice":1200,"minPrice":4000,"tip":"满40减12"},{"discountedPrice":1500,"minPrice":5000,"tip":"满50减15"},{"discountedPrice":1800,"minPrice":6000,"tip":"满60减18"}]
     */

    private List<PromotionBean> promotion;

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getSystemDate() {
        return systemDate;
    }

    public void setSystemDate(String systemDate) {
        this.systemDate = systemDate;
    }

    public List<ProductsBean> getProducts() {
        return products;
    }

    public void setProducts(List<ProductsBean> products) {
        this.products = products;
    }

    public List<PromotionBean> getPromotion() {
        return promotion;
    }

    public void setPromotion(List<PromotionBean> promotion) {
        this.promotion = promotion;
    }

    public static class ProductsBean {
        private boolean isChecked;
        private String authorPenname;
        private String cartId;
        private String category;
        private String categoryIds;
        private String categorys;
        private String coverPic;
        private long creationDate;
        private String deviceTypeCodes;
        private int mediaId;
        private String price;
        private int qutity;
        private int saleId;
        private String title;

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        /**
         * code : 10000
         * columnCode : 1388
         * endTime : 1475247600000
         * extendInfo : 1000_300;2000_600;3000_900;4000_1200;5000_1500;6000_1800
         * iconUrl : http://img4.ddimg.cn/00035/pic/mej_b.png
         * isCirculation : 1
         * name : 电子书满10减3
         * productIdList : []
         * promotionDesc : 满10减3,满20减6,满30减9,满40减12,满50减15,满60减18
         * salePromotionId : 1
         * shortName : 满额减
         * sort : 1
         * startTime : 1474196641000
         * status : 1
         */


        private List<PromotionListBean> promotionList;

        public String getAuthorPenname() {
            return authorPenname;
        }

        public void setAuthorPenname(String authorPenname) {
            this.authorPenname = authorPenname;
        }

        public String getCartId() {
            return cartId;
        }

        public void setCartId(String cartId) {
            this.cartId = cartId;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getCategoryIds() {
            return categoryIds;
        }

        public void setCategoryIds(String categoryIds) {
            this.categoryIds = categoryIds;
        }

        public String getCategorys() {
            return categorys;
        }

        public void setCategorys(String categorys) {
            this.categorys = categorys;
        }

        public String getCoverPic() {
            return coverPic;
        }

        public void setCoverPic(String coverPic) {
            this.coverPic = coverPic;
        }

        public long getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(long creationDate) {
            this.creationDate = creationDate;
        }

        public String getDeviceTypeCodes() {
            return deviceTypeCodes;
        }

        public void setDeviceTypeCodes(String deviceTypeCodes) {
            this.deviceTypeCodes = deviceTypeCodes;
        }

        public int getMediaId() {
            return mediaId;
        }

        public void setMediaId(int mediaId) {
            this.mediaId = mediaId;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public int getQutity() {
            return qutity;
        }

        public void setQutity(int qutity) {
            this.qutity = qutity;
        }

        public int getSaleId() {
            return saleId;
        }

        public void setSaleId(int saleId) {
            this.saleId = saleId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<PromotionListBean> getPromotionList() {
            return promotionList;
        }

        public void setPromotionList(List<PromotionListBean> promotionList) {
            this.promotionList = promotionList;
        }

        public static class PromotionListBean {
            private String code;
            private String columnCode;
            private long endTime;
            private String extendInfo;
            private String iconUrl;
            private int isCirculation;
            private String name;
            private String promotionDesc;
            private int salePromotionId;
            private String shortName;
            private int sort;
            private long startTime;
            private int status;
            private List<?> productIdList;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getColumnCode() {
                return columnCode;
            }

            public void setColumnCode(String columnCode) {
                this.columnCode = columnCode;
            }

            public long getEndTime() {
                return endTime;
            }

            public void setEndTime(long endTime) {
                this.endTime = endTime;
            }

            public String getExtendInfo() {
                return extendInfo;
            }

            public void setExtendInfo(String extendInfo) {
                this.extendInfo = extendInfo;
            }

            public String getIconUrl() {
                return iconUrl;
            }

            public void setIconUrl(String iconUrl) {
                this.iconUrl = iconUrl;
            }

            public int getIsCirculation() {
                return isCirculation;
            }

            public void setIsCirculation(int isCirculation) {
                this.isCirculation = isCirculation;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPromotionDesc() {
                return promotionDesc;
            }

            public void setPromotionDesc(String promotionDesc) {
                this.promotionDesc = promotionDesc;
            }

            public int getSalePromotionId() {
                return salePromotionId;
            }

            public void setSalePromotionId(int salePromotionId) {
                this.salePromotionId = salePromotionId;
            }

            public String getShortName() {
                return shortName;
            }

            public void setShortName(String shortName) {
                this.shortName = shortName;
            }

            public int getSort() {
                return sort;
            }

            public void setSort(int sort) {
                this.sort = sort;
            }

            public long getStartTime() {
                return startTime;
            }

            public void setStartTime(long startTime) {
                this.startTime = startTime;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public List<?> getProductIdList() {
                return productIdList;
            }

            public void setProductIdList(List<?> productIdList) {
                this.productIdList = productIdList;
            }
        }
    }

    public static class PromotionBean {
        private String code;
        private String columnCode;
        private long endTime;
        private String extendInfo;
        private String iconUrl;
        private int isCirculation;
        private String name;
        private String productIds;
        private String promotionDesc;
        private String shortName;
        private int sort;
        private long startTime;
        private int status;
        private List<Integer> productIdList;
        /**
         * discountedPrice : 300
         * minPrice : 1000
         * tip : 满10减3
         */

        private List<TipsBean> tips;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getColumnCode() {
            return columnCode;
        }

        public void setColumnCode(String columnCode) {
            this.columnCode = columnCode;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public String getExtendInfo() {
            return extendInfo;
        }

        public void setExtendInfo(String extendInfo) {
            this.extendInfo = extendInfo;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public int getIsCirculation() {
            return isCirculation;
        }

        public void setIsCirculation(int isCirculation) {
            this.isCirculation = isCirculation;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProductIds() {
            return productIds;
        }

        public void setProductIds(String productIds) {
            this.productIds = productIds;
        }

        public String getPromotionDesc() {
            return promotionDesc;
        }

        public void setPromotionDesc(String promotionDesc) {
            this.promotionDesc = promotionDesc;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public List<Integer> getProductIdList() {
            return productIdList;
        }

        public void setProductIdList(List<Integer> productIdList) {
            this.productIdList = productIdList;
        }

        public List<TipsBean> getTips() {
            return tips;
        }

        public void setTips(List<TipsBean> tips) {
            this.tips = tips;
        }

        public static class TipsBean {
            private int discountedPrice;
            private int minPrice;
            private String tip;

            public int getDiscountedPrice() {
                return discountedPrice;
            }

            public void setDiscountedPrice(int discountedPrice) {
                this.discountedPrice = discountedPrice;
            }

            public int getMinPrice() {
                return minPrice;
            }

            public void setMinPrice(int minPrice) {
                this.minPrice = minPrice;
            }

            public String getTip() {
                return tip;
            }

            public void setTip(String tip) {
                this.tip = tip;
            }
        }
    }
}
