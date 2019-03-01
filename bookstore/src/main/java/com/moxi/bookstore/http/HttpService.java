package com.moxi.bookstore.http;

import com.moxi.bookstore.bean.AliyPayData;
import com.moxi.bookstore.bean.AppendShoppingCart;
import com.moxi.bookstore.bean.BoughtBookData;
import com.moxi.bookstore.bean.Cart;
import com.moxi.bookstore.bean.CatetoryData;
import com.moxi.bookstore.bean.CertificateData;
import com.moxi.bookstore.bean.ChanelData;
import com.moxi.bookstore.bean.ChargeData;
import com.moxi.bookstore.bean.Data;
import com.moxi.bookstore.bean.DeleteShoppingCart;
import com.moxi.bookstore.bean.EbookDetailData;
import com.moxi.bookstore.bean.MakeOrderData;
import com.moxi.bookstore.bean.Message.RecommendData;
import com.moxi.bookstore.bean.OrderData;
import com.moxi.bookstore.bean.PayData;
import com.moxi.bookstore.bean.SearchMediaData;
import com.moxi.bookstore.bean.StoreUpData;
import com.moxi.bookstore.bean.UserInfoData;
import com.moxi.bookstore.bean.VirtualPayMentData;
import com.moxi.bookstore.http.entity.BaseEntity;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import rx.Observable;

/**
 * Created by Administrator on 2016/9/20.
 * 接口observable
 */
public interface HttpService {

    /**
     * 所有分类
     * @return
     */
    @GET("media/api2.go?action=mediaCategory&channelType=dddsonly&start=0&end=5&level=4")
    Observable<BaseEntity<CatetoryData>> getCatetoryData();

    /**
     * Chanel下的salelist
     * dimension: dd_sale 销量最高
     *            newest  时间最新
     */
    @GET("/media/api.go")
    Observable<BaseEntity<ChanelData>> getChanelData(@Query("action") String action,@Query("category") String type,
                                                     @Query("dimension") String str,@Query("start") int start,
                                                     @Query("end") int end,@Query("token") String token);
    /**
     * Chanel下的salelist
     * dimension: dd_sale 销量最高
     *            newest  时间最新
     */
    @GET("/media/api.go")
    Observable<BaseEntity<ChanelData>> getRankData(@Query("action") String action,@Query("category") String type,
                                                     @Query("timeDimension") String str,@Query("rankType") String rankType ,
                                                   @Query("payType") String payType ,@Query("start") int start,
                                                     @Query("end") int end);

    /**
     * channel下salelist
     * @param action =mediaCategoryLeaf 频道活动
     * @param type =xs 频道类别（小说）
     * @param dimension =price 筛选指标（价格）
     * @param order :2 价格升序
     *        1 价格降序
     * @param start
     * @param end
     * @return
     */

    @GET("/media/api.go")
    Observable<BaseEntity<ChanelData>> getChanelDataByPrice(@Query("action") String action,@Query("category") String type,
                                                     @Query("dimension") String dimension,@Query("order") int order,@Query("start") int start,
                                                     @Query("end") int end);

    /**
     * 推荐电子书
     * @return
     */
    @GET("/media/api.go?action=column&columnType=all_rec_dddj&isFull=1&returnType=json&deviceType=Android&channelId=30061&permanentId=&deviceSerialNo=&platformSource=DDDS-P&channelType=&start=0&end=30")
    Observable<BaseEntity<RecommendData>> getRecommendData(@Query("token") String token);

    /**
     * ebook详情
     * @param action =getMedia 详情活动
     * @param id 资源ID
     * @param type =Android 设备类型
     * @param token
     * @return
     */
    @GET("/media/api.go")
    Observable<BaseEntity<EbookDetailData>> getEbookDetailData(@Query("action") String action,@Query("saleId") long id
            ,@Query("deviceType") String type,@Query("token") String token,@Query("clientVersionNo") String clientVersionNo);

