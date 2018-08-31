package com.itzyf.websocketdemo;

import io.reactivex.Completable;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author 依风听雨
 * @version 创建时间：2018/08/31 10:02
 */
public interface ApiService {
    @POST("hello-convert-and-send")
    Completable sendRestEcho(@Query("name") String name);
}
