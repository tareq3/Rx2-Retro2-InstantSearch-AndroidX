/*
 * Created by Tareq Islam on 3/3/19 12:02 AM
 *
 *  Last modified 3/2/19 3:05 AM
 */

package com.mti.jakewharton.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    public static final String BASE_URL = "https://api.androidhive.info/json/";

    private static String TAG = ApiClient.class.getSimpleName();

    private static Retrofit retrofit = null;


    public static Retrofit getClient() {



        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)

                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    public static void resetApiClient() {
        retrofit = null;

    }
}
