package com.example.socialmedia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;


public class CommentModel {

    @SerializedName("results")
    @Expose
    private List<Result> results = null;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public class Result {

        @SerializedName("comment")
        @Expose
        private Comment comment;
        @SerializedName("subComments")
        @Expose
        private SubComments subComments;

        public Comment getComment() {
            return comment;
        }

        public void setComment(Comment comment) {
            this.comment = comment;
        }

        public SubComments getSubComments() {
            return subComments;
        }

        public void setSubComments(SubComments subComments) {
            this.subComments = subComments;
        }

    }
    public class SubComments {

        @SerializedName("lastComment")
        @Expose
        private List<LastComment> lastComment = null;
        @SerializedName("total")
        @Expose
        private Integer total;

        public List<LastComment> getLastComment() {
            return lastComment;
        }

        public void setLastComment(List<LastComment> lastComment) {
            this.lastComment = lastComment;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

    }

    //static for parcel to work as it is inner class
    @Parcel
    public static class Comment {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("comment")
        @Expose
        private String comment;
        @SerializedName("commentBy")
        @Expose
        private String commentBy;
        @SerializedName("commentDate")
        @Expose
        private Double commentDate;
        @SerializedName("superParentId")
        @Expose
        private String superParentId;
        @SerializedName("parentId")
        @Expose
        private String parentId;
        @SerializedName("hasSubComment")
        @Expose
        private Integer hasSubComment;
        @SerializedName("level")
        @Expose
        private String level;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("profileUrl")
        @Expose
        private String profileUrl;
        @SerializedName("userToken")
        @Expose
        private String userToken;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getCommentBy() {
            return commentBy;
        }

        public void setCommentBy(String commentBy) {
            this.commentBy = commentBy;
        }

        public Double getCommentDate() {
            return commentDate;
        }

        public void setCommentDate(Double commentDate) {
            this.commentDate = commentDate;
        }

        public String getSuperParentId() {
            return superParentId;
        }

        public void setSuperParentId(String superParentId) {
            this.superParentId = superParentId;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public Integer getHasSubComment() {
            return hasSubComment;
        }

        public void setHasSubComment(Integer hasSubComment) {
            this.hasSubComment = hasSubComment;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
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

        public String getUserToken() {
            return userToken;
        }

        public void setUserToken(String userToken) {
            this.userToken = userToken;
        }

    }
    public class LastComment {

        @SerializedName("comment")
        @Expose
        private String comment;
        @SerializedName("commentBy")
        @Expose
        private String commentBy;
        @SerializedName("commentDate")
        @Expose
        private Double commentDate;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("profileUrl")
        @Expose
        private String profileUrl;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getCommentBy() {
            return commentBy;
        }

        public void setCommentBy(String commentBy) {
            this.commentBy = commentBy;
        }

        public Double getCommentDate() {
            return commentDate;
        }

        public void setCommentDate(Double commentDate) {
            this.commentDate = commentDate;
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

    }

}








