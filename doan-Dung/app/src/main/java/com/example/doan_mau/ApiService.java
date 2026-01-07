package com.example.doan_mau;

import java.util.List;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // --- PHẦN 1: KHO TÀI LIỆU (Giữ nguyên cũ) ---
    @Multipart
    @POST("api/upload")
    Call<Void> uploadFile(
            @Part("title") RequestBody title,
            @Part("uploader") RequestBody uploader,
            @Part MultipartBody.Part file
    );

    @GET("api/documents")
    Call<List<DocumentModel>> getDocuments();

    // --- PHẦN 2: BLOG TRAO ĐỔI (Dán thêm đống này vào Dung nhé) ---

    // 1. Sinh viên đăng bài hỏi đáp kèm ảnh
    @Multipart
    @POST("api/questions/post")
    Call<ResponseBody> postQuestion(
            @Part("userId") RequestBody userId,
            @Part("content") RequestBody content,
            @Part MultipartBody.Part image
    );

    // 2. Lấy bài viết đã được duyệt để hiện lên Newsfeed
    @GET("api/questions/public")
    Call<List<BlogPost>> getPublicPosts();

    // 3. Admin lấy danh sách bài chờ duyệt
    @GET("api/questions/pending")
    Call<List<BlogPost>> getPendingPosts();

    // 4. Admin bấm Duyệt hoặc Hủy bài
    @PUT("api/questions/approve/{id}")
    Call<ResponseBody> approveOrReject(
            @Path("id") String questionId,
            @Body Map<String, String> body // Chứa { "action": "approved" } hoặc "rejected"
    );

    // 5. Thả tim hoặc Bỏ tim
    @PUT("api/questions/like/{id}")
    Call<ResponseBody> toggleLike(
            @Path("id") String questionId,
            @Body Map<String, String> userId
    );

    // 6. Đăng bình luận vào bài viết
    @POST("api/questions/comment/{id}")
    Call<ResponseBody> postComment(
            @Path("id") String questionId,
            @Body Map<String, String> commentData // Chứa { "userId": "...", "text": "..." }
    );
}