package com.softbrain.hevix.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient mInstance;
    private final Retrofit retrofit;
    public static String BASE = "https://hevuxapi.mybcard.co.in/";


    private RetrofitClient() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .readTimeout(180, TimeUnit.SECONDS).build();

        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

    }

    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public WEB_SERVICE getApi() {

        return retrofit.create(WEB_SERVICE.class);
    }


}
