package com.easy.tvbox.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easy.tvbox.bean.ErrorRespond;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.bean.SuccessRespond;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class AppRequestStore {

    Retrofit retrofit;

    private static class Holder {
        private static final AppRequestStore instance = new AppRequestStore();
    }

    public static AppRequestStore getInstance() {
        return Holder.instance;
    }

    private AppRequestStore() {
        this.retrofit = HttpManager.getInstance().retrofit;
    }

    public Function<ResponseBody, Respond> getCommonFunction() {
        return responseBody -> {
            Respond respondDO = new ErrorRespond();
            try {
                String result = responseBody.string();
                if (!TextUtils.isEmpty(result) && !"[]".equals(result)) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null) {
                        String error = jsonObject.getString("error");
                        //说明是错误的
                        if (!TextUtils.isEmpty(error) && error.contains("{") && error.contains("}")) {
                            respondDO = JSON.parseObject(result, ErrorRespond.class);
                        } else if (jsonObject.getIntValue("error") == 0) {
                            respondDO = JSON.parseObject(result, SuccessRespond.class);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return respondDO;
        };
    }

    /**
     * post 参数封装
     *
     * @return
     */
    public RequestBody getRequestBody(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), value);
    }

    /**
     * 参数转json
     *
     * @param params
     * @return
     */
    public String getJson(Map<String, Object> params) {
        if (params == null || params.size() == 0) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        return jsonObject.toJSONString();
    }

    /**
     * post 参数封装
     *
     * @param params
     * @return
     */
    public RequestBody getRequestBody(Map<String, Object> params) {
        if (params == null || params.size() == 0) {
            return null;
        }
        String jsonString = getJson(params);
        return getRequestBody(jsonString);
    }

    /**
     * 请求二维码
     *
     * @return
     */
    public Single<Respond> requestQrCode() {
        return retrofit.create(ApiService.class)
                .requestQrCode()
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }

    public Single<Respond> requestCheckLogin(String key) {
        return retrofit.create(ApiService.class)
                .requestCheckLogin(key)
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 登陆
     *
     * @return
     */
    public Single<Respond> login(String serial, Map<String, Object> map) {
        return retrofit.create(ApiService.class)
                .login(serial, getRequestBody(map))
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 更新手机号
     *
     * @return
     */
    public Single<Respond> updatePhone(String code, Map<String, Object> map) {
        return retrofit.create(ApiService.class)
                .updatePhone(code, getRequestBody(map))
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 验证旧手机号
     *
     * @return
     */
    public Single<Respond> validOldPhone(Map<String, Object> map) {
        return retrofit.create(ApiService.class)
                .validOldPhone(getRequestBody(map))
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 获取图形验证码
     *
     * @return
     */
    public Single<Respond> generateImageCode() {
        return retrofit.create(ApiService.class)
                .generateImageCode()
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 发送验证码
     *
     * @return
     */
    public Single<Respond> sendMessage(Map<String, Object> map) {
        return retrofit.create(ApiService.class)
                .sendMessage(getRequestBody(map))
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 保存设备信息
     *
     * @return
     */
    public Single<Respond> saveEquipment(Map<String, Object> map) {
        return retrofit.create(ApiService.class)
                .saveEquipment(getRequestBody(map))
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 获取店铺
     *
     * @return
     */
    public Single<Respond> getAllShop() {
        return retrofit.create(ApiService.class)
                .getAllShop()
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 首页轮播图
     *
     * @return
     */
    public Single<Respond> getCarouselByShopNo(String shopNo) {
        return retrofit.create(ApiService.class)
                .getCarouselByShopNo(shopNo)
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 大课直播列表
     *
     * @return
     */
    public Single<Respond> queryForLive(int page, int size, Map<String, Object> map) {
        return retrofit.create(ApiService.class)
                .queryForLive(page, size, getRequestBody(map))
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 音乐列表
     *
     * @return
     */
    public Observable<Respond> queryForMusic(int page, int size, Map<String, Object> map) {
        return retrofit.create(ApiService.class)
                .queryForMusic(page, size, getRequestBody(map))
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 音乐地址
     *
     * @return
     */
    public Single<Respond> getPlayUrl(String id) {
        return retrofit.create(ApiService.class)
                .getPlayUrl(id)
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 音乐详情
     *
     * @return
     */
    public Single<Respond> getMusicDetail(String uid) {
        return retrofit.create(ApiService.class)
                .getMusicDetail(uid)
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }


    /**
     * 获取每日音频列表
     *
     * @return
     */
    public Observable<Respond> queryForAudio(int page, int size, Map<String, Object> map) {
        return retrofit.create(ApiService.class)
                .queryForAudio(page, size, getRequestBody(map))
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }


    /**
     * 获取大课播放地址
     *
     * @return
     */
    public Single<Respond> getLivePlayUrl(String uid) {
        return retrofit.create(ApiService.class)
                .getLivePlayUrl(uid)
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 每日课程--- 获取音频播放地址
     *
     * @return
     */
    public Single<Respond> getTimeAxis(String uid) {
        return retrofit.create(ApiService.class)
                .getTimeAxis(uid)
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }


    public Single<Respond> querySongSheet(Map<String, Object> map) {
        return retrofit.create(ApiService.class)
                .querySongSheet(getRequestBody(map))
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }


    public Observable<Respond> querySongSheetMusic(Map<String, Object> map) {
        return retrofit.create(ApiService.class)
                .querySongSheetMusic(getRequestBody(map))
                .map(getCommonFunction())
                .subscribeOn(Schedulers.io());
    }
}
