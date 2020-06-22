package com.example.socialmedia.rest;

import android.app.Activity;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient extends Activity {

//    public static final String BASE_URL = "https://10.0.2.2/socialmedia/app";
    public static final String BASE_URL = "http://54.253.98.145:8000/app/";

    private static Retrofit retrofit;

    public static Retrofit getApiClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        if(retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
