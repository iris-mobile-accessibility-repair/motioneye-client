/*
 * Copyright (c) 2020 MotionEye Client by Developer From Jokela, All Rights Reserved.
 * Licensed with MIT
 */

package com.developerfromjokela.motioneyeclient.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static Retrofit retrofit = null;
    private static Gson gson = new GsonBuilder().create();
    private static OkHttpClient.Builder client = new OkHttpClient.Builder();
    public static HostnameVerifier motionEyeVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return hostname.equals(session.getPeerHost());
        }
    };


    public static <T> T createService(Class<T> serviceClass, String BASE_URL) throws NoSuchAlgorithmException {
        if (retrofit == null) {


            retrofit = new Retrofit.Builder()
                    .client(createOkHttpClient(false))
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(serviceClass);
    }

    private static OkHttpClient createOkHttpClient(boolean canCache) throws NoSuchAlgorithmException {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        SSLContext sslContext = SSLContext.getDefault();
        return new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60 / 2, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .writeTimeout(60, TimeUnit.SECONDS)
                .cache(null)
                .hostnameVerifier(motionEyeVerifier)
                .sslSocketFactory(sslContext.getSocketFactory())
                .build();
    }

}