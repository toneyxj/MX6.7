package com.moxi.bookstore.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/10/19.
 */
public class OrderBean {
    private String AllocGiftPackagePrice;

    private String ArrivalDate;

    private String ArrivalDatePromise;

    private String AuctionCommission;

    private String AuctionDeposit;

    private String BackAmount;

    private String BackAmountName;

    private String COUPON_APPLY_ID;

    private String CanComment;

    private String CancelDate;

    private String CollectionReducePrice;

    private String ConfirmDate;

    private String ControlStatus;

    private String CouponAmount;

    private String CouponAmountLiQuan;

    private String CouponId;

    private String CouponIdLiPinKa;

    private String CustCreditUsed;

    private String CustEmail;

    private String CustId;

    private String CustMessage;

    private String CustPointUsed;

    private String CustomsDeclareCode;

    private String Deduct_amount;

    private String Deduct_invoice_amount;

    private String DeliveryConfirmType;

    private String DeliveryDate;

    private String ExpectedDeliveryTime;

    private String ExpectedDeliveryTimePromise;

    private String ExpressComplaintTel;

    private String GiftCardCharge;

    private String GiftMessage;

    private String GiftPackagePrice;

    private String GiftSenderPhone;

    private String IS_MOBILE_BLACKLIST_FIRST_ORDER;

    private String IS_NEW_CK_ORDER;

    private String IS_ORDER_FORCIBLY_SPLITTED;

    private String IS_OVERSEAS;

    private String IS_SEND_TOGETHER;

    private String InstallmentType;

    private String InvoiceCategory;

    private String InvoiceContent;

    private String InvoicePrice;

    private String InvoiceTitle;

    private String IsBigShopOrder;

    private String IsDisplayRefundInfo;

    private String IsGiftPackage;

    private String IsGomeSubsidyOrder;

    private String IsHaveVipPrice;

    private String IsInstallment;

    private String IsInvoiceNeed;

    private String IsMoveStore;

    private String IsPointDeductionOrder;

    private String IsPresale;

    private String IsPrintPrice;

    private String IsProductShippingfee;

    private String IsSubsidyConfirm;

    private String LastChangeDate;

    private String NeedOnlinePay;

    private String NewShippingFee;

    private String OVERSEAS_TAX;

    private String OVERSEA_TAX_TYPE;

    private String ObtainTotalPoints;

    private String OrderCancelDate;

    private String OrderCreationDate;

    private String OrderId;

    private String OrderInvoiceInfo;

    private String OrderPromSubtract;

    private String OrderSendDate;

    private String OrderSource;

    private String OrderStatus;

    private String OrderStatusCode;

    private String OrderType;

    private String PaidAmount;

    private String PaidAmountOrder;

    private String PaidAmountString;

    private String PaidDateString;

    private String PayId;

    private String PayId_name;

    private String PayType;

    private String PayTypeName;

    private String PayableAfterAlloc;

    private String PayableAfterAllocOrder;

    private String PayableAmount;

    private String PayableAmountOrder;

    private String PaymentByInstalments;

    private String PaymentId;

    private String PaymentMethodType;

    private String Payment_provider_sub_id;

    private String Platform;

    private String PointDeductionAmount;

    private String PreferentialName;

    private String PreferentialRates;

    private String ProductIds;

    private String ProductTotal;

    private String PromiseWord;

    private String PublicCouponNumber;

    private String REPAYID;

    private String ReceiverAddress;

    private String ReceiverCityId;

    private String ReceiverFixTel;

    private String ReceiverMobilePhone;

    private String ReceiverName;

    private String ReceiverTel;

    private String RefundText;

    private String SendCompany;

    private String SendCompanyTel;

    private String SendPackageCode;

    private String ShipForecast;

    private String ShipType;

    private String ShippingFee;

    private String ShopId;

    private String ShopName;

    private String ShopPromoTotalAfterAloc;

    private String ShopType;

    private String TotalActivityFee;

    private String TotalAfterAlloc;

    private String TotalAfterAllocOrder;

    private String TotalBarginPrice;

    private String TotalBarginPriceB2C;

    private String TotalBarginPriceCheckOut;

    private String TotalBarginPriceOrder;

    private String Warehouse;

    private String WarehouseId;

    private String cancel_type;

    private String collect_subtract1;

    private String collect_subtract10;

    private String collect_subtract2;

    private String collect_subtract3;

    private String collect_subtract4;

    private String collect_subtract5;

    private String collect_subtract6;

    private String collect_subtract7;

    private String collect_subtract8;

    private String collect_subtract9;

    private String collect_subtract_amount1;

    private String collect_subtract_amount10;

    private String collect_subtract_amount2;

    private String collect_subtract_amount3;

    private String collect_subtract_amount4;

    private String collect_subtract_amount5;

    private String collect_subtract_amount6;

    private String collect_subtract_amount7;

    private String collect_subtract_amount8;

    private String collect_subtract_amount9;

    private String collection_promo_id1;

    private String collection_promo_id10;

    private String collection_promo_id2;

    private String collection_promo_id3;

    private String collection_promo_id4;

    private String collection_promo_id5;

    private String collection_promo_id6;

    private String collection_promo_id7;

    private String collection_promo_id8;

    private String collection_promo_id9;

    private String collection_promo_type1;

    private String collection_promo_type10;

    private String collection_promo_type2;

    private String collection_promo_type3;

    private String collection_promo_type4;

