package com.easy.tvbox.http;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 接口声明
 */
public interface ApiService {

    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);//直接使用网址下载

    @GET("getAllShop")
    Single<ResponseBody> getAllShop();

    @POST("sendMessage")
    Single<ResponseBody> sendMessage(@Body RequestBody requestBody);

    @POST("login/{serial}")
    Single<ResponseBody> login(@Path("serial") String serial, @Body RequestBody requestBody);

    @POST("updatePhone/{code}")
    Single<ResponseBody> updatePhone(@Path("code") String code, @Body RequestBody requestBody);

    @GET("generate_image_code")
    Single<ResponseBody> generateImageCode();

    @GET("generate_qrcode")
    Single<ResponseBody> requestQrCode();

    @GET("is_scan/{uid}")
    Single<ResponseBody> requestCheckLogin(@Path("uid") String key);

    @POST("queryForMusic")
    Observable<ResponseBody> queryForMusic(@Query("page") int page, @Query("size") int size, @Body RequestBody requestBody);

    @GET("getMusicDetail/{uid}")
    Single<ResponseBody> getMusicDetail(@Path("uid") String uid);

    @GET("getPlayUrl/{id}")
    Single<ResponseBody> getPlayUrl(@Path("id") String id);

    @POST("validOldPhone")
    Single<ResponseBody> validOldPhone(@Body RequestBody requestBody);

    @POST("queryForLive")
    Single<ResponseBody> queryForLive(@Query("page") int page, @Query("size") int size, @Body RequestBody requestBody);

    @POST("saveEquipment")
    Single<ResponseBody> saveEquipment(@Body RequestBody requestBody);

    @GET("getCarouselByShopNo/{shopNo}")
    Single<ResponseBody> getCarouselByShopNo(@Path("shopNo") String shopNo);

    @POST("queryForAudio")
    Observable<ResponseBody> queryForAudio(@Query("page") int page, @Query("size") int size, @Body RequestBody requestBody);

    @GET("getLivePlayUrl/{uid}")
    Single<ResponseBody> getLivePlayUrl(@Path("uid") String uid);

    @GET("getTimeAxis/{uid}")
    Single<ResponseBody> getTimeAxis(@Path("uid") String uid);

    @POST("querySongSheet")
    Single<ResponseBody> querySongSheet(@Body RequestBody requestBody);

    @POST("querySongSheetMusic")
    Observable<ResponseBody> querySongSheetMusic(@Body RequestBody requestBody);
}
