package com.itzyf.websocketdemo;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author 依风听雨
 * @version 创建时间：2018/08/31 10:05
 */
public class RestClient {

    private static volatile RestClient instance;
    private final ApiService apiService;

    private RestClient() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://" + BuildConfig.SOCKET_API + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static RestClient getInstance() {
        if (instance == null) {
            synchronized (RestClient.class) {
                if (instance == null) {
                    instance = new RestClient();
                }
            }
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }
}
