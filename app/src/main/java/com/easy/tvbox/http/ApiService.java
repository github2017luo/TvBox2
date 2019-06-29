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

/**
 * 接口声明
 */
public interface ApiService {

    @GET("getAllShop")
    Single<ResponseBody> getAllShop();

    @POST("sendMessage")
    Single<ResponseBody> sendMessage(@Body RequestBody requestBody);

    @POST("login/{code}")
    Single<ResponseBody> login(@Path("code") String code, @Body RequestBody requestBody);

    @POST("updatePhone/{code}")
    Single<ResponseBody> updatePhone(@Path("code") String code, @Body RequestBody requestBody);

    @GET("generate_image_code")
    Single<ResponseBody> generateImageCode();

    @POST("register")
    Single<ResponseBody> register(@Body RequestBody requestBody);

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
    Single<ResponseBody> queryForAudio(@Query("page") int page, @Query("size") int size, @Body RequestBody requestBody);

    @GET("getLivePlayUrl/{uid}")
    Single<ResponseBody> getLivePlayUrl(@Path("uid") String uid);

    @GET("getTimeAxis/{uid}")
    Single<ResponseBody> getTimeAxis(@Path("uid") String uid);
}
