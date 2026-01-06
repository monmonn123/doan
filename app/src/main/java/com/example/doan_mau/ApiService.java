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

    // --- PHẦN 1: KHO TÀI LIỆU ---
    @Multipart
    @POST("api/upload")
    Call<Void> uploadFile(
            @Part("title") RequestBody title,
            @Part("uploader") RequestBody uploader,
            @Part MultipartBody.Part file
    );

    @GET("api/documents")
    Call<List<DocumentModel>> getDocuments();

    // --- PHẦN 2: BLOG TRAO ĐỔI ---
    @Multipart
    @POST("api/questions/post")
    Call<ResponseBody> postQuestion(
            @Part("userId") RequestBody userId,
            @Part("content") RequestBody content,
            @Part MultipartBody.Part image
    );

    @GET("api/questions/public")
    Call<List<BlogPost>> getPublicPosts();

    @GET("api/questions/pending")
    Call<List<BlogPost>> getPendingPosts();

    @PUT("api/questions/approve/{id}")
    Call<ResponseBody> approveOrReject(
            @Path("id") String questionId,
            @Body Map<String, String> body
    );

    @PUT("api/questions/like/{id}")
    Call<ResponseBody> toggleLike(
            @Path("id") String questionId,
            @Body Map<String, String> userId
    );

    @POST("api/questions/comment/{id}")
    Call<ResponseBody> postComment(
            @Path("id") String questionId,
            @Body Map<String, String> commentData
    );

    // --- PHẦN 3: AI CHAT ---
    @POST("api/chat/send")
    Call<Map<String, Object>> sendAiMessage(@Body Map<String, String> body);

    @GET("api/chat/history/{conversationId}")
    Call<Map<String, Object>> getAiChatHistory(@Path("conversationId") String conversationId);
}