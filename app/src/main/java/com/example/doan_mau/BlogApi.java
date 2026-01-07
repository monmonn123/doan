package com.example.doan_mau;

import com.example.doan_mau.model.BlogPost;
import com.example.doan_mau.model.DocumentModel;
import com.example.doan_mau.model.UserNotification;
import java.util.List;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

// FILE API DUY NHẤT CHO TOÀN BỘ DỰ ÁN
public interface BlogApi {

    // --- AUTH & USER ---
    @POST("api/auth/register")
    Call<ResponseBody> registerUser(@Body Map<String, String> body);

    @POST("api/auth/login")
    Call<Map<String, Object>> loginUser(@Body Map<String, String> body);

    @POST("api/auth/forgot-password/request")
    Call<Map<String, Object>> requestOTP(@Body Map<String, String> body);

    @POST("api/auth/forgot-password/reset")
    Call<Map<String, Object>> resetPassword(@Body Map<String, String> body);

    @GET("api/users/profile/{id}")
    Call<Map<String, Object>> getUserProfile(@Path("id") String userId);

    @PUT("api/users/profile/{id}")
    Call<Map<String, Object>> updateUserProfile(@Path("id") String userId, @Body Map<String, String> body);

    @GET("api/users/all")
    Call<Map<String, Object>> getAllUsers();

    @PUT("api/users/update-role")
    Call<Map<String, Object>> updateUserRole(@Body Map<String, String> body);

    // --- QUESTIONS & BLOG ---
    @GET("api/questions/public")
    Call<List<BlogPost>> getPublicPosts();

    @GET("api/questions/pending")
    Call<List<BlogPost>> getPendingPosts();

    @GET("api/questions/{id}")
    Call<BlogPost> getPostById(@Path("id") String postId);

    @Multipart
    @POST("api/questions/post")
    Call<ResponseBody> postQuestion(@Part("userId") RequestBody userId, @Part("content") RequestBody content, @Part MultipartBody.Part image);

    @PUT("api/questions/like/{id}")
    Call<Map<String, Object>> toggleLike(@Path("id") String id, @Body Map<String, String> userId);

    @PUT("api/questions/dislike/{id}")
    Call<Map<String, Object>> toggleDislike(@Path("id") String id, @Body Map<String, String> userId);

    @POST("api/questions/comment/{id}")
    Call<ResponseBody> postComment(@Path("id") String id, @Body Map<String, String> commentData);

    @PUT("api/questions/approve/{id}")
    Call<ResponseBody> updateStatus(@Path("id") String id, @Body Map<String, String> body);
    
    // --- CHAT & MESSAGING ---
    @POST("api/chat/send")
    Call<Map<String, Object>> sendAiMessage(@Body Map<String, String> body);

    @GET("api/messages/search/admin")
    Call<Map<String, Object>> searchForAdmin();

    @GET("api/messages/search/{mssv}")
    Call<Map<String, Object>> searchUserByMssv(@Path("mssv") String mssv);

    @GET("api/messages/history/{senderId}/{receiverId}")
    Call<Map<String, Object>> getUserChatHistory(@Path("senderId") String senderId, @Path("receiverId") String receiverId);

    @POST("api/messages/send")
    Call<Map<String, Object>> sendUserMessage(@Body Map<String, String> body);

    @GET("api/messages/conversations/{userId}")
    Call<Map<String, Object>> getConversationList(@Path("userId") String userId);

    // --- NOTIFICATIONS ---
    @GET("api/notifications/{userId}")
    Call<List<UserNotification>> getUserNotifications(@Path("userId") String userId);

    // --- DOCUMENTS ---
    @Multipart
    @POST("api/documents/upload") 
    Call<Void> uploadFile(@Part("title") RequestBody title, @Part("uploader") RequestBody uploader, @Part MultipartBody.Part file);

    @GET("api/documents")
    Call<List<DocumentModel>> getDocuments();

    @DELETE("api/documents/{id}")
    Call<ResponseBody> deleteDocument(@Path("id") String documentId);
}