package com.example.doan_mau;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    // Dùng IP 10.0.2.2 để kết nối Localhost từ máy ảo Android
    private static final String BASE_URL = "http://10.0.2.2:4000/";

    public static BlogApi getApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(BlogApi.class);
    }
}