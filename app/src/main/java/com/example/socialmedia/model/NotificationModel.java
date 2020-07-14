package com.example.socialmedia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationModel {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("notificationTo")
    @Expose
    private String notificationTo;
    @SerializedName("notificationFrom")
    @Expose
    private String notificationFrom;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("notificationTime")
    @Expose
    private Double notificationTime;
    @SerializedName("postId")
    @Expose
    private String postId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("profileUrl")
    @Expose
    private String profileUrl;
    @SerializedName("post")
    @Expose
    private String post;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotificationTo() {
        return notificationTo;
    }

    public void setNotificationTo(String notificationTo) {
        this.notificationTo = notificationTo;
    }

    public String getNotificationFrom() {
        return notificationFrom;
    }

    public void setNotificationFrom(String notificationFrom) {
        this.notificationFrom = notificationFrom;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Double notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

}