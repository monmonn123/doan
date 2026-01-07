package com.example.doan_mau;

import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BlogApi {
    // Lấy bài đã duyệt cho SV lướt
    @GET("api/questions/public")
    Call<List<BlogPost>> getPublicPosts();

    // Thả tim bài viết
    @PUT("api/questions/like/{id}")
    Call<ResponseBody> toggleLike(@Path("id") String id, @Body Map<String, String> userId);

    // Đăng bình luận
    @POST("api/questions/comment/{id}")
    Call<ResponseBody> postComment(@Path("id") String id, @Body Map<String, String> commentData);

    // Admin Duyệt bài
    @PUT("api/questions/approve/{id}")
    Call<ResponseBody> updateStatus(@Path("id") String id, @Body Map<String, String> body);
}