    private String collection_promo_type5;

    private String collection_promo_type6;

    private String collection_promo_type7;

    private String collection_promo_type8;

    private String collection_promo_type9;

    private String cust_type;

    private String earnest_money;

    private String final_money;

    private String is_have_gift;

    private String is_shop_reviewed;

    private String ishaverouteline;

    private String order_canceldate;

    private String order_prom_subtract;

    private String order_route_cancel_date;

    private String order_route_cancel_name;

    private String order_type;

    private String parent_id;

    private String payment_parse;

    private String preferred_shipping_time_type;

    private Integer presentBell;

    private String promotion_id;

    private String promotion_subtract;

    private String promotion_type;

    private String rcv_country_id;

    private String rcv_email;

    private String rcv_fix_tel;

    private String rcv_mobile_tel;

    private String rcv_province_id;

    private String rcv_town_id;

    private String rcv_zip;

    private String routeid;

    private List<Row> rows ;

    private String second_book_shopname;

    private String shipment_type_id;

    private String shop_is_self_cod;

    private String shop_order_prom_name;

    public String getAllocGiftPackagePrice() {
        return AllocGiftPackagePrice;
    }

    public void setAllocGiftPackagePrice(String allocGiftPackagePrice) {
        AllocGiftPackagePrice = allocGiftPackagePrice;
    }

    public String getArrivalDate() {
        return ArrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        ArrivalDate = arrivalDate;
    }

    public String getArrivalDatePromise() {
        return ArrivalDatePromise;
    }

    public void setArrivalDatePromise(String arrivalDatePromise) {
        ArrivalDatePromise = arrivalDatePromise;
    }

    public String getAuctionCommission() {
        return AuctionCommission;
    }

    public void setAuctionCommission(String auctionCommission) {
        AuctionCommission = auctionCommission;
    }

    public String getAuctionDeposit() {
        return AuctionDeposit;
    }

    public void setAuctionDeposit(String auctionDeposit) {
        AuctionDeposit = auctionDeposit;
    }

    public String getBackAmount() {
        return BackAmount;
    }

    public void setBackAmount(String backAmount) {
        BackAmount = backAmount;
    }

    public String getBackAmountName() {
        return BackAmountName;
    }

    public void setBackAmountName(String backAmountName) {
        BackAmountName = backAmountName;
    }

    public String getCOUPON_APPLY_ID() {
        return COUPON_APPLY_ID;
    }

    public void setCOUPON_APPLY_ID(String COUPON_APPLY_ID) {
        this.COUPON_APPLY_ID = COUPON_APPLY_ID;
    }

    public String getCanComment() {
        return CanComment;
    }

    public void setCanComment(String canComment) {
        CanComment = canComment;
    }

    public String getCancelDate() {
        return CancelDate;
    }

    public void setCancelDate(String cancelDate) {
        CancelDate = cancelDate;
    }

    public String getCollectionReducePrice() {
        return CollectionReducePrice;
    }

    public void setCollectionReducePrice(String collectionReducePrice) {
        CollectionReducePrice = collectionReducePrice;
    }

    public String getConfirmDate() {
        return ConfirmDate;
    }

    public void setConfirmDate(String confirmDate) {
        ConfirmDate = confirmDate;
    }

    public String getControlStatus() {
        return ControlStatus;
    }

    public void setControlStatus(String controlStatus) {
        ControlStatus = controlStatus;
    }

    public String getCouponAmount() {
        return CouponAmount;
    }

    public void setCouponAmount(String couponAmount) {
        CouponAmount = couponAmount;
    }

    public String getCouponAmountLiQuan() {
        return CouponAmountLiQuan;
    }

    public void setCouponAmountLiQuan(String couponAmountLiQuan) {
        CouponAmountLiQuan = couponAmountLiQuan;
    }

    public String getCouponId() {
        return CouponId;
    }

    public void setCouponId(String couponId) {
        CouponId = couponId;
    }

    public String getCouponIdLiPinKa() {
        return CouponIdLiPinKa;
    }

    public void setCouponIdLiPinKa(String couponIdLiPinKa) {
        CouponIdLiPinKa = couponIdLiPinKa;
    }

    public String getCustCreditUsed() {
        return CustCreditUsed;
    }

    public void setCustCreditUsed(String custCreditUsed) {
        CustCreditUsed = custCreditUsed;
    }

    public String getCustEmail() {
        return CustEmail;
    }

    public void setCustEmail(String custEmail) {
        CustEmail = custEmail;
    }

    public String getCustId() {
        return CustId;
    }

    public void setCustId(String custId) {
        CustId = custId;
    }

    public String getCustMessage() {
        return CustMessage;
    }

    public void setCustMessage(String custMessage) {
        CustMessage = custMessage;
    }

    public String getCustPointUsed() {
        return CustPointUsed;
    }

    public void setCustPointUsed(String custPointUsed) {
        CustPointUsed = custPointUsed;
    }

    public String getCustomsDeclareCode() {
        return CustomsDeclareCode;
    }

    public void setCustomsDeclareCode(String customsDeclareCode) {
        CustomsDeclareCode = customsDeclareCode;
    }

    public String getDeduct_amount() {
        return Deduct_amount;
    }

    public void setDeduct_amount(String deduct_amount) {
        Deduct_amount = deduct_amount;
    }

    public String getDeduct_invoice_amount() {
        return Deduct_invoice_amount;
    }

