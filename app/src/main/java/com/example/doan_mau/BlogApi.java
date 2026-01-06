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

public interface BlogApi {
    @GET("api/questions/public")
    Call<List<BlogPost>> getPublicPosts();

    @GET("api/questions/pending")
    Call<List<BlogPost>> getPendingPosts();

    @PUT("api/questions/like/{id}")
    Call<ResponseBody> toggleLike(@Path("id") String id, @Body Map<String, String> userId);

    @PUT("api/questions/dislike/{id}")
    Call<ResponseBody> toggleDislike(@Path("id") String id, @Body Map<String, String> userId);

    @POST("api/questions/comment/{id}")
    Call<ResponseBody> postComment(@Path("id") String id, @Body Map<String, String> commentData);

    @PUT("api/questions/approve/{id}")
    Call<ResponseBody> updateStatus(@Path("id") String id, @Body Map<String, String> body);

    @Multipart
    @POST("api/questions/post")
    Call<ResponseBody> postQuestion(
            @Part("userId") RequestBody userId,
            @Part("content") RequestBody content,
            @Part MultipartBody.Part image
    );

    // --- AI CHAT ---
    @POST("api/chat/send")
    Call<Map<String, Object>> sendAiMessage(@Body Map<String, String> body);

    @GET("api/chat/history/{conversationId}")
    Call<Map<String, Object>> getAiChatHistory(@Path("conversationId") String conversationId);

    // --- USER MESSAGING ---
    @GET("api/messages/search/{mssv}")
    Call<Map<String, Object>> searchUserByMssv(@Path("mssv") String mssv);

    @GET("api/messages/history/{senderId}/{receiverId}")
    Call<Map<String, Object>> getUserChatHistory(@Path("senderId") String senderId, @Path("receiverId") String receiverId);

    @POST("api/messages/send")
    Call<Map<String, Object>> sendUserMessage(@Body Map<String, String> body);

    @GET("api/messages/conversations/{userId}")
    Call<Map<String, Object>> getConversationList(@Path("userId") String userId);
}