    /**
     *  搜索keyword
     * @param action =searchMedia 搜索活动
     * @param key 关键字
     * @param start
     * @param end
     * @return
     */
    @GET("/media/api.go")
    Observable<BaseEntity<SearchMediaData>> getSearchMediaData(@Query("action") String action,@Query("keyword") String key
                                                                ,@Query("start") int start,@Query("end") int end);

    /**
     * 添加购物车
     * @param paramMap
     * @return
     */
    @POST("/media/api2.go")
    Observable<BaseEntity<AppendShoppingCart>> appendShoppingCart(@QueryMap HashMap<String,Object> paramMap);

    /**
     * 试读下载
     * @param action =downloadMediaWhole 下载活动
     * @param id 资源ID
     * @param is 全本标记
     * @param device 设备类型
     * @param source =DDDS-P 资源所属标记
     * @return
     */
    @Streaming
    @GET("/media/api2.go")
    Observable<ResponseBody> downloadSDMedia(@Query("action") String action, @Query("mediaId") long id,
                                             @Query("isFull") int is, @Query("deviceType") String device,
                                             @Query("platformSource") String source,@Query("token") String token);

    /**
     * 免费电子书
     * @param mediaId 资源ID
     * @param deviceType 设备类型
     * @param source 资源所属标记
     * @param token
     * @return
     */
    @POST("/media/api2.go?action=freeObtainMedia")
    Observable<BaseEntity<Data>> getFreeMedia(@Query("mediaId") long mediaId,@Query("deviceType") String deviceType,
                                              @Query("platformSource") String source,@Query("token") String token);
    /**
     * 获取证书
     * @param action =getPublishedCertificate 证书活动
     * @param mediaId 资源ID
     * @param key 本地Key
     * @param No 设备号
     * @param isFull 全本标记
     * @param ref =browse 固定映射标记
     * @param returnType =json 返回数据类型
     * @param deviceType 设备类型
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("/media/api2.go")
    Observable<BaseEntity<CertificateData>> getCertificate(@Field("action") String action, @Field("mediaId") long mediaId, @Field("key") String key,
                                                           @Field("No") String No, @Field("isFull") int isFull,@Field("refAction") String ref,
                                                           @Field("returnType") String returnType,@Field("deviceType") String deviceType,
                                                           @Field("token") String token);



    /**
     * 购物列表
     * @param paramMap
     * @return
     */
    @GET("/media/api2.go?action=listShoppingCart")
    Observable<BaseEntity<Cart>> listShoppingCart(@QueryMap HashMap<String,Object> paramMap);

    /**
     * 删除购物车
     * @param paramMap
     * @return
     */
    @GET("/media/api2.go?action=deleteShoppingCart")
    Observable<BaseEntity<DeleteShoppingCart>> deleteShoppingCart(@QueryMap HashMap<String,Object> paramMap);

    /**
     * 收藏(已弃用)
     * @param selfType =0 书架类型
     * @param type =media 资源类型
     * @param token
     * @param pubId
     * @param date
     * @param pageSize
     * @return
     * &returnType=json&deviceType=Android&token=e_6df3816ecabde8b345e65b2cb49eae21d9e1f20768d85a88e2e943c9a9b7dbcd
     */
    @POST("media/api2.go?action=dDReaderStoreUpList")
    Observable<BaseEntity<StoreUpData>> getStoreUpData(@Query("selfType") String selfType,@Query("type") String type,
                                                       @Query("token") String token,@Query("pubId") String pubId,
                                                       @Query("storeDateLong") String date,@Query("pageSize") String pageSize);


    /**
     * 生成订单 （复合请求）
     * @param json 资源json字符串
     * @param permanentId 资源ID(多资源需拼接)
     * @param source 资源所属
     * @param deviceType 设备类型
     * @param token
     * @return
     */
    @GET("/media/api2.go?action=multiAction")
    Observable<BaseEntity<MakeOrderData>> MakeOrder(@Query("field") String json,@Query("permanentId") String permanentId,
                                                    @Query("platformSource") String source,@Query("deviceType") String deviceType,
                                                    @Query("token") String token);

