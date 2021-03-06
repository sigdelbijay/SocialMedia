package com.example.socialmedia.rest.services;

import com.example.socialmedia.activity.LoginActivity;
import com.example.socialmedia.activity.ProfileActivity;
import com.example.socialmedia.adapter.PostAdapter;
import com.example.socialmedia.model.CommentModel;
import com.example.socialmedia.model.FriendsModel;
import com.example.socialmedia.model.NotificationModel;
import com.example.socialmedia.model.PostModel;
import com.example.socialmedia.model.User;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface UserInterface {

    @POST("login")
    Call<Boolean> signin(@Body LoginActivity.UserInfo userInfo);

    @GET("loadownprofile")
    Call<User> loadownProfile(@QueryMap Map<String, String> params);

    @GET("loadotherprofile")
    Call<User> loadOtherProfile(@QueryMap Map<String, String> params);

    @POST("poststatus")
    Call<Integer> uploadStatus(@Body MultipartBody requestBody);

    @POST("uploadImage")
    Call<Integer> uploadImage(@Body MultipartBody requestBody);

    @GET("search")
    Call<List<User>> search(@QueryMap Map<String, String> params);

    @GET("profiletimeline")
    Call<List<PostModel>> getProfilePosts(@QueryMap Map<String, String> params);

    @POST("performAction")
    Call<Integer> performAction(@Body ProfileActivity.performAction performAction);

    @GET("loadfriends")
    Call<FriendsModel> loadfriends(@QueryMap Map<String, String> params);

    @GET("gettimelinepost")
    Call<List<PostModel>> gettimelinepost(@QueryMap Map<String, String> params);

    @POST("likeunlike")
    Call<Integer> likeunlike(@Body PostAdapter.AddLike addLike);

    @POST("postcomment")
    Call<CommentModel> postcomment(@Body PostAdapter.AddComment addComment);

    @GET("retrievetopcomment")
    Call<CommentModel> retrievetopcomment(@QueryMap Map<String, String> params);

    @GET("retrievelowlevelcomment")
    Call<List<CommentModel.Comment>> retrievelowlevelcomment(@QueryMap Map<String, String> params);

    @GET("getnotification")
    Call<List<NotificationModel>> getnotification(@QueryMap Map<String, String> params);

    @GET("notification/postdetails")
    Call<PostModel> postdetails(@QueryMap Map<String, String> params);

}