    public void setDeduct_invoice_amount(String deduct_invoice_amount) {
        Deduct_invoice_amount = deduct_invoice_amount;
    }

    public String getDeliveryConfirmType() {
        return DeliveryConfirmType;
    }

    public void setDeliveryConfirmType(String deliveryConfirmType) {
        DeliveryConfirmType = deliveryConfirmType;
    }

    public String getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        DeliveryDate = deliveryDate;
    }

    public String getExpectedDeliveryTime() {
        return ExpectedDeliveryTime;
    }

    public void setExpectedDeliveryTime(String expectedDeliveryTime) {
        ExpectedDeliveryTime = expectedDeliveryTime;
    }

    public String getExpectedDeliveryTimePromise() {
        return ExpectedDeliveryTimePromise;
    }

    public void setExpectedDeliveryTimePromise(String expectedDeliveryTimePromise) {
        ExpectedDeliveryTimePromise = expectedDeliveryTimePromise;
    }

    public String getExpressComplaintTel() {
        return ExpressComplaintTel;
    }

    public void setExpressComplaintTel(String expressComplaintTel) {
        ExpressComplaintTel = expressComplaintTel;
    }

    public String getGiftCardCharge() {
        return GiftCardCharge;
    }

    public void setGiftCardCharge(String giftCardCharge) {
        GiftCardCharge = giftCardCharge;
    }

    public String getGiftMessage() {
        return GiftMessage;
    }

    public void setGiftMessage(String giftMessage) {
        GiftMessage = giftMessage;
    }

    public String getGiftPackagePrice() {
        return GiftPackagePrice;
    }

    public void setGiftPackagePrice(String giftPackagePrice) {
        GiftPackagePrice = giftPackagePrice;
    }

    public String getGiftSenderPhone() {
        return GiftSenderPhone;
    }

    public void setGiftSenderPhone(String giftSenderPhone) {
        GiftSenderPhone = giftSenderPhone;
    }

    public String getIS_MOBILE_BLACKLIST_FIRST_ORDER() {
        return IS_MOBILE_BLACKLIST_FIRST_ORDER;
    }

    public void setIS_MOBILE_BLACKLIST_FIRST_ORDER(String IS_MOBILE_BLACKLIST_FIRST_ORDER) {
        this.IS_MOBILE_BLACKLIST_FIRST_ORDER = IS_MOBILE_BLACKLIST_FIRST_ORDER;
    }

    public String getIS_NEW_CK_ORDER() {
        return IS_NEW_CK_ORDER;
    }

    public void setIS_NEW_CK_ORDER(String IS_NEW_CK_ORDER) {
        this.IS_NEW_CK_ORDER = IS_NEW_CK_ORDER;
    }

    public String getIS_ORDER_FORCIBLY_SPLITTED() {
        return IS_ORDER_FORCIBLY_SPLITTED;
    }

    public void setIS_ORDER_FORCIBLY_SPLITTED(String IS_ORDER_FORCIBLY_SPLITTED) {
        this.IS_ORDER_FORCIBLY_SPLITTED = IS_ORDER_FORCIBLY_SPLITTED;
    }

    public String getIS_OVERSEAS() {
        return IS_OVERSEAS;
    }

    public void setIS_OVERSEAS(String IS_OVERSEAS) {
        this.IS_OVERSEAS = IS_OVERSEAS;
    }

    public String getIS_SEND_TOGETHER() {
        return IS_SEND_TOGETHER;
    }

    public void setIS_SEND_TOGETHER(String IS_SEND_TOGETHER) {
        this.IS_SEND_TOGETHER = IS_SEND_TOGETHER;
    }

    public String getInstallmentType() {
        return InstallmentType;
    }

    public void setInstallmentType(String installmentType) {
        InstallmentType = installmentType;
    }

    public String getInvoiceCategory() {
        return InvoiceCategory;
    }

    public void setInvoiceCategory(String invoiceCategory) {
        InvoiceCategory = invoiceCategory;
    }

    public String getInvoiceContent() {
        return InvoiceContent;
    }

    public void setInvoiceContent(String invoiceContent) {
        InvoiceContent = invoiceContent;
    }

    public String getInvoicePrice() {
        return InvoicePrice;
    }

    public void setInvoicePrice(String invoicePrice) {
        InvoicePrice = invoicePrice;
    }

    public String getInvoiceTitle() {
        return InvoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        InvoiceTitle = invoiceTitle;
    }

    public String getIsBigShopOrder() {
        return IsBigShopOrder;
    }

    public void setIsBigShopOrder(String isBigShopOrder) {
        IsBigShopOrder = isBigShopOrder;
    }

    public String getIsDisplayRefundInfo() {
        return IsDisplayRefundInfo;
    }

    public void setIsDisplayRefundInfo(String isDisplayRefundInfo) {
        IsDisplayRefundInfo = isDisplayRefundInfo;
    }

    public String getIsGiftPackage() {
        return IsGiftPackage;
    }

    public void setIsGiftPackage(String isGiftPackage) {
        IsGiftPackage = isGiftPackage;
    }

    public String getIsGomeSubsidyOrder() {
        return IsGomeSubsidyOrder;
    }

    public void setIsGomeSubsidyOrder(String isGomeSubsidyOrder) {
        IsGomeSubsidyOrder = isGomeSubsidyOrder;
    }

    public String getIsHaveVipPrice() {
        return IsHaveVipPrice;
    }

    public void setIsHaveVipPrice(String isHaveVipPrice) {
        IsHaveVipPrice = isHaveVipPrice;
    }

    public String getIsInstallment() {
        return IsInstallment;
    }

    public void setIsInstallment(String isInstallment) {
        IsInstallment = isInstallment;
    }

    public String getIsInvoiceNeed() {
        return IsInvoiceNeed;
    }

    public void setIsInvoiceNeed(String isInvoiceNeed) {
        IsInvoiceNeed = isInvoiceNeed;
    }

    public String getIsMoveStore() {
        return IsMoveStore;
    }

    public void setIsMoveStore(String isMoveStore) {
        IsMoveStore = isMoveStore;
    }

    public String getIsPointDeductionOrder() {
        return IsPointDeductionOrder;
    }

    public void setIsPointDeductionOrder(String isPointDeductionOrder) {
        IsPointDeductionOrder = isPointDeductionOrder;
    }

    public String getIsPresale() {
        return IsPresale;
    }

    public void setIsPresale(String isPresale) {
        IsPresale = isPresale;
    }

    public String getIsPrintPrice() {
        return IsPrintPrice;
    }

    public void setIsPrintPrice(String isPrintPrice) {
        IsPrintPrice = isPrintPrice;
    }

    public String getIsProductShippingfee() {
        return IsProductShippingfee;
    }

    public void setIsProductShippingfee(String isProductShippingfee) {
        IsProductShippingfee = isProductShippingfee;
    }

    public String getIsSubsidyConfirm() {
        return IsSubsidyConfirm;
    }

    public void setIsSubsidyConfirm(String isSubsidyConfirm) {
        IsSubsidyConfirm = isSubsidyConfirm;
    }

    public String getLastChangeDate() {
        return LastChangeDate;
    }

    public void setLastChangeDate(String lastChangeDate) {
        LastChangeDate = lastChangeDate;
    }

    public String getNeedOnlinePay() {
        return NeedOnlinePay;
    }

    public void setNeedOnlinePay(String needOnlinePay) {
        NeedOnlinePay = needOnlinePay;
    }

    public String getNewShippingFee() {
        return NewShippingFee;
    }

    public void setNewShippingFee(String newShippingFee) {
        NewShippingFee = newShippingFee;
    }

    public String getOVERSEAS_TAX() {
        return OVERSEAS_TAX;
    }

    public void setOVERSEAS_TAX(String OVERSEAS_TAX) {
        this.OVERSEAS_TAX = OVERSEAS_TAX;
    }

    public String getOVERSEA_TAX_TYPE() {
        return OVERSEA_TAX_TYPE;
    }

    public void setOVERSEA_TAX_TYPE(String OVERSEA_TAX_TYPE) {
        this.OVERSEA_TAX_TYPE = OVERSEA_TAX_TYPE;
    }

    public String getObtainTotalPoints() {
        return ObtainTotalPoints;
    }

    public void setObtainTotalPoints(String obtainTotalPoints) {
        ObtainTotalPoints = obtainTotalPoints;
    }

    public String getOrderCancelDate() {
        return OrderCancelDate;
    }

    public void setOrderCancelDate(String orderCancelDate) {
        OrderCancelDate = orderCancelDate;
    }

    public String getOrderCreationDate() {
        return OrderCreationDate;
    }

    public void setOrderCreationDate(String orderCreationDate) {
        OrderCreationDate = orderCreationDate;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getOrderInvoiceInfo() {
        return OrderInvoiceInfo;
    }

    public void setOrderInvoiceInfo(String orderInvoiceInfo) {
        OrderInvoiceInfo = orderInvoiceInfo;
    }

    public String getOrderPromSubtract() {
        return OrderPromSubtract;
    }

    public void setOrderPromSubtract(String orderPromSubtract) {
        OrderPromSubtract = orderPromSubtract;
    }

    public String getOrderSendDate() {
        return OrderSendDate;
    }

    public void setOrderSendDate(String orderSendDate) {
        OrderSendDate = orderSendDate;
    }

    public String getOrderSource() {
        return OrderSource;
    }

    public void setOrderSource(String orderSource) {
        OrderSource = orderSource;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getOrderStatusCode() {
        return OrderStatusCode;
    }

    public void setOrderStatusCode(String orderStatusCode) {
        OrderStatusCode = orderStatusCode;
    }

    public String getOrderType() {
        return OrderType;
    }

    public void setOrderType(String orderType) {
        OrderType = orderType;
    }

    public String getPaidAmount() {
        return PaidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        PaidAmount = paidAmount;
    }

    public String getPaidAmountOrder() {
        return PaidAmountOrder;
    }

    public void setPaidAmountOrder(String paidAmountOrder) {
        PaidAmountOrder = paidAmountOrder;
    }

    public String getPaidAmountString() {
        return PaidAmountString;
    }

    public void setPaidAmountString(String paidAmountString) {
        PaidAmountString = paidAmountString;
    }

    public String getPaidDateString() {
        return PaidDateString;
    }

    public void setPaidDateString(String paidDateString) {
        PaidDateString = paidDateString;
    }

    public String getPayId() {
        return PayId;
    }

    public void setPayId(String payId) {
        PayId = payId;
    }

    public String getPayId_name() {
        return PayId_name;
    }

    public void setPayId_name(String payId_name) {
        PayId_name = payId_name;
    }

    public String getPayType() {
        return PayType;
    }

    public void setPayType(String payType) {
        PayType = payType;
    }

    public String getPayTypeName() {
        return PayTypeName;
    }

    public void setPayTypeName(String payTypeName) {
        PayTypeName = payTypeName;
    }

    public String getPayableAfterAlloc() {
        return PayableAfterAlloc;
    }

    public void setPayableAfterAlloc(String payableAfterAlloc) {
        PayableAfterAlloc = payableAfterAlloc;
    }

    public String getPayableAfterAllocOrder() {
        return PayableAfterAllocOrder;
    }

    public void setPayableAfterAllocOrder(String payableAfterAllocOrder) {
        PayableAfterAllocOrder = payableAfterAllocOrder;
    }

    public String getPayableAmount() {
        return PayableAmount;
    }

    public void setPayableAmount(String payableAmount) {
        PayableAmount = payableAmount;
    }

    public String getPayableAmountOrder() {
        return PayableAmountOrder;
    }

    public void setPayableAmountOrder(String payableAmountOrder) {
        PayableAmountOrder = payableAmountOrder;
    }

    public String getPaymentByInstalments() {
        return PaymentByInstalments;
    }

    public void setPaymentByInstalments(String paymentByInstalments) {
        PaymentByInstalments = paymentByInstalments;
    }

    public String getPaymentId() {
        return PaymentId;
    }

    public void setPaymentId(String paymentId) {
        PaymentId = paymentId;
    }

    public String getPaymentMethodType() {
        return PaymentMethodType;
    }

    public void setPaymentMethodType(String paymentMethodType) {
        PaymentMethodType = paymentMethodType;
    }

    public String getPayment_provider_sub_id() {
        return Payment_provider_sub_id;
    }

    public void setPayment_provider_sub_id(String payment_provider_sub_id) {
        Payment_provider_sub_id = payment_provider_sub_id;
    }

    public String getPlatform() {
        return Platform;
    }

    public void setPlatform(String platform) {
        Platform = platform;
    }

    public String getPointDeductionAmount() {
        return PointDeductionAmount;
    }

    public void setPointDeductionAmount(String pointDeductionAmount) {
        PointDeductionAmount = pointDeductionAmount;
    }

    public String getPreferentialName() {
        return PreferentialName;
    }

    public void setPreferentialName(String preferentialName) {
        PreferentialName = preferentialName;
    }

    public String getPreferentialRates() {
        return PreferentialRates;
    }

    public void setPreferentialRates(String preferentialRates) {
        PreferentialRates = preferentialRates;
    }

    public String getProductIds() {
        return ProductIds;
    }

    public void setProductIds(String productIds) {
        ProductIds = productIds;
    }

    public String getProductTotal() {
        return ProductTotal;
    }

    public void setProductTotal(String productTotal) {
        ProductTotal = productTotal;
    }

    public String getPromiseWord() {
        return PromiseWord;
    }

    public void setPromiseWord(String promiseWord) {
        PromiseWord = promiseWord;
    }

    public String getPublicCouponNumber() {
        return PublicCouponNumber;
    }

    public void setPublicCouponNumber(String publicCouponNumber) {
        PublicCouponNumber = publicCouponNumber;
    }

    public String getREPAYID() {
        return REPAYID;
    }

    public void setREPAYID(String REPAYID) {
        this.REPAYID = REPAYID;
    }

    public String getReceiverAddress() {
        return ReceiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        ReceiverAddress = receiverAddress;
    }

    public String getReceiverCityId() {
        return ReceiverCityId;
    }

    public void setReceiverCityId(String receiverCityId) {
        ReceiverCityId = receiverCityId;
    }

    public String getReceiverFixTel() {
        return ReceiverFixTel;
    }

    public void setReceiverFixTel(String receiverFixTel) {
        ReceiverFixTel = receiverFixTel;
    }

    public String getReceiverMobilePhone() {
        return ReceiverMobilePhone;
    }

    public void setReceiverMobilePhone(String receiverMobilePhone) {
        ReceiverMobilePhone = receiverMobilePhone;
    }

    public String getReceiverName() {
        return ReceiverName;
    }

    public void setReceiverName(String receiverName) {
        ReceiverName = receiverName;
    }

    public String getReceiverTel() {
        return ReceiverTel;
    }

    public void setReceiverTel(String receiverTel) {
        ReceiverTel = receiverTel;
    }

    public String getRefundText() {
        return RefundText;
    }

    public void setRefundText(String refundText) {
        RefundText = refundText;
    }

    public String getSendCompany() {
        return SendCompany;
    }

    public void setSendCompany(String sendCompany) {
        SendCompany = sendCompany;
    }

    public String getSendCompanyTel() {
        return SendCompanyTel;
    }

    public void setSendCompanyTel(String sendCompanyTel) {
        SendCompanyTel = sendCompanyTel;
    }

    public String getSendPackageCode() {
        return SendPackageCode;
    }

    public void setSendPackageCode(String sendPackageCode) {
        SendPackageCode = sendPackageCode;
    }

    public String getShipForecast() {
        return ShipForecast;
    }

    public void setShipForecast(String shipForecast) {
        ShipForecast = shipForecast;
    }

    public String getShipType() {
        return ShipType;
    }

    public void setShipType(String shipType) {
        ShipType = shipType;
    }

    public String getShippingFee() {
        return ShippingFee;
    }

    public void setShippingFee(String shippingFee) {
        ShippingFee = shippingFee;
    }

    public String getShopId() {
        return ShopId;
    }

    public void setShopId(String shopId) {
        ShopId = shopId;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getShopPromoTotalAfterAloc() {
        return ShopPromoTotalAfterAloc;
    }

    public void setShopPromoTotalAfterAloc(String shopPromoTotalAfterAloc) {
        ShopPromoTotalAfterAloc = shopPromoTotalAfterAloc;
    }

    public String getShopType() {
        return ShopType;
    }

    public void setShopType(String shopType) {
        ShopType = shopType;
    }

    public String getTotalActivityFee() {
        return TotalActivityFee;
    }

    public void setTotalActivityFee(String totalActivityFee) {
        TotalActivityFee = totalActivityFee;
    }

    public String getTotalAfterAlloc() {
        return TotalAfterAlloc;
    }

    public void setTotalAfterAlloc(String totalAfterAlloc) {
        TotalAfterAlloc = totalAfterAlloc;
    }

    public String getTotalAfterAllocOrder() {
        return TotalAfterAllocOrder;
    }

    public void setTotalAfterAllocOrder(String totalAfterAllocOrder) {
        TotalAfterAllocOrder = totalAfterAllocOrder;
    }

    public String getTotalBarginPrice() {
        return TotalBarginPrice;
    }

    public void setTotalBarginPrice(String totalBarginPrice) {
        TotalBarginPrice = totalBarginPrice;
    }

    public String getTotalBarginPriceB2C() {
        return TotalBarginPriceB2C;
    }

    public void setTotalBarginPriceB2C(String totalBarginPriceB2C) {
        TotalBarginPriceB2C = totalBarginPriceB2C;
    }

    public String getTotalBarginPriceCheckOut() {
        return TotalBarginPriceCheckOut;
    }

    public void setTotalBarginPriceCheckOut(String totalBarginPriceCheckOut) {
        TotalBarginPriceCheckOut = totalBarginPriceCheckOut;
    }

    public String getTotalBarginPriceOrder() {
        return TotalBarginPriceOrder;
    }

    public void setTotalBarginPriceOrder(String totalBarginPriceOrder) {
        TotalBarginPriceOrder = totalBarginPriceOrder;
    }

    public String getWarehouse() {
        return Warehouse;
    }

    public void setWarehouse(String warehouse) {
        Warehouse = warehouse;
    }

    public String getWarehouseId() {
        return WarehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        WarehouseId = warehouseId;
    }

    public String getCancel_type() {
        return cancel_type;
    }

    public void setCancel_type(String cancel_type) {
        this.cancel_type = cancel_type;
    }

    public String getCollect_subtract1() {
        return collect_subtract1;
    }

    public void setCollect_subtract1(String collect_subtract1) {
        this.collect_subtract1 = collect_subtract1;
    }

    public String getCollect_subtract10() {
        return collect_subtract10;
    }

    public void setCollect_subtract10(String collect_subtract10) {
        this.collect_subtract10 = collect_subtract10;
    }

    public String getCollect_subtract2() {
        return collect_subtract2;
    }

    public void setCollect_subtract2(String collect_subtract2) {
        this.collect_subtract2 = collect_subtract2;
    }

    public String getCollect_subtract3() {
        return collect_subtract3;
    }

    public void setCollect_subtract3(String collect_subtract3) {
        this.collect_subtract3 = collect_subtract3;
    }

    public String getCollect_subtract4() {
        return collect_subtract4;
    }

    public void setCollect_subtract4(String collect_subtract4) {
        this.collect_subtract4 = collect_subtract4;
    }

    public String getCollect_subtract5() {
        return collect_subtract5;
    }

    public void setCollect_subtract5(String collect_subtract5) {
        this.collect_subtract5 = collect_subtract5;
    }

    public String getCollect_subtract6() {
        return collect_subtract6;
    }

    public void setCollect_subtract6(String collect_subtract6) {
        this.collect_subtract6 = collect_subtract6;
    }

    public String getCollect_subtract7() {
        return collect_subtract7;
    }

    public void setCollect_subtract7(String collect_subtract7) {
        this.collect_subtract7 = collect_subtract7;
    }

    public String getCollect_subtract8() {
        return collect_subtract8;
    }

    public void setCollect_subtract8(String collect_subtract8) {
        this.collect_subtract8 = collect_subtract8;
    }

    public String getCollect_subtract9() {
        return collect_subtract9;
    }

    public void setCollect_subtract9(String collect_subtract9) {
        this.collect_subtract9 = collect_subtract9;
    }

    public String getCollect_subtract_amount1() {
        return collect_subtract_amount1;
    }

    public void setCollect_subtract_amount1(String collect_subtract_amount1) {
        this.collect_subtract_amount1 = collect_subtract_amount1;
    }

    public String getCollect_subtract_amount10() {
        return collect_subtract_amount10;
    }

    public void setCollect_subtract_amount10(String collect_subtract_amount10) {
        this.collect_subtract_amount10 = collect_subtract_amount10;
    }

    public String getCollect_subtract_amount2() {
        return collect_subtract_amount2;
    }

    public void setCollect_subtract_amount2(String collect_subtract_amount2) {
        this.collect_subtract_amount2 = collect_subtract_amount2;
    }

    public String getCollect_subtract_amount3() {
        return collect_subtract_amount3;
    }

    public void setCollect_subtract_amount3(String collect_subtract_amount3) {
        this.collect_subtract_amount3 = collect_subtract_amount3;
    }

    public String getCollect_subtract_amount4() {
        return collect_subtract_amount4;
    }

    public void setCollect_subtract_amount4(String collect_subtract_amount4) {
        this.collect_subtract_amount4 = collect_subtract_amount4;
    }

    public String getCollect_subtract_amount5() {
        return collect_subtract_amount5;
    }

    public void setCollect_subtract_amount5(String collect_subtract_amount5) {
        this.collect_subtract_amount5 = collect_subtract_amount5;
    }

    public String getCollect_subtract_amount6() {
        return collect_subtract_amount6;
    }

    public void setCollect_subtract_amount6(String collect_subtract_amount6) {
        this.collect_subtract_amount6 = collect_subtract_amount6;
    }

    public String getCollect_subtract_amount7() {
        return collect_subtract_amount7;
    }

    public void setCollect_subtract_amount7(String collect_subtract_amount7) {
        this.collect_subtract_amount7 = collect_subtract_amount7;
    }

    public String getCollect_subtract_amount8() {
        return collect_subtract_amount8;
    }

    public void setCollect_subtract_amount8(String collect_subtract_amount8) {
        this.collect_subtract_amount8 = collect_subtract_amount8;
    }

    public String getCollect_subtract_amount9() {
        return collect_subtract_amount9;
    }

    public void setCollect_subtract_amount9(String collect_subtract_amount9) {
        this.collect_subtract_amount9 = collect_subtract_amount9;
    }

    public String getCollection_promo_id1() {
        return collection_promo_id1;
    }

    public void setCollection_promo_id1(String collection_promo_id1) {
        this.collection_promo_id1 = collection_promo_id1;
    }

    public String getCollection_promo_id10() {
        return collection_promo_id10;
    }

    public void setCollection_promo_id10(String collection_promo_id10) {
        this.collection_promo_id10 = collection_promo_id10;
    }

    public String getCollection_promo_id2() {
        return collection_promo_id2;
    }

    public void setCollection_promo_id2(String collection_promo_id2) {
        this.collection_promo_id2 = collection_promo_id2;
    }

    public String getCollection_promo_id3() {
        return collection_promo_id3;
    }

    public void setCollection_promo_id3(String collection_promo_id3) {
        this.collection_promo_id3 = collection_promo_id3;
    }

    public String getCollection_promo_id4() {
        return collection_promo_id4;
    }

    public void setCollection_promo_id4(String collection_promo_id4) {
        this.collection_promo_id4 = collection_promo_id4;
    }

    public String getCollection_promo_id5() {
        return collection_promo_id5;
    }

    public void setCollection_promo_id5(String collection_promo_id5) {
        this.collection_promo_id5 = collection_promo_id5;
    }

    public String getCollection_promo_id6() {
        return collection_promo_id6;
    }

    public void setCollection_promo_id6(String collection_promo_id6) {
        this.collection_promo_id6 = collection_promo_id6;
    }

    public String getCollection_promo_id7() {
        return collection_promo_id7;
    }

    public void setCollection_promo_id7(String collection_promo_id7) {
        this.collection_promo_id7 = collection_promo_id7;
    }

    public String getCollection_promo_id8() {
        return collection_promo_id8;
    }

    public void setCollection_promo_id8(String collection_promo_id8) {
        this.collection_promo_id8 = collection_promo_id8;
    }

    public String getCollection_promo_id9() {
        return collection_promo_id9;
    }

    public void setCollection_promo_id9(String collection_promo_id9) {
        this.collection_promo_id9 = collection_promo_id9;
    }

    public String getCollection_promo_type1() {
        return collection_promo_type1;
    }

    public void setCollection_promo_type1(String collection_promo_type1) {
        this.collection_promo_type1 = collection_promo_type1;
    }

    public String getCollection_promo_type10() {
        return collection_promo_type10;
    }

    public void setCollection_promo_type10(String collection_promo_type10) {
        this.collection_promo_type10 = collection_promo_type10;
    }

    public String getCollection_promo_type2() {
        return collection_promo_type2;
    }

    public void setCollection_promo_type2(String collection_promo_type2) {
        this.collection_promo_type2 = collection_promo_type2;
    }

    public String getCollection_promo_type3() {
        return collection_promo_type3;
    }

    public void setCollection_promo_type3(String collection_promo_type3) {
        this.collection_promo_type3 = collection_promo_type3;
    }

    public String getCollection_promo_type4() {
        return collection_promo_type4;
    }

    public void setCollection_promo_type4(String collection_promo_type4) {
        this.collection_promo_type4 = collection_promo_type4;
    }

    public String getCollection_promo_type5() {
        return collection_promo_type5;
    }

    public void setCollection_promo_type5(String collection_promo_type5) {
        this.collection_promo_type5 = collection_promo_type5;
    }

    public String getCollection_promo_type6() {
        return collection_promo_type6;
    }

    public void setCollection_promo_type6(String collection_promo_type6) {
        this.collection_promo_type6 = collection_promo_type6;
    }

    public String getCollection_promo_type7() {
        return collection_promo_type7;
    }

    public void setCollection_promo_type7(String collection_promo_type7) {
        this.collection_promo_type7 = collection_promo_type7;
    }

    public String getCollection_promo_type8() {
        return collection_promo_type8;
    }

    public void setCollection_promo_type8(String collection_promo_type8) {
        this.collection_promo_type8 = collection_promo_type8;
    }

    public String getCollection_promo_type9() {
        return collection_promo_type9;
    }

    public void setCollection_promo_type9(String collection_promo_type9) {
        this.collection_promo_type9 = collection_promo_type9;
    }

    public String getCust_type() {
        return cust_type;
    }

    public void setCust_type(String cust_type) {
        this.cust_type = cust_type;
    }

    public String getEarnest_money() {
        return earnest_money;
    }

    public void setEarnest_money(String earnest_money) {
        this.earnest_money = earnest_money;
    }

    public String getFinal_money() {
        return final_money;
    }

    public void setFinal_money(String final_money) {
        this.final_money = final_money;
    }

    public String getIs_have_gift() {
        return is_have_gift;
    }

    public void setIs_have_gift(String is_have_gift) {
        this.is_have_gift = is_have_gift;
    }

    public String getIs_shop_reviewed() {
        return is_shop_reviewed;
    }

    public void setIs_shop_reviewed(String is_shop_reviewed) {
        this.is_shop_reviewed = is_shop_reviewed;
    }

    public String getIshaverouteline() {
        return ishaverouteline;
    }

    public void setIshaverouteline(String ishaverouteline) {
        this.ishaverouteline = ishaverouteline;
    }

    public String getOrder_canceldate() {
        return order_canceldate;
    }

    public void setOrder_canceldate(String order_canceldate) {
        this.order_canceldate = order_canceldate;
    }

    public String getOrder_prom_subtract() {
        return order_prom_subtract;
    }

    public void setOrder_prom_subtract(String order_prom_subtract) {
        this.order_prom_subtract = order_prom_subtract;
    }

    public String getOrder_route_cancel_date() {
        return order_route_cancel_date;
    }

    public void setOrder_route_cancel_date(String order_route_cancel_date) {
        this.order_route_cancel_date = order_route_cancel_date;
    }

    public String getOrder_route_cancel_name() {
        return order_route_cancel_name;
    }

    public void setOrder_route_cancel_name(String order_route_cancel_name) {
        this.order_route_cancel_name = order_route_cancel_name;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getPayment_parse() {
        return payment_parse;
    }

    public void setPayment_parse(String payment_parse) {
        this.payment_parse = payment_parse;
    }

    public String getPreferred_shipping_time_type() {
        return preferred_shipping_time_type;
    }

    public void setPreferred_shipping_time_type(String preferred_shipping_time_type) {
        this.preferred_shipping_time_type = preferred_shipping_time_type;
    }

    public Integer getPresentBell() {
        return presentBell;
    }

    public void setPresentBell(Integer presentBell) {
        this.presentBell = presentBell;
    }

    public String getPromotion_id() {
        return promotion_id;
    }

    public void setPromotion_id(String promotion_id) {
        this.promotion_id = promotion_id;
    }

    public String getPromotion_subtract() {
        return promotion_subtract;
    }

    public void setPromotion_subtract(String promotion_subtract) {
        this.promotion_subtract = promotion_subtract;
    }

    public String getPromotion_type() {
        return promotion_type;
    }

    public void setPromotion_type(String promotion_type) {
        this.promotion_type = promotion_type;
    }

    public String getRcv_country_id() {
        return rcv_country_id;
    }

    public void setRcv_country_id(String rcv_country_id) {
        this.rcv_country_id = rcv_country_id;
    }

    public String getRcv_email() {
        return rcv_email;
    }

    public void setRcv_email(String rcv_email) {
        this.rcv_email = rcv_email;
    }

    public String getRcv_fix_tel() {
        return rcv_fix_tel;
    }

    public void setRcv_fix_tel(String rcv_fix_tel) {
        this.rcv_fix_tel = rcv_fix_tel;
    }

    public String getRcv_mobile_tel() {
        return rcv_mobile_tel;
    }

    public void setRcv_mobile_tel(String rcv_mobile_tel) {
        this.rcv_mobile_tel = rcv_mobile_tel;
    }

    public String getRcv_province_id() {
        return rcv_province_id;
    }

    public void setRcv_province_id(String rcv_province_id) {
        this.rcv_province_id = rcv_province_id;
    }

    public String getRcv_town_id() {
        return rcv_town_id;
    }

    public void setRcv_town_id(String rcv_town_id) {
        this.rcv_town_id = rcv_town_id;
    }

    public String getRcv_zip() {
        return rcv_zip;
    }

    public void setRcv_zip(String rcv_zip) {
        this.rcv_zip = rcv_zip;
    }

    public String getRouteid() {
        return routeid;
    }

    public void setRouteid(String routeid) {
        this.routeid = routeid;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public String getSecond_book_shopname() {
        return second_book_shopname;
    }

    public void setSecond_book_shopname(String second_book_shopname) {
        this.second_book_shopname = second_book_shopname;
    }

    public String getShipment_type_id() {
        return shipment_type_id;
    }

    public void setShipment_type_id(String shipment_type_id) {
        this.shipment_type_id = shipment_type_id;
    }

    public String getShop_is_self_cod() {
        return shop_is_self_cod;
    }

    public void setShop_is_self_cod(String shop_is_self_cod) {
        this.shop_is_self_cod = shop_is_self_cod;
    }

    public String getShop_order_prom_name() {
        return shop_order_prom_name;
    }

    public void setShop_order_prom_name(String shop_order_prom_name) {
        this.shop_order_prom_name = shop_order_prom_name;
    }
}