    /**
     * 微信扫描支付
     * @param orderId 订单ID
     * @param totalPrice 价格
     * @param isPaperBook 纸质书标记
     * @param productIds 资源ID
     * @param permanentId null
     * @param platformSource 资源所属
     * @param fromPlatform =401 平台固定标记
     * @param deviceType 设备类型
     * @param token
     * @return
     */
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    @POST("/media/api2.go?action=wxPayForPC")
    Observable<BaseEntity<PayData>> getPayHtml(@Query("orderId") String orderId,@Query("totalPrice") String totalPrice,@Query("payable") String payable,
                                               @Query("isPaperBook") boolean isPaperBook,@Query("productIds") String productIds,
                                               @Query("permanentId") String permanentId,@Query("platformSource") String platformSource,
                                               @Query("fromPlatform") String fromPlatform,@Query("deviceType") String deviceType,@Query("token") String token);

    /**
     * 支付宝扫描支付
     * @param orderId 订单ID
     * @param totalPrice 价格
     * @param isPaperBook 纸质书标记
     * @param productIds 资源ID
     * @param permanentId null
     * @param platformSource 资源所属
     * @param fromPlatform =401 平台固定标记
     * @param deviceType 设备类型
     * @param token
     * @return
     */
    //&orderId=34290504318&totalPrice=5.00&isPaperBook=false&productIds=1900411317&permanentId=125465465465486&platformSource=DDDS-P&fromPlatform=401&deviceType=Android&token=e_06bb046f41ada4ef2f4e2af7347450863a30aceed7d0736a1cac8d81d6b2db2f
    @GET("/media/api2.go?action=alipayForPC")
    Observable<BaseEntity<AliyPayData>> getPayHtmlWithAliy(@Query("orderId") String orderId, @Query("totalPrice") String totalPrice, @Query("isPaperBook") boolean isPaperBook,
                                                           @Query("productIds") String productIds, @Query("permanentId") String permanentId, @Query("platformSource") String platformSource,
                                                           @Query("fromPlatform") String fromPlatform, @Query("deviceType") String deviceType, @Query("token") String token);

    /**
     * 添加收藏
     * @param type =media 资源类型
     * @param targetIds 资源ID
     * @param token
     * @return
     */
    @POST("/media/api2.go?action=dDReaderStoreUpSave")
    Observable<BaseEntity> doStroeBook(@Query("type") String type,@Query("targetIds") String targetIds,
                                       @Query("token") String token);

    /**
     * 取消收藏
     * @param Ids 资源ID
     * @param platformSource 资源所属
     * @param token
     * @return
     * &targetIds=1900076827&platformSource=DDDS-P&token=e_8a30ac41f142395a53ead89bbf642af774586e836089f7f1cf4f71e9b8de7840
     */
    @POST("/media/api2.go?action=dDReaderStoreUpCancel")
    Observable<BaseEntity> cancleStore(@Query("targetIds") String Ids,@Query("platformSource") String platformSource,
                                       @Query("token") String token);

    /**
     * 获取收藏列表
     * @param token
     * @param storeDateLong 最后一条数据的收藏时间
     * @return
     */
    @POST("/media/api2.go?action=dDReaderStoreUpList&selfType=0&type=media&pageSize=240")
    Observable<BaseEntity<StoreUpData>> getStoreData(@Query("token") String token,@Query("storeDateLong ") String storeDateLong );

    /**
     * 获取已购买列表
     * @param deviceType
     * @param token
     * @param lastMediaAuthorityId 最后一本书的id
     * @return
     */
    @Headers({"Accept-Encoding:nogzip"})
    @POST("/media/api2.go?action=getUserBookList&pageSize=200")
    Observable<BaseEntity<BoughtBookData>> getBoughtBookData(@Query("deviceType") String deviceType,
                                                             @Query("token") String token,@Query("lastMediaAuthorityId") String lastMediaAuthorityId);

    /**
     * 用户铃铛
     * @param token
     * @return
     */
    @GET("/media/api.go?action=getUserInfo")
    Observable<BaseEntity<UserInfoData>> getAcountData(@Query("token") String token);

