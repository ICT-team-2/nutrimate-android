package com.ict.nutrimate_android.retrofit;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    public static final String NUTRI_FLASK = "https://cec8-182-220-224-39.ngrok-free.app"; // ngrok : 2222
    public static final String NUTRI_SPRING = "https://6b33-182-220-224-39.ngrok-free.app"; // ngrok : 9999

    // 플라스크 서버
    public static FlaskService getFlaskService() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(NUTRI_FLASK)
                    .client(new OkHttpClient.Builder()
                            .connectTimeout(100, TimeUnit.SECONDS) // 연결 시간 초과 설정
                            .readTimeout(100, TimeUnit.SECONDS)    // 읽기 시간 초과 설정
                            .writeTimeout(100, TimeUnit.SECONDS)   // 쓰기 시간 초과 설정
                            .build())
                    .addConverterFactory(ScalarsConverterFactory.create()) // 서버로부터 받은 데이터가 String일 때
                    .addConverterFactory(JacksonConverterFactory.create()) // 서버로부터 받은 데이터가 JSON일 때
                    .build();
        return retrofit.create(FlaskService.class);
    }

    // 스프링 서버
    public static SpringService getSpringService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NUTRI_SPRING)
                .addConverterFactory(ScalarsConverterFactory.create()) // 서버로부터 받은 데이터가 String일 때
                .addConverterFactory(JacksonConverterFactory.create()) // 서버로부터 받은 데이터가 JSON일 때
                .build();
        return retrofit.create(SpringService.class);
    }

}






