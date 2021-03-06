package com.example.socialmedia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel(Parcel.Serialization.BEAN)
public class PostModel {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("post")
    @Expose
    private String post;
    @SerializedName("postUserId")
    @Expose
    private String postUserId;
    @SerializedName("statusImage")
    @Expose
    private String statusImage;
    @SerializedName("statusTime")
    @Expose
    private Double statusTime;
    @SerializedName("likeCount")
    @Expose
    private Integer likeCount;
    @SerializedName("hasComment")
    @Expose
    private Integer hasComment;
    @SerializedName("privacy")
    @Expose
    private String privacy;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("userProfile")
    @Expose
    private String userProfile;
    @SerializedName("userToken")
    @Expose
    private String userToken;
    @SerializedName("isLiked")
    @Expose
    private Boolean isLiked;

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    @SerializedName("commentCount")
    @Expose
    private Integer commentCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPostUserId() {
        return postUserId;
    }

    public void setPostUserId(String postUserId) {
        this.postUserId = postUserId;
    }

    public String getStatusImage() {
        return statusImage;
    }

    public void setStatusImage(String statusImage) {
        this.statusImage = statusImage;
    }

    public Double getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Double statusTime) {
        this.statusTime = statusTime;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getHasComment() {
        return hasComment;
    }

    public void setHasComment(Integer hasComment) {
        this.hasComment = hasComment;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

}