    @POST("/media/api2.go?action=getOrderDetail")
    Observable<BaseEntity<OrderData>> getOrderDetail(@Query("orderId") String orderId,
                                                     @Query("token") String token);

    /**
     * 新用户获取200元读书卡
     * @param deviceType
     * @param aaId
     * @param deviceSerialNo
     * @param channelId
     * @param token
     * @return
     */
    @GET("/media/api2.go?action=userAttendActivityRewardV2")
    Observable<BaseEntity> getReward(@Query("deviceType") String deviceType,@Query("attachAccountActivityId") int aaId,
                                     @Query("deviceSerialNo") String deviceSerialNo,@Query("channelId") String channelId,
                                     @Query("token") String token);

    /**
     * 虚拟支付（铃铛）
     * @param productArray 资源json字符串
     * @param sing 本地Key
     * @param timestamp 时间戳
     * @param fromPlatform =401 平台标记
     * @param isAppendBorrow=false
     * @param platformSource=DDDS-P 资源所属
     * @param deviceType 设备类型
     * @param token
     * @param dayNum =0
     * @param isAttendPromotion =0
     * @param referType =buy 活动类型
     * @param channelId =30061 商户号
     * @param deviceSerialNo 设备号
     * @return
     */
    @POST("/media/api2.go?action=purchaseEbookVirtualPayment")
    Observable<BaseEntity<VirtualPayMentData>> VirtualPay(@Query("productArray") String productArray, @Query("sign") String sing,
                                                          @Query("timestamp") String timestamp, @Query("fromPlatform") int fromPlatform,
                                                          @Query("isAppendBorrow") boolean isAppendBorrow, @Query("platformSource") String platformSource,
                                                          @Query("deviceType") String deviceType, @Query("token") String token, @Query("dayNum") int dayNum,
                                                          @Query("isAttendPromotion") int isAttendPromotion, @Query("referType") String referType,
                                                          @Query("channelId") int channelId, @Query("deviceSerialNo") String deviceSerialNo);

    /**
     * 充值界面
     * /media/api2.go?action=getDepositShowView
     * @param paymentId =1017 支付类型
     * @param fromPaltform =ds_android 平台类型
     * @param returnType 返回类型
     * @param deviceType 设备类型
     * @param channelId =30061 商户号
     * @param permanentId null
     * @param deviceSerialNo 设备号
     * @param platformSource=DDDS-P 资源所属
     * @param channelType null
     * @param token
     * @return
     */
    @POST("/media/api2.go?action=getDepositShowView")
    Observable<BaseEntity<ChargeData>> getChargeInfor(@Query("paymentId") int paymentId,@Query("fromPaltform") String fromPaltform,
                                                      @Query("returnType") String returnType,@Query("deviceType") String deviceType,
                                                      @Query("channelId") int channelId,@Query("permanentId") String permanentId,
                                                      @Query("deviceSerialNo") String deviceSerialNo,@Query("platformSource") String platformSource,
                                                      @Query("channelType") String channelType,@Query("token") String token);

    //结算

    /**
     * http://e.dangdang.com/media/api2.go?
     * action=getEbookOrderFlowV3
     * &deviceSerialNo=666008d4-bed5-45d5-82d4-77f06280516f
     * &channelId=30061
     * &returnType=json
     * &platformSource=DDDS-P
     * &deviceType=Android&productIds=1900395100
     * &token=e_314f2076eff687a951e9906d794af919231c9d74f230f19c329fe16aaeea3ee7
     * &isAppendBorrow=&dayNum=&fromPlatform=401
     * @return
     */
    @GET("/media/api2.go?action=getEbookOrderFlowV2")
    Observable<BaseEntity> getOFlow(@Query("deviceSerialNo") String deviceNo,@Query("channelId") String channelId,
                                    @Query("returnType") String returnType,@Query("platformSource") String platformSource,
                                    @Query("deviceType") String deviceType,@Query("productIds") String productIds,
                                    @Query("token") String token,@Query("isAppendBorrow") String isAppendBorrow,
                                    @Query("dayNum") String dayNum,@Query("fromPlatform") String fromPlatform);
}
